#!/bin/bash

cd ..
cd blockbattle-engine
javac -d bin/ `find ./ -name '*.java' -regex '^[./A-Za-z0-9]*$'`

java -cp bin com.theaigames.blockbattle.Blockbattle "python ../block-battle-amsully/BotRun.py" "python ../block-battle-amsully/BotRun.py" 2>err.txt 1>out.txt
