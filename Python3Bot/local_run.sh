#!/bin/bash

cd ../..
cd blockbattle-engine
javac -d bin/ `find ./ -name '*.java' -regex '^[./A-Za-z0-9]*$'`

java -cp bin com.theaigames.blockbattle.Blockbattle "python3 ../block-battle-amsully/Python3Bot/BotRun.py" "python3 ../block-battle-amsully/Python3Bot/BotRun.py" 2>err.txt 1>out.txt
