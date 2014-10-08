package chai;

import java.util.Random;

import chesspresso.Chess;
import chesspresso.move.IllegalMoveException;
import chesspresso.position.Position;

public class MinimaxABAI implements ChessAI {
	
	boolean mated = false;
	
	int nodeCount = 0;
	public short getMove(Position position) {
		nodeCount = 0;
		String copyFEN = position.getFEN();
		Position copy = new Position(copyFEN);
		//int rand = new Random().nextInt(moves.length);
		short move = 0;
		int depth = 1;
		while(depth<7){
			move = miniMaxABMove(copy, depth);
			if(move==0){
				break;
			}
			try {
				copy.doMove(move);
			} catch (IllegalMoveException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
			if(mated){
				mated = false;
				System.out.println("CheckMate coming up! at depth: " + depth);
				break;
			}
			
			copy.undoMove();
			depth++;
		}
		System.out.println("The node count is: " + nodeCount);
		return move;
	}
	
	
	public short miniMaxABMove(Position position, int maxDepth){
		nodeCount++;
		short [] moves = position.getAllMoves();
		//set initial upper and lower bounds.
		int alpha = Integer.MIN_VALUE;
		int beta = Integer.MAX_VALUE;
		int max = Integer.MIN_VALUE;
		short maxMove = 0;
		int currentDepth = 1;
		int player = position.getToPlay();
		for(short move:moves){
			int minVal = minValue(move, position, maxDepth, currentDepth, player, alpha, beta);
			if(minVal>max){
				max = minVal;
				maxMove = move;
			}
			//check mate move.
			if(max>=beta){
				mated = true;
				return maxMove;
			}
			//update alpha.
			if(max>=alpha){
				alpha = minVal;
			}
		}
		return maxMove;
	}
	
	public int minValue(short move, Position position, int maxDepth, int currentDepth, int player, int alpha, int beta){
		try {
			position.doMove(move);
		} catch (IllegalMoveException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		int newDepth = currentDepth + 1;
		nodeCount++;
		if(position.isTerminal() || newDepth>maxDepth ){
			int util = utility2(position, player, currentDepth);
			position.undoMove();
			return util;
		}
		
		
		int min = Integer.MAX_VALUE;
		for(short nextMove:position.getAllMoves()){
			int maxVal = maxValue(nextMove, position, maxDepth, newDepth, player, alpha, beta);
			if(maxVal<min){
				min = maxVal;
			}
			
			//if value is worse than alpha, prune off everything else.
			if(min<=alpha){
				position.undoMove();
				return min;
			}
			//update beta.
			if(min<beta){
				beta = min;
			}
		}
		
		position.undoMove();
		return min;
	}
	
	
	public int maxValue(short move, Position position, int maxDepth, int currentDepth, int player, int alpha, int beta){
		try {
			position.doMove(move);
		} catch (IllegalMoveException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		nodeCount++;
		int newDepth = currentDepth + 1;
		if(position.isTerminal()){
			int util = utility2(position, player, currentDepth);
			position.undoMove();
			return -1*util;
		}
		
		if(newDepth > maxDepth){
			int util = utility2(position, player, currentDepth);
			position.undoMove();
			if(player==0){
				return util;
			}else{
				return -1*util;
			}
		}
		
		int max = Integer.MIN_VALUE;
		for(short nextMove:position.getAllMoves()){
			int minVal = minValue(nextMove, position, maxDepth, newDepth, player, alpha, beta);
			if(minVal>max){
				max = minVal;
			}
			
			//if value is worse than (greater than) beta, prune off everything else.
			if(max>=beta){
				position.undoMove();
				return max;
			}
			//update alpha.
			if(max>alpha){
				alpha = max;
			}
		}
		
		position.undoMove();
		return max;
	}
	

	
	
	
	public int utility(Position position, int player){
		if(position.isTerminal()){
			if(position.isMate()){
				return Integer.MAX_VALUE;
			}else{
				return 0;
			}
		}else{
			int rand = new Random().nextInt(Integer.MAX_VALUE);
			if(new Random().nextDouble()<0.5){
				return rand;
			}else{
				return -1*rand;
			}
		}
	}
	
	
	public int utility2(Position position, int player, int depth){
		if(position.isTerminal()){
			if(position.isMate()){
				return Integer.MAX_VALUE;
			}else{
				return 0;
			}
		}else{
			if(player == 0 && position.getToPlay() == player){
				return position.getMaterial();
			}else{
				return -1*position.getMaterial();
			}
		}
	}
	
	
	
}
