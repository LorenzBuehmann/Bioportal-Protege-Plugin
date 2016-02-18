package de.leipzig.imise.bioportal;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;

import org.apache.log4j.Logger;

public class BioPortalProperties {
	
	private static transient Logger log = Logger.getLogger(NcboProperties.class);
	
	public static final String REST_BASE_URL_PROPERTY               = "bioportal.rest.base.url";
	
	private static Properties p = new Properties();
    static {
        try {
            p.load(new FileInputStream(new File("bioportal.properties")));
        }
        catch (IOException ioe) {
            log.error("Could not load properties file", ioe);            
        }
    }
    
    public static String getBioportalRestBaseURLString() {
        return p.getProperty(REST_BASE_URL_PROPERTY);
    }

}
