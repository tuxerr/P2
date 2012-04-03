import java.rmi.Naming;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.HashMap;
import java.util.concurrent.locks.ReentrantLock;

public class Client extends UnicastRemoteObject implements Client_itf {

	private static final long serialVersionUID = 1L;
	private static Client localptr;
	private static Server_itf serv;
	public static HashMap<Integer, SharedObject> objects;
	private static StubGenerator stubgen;
	private static ReentrantLock lookuplock;

	public Client() throws RemoteException {
		super();
		try {
			serv = (Server_itf) Naming.lookup("Server");
		} catch (Exception e) {
			e.printStackTrace();
		}
		objects = new HashMap<Integer, SharedObject>();
	}

	// /////////////////////////////////////////////////
	// Interface to be used by applications
	// /////////////////////////////////////////////////

	// initialization of the client layer
	public static void init() {
		try {
			localptr = new Client();
		} catch (RemoteException e) {
			e.printStackTrace();
		}
		serv = null;
		try {
			serv = (Server_itf) Naming.lookup("Server");
		} catch (Exception e) {
			e.printStackTrace();
		}

		if (serv == null) {
			System.out.println("Server RMI object not found");
		}
		stubgen = new StubGenerator();
		lookuplock = new ReentrantLock();
	}

	// lookup in the name server
	public static SharedObject lookup(String name) {
		try {
			int obj_id = serv.lookup(name);
			if (obj_id == -1) {
				return null;
			} else {
				// on crée un SharedObject de type sharedobject car on n'a pas
				// la classe voulue. On créera un stub lors du premier
				// lock_writer ou lock_read.
				lookuplock.lock();
				Object o = lock_read(obj_id);
				SharedObject obj = stubgen.generateStubFromObject(o, obj_id);
				objects.put(obj_id, obj);
				obj.setLock(SOStatus.RLC);
				lookuplock.unlock();
				return obj;
			}
		} catch (RemoteException e) {
			e.printStackTrace();
			return null;
		}
	}

	// binding in the name server
	public static void register(String name, SharedObject so) {
		try {
			serv.register(name, so.getId());
		} catch (RemoteException e) {
			e.printStackTrace();
		}
	}

	// creation of a shared object
	public static SharedObject create(Object o) {
		try {
			int new_id = serv.create(o);
			SharedObject obj = stubgen.generateStubFromObject(o, new_id);
			objects.put(new_id, obj);
			return obj;
		} catch (RemoteException e) {
			e.printStackTrace();
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
		} catch (RemoteException e) {
			e.printStackTrace();
			return null;
		}
	}

	// request a write lock from the server
	public static Object lock_write(int id) {
		try {
			return serv.lock_write(id, localptr);
		} catch (RemoteException e) {
			e.printStackTrace();
			return null;
		}
	}

	// receive a lock reduction request from the server
	public Object reduce_lock(int id) throws java.rmi.RemoteException {
		SharedObject obj = objects.get(id);
		try {
			synchronized (obj) {
				obj.reduce_lock();
				return obj.obj;
			}
		} catch (InterruptedException e) {
			e.printStackTrace();
			return null;
		}
	}

	// receive a reader invalidation request from the server
	public void invalidate_reader(int id) throws java.rmi.RemoteException {

		lookuplock.lock();
		SharedObject obj = objects.get(id);
		lookuplock.unlock();
		try {
			synchronized (obj) {
				obj.invalidate_reader();
			}
		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	// receive a writer invalidation request from the server
	public Object invalidate_writer(int id) throws java.rmi.RemoteException {
		SharedObject obj = objects.get(id);
		try {
			synchronized (obj) {
				Object wtf = obj.invalidate_writer();
				return wtf;
			}
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}
}
