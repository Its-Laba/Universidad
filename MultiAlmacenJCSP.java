import es.upm.babel.cclib.Producto;
import es.upm.aedlib.indexedlist.ArrayIndexedList;
import es.upm.aedlib.indexedlist.IndexedList;
import es.upm.babel.cclib.MultiAlmacen;

// importamos la librera JCSP

import org.jcsp.lang.*;


class MultiAlmacenJCSP implements MultiAlmacen , CSProcess {

	// Canales para enviar y recibir peticiones al/del servidor
	private final Any2OneChannel chAlmacenar = Channel.any2one();
	private final Any2OneChannel chExtraer = Channel.any2one();
	private int TAM;
	private Producto [] almacenado;
	// Para evitar la construccion de almacenes sin inicializar la
	// capacidad

	private MultiAlmacenJCSP() {
	}

	public MultiAlmacenJCSP(int n) {
		this.TAM = n;
		almacenado = new Producto[TAM];
		// COMPLETAR : inicializacion de otros atributos

	}


	public void almacenar(Producto[] productos) {
		if(productos.length > TAM) {
			throw new IllegalArgumentException("El tamaño de productos excede a la capacidad maxima");
		}
		// COMPLETAR : comunicacion con el servidor
		One2OneChannel canal = Channel.one2one();
		chAlmacenar.out().write(new MensajeAlmacenar(productos,canal));
	}


	public Producto[] extraer(int n) {
		Producto[] result = new Producto[n];
		// COMPLETAR : comunicacion con el servidor
		One2OneChannel canal = Channel.one2one();
		chExtraer.out().write(new MensajeExtraer(result,canal));
		result = (Producto []) canal.in().read();
		return result;
	}

	// Creamos Inner Class para controlar las CPRE

	public class MensajeAlmacenar{
		Producto[] producto;
		One2OneChannel canal = Channel.one2one();
		public MensajeAlmacenar(Producto[] producto, One2OneChannel canal) {
			this.producto = producto;
			this.canal = canal;
		}	
	}

	public class MensajeExtraer{
		Producto[] producto;
		One2OneChannel canal = Channel.one2one();
		public MensajeExtraer(Producto[] producto, One2OneChannel canal) {
			this.producto = producto;
			this.canal = canal;
		}	
	}


	// c´o digo del servidor
	private static final int ALMACENAR = 0;
	private static final int EXTRAER = 1;
	public void run() {
		// COMPLETAR : declaracion de canales y estructuras auxiliares
		int Puntero= 0;
		IndexedList<MensajeAlmacenar> listaAlmacenar= new ArrayIndexedList<MensajeAlmacenar>();
		IndexedList<MensajeExtraer> listaExtraer= new ArrayIndexedList<MensajeExtraer>();

		Guard[] entradas = {
				chAlmacenar.in(),
				chExtraer.in()
		};

		Alternative servicios = new Alternative(entradas);
		int choice = 0;


		while (true) {
			try {
				choice = servicios.fairSelect();
			} catch (ProcessInterruptedException e){}
			switch(choice){
			case ALMACENAR:
				MensajeAlmacenar almacenado = (MensajeAlmacenar) chAlmacenar.in().read(); 
				listaAlmacenar.add(listaAlmacenar.size(), almacenado);
				break;
			case EXTRAER:
				MensajeExtraer extraido = (MensajeExtraer) chExtraer.in().read();
				listaExtraer.add(listaExtraer.size(), extraido);
				break;
			}
			// CPRE Almacenar
			for(int i = 0; i< listaAlmacenar.size(); i++) {
				if(Puntero == TAM) {}else {
					MensajeAlmacenar mensaje = listaAlmacenar.get(i); // cojo el mensaje de almacenar
					Producto [] producto = mensaje.producto;
					
					// Caso en el que se mantiene a la espera
					if(producto.length+Puntero > TAM) {}
					
					// Realiza el almacenado 
					else {
						for(int j = 0; j<producto.length;j++) {
							almacenado[Puntero] = producto[j];
							Puntero++;
						}
						mensaje.canal.out().write(true);
						listaAlmacenar.remove(mensaje);
					}
				}
			}
			
			// CPRE Extraer
			for(int i = 0; i< listaExtraer.size(); i++) {
				if(Puntero == 0) {}else {
					MensajeExtraer mensaje = listaExtraer.get(i); // cojo el mensaje de extraer
					Producto [] resultado = mensaje.producto;
					
					// Caso en el que se mantiene a la espera
					if(resultado.length > Puntero) {}
					
					// Realiza el almacenado 
					else {
						for(int j = 0; j<resultado.length;j++) {
							Puntero--;
							resultado[j] = almacenado[Puntero];
						}
						mensaje.canal.out().write(resultado);
						listaExtraer.remove(mensaje);
					}
				}
			}
		}

	}
}

