import java.io.*;

public class SharedObject implements Serializable, SharedObject_itf {
	
	public Object obj;
	
	private int id;
	
	// values taken by lock :
	// 0 : NL
	// 1 : RLC
	// 2 : WLC
	// 3 : RLT
	// 4 : WLT
	// 5 : RLT_WLC
	private int lock;
	
	public SharedObject(Object o, int i) {
		obj = o;
		id = i;
		lock = 0;
	}
	
	// invoked by the user program on the client node
	public void lock_read() {
		if(lock==0||lock==1) {
			lock = 3;
		}
		else {
			if(lock==2) {
				lock = 5;
			}
			else {
				System.out.println("erreur sharedobject lock_read");
			}
		}
	}

	// invoked by the user program on the client node
	public void lock_write() {
		if(lock==0||lock==1||lock==2) {
			lock = 4;
		}
		else {
			System.out.println("erreur sharedobject lock_write");
		}
	}

	// invoked by the user program on the client node
	public synchronized void unlock() {
		if(lock==3) {
			lock = 1;
		}
		else {
			if(lock==4||lock==5) {
				lock = 2;
			}
			else {
				System.out.println("erreur sharedobject unlock");
			}
		}
		notify();//avant ou après le changement de lock ?
	}

	// callback invoked remotely by the server
	public synchronized Object reduce_lock() {
		if(lock==2||lock==4) {
			lock = 3;
		}
		else {
			System.out.println("erreur sharedobject reduce_lock");
		}
	}

	// callback invoked remotely by the server
	public synchronized void invalidate_reader() {
		if(lock==1) {
			lock = 0;
		}
		else {
			System.out.println("erreur sharedobject invalidate_reader");
		}
	}

	public synchronized Object invalidate_writer() {
		if(lock==2||lock==5) {
			lock = 0;
		}
		else {
			System.out.println("erreur sharedobject invalidate_writer");
		}
		return obj;
	}
}
