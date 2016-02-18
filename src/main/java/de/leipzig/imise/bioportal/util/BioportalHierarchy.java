package de.leipzig.imise.bioportal.util;

import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.thoughtworks.xstream.XStream;

import de.leipzig.imise.bioportal.bean.hierarchy.ClassBean;
import de.leipzig.imise.bioportal.bean.hierarchy.Data;
import de.leipzig.imise.bioportal.bean.hierarchy.Success;

public class BioportalHierarchy {

	private transient Logger log = Logger.getLogger(BioportalConcept.class.getName());

	private XStream xstream;

	public BioportalHierarchy() {
		xstream = new XStream();
		xstream.alias("success", Success.class);
		xstream.alias("data", Data.class);
		xstream.alias("classBean", ClassBean.class);
	}

	public List<ClassBean> getHierarchy(URL conceptURL) {
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
		return success.getData().getClassBeans();
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
		BioportalHierarchy h = new BioportalHierarchy();
//		String urlStr = "http://rest.bioontology.org/bioportal/concepts/children/42331/Melanoma?email=example@example.org";
		String urlStr = "http://rest.bioontology.org/bioportal/concepts/leafpath/42331/Melanoma?email=example@example.org";
		try {
			List<ClassBean> classBeans = h.getHierarchy(new URL(urlStr));
			System.out.println("Number of children: " + classBeans.size());
			for(ClassBean cb : classBeans){
				System.out.println("*******************************************");
				System.out.println("Full ID: " + cb.getFullId());
				System.out.println("ID: " + cb.getId());
				System.out.println("Label: " + cb.getLabel());
				System.out.println("Relations:");
				Map<Object, Object> relationsMap = cb.getRelations();
				for (Iterator<Object> iterator = relationsMap.keySet().iterator(); iterator
						.hasNext();) {
					Object obj = iterator.next();
					System.out.println(obj + ": " + relationsMap.get(obj));
				}
			}
			
		} catch (MalformedURLException e) {
			e.printStackTrace();
		}
	}
}
