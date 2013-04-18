#original Amazon data from
#http://snap.stanford.edu/data/bigdata/amazon/amazon-meta.txt.gz
f = open("amazon-meta.txt", "r")

#the ASIN id
count = 0

#dictionary associates an ASIN with
#its title and similar products
d = dict()

#list to keep the ASINs ordered according to
#order in which they were encountered
l = []

for line in f:
    #seen line starting with 'ASIN'
    #check next line for 'title'
    if line.startswith("ASIN"):
        ASIN = line[6:-1]
        continue

    #seen line starting with 'title'
    #check next line for 'similar'
    elif line.startswith("  title"):
        title = line[9:-1]
        continue

    #once we have seen a line beginning with
    #'similar', we must have seen the proper
    #ASIN and title of the corresponding product
    #therefore, we do execute the remainder of the loop
    elif line.startswith("  similar"):
        similar = line[12:-1].split('  ')[1:]

    #ignore any other lines, read next line
    else:
        continue

    l.append(ASIN)

    #add ASIN entry to dictionary with its information
    d[ASIN] = [count, title, similar]

    count += 1

f.close()

f1 = open("edge-list.txt", "w")
f2 = open("vertex-labels.txt", "w")

for asin in l:
    #get the ID for this ASIN
    asinID = str(d[asin][0])

    #write the edge list for this data in igraph-friendly format
    #only add an entry to the edge list if the source and target
    #of the edge is within this data set
    for similar in d[asin][2]:
        if similar in d:
            f1.write(asinID + ' ' + str(d[similar][0]) + '\n')

    #this file will associate each vertex ID with its 'label'
    #attribute
    f2.write(asinID + ' ' + str(d[asin][1]) + '\n')

f1.close()
f2.close()

