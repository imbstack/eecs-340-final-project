cat data.txt | python parseData.py | sort -n > parsedData.txt
./gpthis.pl -c parsedData.txt
gnuplot < graph.commands > graph.ps
