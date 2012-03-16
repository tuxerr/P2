#!/bin/zsh

java Compteur -1
echo "Lancement de $1 clients qui incrémente $2 fois"

a=$1

for i in {1..$a}; do
  echo "java Compteur $2"
  java Compteur $2 &
done

echo "sleeping 4s"
sleep 8
echo "end sleep"
java CompteurRead
killall java
