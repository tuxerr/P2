import java.io.Serializable;

public class SharedObject implements Serializable, SharedObject_itf {

	private static final long serialVersionUID = 1L;
	public Object obj;
	private Object old;
	private int id;
	private SOStatus status;
	private int idclient;
	private boolean appel;

	public SharedObject(Object obj, int i, int idclient) {
		this.obj = obj;
		this.old = null;
		id = i;
		status = SOStatus.NL;
		this.idclient = idclient;
		appel = false;
	}

	// invoked by the user program on the client node
	public void lock_read() {
		synchronized (this) {
			System.out.println(idclient + " : Lock_reading ...");
			switch (status) {
			case NL:
				appel = true;
				break;

			case RLC:
				System.out.println(idclient
						+ " : Switching from rcached to rtaken");
				status = SOStatus.RLT;
				break;

			case WLC:
				System.out.println(idclient
						+ " : Switching from wcached to rtaken_wcached");
				status = SOStatus.RLT_WLC;
				break;

			case RLT:
				System.out.println(idclient
						+ " : ERROR LOCK_READ RLT §§§§§§§§§§§§§§§§");
				break;

			case WLT:
				System.out.println(idclient
						+ " : ERROR LOCK_READ WLT §§§§§§§§§§§§§§§§");
				break;

			case RLT_WLC:
				System.out.println(idclient
						+ " : ERROR LOCK_READ RLT_WLC §§§§§§§§§§§§§§§§");
				break;
			}
			if (!appel) {
				return;
			}
		}

		while (status != SOStatus.RLT) {
			System.out.println(idclient + " : Tentative de lock_read");
			obj = Client.lock_read(id);
			synchronized (this) {
				if (obj != null) {
					System.out.println(idclient
							+ " : Switching from uncached to rtaken");
					status = SOStatus.RLT;
					appel = false;
				} else {
					System.out.println(idclient + " : FAIL");
				}
			}
		}
		System.out.println(idclient + " : Lock_reading DONE");
	}

	// invoked by the user program on the client node
	public void lock_write() {
		synchronized (this) {
			System.out.println(idclient + " : Lock_writing ...");
			switch (status) {
			case NL:
				appel = true;
				break;

			case RLC:
				appel = true;
				break;

			case WLC:
				System.out.println(idclient
						+ " : Switching from wcached to wtaken");
				status = SOStatus.WLT;
				break;

			case RLT:
				System.out.println(idclient
						+ " : ERROR LOCK_WRITE RLT §§§§§§§§§§§§§§§§");
				break;

			case WLT:
				System.out.println(idclient
						+ " : ERROR LOCK_WRITE WLT §§§§§§§§§§§§§§§§");
				break;

			case RLT_WLC:
				System.out.println(idclient
						+ " : ERROR LOCK_WRITE RLT_WLC §§§§§§§§§§§§§§§§");
				break;
			}
			if (!appel) {
				return;
			}
		}

		while (status != SOStatus.WLT) {
			System.out.println(idclient + " : Tentative de lock_write");
			obj = Client.lock_write(id);
			synchronized (this) {
				if (obj != null) {
					System.out.println(idclient
							+ " : Switching from uncached/wcached to wtaken");
					status = SOStatus.WLT;
					appel = false;
				} else {
					System.out.println(idclient + " : FAIL");
				}
			}
		}
		System.out.println(idclient + " : Lock_writing DONE");
	}

	// invoked by the user program on the client node
	public synchronized void unlock() {
		System.out.println(idclient + " : Unlocking Object");
		switch (status) {
		case NL:
			System.out
					.println(idclient + " : ERROR UNLOCK NL §§§§§§§§§§§§§§§§");
			break;

		case RLC:
			System.out.println(idclient
					+ " : ERROR UNLOCK RLC §§§§§§§§§§§§§§§§");
			break;

		case WLC:
			System.out.println(idclient
					+ " : ERROR UNLOCK WLC §§§§§§§§§§§§§§§§");
			break;

		case RLT:
			status = SOStatus.RLC;
			System.out
					.println(idclient + " : Switching from rtaken to rcached");
			break;

		case WLT:
			status = SOStatus.WLC;
			System.out
					.println(idclient + " : Switching from wtaken to wcached");
			break;

		case RLT_WLC:
			status = SOStatus.WLC;
			System.out.println(idclient
					+ " : Switching from rtaken_wcached to wcached");
			break;
		}
		System.out.println(idclient + " : Notifying");
		notify();
	}

