import java.util.Arrays;
import java.util.Comparator;
public class Sort {
	private static abstract class CustomComparator implements Comparator<Object> {
    	@Override
    	public abstract int compare(Object o1, Object o2);
	}
	// Filters
	private static CustomComparator cardNumbers = new CustomComparator(){
		public int compare(Object one, Object two){
			if (one==null)
				return -1;
			if (two==null)
				return 1;
			int e1 = ((Card)one).number;
			int e2 = ((Card)two).number;
			return compareTo(e1,e2);
		}
	};
	private static int compareTo(int x, int y){
		if (x==1)
			x=20;
		if (x<y)
			return -1;
		if (x>y)
			return 1;
		return 0;
	}
	// Methods
	public static void sortHand(Card[] hand){
		Arrays.sort(hand,cardNumbers);
	}
}