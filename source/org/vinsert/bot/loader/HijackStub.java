package org.vinsert.bot.loader;
import java.applet.AppletContext;
import java.applet.AppletStub;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * A representation of the RuneScape client's stub.
 * @author `Discardedx2 (Discardedx2)
 */
public class HijackStub implements AppletStub {

	/**
	 * This applet's parameters.
	 */
	private Map<String, String> parameters = new HashMap<String, String>();
	/**
	 * The document base of this applet.
	 */
	private URL documentBase;
	/**
	 * The code base of this applet.
	 */
	private URL codeBase;
	
	/**
	 * Constructs a new GameStub.
	 * @param parameterPattern The pattern used to identify the applet parameters.
	 * @param frameSource
	 */
	public HijackStub(Pattern parameterPattern, String frameSource) {
		Matcher param = parameterPattern.matcher(frameSource);
		while(param.find()) {
			String key = param.group(1);
			String value = param.group(2);
			parameters.put(key, value);
			System.err.println("Parameter loaded.  key="+key+", value="+value);
		}
		if(frameSource.contains("archive")) {
			parameters.put("archive", frameSource.substring(frameSource.indexOf("archive=") + 8, frameSource.indexOf(".jar") + 4));
        }
	}
	
	/**
	 * Sets the document base.
	 * @param documentBase the documentBase to set.
	 */
    public void setDocumentBase(URL documentBase) {
        this.documentBase = documentBase;
    }
    
	/**
	 * Sets the code base.
	 * @param codeBase the codeBase to set.
	 */
    public void setCodeBase(URL codeBase) {
        this.codeBase = codeBase;
    }

	@Override
	public boolean isActive() {
		return true;
	}

	@Override
	public URL getDocumentBase() {
		return documentBase;
	}

	@Override
	public URL getCodeBase() {
		return codeBase;
	}

	@Override
	public String getParameter(String name) {
		return parameters.get(name);
	}

	@Override
	public AppletContext getAppletContext() {
		return null;
	}

	@Override
	public void appletResize(int width, int height) {

	}

}
