package google_spreadsheet;

import com.google.gdata.client.spreadsheet.*;
import com.google.gdata.data.spreadsheet.*;
import com.google.gdata.util.*;

import java.io.IOException;
import java.net.*;
import java.util.*;

public class SpreadsheetAccess {
	
	private static final String USERNAME = "networksdummy@gmail.com";
	private static final String PASSWORD = "590networks";
	private static final String SPREADSHEET = "ss1";

	private SpreadsheetEntry ss;
	private WorksheetEntry ws;
	private SpreadsheetService service;
	
	public SpreadsheetAccess() throws IOException, ServiceException	{
		System.out.println("CONFIG: USERNAME=" + USERNAME + ",SPREADSHEET=" + SPREADSHEET);
		System.out.println("------------------------------------------------------");
		this.service = new SpreadsheetService("SpreadsheetAccess");
		this.service.setUserCredentials(USERNAME, PASSWORD);

		// Define the URL to request.  This should never change.
	    URL SPREADSHEET_FEED_URL = new URL(
	        "https://spreadsheets.google.com/feeds/spreadsheets/private/full");

	    // Make a request to the API and get all spreadsheets.
	    System.out.println("PRINTING ALL SPREADSHEETS FOR ACCOUNT: " + USERNAME);
	    SpreadsheetFeed feed = service.getFeed(SPREADSHEET_FEED_URL, SpreadsheetFeed.class);
	    List<SpreadsheetEntry> spreadsheets = feed.getEntries();
	    this.ss = null;
	    
	    for (SpreadsheetEntry spreadsheet : spreadsheets) {
	      // Print the title of this spreadsheet to the screen
	      System.out.println("\t" + spreadsheet.getTitle().getPlainText());
	      if (spreadsheet.getTitle().getPlainText().equals(SPREADSHEET))
	    	  this.ss = spreadsheet;
	    }
	    
	    if (this.ss == null)	{
	    	System.err.println("ERROR: SPREADSHEET NOT FOUND FOR ACCOUNT: " + SPREADSHEET);
	    	return;
	    }
	    
	    System.out.println("PRINTING ALL WORKSHEETS FOR SPREADSHEET: " + SPREADSHEET);

	    // Make a request to the API to fetch information about all
	    // worksheets in the spreadsheet.
	    List<WorksheetEntry> worksheets = ss.getWorksheets();
	    this.ws = null;
	    

	    // Iterate through each worksheet in the spreadsheet.
	    for (WorksheetEntry worksheet : worksheets) {
	    	// Get the worksheet's title, row count, and column count.
	    	String title = worksheet.getTitle().getPlainText();
	    	int rowCount = worksheet.getRowCount();
	    	int colCount = worksheet.getColCount();

	    	// Print the fetched information to the screen for this worksheet.
	    	System.out.println("\t" + title + "- rows:" + rowCount + " cols: " + colCount);
	    	if (this.ws == null)
	    		this.ws = worksheet;
	    }
	    
	    if (this.ws == null)	{
	    	System.err.println("ERROR: WORKSHEET NOT FOUND FOR SPREADSHEET: " + SPREADSHEET);
    		return;
    	}	
	
	    System.out.println("CHOOSING THE FIRST WORKSHEET: " + ws.getTitle().getPlainText());

	}

	public void clearWorksheet() throws IOException, ServiceException	{
		System.out.print("CLEARING WORKSHEET...");
		// Fetch the cell feed of the worksheet.
	    URL cellFeedUrl = this.ws.getCellFeedUrl();
	    CellFeed cellFeed = this.service.getFeed(cellFeedUrl, CellFeed.class);

	    // Iterate through each cell
	    for (CellEntry cell : cellFeed.getEntries()) {
	        cell.changeInputValueLocal("");
	        cell.update();
	    }

	    System.out.println("done");
	}
	
	public void insertWordInA1(String seedWord) throws IOException, ServiceException	{
		System.out.print("INSERTING SEED WORD '" + seedWord + "' IN A1...");
	    // Fetch the cell feed of the worksheet.
	    URL cellFeedUrl = ws.getCellFeedUrl();
	    CellFeed cellFeed = service.getFeed(cellFeedUrl, CellFeed.class);
	    CellEntry cell = new CellEntry(1, 1, seedWord);
	    cellFeed.insert(cell);	    
	    System.out.println("done");
	}
	
	public List<String> fetchFirstColumn() throws URISyntaxException, IOException, ServiceException	{
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
