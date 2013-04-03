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
	      throws AuthenticationException, MalformedURLException, IOException, ServiceException {

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
	    // Iterate through all of the spreadsheets returned
	    
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

	    // Iterate through each worksheet in the spreadsheet.
	    for (WorksheetEntry worksheet : worksheets) {
	    	// Get the worksheet's title, row count, and column count.
	    	String title = worksheet.getTitle().getPlainText();
	    	int rowCount = worksheet.getRowCount();
	    	int colCount = worksheet.getColCount();

	    	// Print the fetched information to the screen for this worksheet.
	    	System.out.println("\t" + title + "- rows:" + rowCount + " cols: " + colCount);
	    }

	}
}
