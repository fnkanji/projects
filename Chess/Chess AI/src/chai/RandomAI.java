package chai;

import java.util.Random;

import chesspresso.position.Position;

public class RandomAI implements ChessAI {
	public short getMove(Position position) {
		short [] moves = position.getAllMoves();
		int rand = new Random().nextInt(moves.length);
		short move = moves[rand];
		return move;
	}
}
