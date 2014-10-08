package chai;

import java.util.HashMap;
import java.util.Random;

import chesspresso.Chess;
import chesspresso.move.IllegalMoveException;
import chesspresso.position.Position;

public class MinimaxABAIT implements ChessAI {
	
	int nodeCount = 0;
	HashMap<Long, TranspositionNode> transpositionTable;
	boolean mated = false;
	public short getMove(Position position) {
		nodeCount = 0;
		 transpositionTable= new HashMap<Long, TranspositionNode>();
		String copyFEN = position.getFEN();
		Position copy = new Position(copyFEN);
		//int rand = new Random().nextInt(moves.length);
		short move = 0;
		int depth = 1;
		
		while(depth<5){
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
		System.out.println("The NodeCount is  " + nodeCount);
		return move;
	}
	
	public short miniMaxABMove(Position position, int maxDepth){
		short [] moves = position.getAllMoves();
		nodeCount ++;
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
			if(max>=beta){
				//add the value to the transposition table as an upper bound value.
				transpositionTable.put(position.getHashCode(), new TranspositionNode(maxDepth, 2, max));
				mated = true;
				return maxMove;
			}
			if(max>=alpha){
				alpha = minVal;
			}
		}
		//add the value to the transposition table as an exact value.
		transpositionTable.put(position.getHashCode(), new TranspositionNode(maxDepth, 0, max));
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
			//add to transposition table as exact value.
			transpositionTable.put(position.getHashCode(), new TranspositionNode(maxDepth-currentDepth, 0, util));
			position.undoMove();
			return util;
		}
		
		
		//check if in transposition table.
		if(transpositionTable.containsKey(position.getHashCode())){
			TranspositionNode current = transpositionTable.get(position.getHashCode());
			//if height is greater than the height we will be searching to
			if(current.getHeight()>=maxDepth-currentDepth){
				// if the node is an exact value node.
				if(current.getType()==0){
					//simply return that value.
					position.undoMove();
					return current.getValue();
				}else if(current.getType()==1){
					//if it is a lower bound node, check if it is less than alpha.
					if(current.getValue()<=alpha){
						//if so then return alpha.
						position.undoMove();
						return alpha;
					}
				}
			}
		}
		
		
		
		
		
		int min = Integer.MAX_VALUE;
		for(short nextMove:position.getAllMoves()){
			int maxVal = maxValue(nextMove, position, maxDepth, newDepth, player, alpha, beta);
			if(maxVal<min){
				min = maxVal;
			}
			if(min<=alpha){
				//add node as lower bound node.
				transpositionTable.put(position.getHashCode(), new TranspositionNode(maxDepth-currentDepth, 1, min));
				position.undoMove();
				return min;
			}
			if(min<beta){
				beta = min;
			}
		}
		
		//add node as exact node.
		transpositionTable.put(position.getHashCode(), new TranspositionNode(maxDepth-currentDepth, 0, min));
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
		int newDepth = currentDepth + 1;
		nodeCount++;
		
		if(position.isTerminal()){
			int util = utility2(position, player, currentDepth);
			//add node as exact node.
			transpositionTable.put(position.getHashCode(), new TranspositionNode(maxDepth-currentDepth, 0, util));
			position.undoMove();
			return -1*util;
		}
		
		if(newDepth > maxDepth){
			int util = utility2(position, player, currentDepth);
			
			if(player==0){
				transpositionTable.put(position.getHashCode(), new TranspositionNode(maxDepth-currentDepth, 0, util));
				position.undoMove();
				return util;
			}else{
				transpositionTable.put(position.getHashCode(), new TranspositionNode(maxDepth-currentDepth, 0, -1*util));
				position.undoMove();
				return -1*util;
			}
		}
		
		
		//similar to what was done in minValue().
		if(transpositionTable.containsKey(position.getHashCode())){
			TranspositionNode current = transpositionTable.get(position.getHashCode());
			if(current.getHeight()>=maxDepth-currentDepth){
				if(current.getType()==0){
					position.undoMove();
					return current.getValue();
					// if node is an upper bound node.
				}else if(current.getType()==2){
					//check if it is greater than beta.
					if(current.getValue()>=beta){
						//if so then return beta.
						position.undoMove();
						return beta;
					}
				}
			}
		}
		
		
		
		int max = Integer.MIN_VALUE;
		for(short nextMove:position.getAllMoves()){
			int minVal = minValue(nextMove, position, maxDepth, newDepth, player, alpha, beta);
			if(minVal>max){
				max = minVal;
			}
			if(max>=beta){
				//add node as upper bound node.
				transpositionTable.put(position.getHashCode(), new TranspositionNode(maxDepth-currentDepth, 2, max));
				position.undoMove();
				return max;
			}
			if(max>alpha){
				alpha = max;
			}
		}
		
		//add node as exact node.
		transpositionTable.put(position.getHashCode(), new TranspositionNode(maxDepth-currentDepth, 0, max));
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
				//System.out.println("You will be mated! " + depth);
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
