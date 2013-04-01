package org.vinsert.bot.script.randevent.impl;

import org.vinsert.bot.script.ScriptManifest;
import org.vinsert.bot.script.api.Widget;
import org.vinsert.bot.script.randevent.RandomEvent;

/**
 *
 * @author tholomew
 */
@ScriptManifest(name = "Bank Pin", authors = { "tholomew" }, description = "Bank Pin Handler", version = 1.0)
public class BankPin extends RandomEvent{
    
    private String[] bankPin;
    private int bankPinIndex = 0;

    @Override
    public boolean init() {
        for (Widget w : widgets.getValidated()) {
            System.out.println(w.getParentId());
            if (w.getParentId() == 13) {
                log("Parsing bank pin.");
                bankPin = parseBankPin(getContext().getAccount().getPin());
                return true;
            }
        }
        return false;
    }

    @Override
    public int pulse() {
        log("Entering pin");
        for (Widget w : widgets.get(13)) {  //finds the widget that contains the number
            String num = w.getText();
            if (bankPin[bankPinIndex].equals(num)) {
                w.click();
                bankPinIndex++;
                break;
            }
        }
        if (bankPinIndex >= 4) {
            close();
        }
        return random(1000, 1500);
    }

    @Override
    public void close() {
        log("Exiting bank pin handler.");
    }

    @Override
    public RandomEventPriority priority() {
        return RandomEventPriority.LOW;     //bank pins wont teleport you away like an asshole
    }
    
    public String[] parseBankPin(String p) {
        char[] pin = p.toCharArray();
        String[] numPin = new String[4];
        for (int i = 0; i < 4; i++) {   //uses a 4 constant to prevent ONE ArrayOutOfBoundsExceptions
                                        //this will throw an exception if someone puts in a < 4 number pin
            numPin[i] = String.valueOf(pin[i]);
        }
        return numPin;
    }
    
}
