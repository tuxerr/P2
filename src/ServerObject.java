import java.util.ArrayList;


public class ServerObject {
	
	private int id;
	// 0 : NL
	// 1 : RL
	// 2 : WL
	private int lock;
	private ArrayList<Client_itf> Clients;
	
	public void lock_read() {
		lock = 1;
	}
	
	public void lock_write() {
		lock = 2;
	}

	public int getId() {
		return id;
	}
	
	public int getLock() {
		return lock;
	}
	
	public ArrayList<Client_itf> getClients() {
		return Clients;
	}
}
