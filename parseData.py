import sys
data = {}
def removeMin(x):
	themin = 1000000
	for i in x:
		if i < themin:
			themin = i
	y = []
	hasremoved = 0
	for i in x:
		if i != themin or hasremoved == 1:
			y.append(int(i))
		else:
			hasremoved = 1
	return y

def removeMax(x):
	themin = 0
	for i in x:
		if i > themin:
			themin = i
	y = []
	hasremoved = 0
	for i in x:
		if i != themin or hasremoved == 1:
			y.append(int(i))
		else:
			hasremoved = 1
	return y

def average(x):
	return float(sum(x))/len(x)

for l in sys.stdin:
	j = l.split()
	if j[0] not in data:
		data[j[0]] = []
	data[j[0]].append(j[1])

for i,j in data.iteritems():
	#data = [int(k) for k in j]
	data = removeMin(removeMin(removeMin(removeMax(removeMax(removeMax(j))))))
	print str(i) + " " + str(round(average(data))) + " " + str(min(data)) + " " + str(max(data)) 
