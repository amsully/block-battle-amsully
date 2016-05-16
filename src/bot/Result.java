package bot;

import java.util.ArrayList;

import moves.MoveType;

public class Result {

        double score;

        int initialDown;
        int rotationsRight;
        int left; 
        int right;
        int down;
        int floor_right;
        int floor_left;
        int floor_rotationsRight;
        int floor_rotationsLeft;
        
        public Result() {
                score = -Double.MAX_VALUE;
                initialDown = 0;
                rotationsRight = 0;
                left = 0; 
                right = 0; 
                down = 0;
                floor_right = 0;
                floor_left = 0;
                floor_rotationsRight = 0;
                floor_rotationsLeft = 0;
                
        }
        
        public void resetScore(){
                this.score = -Double.MAX_VALUE;
        }
        
        
        public int getDown() {
                return down;
        }


        public void setDown(int down) {
                this.down = down;
        }


        public int getInitialDown() {
                return initialDown;
        }



        public void setInitialDown(int initialDown) {
                this.initialDown = initialDown;
        }
        
        public int getRotationsRight() {
                return rotationsRight;
        }



        public void setRotationsRight(int rotationsRight) {
                this.rotationsRight = rotationsRight;
        }



        public int getLeft() {
                return left;
        }



        public void setLeft(int left) {
                this.left = left;
        }



        public int getRight() {
                return right;
        }



        public void setRight(int right) {
                this.right = right;
        }



        public int getFloor_right() {
                return floor_right;
        }



        public void setFloor_right(int floor_right) {
                this.floor_right = floor_right;
        }



        public int getFloor_left() {
                return floor_left;
        }



        public void setFloor_left(int floor_left) {
                this.floor_left = floor_left;
        }



        public int getFloor_rotationsRight() {
                return floor_rotationsRight;
        }



        public void setFloor_rotationsRight(int floor_rotationsRight) {
                this.floor_rotationsRight = floor_rotationsRight;
        }



        public int getFloor_rotationsLeft() {
                return floor_rotationsLeft;
        }



        public void setFloor_rotationsLeft(int floor_rotationsLeft) {
                this.floor_rotationsLeft = floor_rotationsLeft;
        }

        public void setScore(double score) {
                this.score = new Double(score);
        }

        public double getScore() {
                return score;
        }
}
