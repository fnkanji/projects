package chai;

import java.util.Random;

import chesspresso.Chess;
import chesspresso.move.IllegalMoveException;
import chesspresso.position.Position;

public class MinimaxABAIEV implements ChessAI {
	
	boolean mated = false;
	public int pawnMultiple = 100;
	public int knightMultiple = 320;
	public int bishopMultiple = 325;
	public int rookMultiple = 500;
	public int queenMultiple = 975;
	int nodeCount = 0;
	
	//We want to essentially encourage pawns to move forward.
	private static short[] pawnTable = new short[]
			{
	     0,  0,  0,  0,  0,  0,  0,  0,
	    50, 50, 50, 50, 50, 50, 50, 50,
	    10, 10, 20, 30, 30, 20, 10, 10,
	     5,  5, 10, 27, 27, 10,  5,  5,
	     0,  0,  0, 25, 25,  0,  0,  0,
	     5, -5,-10,  0,  0,-10, -5,  5,
	     5, 10, 10,-25,-25, 10, 10,  5,
	     0,  0,  0,  0,  0,  0,  0,  0
	};
	
	//knights are better towards the center, so push them there.
	private static short[] knightTable = new short[]
			{
			    -50,-40,-30,-30,-30,-30,-40,-50,
			    -40,-20,  0,  0,  0,  0,-20,-40,
			    -30,  0, 10, 15, 15, 10,  0,-30,
			    -30,  5, 15, 20, 20, 15,  5,-30,
			    -30,  0, 15, 20, 20, 15,  0,-30,
			    -30,  5, 10, 15, 15, 10,  5,-30,
			    -40,-20,  0,  5,  5,  0,-20,-40,
			    -50,-40,-20,-30,-30,-20,-40,-50,
			};
	
	//Again bishops are better in the center.
	private static short[] bishopTable = new short[]
			{
			    -20,-10,-10,-10,-10,-10,-10,-20,
			    -10,  0,  0,  0,  0,  0,  0,-10,
			    -10,  0,  5, 10, 10,  5,  0,-10,
			    -10,  5,  5, 10, 10,  5,  5,-10,
			    -10,  0, 10, 10, 10, 10,  0,-10,
			    -10, 10, 10, 10, 10, 10, 10,-10,
			    -10,  5,  0,  0,  0,  0,  5,-10,
			    -20,-10,-40,-10,-10,-40,-10,-20,
			};
	
