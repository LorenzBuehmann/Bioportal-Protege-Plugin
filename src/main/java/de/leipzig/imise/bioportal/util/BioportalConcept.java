package de.leipzig.imise.bioportal.util;

import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.util.Iterator;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.xml.ws.http.HTTPException;

import com.thoughtworks.xstream.XStream;
import com.thoughtworks.xstream.io.xml.KXml2DomDriver;

import de.leipzig.imise.bioportal.bean.concept.ClassBean;
import de.leipzig.imise.bioportal.bean.concept.Data;
import de.leipzig.imise.bioportal.bean.concept.Success;

public class BioportalConcept {
	private Logger log = Logger.getLogger(BioportalConcept.class.getName());

	private XStream xstream;

	public BioportalConcept() {
		xstream = new XStream(new KXml2DomDriver());
		xstream.alias("success", Success.class);
		xstream.alias("data", Data.class);
		xstream.alias("classBean", ClassBean.class);
	}

	public ClassBean getConceptProperties(URL conceptURL) throws PrivateOntologyException{
		InputStream is = null;
		try {
			is = getInputStream(conceptURL);
		} catch (IOException e) {
			log.log(Level.WARNING, "Exception caught talking to bioportal");
			throw new PrivateOntologyException();
		}
		if (is == null) {
			return null;
		}
		Success success = (Success) xstream.fromXML(is);
		if (success == null) {
			return null;
		}
		return success.getData().getClassBean();
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

	public static void main(String[] args) throws Exception{
		BioportalConcept c = new BioportalConcept();
		String urlStr = "http://rest.bioontology.org/bioportal/concepts/39002/BRO:Resource";
		try {
			ClassBean cb = c.getConceptProperties(new URL(urlStr));
			System.out.println(cb.getFullId() + " " + cb.getId() + " "
					+ cb.getLabel());
			Map<Object, Object> relationsMap = cb.getRelations();
			for (Iterator<Object> iterator = relationsMap.keySet().iterator(); iterator
					.hasNext();) {
				Object obj = iterator.next();
				System.out.println(obj + ": " + relationsMap.get(obj));
			}
		} catch (MalformedURLException e) {
			e.printStackTrace();
		}
	}

}
