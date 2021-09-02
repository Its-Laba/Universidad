
public class CC_02_Carrera {

	final static int M = 3;
	final static int N = 10;


	public static void main (String  [] args) {

		Entero num = new Entero();
		//while(num.n == 0) {
		Thread hilosinc[] = new Thread[M];
		Thread hilosdec[] = new Thread[M];
		hilosinc[2]= new Incrementar(num);
		hilosinc[1] = new Incrementar(num);
		hilosinc[1].run();
		hilosinc[1].start();
		try {
			hilosinc[1].join();
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		hilosinc[2].start();
		try {
			hilosinc[2].join();
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();}
//		for(int i = 0; i< M;i++) {
//			hilosinc[i] = new Incrementar(num);
//
//			hilosdec [i] = new Decrementar(num);
//		}
//		for(int i =0; i<M;i++) {
//			hilosinc[i].start();
//			hilosdec[i].start();
//		}
//		for(int i =0; i<M;i++) {
//			try{
//				hilosinc[i].join();
//				hilosdec[i].join();
//			}
//			catch(InterruptedException e) {
//				System.out.println("Thread Interrumpido");
//			}
//		}
		System.out.println(num.n);
	}
	//}
	public static class Entero{
		int n = 0;
	}

	static class Incrementar  extends Thread {
		Entero num;
		public Incrementar(Entero num){
			super();
			this.num= num;

		}
		public void run() {
			for (int i = 0; i<N; i++) {
				num.n++;
			}

		}
	}
	static class Decrementar  extends Thread {
		Entero num;
		public Decrementar(Entero num){
			super();
			this.num = num;
		}
		public void run() {
			for (int i = 0; i<N; i++) {
				num.n--;
			}

		}
	}
}
