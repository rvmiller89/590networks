package controller;

import java.io.IOException;
import java.util.List;
import java.util.Scanner;

import com.google.gdata.util.ServiceException;

import google_spreadsheet.SpreadsheetAccess;
import robot.SpreadsheetRobot;

public class Controller {

	
	
	private static long iteration = 0;

	public static void main(String[] args) {
		Scanner keyboardScanner = new Scanner(System.in);
		System.out.print("ENTER SEED WORD: ");
        String seedWord = keyboardScanner.nextLine();
		
        SpreadsheetAccess spreadsheetAccess = null;
		try {
			spreadsheetAccess = new SpreadsheetAccess();
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		SpreadsheetRobot r = new SpreadsheetRobot();
        
        // TODO make this able to stop...
        while (true)	{
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
            // robot.populateWords()
            
            List<String> firstCol = null;
            try	{
            	firstCol = spreadsheetAccess.fetchFirstColumn();
            }
            catch (Exception e)	{
            	e.printStackTrace();
            }
            
            // TODO use firstCol to call addWords
            // graph.addWords(firstCol)
            
            // TODO pick new seedWord before repeating with pickNewSeed()
    		// seedWord = graph.pickNewSeed()
        }
        
	}

}
