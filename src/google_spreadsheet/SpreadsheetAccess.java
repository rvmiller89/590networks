package google_spreadsheet;

import com.google.gdata.client.spreadsheet.*;
import com.google.gdata.data.Link;
import com.google.gdata.data.batch.BatchOperationType;
import com.google.gdata.data.batch.BatchStatus;
import com.google.gdata.data.batch.BatchUtils;
import com.google.gdata.data.spreadsheet.*;
import com.google.gdata.util.*;

import java.io.IOException;
import java.net.*;
import java.util.*;

public class SpreadsheetAccess {
	
	private static final String USERNAME = "networksdummy@gmail.com";
	private static final String PASSWORD = "590networks";
	private static final String SPREADSHEET = "ss3";
	private static final int 	MAX_ROWS = 80;		// Needed for batch clear worksheet

	private SpreadsheetEntry ss;
	private WorksheetEntry ws;
	private SpreadsheetService service;
	
	/*
	 * Instantiates a connection to Google Spreadsheet API using connection details above
	 * 
	 * Use this connection for methods below
	 */
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

	/*
	 * Clears the current worksheet (removes values from all cells in CellFeed)
	 * 
	 * Updated for batch clear of first column
	 */
	public void clearWorksheet() throws IOException, ServiceException	{
		System.out.print("CLEARING WORKSHEET...");
		// Fetch the cell feed of the worksheet.
	    URL cellFeedUrl = this.ws.getCellFeedUrl();
	    CellFeed cellFeed = this.service.getFeed(cellFeedUrl, CellFeed.class);

	    List<CellAddress> cellAddrs = new ArrayList<CellAddress>();
	    for (int row = 1; row < MAX_ROWS; row++){
	    	cellAddrs.add(new CellAddress(row, 1));		// Each row in first column
	    }

	    Map<String, CellEntry> cellEntries = getCellEntryMap(this.service, cellFeedUrl, cellAddrs);

	    CellFeed batchRequest = new CellFeed();
	    for (CellAddress cellAddr : cellAddrs) {
	    	URL entryUrl = new URL(cellFeedUrl.toString() + "/" + cellAddr.idString);
	    	CellEntry batchEntry = new CellEntry(cellEntries.get(cellAddr.idString));
	    	batchEntry.changeInputValueLocal("");	// Empty string
	    	BatchUtils.setBatchId(batchEntry, cellAddr.idString);
	    	BatchUtils.setBatchOperationType(batchEntry, BatchOperationType.UPDATE);
	    	batchRequest.getEntries().add(batchEntry);
	    }

	    // Submit the update
	    Link batchLink = cellFeed.getLink(Link.Rel.FEED_BATCH, Link.Type.ATOM);
	    service.setHeader("If-Match", "*");
	    CellFeed batchResponse = service.batch(new URL(batchLink.getHref()), batchRequest);
	    service.setHeader("If-Match", null);
	    
	    // Check the results
	    boolean isSuccess = true;
	    for (CellEntry entry : batchResponse.getEntries()) {
	      String batchId = BatchUtils.getBatchId(entry);
	      if (!BatchUtils.isSuccess(entry)) {
	        isSuccess = false;
	        BatchStatus status = BatchUtils.getBatchStatus(entry);
	      }
	    }
	    if (isSuccess)
	    	System.out.println("done");
	    else	{
	    	System.err.println("ERROR: unable to clear worksheet");
	    	return;
	    }
	    	
	}
	
	/* 
	 * Inserts word, seedWord, into position A1 (first column, first row)
	 */
	public void insertWordInA1(String seedWord) throws IOException, ServiceException	{
		System.out.print("INSERTING SEED WORD '" + seedWord + "' IN A1...");
	    // Fetch the cell feed of the worksheet.
	    URL cellFeedUrl = ws.getCellFeedUrl();
	    CellFeed cellFeed = service.getFeed(cellFeedUrl, CellFeed.class);
	    CellEntry cell = new CellEntry(1, 1, seedWord);
	    cellFeed.insert(cell);	    
	    System.out.println("done");
	}
	
	/*
	 * Fetches non-empty words from column A (first column) and returns as a List<String>
	 */
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
	
	public static Map<String, CellEntry> getCellEntryMap(
			SpreadsheetService ssSvc, URL cellFeedUrl, List<CellAddress> cellAddrs)
			throws IOException, ServiceException {
		CellFeed batchRequest = new CellFeed();
		for (CellAddress cellId : cellAddrs) {
			CellEntry batchEntry = new CellEntry(cellId.row, cellId.col, cellId.idString);
			batchEntry.setId(String.format("%s/%s", cellFeedUrl.toString(), cellId.idString));
			BatchUtils.setBatchId(batchEntry, cellId.idString);
			BatchUtils.setBatchOperationType(batchEntry, BatchOperationType.QUERY);
			batchRequest.getEntries().add(batchEntry);
		}

		CellFeed cellFeed = ssSvc.getFeed(cellFeedUrl, CellFeed.class);
		CellFeed queryBatchResponse =
				ssSvc.batch(new URL(cellFeed.getLink(Link.Rel.FEED_BATCH, Link.Type.ATOM).getHref()),
              batchRequest);

		Map<String, CellEntry> cellEntryMap = new HashMap<String, CellEntry>(cellAddrs.size());
		for (CellEntry entry : queryBatchResponse.getEntries()) {
			cellEntryMap.put(BatchUtils.getBatchId(entry), entry);
			//System.out.printf("batch %s {CellEntry: id=%s editLink=%s inputValue=%s\n",
			///BatchUtils.getBatchId(entry), entry.getId(), entry.getEditLink().getHref(),
			//entry.getCell().getInputValue());
		}

		return cellEntryMap;
	}
	
	/*
	 * Useful for testing
	 */
	public static void main(String[] args)	{
		SpreadsheetAccess spreadsheetAccess = null;
		try {
			spreadsheetAccess = new SpreadsheetAccess();
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		try {
			spreadsheetAccess.clearWorksheet();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ServiceException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	private static class CellAddress {
	    public final int row;
	    public final int col;
	    public final String idString;

	    /**
	     * Constructs a CellAddress representing the specified {@code row} and
	     * {@code col}.  The idString will be set in 'RnCn' notation.
	     */
	    public CellAddress(int row, int col) {
	      this.row = row;
	      this.col = col;
	      this.idString = String.format("R%sC%s", row, col);
	    }
	}
}
