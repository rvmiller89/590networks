#-------------------------------------------------------------------------------
# Name:        wiki_generator
# Purpose:     Generates subgraphs with k = 4 for the given seed words
#
# Author:      Ryan Miller
#
# Created:     17/04/2013
# Copyright:   (c) Not Azfar Khandoker 2013
# Licence:     <your licence>
#-------------------------------------------------------------------------------

from igraph import *
import re

seeds = [
    'Las_Vegas',
    'The_Beatles',
    'Oreo',
    'Zebra',
    'Sony'
]

k = 4

print "Reading edge list"

#read the GML data into memory
g = Graph.Read_Edgelist("/homes/millerrv/scratch/links_el_final.txt")

print "Done"
print "Combining data with titles"

#combine data with "title" attribute
titles = []
f = open("/homes/millerrv/scratch/titles-sorted.txt", 'r') 
for line in f:
	titles.append(line.rstrip())
g.vs["title"] = titles

print "Done"

for seed in seeds:
	print "Making subgraph for seed: " + seed
	count = 0
	vertex = g.vs.select(title=seed)[0]	
	neighborhood = g.neighborhood(vertex, k)

	subgraph = g.subgraph(neighborhood)

	new_file = open("/homes/millerrv/scratch/" + seed + "_" + str(count) + ".gml", 'w')
	subgraph.write_gml(new_file)
	new_file.close()