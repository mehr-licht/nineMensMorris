#!/bin/bash

if [ ! -d "results" ]; then
    mkdir results
fi

for file in data/d*
    do 
        text=$(tail -n 3 $file)
        printf "$text \n\n=========\n\n" >> results/$(basename ${file%[*}results);
done
