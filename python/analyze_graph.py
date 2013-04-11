#-------------------------------------------------------------------------------
# Name:        graph_plot
# Purpose:
#
# Author:      Azfar Khandoker
#
# Created:     10/04/2013
# Copyright:   (c) Azfar Khandoker 2013
# Licence:     <your licence>
#-------------------------------------------------------------------------------

from igraph import *

filename = raw_input("filename: ")

g = Graph.Read_GML(filename)

summary(g)

print("\naverage path length = " + str(g.average_path_length()))

seedWord = raw_input("seed word: ")
targetWord = raw_input("target word: ")

a = g.vs.select(label_eq=seedWord)[0]
b = g.vs.select(label_eq=targetWord)[0]

path = g.get_all_shortest_paths(a,to=b)[0]

for i in path:
    print "%d: %s" % (i, g.vs[i]["label"])

print "Length: %d" % len(path)

#plot(g, "output.png", margin = 50)
