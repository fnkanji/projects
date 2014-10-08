package chai;

import chesspresso.Chess;
import chesspresso.move.IllegalMoveException;
import chesspresso.move.Move;
import chesspresso.position.Position;

public class ChessGame {

	public Position position;

	public int rows = 8;
	public int columns = 8;

	public ChessGame() {
		position = new Position(
				//"rnbqkbnr/pppppppp/8/8/8/8/PPPPPPPP/RNBQKBNR w KQkq - 0 1");
				//"4K3/8/8/8/8/R6R/8/4k3 w - - 0 1");
				//"4k3/8/pppppppp/4b3/4Q3/8/8/4K3 w - - 0 1");
				"8/Q7/8/2p2p2/1k1nq3/1P6/5P2/4KB2 w - - 0 1");
				//"r1bk1bnr/pp2p1p1/2p3Bp/3p4/5p2/N1P1PN1q/PP1P1PPP/R1BQK2R w KQ - 5 10");

	}

	public int getStone(int col, int row) {
		return position.getStone(Chess.coorToSqi(col, row));
	}
	
	public boolean squareOccupied(int sqi) {
		return position.getStone(sqi) != 0;
		
	}

	public boolean legalMove(short move) {
		
		for(short m: position.getAllMoves()) {
			if(m == move) return true;
		}
		System.out.println(java.util.Arrays.toString(position.getAllMoves()));
		System.out.println(move);
		return false;
	
	}

	// find a move from the list of legal moves from fromSqi to toSqi
	// return 0 if none available
	public short findMove(int fromSqi, int toSqi) {
		
		for(short move: position.getAllMoves()) {
			if(Move.getFromSqi(move) == fromSqi && 
					Move.getToSqi(move) == toSqi) return move;
		}
		return 0;
	}
	
	public void doMove(short move) {
		try {

			System.out.println("making move " + move);

			position.doMove(move);
			System.out.println(position);
		} catch (IllegalMoveException e) {
			System.out.println("illegal move!");
		}
	}

	public static void main(String[] args) {
		System.out.println();

		// Create a starting position using "Forsyth–Edwards Notation". (See
		// Wikipedia.)
		Position position = new Position(
				"rnbqkbnr/pppppppp/8/8/8/8/PPPPPPPP/RNBQKBNR w KQkq - 0 1");

		System.out.println(position);

	}
	
	

}
