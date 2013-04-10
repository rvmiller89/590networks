package graph;

import org.jgrapht.*;
import org.jgrapht.graph.*;
import java.util.*;
import java.io.*;

public class GraphGenerator 
{
    private int k;
    private UndirectedGraph<String, DefaultEdge> graph;
    private LinkedList<String> seedQueue;
    private String sourceWord, targetWord;
    private boolean foundTargetWord;
    
    /*
     * a statically allocated String that will act as an indicator
     * to when we have completed examining all the words for a given
     * seed word as seed words themselves
     * 
     * i.e. this is a marker that delimits depth
     */
    private static final String DEPTH_TOKEN = "DEPTH_TOKEN";
    
    /*
     * output to python directory
     */
    private static final String OUTPUT_FILENAME = "output.gml";
    
    public GraphGenerator(String sourceWord, String targetWord, int k)
    {
        graph = new SimpleGraph<String, DefaultEdge>(DefaultEdge.class);
        seedQueue = new LinkedList<String>();
        this.k = k;
        this.sourceWord = sourceWord;
        this.targetWord = targetWord;
        foundTargetWord = false;
        
        //start queue off with the DEPTH_TOKEN
        seedQueue.addLast(DEPTH_TOKEN);
    }
    
    public void addWords(String seed, List<String> words)
    {
        if (graph.addVertex(seed))
        {
            System.out.println("Adding " + seed + " as seed");
        }
        
        else
        {
            System.out.println("Not adding seed " + seed + " as vertex because it already exists");
        }
        
        for (String word : words)
        {
            //if the word does not already exist in the graph
            if (graph.addVertex(word))
            {
                //add the result word to the queue seed words
                seedQueue.addLast(word);
                
                System.out.println("Queued and inserted " + word);
            }
            
            else
            {
                System.out.println("Not inserting or queueing " + word);
            }
            
            //if a result word for a seed is ever the seed itself,
            //we do not add an edge from it to the seed, which would 
            //be a self-loop
            if (!seed.equals(word))
            {
                graph.addEdge(seed, word);
            }
            
            else
            {
                System.out.println("Found word == seed: " + word);
            }
            
            //once we find the target word, we do not continue checking for it;
            //we are done
            if (!foundTargetWord && word.equalsIgnoreCase(targetWord))
            {
                System.out.println("Found target word");
                
                foundTargetWord = true;
            }
        }
    }
    
    public String pickNewSeed() throws NoSuchElementException
    {
        //dequeue the next seed word
        String newSeed = seedQueue.removeFirst();
        
        //this is a "while" because of the off chance that
        //a seed word results in no result words...which is
        //unlikely
        //but if it happens, we keep popping off the DEPTH_TOKEN
        //and decrementing the depth (because it still counts as depth,
        //this allows for the program to terminate if the UI ever messes
        //up) until we reach a valid word...or we throw an exception
        //if we unexpectedly run out of words...which shouldn't happen either
        while (newSeed == DEPTH_TOKEN)
        {
            System.out.println("Found DEPTH_TOKEN, moving to next depth");
            
            //queue the DEPTH_TOKEN to indicate the next depth
            seedQueue.addLast(DEPTH_TOKEN);
            
            k--;
            
            System.out.println("k = " + k);
            
            //dequeue the next seed word
            newSeed = seedQueue.removeFirst();
        }
        
        return newSeed;
    }
    
    public void outputGraph() throws IOException
    {
        System.out.print("Generating graph...");
        
        BufferedWriter writer = new BufferedWriter(new FileWriter(OUTPUT_FILENAME));
        int id = 0;
        Map<String, Integer> map = new HashMap<String, Integer>();
        StringBuilder sb = new StringBuilder();
        String color;
        
        /*
         * outputs the graph we generated in GML format
         * 
         * intended to analyzed and visualized using igraph and Cairo
         */
        
        sb.append("graph\n");
        sb.append("[\n");
        sb.append("  directed 0\n");
        
        for (String v : graph.vertexSet())
        {
            map.put(v, id);
            
            color = v.equals(sourceWord) || v.equals(targetWord) ? "red" : "blue";
            
            sb.append("  node\n");
            sb.append("  [\n");
            sb.append("    id " + id + "\n");
            sb.append("    label \"" + v + "\"\n");
            sb.append("    color \"" + color + "\"\n");
            sb.append("    label_dist 2\n");
            sb.append("  ]\n");
            
            id++;
        }
        
        for (DefaultEdge e : graph.edgeSet())
        {
            sb.append("  edge\n");
            sb.append("  [\n");
            sb.append("    source " + map.get(graph.getEdgeSource(e)) + "\n");
            sb.append("    target " + map.get(graph.getEdgeTarget(e)) + "\n");
            sb.append("  ]\n");
        }
        
        sb.append("]\n");
        
        System.out.println("done");
        
        System.out.print("Printing graph...");
        
        writer.write(sb.toString());
        writer.close();
        
        System.out.println("done (" + OUTPUT_FILENAME + ")");
    }
    
    public boolean isDone()
    {
        //we are only done when we have passed our predefined depth or found
        //the target word
        return k <= 0 || foundTargetWord;
    }

    public static void main(String[] args) throws IOException 
    {
        GraphGenerator g = new GraphGenerator("car", "oil", 3);
        List<String> words = new ArrayList<String>();
        String seed = "car";
        
        words.add("engine");
        words.add("tire");
        words.add("gas");
        
        g.addWords(seed, words);
        words.clear();
        
        System.out.println("isDone()? " + g.isDone());
        
        seed = g.pickNewSeed();
        
        words.add("gas");
        words.add("oil");
        
        g.addWords(seed, words);
        words.clear();
        
        System.out.println("isDone()? " + g.isDone());
        
        g.outputGraph();
        
        System.out.println(g.graph);
    }

}
