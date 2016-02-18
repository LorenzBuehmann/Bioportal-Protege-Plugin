package de.leipzig.imise.bioportal.util;

import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.thoughtworks.xstream.XStream;
import com.thoughtworks.xstream.io.xml.KXml2Driver;

import de.leipzig.imise.bioportal.bean.acl.UserAcl;
import de.leipzig.imise.bioportal.bean.acl.UserEntry;
import de.leipzig.imise.bioportal.bean.ontologies.Data;
import de.leipzig.imise.bioportal.bean.ontologies.OntologyBean;
import de.leipzig.imise.bioportal.bean.ontologies.Success;

public class BioportalOntologies {

	private transient Logger log = Logger.getLogger(BioportalOntologies.class.getName());

	private XStream xstream;

	public BioportalOntologies() {
		xstream = new XStream(new KXml2Driver());
		xstream.alias("success", Success.class);
		xstream.alias("data", Data.class);
		xstream.alias("ontologyBean", OntologyBean.class);
//		xstream.alias("userAcl", UserAcl.class);
		xstream.alias("userEntry", UserEntry.class);
	}

	public List<OntologyBean> getOntologies(URL conceptURL) {
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
		return success.getData().getOntologyBeans();
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
		log.info("Loaded " + success.getData().getOntologyBeans().size() + " ontologies.");
		return success.getData().getOntologyBeans().get(0);
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

	public static void main(String[] args) {
		BioportalOntologies h = new BioportalOntologies();
		String urlStr = "http://rest.bioontology.org/bioportal/ontologies/";
		try {
			List<OntologyBean> ontologyBeans = h.getOntologies(new URL(urlStr));
			for(OntologyBean cb : ontologyBeans){
				System.out.println("*******************************************");
				System.out.println("ID: " + cb.getId());
				System.out.println("Abbreviation: " + cb.getAbbreviation());
				System.out.println("Label: " + cb.getDisplayLabel());
				System.out.println("Categories: " + cb.getCategoryIds());
			}
			
		} catch (MalformedURLException e) {
			e.printStackTrace();
		}
	}
}
