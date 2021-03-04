package mottinol.alh.gui;

import java.awt.Color;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

import javax.swing.JPasswordField;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.text.JTextComponent;


/**
 * Agile Learning Helper
 * 
 * @version 1.1
 * @since 1.9
 * @author Loris Mottino
 *
 */
public class GhostText implements FocusListener, DocumentListener, PropertyChangeListener {
	
	private final JTextComponent textComponent;
	private final char defaultEchoChar;
	private final boolean isPasswordField;
	private boolean isEmpty;
	private Color ghostColor = Color.LIGHT_GRAY, foregroundColor;
	private final String ghostText;
	
	
	protected GhostText(final JTextComponent textComponent, String ghostText) {
		super();
		this.textComponent = textComponent;
		this.ghostText = ghostText;
		this.isPasswordField = textComponent instanceof JPasswordField;
		if (this.isPasswordField)
			this.defaultEchoChar = ((JPasswordField)this.textComponent).getEchoChar();
		else
			this.defaultEchoChar = (char)0;
		
		this.textComponent.addFocusListener(this);
		this.registerListeners();
		this.updateState();
		if (!textComponent.hasFocus())
			focusLost(null);
	}
	
	
	
	public boolean isEmpty() {
		return this.isEmpty;
	}
	
	
	
	public void delete() {
		unregisterListeners();
		textComponent.removeFocusListener(this);
	}

	private void registerListeners() {
		textComponent.getDocument().addDocumentListener(this);
		textComponent.addPropertyChangeListener("foreground", this);
	}

	private void unregisterListeners() {
		textComponent.getDocument().removeDocumentListener(this);
		textComponent.removePropertyChangeListener("foreground", this);
	}

	public Color getGhostColor() {
		return ghostColor;
	}

	public void setGhostColor(Color ghostColor) {
		this.ghostColor = ghostColor;
	}

	private void updateState() {
		isEmpty = textComponent.getText().length() == 0;
		foregroundColor = textComponent.getForeground();
	}

	@Override
	public void focusGained(FocusEvent e) {
		if (isEmpty) {
			unregisterListeners();
			if (isPasswordField)
				((JPasswordField)textComponent).setEchoChar(defaultEchoChar);
			try {
				textComponent.setText("");
				textComponent.setForeground(foregroundColor);
			} finally {
				registerListeners();
			}
		}
	}

	@Override
	public void focusLost(FocusEvent e) {
		if (isEmpty) {
			unregisterListeners();
			if (isPasswordField)
				((JPasswordField)textComponent).setEchoChar((char)0);
			try {
				textComponent.setText(ghostText);
				textComponent.setForeground(ghostColor);
			} finally {
				registerListeners();
			}
		} else {
			unregisterListeners();
			try {
				textComponent.setForeground(foregroundColor);
			} finally {
				registerListeners();
			}
		}
	}

	@Override
	public void propertyChange(PropertyChangeEvent evt) {
		updateState();
	}

	@Override
	public void changedUpdate(DocumentEvent e) {
		updateState();
	}

	@Override
	public void insertUpdate(DocumentEvent e) {
		updateState();
	}

	@Override
	public void removeUpdate(DocumentEvent e) {
		updateState();
	}
	
}
