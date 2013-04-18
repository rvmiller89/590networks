#-------------------------------------------------------------------------------
# Name:        edge_list_to_gml
# Purpose:
#
# Author:      Azfar Khandoker
#
# Created:     17/04/2013
# Copyright:   (c) Azfar Khandoker 2013
# Licence:     <your licence>
#-------------------------------------------------------------------------------

from igraph import *

#read in the edge list
#have to use explicit file handlers to ensure
#data is read properly...otherwise igraph closes
#the file and flushes it when it pleases...messing
#up other scripts in the pipeline
f = open('edge-list.txt', 'r')

g = Graph.Read_Edgelist(f)

#no more need to read from file
f.close()

f = open("vertex-labels.txt", 'r')

#delete any double quotes that appear
#in the labels of the verticies
#double quotes mess things up
labels = []
for line in f:
    labels.append(line[:-1].split(' ', 1)[1].replace('"', ''))

f.close()

g.vs["label"] = labels

#transform the graph from edge list format to
#GML format with labels, again using explicit file handles
f = open('amazon.gml', 'w')

g.write_gml(f)

f.close()