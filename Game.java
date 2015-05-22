import java.util.ArrayList;
import java.util.Arrays;
public class Game {
	public Player players[];
	public Deck deck;

	public Game(ArrayList<Client> clients){
		players = new Player[clients.size()];
		for (int i = 0; i<players.length; i++)
			players[i] = clients.get(i).player;
		deck = new Deck();
		for (Player p: players)
			p.reset();
	}
	public void start(){
		for (int i = 0; i<5; i++){
			for (Player p: players){
				p.draw(deck);
			}
		}
		for (Player p : players){
			p.sortHand();
		}
	}
	public Card draw(Player p){
		return p.draw(deck);
	}
	public Player getWinner(){
		boolean tie = false;
		Player winner=null;
		for (Player p: players){
			if (p.fold)
				continue;
			winner = p;
			break;
		}
		if (winner==null)
			return null;
		for (Player p: players){
			if (p.fold)
				continue;
			if (p!=winner){
				p = compareHands(p,winner);
				if (p==null){
					tie = true;
					continue;
				}
				else{
					winner = p;
					tie = false;
				}
			}
		}
		if (tie)
			return null;
		return winner;
	}
	private Player compareHands(Player a, Player b){
		int p1 = a.getHandType();
		int p2 = b.getHandType();
		if (p1>p2)
			return a;
		else if (p2>p1)
			return b;
		else {
			return null;
		}
	}
}