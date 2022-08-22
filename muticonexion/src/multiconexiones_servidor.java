import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.Hashtable;
 
interface config{
	public static final int    _port    = 8081;
	public static final int    _backlog = 500;
	public static final String _host    = "localhost";
}
class server extends ServerSocket implements Runnable 
{
	private class user extends Thread{
		private BufferedReader reader      = null;
		private PrintWriter    printWriter = null;
		private Socket  socket             = null;
		private boolean connected          = false;
		private int     nro = 0;
		private String username = null;
		public user(Socket socket, int nro){
			this.socket = socket; 
			try {
				reader = new BufferedReader(new InputStreamReader(server.this.socket.getInputStream()));
			    printWriter = new PrintWriter(this.socket.getOutputStream());
				connected = true;
				username = this.socket.getLocalAddress().getHostAddress();
				this.nro = nro;
			} catch (IOException e) {
			}
		}
             void writer(String msg){
			 printWriter.println(msg);
		}
		public boolean isActive() {
			return connected;
		}
		public void setActive(boolean active) {
			this.connected = active;
		}
		@Override
		public void run() {
			while(connected){
				try {
					if(reader.ready()){	
						String msg = reader.readLine();
						System.out.println(" User:"+nro+ "+"+username+"-> "+msg);
						if(msg.equalsIgnoreCase("disconnected")){
							handler.remove(username);
						}
					}
				} catch (IOException e) {
				}
			}
		}}
	private Socket      socket	    = null;
	boolean init                    = true;
	private final Hashtable< String, user> handler  = new Hashtable<>();
	int nro = 0;
	@Override public void run() {
		while(init){
			try {socket = accept();
				System.out.println("New User: "+socket.getLocalAddress().getHostAddress());
					handler.put(socket.getLocalAddress().getHostAddress(), new user(socket,nro));
					handler.get(socket.getLocalAddress().getHostAddress()).writer("true");
				nro ++;
			} catch (IOException e) {
			}		
		}
	}
	public boolean isInit() {
		return init;
	}
	public void setInit(boolean init) {
		this.init = init;
	}
	public server(int port, int backlog, InetAddress bindAddr)
			throws IOException {
		super(port, backlog, bindAddr);
		System.out.println("---------------------------------------");
		System.out.println("Server is OK");
		System.out.println("---------------------------------------");
		new Thread(this).start();
	}
	/**
	 * @paramargs
	 * @throws IOException 
	 * @throws UnknownHostException 
	 */
	public static void main(String[] args) throws UnknownHostException
	, IOException {
		new server(config._port,
				config._backlog,
				InetAddress.getByName(config._host));
	}
}
