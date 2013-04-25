package org.vinsert.bot.script.callback;

import org.vinsert.insertion.IItemDefinition;

import java.util.ArrayList;
import java.util.List;

/**
 * @author iJava
 */
public class ItemDefinitionCallback {

    public static List<IItemDefinition> defs = new ArrayList<>();

    public static void callback(IItemDefinition def) {
       if(def != null) {
           defs.add(def);
       }
    }
}
