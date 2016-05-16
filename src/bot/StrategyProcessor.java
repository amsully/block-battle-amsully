package bot;

import java.awt.geom.CubicCurve2D;
import java.io.FileWriter;
import java.io.IOException;

import field.Cell;
import field.Field;
import field.Shape;
import field.ShapeType;
import moves.MoveType;

public class StrategyProcessor {
        Shape thisShape;
        Shape nextShape;
        Field field;
        Result best;

        int rotations;
        int left;
        int down;
        int leftRotations;
        int rightRotations;

        int combo;
        double[] parameters;

        public StrategyProcessor(Shape currentShape, Shape nextShape, Field field, int combo, double[] parameters) {
                this.thisShape = currentShape;
                this.nextShape = nextShape;
                this.field = field;
                this.best = new Result();

                this.rotations = 0;
                this.left = 0;
                this.down = 0;
                this.leftRotations = 0;
                this.rightRotations = 0;

                this.combo = combo;
                this.parameters = parameters;
        }

        public void run() {
                searchSpace();
        }

        private void searchSpace() {
                field.erradicateShape(thisShape);

                // Drop until two above.
                // int position = field.getMaxHeight();
                // int initDrops = 0;
                //
                // while (position + 3 < 20) {
                // initDrops++;
                //
                // tempCurrentShape.oneDown();
                //
                // position++;
                // }

                // this.tempResult.setInitialDown(initDrops);

                // for (int i = 0; i < tempCurrentShape.getRotations(); i++) {
                for (int i = 0; i < 4; i++) {

                        rotations = i;

                        if (i != 0) {
                                thisShape.turnRight();
                        }

                        Shape dummyShape = thisShape.copyShape();
                        beginExplorationLeft(dummyShape);
                }
        }

        private void beginExplorationLeft(Shape dummyShape) {
                // TODO Auto-generated method stub
                left = 0;
                // On zero will not move left.
                while (field.isValidPiece(dummyShape)) {
                        dummyShape.oneLeft();
                        left++;
                }
                left--;
                dummyShape.oneRight(); // Correct error

                exploreRight(dummyShape);
        }

        private void exploreRight(Shape leftShape) {

                // On zero it will not move right.
                while (field.isValidPiece(leftShape)) {
                        // Move all the way down.

                        Shape dummyShape = leftShape.copyShape();

                        placeOnBottom(dummyShape);

                        /*
                         * CHECKING FOR FINAL 'Scooting' of the piece under
                         * others.
                         */
                        // CHECKING RIGHT
                        // if(field.canMoveRight(currentShape)){
                        // tempResult.addMove(MoveType.RIGHT);
                        // currentShape.oneRight();
                        //
                        // evaluateFinalPosition(currentShape);
                        // tempResult.removeLastMove();
                        // currentShape.oneLeft();
                        //
                        // }
                        //
                        // CHECKING LEFT
                        // if(field.canMoveLeft(currentShape)){
                        // tempResult.addMove(MoveType.LEFT);
                        // currentShape.oneLeft();
                        // evaluateFinalPosition(currentShape);
                        // tempResult.removeLastMove();
                        // currentShape.oneRight();
                        // }
                        // if(tempResult.lastMove() == MoveType.LEFT){
                        //
                        // }
                        //
                        // while (tempResult.lastMove() == MoveType.DOWN) {
                        // currentShape.oneUp();
                        // tempResult.removeLastMove();
                        // }
                        // CHECK ROTATIONS
                        // for(int i = 0; i < currentShape.getRotations(); i++){
                        // currentShape.turnRight();
                        // tempResult.addMove(MoveType.TURNRIGHT);
                        // }
                        // evaluateFinalPosition(currentShape);
                        // while(tempResult.lastMove() == MoveType.TURNRIGHT){
                        // currentShape.turnLeft();
                        // tempResult.removeLastMove();
                        // }

                        leftShape.oneRight();
                        left--;
                }
        }

        private void placeOnBottom(Shape dummyShape) {
                // TODO Auto-generated method stub
                while (field.isValidPiece(dummyShape)) {
                        dummyShape.oneDown();
                        down++;
                }
                down--;
                dummyShape.oneUp(); // Correct overshoot.

                evaluateFinalPosition(dummyShape);

                if (dummyShape.getType() == ShapeType.T) {
                        evaluateRotateLeft(dummyShape.copyShape());
                        evaluateRotateRight(dummyShape.copyShape());
                }

                down = 0;
        }

        private void evaluateRotateRight(Shape copyShape) {
                // TODO Auto-generated method stub
                copyShape.turnRight();
                copyShape.turnRight();
                if (field.isValidPiece(copyShape)) {
                        rightRotations = 2;
                        evaluateFinalPosition(copyShape);
                        rightRotations = 0;
                }
        }

        private void evaluateRotateLeft(Shape copyShape) {
                // TODO Auto-generated method stub
                copyShape.turnLeft();
                copyShape.turnLeft();
                if (field.isValidPiece(copyShape)) {
                        leftRotations = 2;
                        evaluateFinalPosition(copyShape);
                        leftRotations = 0;
                }
        }

        private void evaluateFinalPosition(Shape currentShape) {
                Field tempField = field.copyField();

                tempField.setBlock(currentShape);

                double score;
                if (leftRotations == 2 || rightRotations == 2) {
                        score = tempField.evaluateScore(currentShape, combo*10, parameters);
                } else {
                        score = tempField.evaluateScore(currentShape, combo * 2, parameters);
                }
                // for(int row = 0; row < tempField.getHeight(); row++){
                // String str ="";
                // for(int col = 0; col < tempField.getWidth(); col ++){
                // str+= " " + tempField.getCell(col, row).getState() + " ";
                //// write(" " + tempField.getCell(col, row).getState() +" ");
                // }
                // write(str);
                // }
                // write(" " );

                /*
                 * ONE LOOKAHEAD
                 */

                if (nextShape != null) {
                        int removedLines = tempField.removeCompletedLines();
                        StrategyProcessor lookaheadProcessor = new StrategyProcessor(nextShape.copyShape(), null,
                                        tempField, removedLines + combo, parameters);
                        lookaheadProcessor.run();
                        score += lookaheadProcessor.getBest().score;
                }

                if (score > best.getScore()) {

                        // write("HT: " + tempField.getHeight() + " Loc: " +
                        // currentShape.getLocation().getY() + " Size: " +
                        // currentShape.getSize());
                        // write("Compl rows: " + tempCompletedLines + " Combo:
                        // " + combo);
                        // write("holes: " + tempField.getHoles(currentShape));
                        // write(" ");

                        // write("New best: " );
                        // write(tempResult.getScore() + "");
                        // write(tempResult.getRight() + "r");
                        // write(tempResult.getLeft() + "l");
                        // write(tempResult.getRotationsRight() + "rot");
                        best.setScore(score);
                        best.setLeft(left);
                        best.setRotationsRight(rotations);
                        best.setFloor_rotationsLeft(leftRotations);
                        best.setFloor_rotationsRight(rightRotations);
                        best.setDown(down);

                }
        }

        public Result getBest() {
                return best;
        }

        // public void write(String line) {
        // try {
        // FileWriter writer = new FileWriter("starter_out.txt", true);
        // writer.write(line);
        // writer.write("\n");
        // writer.close();
        // } catch (IOException e) {
        // // TODO Auto-generated catch block
        // e.printStackTrace();
        // }
        // }
}
