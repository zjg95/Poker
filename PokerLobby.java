import java.net.*;
import java.io.*;
import java.util.ArrayList;
public class PokerLobby extends Thread {

    private class ClientListener implements Runnable {
        private Client client;
        private Player player;
        private int number;
        private ListenType listenType;
        private String message;
        public ClientListener(Client c, ListenType l, String m){
            client = c;
            listenType = l;
            number = client.player.number;
            player = client.player;
            message = m;
        }
        public void run(){
            switch (listenType) {
                case BET:
                    bet();
                    return;
                case MOVE:
                    move();
                    break;
                case YESNO:
                    yesno();
                    break;
            }
            client.tell("Waiting for other players...");
        }
        private void bet(){
            System.out.printf("[ClientListener] waiting for player %s\n",client);
            client.tell("askbet");
            client.tell(""+minimum_bet);

            String response = client.listen();
            if (response.equals("fold")){
                player.fold();
                broadcast_active(""+client+" folds");
            }
            else {
                int bet = Integer.parseInt(response);
                if (bet>0){
                    player.bet(bet,true);
                    broadcast_active(""+client+" raises the bet to $"+bet);
                }
            }
            client.tell("Waiting for other players to make their bets...");
        }
        private void yesno(){
            client.tell("yesno");
            client.tell(message);
            String res = client.listen();
            if (!res.equals("y")){
                disconnect_client(client);
            }
        }
        private void move(){
            System.out.printf("[ClientListener] waiting for player %s\n",client);
            System.out.printf("[%s]: ",client);
            for (Card c:player.hand)
                System.out.printf("%s ",c);
            System.out.println();
            client.tell("move");
            String answer;
            int count = 0;
            while (!(answer = client.listen()).equals("done")){
                System.out.printf("[Player %d]: discard %s\n",number,answer);
                int card = Integer.parseInt(answer);
                player.discard(card);
                count++;
            }
            for (int q = 0; q<count;q++){
                Card c = player_draw_next_card(player);
                System.out.printf("[%s]: drawing %s\n",client,c);
                tell_draw_card(client,c);
            }
            client.tell("printhand");
        }
    }

    private ArrayList<Client> inactiveClients; // clients that are in a suspended state
    private ArrayList<Client> allClients; // all clients connected to server
    private ArrayList<Client> activeClients; // clients actively playing
    private ArrayList<Client> joiningClients; // clients that are waiting to join

	private Game game;
	private boolean isPrivate;
    private int
        numPlayers, minimum_bet, maximum_bet,
        bet_pool, bonus_pool, initial_bet;

    public boolean lobby_alive;

    private enum ListenType {
        MOVE, YESNO, BET
    }
	public PokerLobby() {
        super("PokerLobby");

        allClients = new ArrayList<Client>();
        activeClients = new ArrayList<Client>();
        joiningClients = new ArrayList<Client>();
        inactiveClients = new ArrayList<Client>();

        lobby_alive = true;
        numPlayers = 0;
        initial_bet = 1;
        minimum_bet = 0;
        maximum_bet = 50;
        bonus_pool = 0;
        bet_pool = 0;
    }

    public void run(){
        System.out.println("[PokerLobby] Starting lobby.");
        while (lobby_alive){
            acceptClients();
            if (hasPlayers())
                play();
        }
        disconnect_all();
        System.out.println(" [PokerLobby] Closing lobby.");
    }

