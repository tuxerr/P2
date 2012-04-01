
public class CompteurRead {

	public static void main(String argv[]) {
		
		Client.init();		
		SharedObject x = Client.lookup("COMPTEUR");
		if (x != null)  {
                    x.lock_read();
                    System.out.println(((Entier_itf) x).getCompteur());
                    x.unlock();
		}
		System.exit(0);
	}
}
