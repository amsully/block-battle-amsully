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
        private int tempVals[][];
        private ArrayList<Point> interestingRows;
        
        public Field(int width, int height, String fieldString) {
                this.width = width;
                this.height = height;

                this.tempVals = new int[width][height];
                this.interestingRows = new ArrayList<Point>();
                
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
                        }
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

        public String gridString(Shape shape) {
                StringBuilder bldr = new StringBuilder();
                String[][] actual = new String[grid[0].length][grid.length];
                for (Cell[] cellArr : grid) {
                        for (Cell cell : cellArr) {
                                Point p = cell.getLocation();
                                int x = p.x;
                                int y = p.y;
                                if (!cell.getState().toString().equals("SHAPE"))
                                        actual[y][x] = cell.getState().toString();
                                else
                                        actual[y][x] = "EMPTY"; // HACKY
                        }
                }

                for (Cell cell : shape.getBlocks()) {
                        Point p = cell.getLocation();
                        int x = p.x;
                        int y = p.y;
                        if (x < 0 || x >= this.width || y < 0 || y >= this.height) {

                        } else {
                                actual[y][x] = cell.getState().toString();
                        }
                }
                for (String[] strArr : actual) {
                        for (String str : strArr) {
                                bldr.append(" " + str);
                        }
                        bldr.append("\n");
                }
                bldr.append("\n");

                return bldr.toString();
        }

        public double evaluateScore(Shape currShape) {
                // TODO Auto-generated method stub

                // Cell[] blocks = currShape.getBlocks();
                Cell[][] locations = currShape.getShapeBox();

                tempVals = new int[this.width][this.height];

                // TEMP MEMORY ISSUE
                for (Cell[] shape_arr : locations) {
                        for (Cell shape_cell : shape_arr) {
                                if (!shape_cell.isEmpty()) {
                                        int shape_x = shape_cell.getLocation().x;
                                        int shape_y = shape_cell.getLocation().y;

                                        Cell field_cell = getCell(shape_x, shape_y);

                                        /*
                                         * Check if field cell exists and is
                                         * valid. If null and not a start
                                         * block.. continue.
                                         * 
                                         * Potentially error with continuing on
                                         * null?
                                         */
                                        if (field_cell == null && (shape_y != -1)) {
                                                return -Double.MAX_VALUE;
                                        } else if (shape_y == -1 && (shape_x < 0 || shape_x >= this.width)) {

                                                return -Double.MAX_VALUE;
                                        }

                                        /*
                                         * If cell is empty or not the shape, we
                                         * have a valid position.
                                         */
                                        if (field_cell != null && (field_cell.isEmpty() || field_cell.isShape())) {
                                                tempVals[shape_x][shape_y] = 4;
                                                interestingRows.add(new Point(shape_x, shape_y));

                                        } else {
                                                return -Double.MAX_VALUE;
                                        }
                                }
                        }
                }

                Double max_height = getMaxHeight();
                 double average_height = getAverageHeight();

                double hole_count = getHoles(max_height);
                double hole_parameter = -20/Math.pow(max_height,2);
                
                double complete_rows = getCompletedRows();
                
                double number_connected = getConnected();
                
                double resultingHeight = max_height - complete_rows;
                
                double score =    (resultingHeight      *       -10)// (20-max_height)) // Low doesnt matter too much 
                                + (hole_count           *       hole_parameter) 
                                + (complete_rows        *       2)
                                + (number_connected     *       5)
                                + (average_height       *       -.5);
                
                // VERSION 18 SCORING FUNCTION
//                double score = (hole_count * hole_parameter) + (resultingHeight * -5);
                
//                double score = (complete_rows) + (hole_count * hole_parameter) + (resultingHeight *-5) - max_height;
                interestingRows.clear();
                tempVals = new int[this.width][this.height];
                return score;

        }

        private double getConnected() {
                // TODO Auto-generated method stub
                int[][] orientation = {{0,1},{1,0},{-1,0},{0,-1}};
                double totalAttached = 0;
                for(Point pt : interestingRows){
                        for(int[] offset : orientation){
                                int x_val = pt.x + offset[0];
                                int y_val = pt.y + offset[1];
                                
                                Cell xy = getCell(x_val, y_val);
                                
                                if(xy == null){
                                        totalAttached++;
                                }else{
                                        if(!xy.isEmpty() && !xy.isShape() && tempVals[x_val][y_val] != 4){
                                                totalAttached++;
                                        }
                                }
                                
                        }
                }
                
                return totalAttached;
        }

        private double getCompletedRows() {
                // TODO Auto-generated method stub
                double rows = 0;
                for(Point pt : interestingRows){
                        int v= pt.y;
                        boolean completeRow = true;
                        for(int i = 0; i < grid.length; i++){
                                
                                if(tempVals[i][v] == 4) continue;
                                
                                if(grid[i][v].isEmpty() || grid[i][v].isShape() ){
                                        completeRow = false;
                                        break;
                                }
                        }
                        
                        if(completeRow){
                                rows++;
                        }
                        
                }
                return rows;
        }

        private double getHoles(Double max_height) {
                // TODO Auto-generated method stub
                double hole_count = 0;
                
                for (int i = 0; i < grid.length; i++) {
                        boolean countHole = false;
                        for (int j = max_height.intValue() - 1; j < this.height; j++) {
                                Cell c = grid[i][j];
                                if(!c.isEmpty() || tempVals[i][j] == 4){
                                        countHole = true;
                                }
                                if (countHole && c.isEmpty() && tempVals[i][j] != 4) {
                                        hole_count++;
                                }
                        }
                }

                return hole_count;
        }

        /*
         * Calculates average height of the stack Highest part of the board is 0
         * and lowest is 19. (20 height).
         */
        public double getAverageHeight() {
                // TODO Auto-generated method stub
                double heightOfBoard = grid[0].length;

                double total = 0;

                for (int i = 0; i < grid.length; i++) {
                        for (int j = 0; j < grid[0].length; j++) {
                                Cell c = grid[i][j];
                                if (!c.isEmpty() || tempVals[i][j] == 4) {

                                        // Continue if it is the shape we are
                                        // trying to process.
                                        if (c.isShape() && tempVals[i][j] != 4)
                                                continue;

                                        double height = heightOfBoard - c.getLocation().getY();
                                        total+= height;
                                        break;
                                }
                        }
                }

                return total/grid.length;
        }

        public double getMaxHeight() {
                // TODO Auto-generated method stub
                double heightOfBoard = grid[0].length;

                double max_height = 0;

                for (int i = 0; i < grid.length; i++) {
                        for (int j = 0; j < grid[0].length; j++) {
                                Cell c = grid[i][j];
                                if (!c.isEmpty() || tempVals[i][j] == 4) {

                                        // Continue if it is the shape we are
                                        // trying to process.
                                        if (c.isShape() && tempVals[i][j] != 4)
                                                continue;

                                        double height = heightOfBoard - c.getLocation().getY();
                                        if (height > max_height) {
                                                max_height = height;
                                        }
                                        break;
                                }
                        }
                }

                return max_height;
        }

        public Field getNewField(Shape currShape) {
                // TODO Auto-generated method stub
                return null;
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
