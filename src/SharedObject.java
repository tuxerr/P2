import java.io.*;
import java.util.concurrent.locks;

public enum SOStatus {
    NL,RLC,WLC,RLT,WLT,RLT_WLC;
}

public class SharedObject implements Serializable, SharedObject_itf {
	
    public Object obj;
	
    private int id;

    // 0 : NL
    // 1 : RLC
    // 2 : WLC
    // 3 : RLT
    // 4 : WLT
    // 5 : RLT_WLC
    private SOStatus status;
    private Client client;
//    private Lock lock;
//    private Condition unlocked;

    public SharedObject(Object obj,int i,Client cli) {
	this.obj = obj;
	id = i;
	status = NL;
        client=cli;
//        lock=new ReentrantLock();
//        unlocked=lock.newCondition();
    }
	
    // invoked by the user program on the client node
    public void lock_read() {
        switch(status) {
        case NL:
            status=RLT;
            cli.lock_read(id);
            break;
        
        case RLC:
            status=RLT;
            break;

        case WLC:
            status=RLT_WLC;
            cli.lock_read(id);
            break;

        case RLT:
            status=RLT;
            break;

        case WLT:
            status=WLT;
            break;

        case RLT_WLC:
            status=RLT_WLC;
            break;
        }
    }

    // invoked by the user program on the client node
    public void lock_write() {
        switch(status) {
        case NL:
            status=WLT;
            cli.lock_write(id);
            break;

        case RLC:
            status=WLT;
            cli.lock_write(id);
            break;

        case WLC:
            status=WLT;
            break;

        case RLT:
            status=WLT;
            cli.lock_write(id);
            break;

        case WLT:
            status=WLT;
            break;

        case RLT_WLC:
            status=WLT;
            break;
        }

    }

    // invoked by the user program on the client node
    public synchronized void unlock() {
        switch(status) {
        case NL:
            status=NL;
            break;

        case RLC:
            status=RLC;
            break;

        case WLC:
            status=NL;
            break;

        case RLT:
            status=RLC;
            break;

        case WLT:
            status=WLC;
            break;

        case RLT_WLC:
            status=WLC;
            break;
        }
        notify();
    }

    // callback invoked remotely by the server
    public synchronized Object reduce_lock() {
        switch(status) {
        case NL:
            status=NL;
            break;

        case RLC:
            status=NL;
            break;

        case WLC:
            status=NL;
            break;

        case RLT:
            status=NL;
            break;

        case WLT:
            status=RLC;
            break;

        case RLT_WLC:
            status=RLT;
            break;
        }
    }

    // callback invoked remotely by the server
    public synchronized void invalidate_reader() {
        switch(status) {
        case NL:
            status=NL;
            break;

        case RLC:
            status=NL;
            break;

        case WLC:
            status=WLC;
            break;

        case RLT:
            wait();
            status=NL;
            break;

        case WLT:
            status=WLT;
            break;

        case RLT_WLC:
            wait();
            status=NL;
            break;
        }
    }

    public synchronized Object invalidate_writer() {
        switch(status) {
        case NL:
            status=NL;
            break;

        case RLC:
            status=NL;
            break;

        case WLC:
            status=NL;
            break;

        case RLT:
            status=NL;
            break;

        case WLT:
            wait();
            status=NL;
            break;

        case RLT_WLC:
            status=NL;
            break;
        }
    }
}
