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

package com.theaigames.blockbattle;

import java.io.FileWriter;
import java.io.IOException;
import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Random;

import com.theaigames.blockbattle.field.Field;
import com.theaigames.blockbattle.player.Player;
import com.theaigames.engine.io.IOPlayer;
import com.theaigames.game.AbstractGame;
import com.theaigames.game.player.AbstractPlayer;

public class Blockbattle extends AbstractGame {

        private final long TIMEBANK_MAX = 10000l;
        private final long TIME_PER_MOVE = 500l;
        private final int FIELD_WIDTH = 10;
        private final int FIELD_HEIGHT = 20;

        private List<Player> players;
        // private String TEST_BOT_2;

        @Override
        public void setupGame(ArrayList<IOPlayer> ioPlayers) throws Exception {

                System.out.println("Setting up game...");

                // set the maximum number of rounds if necessary
                super.maxRounds = -1;

                // create all the players and everything they need
                this.players = new ArrayList<Player>();
                for (int i = 0; i < ioPlayers.size(); i++) {

                        // create the playing field
                        Field field = new Field(FIELD_WIDTH, FIELD_HEIGHT);

                        // create the player
                        String playerName = String.format("player%d", i + 1);
                        Player player = new Player(playerName, ioPlayers.get(i), TIMEBANK_MAX, TIME_PER_MOVE, field);
                        this.players.add(player);
                }

                // send the settings
                for (AbstractPlayer player : this.players)
                        sendSettings(player);

                // create the processor
                super.processor = new Processor(this.players, FIELD_WIDTH, FIELD_HEIGHT);
        }

        @Override
        public void sendSettings(AbstractPlayer player) {

                // create player names string
                String playerNames = "";
                for (Player p : this.players) {
                        playerNames += p.getName() + ",";
                }
                playerNames = playerNames.substring(0, playerNames.length() - 1);

                // send the mandatory settings
                player.sendSetting("timebank", (int) TIMEBANK_MAX);
                player.sendSetting("time_per_move", (int) TIME_PER_MOVE);
                player.sendSetting("player_names", playerNames);
                player.sendSetting("your_bot", player.getName());

                // send the game specific settings
                player.sendSetting("field_width", FIELD_WIDTH);
                player.sendSetting("field_height", FIELD_HEIGHT);
        }

        @Override
        protected void runEngine() throws Exception {
                super.engine.setLogic(this);
                super.engine.start();
        }

