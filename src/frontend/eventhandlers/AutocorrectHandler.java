package frontend.eventhandlers;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.util.List;

import javax.swing.JComboBox;

import frontend.app.GUIApp;

/**
 * Calls autocorrect as needed when the enter key is pressed and the fields are filled or when the user presses a button
 * 
 * @author dgattey
 */
public class AutocorrectHandler implements ActionListener, KeyListener, FocusListener {
	
	private final GUIApp		app;
	private JComboBox<String>	field;
	
	/**
	 * Controller to take in an app to use its hub
	 * 
	 * @param app the app object
	 */
	public AutocorrectHandler(final GUIApp app) {
		this.app = app;
	}
	
	public JComboBox<String> getField() {
		return field;
	}
	
	public void setField(final JComboBox<String> field) {
		this.field = field;
	}
	
	/**
	 * Actually get the suggestions and update the view controller with them
	 */
	private void getSuggestions() {
		final String input = field.getEditor().getItem().toString();
		final List<String> suggestions = app.getHub().getSuggestions(input);
		if (suggestions == null) {
			field.removeAllItems();
			return;
		}
		
		// Update box
		field.removeAllItems();
		for (final String str : suggestions) {
			// No duplicates
			if (!str.equals(input)) {
				field.addItem(str);
			}
		}
	}
	
	@Override
	public void actionPerformed(final ActionEvent e) {
		getSuggestions();
	}
	
	@Override
	public void keyTyped(final KeyEvent e) {
		if (field.isPopupVisible()) {
			field.hidePopup();
			field.removeAllItems();
		}
	}
	
	@Override
	public void keyPressed(final KeyEvent e) {}
	
	@Override
	public void keyReleased(final KeyEvent e) {}
	
	@Override
	public void focusGained(final FocusEvent e) {
		getSuggestions();
	}
	
	@Override
	public void focusLost(final FocusEvent e) {
		field.hidePopup();
	}
}
