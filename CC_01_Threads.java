/**
 * @author Lucas Abad Bermejo, 190132, 51499607
 *
 */

public class CC_01_Threads {
	
	// Constantes pedidas
	static int  NUM = 5;
	static int TIEMPO = 500;
	
	public  static class ThreadHijo extends Thread {
		 static Thread [] hilos;
		 int id = 1;
		public ThreadHijo( String msg) {
			// Constructor del hilo
			super(msg);
		}
		
		public static void  constructorHilo (int num) {
			hilos = new Thread[num];
			for (int i = 0 ; i < num; i++ ) {
				Thread hilo = new ThreadHijo("hilo");
				hilos[i]= hilo;
				hilos[i].start();
			
			}
		}
		
		
		
		
		public void run() {
			// Lo que hace el hilo (Imprimir su numero, esperar un tiempo T y volver a imprimir su numero)
			try{
					System.out.println("#"+id +" Thread iniciado");
					Thread.sleep(TIEMPO);
					System.out.println("#"+id +" Thread terminado");
			
			}catch(InterruptedException e){
				System.out.println("Thread Interrumpido");
			}
			}
		}
	
	
	
	public static void main(String [] args) {
		System.out.println("Hilo principal iniciado.");
		ThreadHijo.constructorHilo(NUM);
		for(int i = 0 ; i < ThreadHijo.hilos.length; i++){
			try {
				ThreadHijo.hilos[i].join();
			} catch (InterruptedException e) {
				System.out.println("Thread Interrumpido");
			}}
		System.out.println("Hilo principal terminado.");
		
		}
	}