        // DEV_MODE can be turned on to easily test the
        // engine from eclipse
        public static void main(String args[]) throws Exception {
                // Blockbattle game = new Blockbattle();

                // Array of parameters // 100 of them

                /*
                 * for 1 - 1000 -- randomly choose two values. -- delete the
                 * loser -- increment the winner -- create offspring with random
                 * person
                 */
                ArrayList<Bot> parameters = new ArrayList<Bot>();
                Random random = new Random();

                for (int i = 0; i < 20; i++) {
                        Double[] temp = new Double[10];
                        for (int j = 0; j < 10; j++) {
                                temp[j] = (double) random.nextInt(20) - 10; // Range
                                                                            // -10
                                                                            // to
                                                                            // 10
                        }
                        parameters.add(new Bot(temp));
                }

                // DEV_MODE settings
                // game.TEST_BOT = "java -cp
                // /home/ams/Dropbox/GitHub/block-battle-amsully/bin/
                // bot.BotStarter";//
                // /home/ams/Dropbox/GitHub/block-battle-amsully/JavaBot/bot/BotStarter.java";
                // game.TEST_BOT_2 = "java -cp
                // /home/ams/Dropbox/GitHub/block-battle-amsully/bin/
                // bot.BotStarter";
                // game.TEST_BOT = "java -cp
                // /home/ams/Dropbox/workspace/amazingBot/bin bot.BotStarter";//
                // /home/ams/Dropbox/GitHub/block-battle-amsully/JavaBot/bot/BotStarter.java";

                // game.NUM_TEST_BOTS = 2;
                // game.DEV_MODE = true;
                while (true) {
                        // Play each bot 50 times against others.
                        for (int i = 0; i < parameters.size(); i++) {
                                Blockbattle game = new Blockbattle();
                                game.NUM_TEST_BOTS = 2;
                                game.DEV_MODE = true;

                                int bot_one = i;

                                // This Bot will play 50 games
                                for (int f = 0; f < 5; f++) {
                                        int bot_two = (int) (Math.random() * parameters.size());

                                        System.out.println(
                                                        "Rank 1: " + bot_one + " Wins: " + parameters.get(bot_one).win);
                                        System.out.println(
                                                        "Rank 2: " + bot_two + " Wins: " + parameters.get(bot_two).win);

                                        game.TEST_BOT = "java -cp /home/ams/Dropbox/GitHub/block-battle-amsully/bin/ bot.BotStarter";// /home/ams/Dropbox/GitHub/block-battle-amsully/JavaBot/bot/BotStarter.java";
                                        game.TEST_BOT_2 = "java -cp /home/ams/Dropbox/GitHub/block-battle-amsully/bin/ bot.BotStarter";

                                        for (Double par : parameters.get(bot_one).getParameters()) {
                                                game.TEST_BOT = game.TEST_BOT + " " + par;
                                        }

                                        for (Double par : parameters.get(bot_two).getParameters()) {
                                                game.TEST_BOT_2 = game.TEST_BOT_2 + " " + par;
                                        }

                                        if (game.TEST_BOT_2.contains("null") || game.TEST_BOT.contains("null")) {
                                                continue;
                                        }

                                        game.setupEngine(args);
                                        game.runEngine();
                                        // If processor returns TEST_BOT as
                                        // winner,
                                        // increment winner.

                                        // Dont do anything if draw
                                        if (game.processor.getWinner() == null)
                                                continue;

                                        if (game.processor.getWinner().getName()
                                                        .equals(game.players.get(0).getName())) {
                                                System.out.println("Bot One Wins");
                                                parameters.get(bot_one).win++;
                                                parameters.get(bot_two).win--;
                                                // for (Double parameter :
                                                // parameters.get(bot_two).getParameters())
                                                // {
                                                // parameter += (Math.random() *
                                                // .3) - .1; // -.1
                                                // // to
                                                // // .1
                                                // }
                                        } else {
                                                System.out.println("Bot Two Wins");
                                                parameters.get(bot_one).win--;
                                                parameters.get(bot_two).win++;

                                                // for (Double parameter :
                                                // parameters.get(bot_one).getParameters())
                                                // {
                                                // parameter += (Math.random() *
                                                // .2) - .1; // -.1
                                                // // to
                                                // // .1
                                                // }
                                        }
                                }
                        }
                        /*
                         * Print winners for reference
                         */
                        Collections.sort(parameters, Collections.reverseOrder());
                        System.out.println("WINNER LIST");
                        write("Winners List");
                        for (Bot bot : parameters) {
                                write(bot.win + " " + Arrays.toString(bot.getParameters()));
                                System.out.println(bot.win + " " + Arrays.toString(bot.getParameters()));
                        }
                        write("");

                        /*
                         * Create offspring
                         */
                        ArrayList<Bot> offspring = new ArrayList<Bot>();
                        while (offspring.size() < parameters.size() * .3) {
                                // Pick 20% of bots (50 for 500 bots).
                                ArrayList<Bot> randomSample = new ArrayList<Bot>();
                                for (int i = 0; i < 4; i++) {
                                        randomSample.add(parameters.get((int) (Math.random() * parameters.size())));
                                }
                                // Sort by rank.
                                Collections.sort(randomSample, Collections.reverseOrder());

                                Bot one = randomSample.get(0);
                                Bot two = randomSample.get(1);

                                // How much the parameters lean towards the
                                // winner.
                                double fitRatio = 0;
                                if (one.win == 0) {
                                        fitRatio = 1;
                                } else {
                                        fitRatio = two.win / one.win;
                                }
                                Double[] newPar = new Double[10];

                                for (int i = 0; i < newPar.length; i++) {
                                        newPar[i] = (double)(int)((fitRatio * two.parameters[i])
                                                        + ((1 - fitRatio) * one.parameters[i]));
                                }
                                
                                // 7% chance of mutation.
//                                if(Math.random() < .15){
//                                        int componentMutation = random.nextInt(10);
//                                        int amt = random.nextInt(7)-3;
//                                        newPar[componentMutation] += amt;
//                                }
                                
                                // % mutation for each component
                                for(int i = 0; i < newPar.length; i++){
                                        if(Math.random() < .15){
                                                int componentMutation = random.nextInt(10);
                                                int amt = random.nextInt(7)-3;
                                                newPar[componentMutation] += amt;
                                        }
                                }
                                
                                offspring.add(new Bot(newPar));

                        }

                        for(int i = 0; i < offspring.size(); i++){
                                parameters.remove(parameters.size()-1);
                        }

                        // Add all offspring.
                        System.out.println("Offspring size: " + offspring.size());
                        parameters.addAll(offspring);

                        // Reset the win count.
                        for (int i = 0; i < parameters.size(); i++) {
                                parameters.get(i).win = 0;
                        }

                }
                // game.setupEngine(args);
                // game.runEngine();

        }

        static public void write(String line) {
                try {
                        FileWriter writer = new FileWriter("genetic_algo.txt", true);
                        writer.write(line);
                        writer.write("\n");
                        writer.close();
                } catch (IOException e) {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                }
        }

        static class Bot implements Comparable<Bot> {
                Double[] parameters = new Double[10];
                int win;

                public Bot(Double[] par) {
                        for (int i = 0; i < par.length; i++) {
                                parameters[i] = par[i];
                        }
                        win = 0;
                }

                @Override
                public int compareTo(Bot arg0) {
                        // TODO Auto-generated method stub
                        return win - arg0.win;
                }

                public Double[] getParameters() {
                        return this.parameters;
                }
        }
}
