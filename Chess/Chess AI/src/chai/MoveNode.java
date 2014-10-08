package chai;

public class MoveNode implements Comparable<MoveNode>{
	public short move;
	public int utility;
	
	public MoveNode(short theMove, int value){
		move = theMove;
		utility = value;
	}
	
	public boolean equals(Object other) {
		return (utility == ((MoveNode) other).utility);
	}
	
	public int compareTo(MoveNode o) {
		return (int) Math.signum(o.utility - utility);
	}
	
}
