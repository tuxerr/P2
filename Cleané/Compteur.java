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
			((Entier_itf) x).incr();
			x.unlock();
			x.lock_write();
			x.unlock();
			x.lock_read();
                        System.out.println(((Entier_itf) x).getCompteur());
			x.unlock();
			x.lock_read();
			x.unlock();
		}
		System.out.println("ENDING");
	}

}
