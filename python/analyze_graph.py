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

g = Graph.Read_GML("../output.gml")

summary(g)

print("\naverage path length = " + str(g.average_path_length()))

#plot(g, "output.png", margin = 50)
