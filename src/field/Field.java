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

        // Score values
        int bumpiness;

        public Field(int width, int height, String fieldString) {
                this.width = width;
                this.height = height;
                this.maxHeight = 0; // Initial height.
                this.bumpiness = 0;
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
                if (!cell.isEmpty() && !cell.isShape() && (20 - y) > maxHeight) {
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

        public boolean canMoveLeft(Shape currentShape) {
                // TODO Auto-generated method stub
                currentShape.oneLeft();
                boolean result = true;
                for (Cell cellBlock : currentShape.getBlocks()) {
                        Cell fieldBlock = getCell(cellBlock.getLocation().x, cellBlock.getLocation().y);

                        if (fieldBlock == null && cellBlock.getLocation().y != -1) {
                                result = false;
                                break;
                        } else if (fieldBlock != null && !fieldBlock.isEmpty() && !fieldBlock.isShape()) {
                                result = false;
                                break;
                        }
                }

                currentShape.oneRight();
                return result;
        }

        public boolean canMoveRight(Shape currentShape) {
                // TODO Auto-generated method stub

                currentShape.oneRight();
                boolean result = true;
                for (Cell cellBlock : currentShape.getBlocks()) {

                        Cell fieldBlock = getCell(cellBlock.getLocation().x, cellBlock.getLocation().y);

                        if (fieldBlock == null && cellBlock.getLocation().y != -1) {
                                result = false;
                                break;
                        } else if (fieldBlock != null && !fieldBlock.isEmpty() && !fieldBlock.isShape()) {
                                result = false;
                                break;
                        }
                }

                currentShape.oneLeft();
                return result;
        }

        public boolean canMoveDown(Shape currentShape) {
                // TODO Auto-generated method stub
                currentShape.oneDown();
                boolean result = true;
                for (Cell cellBlock : currentShape.getBlocks()) {
                        Cell fieldBlock = getCell(cellBlock.getLocation().x, cellBlock.getLocation().y);
                        if (fieldBlock == null && cellBlock.getLocation().y != -1) {
                                result = false;
                                break;
                        } else if (fieldBlock != null && !fieldBlock.isEmpty() && !fieldBlock.isShape()) {
                                result = false;
                                break;
                        }
                }

                currentShape.oneUp();
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
                        Cell fieldCell = getCell(c.getLocation().x, c.getLocation().y);

                        if (fieldCell == null)
                                continue;

                        fieldCell.setShape();

                        if (20 - fieldCell.getLocation().y > maxHeight)
                                maxHeight = 20 - fieldCell.getLocation().y;
                }

        }

        public double evaluateScore(Shape currentShape) {
                // TODO Auto-generated method stub
                // double adjacent = getAdjacentBlockCount(currentShape);
                double completedRows = getCompletedRows(currentShape);
                double holes = getHoles(currentShape);
                double aggregateHeight = getAggregateHeight_SetBump();

                double a = -0.51006, b = 0.760666, c = -0.35663, d = -0.184483;

//                return aggregateHeight * -1;
                 double score = (a * aggregateHeight)+
                 (b* completedRows) +
                 (c * holes) +
                 (d * bumpiness);
                
                 return score;

                // double averageHeight = getAverageHeight();
                // double resultingHeight = maxHeight - completedRows;
                //
                // double hole_parameter = -20 / Math.pow(maxHeight, 2);
                //// double hole_parameter = -7.8;
                //
                // return (averageHeight * -.5) + (resultingHeight * -10) +
                // (adjacent * 5) + (completedRows * 2)
                // + (holes * hole_parameter);
                // return (averageHeight * -.5) + (resultingHeight * -10) +
                // (adjacent * 5) + (completedRows * 2)
                // + (holes * -20);

                // return (averageHeight * -.5) + (holes *hole_parameter);
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

                        temp += Math.abs(curr - prev);

                        prev = curr;
                }
                bumpiness = temp;

//                write(total + "");
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

        private int getCompletedRows(Shape currentShape) {
                // TODO Auto-generated method stub
                int rows = 0;
                for (Cell c : currentShape.getBlocks()) {
                        int v = c.getLocation().y;
                        boolean completeRow = true;

                        if (v < 0)
                                continue;

                        for (int i = 0; i < grid.length; i++) {

                                if (grid[i][v].isEmpty() || grid[i][v].isShape()) {
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

        private int getHoles(Shape currentShape) {
                // TODO Auto-generated method stub
                int hole_count = 0;

                for (int i = 0; i < grid.length; i++) {
                        boolean countHole = false;
                        // TODO : OPTIMIZE SO WE DOnT ITERATE OVER EVERYTHING
                        for (int j = 0; j < this.height; j++) {

                                Cell c = grid[i][j];
                                if (!c.isEmpty()) {
                                        countHole = true;
                                } else if (countHole) {
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

                        fieldCell.setEmpty();// = new Cell(c.getLocation().x,
                                             // c.getLocation().y,
                                             // CellType.EMPTY);

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
}

// public double evaluateScore(Shape currShape) {
// Double max_height = getMaxHeight();
// double average_height = getAverageHeight();
//
// double hole_count = getHoles(max_height);
// double hole_parameter = -20/Math.pow(max_height,2);
//
// double complete_rows = getCompletedRows();
//
// double number_connected = getConnected();
//
// double resultingHeight = max_height - complete_rows;
//
// double score = (resultingHeight * -10)// (20-max_height)) // Low doesnt
// matter too much
// + (hole_count * hole_parameter)
// + (complete_rows * 2)
// + (number_connected * 5)
// + (average_height * -.5);
//
// // VERSION 18 SCORING FUNCTION
//// double score = (hole_count * hole_parameter) + (resultingHeight * -5);
//
//// double score = (complete_rows) + (hole_count * hole_parameter) +
// (resultingHeight *-5) - max_height;
// interestingRows.clear();
// tempVals = new int[this.width][this.height];
// return score;
//
// }
//

//
// private double getCompletedRows() {
// // TODO Auto-generated method stub
// double rows = 0;
// for(Point pt : interestingRows){
// int v= pt.y;
// boolean completeRow = true;
// for(int i = 0; i < grid.length; i++){
//
// if(tempVals[i][v] == 4) continue;
//
// if(grid[i][v].isEmpty() || grid[i][v].isShape() ){
// completeRow = false;
// break;
// }
// }
//
// if(completeRow){
// rows++;
// }
//
// }
// return rows;
// }
//
// private double getHoles(Double max_height) {
// // TODO Auto-generated method stub
// double hole_count = 0;
//
// for (int i = 0; i < grid.length; i++) {
// boolean countHole = false;
// for (int j = max_height.intValue() - 1; j < this.height; j++) {
// Cell c = grid[i][j];
// if(!c.isEmpty() || tempVals[i][j] == 4){
// countHole = true;
// }
// if (countHole && c.isEmpty() && tempVals[i][j] != 4) {
// hole_count++;
// }
// }
// }
//
// return hole_count;
// }
//
/// *
// * Calculates average height of the stack Highest part of the board is 0
// * and lowest is 19. (20 height).
// */
// public double getAverageHeight() {
// // TODO Auto-generated method stub
// double heightOfBoard = grid[0].length;
//
// double total = 0;
//
// for (int i = 0; i < grid.length; i++) {
// for (int j = 0; j < grid[0].length; j++) {
// Cell c = grid[i][j];
// if (!c.isEmpty() || tempVals[i][j] == 4) {
//
// // Continue if it is the shape we are
// // trying to process.
// if (c.isShape() && tempVals[i][j] != 4)
// continue;
//
// double height = heightOfBoard - c.getLocation().getY();
// total+= height;
// break;
// }
// }
// }
//
// return total/grid.length;
// }
//
// public double getMaxHeight() {
// // TODO Auto-generated method stub
// double heightOfBoard = grid[0].length;
//
// double max_height = 0;
//
// for (int i = 0; i < grid.length; i++) {
// for (int j = 0; j < grid[0].length; j++) {
// Cell c = grid[i][j];
// if (!c.isEmpty() || tempVals[i][j] == 4) {
//
// // Continue if it is the shape we are
// // trying to process.
// if (c.isShape() && tempVals[i][j] != 4)
// continue;
//
// double height = heightOfBoard - c.getLocation().getY();
// if (height > max_height) {
// max_height = height;
// }
// break;
// }
// }
// }
//
// return max_height;
// }
