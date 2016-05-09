package bot;

import java.util.ArrayList;

import moves.MoveType;

public class Result {

        ArrayList<MoveType> moves;
        double score;

        int rotationsRight;
        int left; 
        int right;
        int floor_right;
        int floor_left;
        int floor_rotationsRight;
        int floor_rotationsLeft;
        
        public Result() {
                moves = new ArrayList<MoveType>();
                score = -Double.MAX_VALUE;

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

        public MoveType lastMove() {
                // TODO Auto-generated method stub
                if(moves.isEmpty()) return null;
                
                return moves.get(moves.size()-1);
        }

}
