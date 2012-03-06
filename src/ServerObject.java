import java.util.ArrayList;


public class ServerObject {
	
    public ServerObject() {
        Clients = new ArrayList<Client_itf>();
        lock=0;
    }

    // 0 : NL
    // 1 : RL
    // 2 : WL
    private int lock;
    private ArrayList<Client_itf> Clients;
	
    public void lock_read(Client cli) {
        lock = 1;
    }
	
    public void lock_write(Client cli) {
        lock = 2;
    }

    public int getId() {
        return id;
    }
	
    public int getLock() {
        return lock;
    }
}
