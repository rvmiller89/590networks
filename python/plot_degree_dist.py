from igraph import *
import matplotlib.pyplot as plt

plt.figure()

g = Graph.Read_GML(raw_input())

dd = g.degree_distribution()

xs, ys = zip(*[(left, count / float(dd.n)) for left, _, count in dd.bins()])

plt.xscale('log')
plt.yscale('log')

plt.title(r"$\mathrm{"+name+"\ Degree\ Distribution}$")
plt.xlabel(r"$\mathrm{Degree}$")
plt.ylabel(r"$\mathrm{Probability}$")

plt.plot(xs, ys)

plt.savefig("plot.png")
