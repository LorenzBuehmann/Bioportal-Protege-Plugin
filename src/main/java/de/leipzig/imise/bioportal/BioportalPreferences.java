package de.leipzig.imise.bioportal;

import java.net.MalformedURLException;
import java.net.URL;

import org.protege.editor.core.prefs.Preferences;
import org.protege.editor.core.prefs.PreferencesManager;

public class BioportalPreferences {
	
	private static BioportalPreferences instance;

    private static final String KEY = "de.leipzig.imise.bioportal";

    private static final String BIOPORTAL_REST_BASE_URL_KEY = "BIOPORTAL_REST_BASE_URL";
	private static final String BIOPORTAL_REST_API_KEY = "BIOPORTAL_REST_API_KEY";

	private static final String DEFAULT_REST_BASE_URL = "http://data.bioontology.org";
	private static final String DEFAULT_REST_API_KEY = "8fadfa2c-47de-4487-a1f5-b7af7378d693";

    public static synchronized BioportalPreferences getInstance() {
        if(instance == null) {
            instance = new BioportalPreferences();
        }
        return instance;
    }
    
    private Preferences getPrefs() {
        return PreferencesManager.getInstance().getApplicationPreferences(KEY);
    }
    
    public URL getRestBaseURL(){
    	String urlString = getPrefs().getString(BIOPORTAL_REST_BASE_URL_KEY, DEFAULT_REST_BASE_URL);
    	URL url = null;
		try {
			url = new URL(urlString);
		} catch (MalformedURLException e) {
			e.printStackTrace();
		}
    	return url;
    }

	public void setRestBaseURL(URL url){
		getPrefs().putString(BIOPORTAL_REST_BASE_URL_KEY, url.toString());
	}
    
    public void setRestAPIKey(String key){
    	getPrefs().putString(BIOPORTAL_REST_API_KEY, key);
    }

	public String getRestAPIKey(){
		return getPrefs().getString(BIOPORTAL_REST_API_KEY, DEFAULT_REST_API_KEY);
	}
}
