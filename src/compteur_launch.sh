pid=""
java Compteur -1
echo "Lancement de $1 clients qui incrémentend $2 fois"

for i in {1..$1}; do
  echo "java Compteur $2"
  java Compteur $2 &
done

echo "sleeping 4s"
sleep 4
echo "end sleep"
java CompteurRead
killall java
