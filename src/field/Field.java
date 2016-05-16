// Copyright 2015 theaigames.com (developers@theaigames.com)

//    Licensed under the Apache License, Version 2.0 (the "License");
//    you may not use this file except in compliance with the License.
//    You may obtain a copy of the License at

//        http://www.apache.org/licenses/LICENSE-2.0

//    Unless required by applicable law or agreed to in writing, software
//    distributed under the License is distributed on an "AS IS" BASIS,
//    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
//    See the License for the specific language governing permissions and
//    limitations under the License.
//	
//    For the full copyright and license information, please view the LICENSE
//    file that was distributed with this source code.

package field;

import java.awt.Point;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;

import com.sun.corba.se.impl.protocol.ServantCacheLocalCRDBase;

import field.Cell;
import moves.MoveType;

/**
 * Field class
 * 
 * Represents the playing field for one player. Has some basic methods already
 * implemented.
 * 
 * @author Jim van Eeden <jim@starapple.nl>
 */

public class Field {

        private int width;
        private int height;
        private Cell grid[][];
        private int maxHeight;
        boolean completeRows[];

        // Score values
        int bumpiness;

        public Field(int width, int height, String fieldString) {
                this.width = width;
                this.height = height;
                this.maxHeight = 0; // Initial height.
                this.bumpiness = 0;
                this.completeRows = new boolean[this.height];
                parse(fieldString);
        }

        /**
         * Parses the input string to get a grid with Cell objects
         * 
         * @param fieldString
         *                : input string
         */
        private void parse(String fieldString) {

                this.grid = new Cell[this.width][this.height];

                // get the separate rows
                String[] rows = fieldString.split(";");
                for (int y = 0; y < this.height; y++) {
                        String[] rowCells = rows[y].split(",");

                        // parse each cell of the row
                        for (int x = 0; x < this.width; x++) {
                                int cellCode = Integer.parseInt(rowCells[x]);
                                this.grid[x][y] = new Cell(x, y, CellType.values()[cellCode]);

                                updateMax(this.grid[x][y], y);

                        }
                }
        }

        // Updates the max. This does not consider 'shape' blocks.
        private void updateMax(Cell cell, int y) {
                // TODO Auto-generated method stub
                if (cell.isBlock() && (20 - y) > maxHeight) {
                        maxHeight = 20 - y;
                }
        }

        public Cell getCell(int x, int y) {
                if (x < 0 || x >= this.width || y < 0 || y >= this.height)
                        return null;
                return this.grid[x][y];
        }

        public int getHeight() {
                return this.height;
        }

        public int getWidth() {
                return this.width;
        }

        public boolean isValidPiece(Shape currentShape) {
                boolean result = true;
                for (Cell cellBlock : currentShape.getBlocks()) {

                        if (cellBlock.hasCollision(this) || cellBlock.isOutOfBoundaries(this)) {
                                result = false;
                                break;
                        }
                }
                return result;
        }

        public Field copyField() {
                // TODO Auto-generated method stub
                return new Field(this.width, this.height, this.getField());
        }

        private String getField() {
                // TODO Auto-generated method stub
                StringBuilder strBldr = new StringBuilder();

                for (int i = 0; i < this.height; i++) {
                        boolean first = true;
                        for (int j = 0; j < this.width; j++) {
                                Cell c = grid[j][i];

                                if (first) {
                                        strBldr.append(c.getState().getCode());
                                        first = false;
                                } else {
                                        strBldr.append("," + c.getState().getCode());
                                }
                        }
                        strBldr.append(";");
                }

                return strBldr.toString().substring(0, strBldr.length() - 1);
        }

        public int getMaxHeight() {
                return maxHeight;
        }

        // Required that we already know this is a valid block.
        public void setBlock(Shape currentShape) {

                Cell[] blocks = currentShape.getBlocks();

                for (Cell c : blocks) {

                        setCell(c);

                }

        }

        private void setCell(Cell cell) {
                int x = (int) cell.getLocation().getX();
                int y = (int) cell.getLocation().getY();
                if (x < 0 || x >= this.width || y < 0 || y >= this.height)
                        return;
                this.grid[x][y].setBlock();

                if (20 - y > maxHeight) {
                        maxHeight = 20 - y;
                }

        }

