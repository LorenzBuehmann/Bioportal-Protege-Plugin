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

import de.leipzig.imise.bioportal.bean.category.CategoryBean;
import de.leipzig.imise.bioportal.bean.category.Data;
import de.leipzig.imise.bioportal.bean.category.Success;

public class BioportalCategories {

	private transient Logger log = Logger.getLogger(BioportalConcept.class.getName());

	private XStream xstream;

	public BioportalCategories() {
		xstream = new XStream(new KXml2Driver());
		xstream.alias("success", Success.class);
		xstream.alias("data", Data.class);
		xstream.alias("categoryBean", CategoryBean.class);
	}

	public List<CategoryBean> getCategories(URL conceptURL) {
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
		log.info("Loaded " + success.getData().getCategoryBeans().size() + " categories.");
		return success.getData().getCategoryBeans();
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
		BioportalCategories h = new BioportalCategories();
		String urlStr = "http://rest.bioontology.org/bioportal/categories";
		try {
			List<CategoryBean> categoryBeans = h.getCategories(new URL(urlStr));
			for(CategoryBean cb : categoryBeans){
				System.out.println("*******************************************");
				System.out.println("ID: " + cb.getId());
				System.out.println("Name: " + cb.getName());
			}
			
		} catch (MalformedURLException e) {
			e.printStackTrace();
		}
	}
}
