import java.rmi.Naming;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.HashMap;

public class Client extends UnicastRemoteObject implements Client_itf {

	private static final long serialVersionUID = 1L;

	private static Client localptr;

	private static Server_itf serv;

	private static HashMap<Integer, SharedObject> objects;

	private static int idclient;

	public Client() throws RemoteException {
		super();
		try {
			serv = (Server_itf) Naming.lookup("Server");
		} catch (Exception e) {
			e.printStackTrace();
		}
		idclient = serv.getIdClient();
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
			e.printStackTrace();
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
				SharedObject obj = new SharedObject(null, obj_id, idclient);
				objects.put(obj_id, obj);
				return obj;
			}
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}

	// binding in the name server
	public static void register(String name, SharedObject so) {
		try {
			serv.register(name, so.getId());
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	// creation of a shared object
	public static SharedObject create(Object o) {
		try {
			int new_id = serv.create(o);
			SharedObject obj = new SharedObject(o, new_id, idclient);
			objects.put(new_id, obj);
			return obj;
		} catch (Exception e) {
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
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}

	// request a write lock from the server
	public static Object lock_write(int id) {
		try {
			return serv.lock_write(id, localptr);
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}

	// receive a lock reduction request from the server
	public Object reduce_lock(int id, int clientdem)
			throws java.rmi.RemoteException {
		SharedObject obj = objects.get(id);
		if (obj != null) {
			try {
				synchronized (obj) {
					System.out.println(idclient + " : Reduce_locking object "
							+ id + " from " + clientdem);
					obj.reduce_lock();
					System.out.println(idclient + " : Reduce_locking done");
					return obj.obj;
				}
			} catch (InterruptedException e) {
				e.printStackTrace();
				return null;
			}
		} else {
			System.out.println("ERROR NULL OBJECT");
			return null;
		}
	}

	// receive a reader invalidation request from the server
	public void invalidate_reader(int id, int clientdem)
			throws java.rmi.RemoteException {
		SharedObject obj = objects.get(id);
		if (obj != null) {
			try {
				synchronized (obj) {
					System.out.println(idclient + " : Invalidating object "
							+ id + " for reading from " + clientdem);
					obj.invalidate_reader();
					System.out.println(idclient + " : Invalidate Reader done from "
							+ clientdem);
				}
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		} else {
			System.out.println("ERROR NULL OBJECT");
		}
	}

	// receive a writer invalidation request from the server
	public Object invalidate_writer(int id, int clientdem)
			throws java.rmi.RemoteException {
		SharedObject obj = objects.get(id);
		if (obj != null) {
			try {
				synchronized (obj) {
					System.out.println(idclient + " : Invalidating object "
							+ id + " for writing from " + clientdem);
					obj.obj = obj.invalidate_writer();
					System.out.println(idclient
							+ " : Invalidate writer done from " + clientdem);
					return obj.obj;
				}
			} catch (InterruptedException e) {
				e.printStackTrace();
				return null;
			}
		} else {
			System.out.println("ERROR NULL OBJECT");
			return null;
		}
	}

	public int getIdClient() throws java.rmi.RemoteException {
		return idclient;
	}

}
