#!/bin/bash

if [ ! -d "CSV" ]; then
    mkdir CSV
fi

for file in results/d[0-9]*
    do sed -n -e "/winner/p;/completed/p;/plays/p" $file | sed -e "s/1 games completed in: //" | sed -e "s/ ms/,/" | sed -e "s/number of plays: //" | sed -e "s/winner : /,/" | sed -n '{N; N; s/\n//g; p;}' | sed -r "s/([0-9]*)(,*)([0-9]*)(,*)([DOX])/\5\4\3\2\1/g" | sed -e "1i\winner\,plays\,time" >> CSV/$(basename ${file%-*}.csv);

done
