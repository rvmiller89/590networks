package robot;

import java.awt.*;
import java.awt.event.*;

public class SpreadsheetRobot 
{
    /*
     * These starting coordinates are known to work on Azfar's
     * computer with screen resolution 1920x1080 with Google
     * Spreadsheet open in Firefox with both Google Spreadsheet
     * *AND* Firefox in full screen mode
     * 
     * generally, it should work if your browser is in full screen
     * mode *AND* Google Spreadsheet is in full screen mode (View -> Full screen)
     */
    private static final int START_X = 166;
    private static final int START_Y = 41;

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
        new SpreadsheetRobot().populateWords();
    }
}
