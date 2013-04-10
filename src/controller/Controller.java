package controller;

import java.awt.AWTException;
import java.io.IOException;
import java.util.List;
import java.util.Scanner;

import com.google.gdata.util.ServiceException;

import google_spreadsheet.SpreadsheetAccess;
import graph.GraphGenerator;
import robot.SpreadsheetRobot;

public class Controller {
	private static long iteration = 0;

	public static void main(String[] args) {
		Scanner keyboardScanner = new Scanner(System.in);
		System.out.print("ENTER SEED WORD: ");
        String seedWord = keyboardScanner.nextLine();
        System.out.print("ENTER TARGET WORD: ");
        String targetWord = keyboardScanner.nextLine();
        System.out.print("ENTER DEPTH: ");
        int k = keyboardScanner.nextInt();
        
        keyboardScanner.close();
		
        SpreadsheetAccess spreadsheetAccess = null;
		try {
			spreadsheetAccess = new SpreadsheetAccess();
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		SpreadsheetRobot r = null;
        try {
            r = new SpreadsheetRobot();
        } catch (AWTException e1) {
            e1.printStackTrace();
        }
        
        GraphGenerator graph = new GraphGenerator(seedWord, targetWord, k);
        
        while (!graph.isDone())	{
        	System.out.println("======= ITERATION " + iteration + " ========");
            iteration++;

            try {
				spreadsheetAccess.clearWorksheet();
			} catch (Exception e) {
				e.printStackTrace();
			}
            try	{
            	spreadsheetAccess.insertWordInA1(seedWord);
	        } catch (Exception e) {
				e.printStackTrace();
			}
            
            r.populateWords();
            
            List<String> firstCol = null;
            try	{
            	firstCol = spreadsheetAccess.fetchFirstColumn();
            }
            catch (Exception e)	{
            	e.printStackTrace();
            }
            
            graph.addWords(seedWord, firstCol);
            
            seedWord = graph.pickNewSeed();
        }
        
        try {
            graph.outputGraph();
        } catch (IOException e) {
            e.printStackTrace();
        }        
	}

}
