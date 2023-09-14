package com.mpf.tools;

import javax.swing.JOptionPane;
import javax.swing.text.AttributeSet;
import javax.swing.text.BadLocationException;
import javax.swing.text.DocumentFilter;

public class InputValidationInteger extends DocumentFilter {

	@Override
	public void replace(DocumentFilter.FilterBypass fb, int offset, int length, String text, AttributeSet attrs)
			throws BadLocationException {

		boolean foundNonDigit = false;
		StringBuffer buffer = new StringBuffer(text);
		for (int i = buffer.length() - 1; i >= 0; i--) {
			char ch = buffer.charAt(i);
			if (!Character.isDigit(ch)) {
				buffer.deleteCharAt(i);
				foundNonDigit = true;
			}
		}

		if (foundNonDigit) {
			JOptionPane.showMessageDialog(null,
					"Only digits are allowed. Other characters have been removed from your input.");
		}
		super.replace(fb, offset, length, buffer.toString(), attrs);
	}

	@Override
	public void insertString(DocumentFilter.FilterBypass fb, int offset, String text, AttributeSet attr)
			throws BadLocationException {
		// only being used if the Document gets direct updates, not needed here for now
		super.insertString(fb, offset, text, attr);
	}

	@Override
	public void remove(DocumentFilter.FilterBypass fb, int offset, int length) throws BadLocationException {

		super.remove(fb, offset, length);
	}

}
