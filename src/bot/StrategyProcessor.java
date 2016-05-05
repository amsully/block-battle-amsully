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
                                tempCurrentShape.turnRight();
                                tempResult.addMove(MoveType.TURNRIGHT);
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
                                tempResult.addMove(MoveType.RIGHT);
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
                        for (int i = 0; i < numberDown; i++) {
                                currentShape.oneUp();
                                tempResult.removeLastMove();
                        }
                        moveRightCount++;
                }
        }

        private void evaluateFinalPosition(Shape currentShape) {
                // TODO Auto-generated method stub
                
                Field tempField = field.copyField();
                
                tempField.setBlock(currentShape);
                
                tempResult.score = tempField.evaluateScore();
                
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
