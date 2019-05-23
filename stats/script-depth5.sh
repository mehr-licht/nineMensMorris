#!/bin/bash

if [ ! -d "data" ]; then
    mkdir data
fi

depth=5

for i in {2..6}
    do for j in {0..9}
        do java -cp ../out/production/NineMensMorris/ NineMensMorris $depth 1 $i 1 > data/d$depth-A1A"$i"-[$j]
    done
done

for i in {2..6}
    do for j in {0..9}
        do java -cp ../out/production/NineMensMorris/ NineMensMorris $depth $i 1 1 > data/d$depth-A"$i"A1-[$j]
    done
done
