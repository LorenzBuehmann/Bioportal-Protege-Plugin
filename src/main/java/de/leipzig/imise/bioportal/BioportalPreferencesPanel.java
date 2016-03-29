package de.leipzig.imise.bioportal;

import org.protege.editor.core.ui.preferences.PreferencesLayoutPanel;
import org.protege.editor.core.ui.preferences.PreferencesPanel;

import javax.swing.*;
import java.awt.*;
import java.net.MalformedURLException;
import java.net.URL;

/**
 * The plugin to setup BioPortal preferences in the Protege preferences menu.
 */
public class BioportalPreferencesPanel extends PreferencesPanel{

	private static final long serialVersionUID = 8466087342212024192L;

	private JTextField urlField;
	private JTextField apiKeyField;

	@Override
	public void applyChanges() {
		BioportalPreferences prefs = BioportalPreferences.getInstance();
		try {
			prefs.setRestBaseURL(new URL(urlField.getText()));
			prefs.setRestAPIKey(apiKeyField.getText());
		} catch (MalformedURLException e) {
			e.printStackTrace();
		}
	}

	@Override
	public void initialise() throws Exception {
		setLayout(new BorderLayout());

		PreferencesLayoutPanel panel = new PreferencesLayoutPanel();
		add(panel, BorderLayout.NORTH);

		BioportalPreferences prefs = BioportalPreferences.getInstance();

		panel.addGroup("REST Service");
		urlField = new JTextField(prefs.getRestBaseURL().toString());
		panel.addLabelledGroupComponent("Base URL", urlField);

		apiKeyField = new JTextField(prefs.getRestAPIKey());
		panel.addLabelledGroupComponent("API Key", apiKeyField);

//		panel.addSeparator();
//
//		panel.addGroup("Caching");
	}

	@Override
	public void dispose() throws Exception {
		
	}

	public static void main(String[] args) throws Exception {
		JFrame frame = new JFrame();
		BioportalPreferencesPanel panel = new BioportalPreferencesPanel();
		panel.initialise();
		frame.add(panel);
		frame.setPreferredSize(new Dimension(600, 300));
		frame.pack();
		frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
		frame.setVisible(true);
	}

}
