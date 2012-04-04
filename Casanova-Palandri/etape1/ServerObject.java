import java.rmi.RemoteException;
import java.util.ArrayList;

public class ServerObject {

	private SOStatus lock;
	private int id;
	private ArrayList<Client_itf> Clients;
	private Object obj_cache;

	public ServerObject(int id, Object o) {
		lock = SOStatus.NL;
		this.id = id;
		Clients = new ArrayList<Client_itf>();
		obj_cache = o;
	}

	public void lock_read(Client_itf cli) {
		if (lock == SOStatus.WLT) {
			for (Client_itf client : Clients) {
				try {
					Object o = client.reduce_lock(id);
					obj_cache = o;
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}
		Clients.add(cli);
		lock = SOStatus.RLT;
	}

	public void lock_write(Client_itf cli) {
		if (lock == SOStatus.RLT) {
			for (Client_itf client : Clients) {
				try {
					client.invalidate_reader(id);
				} catch (RemoteException e) {
					e.printStackTrace();
				}
			}

		} else if (lock == SOStatus.WLT) {
			for (Client_itf client : Clients) {
				try {
					Object o = client.invalidate_writer(id);
					obj_cache = o;
				} catch (RemoteException e) {
					e.printStackTrace();
				}
			}
		}
		Clients.clear();
		Clients.add(cli);
		lock = SOStatus.WLT;
	}

	public Object getCache() {
		return obj_cache;
	}
}
