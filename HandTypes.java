public class HandTypes{
	private static String[] handTypes = new String[]{
		"(garbage)","(1 pair)","(2 pair)","(three of a kind)",
		"(straight)", "(flush)","(full house)","(four of a kind)",
		"(straight flush)"
	};
	public static String getHandType(int x){
		return "("+handTypes[x]+")";
	}
}