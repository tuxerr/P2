import java.util.ArrayList;


public class ServerObject {
	
    public ServerObject(int id,Object o) {
        Clients = new ArrayList<Client_itf>();
        lock=0;
	obj_cache=o.clone();
    }

    // 0 : NL
    // 1 : RL
    // 2 : WL
    private int lock;
    private int id;
    private ArrayList<Client_itf> Clients;
    private Object obj_cache;
	
    public void lock_read(Client cli) {
	if(lock==0) {
	    Clients.add(cli);
	} else if(lock==1) {
	    Clients.add(cli);

	} else if(lock==2) {
	    for(Client_itf cli : Clients) {
		Object o = cli.invalidate_writer(id);
		obj_cache=o.clone();
	    }
	    Clients.clear();
	    Clients.add(cli);
	}
        lock = 1;
    }
	
    public void lock_write(Client cli) {
	if(lock==0) {

	} else if(lock==1) {
	    for(Client_itf cli : Clients) {
		cli.invalidate_reader(id);
	    }

	} else if(lock==2) {
	    for(Client_itf cli : Clients) {
		Object o = cli.invalidate_writer(id);
		obj_cache=o.clone();
	    }

	}

        Clients.clear();
        Clients.add(cli);
        lock = 2;
    }

    public Object getCache() {
	return obj_cache;
    }
}
