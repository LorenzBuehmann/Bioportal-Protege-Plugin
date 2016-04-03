package de.leipzig.imise.bioportal.util;

import com.google.common.base.Joiner;
import de.leipzig.imise.bioportal.rest.BioportalRESTService;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.SystemUtils;
import org.apache.commons.lang3.builder.ReflectionToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import org.semanticweb.owlapi.model.IRI;
import org.semanticweb.owlapi.util.IRIShortFormProvider;
import org.semanticweb.owlapi.util.ShortFormProvider;
import org.semanticweb.owlapi.util.SimpleIRIShortFormProvider;

import java.util.Collection;
import java.util.Iterator;
import java.util.Map;

/**
 * Convert a POJO into a simple 2 column HTML table.
 */
public class POJO2HTML {
	static public String makeHTML(Object object) {
		return ReflectionToStringBuilder.toString(object, new HTMLStyle());
	}

	private static final class HTMLStyle extends ToStringStyle {

		int row = 0;

		public HTMLStyle() {
			setFieldSeparator("</td></tr>" + SystemUtils.LINE_SEPARATOR + "<tr style='border-bottom: thin solid black;'><td style='font-weight: bold;'>");

			setContentStart("<table id=\"customers\">" + SystemUtils.LINE_SEPARATOR +
//									"<thead><tr><th>Field</th><th>Data</th></tr></thead>" +
									"<tbody>" + SystemUtils.LINE_SEPARATOR +
									"<tr><td style='font-weight: bold;'>");

			setFieldNameValueSeparator("</td><td>");

			setContentEnd("</td></tr>" + SystemUtils.LINE_SEPARATOR + "</tbody></table>");

			setArrayContentDetail(true);
			setUseShortClassName(true);
			setUseClassName(false);
			setUseIdentityHashCode(false);
		}

		private boolean isMetaDataField(String fieldName) {
			return fieldName.toLowerCase().endsWith("links") || fieldName.equals("@context");
		}


		public void append(final StringBuffer buffer, final String fieldName, final Object value, final Boolean fullDetail) {
			if(!isMetaDataField(fieldName)) {
				if(fieldName.equals("additionalProperties") || fieldName.equals("properties")) {
					Map<String, Object> map = (Map<String, Object>) value;
					for (Map.Entry<String,Object> entry : map.entrySet()) {
						append(buffer, entry.getKey(), makeLink(entry.getValue()), fullDetail);
						row++;
					}
				} else {
					row++;
					super.append(buffer, fieldName, (value == null) ? null : makeLink(value), fullDetail);
				}

			}
		}

		private Object makeLink(Object value) {
			if(value != null && value.toString().startsWith("http://")) {
				return "<a href='" + BioportalRESTService.asBioportalLink(value.toString()) + "'>" + value + "</a>";
			}
			return value;
		}

		@Override
		public void appendDetail(StringBuffer buffer, String fieldName, Object value) {
			if (value.getClass().getName().startsWith("java.lang")) {
				super.appendDetail(buffer, fieldName, value);
			} else {
				if(!isMetaDataField(fieldName)) {
					if(value instanceof Collection) {
						for (Object o : ((Collection) value)) {
							buffer.append(ReflectionToStringBuilder.toString(o, this));
						}
					} else {
						buffer.append(ReflectionToStringBuilder.toString(makeLink(value), this));
					}
				}
			}
		}

		@Override
		protected void appendDetail(final StringBuffer buffer, final String fieldName, final Collection<?> coll) {

//			// first value inline with field name
//			buffer.append(makeLink(iterator.next()));
//
//			while(iterator.hasNext()) {
//				buffer.append("</td></tr>" + SystemUtils.LINE_SEPARATOR);
//				buffer.append("<tr><td></td><td>" + makeLink(iterator.next()));
//			}

			for (Object aColl : coll) {
				appendDetail(buffer, fieldName, makeLink(aColl));
			}
		}

		@Override
		protected void appendFieldStart(StringBuffer buffer, String fieldName) {
			IRIShortFormProvider sfp = new SimpleIRIShortFormProvider();
			try {
				fieldName = sfp.getShortForm(IRI.create(fieldName));
			} catch (Exception e) {

			}

			fieldName = fieldName.replace("_", " ");

			// split field name by camel case
			String[] split = StringUtils.splitByCharacterTypeCamelCase(fieldName);
			for (int i = 0; i < split.length; i++) {
				String token = split[i];
				// non acronyms to lower case
				if(!StringUtils.isAllUpperCase(token)) {
					split[i] = token.toLowerCase();
				}
			}
			fieldName = Joiner.on(" ").join(split);
			super.appendFieldStart(buffer, fieldName);
		}

		@Override
		protected void appendFieldSeparator(StringBuffer buffer) {
			String color = row % 2 == 0 ? "#E5E5E5" : "#c7d4e5";
			setFieldSeparator("</td></tr>" + SystemUtils.LINE_SEPARATOR + "<tr style='border-bottom: thin solid black; background-color=" + color + ";'><td style='font-weight: bold;'>");
			super.appendFieldSeparator(buffer);
		}
	}
}