package robot;
import java.awt.*;
import java.awt.event.*;

public class SpreadsheetRobot 
{
    public static void main(String[] args)
    {
        /*
         * first open text editor then run
         */
        
        Robot r = null;
        
        try
        {
            r = new Robot();
        }
        
        catch(AWTException e)
        {
            e.printStackTrace();
        }
        
        r.delay(1000);
        
        //writes "hi buddy"
        
        r.keyPress(KeyEvent.VK_H);
        r.keyRelease(KeyEvent.VK_H);
        r.keyPress(KeyEvent.VK_I);
        r.keyRelease(KeyEvent.VK_I);
        r.keyPress(KeyEvent.VK_SPACE);
        r.keyRelease(KeyEvent.VK_SPACE);
        r.keyPress(KeyEvent.VK_B);
        r.keyRelease(KeyEvent.VK_B);
        r.keyPress(KeyEvent.VK_U);
        r.keyRelease(KeyEvent.VK_U);
        r.keyPress(KeyEvent.VK_D);
        r.keyRelease(KeyEvent.VK_D);
        r.keyPress(KeyEvent.VK_D);
        r.keyRelease(KeyEvent.VK_D);
        r.keyPress(KeyEvent.VK_Y);
        r.keyRelease(KeyEvent.VK_Y);
    }
}
