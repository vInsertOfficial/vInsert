package org.vinsert.bot.script.randevent.impl;

import org.vinsert.bot.script.ScriptManifest;
import org.vinsert.bot.script.api.Widget;
import org.vinsert.bot.script.randevent.RandomEvent;

/**
 *
 * @author tholomew
 * @version 1.1: now finds the correct widget that a human would most likely click, rather than a tiny box
 *      also enters in the pin based on what window is showing
 */
@ScriptManifest(name = "Bank Pin", authors = {"tholomew"}, description = "Bank Pin Handler", version = 1.1)
public class BankPin extends RandomEvent {

    private String[] bankPin;
    private final int PARENT_ID = 13;

    @Override
    public boolean init() {
        if (isPinWindowOpen()) {
                log("Parsing bank pin.");
                String sPin = getContext().getAccount().getPin();
                if (sPin.length() < 4 || sPin.equals("null")) {
                    log("You must enter a valid bank pin in accounts!");
                    return false;
                }
                bankPin = parseBankPin(sPin);
                return true;
            }
        return false;
    }

    @Override
    public int pulse() {
        int digit = getDigitToTypeIndex();
        if (digit >= 0 && digit <= 3) {
            Widget click = getEnterDigit(bankPin[digit]);
            if (click.isValid()) {
                click.click();
            } else {
                requestExit();
                sleep(1000, 1500);
            }
        }
        if (!isPinWindowOpen()) {
            requestExit();
            sleep(1000, 1500);
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

    public Widget getEnterDigit(String n) {
        for (int i = 110; i < 120; i++) {
            if (widgets.get(PARENT_ID, i).getText().equals(n)) {
                return widgets.get(PARENT_ID, i - 10);
            }
        }
        return null;
    }

    public int getDigitToTypeIndex() {
        Widget clickText = widgets.get(PARENT_ID, 151);
        if (clickText != null) {
            if (clickText.equals("First click the FIRST digit."))
            	return 0;
            else if (clickText.equals("Now click the SECOND digit."))
            	return 1;
            else if (clickText.equals("Time for the THIRD digit."))
            	return 2;
            else if (clickText.equals("Finally, the FOURTH digit."))
            	return 3;
        }
        return -1;  //shouldn't ever get here
    }
    
    public boolean isPinWindowOpen() {
        for (Widget w : widgets.getValidated()) {
            if (w.getParentId() == PARENT_ID) {
                return true;
            }
        }
        return false;
    }
}