	//King should stay back and let the other pieces defend him.
	private static short[] kingTable = new short[]
			{
			  -30, -40, -40, -50, -50, -40, -40, -30,
			  -30, -40, -40, -50, -50, -40, -40, -30,
			  -30, -40, -40, -50, -50, -40, -40, -30,
			  -30, -40, -40, -50, -50, -40, -40, -30,
			  -20, -30, -30, -40, -40, -30, -30, -20,
			  -10, -20, -20, -20, -20, -20, -20, -10, 
			   20,  20,   0,   0,   0,   0,  20,  20,
			   20,  30,  10,   0,   0,  10,  30,  20
			};
	
	
	public short getMove(Position position) {
		nodeCount = 0;
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
		System.out.println("The nodeCount is: " + nodeCount);
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
		for(short move:moves){
			int minVal = minValue(move, position, maxDepth, currentDepth, player, alpha, beta);
			if(minVal>max){
				max = minVal;
				maxMove = move;
			}
			if(max>=beta){
				mated = true;
				return maxMove;
			}
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
		nodeCount ++;
		int newDepth = currentDepth + 1;
		if(position.isTerminal() || newDepth>maxDepth ){
			int util = utility3(position, player, currentDepth);
			position.undoMove();
			return util;
		}
		
		int min = Integer.MAX_VALUE;
		for(short nextMove:position.getAllMoves()){
			int maxVal = maxValue(nextMove, position, maxDepth, newDepth, player, alpha, beta);
			if(maxVal<min){
				min = maxVal;
			}
			if(min<=alpha){
				position.undoMove();
				return min;
			}
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
		int newDepth = currentDepth + 1;
		nodeCount ++;
		if(position.isTerminal()){
			int util = utility3(position, player, currentDepth);
			position.undoMove();
			return -1*util;
		}
		
		if(newDepth > maxDepth){
			int util = utility3(position, player, currentDepth);
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
			if(max>=beta){
				position.undoMove();
				return max;
			}
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
	
	
	public int getMaterial2 (Position position){
		int player = position.getToPlay();
		int score = 0;
		int piece = 0;
		for (int i = 0; i<64; i++){
			piece = position.getStone(i);
			if(piece == Chess.WHITE_PAWN){
				score += pawnMultiple;
				score += pawnTable[i];
			}else if(piece == Chess.WHITE_BISHOP){
				score += bishopMultiple;
				score += bishopTable[i];
			}else if(piece == Chess.WHITE_KNIGHT){
				score += knightMultiple;
				score += knightTable[i];
			}else if(piece == Chess.WHITE_QUEEN){
				score += queenMultiple;
			}else if(piece == Chess.WHITE_ROOK){
				score += rookMultiple;
			}else if(piece == Chess.WHITE_KING){
				score += kingTable[i];
			}else if(piece == Chess.BLACK_PAWN){
				score -= pawnMultiple;
				score -= pawnTable[63-i];
			}else if(piece == Chess.BLACK_BISHOP){
				score -= bishopMultiple;
				score -= bishopTable[63-i];
			}else if(piece == Chess.BLACK_KNIGHT){
				score -= knightMultiple;
				score -= knightTable[63-i];
			}else if(piece == Chess.BLACK_QUEEN){
				score -= queenMultiple;
			}else if(piece == Chess.BLACK_ROOK){
				score -= rookMultiple;
			}else if(piece == Chess.BLACK_KING){
				score -= kingTable[63-i];
			}
		}
		int mobility = position.getAllMoves().length;
		position.toggleToPlay();
		mobility = mobility - position.getAllMoves().length;
		position.toggleToPlay();
		if(player == 0){
			return score + 10*mobility;
		}else{
			return (-1*score) + 10*mobility;
		}
		
	}
	
	
	
	
	public int getMaterial (Position position){
		int player = position.getToPlay();
		int score = 0;
		int piece = 0;
		for (int i = 0; i<64; i++){
			piece = position.getStone(i);
			if(piece == Chess.WHITE_PAWN){
				//add the pawn weight.
				score += pawnMultiple;
				score += pawnTable[i]; //add the piece table value.
				
				//Doubled Pawn.
				for (int j = i; j<64; j=j+8){
					if(j==i){
						continue;
					}
					if (position.getStone(j)==Chess.WHITE_PAWN){
						score -= 50;
						break;
					}
				}
				
				// Isolated Pawns.
				if(i%8==0){
					int pawn = 0;
					for(int j = i+1; j<64; j=j+8){
						if(position.getStone(j)==Chess.WHITE_PAWN){
							pawn++;
							break;
						}
					}
					if(pawn<1)
						score -=50;
				}else if(i%8 == 7){
					int pawn = 0;
					for(int j = i-1; j<64; j=j+8){
						if(position.getStone(j)==Chess.WHITE_PAWN){
							pawn++;
							break;
						}
					}
					if(pawn<1)
						score -=50;
				}else{
					int pawn = 0;
					for(int j = i-1; j<64; j=j+8){
						if(position.getStone(j)==Chess.WHITE_PAWN){
							pawn++;
							break;
						}
					}
					
					for(int j = i+1; j<64; j=j+8){
						if(position.getStone(j)==Chess.WHITE_PAWN){
							pawn++;
							break;
						}
					}
					if(pawn<1)
						score-=50;
				}
				
				
				
				//Passed Pawn.
				int pawn = 0;
				for(int j = i; j<64; j = j+8){
					if(position.getStone(j)==Chess.BLACK_PAWN){
						pawn++;
						break;
					}
				}
				if(pawn<1){
					score+=50;
				}
				
			}else if(piece == Chess.WHITE_BISHOP){
				score += bishopMultiple;
				score += bishopTable[i];
			}else if(piece == Chess.WHITE_KNIGHT){
				score += knightMultiple;
				score += knightTable[i];
			}else if(piece == Chess.WHITE_QUEEN){
				score += queenMultiple;
			}else if(piece == Chess.WHITE_ROOK){
				score += rookMultiple;
			}else if(piece == Chess.WHITE_KING){
				score += kingTable[i];
			}else if(piece == Chess.BLACK_PAWN){
				score -= pawnMultiple;
				score -= pawnTable[63-i];
				
				//Doubled Pawn.
				for (int j = i; j>0; j=j-8){
					if(j==i){
						continue;
					}
					if (position.getStone(j)==Chess.BLACK_PAWN){
						score += 50;
						break;
					}
				}
				
				// Isolated Pawns.
				if(i%8==0){
					int pawn = 0;
					for(int j = i+1; j>0; j=j-8){
						if(position.getStone(j)==Chess.BLACK_PAWN){
							pawn++;
							break;
						}
					}
					if(pawn<1)
						score +=50;
				}else if(i%8 == 7){
					int pawn = 0;
					for(int j = i-1; j>0; j=j-8){
						if(position.getStone(j)==Chess.BLACK_PAWN){
							pawn++;
							break;
						}
					}
					if(pawn<1)
						score +=50;
				}else{
					int pawn = 0;
					for(int j = i-1; j>0; j=j-8){
						if(position.getStone(j)==Chess.BLACK_PAWN){
							pawn++;
							break;
						}
					}
					
					for(int j = i+1; j>0; j=j-8){
						if(position.getStone(j)==Chess.BLACK_PAWN){
							pawn++;
							break;
						}
					}
					if(pawn<1)
						score+=50;
				}
				
				
				//Passed Pawn.
				int pawn = 0;
				for(int j = i; j>0; j = j-8){
					if(position.getStone(j)==Chess.WHITE_PAWN){
						pawn++;
						break;
					}
				}
				if(pawn<1){
					score-=50;
				}
				
				
			}else if(piece == Chess.BLACK_BISHOP){
				score -= bishopMultiple;
				score -= bishopTable[63-i];
			}else if(piece == Chess.BLACK_KNIGHT){
				score -= knightMultiple;
				score -= knightTable[63-i];
			}else if(piece == Chess.BLACK_QUEEN){
				score -= queenMultiple;
			}else if(piece == Chess.BLACK_ROOK){
				score -= rookMultiple;
			}else if(piece == Chess.BLACK_KING){
				score -= kingTable[63-i];
			}
		}
		//get Mobility
		int mobility = position.getAllMoves().length;
		position.toggleToPlay();
		mobility = mobility - position.getAllMoves().length;
		position.toggleToPlay();
		if(player == 0){
			return score + 10*mobility;
		}else{
			return (-1*score) + 10*mobility;
		}
		
	}
	
//	public int staticEvaluation(Position position, int player, int depth){
//		int score = 0;
//		if(position.getToPlay()==1){
//			for(int i = 0; i<64; i++){
//				position.getPiece(i);
//			}
//			
//		}
//	}
	
	
	public int utility3(Position position, int player, int depth){
		if(position.isTerminal()){
			if(position.isMate()){
				return Integer.MAX_VALUE;
			}else{
				return 0;
			}
		}else{
			if(player == 0 && position.getToPlay() == player){
				return getMaterial(position);
			}else{
				return -1*getMaterial(position);
			}
		}
	}
	
	
	
}
