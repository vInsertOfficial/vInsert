package org.vinsert.bot.script;

/**
 * @author iJava
 */
public class ScriptInfo {

    private String name;
    private String desc;
    private Class<?> clazz;
    private String[] authors;
    private double version;
    private ScriptType type;

    public ScriptInfo(String name, String desc, Class<?> clazz, String[] authors, double version, ScriptType type) {
        this.name = name;
        this.desc = desc;
        this.clazz = clazz;
        this.authors = authors;
        this.version = version;
        this.type = type;
    }

    public ScriptType getType() {
        return type;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDesc() {
        return desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }

    public Class<?> getClazz() {
        return clazz;
    }

    public void setClazz(Class<?> clazz) {
        this.clazz = clazz;
    }

    public String[] getAuthors() {
        return authors;
    }

    public void setAuthors(String[] authors) {
        this.authors = authors;
    }

    public double getVersion() {
        return version;
    }

    public void setVersion(double version) {
        this.version = version;
    }
}
