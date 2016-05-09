package bot;

import java.io.FileWriter;
import java.io.IOException;

import field.Field;
import field.Shape;
import moves.MoveType;

public class StrategyProcessor {
        Shape thisShape;
        Shape nextShape;
        Field field;
        Result best;
        Result tempResult;
        int combo;
        double[] parameters;

        public StrategyProcessor(Shape currentShape, Shape nextShape, Field field, int combo, double[] parameters) {
                this.thisShape = currentShape;
                this.nextShape = nextShape;
                this.field = field;
                this.best = new Result();
                this.tempResult = new Result();
                this.combo = combo;
                this.parameters = parameters;
        }

        public void run() {
                searchSpace();
        }

        private void searchSpace() {

                field.erradicateShape(thisShape);
                Shape tempCurrentShape = thisShape.copyShape(field);

                // Drop until two above.
                int position = field.getMaxHeight();
                while(position+3 < 20){
                        tempCurrentShape.oneDown();
                        tempResult.addMove(MoveType.DOWN);
                        position++;
                }
                
                for (int i = 0; i < tempCurrentShape.getRotations(); i++) {
                        if (i != 0) {
                                tempCurrentShape.turnRight(i);

                                for (int j = 0; j < i; j++) {
                                        tempResult.addMove(MoveType.TURNRIGHT);
                                }
                        }

                        int moveLeftCount = 0;
                        while (field.canMoveLeft(tempCurrentShape)) {
                                if (moveLeftCount != 0) {
                                        tempCurrentShape.oneLeft();
                                        tempResult.addMove(MoveType.LEFT);
                                }
                                moveLeftCount++;
                        }

                        exploreRight(tempCurrentShape);

                        while (tempResult.lastMove() == MoveType.LEFT) {
                                tempCurrentShape.oneRight();
                                tempResult.removeLastMove();
                        }

                        while (tempResult.lastMove() == MoveType.TURNRIGHT) {
                                tempCurrentShape.turnLeft();
                                tempResult.removeLastMove();
                        }
                }

        }

        private void exploreRight(Shape currentShape) {

                int moveRightCount = 0;
                while (field.canMoveRight(currentShape)) {
                        if (currentShape.isOutOfBoundaries(field)) {
                                continue;
                        }

                        if (moveRightCount != 0) {
                                if (tempResult.lastMove() == MoveType.LEFT) {
                                        tempResult.removeLastMove();
                                } else {
                                        tempResult.addMove(MoveType.RIGHT);
                                }
                                currentShape.oneRight();
                        }

                        // Move all the way down.
                        while (field.canMoveDown(currentShape)) {
                                tempResult.addMove(MoveType.DOWN);
                                currentShape.oneDown();
                        }

                        
                        evaluateFinalPosition(currentShape);

                        /*
                         * CHECKING FOR FINAL 'Scooting' of the piece under others.
                         */
                        // CHECKING RIGHT
                        if(field.canMoveRight(currentShape)){
                                tempResult.addMove(MoveType.RIGHT);
                                currentShape.oneRight();
                                
                                evaluateFinalPosition(currentShape);
                                tempResult.removeLastMove();
                                currentShape.oneLeft();

                        }
//                        
                        // CHECKING LEFT
                        if(field.canMoveLeft(currentShape)){
                                tempResult.addMove(MoveType.LEFT);
                                currentShape.oneLeft();
                                evaluateFinalPosition(currentShape);
                                tempResult.removeLastMove();
                                currentShape.oneRight();
                        }
//                        if(tempResult.lastMove() == MoveType.LEFT){
//
//                        }
//
//                        while (tempResult.lastMove() == MoveType.DOWN) {
//                                currentShape.oneUp();
//                                tempResult.removeLastMove();
//                        }
                        // CHECK ROTATIONS
//                        for(int i = 0; i < currentShape.getRotations(); i++){
//                                currentShape.turnRight();
//                                tempResult.addMove(MoveType.TURNRIGHT);
//                        }
//                        evaluateFinalPosition(currentShape);
//                        while(tempResult.lastMove() == MoveType.TURNRIGHT){
//                                currentShape.turnLeft();
//                                tempResult.removeLastMove();
//                        }
                        

                        
                        while (tempResult.lastMove() == MoveType.DOWN) {
                                currentShape.oneUp();
                                tempResult.removeLastMove();
                        }

                        moveRightCount++;
                }

                while (tempResult.lastMove() == MoveType.RIGHT) {
                        currentShape.oneLeft();
                        tempResult.removeLastMove();
                        moveRightCount--;
                }
                // In the beginning we remove lefts to act has 'moveRight'. We kept track of the number
                // rights but forgot to re-add the left.
                for(int i = 0; i <moveRightCount; i++){
                        tempResult.addMove(MoveType.LEFT);
                        currentShape.oneLeft();
                }
        }

        private void evaluateFinalPosition(Shape currentShape) {

                Field tempField = field.copyField();

                tempField.setBlock(currentShape);

                tempResult.score = tempField.evaluateScore(currentShape, combo, parameters);

                /*
                 * ONE LOOKAHEAD
                 */
                if (nextShape != null) {
                        int removedLines  = tempField.removeCompletedLines();
                        StrategyProcessor lookaheadProcessor = new StrategyProcessor(nextShape, null, tempField, combo + removedLines, parameters);
                        lookaheadProcessor.run();
                        tempResult.score +=  lookaheadProcessor.getBest().score;
                }

                if (tempResult.getScore() > best.getScore()) {
                        best.setMoves(tempResult.moves);
                        best.setScore(tempResult.score);
                }
        }

        public Result getBest() {
                return best;
        }

//        public void write(String line) {
//                try {
//                        FileWriter writer = new FileWriter("starter_out.txt", true);
//                        writer.write(line);
//                        writer.write("\n");
//                        writer.close();
//                } catch (IOException e) {
//                        // TODO Auto-generated catch block
//                        e.printStackTrace();
//                }
//        }
}
