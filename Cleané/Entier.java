
public class Entier implements java.io.Serializable {
	
	private static final long serialVersionUID = 1L;
	private int c;
	
	public Entier(int nombre) {
		c = nombre;
	}

	public void incr() {
		c++;
	}
	
	public int getCompteur() {
		return c;
	}
}
