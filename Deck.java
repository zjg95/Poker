import java.util.LinkedList;
import java.util.Random;
public class Deck {
	private Card cards[];
	private LinkedList<Card> deck;
	private Random shuffler;
	public Deck(){
		shuffler = new Random();
		deck = new LinkedList<Card>();
		cards = new Card[52];
		int pos = 0;
		for (int i = 0; i<4; i++){
			for (int j = 1; j<14; j++) {
				cards[pos++] = new Card(j,i);
			}
		}
		shuffle();
	}
	public void shuffle(){
		boolean used[] = new boolean[52];
		for (int i = 0; i<52; i++)
			used[i] = false;
		int n;
		int count = 0;
		while (count<52){
			do {
				n = shuffler.nextInt(52);
			} while (used[n]);
			count++;
			used[n] = true;
			deck.add(cards[n]);
		}
	}
	public Card nextCard(){
		return deck.pop();
	}
	public void print(){
		int s = 1;
		for (Card i : cards){
			System.out.printf("%s ",i);
			if (++s>13){
				s=1;
				System.out.println();
			}
		}
	}
}