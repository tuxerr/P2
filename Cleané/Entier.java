
public class Entier implements java.io.Serializable  {
	private static final long serialVersionUID = 1L;
	private int c;
	private SharedObject e;
	
	public Entier(int nombre, SharedObject ent) {
		c = nombre;
		e = ent;
	}

	public void incr() {
		c++;
		e.lock_write();
		((Entier_bis_itf) e).incr();
		e.unlock();
	}
	
	public SharedObject getEntierBis() {
		return e;
	}
	
	public int getCompteur() {
		return c;
	}
	
	public int getCompteurBis() {
		return ((Entier_bis_itf) e).getCompteur();
	}
}
