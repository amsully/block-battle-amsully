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

        public Field(int width, int height, String fieldString) {
                this.width = width;
                this.height = height;

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
                write("return!");
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

                write(strBldr.toString().substring(0, strBldr.length() - 1));
                return strBldr.toString().substring(0, strBldr.length() - 1);
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

        public double evaluateScore() {
                // TODO Auto-generated method stub
                return 0;
        }

        // Required that we already know this is a valid block.
        public void setBlock(Shape currentShape) {

                Cell[] blocks = currentShape.getBlocks();

                for (Cell c : blocks) {
                        Cell fieldCell = getCell(c.getLocation().x, c.getLocation().y);
                        fieldCell.setShape();
                }

        }

}
