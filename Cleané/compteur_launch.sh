#!/bin/bash

java Compteur -1
echo "Lancement de $1 clients qui incrémente $2 fois"
sleep 1
for ((i = 0; i < $1; i += 1)) do
  java Compteur $2 &
done

echo "sleeping 15s"
sleep 15
echo "end sleep"
java CompteurRead
killall java
