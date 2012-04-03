
public class Entier_bis implements java.io.Serializable{
	private static final long serialVersionUID = 1L;
	private int c;
	
	public Entier_bis(int nombre) {
		c = nombre;
	}

	public void incr() {
		c++;
	}
	
	public int getCompteur() {
		return c;
	}
	
	
}
