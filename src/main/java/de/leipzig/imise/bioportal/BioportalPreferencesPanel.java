package de.leipzig.imise.bioportal;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.net.MalformedURLException;
import java.net.URL;

import javax.swing.JLabel;
import javax.swing.JTextField;

import org.protege.editor.core.ui.preferences.PreferencesPanel;

public class BioportalPreferencesPanel extends PreferencesPanel{

	/**
	 * 
	 */
	private static final long serialVersionUID = 8466087342212024192L;
	private JTextField urlField;

	@Override
	public void applyChanges() {
		BioportalPreferences prefs = BioportalPreferences.getInstance();
		try {
			prefs.setRestBaseURL(new URL(urlField.getText()));
		} catch (MalformedURLException e) {
			e.printStackTrace();
		}
	}

	@Override
	public void initialise() throws Exception {
		BioportalPreferences prefs = BioportalPreferences.getInstance();
		
		setLayout(new GridBagLayout());
		GridBagConstraints c = new GridBagConstraints();
		c.gridwidth = GridBagConstraints.REMAINDER;
		c.anchor = GridBagConstraints.NORTHWEST;
		add(new JLabel("Bioportal rest service base URL:"), c);
		c.fill = GridBagConstraints.HORIZONTAL;
		c.weightx = 1.0;
		urlField = new JTextField(prefs.getRestBaseURL().toString());
		add(urlField, c);
	}

	@Override
	public void dispose() throws Exception {
		
	}

}
