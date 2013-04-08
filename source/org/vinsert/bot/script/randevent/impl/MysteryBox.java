package org.vinsert.bot.script.randevent.impl;

import java.util.Random;
import org.vinsert.bot.script.ScriptManifest;
import org.vinsert.bot.script.api.Item;
import org.vinsert.bot.script.api.Widget;
import org.vinsert.bot.script.randevent.RandomEvent;
import org.vinsert.bot.script.randevent.RandomEvent.RandomEventPriority;
import org.vinsert.bot.util.Condition;
import org.vinsert.bot.util.Utils;

/**
 *
 * @author tholomew
 */
@ScriptManifest(name = "MysteryBox", description = "Solves the Mystery Box", authors = {"tholomew"})
public class MysteryBox extends RandomEvent {

    private final int PARENT_ID = 190;
    private final int BOX_ID = 3063;
    Item box;

    @Override
    public boolean init() {
        box = inventory.getItem(BOX_ID);
        if (box != null) {
            return true;
        }
        return false;
    }

    @Override
    public int pulse() {
        String anw;
        Widget found;
        if (box != null) {
            inventory.interact(inventory.getSlot(box), "Open");
            Utils.waitFor(new Condition() {

                @Override
                public boolean validate() {
                    return widgets.get(PARENT_ID) != null;
                }
            }, 1500);
            if (widgets.get(PARENT_ID) != null) {
                anw = getAnwser();
                for (int i = 10; i < 13; i++) {
                    found = widgets.get(190, i);
                    if (found.getText().toLowerCase().contains(anw)) {
                        found.click();
                    }
                    Utils.waitFor(new Condition() {

                        @Override
                        public boolean validate() {
                            return widgets.get(PARENT_ID) == null;
                        }
                    }, 1500);
                }
            }
        }
        return random(150, 200);
    }

    @Override
    public void close() {
    }

    @Override
    public RandomEventPriority priority() {
        return RandomEventPriority.MEDIUM;
    }

    public String getAnwser() {
        String anw = "";
        String question = getQuestion();
        String anwser = getSides();
        String temp[] = anwser.split("-");
        for (String i : temp) {
            if (i.contains(question)) {
                anw = i.replace(question, "");
            }
        }
        return anw;
    }

    public String getQuestion() {
        String question = "";
        if (widgets.get(PARENT_ID, 6) != null) {
            question = widgets.get(PARENT_ID, 6).getText();
            if (question.contains("shape has")) {
                question = question.substring(question.indexOf("number ") + 7, question.indexOf("?"));
            }
            if (question.contains("number is")) {
                question = question.substring(question.indexOf("the ") + 4, question.indexOf("?")).toLowerCase();
            }
        }
        return question;
    }

    public String getSides() {

        String result = "";
        String[] shape = {"", "", ""};
        String[] number = {"", "", ""};

        int[] circle = {7005, 7020, 7035};
        int[] pentagon = {7006, 7021, 7036};
        int[] square = {7007, 7022, 7037};
        int[] star = {7008, 7023, 7038};
        int[] triangle = {7009, 7024, 7039};

        int[] n0 = {7010, 7025, 7040};
        int[] n1 = {7011, 7026, 7041};
        int[] n2 = {7012, 7027, 7042};
        int[] n3 = {7013, 7028, 7043};
        int[] n4 = {7014, 7029, 7044};
        int[] n5 = {7015, 7030, 7045};
        int[] n6 = {7016, 7031, 7046};
        int[] n7 = {7017, 7032, 7047};
        int[] n8 = {7018, 7033, 7048};
        int[] n9 = {7019, 7034, 7049};


        if (widgets.get(PARENT_ID, 0) != null) {
            for (int i = 0; i < 3; i++) {
                if (contains(circle, widgets.get(PARENT_ID, i).getModelId())) {
                    shape[i] = "circle";
                }
                if (contains(pentagon, widgets.get(PARENT_ID, i).getModelId())) {
                    shape[i] = "pentagon";
                }
                if (contains(star, widgets.get(PARENT_ID, i).getModelId())) {
                    shape[i] = "star";
                }
                if (contains(square, widgets.get(PARENT_ID, i).getModelId())) {
                    shape[i] = "square";
                }
                if (contains(triangle, widgets.get(PARENT_ID, i).getModelId())) {
                    shape[i] = "triangle";
                }
            }
            for (int i = 3; i < 6; i++) {
                if (contains(n0, widgets.get(PARENT_ID, i).getModelId())) {
                    number[i - 3] = "0";
                }
                if (contains(n1, widgets.get(PARENT_ID, i).getModelId())) {
                    number[i - 3] = "1";
                }
                if (contains(n2, widgets.get(PARENT_ID, i).getModelId())) {
                    number[i - 3] = "2";
                }
                if (contains(n3, widgets.get(PARENT_ID, i).getModelId())) {
                    number[i - 3] = "3";
                }
                if (contains(n4, widgets.get(PARENT_ID, i).getModelId())) {
                    number[i - 3] = "4";
                }
                if (contains(n5, widgets.get(PARENT_ID, i).getModelId())) {
                    number[i - 3] = "5";
                }
                if (contains(n6, widgets.get(PARENT_ID, i).getModelId())) {
                    number[i - 3] = "6";
                }
                if (contains(n7, widgets.get(PARENT_ID, i).getModelId())) {
                    number[i - 3] = "7";
                }
                if (contains(n8, widgets.get(PARENT_ID, i).getModelId())) {
                    number[i - 3] = "8";
                }
                if (contains(n9, widgets.get(PARENT_ID, i).getModelId())) {
                    number[i - 3] = "9";
                }
            }

            for (int i = 0; i < 3; i++) {
                result += shape[i] + number[i] + "-";
            }

        }
        return result;
    }

    private boolean contains(final int[] y, final int i) {
        for (int x : y) {
            if (x == i) {
                return true;
            }
        }
        return false;
    }
}
