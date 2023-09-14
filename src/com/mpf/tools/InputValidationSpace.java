package com.mpf.tools;

import javax.swing.JOptionPane;
import javax.swing.text.AttributeSet;
import javax.swing.text.BadLocationException;
import javax.swing.text.DocumentFilter;

public class InputValidationSpace extends DocumentFilter {

	@Override
	public void replace(DocumentFilter.FilterBypass fb, int offset, int length, String text, AttributeSet attrs)
			throws BadLocationException {

		if (text.indexOf(" ") != -1) {
			JOptionPane.showMessageDialog(null,
					"Blanks are not allowed.\r\nI removed them for you.");
			text = text.replaceAll(" ", "");
		}
		super.replace(fb, offset, length, text, attrs);
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