	// callback invoked remotely by the server
	public Object reduce_lock() throws InterruptedException {
		switch (status) {
		case NL:
			while ((status != SOStatus.WLC) && (status != SOStatus.RLT_WLC)) {
				System.out.println(idclient + " : Waiting RL ...");
				wait();
			}
			if (status == SOStatus.WLC) {
				status = SOStatus.RLC;
			} else {
				status = SOStatus.RLT;
			}
			System.out.println(idclient
					+ " : FAIL RMI RL_NL");
			break;

		case RLC:
			System.out.println(idclient
					+ " : ERROR REDUCE_LOCK RLC §§§§§§§§§§§§§§§§");
			break;

		case WLC:
			status = SOStatus.RLC;
			System.out.println(idclient
					+ " : Switching from wcached to rcached");
			break;

		case RLT:
			System.out.println(idclient
					+ " : ERROR REDUCE_LOCK RLT §§§§§§§§§§§§§§§§");
			break;

		case WLT:
			while ((status != SOStatus.WLC) && (status != SOStatus.RLT_WLC)) {
				System.out.println(idclient + " : Waiting RL ...");
				wait();
			}
			if (status == SOStatus.WLC) {
				status = SOStatus.RLC;
			} else {
				status = SOStatus.RLT;
			}
			System.out.println(idclient
					+ " : Switching from wcached to rcached");
			break;

		case RLT_WLC:
			status = SOStatus.RLT;
			System.out.println(idclient
					+ " : Switching from rtaken_wcached to rtaken");
			break;
		}
		return obj;
	}

	// callback invoked remotely by the server
	public void invalidate_reader() throws InterruptedException {

		switch (status) {
		case NL:
			while (status != SOStatus.RLC) {
				System.out.println(idclient + " : Waiting RMI FAIL IR_NL ...");
				wait();
			}
			status = SOStatus.NL;
			System.out.println(idclient + " : RMI FAIL");
			break;

		case RLC:
			status = SOStatus.NL;
			System.out.println(idclient
					+ " : Switching from rcached to uncached");
			break;

		case WLC:
			// cas de merde
			System.out.println(idclient + " : Waiting RMI FAIL IR_WLC ...");
			wait();
			while (status != SOStatus.WLC) {
				wait();
			}
			status = SOStatus.NL;
			System.out.println(idclient + " : RMI FAIL");
			break;

		case RLT:
			while (status != SOStatus.RLC) {
				System.out.println(idclient + " : Waiting IR ...");
				wait();
			}
			status = SOStatus.NL;
			System.out.println(idclient
					+ " : Switching from rtaken to uncached");
			break;

		case WLT:
			System.out.println(idclient
					+ " : ERROR INVALIDATE READER WLT §§§§§§§§§§§§§§§§");
			break;

		case RLT_WLC:
			while (status != SOStatus.WLC) {
				System.out.println(idclient + " : Waiting IR ...");
				wait();
			}
			status = SOStatus.NL;
			System.out.println(idclient
					+ " : Switching from rtaken_wcached to uncached");
			break;
		}
		obj = null;
	}

	public Object invalidate_writer() throws InterruptedException {
		switch (status) {
		case NL:
			while ((status != SOStatus.WLC) && (status != SOStatus.RLC)) {
				System.out.println(idclient + " : Waiting RMI FAIL IW_NL ...");
				wait();
				System.out.println(idclient
						+ " : Waiting stopped : switching to NL");
			}
			status = SOStatus.NL;
			System.out.println(idclient + " : RMI FAIL");
			break;

		case RLC:
			while (status != SOStatus.WLC) {
				System.out.println(idclient + " : Waiting RMI FAIL IW_RLC ...");
				wait();
				System.out.println(idclient
						+ " : Waiting stopped : switching to NL");
			}
			status = SOStatus.NL;
			System.out.println(idclient + " : RMI FAIL");
			break;

		case WLC:
			status = SOStatus.NL;
			System.out.println(idclient
					+ " : Switching from wcached to uncached");
			break;

		case RLT:
			System.out.println(idclient
					+ " : ERROR INVALIDATE WRITER RLT §§§§§§§§§§§§§§§§");
			break;

		case WLT:
			while (status != SOStatus.WLC) {
				System.out.println(idclient + " : Waiting  IW ...");
				wait();
				System.out.println(idclient
						+ " : Waiting stopped : switching to NL");
			}
			status = SOStatus.NL;
			System.out.println(idclient
					+ " : Switching from wtaken to uncached");
			break;

		case RLT_WLC:
			while (status != SOStatus.WLC) {
				System.out.println(idclient + " : Waiting IW ...");
				wait();
				System.out.println(idclient
						+ " : Waiting stopped : switching to NL");
			}
			status = SOStatus.NL;
			System.out.println(idclient
					+ " : Switching from rtaken_wcached to uncached");
			break;
		}

		// DAFUQ
		old = obj;
		obj = null;
		if (old == null) {
			System.out
					.println("WTFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFF");
		}
		return old;
	}

	public int getId() {
		return id;
	}
}
