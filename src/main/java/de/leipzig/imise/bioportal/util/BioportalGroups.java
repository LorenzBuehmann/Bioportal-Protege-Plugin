package de.leipzig.imise.bioportal.util;

import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.kxml2.io.KXmlParser;

import com.thoughtworks.xstream.XStream;
import com.thoughtworks.xstream.io.xml.KXml2Driver;

import de.leipzig.imise.bioportal.bean.group.Data;
import de.leipzig.imise.bioportal.bean.group.GroupBean;
import de.leipzig.imise.bioportal.bean.group.Success;

public class BioportalGroups {

	private transient Logger log = Logger.getLogger(BioportalConcept.class.getName());

	private XStream xstream;

	public BioportalGroups() {
		xstream = new XStream(new KXml2Driver());
		xstream.alias("success", Success.class);
		xstream.alias("data", Data.class);
		xstream.alias("groupBean", GroupBean.class);
	}

	public List<GroupBean> getGroups(URL conceptURL) {
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
		log.info("Loaded " + success.getData().getGroupBeans().size() + " groups.");
		return success.getData().getGroupBeans();
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
		BioportalGroups h = new BioportalGroups();
		String urlStr = "http://rest.bioontology.org/bioportal/groups";
		try {
			List<GroupBean> groupBeans = h.getGroups(new URL(urlStr));
			for(GroupBean cb : groupBeans){
				System.out.println("*******************************************");
				System.out.println("ID: " + cb.getId());
				System.out.println("Name: " + cb.getName());
				System.out.println("Acronym: " + cb.getAcronym());
			}
			
		} catch (MalformedURLException e) {
			e.printStackTrace();
		}
	}
}
