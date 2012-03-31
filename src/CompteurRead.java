
public class CompteurRead {

	public static void main(String argv[]) {
		
		Client.init();		
		SharedObject x = Client.lookup("COMPTEUR");
		if (x != null)  {
                    x.lock_read();
                    System.out.println(((Entier) x.obj).getCompteur());
                    x.unlock();
		}
	}
}
