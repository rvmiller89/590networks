#-------------------------------------------------------------------------------
# Name:        amazon_generator
# Purpose:
#
# Author:      Azfar Khandoker
#
# Created:     17/04/2013
# Copyright:   (c) Azfar Khandoker 2013
# Licence:     <your licence>
#-------------------------------------------------------------------------------

from igraph import *
import re

seeds = [
    'las vegas',
    'the beatles',
    'oreo',
    'zebra',
    'sony'
]

k = 4

#read the GML data into memory so we do not
#need to keep the GML file open
f = open('amazon.gml', 'r')
g = Graph.Read_GML(f)
f.close()

for seed in seeds:
    count = 0

    #this regular expression allows only the whole word of the
    #seed to be matched
    #in other words, if there is a letter before or after the
    #seed word, we do not count it as the seed word
    #therefore with a seed of 'car', 'card' nor 'scar' will get
    #matched and will be ignored
    regex = re.compile('.*([^a-z]|\A)' + seed + '([^a-z]|$).*')

    for v in g.vs:
        #see if the label of the vertex matches the regex
        #we first transform the label to lower case to avoid
        #the case-sensitivity issue
        if regex.match(v['label'].lower()) != None:

            #returns a subgraph rooted at the matched vertex
            #containing all verticies at most k hops away from it
            #this is called the neighborhood of a vertex in a graph
            neighborhood = g.neighborhood(int(v['id']), k)

            #create a subgraph of the original graph containing
            #all the verticies that are in the neighborhood of the
            #matched vertex
            subgraph = g.subgraph(neighborhood)

            #many verticies may match the regular expression,
            #but not all of them will generate interesting neighborhoods
            #therefore, we only consider those verticies whose
            #neighborhoods will generate subgraphs of the original
            #graph that have diameter at least k
            #since multiple of these such graphs are possible,
            #we use a counter to allow for unique filenames when outputting
            if subgraph.diameter() >= k:
                subgraph.write_gml('output/' + seed + '_' + str(count) + '.gml')
                count += 1

    if count == 0:
        print(seed + 'not found in graph')

