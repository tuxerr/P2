
public class CompteurRead {

	public static void main(String argv[]) {
		
		Client.init();		
		Entier_itf x = (Entier_itf) Client.lookup("COMPTEUR");
		Entier_bis_itf e = (Entier_bis_itf) Client.lookup("COMPTEUR_BIS");
		if (x != null)  {
                    x.lock_read();
                    System.out.println("Valeur Object : " + x.getCompteur());
                    System.out.println("Valeur SharedObject : " + e.getCompteur());
                    x.unlock();
		}
		System.exit(0);
	}
}
