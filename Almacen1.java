import es.upm.babel.cclib.Producto;
import es.upm.babel.cclib.Semaphore;
import es.upm.babel.cclib.Almacen;

// TODO: importar la clase de los semáforos.

/**
 * Implementación de la clase Almacen que permite el almacenamiento
 * de producto y el uso simultáneo del almacen por varios threads.
 */
class Almacen1 implements Almacen {
   // Producto a almacenar: null representa que no hay producto
   private Producto almacenado = null;

   // TODO: declaración e inicialización de los semáforos
   // necesarios
   private Semaphore sem;
   private Semaphore full;
   private Semaphore empty;
   
   public Almacen1(Semaphore semaforo, Semaphore semaforo1, Semaphore semaforo2) {
	  sem = semaforo;
	  full = semaforo1;
	  empty = semaforo2;
   }

   public void almacenar(Producto producto) {
      // TODO: protocolo de acceso a la sección crítica y código de
      // sincronización para poder almacenar.
	   sem.await();
	   if(almacenado != null) {
		   sem.signal();
		   empty.await();
		   sem.await();
	   }
	  
      // Sección crítica
	   
      almacenado = producto;

      // TODO: protocolo de salida de la sección crítica y código de
      // sincronización para poder extraer.
      sem.signal();
      full.signal();
   }

   public Producto extraer() {
      Producto result;
      
      // TODO: protocolo de acceso a la sección crítica y código de
      // sincronización para poder extraer.
      sem.await();
      if(almacenado == null) {
    	  sem.signal();
    	  full.await();
    	  sem.await();
      }
      // Sección crítica
      result = almacenado;
      almacenado = null;
      

      // TODO: protocolo de salida de la sección crítica y código de
      // sincronización para poder almacenar.
     sem.signal();
     empty.signal();
      return result;
   }
}
