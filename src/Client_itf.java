public interface Client_itf extends java.rmi.Remote {
	public Object reduce_lock(int id, int clidem) throws java.rmi.RemoteException;
	public void invalidate_reader(int id, int clidem) throws java.rmi.RemoteException;
	public Object invalidate_writer(int id, int clidem) throws java.rmi.RemoteException;
	public int getIdClient() throws java.rmi.RemoteException;
}
