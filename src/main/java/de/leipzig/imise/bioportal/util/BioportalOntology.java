package de.leipzig.imise.bioportal.util;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.thoughtworks.xstream.XStream;
import com.thoughtworks.xstream.io.xml.KXml2Driver;

import de.leipzig.imise.bioportal.bean.acl.UserEntry;
import de.leipzig.imise.bioportal.bean.ontology.Data;
import de.leipzig.imise.bioportal.bean.ontology.OntologyBean;
import de.leipzig.imise.bioportal.bean.ontology.Success;


public class BioportalOntology {

	private transient Logger log = Logger.getLogger(BioportalOntology.class.getName());

	private XStream xstream;

	public BioportalOntology() {
		xstream = new XStream(new KXml2Driver());
		xstream.alias("success", Success.class);
		xstream.alias("data", Data.class);
		xstream.alias("ontologyBean", OntologyBean.class);
		xstream.alias("userEntry", UserEntry.class);
	}

	public OntologyBean getOntologyProperties(URL conceptURL) {
		InputStream is = null;
		try {
			is = getInputStream(conceptURL);
		} catch (IOException e) {
			log.log(Level.WARNING, "Exception caught talking to bioportal", e);
		}
		if (is == null) {
			return null;
		}
		Success success = (Success) xstream.fromXML(is);
		if (success == null) {
			return null;
		}
		return success.getData().getOntologyBean();
	}

	public static InputStream getInputStream(URL url) throws IOException {
		if (url.getProtocol().equals("http")) {
			URLConnection conn;
			conn = url.openConnection();
			conn.setRequestProperty("Accept", "application/rdf+xml");
			conn.addRequestProperty("Accept", "text/xml");
			conn.addRequestProperty("Accept", "*/*");
			return conn.getInputStream();
		} else {
			return url.openStream();
		}
	}

}
