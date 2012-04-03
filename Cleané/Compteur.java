public class Compteur {

	public static void main(String argv[]) {

		Client.init();

		SharedObject a;
		SharedObject b;
		if (Integer.parseInt(argv[0]) == -1) {
			a = Client.create(new Entier_bis(0));
			b = Client.create(new Entier(0, a));
			Client.register("COMPTEUR_BIS", a);
			Client.register("COMPTEUR", b);
			System.exit(0);
		}

		try {
			Entier_itf x = (Entier_itf) Client.lookup("COMPTEUR");
			Entier_bis_itf e = (Entier_bis_itf) Client.lookup("COMPTEUR_BIS");
			if (x == null) {
				System.out.println("ERROR : Compteur devrait etre cree");
			}
			if (e == null) {
				System.out.println("ERROR : Compteur_bis devrait etre cree");
			}

			int i;
			int max = Integer.parseInt(argv[0]);
			for (i = 0; i < max; i++) {
				System.out.println(i);
				x.lock_write();
				x.incr();
				x.unlock();
				e.lock_write();
				e.incr();
				e.unlock();
				x.lock_read();
				System.out.println(x.getCompteur());
				x.unlock();
				e.lock_read();
				System.out.println(e.getCompteur());
				e.unlock();
			}
			System.out.println("ENDING");
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

}
