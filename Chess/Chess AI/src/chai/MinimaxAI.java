package chai;

import java.util.Random;

import chesspresso.Chess;
import chesspresso.move.IllegalMoveException;
import chesspresso.position.Position;

public class MinimaxAI implements ChessAI {
	
	boolean mated = false;
	
	int nodeCount = 0;
	public short getMove(Position position) {
		nodeCount = 0; //keep track of time.
		
		// copy the current position.
		String copyFEN = position.getFEN();
		Position copy = new Position(copyFEN);
		
		short move = 0;
		int depth = 1;
		//iterative deepening minimax.
		while(depth<2){
			move = miniMaxMove(copy, depth);
			if(move==0){
				break;
			}
			
			if(mated){
				mated = false;
				break;
			}
			depth++;
		}
		System.out.println("The node count is:  " + nodeCount);
		return move;
	}
	
	public short miniMaxMove(Position position, int maxDepth){
		nodeCount++; //to keep track of time.
		//get all moves from the current posiiton.
		short [] moves = position.getAllMoves();
		
		int max = Integer.MIN_VALUE;
		short maxMove = 0;
		int currentDepth = 1;
		int player = position.getToPlay();
		
		//for each move, get the utility using minValue.
		for(short move:moves){
			int minVal = minValue(move, position, maxDepth, currentDepth, player);
			//update max if below condition holds.
			if(minVal>max){
				max = minVal;
				maxMove = move;
			}
		}
		//Does this move eventually lead to a checkmate?
		if(max == Integer.MAX_VALUE){
			System.out.println("Evaluation value of move: " + max);
			mated = true;
		}
		
		if(maxDepth == 4 && !mated){
			System.out.println("Evaluation value of move: " + max);
		}
		
		//return the corresponding move.
		return maxMove;
	}
	
	
	
	public int minValue(short move, Position position, int maxDepth, int currentDepth, int player){
		
		//do the move.
		try {
			position.doMove(move);
		} catch (IllegalMoveException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		nodeCount++;
		//increment depth.
		int newDepth = currentDepth + 1;
		
		//do the cutoff test.
		if(position.isTerminal() || newDepth>maxDepth ){
			int util = utility2(position, player, currentDepth);
			position.undoMove();
			return util;
		}
		
		//loop through the moves finding the one with the minimum value.
		int min = Integer.MAX_VALUE;
		for(short nextMove:position.getAllMoves()){
			int maxVal = maxValue(nextMove, position, maxDepth, newDepth, player);
			if(maxVal<min){
				min = maxVal;
			}
		}
		
		position.undoMove();
		//return that min value.
		return min;
	}
	
	
	public int maxValue(short move, Position position, int maxDepth, int currentDepth, int player){
		//do the move.
		try {
			position.doMove(move);
		} catch (IllegalMoveException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		//increment depth.
		int newDepth = currentDepth + 1;
		nodeCount++;
		
		//do the cutoff tests.
		if(position.isTerminal()){
			int util = utility2(position, player, currentDepth);
			position.undoMove();
			return -1*util;  //-1 is multiplied as this is a loss for the player.
		}
		
		if(newDepth > maxDepth){
			int util = utility2(position, player, currentDepth);
			position.undoMove();
			// get appropriate utility based on whether the starting player is white or black.
			// this ensures that we can pick either color for the computer.
			if(player==0){
				return util;
			}else{
				return -1*util;
			}
		}
		
		//loop through moves to find one with maximum value.
		int max = Integer.MIN_VALUE;
		for(short nextMove:position.getAllMoves()){
			int minVal = minValue(nextMove, position, maxDepth, newDepth, player);
			if(minVal>max){
				max = minVal;
			}
		}
		
		position.undoMove();
		//return that move.
		return max;
	}
	

	
	
	
	public int utility(Position position, int player, int depth){
		//if the state is terminal, see if it's a mate or a draw.
		if(position.isTerminal()){
			if(position.isMate()){
				return Integer.MAX_VALUE;
			}else{
				return 0;
			}
		}else{
			//otherwise return a random integer between Integer.Min_Value and
			//Integer.Max_Value.
			int rand = new Random().nextInt(Integer.MAX_VALUE);
			if(new Random().nextDouble()<0.5){
				return rand;
			}else{
				return -1*rand;
			}
		}
	}
	
	
	public int utility2(Position position, int player, int depth){
		//same as utility for terminal
		if(position.isTerminal()){
			if(position.isMate()){
				return Integer.MAX_VALUE;
			}else{
				return 0;
			}
		}else{
			// to ensure we get the right value for the right player.
			if(player == 0 && position.getToPlay() == player){
				return position.getMaterial();
			}else{
				return -1*position.getMaterial();
			}
		}
	}
	
	
	
}
