import java.io.*;
import java.net.*;

public class PokerServer {
	private static ServerSocket socket;

	public static void main(String[] args){
		System.out.println("---------------------");
		System.out.println("| Poker Server v1.3 |");
		System.out.println("---------------------");

		try (ServerSocket serverSocket = new ServerSocket(4444)) {
        	socket = serverSocket;
	        PokerLobby lobby = new PokerLobby();
        	System.out.println("[PokerServer] new lobby created.");
        	lobby.start();
        	// Listen for clients
            while (true) {
            	System.out.println("[PokerServer] awaiting connection...");
            	lobby.addClient(getClient());
	        }

	    } catch (IOException e) {
            System.err.println("[PokerServer] Could not listen on port 4444");
            System.exit(-1);
        }
	}
    public static Client getClient(){
    	Client a = null;
    	try{
    		a = new Client(socket.accept());
    		System.out.println("[PokerServer] Client accepted.");
    	} catch (IOException e) {
    		System.out.println("[PokerServer] Unable to accept client!");
    	}
    	return a;
    }
}