import java.io.Serializable;

public class SharedObject implements Serializable, SharedObject_itf {

	private static final long serialVersionUID = 1L;
	public Object obj;
	private int id;
	private SOStatus status;
	private int idclient;

	// private Lock lock;
	// private Condition unlocked;

	public SharedObject(Object obj, int i, int idclient) {
		this.obj = obj;
		id = i;
		status = SOStatus.NL;
		this.idclient = idclient;
		// lock=new ReentrantLock();
		// unlocked=lock.newCondition();
	}

	// invoked by the user program on the client node
	public synchronized void lock_read() {
		switch (status) {
		case NL:
			status = SOStatus.RLT;
			obj = Client.lock_read(id);
			System.out.println(idclient + " : Switching from uncached to rtaken");
			break;

		case RLC:
			status = SOStatus.RLT;
			System.out.println(idclient + " : Switching from rcached to rtaken");
			break;

		case WLC:
			status = SOStatus.RLT_WLC;
			obj = Client.lock_read(id);
			System.out.println(idclient + " : Switching from wcached to rtaken_wcached");
			break;

		case RLT:
			status = SOStatus.RLT;
			System.out.println(idclient + " : ERROR LOCK_READ RLT §§§§§§§§§§§§§§§§");
			break;

		case WLT:
			status = SOStatus.WLT;
			System.out.println(idclient + " : ERROR LOCK_READ WLT §§§§§§§§§§§§§§§§");
			break;

		case RLT_WLC:
			status = SOStatus.RLT_WLC;
			System.out.println(idclient + " : ERROR LOCK_READ RLT_WLC §§§§§§§§§§§§§§§§");
			break;
		}
	}

	// invoked by the user program on the client node
	public synchronized void lock_write() {
		switch (status) {
		case NL:
			status = SOStatus.WLT;
			obj = Client.lock_write(id);
			System.out.println(idclient + " : Switching from uncached to wtaken");
			break;

		case RLC:
			status = SOStatus.WLT;
			obj = Client.lock_write(id);
			System.out.println(idclient + " : Switching from rcached to wtaken");
			break;

		case WLC:
			status = SOStatus.WLT;
			System.out.println(idclient + " : Switching from wcached to wtaken");
			break;

		case RLT:
			status = SOStatus.WLT;
			System.out.println(idclient + " : ERROR LOCK_WRITE RLT §§§§§§§§§§§§§§§§");
			break;

		case WLT:
			status = SOStatus.WLT;
			System.out.println(idclient + " : ERROR LOCK_WRITE WLT §§§§§§§§§§§§§§§§");
			break;

		case RLT_WLC:
			status = SOStatus.WLT;
			System.out.println(idclient + " : ERROR LOCK_WRITE RLT_WLC §§§§§§§§§§§§§§§§");
			break;
		}

	}

	// invoked by the user program on the client node
	public synchronized void unlock() {
		System.out.println(idclient + " : Unlocking Object");
		switch (status) {
		case NL:
			status = SOStatus.NL;
			System.out.println(idclient + " : ERROR UNLOCK NL §§§§§§§§§§§§§§§§");
			break;

		case RLC:
			status = SOStatus.RLC;
			System.out.println(idclient + " : ERROR UNLOCK RLC §§§§§§§§§§§§§§§§");
			break;

		case WLC:
			status = SOStatus.WLC;
			System.out.println(idclient + " : ERROR UNLOCK WLC §§§§§§§§§§§§§§§§");
			break;

		case RLT:
			status = SOStatus.RLC;
			System.out.println(idclient + " : Switching from rtaken to rcached");
			break;

		case WLT:
			status = SOStatus.WLC;
			System.out.println(idclient + " : Switching from wtaken to wcached");
			break;

		case RLT_WLC:
			status = SOStatus.WLC;
			System.out.println(idclient + " : Switching from rtaken_wcached to wcached");
			break;
		}
		System.out.println(idclient + " : Notifying");
		notify();
	}

	// callback invoked remotely by the server
	public synchronized Object reduce_lock() throws InterruptedException {
		switch (status) {
		case NL:
			status = SOStatus.NL;
			System.out.println(idclient + " : ERROR REDUCE_LOCK NL §§§§§§§§§§§§§§§§");
			break;

		case RLC:
			status = SOStatus.RLC;
			System.out.println(idclient + " : Reduce_locking object");
			break;

		case WLC:
			status = SOStatus.RLC;
			System.out.println(idclient + " : Reduce_locking object");
			break;

		case RLT:
			status = SOStatus.RLT;
			System.out.println(idclient + " : Reduce_locking object");
			break;

		case WLT:
			while (status == SOStatus.WLT) {
				wait();
				status = SOStatus.RLC;
			}
			System.out.println(idclient + " : Reduce_locking object");
			break;

		case RLT_WLC:
			status = SOStatus.RLT;
			System.out.println(idclient + " : Reduce_locking object");
			break;
		}
		return obj;
	}

	// callback invoked remotely by the server
	public synchronized void invalidate_reader() throws InterruptedException {
		switch (status) {
		case NL:
			status = SOStatus.NL;
			System.out.println(idclient + " : ERROR INVALIDATE READER NL §§§§§§§§§§§§§§§§");
			break;

		case RLC:
			status = SOStatus.NL;
			System.out.println(idclient + " : Switching from rcached to uncached");
			break;

		case WLC:
			status = SOStatus.WLC;
			System.out.println(idclient + " : ERROR INVALIDATE READER WLC §§§§§§§§§§§§§§§§");
			break;

		case RLT:
			while (status == SOStatus.RLT) {
				wait();
				status = SOStatus.NL;
			}
			System.out.println(idclient + " : Switching from rtaken to uncached");
			break;

		case WLT:
			status = SOStatus.WLT;
			System.out.println(idclient + " : ERROR INVALIDATE READER WLT §§§§§§§§§§§§§§§§");
			break;

		case RLT_WLC:
			while (status == SOStatus.RLT_WLC) {
				wait();
				status = SOStatus.NL;
			}
			System.out.println(idclient + " : Switching from rtaken_wcached to uncached");
			break;
		}
	}

	public synchronized Object invalidate_writer() throws InterruptedException {
		switch (status) {
		case NL:
			status = SOStatus.NL;
			System.out.println(idclient + " : ERROR INVALIDATE WRITER NL §§§§§§§§§§§§§§§§");
			break;

		case RLC:
			status = SOStatus.NL;
			System.out.println(idclient + " : ERROR INVALIDATE WRITER RLC §§§§§§§§§§§§§§§§");
			break;

		case WLC:
			status = SOStatus.NL;
			System.out.println(idclient + " : Switching from wcached to uncached");
			break;

		case RLT:
			status = SOStatus.NL;
			System.out.println(idclient + " : ERROR INVALIDATE WRITER RLT §§§§§§§§§§§§§§§§");
			break;

		case WLT:
			while (status == SOStatus.WLT) {
				System.out.println(idclient + " : Waiting ...");
				wait();
				status = SOStatus.NL;
			}
			System.out.println(idclient + " : Switching from wtaken to uncached");
			break;

		case RLT_WLC:
			status = SOStatus.NL;
			System.out.println(idclient + " : Switching from rtaken_wcached to uncached");
			break;
		}
		return obj;
	}

	public int getId() {
		return id;
	}
}
