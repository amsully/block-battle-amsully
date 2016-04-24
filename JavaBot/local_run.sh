#!/bin/bash

cd ../..
cd blockbattle-engine
javac -d bin/ `find ./ -name '*.java' -regex '^[./A-Za-z0-9]*$'`

java -cp bin com.theaigames.blockbattle.Blockbattle "../block-battle-amsully/JavaBot/bot/BotStarter.java" "../block-battle-amsully/JavaBot/bot/BotStarter.java" 2>err.txt 1>out.txt
