

public class ServerObject {

    private Client ClientEcrivain;
    private ArrayList<Client> ClientLecteur;

    public Client getClientEcrivain {
	return ClientEcrivain;
    }

    public Arraylist<Client> getClientLecteur {
	return ClientLecteur;
    }

    public Object lock_read(Client_itf client) {
    }

    public Object lock_write(Client_itf client) {
    }
    
}
