def cosine_similarity(g,i,j):
	adj = g.get_adjacency()
	n = 0.0
	for k in range(adj.shape[0]):
		n = n + (adj[i,k] * adj[k,j])
	sim = n / math.sqrt(g.vs[i].degree() * g.vs[j].degree())
	return sim

# warning: takes a long time
def all_cosine_similarity(g):
  list = []
  for i in range(len(g.vs)):
    for j in range(len(g.vs)):
      list.append([i,j,cosine_similarity(g,i,j)])
  return list
