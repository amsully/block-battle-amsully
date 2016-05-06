package bot;

import java.io.FileWriter;
import java.io.IOException;

import field.Field;
import field.Shape;
import moves.MoveType;

public class StrategyProcessor {
        Shape currentShape;
        Shape nextShape;
        Field field;
        Result best;
        Result tempResult;

        public StrategyProcessor(Shape currentShape, Shape nextShape, Field field) {
                this.currentShape = currentShape;
                this.nextShape = nextShape;
                this.field = field;
                this.best = new Result();
                this.tempResult = new Result();
        }

        public void run() {
                // TODO Auto-generated method stub
                searchSpace();
        }

        private void searchSpace() {
                // TODO Auto-generated method stub

                Shape tempCurrentShape = currentShape.copyShape(field);

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

//                        for (int j = 0; j < moveLeftCount - 1; j++) {
//                                tempCurrentShape.oneRight();
//                                tempResult.removeLastMove();
//                        }
                        while(tempResult.lastMove() == MoveType.LEFT){
                                tempCurrentShape.oneRight();
                                tempResult.removeLastMove();
                        }
                        
                        // Turn the shape back and remove moves.
//                        if (i != 0) {
//                                tempCurrentShape.turnLeft(i);
//
//                                for (int j = 0; j < i; j++) {
//                                        tempResult.removeLastMove();
//                                }
//                        }
                        while(tempResult.lastMove() == MoveType.TURNRIGHT){
                                tempCurrentShape.turnLeft();
                                tempResult.removeLastMove();
                        }
                }

        }

        private void exploreRight(Shape currentShape) {
                // TODO Auto-generated method stub
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
//                                tempResult.addMove(MoveType.RIGHT);
                                currentShape.oneRight();
                        }

                        // Move all the way down.
                        int numberDown = 0;
                        while (field.canMoveDown(currentShape)) {
                                tempResult.addMove(MoveType.DOWN);
                                currentShape.oneDown();
                                numberDown++;
                        }
                        
                        evaluateFinalPosition(currentShape);
                        // Return shape back to top.
//                        for (int i = 0; i < numberDown; i++) {
//                                currentShape.oneUp();
//                                tempResult.removeLastMove();
//                        }
                        
                        while(tempResult.lastMove() == MoveType.DOWN){
                                currentShape.oneUp();
                                tempResult.removeLastMove();
                        }

                        moveRightCount++;
                }

                // Remove all previous right moves.
//                for (int i = 0; i < moveRightCount - 1; i++) {
//                        currentShape.oneLeft();
//                        tempResult.removeLastMove();
//                }
                while(tempResult.lastMove() == MoveType.RIGHT){
                        currentShape.oneLeft();
                        tempResult.removeLastMove();
                }
        }

        private void evaluateFinalPosition(Shape currentShape) {
                // TODO Auto-generated method stub

                Field tempField = field.copyField();

                tempField.setBlock(currentShape);

                tempResult.score = tempField.evaluateScore(currentShape);

                /*
                 * ONE LOOKAHEAD
                 */
                if (nextShape != null) {
                        // tempField.removeCompletedLines();
                        StrategyProcessor lookaheadProcessor = new StrategyProcessor(nextShape, null, tempField);
                        lookaheadProcessor.run();
                        tempResult.score += .5 * lookaheadProcessor.getBest().score;
                }

                if (tempResult.getScore() > best.getScore()) {
                        best.setMoves(tempResult.moves);
                        best.setScore(tempResult.score);
                }
        }

        public Result getBest() {
                return best;
        }

        public void write(String line) {
                try {
                        FileWriter writer = new FileWriter("starter_out.txt", true);
                        writer.write(line);
                        writer.write("\n");
                        writer.close();
                } catch (IOException e) {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                }
        }
}
