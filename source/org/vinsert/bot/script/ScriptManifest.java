package org.vinsert.bot.script;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * An annotation providing script details
 * @author tommo
 *
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
public @interface ScriptManifest {
	
	String name();
	
	String description() default "";
	
	String[] authors();
	
	double version() default 1.0;

    ScriptType type() default ScriptType.FREE;
	
}
