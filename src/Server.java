import java.rmi.*;
import java.rmi.server.UnicastRemoteObject;
import java.rmi.registry.*;
import java.net.*;
import java.util.HashMap;


public class Server extends UnicastRemoteObject implements Server_itf {
    private HashMap<int,ServerObject> objmap;
    private HashMap<String,int> namemap;
    private int currentid;

    public Server() {
        objmap=new HashMap<int,ServerObject>();
        namemap=new HashMap<String,int>();
        currentid=0;
    }

    public int lookup(String name) throws java.rmi.RemoteException {
        Integer i = namemap.get(name);
        if(i==null) {
            return -1;
        } else {
            return i;
        }
    }

    public void register(String name, int id) throws java.rmi.RemoteException {
        namemap.put(name,id);
    }

    public int create(Object o) throws java.rmi.RemoteException {
        ServerObject obj = new ServerObject();
        objmap.put(currentid,obj);
        currentid++;
    }

    public Object lock_read(int id, Client_itf client) throws java.rmi.RemoteException {
        
    }

    public Object lock_write(int id, Client_itf client) throws java.rmi.RemoteException {
    }
}