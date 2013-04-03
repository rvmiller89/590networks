package google_spreadsheet;

import com.google.gdata.client.authn.oauth.*;
import com.google.gdata.client.spreadsheet.*;
import com.google.gdata.data.*;
import com.google.gdata.data.batch.*;
import com.google.gdata.data.spreadsheet.*;
import com.google.gdata.util.*;

import java.io.IOException;
import java.net.*;
import java.util.*;

public class SpreadsheetAccess {
	public static void main(String[] args)
	      throws AuthenticationException, MalformedURLException, IOException, ServiceException, URISyntaxException {

		String USERNAME = "networksdummy@gmail.com";
		String PASSWORD = "590networks";
		String SPREADSHEET = "ss1";
		
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
	    
	    
	    System.out.println("FETCHING EVERY ROW IN FIRST COLUMN:");
	    
	    List<String> values = fetchFirstColumn(ws, service);


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
