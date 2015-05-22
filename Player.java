import java.util.Arrays;
public class Player {
	public static String[] handTypes = new String[]{
		"(garbage)","(1 pair)","(2 pair)","(three of a kind)",
		"(straight)", "(flush)","(full house)","(four of a kind)",
		"(straight flush)"
	};
	public int money;
	public Card hand[];
	public int
		number,wins,losses,ties,cards_in_hand,current_bet;
	public String name;
	public boolean fold;
	public Player(){
		hand = new Card[5];
		money = 100;
		wins=losses=ties=current_bet=0;
		fold = false;
	}
	public void reset(){
		discard_all();
		current_bet = 0;
		fold = false;
	}
	public void fold(){
		fold = true;
	}
	public synchronized void addMoney(int x){
		money+=x;
	}
	private synchronized void subMoney(int x){
		money-=x;
	}
	public boolean bet(int x, boolean count){
		if (money<x)
			return false;
		subMoney(x);
		if (count){
			current_bet+=x;
		}
		return true;
	}
	public boolean canBet(int x){
		return x<=money;
	}
	public Card draw(Deck d){
		for (int i=0; i<5; i++) {
			if (hand[i]==null){
				hand[i]= d.nextCard();
				Card c = hand[i];
				return c;
			}
		}
		return null;
	}
	public void draw(Card c){
		for (int i=0; i<5; i++) {
			if (hand[i]==null){
				hand[i]= c;
				break;
			}
		}
	}
	public void discard(int x){
		hand[x] = null;
	}
	public void discard_all(){
		for (int i = 0; i<5; i++)
			discard(i);
	}
	public void sortHand(){
		System.out.println("sorting hand");
		Sort.sortHand(hand);
	}
	public void printHand(){
		System.out.print("			");	
		System.out.println("Your hand:");
		System.out.print("			");	
		for (int i = 0; i<5; i++)
			System.out.printf(" %d ",i);
		System.out.println();
		System.out.print("			");	
		for (Card c : hand)
			System.out.printf("%s ",c);
		System.out.println();
		System.out.print("			");
		System.out.println(Player.handTypes[getHandType()]);
	}

	// Hand type methods
	public int getHandType(){
		if (straightFlush())
			return 8;
		if (fourKind()!=null)
			return 7;
		if (fullHouse()!=null)
			return 6;
		if (flush())
			return 5;
		if (straight())
			return 4;
		if (threeKind()!=null)
			return 3;
		if (twoPair()!=null)
			return 2;
		if (pair()!=null)
			return 1;
		return 0;
	}
	private boolean straightFlush(){
		return (straight()&&flush());
	}
	private Card[] fourKind(){
		for (Card c: hand) {
			int count = 1;
			for (Card d: hand){
				if (c==d)
					continue;
				if (c.sameNumber(d))
					count++;
			}
			if (count>3)
				return new Card[1];
		}
		return null;
	}
	private Card[] fullHouse(){
		return null;
	}
	private boolean flush(){
		int suite = hand[0].suite;
		for (Card c : hand){
			if (c.suite!=suite)
				return false;
		}
		return true;
	}
	private boolean straight(){
		int[] nums = new int[5];
		for (int i = 0; i<5; i++)
			nums[i] = hand[i].number;
		Arrays.sort(nums);
		int x = nums[0];
		for (int i = 1; i<5; i++){
			if (x==1)
				x=10;
			if (nums[i]!=x+1)
				return false;
			x++;
		}
		return true;
	}
	private Card[] threeKind(){
		for (Card c: hand) {
			int count = 1;
			for (Card d: hand){
				if (c==d)
					continue;
				if (c.sameNumber(d))
					count++;
			}
			if (count>2)
				return new Card[2];
		}
		return null;
	}
	private Card[] twoPair(){
		int p1a,p1b;
		p1a=p1b = -1;
		for (int i = 0; i<5; i++) {
			for (int j=0; j<5; j++) {
				if (j==i)
					continue;
				if (hand[i].sameNumber(hand[j])){
					p1a=i;
					p1b=j;
					break;
				}
			}
		}
		if (p1a==-1)
			return null;
		for (int i = 0; i<5; i++) {
			if (i==p1a)
				continue;
			for (int j=0; j<5; j++) {
				if (j==i)
					continue;
				if (j==p1b||j==p1a)
					continue;
				if (hand[i].sameNumber(hand[j])){
					return new Card[]{hand[p1a],hand[p1b],hand[i],hand[j]};
				}
			}
		}
		return null;
	}
	private Card[] pair(){
		for (Card c: hand) {
			for (Card d: hand){
				if (c==d)
					continue;
				if (c.sameNumber(d))
					return new Card[2];
			}
		}
		return null;
	}
}