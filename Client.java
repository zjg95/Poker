import java.net.*;
import java.io.*;

public class Client {
	private Socket socket;
	private BufferedReader reader;
	private PrintWriter writer;

	public String address;
	public String hostName;
    public int portNumber;

    public Player player;

	public Client(Socket s){ // Client
		socket = s;
		try {
			reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
			writer = new PrintWriter(socket.getOutputStream(),true);
			InetAddress addr = socket.getLocalAddress();
        	address = addr.getHostAddress();
        	System.out.printf("[Server] Client Addr: %s\n",address);
		} catch (IOException e){
			System.out.println("[Server] Error initializing client");
		}
	}
	public Client(String hostName, int portNumber){ // Server
		try {
			socket = new Socket(hostName, portNumber);
			reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
			writer = new PrintWriter(socket.getOutputStream(),true);

			this.portNumber = portNumber;
			this.hostName = hostName;

			InetAddress addr = socket.getLocalAddress();
        	address = addr.getHostAddress();
        	System.out.printf("[Client] Server Addr: %s\n",address);
       	} catch (UnknownHostException e) {
            System.err.println("[Client] Don't know about host " + hostName);
            System.exit(1);
		} catch (IOException e){
			System.out.println("[Client] Couldn't get I/O for the connection to Server");
		}
	}
	public void tell(String message){
		writer.println(message);
	}
	public String listen(){
		try {
			return reader.readLine();
		} catch (IOException e){
			System.out.println("Unable to listen! client.java");
			System.exit(-1);
			return null;
		}
	}
	public void disconnect(){
        writer.println(-2);
        try {
	        socket.close();
	        writer.close();
	        reader.close();
	        System.out.println("[Client] Client disconnected.");
    	} catch (IOException e){
    		System.out.println("[Client] Unable to disconnect client!");
    	}
    }
    public void setPlayer(Player p){
    	player = p;
    }
    public Player getPlayer(){
    	return player;
    }
    public String toString(){
    	return address;
    }
}