import java.rmi.Naming;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.HashMap;

public class Client extends UnicastRemoteObject implements Client_itf {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private static Client localptr;

	private static Server_itf serv;

	private static HashMap<Integer, SharedObject> objects;

	public Client() throws RemoteException {
		super();
		objects = new HashMap<Integer, SharedObject>();
	}

	// /////////////////////////////////////////////////
	// Interface to be used by applications
	// /////////////////////////////////////////////////

	// initialization of the client layer
	public static void init() {
		try {
			localptr = new Client();
		} catch (Exception e) {
			System.out.println(e);
		}
		serv = null;
		try {
			serv = (Server_itf) Naming.lookup("Server");
		} catch (Exception e) {
			System.err.println("Client init exception : " + e);
			e.printStackTrace();
		}

		if (serv == null) {
			System.out.println("Server RMI object not found");
		}
	}

	// lookup in the name server
	public static SharedObject lookup(String name) {
		try {
			int obj_id = serv.lookup(name);
			if (obj_id == -1) {
				return null;
			} else {
				SharedObject obj = new SharedObject(null, obj_id);
				objects.put(obj_id, obj);
				return obj;
			}
		} catch (Exception e) {
			System.out.println(e);
			return null;
		}
	}

	// binding in the name server
	public static void register(String name, SharedObject so) {
		try {
			serv.register(name, so.getId());
		} catch (Exception e) {
			System.out.println(e);
		}
	}

	// creation of a shared object
	public static SharedObject create(Object o) {
		try {
			int new_id = serv.create(o);
			SharedObject obj = new SharedObject(o, new_id);
			return obj;
		} catch (Exception e) {
			System.out.println(e);
			return null;
		}
	}

	// ///////////////////////////////////////////////////////////
	// Interface to be used by the consistency protocol
	// //////////////////////////////////////////////////////////

	// request a read lock from the server
	public static Object lock_read(int id) {
		try {
			return serv.lock_read(id, localptr);
		} catch (Exception e) {
			System.out.println(e);
			return null;
		}
	}

	// request a write lock from the server
	public static Object lock_write(int id) {
		try {
			return serv.lock_write(id, localptr);
		} catch (Exception e) {
			System.out.println(e);
			return null;
		}
	}

	// receive a lock reduction request from the server
	public Object reduce_lock(int id) throws java.rmi.RemoteException {
		SharedObject obj = objects.get(id);
		if (obj != null) {
			obj.reduce_lock();
		}
		return obj.obj;
	}

	// receive a reader invalidation request from the server
	public void invalidate_reader(int id) throws java.rmi.RemoteException {
		SharedObject obj = objects.get(id);
		if (obj != null) {
			try {
				obj.invalidate_reader();
			} catch (Exception e) {
				System.out.println(e);
			}
		}
	}

	// receive a writer invalidation request from the server
	public Object invalidate_writer(int id) throws java.rmi.RemoteException {
		SharedObject obj = objects.get(id);
		if (obj != null) {
			try {
				obj.invalidate_writer();
			} catch (Exception e) {
				System.out.println(e);
			}
		}
		return obj.obj;
	}
}
