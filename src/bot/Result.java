package bot;

import java.util.ArrayList;

import moves.MoveType;

public class Result {

        ArrayList<MoveType> moves;
        double score;

        public Result() {
        }

        public void setMoves(ArrayList<MoveType> moves) {
                this.moves = new ArrayList<MoveType>(moves);
        }

        public void setScore(double score) {
                this.score = score;
        }

        public double getScore() {
                return score;
        }

        public void reset() {
                // TODO Auto-generated method stub
                score = -Double.MIN_VALUE;
                moves.clear();
        }

        public void addMove(MoveType move) {
                // TODO Auto-generated method stub
                moves.add(move);
        }

        public void removeLastMove() {
                // TODO Auto-generated method stub
                moves.remove(moves.size()-1);
        }

}
