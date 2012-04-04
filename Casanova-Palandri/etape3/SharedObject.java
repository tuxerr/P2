import java.io.ObjectStreamException;
import java.io.Serializable;

public class SharedObject implements Serializable, SharedObject_itf {

	private static final long serialVersionUID = 1L;
	public transient Object obj;
	private Object old;
	private int id;
	private SOStatus status;
	private boolean appel;
	private boolean cote;

	public SharedObject(Object obj, int i) {
		this.obj = obj;
		this.old = null;
		id = i;
		status = SOStatus.NL;
		appel = false;
		cote = true;
	}

	// invoked by the user program on the client node
	public void lock_read() {
		synchronized (this) {
			switch (status) {
			case NL:
				appel = true;
				break;

			case RLC:
				status = SOStatus.RLT;
				break;

			case WLC:
				status = SOStatus.RLT_WLC;
				break;

			case RLT:
				break;

			case WLT:
				break;

			case RLT_WLC:
				break;
			}
			if (!appel) {
				return;
			}
		}

		obj = Client.lock_read(id);
		synchronized (this) {
			status = SOStatus.RLT;
			appel = false;
		}
	}

	// invoked by the user program on the client node
	public void lock_write() {
		synchronized (this) {
			switch (status) {
			case NL:
				appel = true;
				break;

			case RLC:
				appel = true;
				break;

			case WLC:
				status = SOStatus.WLT;
				break;

			case RLT:
				break;

			case WLT:
				break;

			case RLT_WLC:
				break;
			}
			if (!appel) {
				return;
			}
		}

		obj = Client.lock_write(id);
		synchronized (this) {
			status = SOStatus.WLT;
			appel = false;
		}
	}

	// invoked by the user program on the client node
	public synchronized void unlock() {
		switch (status) {
		case NL:
			break;

		case RLC:
			break;

		case WLC:
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

	public synchronized void setLock(SOStatus s) {
		status = s;
	}

	// callback invoked remotely by the server
	public Object reduce_lock() throws InterruptedException {
		switch (status) {
		case NL:
			while ((status != SOStatus.WLC) && (status != SOStatus.RLT_WLC)) {
				wait();
			}
			if (status == SOStatus.WLC) {
				status = SOStatus.RLC;
			} else {
				status = SOStatus.RLT;
			}
			break;

		case RLC:
			break;

		case WLC:
			status = SOStatus.RLC;
			break;

		case RLT:
			break;

		case WLT:
			while ((status != SOStatus.WLC) && (status != SOStatus.RLT_WLC)) {
				wait();
			}
			if (status == SOStatus.WLC) {
				status = SOStatus.RLC;
			} else {
				status = SOStatus.RLT;
			}
			break;

		case RLT_WLC:
			status = SOStatus.RLT;
			break;
		}
		return obj;
	}

	// callback invoked remotely by the server
	public void invalidate_reader() throws InterruptedException {

		switch (status) {
		case NL:
			while (status != SOStatus.RLC) {
				wait();
			}
			status = SOStatus.NL;
			break;

		case RLC:
			status = SOStatus.NL;
			break;

		case WLC:
			wait();
			while (status != SOStatus.WLC) {
				wait();
			}
			status = SOStatus.NL;
			break;

		case RLT:
			while (status != SOStatus.RLC) {
				wait();
			}
			status = SOStatus.NL;
			break;

		case WLT:
			break;

		case RLT_WLC:
			while (status != SOStatus.WLC) {
				wait();
			}
			status = SOStatus.NL;
			break;
		}
		obj = null;
	}

	public Object invalidate_writer() throws InterruptedException {
		switch (status) {
		case NL:
			while ((status != SOStatus.WLC) && (status != SOStatus.RLC)) {
				wait();
			}
			status = SOStatus.NL;
			break;

		case RLC:
			while (status != SOStatus.WLC) {
				wait();
			}
			status = SOStatus.NL;
			break;

		case WLC:
			status = SOStatus.NL;
			break;

		case RLT:
			break;

		case WLT:
			while (status != SOStatus.WLC) {
				wait();
			}
			status = SOStatus.NL;
			break;

		case RLT_WLC:
			while (status != SOStatus.WLC) {
				wait();
			}
			status = SOStatus.NL;
			break;
		}
		old = obj;
		obj = null;
		return old;
	}

	// readResolve
	public Object readResolve() throws ObjectStreamException {
		if (this.cote) {
			this.cote = false;
			return this;
		} else {
			this.cote = true;
			if (Client.objects.containsKey(this.id)) {
				return Client.objects.get(this.id);
			} else {
				this.status = SOStatus.NL;
				Client.objects.put(this.id, this);
				return this;
			}
		}
	}

	public int getId() {
		return id;
	}
}