        public double evaluateScore(Shape currentShape, int combo, double parameters[]) {

                double adjacent = getAdjacentBlockCount(currentShape);

                double completedRows = getCompletedRows(currentShape);
                double holes = getHoles(currentShape);
                double aggregateHeight = getAggregateHeight_SetBump();
                // double averageHeight = getAverageHeight();

                // double score = (a * aggregateHeight) + (b * completedRows) +
                // (c * holes) + (d * bumpiness)
                // // + (.3 * adjacent) + (-.1*maxHeight);
                parameters[0] = -4;
                parameters[1] = -7;
                parameters[2] = 4;
                parameters[3] = 3;
                parameters[4] = -7;
                parameters[5] = -5;
                parameters[6] = 3;
                parameters[7] = 0;

                double a = -4, b = -7, c = 4, d = 3, e = -7, f = -5, g = 3, h = 0;

                double averageHeight = getAverageHeight();
                double resultingHeight = maxHeight - completedRows;

                // ALGORITHM RUN:

                double score = (averageHeight * parameters[0]) + (resultingHeight * parameters[1])
                                + (adjacent * parameters[2]) + (completedRows * combo * parameters[3])
                                + (holes * getHoleParameter(parameters[4]))
                                + (parameters[5] * (this.getHeight() - currentShape.getLocation().getY()
                                                - currentShape.getSize()))
                                + (bumpiness * parameters[6]) + (aggregateHeight * parameters[7])
                                + getTSpinSlot(parameters[8], parameters[9]);

                /*
                 * Score focusing on TSPIN
                 */
                // double score = getTSpinSlot();

                // write(score + "" );
                return score;
                // INCLUDES NEW CALCULATION FOR HOLES
                // return (adjacent * 5) + (completedRows * combo * 2 )
                // + (holes * -10) + (-3 *(this.getHeight() -
                // currentShape.getLocation().getY()
                // - currentShape.getSize()) ) + (-2 * bumpiness) + (-5 *
                // aggregateHeight);

                // return (averageHeight * -.5) + (resultingHeight * -1) +
                // (adjacent * 2) + (completedRows * combo * 5 )
                // + (holes * -20) + (-10 *(this.getHeight() -
                // currentShape.getLocation().getY()
                // - currentShape.getSize()) );

                // return ((this.getHeight() - currentShape.getLocation().getY()
                // - currentShape.getSize()) * -5)
                // + (completedRows * combo * 3) + (holes * -10);

                // return (averageHeight * -.5) + (resultingHeight * -10) +
                // (adjacent * 5) + (completedRows * combo)
                // + (holes * -20);

                /*
                 * A
                 */
                // return ((this.getHeight() - currentShape.getLocation().getY()
                // - currentShape.getSize()) * -5)+
                // (completedRows * combo * 3) + (holes * -10);

                // return score;
        }

        private double getHoleParameter(double d) {
                // TODO Auto-generated method stub
                return -20 / Math.pow(maxHeight, d);
        }

        /*
         * We can weight each feature a little more. 10, 100, 1000. 10 points
         * for an empty row (add an exception that it is isolated). 100 points
         * for 3 emptys above it. 1000 points for a successful completion.
         */
        private double getTSpinSlot(double parameters, double parameters2) {
                int bottomRow = 19;
                int midRow = 18;
                int highRow = 17;

                int tSpinScore = 0;
                for (int i = highRow; i >= 0; i--) {

                        tSpinScore += getTSpinSection(bottomRow, midRow, highRow);

                        highRow--;
                        midRow--;
                        bottomRow--;
                }

                return tSpinScore;
        }

        private double getTSpinSection(int bottomRow, int midRow, int highRow) {
                // TODO Auto-generated method stub
                if (this.getCell(0, bottomRow).isSolid()) {
                        return 0;
                }

                int score = 0;

                // TODO: NOTE: Now only basing it on the central column.
                for (int col = 5; col <= 5; col++) {
                        if (getCell(col, bottomRow).isEmpty()) {
                                score += 100;
                                if (firstLevelScores(col, bottomRow)) {
                                        score += 1000;

                                        if (midRowValid(col, midRow)) {

                                                score += 10000;

                                                if (highRowValid(col, highRow)) {
                                                        if (colIsClear(col, highRow)) {
                                                                write("Gold");
                                                                score += 100000;

                                                        }
                                                } else {
                                                        score -= 10000;
                                                }
                                        } else {
                                                score -= 1000;
                                        }
                                } else {
                                        score -= 100;
                                }

                        } else {
                                score -= 10;
                        }
                }

                return score;
        }

        private boolean firstLevelScores(int col, int bottomRow) {
                // TODO Auto-generated method stub

                if (col == 0 || col == this.getWidth() - 1)
                        return false;

                int left = col - 1;
                int right = col + 1;

                Cell leftCell = this.getCell(left, bottomRow);
                Cell rightCell = this.getCell(right, bottomRow);

                if (leftCell.isEmpty() || rightCell.isEmpty()) {
                        return false;
                }
                return true;
        }

        // Method to determine if we can fit a t-block into the slot.
        private boolean colIsClear(int col, int highRow) {
                // TODO Auto-generated method stub
                int sideCol = col;
                if (this.getCell(sideCol - 1, highRow).isEmpty()) {
                        sideCol++; // We need to check the right col.
                } else {
                        sideCol--; // We need to check the left col.
                }

                int high = highRow;
                while (high != 0) {

                        Cell side = this.getCell(sideCol, high);
                        Cell mid = this.getCell(col, high);

                        if (!side.isEmpty() || !mid.isEmpty()) {
                                return false;
                        }

                        high--;
                }

                return true;
        }

        // n = none, l = left, r = right
        private Boolean highRowValid(int col, int highRow) {
                // TODO Auto-generated method stub
                if (highRow < 0)
                        return false; // Out of bounds

                if (!this.getCell(col, highRow).isEmpty())
                        return false; // Mid col blocked.

                Cell left = this.getCell(col - 1, highRow);
                Cell right = this.getCell(col + 1, highRow);

                if (left.isEmpty() && !right.isEmpty()) {
                        return true; // Rotate right needed.
                } else if (right.isEmpty() && !left.isEmpty()) {
                        return true; // Rotate left needed.
                }
                return false;
        }

