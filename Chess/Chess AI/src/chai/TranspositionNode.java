package chai;

public class TranspositionNode {
	private int height;
	private int nodeType; //0 for PV Node (exact score), 1 for Cut Node (lower bound)
						  // or 2 for All Node (upper bound).
	private int value;
	
	public TranspositionNode(int currHeight, int type, int evaluation){
		height = currHeight;
		nodeType = type;
		value = evaluation;
	}
	public int getHeight(){
		return height;
	}
	
	public int getType(){
		return nodeType;
	}
	
	public int getValue(){
		return value;
	}
	
}
