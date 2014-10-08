package chai;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.PriorityQueue;
import java.util.Random;

import chesspresso.Chess;
import chesspresso.move.IllegalMoveException;
import chesspresso.position.Position;

public class MinimaxABAITMR implements ChessAI {
	
	public int nodeCount = 0;
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
		
		while(depth<7){
			//System.out.println("Checking at depth " + depth);
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
		System.out.println("The node Count is:  " + nodeCount);
		return move;
	}
	
	
	
	
	public short miniMaxABMove(Position position, int maxDepth){
		nodeCount ++;
		short [] moves = position.getAllMoves();
		int alpha = Integer.MIN_VALUE;
		int beta = Integer.MAX_VALUE;
		int max = Integer.MIN_VALUE;
		short maxMove = 0;
		int currentDepth = 1;
		int player = position.getToPlay();

		PriorityQueue<MoveNode> q = new PriorityQueue<MoveNode>();
		LinkedList<Short> ordered = new LinkedList<Short>();
		
		for(short move:moves){
			try {
				position.doMove(move);
			} catch (IllegalMoveException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			if(transpositionTable.containsKey(position.getHashCode())){
				int value = transpositionTable.get(position.getHashCode()).getValue();
				
				MoveNode the = new MoveNode(move, value);
				q.add(the);
			}
			position.undoMove();
		}
		while(!q.isEmpty()){
			short move = q.remove().move;
			ordered.add(move);
		}
		
		for(short move:moves){
			if(!ordered.contains(move)){
				ordered.add(move);
			}
		}
		
		for(short move:ordered){
			int minVal = minValue(move, position, maxDepth, currentDepth, player, alpha, beta);
			if(minVal>max){
				max = minVal;
				maxMove = move;
			}
			if(max>=beta){
				transpositionTable.put(position.getHashCode(), new TranspositionNode(maxDepth, 2, max));
				mated = true;
				return maxMove;
			}
			if(max>=alpha){
				alpha = minVal;
			}
		}
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
			transpositionTable.put(position.getHashCode(), new TranspositionNode(maxDepth-currentDepth, 0, util));
			position.undoMove();
			return util;
		}
		
		
		if(transpositionTable.containsKey(position.getHashCode())){
			TranspositionNode current = transpositionTable.get(position.getHashCode());
			if(current.getHeight()>=maxDepth-currentDepth){
				if(current.getType()==0){
					position.undoMove();
					return current.getValue();
				}else if(current.getType()==1){
					if(current.getValue()<=alpha){
						position.undoMove();
						return alpha;
					}
				}
			}
		}
		
		
		if(!position.isCheck()){
			position.toggleToPlay();
			if(position.isLegal()){
				int nullVal = maxValue2(position, maxDepth - 2, newDepth, player, alpha, beta);
				if(nullVal<=alpha){
					position.toggleToPlay();
					position.undoMove();
					//System.out.println("Nulled!");
					return alpha;
				}
			}
			position.toggleToPlay();
		}
		
		short [] moves = position.getAllMoves();
		
		PriorityQueue<MoveNode> q = new PriorityQueue<MoveNode>();
		LinkedList<Short> ordered = new LinkedList<Short>();
		for(short theMove:moves){
			try {
				position.doMove(theMove);
			} catch (IllegalMoveException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
			if(transpositionTable.containsKey(position.getHashCode())){
				q.add(new MoveNode(theMove, transpositionTable.get(position.getHashCode()).getValue()));
			}
			position.undoMove();
		}
		
		while(!q.isEmpty()){
			short theMove = q.remove().move;
			ordered.addFirst(theMove);
		}
		
		for(short theMove:moves){
			if(!ordered.contains(theMove)){
				ordered.add(theMove);
			}
		}
		
		
		
		int min = Integer.MAX_VALUE;
		for(short nextMove:ordered){
			int maxVal = maxValue(nextMove, position, maxDepth, newDepth, player, alpha, beta);
			if(maxVal<min){
				min = maxVal;
			}
			if(min<=alpha){
				transpositionTable.put(position.getHashCode(), new TranspositionNode(maxDepth-currentDepth, 1, min));
				position.undoMove();
				return min;
			}
			if(min<beta){
				beta = min;
			}
		}
		
		transpositionTable.put(position.getHashCode(), new TranspositionNode(maxDepth-currentDepth, 0, min));
		position.undoMove();
		return min;
	}
	
	
	public int minValue2(Position position, int maxDepth, int currentDepth, int player, int alpha, int beta){
		int newDepth = currentDepth + 1;
		nodeCount++;
		if(position.isTerminal() || newDepth>maxDepth ){
			int util = utility2(position, player, currentDepth);
			return util;
		}
		
		if(!position.isCheck()){
			position.toggleToPlay();
			if(position.isLegal()){
				int nullVal = maxValue2(position, maxDepth - 2, newDepth, player, alpha, beta);
				if(nullVal<=alpha){
					position.toggleToPlay();
					//System.out.println("Nulled!");
					return alpha;
				}
			}
			position.toggleToPlay();
		}
		
		int min = Integer.MAX_VALUE;
		for(short nextMove:position.getAllMoves()){
			int maxVal = maxValue(nextMove, position, maxDepth, newDepth, player, alpha, beta);
			if(maxVal<min){
				min = maxVal;
			}
			if(min<=alpha){
				return min;
			}
			if(min<beta){
				beta = min;
			}
		}
		
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
		
		
		if(transpositionTable.containsKey(position.getHashCode())){
			TranspositionNode current = transpositionTable.get(position.getHashCode());
			if(current.getHeight()>=maxDepth-currentDepth){
				if(current.getType()==0){
					position.undoMove();
					return current.getValue();
				}else if(current.getType()==2){
					if(current.getValue()>=beta){
						position.undoMove();
						return beta;
					}
				}
			}
		}
		
		if(!position.isCheck()){
			position.toggleToPlay();
			if(position.isLegal()){
				int nullVal = minValue2(position, maxDepth - 2, newDepth, player, alpha, beta);
				if(nullVal>=beta){
					position.toggleToPlay();
					position.undoMove();
					//System.out.println("Nulled!");
					return beta;
				}
			}
			position.toggleToPlay();
		}
		
		short [] moves = position.getAllMoves();
		
		
		PriorityQueue<MoveNode> q = new PriorityQueue<MoveNode>();
		LinkedList<Short> ordered = new LinkedList<Short>();
		for(short theMove:moves){
			try {
				position.doMove(theMove);
			} catch (IllegalMoveException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
			if(transpositionTable.containsKey(position.getHashCode())){
				q.add(new MoveNode(theMove, transpositionTable.get(position.getHashCode()).getValue()));
			}
			position.undoMove();
		}
		
		while(!q.isEmpty()){
			short theMove = q.remove().move;
			ordered.add(theMove);
		}
		
		for(short theMove:moves){
			if(!ordered.contains(theMove)){
				ordered.add(theMove);
			}
		}
		
		
		int max = Integer.MIN_VALUE;
		for(short nextMove:ordered){
			int minVal = minValue(nextMove, position, maxDepth, newDepth, player, alpha, beta);
			if(minVal>max){
				max = minVal;
			}
			if(max>=beta){
				transpositionTable.put(position.getHashCode(), new TranspositionNode(maxDepth-currentDepth, 2, max));
				position.undoMove();
				return max;
			}
			if(max>alpha){
				alpha = max;
			}
		}
		
		transpositionTable.put(position.getHashCode(), new TranspositionNode(maxDepth-currentDepth, 0, max));
		position.undoMove();
		return max;
	}
	
	
	public int maxValue2(Position position, int maxDepth, int currentDepth, int player, int alpha, int beta){
		int newDepth = currentDepth + 1;
		nodeCount++;
		if(position.isTerminal()){
			int util = utility2(position, player, currentDepth);
			return -1*util;
		}
		
		if(newDepth > maxDepth){
			int util = utility2(position, player, currentDepth);
			if(player==0){
				return util;
			}else{
				return -1*util;
			}
		}
		
		if(!position.isCheck()){
			position.toggleToPlay();
			if(position.isLegal()){
				int nullVal = minValue2(position, maxDepth - 2, newDepth, player, alpha, beta);
				if(nullVal>=beta){
					position.toggleToPlay();
					//System.out.println("Nulled!");
					return beta;
				}
			}
			position.toggleToPlay();
		}
		
		int max = Integer.MIN_VALUE;
		for(short nextMove:position.getAllMoves()){
			int minVal = minValue(nextMove, position, maxDepth, newDepth, player, alpha, beta);
			if(minVal>max){
				max = minVal;
			}
			if(max>=beta){
				return max;
			}
			if(max>alpha){
				alpha = max;
			}
		}
		
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
