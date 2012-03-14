
public class Compteur {

	private static SharedObject compt;
	
	public static void main(String argv[]) {
		
		Client.init();		
		SharedObject x = Client.lookup("COMPTEUR");
		if (x == null) {
			x = Client.create(new Entier(0));
			Client.register("COMPTEUR", x);
		}
		new Compteur(x);
		
		int i;
		for(i=0;i<10;i++) {
			compt.lock_write();
			((Entier) compt.obj).incr();
			compt.unlock();
		}
		
		compt.lock_read();
		System.out.print(getCompt());
		compt.unlock();
	}
	
	public Compteur(SharedObject x) {
		compt = x;
	}
	
	public static int getCompt() {
		return ((Entier) compt.obj).getCompteur();
	}
}
