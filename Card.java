public class Card {
	// ♥ ♦ ♣ ♠
	public int number;
	public int suite;
	public Card(int n, int s){
		number = n;
		suite = s;
	}
	public String toString(){
		String string = "";
		switch (suite){
			case(1): string+="C"; break;
			case(0): string+="H"; break;
			case(2): string+="D"; break;
			case(3): string+="S"; break;
		}
		switch (number){
			case(1): string+="A"; break;
			case(11): string+="J"; break;
			case(12): string+="Q"; break;
			case(13): string+="K"; break;
			default: string+=number;
		}
		return string;
	}
	public boolean sameNumber(Card c){
		return (number == c.number);
	}
	public boolean sameSuite(Card c){
		return (suite == c.suite);
	}
	public int compareTo(Card c){
		if (number == c.number)
			return 0;
		return (number > c.number)? 1: -1;
	}
}