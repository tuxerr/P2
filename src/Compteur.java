public class Compteur {

	public static void main(String argv[]) {

		Client.init();

		SharedObject x;
		if (Integer.parseInt(argv[0]) == -1) {
			x = Client.create(new Entier(0));
			Client.register("COMPTEUR", x);
			System.exit(0);
		}

		x = Client.lookup("COMPTEUR");
		if (x == null) {
			System.out.println("ERROR : Compteur devrait etre cree");
		}

		int i;
		int max = Integer.parseInt(argv[0]);
		for (i = 0; i < max; i++) {
			x.lock_write();
			System.out.println("Value : " + ((Entier) x.obj).getCompteur());
			((Entier) x.obj).incr();
			x.unlock();
		}
		System.out.println("ENDING");
	}

}
