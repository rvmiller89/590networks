package google_spreadsheet;

import com.google.gdata.client.spreadsheet.*;
import com.google.gdata.data.spreadsheet.*;
import com.google.gdata.util.*;

import java.io.IOException;
import java.net.*;
import java.util.*;

public class SpreadsheetAccess {
	
	private static long iteration = 0;
	
	private static final String USERNAME = "networksdummy@gmail.com";
	private static final String PASSWORD = "590networks";
	private static final String SPREADSHEET = "ss1";
	private static String SEED_WORD = "";
	private static final int TIMEOUT = 1500;	// in ms
	
	public static void main(String[] args)
	      throws AuthenticationException, MalformedURLException, IOException, ServiceException, URISyntaxException {

		Scanner keyboardScanner = new Scanner(System.in);

		System.out.println("CONFIG: USERNAME=" + USERNAME + ",SPREADSHEET=" + SPREADSHEET);
		System.out.println("------------------------------------------------------");

		SpreadsheetService service = new SpreadsheetService("SpreadsheetAccess");
		service.setUserCredentials(USERNAME, PASSWORD);

		// Define the URL to request.  This should never change.
	    URL SPREADSHEET_FEED_URL = new URL(
	        "https://spreadsheets.google.com/feeds/spreadsheets/private/full");

	    // Make a request to the API and get all spreadsheets.
	    System.out.println("PRINTING ALL SPREADSHEETS FOR ACCOUNT: " + USERNAME);
	    SpreadsheetFeed feed = service.getFeed(SPREADSHEET_FEED_URL, SpreadsheetFeed.class);
	    List<SpreadsheetEntry> spreadsheets = feed.getEntries();
	    SpreadsheetEntry ss = null;
	    
	    for (SpreadsheetEntry spreadsheet : spreadsheets) {
	      // Print the title of this spreadsheet to the screen
	      System.out.println("\t" + spreadsheet.getTitle().getPlainText());
	      if (spreadsheet.getTitle().getPlainText().equals(SPREADSHEET))
	    	  ss = spreadsheet;
	    }
	    
	    if (ss == null)	{
	    	System.err.println("ERROR: SPREADSHEET NOT FOUND FOR ACCOUNT: " + SPREADSHEET);
	    	System.exit(1);
	    }
	    
	    System.out.println("PRINTING ALL WORKSHEETS FOR SPREADSHEET: " + SPREADSHEET);

	    // Make a request to the API to fetch information about all
	    // worksheets in the spreadsheet.
	    List<WorksheetEntry> worksheets = ss.getWorksheets();
	    WorksheetEntry ws = null;
	    

	    // Iterate through each worksheet in the spreadsheet.
	    for (WorksheetEntry worksheet : worksheets) {
	    	// Get the worksheet's title, row count, and column count.
	    	String title = worksheet.getTitle().getPlainText();
	    	int rowCount = worksheet.getRowCount();
	    	int colCount = worksheet.getColCount();

	    	// Print the fetched information to the screen for this worksheet.
	    	System.out.println("\t" + title + "- rows:" + rowCount + " cols: " + colCount);
	    	if (ws == null)
	    		ws = worksheet;
	    }
	    
	    if (ws == null)	{
	    	System.err.println("ERROR: WORKSHEET NOT FOUND FOR SPREADSHEET: " + SPREADSHEET);
    		System.exit(1);
    	}	
	
	    System.out.println("CHOOSING THE FIRST WORKSHEET: " + ws.getTitle().getPlainText());
	    
	    
	    if (SEED_WORD.equals(""))	{
	    	System.out.print("ENTER SEED WORD: ");
	    	SEED_WORD = keyboardScanner.nextLine();
	    }
	    
	    while (true)	{
	    	
	    	// Main Loop: 	(0) Clear worksheet
	    	//				(1) Insert SEED_WORD
	    	// 				(2) Wait TIMEOUT for robot to generate new words
	    	//				(3) Read column of words and give to Azfar's thing
	    	//				(4) Wait for Azfar to provide new SEED_WORD
	    	//				(4) Repeat
	    	
	    	System.out.println("======= ITERATION " + iteration + " ========");
	    	iteration++;
	    	
	    	System.out.print("CLEARING WORKSHEET...");
		    clearWorksheet(ws, service);
		    System.out.println("done");
	    
		    System.out.print("INSERTING SEED WORD '" + SEED_WORD + "' IN A1...");
		    if (insertWordInA1(SEED_WORD, ws, service))
		    	System.out.println("done");
		    else	{
		    	System.err.println("ERROR: UNABLE TO INSERT SEED WORD");
		    	System.exit(1);
		    }
		    
		    System.out.print("Waiting " + TIMEOUT + "ms FOR ROBOT TO GENERATE NEW WORDS...");
		    try {
				Thread.sleep(TIMEOUT);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		    System.out.println("done");
		    
		    System.out.println("FETCHING EVERY ROW IN FIRST COLUMN:");
		    // TODO give to Azfar
		    List<String> values = fetchFirstColumn(ws, service);

	    }
	}

	private static boolean clearWorksheet(WorksheetEntry ws, SpreadsheetService service) 
			throws IOException, ServiceException	{
		// Fetch the cell feed of the worksheet.
	    URL cellFeedUrl = ws.getCellFeedUrl();
	    CellFeed cellFeed = service.getFeed(cellFeedUrl, CellFeed.class);

	    // Iterate through each cell
	    for (CellEntry cell : cellFeed.getEntries()) {
	        cell.changeInputValueLocal("");
	        cell.update();
	    }
	    return true;
	}
	
	public static boolean insertWordInA1(String word, WorksheetEntry ws, SpreadsheetService service) 
			throws IOException, ServiceException	{
	    
	    // Fetch the cell feed of the worksheet.
	    URL cellFeedUrl = ws.getCellFeedUrl();
	    CellFeed cellFeed = service.getFeed(cellFeedUrl, CellFeed.class);
	    CellEntry cell = new CellEntry(1, 1, word);
	    cellFeed.insert(cell);	    

		return true;
	}
	
	public static List<String> fetchFirstColumn(WorksheetEntry ws, SpreadsheetService service) 
			throws URISyntaxException, IOException, ServiceException	{
		List<String> values = new ArrayList<String>();
	    URL firstRowCellFeedUrl = new URI(ws.getCellFeedUrl().toString() + "?min-row=1&min-col=1&max-col=1").toURL();
	    CellFeed firstRowCellFeed = service.getFeed(firstRowCellFeedUrl, CellFeed.class);

	    // Iterate through each cell, printing its value.
	    for (CellEntry cell : firstRowCellFeed.getEntries()) {
	    	// Print the cell's address in R1C1 notation
	    	//System.out.print(cell.getId().substring(cell.getId().lastIndexOf('/') + 1) + "\t");

	    	// Print the cell's displayed value (useful if the cell has a formula)
	    	System.out.println("\t" + cell.getCell().getValue());
	    	values.add(cell.getCell().getValue());
	    }
		return values;
	}
}
