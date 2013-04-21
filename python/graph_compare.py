from igraph import *

num_gs_only = 0;        # gs = Google Sets
num_shared = 0;
num_not_gs = 0;

g_name = raw_input("Google sets graph: ")

g = Graph.Read_GML(g_name);

h_name = raw_input("Amazon graph: ")

h = Graph.Read_GML(h_name);

for v in g.vs:
        query = v["label"]
        query = query.title().replace(" ", "_")
        set = h.vs.select(label=query)
        if len(set) == 0:
                num_gs_only += 1
        else:
                num_shared += 1

num_not_gs = len(h.vs) - num_shared

print "Num gs only: " + str(num_gs_only)
print "Num shared: " + str(num_shared)
print "Num not gs: " + str(num_not_gs)

p1 = float(num_shared) / float(len(h.vs))

print "Fraction shared: " + str(p1)


