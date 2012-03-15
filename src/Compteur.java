
public class Compteur {

	public static void main(String argv[]) {
		
		Client.init();		
		SharedObject x = Client.lookup("COMPTEUR");
		if (x == null) {
			x = Client.create(new Entier(0));
			Client.register("COMPTEUR", x);
		}

                if(Integer.parseInt(argv[0])==-1) {
			System.exit(0);
		}
		
		int i;
         	int max = Integer.parseInt(argv[0]);
		for(i=0;i<max;i++) {
			x.lock_write();
			((Entier) x.obj).incr();
			x.unlock();
		}
	}
	
}
