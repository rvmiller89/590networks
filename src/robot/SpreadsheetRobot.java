package robot;

import java.awt.*;
import java.awt.event.*;

public class SpreadsheetRobot 
{
    /*
     * These starting coordinates are known to work on Azfar's
     * computer with screen resolution 1920x1080 with Google
     * Spreadsheet open in Firefox with Firefox in full screen mode
     * 
     * generally, it should work if your browser is in full screen
     * mode
     */
    private static final int START_X = 406;
    private static final int START_Y = 185;

    /*
     * a large number of cells so the robot will scroll past the bottom
     * of the screen on the spreadsheet, allowing for more columns to be filled
     */
    private static final int NUM_CELLS = 40;

    /*
     * the height of a Google Spreadsheet cell as it appears on
     * Azfar's computer...probably in general too
     */
    private static final int CELL_HEIGHT = 18;

    //the Robot
    private Robot r;

    public SpreadsheetRobot() throws AWTException
    {
        r = new Robot();

        //put in an automatic delay so the UI can
        //keep up with the Robot's movements
        r.setAutoDelay(100);
    }
    
    public void reloadPage()
    {
        System.out.print("Refreshing page...");
        
        /*
         * we reload the Google Spreadsheet
         * to avoid any hiccups from Google (or us)
         * 
         */
        
        r.keyPress(KeyEvent.VK_F5);
        r.keyRelease(KeyEvent.VK_F5);
        
        //should gets rid of the "Are you sure?" box
        r.keyPress(KeyEvent.VK_ENTER);
        r.keyRelease(KeyEvent.VK_ENTER);
        
        //wait for page to reload
        r.delay(3000);
        
        System.out.println("done");
    }
    
    public void clearWorksheet()	{
    	//press the CTRL key
    	r.delay(200);
        r.keyPress(KeyEvent.VK_CONTROL);
        r.keyPress(KeyEvent.VK_A);
        r.keyRelease(KeyEvent.VK_CONTROL);
        r.keyRelease(KeyEvent.VK_A);
        
        r.delay(200);
        
        r.keyPress(KeyEvent.VK_DELETE);
        r.keyRelease(KeyEvent.VK_DELETE);
        
        r.delay(200);
   
    }

    public void populateWords()
    {
        System.out.print("Robot working...");
        
        //move to bottom right corner of first spreadsheet cell
        r.mouseMove(START_X, START_Y);
        
        //click
        r.mousePress(InputEvent.BUTTON1_MASK);
        r.mouseRelease(InputEvent.BUTTON1_MASK);

        //press the CTRL key
        r.keyPress(KeyEvent.VK_CONTROL);

        //press left mouse button
        r.mousePress(InputEvent.BUTTON1_MASK);

        //move to the last cell 
        r.mouseMove(START_X, START_Y + getDeltaY());

        //release the left mouse button
        r.mouseRelease(InputEvent.BUTTON1_MASK);

        //release the CTRL key
        r.keyRelease(KeyEvent.VK_CONTROL);
        
        //deselect the first column by
        //clicking somewhere not in the first column
        r.mouseMove(START_X * 2, START_Y);
        r.mousePress(InputEvent.BUTTON1_MASK);
        r.mouseRelease(InputEvent.BUTTON1_MASK);
        
        //wait 1 more seconds to let Google Sets populate
        r.delay(1 * 1000);
        
        System.out.println("done");
    }

    private static final int getDeltaY()
    {
        /*
         * this is how far the mouse has to move from the lower right corner
         * of the first cell
         * 
         * this is equal to the number of cells we need to populate (n) multiplied
         * by the height of each cell
         * 
         * this product is decreased by n - 1 because there are 
         * borders around the cells of height 1 pixel on the bottom edge; and there are 
         * n - 1 such cell borders
         */
        return (CELL_HEIGHT * NUM_CELLS) - (NUM_CELLS - 1);
    }
    
    public static void main(String[] args) throws AWTException
    {
        SpreadsheetRobot spreadsheetRobot = new SpreadsheetRobot();
        
        for (int i = 0 ; i < 10 ; i++)
        {
            spreadsheetRobot.populateWords();
            
            if (i % 3 == 0)
            {
                spreadsheetRobot.reloadPage();
            }
        }
    }
}
