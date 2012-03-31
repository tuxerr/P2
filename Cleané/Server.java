import java.rmi.Naming;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.server.UnicastRemoteObject;
import java.util.HashMap;

public class Server extends UnicastRemoteObject implements Server_itf {

	private static final long serialVersionUID = 1L;
	private final HashMap<Integer, ServerObject> objmap;
	private final HashMap<String, Integer> namemap;
	private int currentid;

	public Server() throws RemoteException {

		objmap = new HashMap<Integer, ServerObject>();
		namemap = new HashMap<String, Integer>();
		currentid = -1;

		try {
			LocateRegistry.createRegistry(1099);
			System.out.println("java RMI registry launched on port 1099.");
		} catch (RemoteException e) {
			System.out
					.println("java RMI registry already exists on this port.");
			e.printStackTrace();
		}

		try {
			Naming.rebind("Server", this);
		} catch (Exception e) {
			System.out.println("Couldn't upload server to RMI registry");
			e.printStackTrace();
		}
	}

	public int lookup(String name) throws java.rmi.RemoteException {
		Integer i = namemap.get(name);
		if (i == null) {
			return -1;
		} else {
			return i;
		}
	}

	public void register(String name, int id) throws java.rmi.RemoteException {
		namemap.put(name, id);
	}

	public int create(Object o) throws java.rmi.RemoteException {
		currentid++;
		ServerObject obj = new ServerObject(currentid, o);
		objmap.put(currentid, obj);
		return currentid;
	}

	public Object lock_read(int id, Client_itf client)
			throws java.rmi.RemoteException {
		ServerObject sobj = objmap.get(id);
		synchronized (sobj) {
			sobj.lock_read(client);
			return sobj.getCache();
		}
	}

	public Object lock_write(int id, Client_itf client)
			throws java.rmi.RemoteException {
		ServerObject sobj = objmap.get(id);
		synchronized (sobj) {
			sobj.lock_write(client);
			return sobj.getCache();
		}
	}

	public static void main(String args[]) {
		try {
			Server s = new Server();
		} catch (RemoteException e) {
			e.printStackTrace();
		}
	}

}
