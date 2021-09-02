
public class exam {

		static class MiHilo extends Thread{
			volatile static int n;
			
			public void run () {
				for (int i=0; i <100; i++) {
					n ++;
				}
				System.out.println(n);
			}
		}
		public void main (String [] args) {
			Thread t = new MiHilo ();
			Thread t2 = new MiHilo ();
			t.run ();
			t.start ();
			//hacerAlgo (); // hace algo ...
			try {t.join ();} catch ( Exception e){}
			t2.start ();
			//hacerAlgo (); // hace algo ...
			try {t2.join ();} catch ( Exception e){}
			
		}
	} // Fin clase Hilos


