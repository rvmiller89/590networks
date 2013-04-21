#!/bin/usr/python

from igraph import *

print "Filename: "
file = raw_input()

g = Graph.Read_GML(file)
print g.summary()

n = g.degree_distribution().n

f = open(file + "_deg_dist.csv", 'w')
for left, right, count in g.degree_distribution().bins():
	print >>f, "%d,%f" % (left, float(float(count)/n))
f.close()
