#-------------------------------------------------------------------------------
# Name:        analyze_graph
# Purpose:
#
# Author:      Azfar Khandoker
#
# Created:     10/04/2013
# Copyright:   (c) Azfar Khandoker 2013
# Licence:     <your licence>
#-------------------------------------------------------------------------------

from igraph import *

#this should be *JUST* the filename, not the path to it
#for example, input would be: "apple.orange.gml"
filename = raw_input("filename: ")

#the graphs should be located in the graphs/
#directory, which is one level above this
#working directory
file_to_read = "../graphs/" + filename

print ("reading " + file_to_read)

g = Graph.Read_GML(file_to_read)

summary(g)

print("\naverage path length = " + str(g.average_path_length()))

#this should gives us an array of
#3 elements with the 0th element being
#the seed word, the 1th element being the
#target word and the 2th element being "gml"
result = filename.split('.')

seedWord = result[0]
targetWord = result[1]

print ("seed word = \"" + seedWord + "\"")
print ("target word = \"" + targetWord + "\"")

a = g.vs.select(label_eq=seedWord)[0]
b = g.vs.select(label_eq=targetWord)[0]

path = g.get_all_shortest_paths(a,to=b)[0]

for i in path:
    print "%d: %s" % (i, g.vs[i]["label"])

print "Length: %d" % len(path)

#plot(g, "output.png", margin = 50)
