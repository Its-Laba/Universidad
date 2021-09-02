// Exclusi�n mutua con espera activa.
//
// Intentar garantizar la exclusi�n mutua en sc_inc y sc_dec sin
// utilizar m�s mecanismo de concurrencia que el de la espera activa
// (nuevas variables y bucles).
//
// Las propiedades que deber�n cumplirse:
// - Garant�a mutual exclusi�n (exclusi�n m�tua): nunca hay dos
//   procesos ejecutando secciones cr�ticas de forma simult�nea.
// - Ausencia de deadlock (interbloqueo): los procesos no quedan
//   "atrapados" para siempre.
// - Ausencia de starvation (inanici�n): si un proceso quiere acceder
//   a su secci�n cr�tica entonces es seguro que alguna vez lo hace.
// - Ausencia de esperas innecesarias: si un proceso quiere acceder a
//   su secci�n cr�tica y ning�n otro proceso est� accediendo ni
//   quiere acceder entonces el primero puede acceder.
//
// Ideas:
// - Una variable booleana en_sc que indica que alg�n proceso est�
//   ejecutando en la secci�n cr�tica?
// - Una variable booleana turno?
// - Dos variables booleanas en_sc_inc y en_sc_dec que indican que un
//   determinado proceso (el incrementador o el decrementador) est�
//   ejecutando su secci�n cr�tica?
// - Combinaciones?

class CC_03_MutexEA {
   
	static final int N_PASOS = 10000;

   // Generador de n�meros aleatorios para simular tiempos de
   // ejecuci�n
    static final java.util.Random RNG = new java.util.Random(0);

   // Variable compartida
   volatile static int n = 0;

   // Variables para asegurar exclusi�n mutua
   volatile static boolean en_sc = false;
   volatile static int turno = 0 ;

   // Secci�n no cr�tica
   static void no_sc() {
       //System.out.println("No SC");
       try {
          // No m�s de 2ms
    	   
    	   while (en_sc ) {
          Thread.sleep(RNG.nextInt(3));
    	   }
    	   
       }
       catch (Exception e) {
          e.printStackTrace();
       }
   }

   // Secciones cr�ticas
   static void sc_inc() {
       //System.out.println("Incrementando");
      n++;
   }

   static void sc_dec() {
       //System.out.println("Decrementando");
      n--;
   }

   // La labor del proceso incrementador es ejecutar no_sc() y luego
   // sc_inc() durante N_PASOS asegurando exclusi�n mutua sobre
   // sc_inc().
   static class Incrementador extends Thread {
      public void run () {
         for (int i = 0; i < N_PASOS; i++) {
            // Secci�n no cr�tica
            no_sc();
            
            // Protocolo de acceso a la secci�n cr�tica
            while (en_sc ||  turno == 0 ) {}
            
           
            en_sc = true;
            
            // Secci�n cr�tica
            sc_inc();

            // Protocolo de salida de la secci�n cr�tica
            en_sc = false;
            
            turno = 0;
         }
      }
   }

   // La labor del proceso incrementador es ejecutar no_sc() y luego
   // sc_dec() durante N_PASOS asegurando exclusi�n mutua sobre
   // sc_dec().
   static class Decrementador extends Thread {
      public void run () {
         for (int i = 0; i < N_PASOS; i++) {
            // Secci�n no cr�tica
            no_sc();
           
            // Protocolo de acceso a la secci�n cr�tica
            while (en_sc || turno == 1) {}
            
            en_sc = true;
           
            // Secci�n cr�tica
            sc_dec();

            // Protocolo de salida de la secci�n cr�tica
            en_sc = false;
            
            turno = 1;
         }
      }
   }

   public static final void main(final String[] args)
      throws InterruptedException
   {
	   
      // Creamos las tareas
      Thread t1 = new Incrementador();
      Thread t2 = new Decrementador();

      // Las ponemos en marcha
      t1.start();
      t2.start();

      // Esperamos a que terminen
      t1.join();
      t2.join();

      // Simplemente se muestra el valor final de la variable:
      System.out.println(n);
   }
}