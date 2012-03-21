public class Sentence implements java.io.Serializable {
	String data;
	private static final long serialVersionUID = 1L;
	
	public Sentence() {
		data = new String("");
	}

	public void write(String text) {
		data = text;
	}

	public String read() {
		return data;
	}

}