    /*
        I/O methods
    */
    public synchronized boolean addClient(Client c){ // adds clients to waiting list
    	if (isPrivate)
    		return false;
        c.tell("Waiting to connect; game in session.");
        for (Client a : allClients){
            a.tell("A new player has been added to the lobby.");
        }
        c.player = new Player();
    	allClients.add(c);
        joiningClients.add(c);
    	System.out.printf("[PokerLobby] added client %s to wait queue\n",c);
    	return true;
    }
    private synchronized void acceptClients(){ // adds clients to active list
        for (Client c: joiningClients){
            activeClients.add(c);
            numPlayers++;
            c.tell("You have been added to the game.");
            System.out.printf("[Server] %s is active\n",c);
        }
        joiningClients.clear();
    }
    private synchronized void broadcast_all(String message){
        for (Client a: allClients){
            a.tell(message);
        }
    }
    private synchronized void broadcast_active(String message){
        for (Client a: activeClients)
            a.tell(message);
    }
    private synchronized void suspendClient(Client c){ // removes client from active list and adds to suspended list
        activeClients.remove(c);
        inactiveClients.add(c);
        numPlayers--;
        System.out.printf("[server] suspended client %s\n",c);
    }
    private synchronized void unsuspendClient(Client c){
        activeClients.add(c);
        inactiveClients.remove(c);
        numPlayers++;
        System.out.printf("[server] unsuspended client %s\n",c);
    }
    private synchronized void disconnect_all(){ // disconnects all clients
    	for (Client c : allClients){
    		disconnect_client(c);
    	}
        allClients.clear();
        joiningClients.clear();
        activeClients.clear();
        inactiveClients.clear();
        System.out.printf("[server] disconnected all clients\n");
    }
    private synchronized void disconnect_client(Client c){
        c.tell("kill");
        c.disconnect();
        allClients.remove(c);
        joiningClients.remove(c);
        activeClients.remove(c);
        inactiveClients.remove(c);
        numPlayers--;
        System.out.printf("[server] disconnected %s\n",c);
    }
    /*
        operational methods
    */
    private synchronized void tell_all_draw_hand(){
        for (Client a: activeClients){
            for (int j = 0; j<5; j++){
                tell_draw_card(a,a.player.hand[j]);
            }
            a.tell("printhand");
        }
    }
    private synchronized void tell_draw_card(Client a, Card c){
        a.tell("draw");
        a.tell(""+c.number);
        a.tell(""+c.suite);
    }
    private synchronized Card player_draw_next_card(Player p){
        return p.draw(game.deck);
    }
    private void play(){        
        start_game();

        get_response(ListenType.MOVE,null);

        if (numPlayers>1)
            take_bets();

        get_winner();

        get_response(ListenType.YESNO,"Play again?");
    }
    private void get_winner(){
        Player winner = game.getWinner();
        Client w=null;
        if (winner != null){
            int type = winner.getHandType();
            for (Client a : activeClients){
                if (a.player==winner){
                    w = a;
                }
                else{
                    tell_lose(a,winner,type);
                    a.player.losses++;
                }
            }
            winner.wins++;
            System.out.printf("[server] %s wins! Paying $%d\n",w,bet_pool);
            pay_winner(w);
        }
        else {
            for (Client a: activeClients)
                a.tell("                      Everyone lost!");
        }
    }
    private void tell_lose(Client a,Player winner,int type){
        a.tell("lose");
        a.tell(""+winner.number);
        for (Card c: winner.hand){
            a.tell(""+c.number);
            a.tell(""+c.suite);
        }
        a.tell(Player.handTypes[type]);
        a.tell("Player "+winner.number+" won $"+bet_pool);
    }
    private void get_response(ListenType listen,String message){
        ArrayList<Thread> threads = new ArrayList<Thread>();
        for (Client a : activeClients){
            Thread m = new Thread(new ClientListener(a,listen,message));
            threads.add(m);
            m.start();
        }
        for (Thread t: threads)
            try{
                t.join();
            }catch(InterruptedException e){
                System.out.println("[Server] unable to wait for client thread! get_response()");
            }
    }
    private boolean unevenBets(){
        for (Client a: activeClients){
            Player p = a.player;
            if (p.fold)
                continue;
            if (minimum_bet<p.current_bet)
                minimum_bet=p.current_bet;
        }
        for (Client a: activeClients){
            Player p = a.player;
            if (p.fold)
                continue;
            if (p.current_bet<minimum_bet)
                return true;
        }
        return false;
    }
    private void take_bets(){
        do {
            ArrayList<Thread> threads = new ArrayList<Thread>();
            for (Client a : activeClients){
                int current_bet = a.player.current_bet;
                if (!(current_bet<minimum_bet||current_bet==0)){
                    a.tell("Waiting for other players to make their bets.");
                    continue;
                }
                if (!a.player.canBet(minimum_bet-current_bet)){
                    a.tell("Waiting for other players to make their bets.");
                    continue;
                }
                Thread m = new Thread(new ClientListener(a,ListenType.BET,null));
                threads.add(m);
                m.start();
            }
            for (Thread t: threads)
                try{
                    t.join();
                }catch(InterruptedException e){
                    System.out.println("[Server] unable to wait for client thread! get_response()");
                }
        } while (unevenBets());
        for (Client c: activeClients)
            bet_pool+=c.player.current_bet;
    }
    private void take_initial_bets(){
        for (Client a: activeClients){
            boolean success = a.player.bet(initial_bet,false);
            if (success){
                System.out.printf("Taking bet from %s\n",a);
                a.tell("bet");
                a.tell("0");
                a.tell("1");
                increase_bet_pool(initial_bet);
            }
            else{
                a.tell("You do not have enough money to bet.");
            }
        }
    }
    private synchronized void increase_bet_pool(int x){
        bet_pool+=x;
    }
    private synchronized void pay_winner(Client c){
        c.player.addMoney(bet_pool);
        c.tell("pay");
        c.tell(""+bet_pool);
        bet_pool = 0;
    }
    private void start_game(){
        game = new Game(activeClients);
        game.start();
        minimum_bet = 0;
        for (Client a: activeClients){
            a.tell("new");
            a.tell(""+a.player.number);
        }
        take_initial_bets();
        tell_all_draw_hand();
    }
    private boolean hasPlayers(){
        return numPlayers>0;
    }
}