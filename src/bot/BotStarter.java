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

package bot;

import java.awt.Point;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Random;

import field.Field;
import field.Shape;
import field.ShapeType;
import moves.MoveType;

/**
 * BotStarter class
 * 
 * This class is where the main logic should be. Implement getMoves() to return
 * something better than random moves.
 * 
 * @author Jim van Eeden <jim@starapple.nl>
 */

/*
 * TO UPLOAD BOT:
 * 
 * Remove file writer from BotStarter, BotParser and Field.
 * 
 */

public class BotStarter {

        public BotStarter() {
        }

        /**
         * Returns a random amount of random moves
         * 
         * @param state
         *                : current state of the bot
         * @param timeout
         *                : time to respond
         * @return : a list of moves to execute
         */
        public ArrayList<MoveType> getMoves(BotState state, long timeout) {

                Shape currentShape;
                Shape nextShape;
                Field field = state.getMyField();

                // Create current shape.
                Point location = state.getShapeLocation();
                ShapeType type = state.getCurrentShape();
                currentShape = new Shape(type, field, location);

                // Create next shape.
                ShapeType nType = state.getNextShape();
                Point nLocation = (nType == ShapeType.O) ? new Point(4, -1) : new Point(3, -1);
                nextShape = new Shape(nType, field, nLocation);
                
                StrategyProcessor processor = new StrategyProcessor(currentShape, nextShape, field);
                processor.run();

                Result bestResult = processor.getBest();
                
                
                bestResult.moves.add(MoveType.DOWN);
                
                write("ROUND " + state.getRound() + ": " +  bestResult.moves.toString());
                return bestResult.moves;
        }


        public static void main(String[] args) {
                BotParser parser = new BotParser(new BotStarter());
                parser.run();
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