        private boolean midRowValid(int col, int midRow) {
                // TODO Auto-generated method stub
                if (col == 0 || col == this.width - 1) {
                        return false;
                }

                Cell left = this.getCell(col - 1, midRow);
                Cell mid = this.getCell(col, midRow);
                Cell right = this.getCell(col + 1, midRow);

                if (left.isEmpty() && mid.isEmpty() && right.isEmpty()) {
                        return true;
                }

                return false;
        }

        private double getAggregateHeight_SetBump() {
                double total = 0;

                ArrayList<Double> heights = new ArrayList<Double>();

                for (int i = 0; i < this.width; i++) {
                        for (int j = 0; j < this.height; j++) {
                                Cell c = this.getCell(i, j); // grid[i][j];
                                if (!c.isEmpty()) {
                                        double height = this.height - c.getLocation().getY();
                                        total += height;

                                        heights.add(height);
                                        break;
                                }
                        }
                }

                int temp = 0;
                double prev = heights.get(0);
                for (int i = 1; i < heights.size(); i++) {
                        double curr = heights.get(i);

                        temp += Math.sqrt(Math.abs(curr - prev));

                        prev = curr;
                }
                bumpiness = temp;

                // write(total + "");
                return total;
        }

        private int getAdjacentBlockCount(Shape currentShape) {
                // TODO Auto-generated method stub
                int totalAttached = 0;
                int[][] orientation = { { 0, 1 }, { 1, 0 }, { -1, 0 }, { 0, -1 } };
                for (Cell c : currentShape.getBlocks()) {
                        Point pt = c.getLocation();
                        for (int[] offset : orientation) {
                                int x_val = pt.x + offset[0];
                                int y_val = pt.y + offset[1];

                                Cell xy = getCell(x_val, y_val);

                                // Weight for the wall is half
                                if (xy == null) {
                                        totalAttached++;
                                } else {
                                        if (!xy.isEmpty()) {
                                                totalAttached++;
                                        }
                                }

                        }
                }

                return totalAttached;
        }

        public int getCompletedRows(Shape currentShape) {
                // TODO Auto-generated method stub
                int rows = 0;
                for (Cell c : currentShape.getBlocks()) {
                        int v = c.getLocation().y;
                        boolean completeRow = true;

                        if (v < 0)
                                continue;

                        for (int i = 0; i < this.getWidth(); i++) {

                                if (grid[i][v].isEmpty() || grid[i][v].isSolid()) {
                                        completeRow = false;
                                        break;
                                }
                        }

                        if (completeRow) {
                                rows++;
                        }

                }
                return rows;
        }

        public int getHoles(Shape currentShape) {
                // TODO Auto-generated method stub
                int hole_count = 0;

                for (int i = 0; i < this.width; i++) {
                        boolean countHole = false;
                        int multiplier = 0;
                        // TODO : OPTIMIZE SO WE DOnT ITERATE OVER EVERYTHING
                        for (int j = 0; j < this.height; j++) {

                                if (grid[i][j].isBlock()) {
                                        countHole = true;
                                } else if (countHole && grid[i][j].isEmpty()) {
                                        // multiplier+=1;
                                        // hole_count+=multiplier;
                                        hole_count++;
                                }

                        }
                }
                return hole_count;
        }

        public double getAverageHeight() {
                // TODO Auto-generated method stub
                double heightOfBoard = grid[0].length;

                double total = 0;

                for (int i = 0; i < grid.length; i++) {
                        for (int j = 0; j < grid[0].length; j++) {
                                Cell c = grid[i][j];
                                if (!c.isEmpty()) {
                                        double height = heightOfBoard - c.getLocation().getY();
                                        total += height;
                                        break;
                                }
                        }
                }
                return total / grid.length;
        }

        public void erradicateShape(Shape currentShape) {
                // TODO Auto-generated method stub
                for (Cell c : currentShape.getBlocks()) {

                        Cell fieldCell = getCell(c.getLocation().x, c.getLocation().y);

                        if (fieldCell == null)
                                continue;

                        fieldCell.setEmpty();

                }
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

        public int removeCompletedLines() {
                // TODO Auto-generated method stub
                int total = 0;

                for (int row = 0; row < this.height; row++) {
                        for (int col = 0; col < this.width; col++) {
                                if (!this.grid[col][row].isBlock()) {
                                        break;
                                }
                                if (col == this.width - 1) {
                                        total++;
                                        Cell bringDown;
                                        for (int i = 0; i < this.width; i++) {
                                                this.grid[i][row].setEmpty();
                                                for (int j = row - 1; j >= 0; j--) {
                                                        bringDown = this.grid[i][j + 1];
                                                        this.grid[i][j + 1] = this.grid[i][j];
                                                        this.grid[i][j] = bringDown;
                                                }
                                        }
                                }
                        }
                }

                return total;
        }
}