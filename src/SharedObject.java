import java.io.Serializable;

public class SharedObject implements Serializable, SharedObject_itf {


	private static final long serialVersionUID = 1L;
	public Object obj;
	private int id;
	private SOStatus status;

	// private Lock lock;
	// private Condition unlocked;

	public SharedObject(Object obj, int i) {
		this.obj = obj;
		id = i;
		status = SOStatus.NL;
		// lock=new ReentrantLock();
		// unlocked=lock.newCondition();
	}

	// invoked by the user program on the client node
	public void lock_read() {
		switch (status) {
		case NL:
                    status = SOStatus.RLT;
			obj=Client.lock_read(id);
			break;

		case RLC:
			status = SOStatus.RLT;
			break;

		case WLC:
			status = SOStatus.RLT_WLC;
			obj=Client.lock_read(id);
			break;

		case RLT:
			status = SOStatus.RLT;
			break;

		case WLT:
			status = SOStatus.WLT;
			break;

		case RLT_WLC:
			status = SOStatus.RLT_WLC;
			break;
		}
	}

	// invoked by the user program on the client node
	public void lock_write() {
		switch (status) {
		case NL:
			status = SOStatus.WLT;
			obj=Client.lock_write(id);
			break;

		case RLC:
			status = SOStatus.WLT;
			obj=Client.lock_write(id);
			break;

		case WLC:
			status = SOStatus.WLT;
			break;

		case RLT:
			status = SOStatus.WLT;
			obj=Client.lock_write(id);
			break;

		case WLT:
			status = SOStatus.WLT;
			break;

		case RLT_WLC:
			status = SOStatus.WLT;
			break;
		}

	}

	// invoked by the user program on the client node
	public synchronized void unlock() {
		switch (status) {
		case NL:
			status = SOStatus.NL;
			break;

		case RLC:
			status = SOStatus.RLC;
			break;

		case WLC:
			status = SOStatus.NL;
			break;

		case RLT:
			status = SOStatus.RLC;
			break;

		case WLT:
			status = SOStatus.WLC;
			break;

		case RLT_WLC:
			status = SOStatus.WLC;
			break;
		}
		notify();
	}

	// callback invoked remotely by the server
	public synchronized Object reduce_lock() {
		switch (status) {
		case NL:
			status = SOStatus.NL;
			break;

		case RLC:
			status = SOStatus.NL;
			break;

		case WLC:
			status = SOStatus.NL;
			break;

		case RLT:
			status = SOStatus.NL;
			break;

		case WLT:
			status = SOStatus.RLC;
			break;

		case RLT_WLC:
			status = SOStatus.RLT;
			break;
		}
		return obj;
	}

	// callback invoked remotely by the server
	public synchronized void invalidate_reader() throws InterruptedException {
		switch (status) {
		case NL:
			status = SOStatus.NL;
			break;

		case RLC:
			status = SOStatus.NL;
			break;

		case WLC:
			status = SOStatus.WLC;
			break;

		case RLT:
                    while(status==RLT) {
			wait();
			status = SOStatus.NL;
                    }
                    break;

		case WLT:
			status = SOStatus.WLT;
			break;

		case RLT_WLC:
                    while(status==RLT_WLC) {
			wait();
			status = SOStatus.NL;
                    }
                    break;
		}
	}

	public synchronized Object invalidate_writer() throws InterruptedException {
		switch (status) {
		case NL:
			status = SOStatus.NL;
			break;

		case RLC:
			status = SOStatus.NL;
			break;

		case WLC:
			status = SOStatus.NL;
			break;

		case RLT:
			status = SOStatus.NL;
			break;

		case WLT:
                    while(status==WLT) {
                        wait();
			status = SOStatus.NL;
                    }
                    break;

		case RLT_WLC:
			status = SOStatus.NL;
			break;
		}
		return obj;
	}

	public int getId() {
		return id;
	}
}
