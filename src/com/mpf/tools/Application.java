package com.mpf.tools;

import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.io.Writer;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.List;

import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.SpringLayout;
import javax.swing.filechooser.FileNameExtensionFilter;
import javax.swing.text.AbstractDocument;

public class Application extends JFrame implements ActionListener {

              List<Integer> ledNumbers = new ArrayList<Integer>(); // the final array we need to loop over when writing the file
              JTextField stripPreField;
              JTextField stripPostField;
              JTextField stripRangeStartField;
              JTextField stripRangeEndField;
              JTextField lightPatternField;
              JTextField fadeField;
              JTextField stepTimeField;
              JTextField ledTypeField;
              JTextField ledSubtypeField;
              JTextField ledStripNumbersField;
              JTextField tagsField;
              JTextField startNumberField;
              JButton generateShowButton;
              JButton generateLightButton;
              JComboBox<String> animationCombo;
              JTextField filenameField;
              JButton selectFileButton;
              JButton validateLightPatternButton;
              JPanel panelShow;
              JPanel panelLight;
              JMenuItem menuItemGenShow;
              JMenuItem menuItemGenLight;

              static Writer writer;

              public static void main(String[] args) {

                            JFrame myApp = new Application();
              }

              public Application() {
                            this.setTitle("MPF light and show generator");
                            this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

                            createMenu();

                            this.setSize(700, 300);
                            this.setVisible(true);
              }

              public void createMenu() {
                            JMenuBar menuBar = new JMenuBar();
                            menuBar.setOpaque(true);
                            menuBar.setPreferredSize(new Dimension(200, 20));
                            this.setJMenuBar(menuBar);

                            JMenu menu1 = new JMenu("Main");
                            menu1.setMnemonic(KeyEvent.VK_M);
                            menuBar.add(menu1);

                            menuItemGenLight = new JMenuItem("Create light definition");
                            menuItemGenLight.setMnemonic(KeyEvent.VK_D);
                            menuItemGenLight.addActionListener(this);
                            menu1.add(menuItemGenLight);

                            menuItemGenShow = new JMenuItem("Create light show");
                            menuItemGenShow.setMnemonic(KeyEvent.VK_S);
                            menuItemGenShow.addActionListener(this);
                            menu1.add(menuItemGenShow);
              }

              public void buildShowUi() {

                            JLabel stripPreLabel = new JLabel("Light Strip Prefix");
                            JLabel stripPostLabel = new JLabel("Light Strip Postfix");
                            JLabel lightPatternLabel = new JLabel("Light Pattern");
                            JLabel fadeLabel = new JLabel("Fade Time");
                            JLabel stepTimeLabel = new JLabel("Step Time");
                            JLabel animationTypeLabel = new JLabel("Animation");
                            JLabel filenameLabel = new JLabel("Filename");
                            stripPreField = new JTextField("pre_", 10);
                            ((AbstractDocument) stripPreField.getDocument()).setDocumentFilter(new InputValidationSpace());
                            stripPostField = new JTextField("_post", 10);
                            ((AbstractDocument) stripPostField.getDocument()).setDocumentFilter(new InputValidationSpace());
                            stripRangeStartField = new JTextField("25", 3);
                            ((AbstractDocument) stripRangeStartField.getDocument()).setDocumentFilter(new InputValidationInteger());
                            stripRangeEndField = new JTextField("30", 3);
                            ((AbstractDocument) stripRangeEndField.getDocument()).setDocumentFilter(new InputValidationInteger());
                            JLabel ledStripNumbersLabel = new JLabel("LED numbers");
                            ledStripNumbersField = new JTextField("", 37); // define it here, but only visible after toggle button is pressed
                            ((AbstractDocument) ledStripNumbersField.getDocument()).setDocumentFilter(new InputValidationLedRange());
                            lightPatternField = new JTextField("green; red; blue", 37);
                            lightPatternField.setToolTipText("Use a \";\" to separate the entries, don't use a \",\".");
                            fadeField = new JTextField("100ms", 10);
                            stepTimeField = new JTextField("100ms", 10);
                            String[] animationStrings = { "all at same time", "one after other same color", "one after other next color", "circular" };
                            animationCombo = new JComboBox<>(animationStrings);
                            generateShowButton = new JButton("Generate");
                            generateShowButton.setPreferredSize(new Dimension(100, 25));
                            filenameField = new JTextField("", 37);
                            selectFileButton = new JButton("Select");
                            selectFileButton.setPreferredSize(new Dimension(100, 25));
                            validateLightPatternButton = new JButton("Validate");
                            validateLightPatternButton.setPreferredSize(new Dimension(100, 25));

                            panelShow = new JPanel();
                            SpringLayout layout = new SpringLayout();
                            panelShow.setLayout(layout);

                            // Layout for light strip strings
                            layout.putConstraint(SpringLayout.WEST, stripPreLabel, 15, SpringLayout.WEST, panelShow);
                            layout.putConstraint(SpringLayout.NORTH, stripPreLabel, 15, SpringLayout.NORTH, panelShow);

                            layout.putConstraint(SpringLayout.WEST, stripPreField, 15, SpringLayout.EAST, stripPreLabel);
                            layout.putConstraint(SpringLayout.NORTH, stripPreField, 15, SpringLayout.NORTH, panelShow);

                            layout.putConstraint(SpringLayout.WEST, stripPostLabel, 30, SpringLayout.EAST, stripPreField);
                            layout.putConstraint(SpringLayout.NORTH, stripPostLabel, 15, SpringLayout.NORTH, panelShow);

                            layout.putConstraint(SpringLayout.WEST, stripPostField, 15, SpringLayout.EAST, stripPostLabel);
                            layout.putConstraint(SpringLayout.NORTH, stripPostField, 15, SpringLayout.NORTH, panelShow);

                            // Layout for light strip led number array (in case no range is given but an array)
                            layout.putConstraint(SpringLayout.WEST, ledStripNumbersLabel, 15, SpringLayout.WEST, panelShow);
                            layout.putConstraint(SpringLayout.NORTH, ledStripNumbersLabel, 15, SpringLayout.SOUTH, stripPreLabel);

                            layout.putConstraint(SpringLayout.WEST, ledStripNumbersField, 0, SpringLayout.WEST, stripPreField);
                            layout.putConstraint(SpringLayout.NORTH, ledStripNumbersField, 15, SpringLayout.SOUTH, stripPreLabel);

                            // Layout for step time value
                            layout.putConstraint(SpringLayout.WEST, stepTimeLabel, 15, SpringLayout.WEST, panelShow);
                            layout.putConstraint(SpringLayout.NORTH, stepTimeLabel, 15, SpringLayout.SOUTH, ledStripNumbersLabel);

                            layout.putConstraint(SpringLayout.WEST, stepTimeField, 15, SpringLayout.EAST, stripPreLabel);
                            layout.putConstraint(SpringLayout.NORTH, stepTimeField, 15, SpringLayout.SOUTH, ledStripNumbersLabel);

                            // Layout for fade value
                            layout.putConstraint(SpringLayout.WEST, fadeLabel, 0, SpringLayout.WEST, stripPostLabel);
                            layout.putConstraint(SpringLayout.NORTH, fadeLabel, 15, SpringLayout.SOUTH, ledStripNumbersLabel);

                            layout.putConstraint(SpringLayout.WEST, fadeField, 0, SpringLayout.WEST, stripPostField);
                            layout.putConstraint(SpringLayout.NORTH, fadeField, 15, SpringLayout.SOUTH, ledStripNumbersLabel);

                            // Layout for light pattern
                            layout.putConstraint(SpringLayout.WEST, lightPatternLabel, 0, SpringLayout.WEST, stripPreLabel);
                            layout.putConstraint(SpringLayout.NORTH, lightPatternLabel, 15, SpringLayout.SOUTH, fadeLabel);

                            layout.putConstraint(SpringLayout.WEST, lightPatternField, 0, SpringLayout.WEST, stripPreField);
                            layout.putConstraint(SpringLayout.NORTH, lightPatternField, 15, SpringLayout.SOUTH, fadeLabel);

                            layout.putConstraint(SpringLayout.WEST, validateLightPatternButton, 15, SpringLayout.EAST, lightPatternField);
                            layout.putConstraint(SpringLayout.VERTICAL_CENTER, validateLightPatternButton, 0, SpringLayout.VERTICAL_CENTER, lightPatternField);

                            // Layout for animation type combo box
                            layout.putConstraint(SpringLayout.WEST, animationTypeLabel, 0, SpringLayout.WEST, stripPreLabel);
                            layout.putConstraint(SpringLayout.NORTH, animationTypeLabel, 15, SpringLayout.SOUTH, lightPatternLabel);

                            layout.putConstraint(SpringLayout.WEST, animationCombo, 0, SpringLayout.WEST, stripPreField);
                            layout.putConstraint(SpringLayout.VERTICAL_CENTER, animationCombo, 0, SpringLayout.VERTICAL_CENTER, animationTypeLabel);

                            // Layout for filename
                            layout.putConstraint(SpringLayout.WEST, filenameLabel, 0, SpringLayout.WEST, stripPreLabel);
                            layout.putConstraint(SpringLayout.NORTH, filenameLabel, 15, SpringLayout.SOUTH, animationTypeLabel);

                            layout.putConstraint(SpringLayout.WEST, filenameField, 0, SpringLayout.WEST, stripPreField);
                            layout.putConstraint(SpringLayout.NORTH, filenameField, 15, SpringLayout.SOUTH, animationTypeLabel);

                            layout.putConstraint(SpringLayout.WEST, selectFileButton, 15, SpringLayout.EAST, filenameField);
                            layout.putConstraint(SpringLayout.VERTICAL_CENTER, selectFileButton, 0, SpringLayout.VERTICAL_CENTER, filenameField);

                            // Layout for generate button
                            layout.putConstraint(SpringLayout.WEST, generateShowButton, 0, SpringLayout.WEST, stripPreLabel);
                            layout.putConstraint(SpringLayout.NORTH, generateShowButton, 15, SpringLayout.SOUTH, filenameLabel);

                            // adding objects to the panel for the show definition

                            panelShow.add(stripPreLabel);
                            panelShow.add(stripPreField);
                            panelShow.add(stripPostLabel);
                            panelShow.add(stripPostField);
                            panelShow.add(ledStripNumbersLabel);
                            panelShow.add(ledStripNumbersField);
                            panelShow.add(fadeLabel);
                            panelShow.add(fadeField);
                            panelShow.add(lightPatternLabel);
                            panelShow.add(lightPatternField);
                            panelShow.add(stepTimeLabel);
                            panelShow.add(stepTimeField);
                            panelShow.add(animationTypeLabel);
                            panelShow.add(animationCombo);
                            panelShow.add(generateShowButton);
                            panelShow.add(filenameLabel);
                            panelShow.add(filenameField);
                            panelShow.add(selectFileButton);
                            panelShow.add(validateLightPatternButton);

                            generateShowButton.addActionListener(this);
                            selectFileButton.addActionListener(this);
                            validateLightPatternButton.addActionListener(this);

                            this.add(panelShow);
              }

              public void buildLightUi() {

                            JLabel stripPreLabel = new JLabel("Light Strip Prefix");
                            JLabel stripPostLabel = new JLabel("Light Strip Postfix");
                            JLabel stripRangeStartLabel = new JLabel("Range Start");
                            JLabel stripRangeEndLabel = new JLabel("Range End");
                            JLabel filenameLabel = new JLabel("Filename");
                            JLabel ledTypeLabel = new JLabel("LED Type");
                            JLabel ledSubtypeLabel = new JLabel("Light Subtype");
                            JLabel startNumberLabel = new JLabel("Start Number");
                            JLabel tagsLabel = new JLabel("Tags");

                            stripPreField = new JTextField("pre_", 10);
                            ((AbstractDocument) stripPreField.getDocument()).setDocumentFilter(new InputValidationSpace());
                            stripPostField = new JTextField("_post", 10);
                            ((AbstractDocument) stripPostField.getDocument()).setDocumentFilter(new InputValidationSpace());
                            stripRangeStartField = new JTextField("25", 3);
                            ((AbstractDocument) stripRangeStartField.getDocument()).setDocumentFilter(new InputValidationInteger());
                            stripRangeEndField = new JTextField("30", 3);
                            ((AbstractDocument) stripRangeEndField.getDocument()).setDocumentFilter(new InputValidationInteger());
                            ledTypeField = new JTextField("rgb", 10);
                            ledSubtypeField = new JTextField("led", 10);
                            startNumberField = new JTextField("", 10);
                            tagsField = new JTextField("", 10);
                            filenameField = new JTextField("", 37);

                            selectFileButton = new JButton("Select");
                            selectFileButton.setPreferredSize(new Dimension(100, 25));
                            generateLightButton = new JButton("Generate");
                            generateLightButton.setPreferredSize(new Dimension(100, 25));

                            panelLight = new JPanel();
                            SpringLayout layout = new SpringLayout();
                            panelLight.setLayout(layout);

                            // Layout for light strip strings
                            layout.putConstraint(SpringLayout.WEST, stripPreLabel, 15, SpringLayout.WEST, panelLight);
                            layout.putConstraint(SpringLayout.NORTH, stripPreLabel, 15, SpringLayout.NORTH, panelLight);

                            layout.putConstraint(SpringLayout.WEST, stripPreField, 15, SpringLayout.EAST, stripPreLabel);
                            layout.putConstraint(SpringLayout.NORTH, stripPreField, 15, SpringLayout.NORTH, panelLight);

                            layout.putConstraint(SpringLayout.WEST, stripPostLabel, 30, SpringLayout.EAST, stripPreField);
                            layout.putConstraint(SpringLayout.NORTH, stripPostLabel, 15, SpringLayout.NORTH, panelLight);

                            layout.putConstraint(SpringLayout.WEST, stripPostField, 15, SpringLayout.EAST, stripPostLabel);
                            layout.putConstraint(SpringLayout.NORTH, stripPostField, 15, SpringLayout.NORTH, panelLight);

                            // Layout for light strip led number range
                            layout.putConstraint(SpringLayout.WEST, stripRangeStartLabel, 0, SpringLayout.WEST, stripPreLabel);
                            layout.putConstraint(SpringLayout.NORTH, stripRangeStartLabel, 15, SpringLayout.SOUTH, stripPreLabel);

                            layout.putConstraint(SpringLayout.WEST, stripRangeStartField, 0, SpringLayout.WEST, stripPreField);
                            layout.putConstraint(SpringLayout.NORTH, stripRangeStartField, 15, SpringLayout.SOUTH, stripPreLabel);

                            layout.putConstraint(SpringLayout.WEST, stripRangeEndLabel, 0, SpringLayout.WEST, stripPostLabel);
                            layout.putConstraint(SpringLayout.NORTH, stripRangeEndLabel, 15, SpringLayout.SOUTH, stripPreLabel);

                            layout.putConstraint(SpringLayout.WEST, stripRangeEndField, 0, SpringLayout.WEST, stripPostField);
                            layout.putConstraint(SpringLayout.NORTH, stripRangeEndField, 15, SpringLayout.SOUTH, stripPreLabel);

                            // Layout for LED type
                            layout.putConstraint(SpringLayout.WEST, ledTypeLabel, 0, SpringLayout.WEST, stripPreLabel);
                            layout.putConstraint(SpringLayout.NORTH, ledTypeLabel, 15, SpringLayout.SOUTH, stripRangeStartLabel);

                            layout.putConstraint(SpringLayout.WEST, ledTypeField, 0, SpringLayout.WEST, stripPreField);
                            layout.putConstraint(SpringLayout.NORTH, ledTypeField, 15, SpringLayout.SOUTH, stripRangeStartLabel);

                            // Layout for light subtype
                            layout.putConstraint(SpringLayout.WEST, ledSubtypeLabel, 0, SpringLayout.WEST, stripPostLabel);
                            layout.putConstraint(SpringLayout.NORTH, ledSubtypeLabel, 15, SpringLayout.SOUTH, stripRangeStartLabel);

                            layout.putConstraint(SpringLayout.WEST, ledSubtypeField, 0, SpringLayout.WEST, stripPostField);
                            layout.putConstraint(SpringLayout.NORTH, ledSubtypeField, 15, SpringLayout.SOUTH, stripRangeStartLabel);

                            // Layout for start number
                            layout.putConstraint(SpringLayout.WEST, startNumberLabel, 0, SpringLayout.WEST, stripPreLabel);
                            layout.putConstraint(SpringLayout.NORTH, startNumberLabel, 15, SpringLayout.SOUTH, ledTypeLabel);

                            layout.putConstraint(SpringLayout.WEST, startNumberField, 0, SpringLayout.WEST, stripPreField);
                            layout.putConstraint(SpringLayout.NORTH, startNumberField, 15, SpringLayout.SOUTH, ledTypeLabel);

                            // Layout for tags
                            layout.putConstraint(SpringLayout.WEST, tagsLabel, 0, SpringLayout.WEST, stripPreLabel);
                            layout.putConstraint(SpringLayout.NORTH, tagsLabel, 15, SpringLayout.SOUTH, startNumberLabel);

                            layout.putConstraint(SpringLayout.WEST, tagsField, 0, SpringLayout.WEST, stripPreField);
                            layout.putConstraint(SpringLayout.NORTH, tagsField, 15, SpringLayout.SOUTH, startNumberLabel);

                            // Layout for filename
                            layout.putConstraint(SpringLayout.WEST, filenameLabel, 0, SpringLayout.WEST, stripPreLabel);
                            layout.putConstraint(SpringLayout.NORTH, filenameLabel, 15, SpringLayout.SOUTH, tagsLabel);

                            layout.putConstraint(SpringLayout.WEST, filenameField, 0, SpringLayout.WEST, stripPreField);
                            layout.putConstraint(SpringLayout.NORTH, filenameField, 15, SpringLayout.SOUTH, tagsLabel);

                            layout.putConstraint(SpringLayout.WEST, selectFileButton, 15, SpringLayout.EAST, filenameField);
                            layout.putConstraint(SpringLayout.VERTICAL_CENTER, selectFileButton, 0, SpringLayout.VERTICAL_CENTER, filenameField);

                            // Layout for generate button
                            layout.putConstraint(SpringLayout.WEST, generateLightButton, 0, SpringLayout.WEST, stripPreLabel);
                            layout.putConstraint(SpringLayout.NORTH, generateLightButton, 15, SpringLayout.SOUTH, filenameLabel);

                            // adding objects to the panel for the light definition

                            panelLight.add(stripPreLabel);
                            panelLight.add(stripPreField);
                            panelLight.add(stripPostLabel);
                            panelLight.add(stripPostField);
                            panelLight.add(stripRangeStartLabel);
                            panelLight.add(stripRangeStartField);
                            panelLight.add(stripRangeEndLabel);
                            panelLight.add(stripRangeEndField);
                            panelLight.add(ledTypeLabel);
                            panelLight.add(ledTypeField);
                            panelLight.add(ledSubtypeLabel);
                            panelLight.add(ledSubtypeField);
                            panelLight.add(tagsLabel);
                            panelLight.add(tagsField);
                            panelLight.add(startNumberLabel);
                            panelLight.add(startNumberField);
                            panelLight.add(generateLightButton);
                            panelLight.add(filenameLabel);
                            panelLight.add(filenameField);
                            panelLight.add(selectFileButton);

                            generateLightButton.addActionListener(this);
                            selectFileButton.addActionListener(this);

                            this.add(panelLight);
              }

              @Override
              public void actionPerformed(ActionEvent e) {
                            Object source = e.getSource();

                            if (source == generateShowButton) {
                                          writeFile("show");
                            } else if (source == generateLightButton) {
                                          writeFile("light");
                            } else if (source == selectFileButton) {
                                          String storagePath = System.getProperty("user.home");

                                          JFileChooser j = new JFileChooser(storagePath);
                                          FileNameExtensionFilter filter = new FileNameExtensionFilter("YAML files", "yaml");
                                          j.setFileFilter(filter);

                                          int r = j.showSaveDialog(null);

                                          if (r == JFileChooser.APPROVE_OPTION) {
                                                        filenameField.setText(j.getSelectedFile().getAbsolutePath());
                                          }
                            } else if (source == validateLightPatternButton) {

                                          if (lightPatternField.getText().substring(lightPatternField.getText().length() - 1, lightPatternField.getText().length()).equalsIgnoreCase(";")) {
                                                         // if the last character is a ; then remove it to avoid having an empty array entry
                                                         String newString = lightPatternField.getText().substring(0, lightPatternField.getText().length() - 1);
                                                         lightPatternField.setText(newString); // rewrite the input field secretly without the ;
                                          }

                                          String[] lightPatternArray = lightPatternField.getText().split(";");
                                          String problems = "";
                                          boolean emptyArrayField = false;

                                          for (int i = 0; i < lightPatternArray.length; i++) {
                                                         String light = lightPatternArray[i].strip().toLowerCase();

                                                         if (light.isEmpty() || light == null) {
                                                                       // if an empty field is found stop validation that needs to be fixed first
                                                                       emptyArrayField = true;
                                                                       break;
                                                         }

                                                         // checks if field is a well known CSS color name, or "off"/"on"
                                                         boolean contains = Constants.colorNames.contains(light);

                                                         if (!contains)
                                                         {
                                                                       // if a not a color name, check for hex color code
                                                                       boolean isHex = light.matches("#?[0-9a-fA-F]{6}"); // should be a 6 digit hex number
                                                                       if (!isHex) {
                                                                                     // if not hex, check if RGB value given in format [123, 34, 10]
                                                                                     if ((light.substring(0, 1).equalsIgnoreCase("[") && light.substring(light.length() - 1, light.length()).equalsIgnoreCase("]"))) {
                                                                                                   String[] rgbCodes = light.substring(1, light.length() - 1).split(",");
                                                                                                   if (rgbCodes.length == 3) // need exactly 3 values in array for RGB
                                                                                                   {
                                                                                                                 for (int j = 0; j < rgbCodes.length; j++) {
                                                                                                                               try {
                                                                                                                                             int rgbValue = Integer.parseInt(rgbCodes[j].strip());
                                                                                                                                             if (rgbValue > 255 || rgbValue < 0) {
                                                                                                                                                           problems = problems + "- RGB values need to be in the range of 0...255: " + light + "\r\n";
                                                                                                                                                           break;
                                                                                                                                             }
                                                                                                                               } catch (NumberFormatException numberException) {
                                                                                                                                             problems = problems + "- Only integer values are allowed: " + light + "\r\n";
                                                                                                                                             break;
                                                                                                                               }
                                                                                                                 }
                                                                                                   } else {
                                                                                                                 problems = problems + "- You need exactly 3 comma separated values: " + light + "\r\n";
                                                                                                   }

                                                                                     } else {
                                                                                                   problems = problems + "- This doesn't seem to be a known color code: " + light + "\r\n";
                                                                                     }

                                                                       }
                                                         }
                                          }

                                          if (emptyArrayField) {
                                                         JOptionPane.showMessageDialog(this, "You have somewhere an empty entry, please remove this.\r\nThen validate again.", "Error", JOptionPane.ERROR_MESSAGE);
                                          } else if (!problems.isEmpty()) {
                                                         JOptionPane.showMessageDialog(this, "The following entries might be problematic, check again and edit where needed:\r\n" + problems, "Warning",
                                                                                     JOptionPane.WARNING_MESSAGE);
                                          } else {
                                                         JOptionPane.showMessageDialog(this, "All entries seem to be good.", "Well Done!", JOptionPane.INFORMATION_MESSAGE);
                                          }

                            } else if (source == menuItemGenShow) {
                                          getContentPane().removeAll();
                                          buildShowUi();
                                          validate();
                                          repaint();
                            } else if (source == menuItemGenLight) {
                                          getContentPane().removeAll();
                                          buildLightUi();
                                          validate();
                                          repaint();
                            }
              }

              private void writeFile(String fileType) {

                            if (stripPostField.getText().isBlank() && stripPreField.getText().isBlank()) {
                                          JOptionPane.showMessageDialog(this, "Pre and Post Field Name input cannot be blank at the same time.", "Error", JOptionPane.ERROR_MESSAGE);
                                          System.out.println("Error: Pre and Post Field Name input cannot be blank at the same time.");
                                          return;
                            }

                            if (fileType.equalsIgnoreCase("light") && Integer.parseInt(stripRangeStartField.getText()) > Integer.parseInt(stripRangeEndField.getText())) {
                                          JOptionPane.showMessageDialog(this, "When defining lights (not light shows), the start number for field numbering must be lower than the end number.", "Error",
                                                                       JOptionPane.ERROR_MESSAGE);
                                          System.out.println("Error: Start number for field numbering must be lower than end number for light definition.");
                                          return;
                            }

                            try {
                                          String filenameString = filenameField.getText();
                                          if (filenameString.isBlank()) {
                                                         JOptionPane.showMessageDialog(this, "Filename must not be empty.", "Error", JOptionPane.ERROR_MESSAGE);
                                                         System.out.println("Error: Filename must not be empty.");
                                                         return;
                                          }

                                          File targetFile = new File(filenameString);
                                          if (targetFile.exists()) {
                                                         int res = JOptionPane.showConfirmDialog(this, "File already exists, overwrite?", "Overwrite?", JOptionPane.YES_NO_OPTION);
                                                         if (res == JOptionPane.NO_OPTION) {
                                                                       return;
                                                         }
                                                         System.out.println("Warning: Target file will be overwritten: " + filenameString);
                                          }

                                          writer = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(filenameString), "utf-8"));

                                          System.out.println("Generation started...");
                                          Calendar cal = Calendar.getInstance();
                                          SimpleDateFormat dtStampFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                                          String dtStamp = dtStampFormat.format(cal.getTime());

                                          if (fileType.equalsIgnoreCase("show")) {

                                                         // Write Header information
                                                         writer.write("#show_version=5\r\n");
                                                         writer.write("# mpf light and show config generator\r\n");
                                                         writer.write("# Version: 0.1\r\n");
                                                         writer.write("# " + dtStamp + "\r\n");
                                                         writer.write("\r\n");

                                                         /*
                                                         * Parsing the LED number range input field to see what values in what order are needed. All these values and ranges
                                                         * will be put in a new array to loop over at a later point during file generation.
                                                         */

                                                         ledNumbers.clear();
                                                         String problems = "";
                                                         String[] ledNumbersTemp = ledStripNumbersField.getText().split(";");
                                                         for (int i = 0; i < ledNumbersTemp.length; i++) {
                                                                       if (ledNumbersTemp[i].strip().contains("-")) {
                                                                                     String[] startEndValue = ledNumbersTemp[i].strip().split("-");
                                                                                     if (startEndValue.length != 2 || startEndValue[0].isBlank() || startEndValue[1].isBlank()) {
                                                                                                   problems = problems + "- Range definition seems to be corrupt: " + ledNumbersTemp[i] + "\r\n";
                                                                                                   System.out.println("Warning: found a corrupt range definition, skipping this entry: " + ledNumbersTemp[i]);
                                                                                                   continue;
                                                                                     }

                                                                                     Integer startValue = Integer.parseInt(startEndValue[0].strip());
                                                                                     Integer endValue = Integer.parseInt(startEndValue[1].strip());
                                                                                     int amountLed = Math.abs(endValue - startValue) + 1;
                                                                                     int m = 1;
                                                                                     if (endValue < startValue) {
                                                                                                   m = -1;
                                                                                     }

                                                                                     for (int j = 0; j < amountLed; j++) {
                                                                                                   ledNumbers.add(startValue + (m * j));
                                                                                     }
                                                                       } else {
                                                                                     if (!ledNumbersTemp[i].isBlank()) {
                                                                                                   ledNumbers.add(Integer.parseInt(ledNumbersTemp[i].strip()));
                                                                                     } else {
                                                                                                   problems = problems + "- An emtpy range was found.\r\n";
                                                                                                   System.out.println("Warning: found an empty entry, skipping this entry.");
                                                                                     }
                                                                       }
                                                         }

                                                         if (!problems.isBlank()) {
                                                                       JOptionPane.showMessageDialog(this, "The following seem to be invalid, will ignore them for now:\r\n" + problems, "Warning", JOptionPane.WARNING_MESSAGE);
                                                         }

                                                         writer.write("# Generation of yaml file for light show\r\n");
                                                         writer.write("# LED array: " + ledNumbers.toString() + "\r\n");
                                                         writer.write("# Color array: " + lightPatternField.getText() + "\r\n");
                                                         writer.write("\r\n");

                                                         int animationId = animationCombo.getSelectedIndex();
                                                         switch (animationId) {
                                                         case 0:
                                                                       writeAnimation0(writer);
                                                                       break;
                                                         case 1:
                                                                       writeAnimation1(writer);
                                                                       break;
                                                         case 2:
                                                                       writeAnimation2(writer);
                                                                       break;
                                                         case 3:
                                                                       writeAnimation3(writer);
                                                                       break;
                                                         default:
                                                         }
                                          } else if (fileType.equalsIgnoreCase("light")) {
                                                         // Write Header information
                                                         writer.write("#config_version=5\r\n");
                                                         writer.write("# mpf light and show config generator\r\n");
                                                         writer.write("# Version: 0.1\r\n");
                                                         writer.write("# " + dtStamp + "\r\n");
                                                         writer.write("\r\n");
                                                         writer.write("# Generation of yaml file for light definition\r\n");
                                                         writer.write("\r\n");
                                                         writeLightDefinition();
                                          }

                                          writer.close();
                                          System.out.println("Generation finished...");

                                          JOptionPane.showMessageDialog(this, "File has been generated.", "Mission accomplished!", JOptionPane.INFORMATION_MESSAGE);

                            } catch (UnsupportedEncodingException e) {
                                          e.printStackTrace();
                            } catch (FileNotFoundException e) {
                                          e.printStackTrace();
                            } catch (IOException e) {
                                          e.printStackTrace();
                            }
              }

              private void writeAnimation0(Writer writer) {
                            /*
                            * All specified lights change the color at the same time
                            *
                             */

                            try {

                                          writer.write("# Generation for lights to change all at the same time.\r\n");
                                          writer.write("\r\n");

                                          String[] lightPatternArray = lightPatternField.getText().split(";");

                                          for (int i = 0; i < lightPatternArray.length; i++) {
                                                         String lightColor = lightPatternArray[i].strip();

                                                         writer.write("\r\n");
                                                         writer.write("# Step number: " + (i + 1) + " with color: " + lightColor + "\r\n");
                                                         writer.write("- duration: " + stepTimeField.getText().strip() + "\r\n");
                                                         writer.write("  lights:\r\n");

                                                         for (int j = 0; j < ledNumbers.size(); j++) {
                                                                       writer.write("    " + stripPreField.getText().strip() + ledNumbers.get(j) + stripPostField.getText().strip() + ":" + "\r\n");
                                                                       writer.write("      color: " + lightColor + "\r\n");
                                                                       writer.write("      fade: " + fadeField.getText().strip() + "\r\n");
                                                         }
                                          }
                            } catch (IOException e) {
                                          System.out.println("IO Exception when trying to write to file " + filenameField.getText());
                                          e.printStackTrace();
                            }
              }

              private void writeAnimation1(Writer writer) {
                            /*
                            * All lights get one after the other the specified color. When all lights light up in the same color then the next
                            * color will be applied.
                            */

                            String[] lightPatternArray = lightPatternField.getText().split(";");

                            try {
                                          writer.write("# Generation for light show to change LED color one by one, new color starts only when first color has reached end of strip.\r\n");
                                          writer.write("\r\n");

                                          for (int i = 0; i < lightPatternArray.length; i++) {
                                                         String lightColor = lightPatternArray[i].strip();
                                                         for (int j = 0; j < ledNumbers.size(); j++) {
                                                                       writer.write("\r\n");
                                                                       writer.write("# Light number: " + (i + 1) + "/" + lightPatternArray.length + " LED number: " + (j + 1) + " with color: " + lightColor + "\r\n");
                                                                       writer.write("- duration: " + stepTimeField.getText().strip() + "\r\n");
                                                                       writer.write("  lights:\r\n");
                                                                       writer.write("    " + stripPreField.getText().strip() + ledNumbers.get(j) + stripPostField.getText().strip() + ":" + "\r\n");
                                                                       writer.write("      color: " + lightColor + "\r\n");
                                                                       writer.write("      fade: " + fadeField.getText().strip() + "\r\n");
                                                         }
                                          }
                            } catch (IOException e) {
                                          e.printStackTrace();
                            }
              }

              private void writeAnimation2(Writer writer) {
                            /*
                            * Before an LED light will be overwritten with the next color, that color will be shifted to the next light.
                            */

                            List<String> lightPatternList = new ArrayList<String>(Arrays.asList(lightPatternField.getText().split(";")));

                            /*
                            * need to see if user has specified already that LEDs should be off at the end the array needs to have at the end as
                            * many off lights specified as we have LEDs in the LED strip
                            */

                            int amountOfOffs = 0;

                            for (int i = lightPatternList.size() - 1; i > -1; i--) {
                                          if (lightPatternList.get(i).strip().equalsIgnoreCase("off") || lightPatternList.get(i).strip().equalsIgnoreCase("000000")|| lightPatternList.get(i).strip().equalsIgnoreCase("#000000"))
                                          {
                                                         amountOfOffs = amountOfOffs + 1;
                                          } else {
                                                         break;
                                          }

                            }

                            // need to enrich array of colors with enough "off" entries
                            if (amountOfOffs < ledNumbers.size()) {
                                          for (int i = 0; i < ledNumbers.size() - amountOfOffs; i++) {
                                                         lightPatternList.add("off");
                                          }
                            }

                            try {
                                          writer.write("# Generation for light show that every LED has its own color and that color propagates along the strip.\r\n");
                                          writer.write("\r\n");
                                          for (int i = 0; i < lightPatternList.size(); i++) {

                                                         writer.write("\r\n");
                                                         writer.write("# Last light number added: " + (i + 1) + "/" + lightPatternList.size() + " with color: " + lightPatternList.get(i) + "\r\n");
                                                         writer.write("- duration: " + stepTimeField.getText().strip() + "\r\n");
                                                         writer.write("  lights:\r\n");

                                                         for (int j = 0; j < i + 1; j++) {
                                                                       if (i - j < ledNumbers.size()) {
                                                                                     String lightColor = lightPatternList.get(j).strip();
                                                                                     writer.write("    " + stripPreField.getText().strip() + ledNumbers.get(i - j) + stripPostField.getText().strip() + ":" + "\r\n");
                                                                                     writer.write("      color: " + lightColor + "\r\n");
                                                                                     writer.write("      fade: " + fadeField.getText().strip() + "\r\n");
                                                                       }
                                                         }
                                          }
                            } catch (IOException e) {
                                          e.printStackTrace();
                            }
              }

              private void writeAnimation3(Writer writer) {
                            /*
                            * Lights to run in circles
                            *
                             */

                            try {

                                          writer.write("# Generation for lights to run in circles.\r\n");
                                          writer.write("\r\n");

                                          List<String> lightPatternList = new ArrayList<String>(Arrays.asList(lightPatternField.getText().split(";")));

                                          int lengthDiff = lightPatternList.size() - ledNumbers.size();
                                          if(lengthDiff > 0) //we cannot have more lights than LEDs
                                          {
                                                         for (int i = 0; i < lengthDiff; i++) { //remove lights from end of list
                                                                       lightPatternList.remove(lightPatternList.size()-1);
                                                         }
                                          }

                                          // need to enrich array of colors with enough "off" entries, size of both arrays must be equal
                                          if (lightPatternList.size() < ledNumbers.size()) {
                                                         int amountOfOffs = ledNumbers.size() - lightPatternList.size();
                                                         for (int i = 0; i < amountOfOffs; i++) {
                                                                       lightPatternList.add("off");
                                                         }
                                          }

                                          for (int i = 0; i < ledNumbers.size(); i++) {
                                                         writer.write("\r\n");
                                                         writer.write("- duration: " + stepTimeField.getText().strip() + "\r\n");
                                                         writer.write("  lights:\r\n");

                                                         for (int j = 0; j < ledNumbers.size(); j++) {
                                                                       writer.write("    " + stripPreField.getText().strip() + ledNumbers.get(j) + stripPostField.getText().strip() + ":" + "\r\n");
                                                                       writer.write("      color: " + lightPatternList.get(j).strip() + "\r\n");
                                                                       writer.write("      fade: " + fadeField.getText().strip() + "\r\n");
                                                         }

                                                        lightPatternList.add(lightPatternList.get(0).strip());
                                                         lightPatternList.remove(0);
                                          }
                            } catch (IOException e) {
                                          System.out.println("IO Exception when trying to write to file " + filenameField.getText());
                                          e.printStackTrace();
                            }

              }

              private void writeLightDefinition() {
                            try {
                                          writer.write("lights:\r\n");
                                          int startLight = Integer.parseInt(stripRangeStartField.getText());
                                          int endLight = Integer.parseInt(stripRangeEndField.getText());
                                          for (int i = startLight; i < endLight; i++) {
                                                         writer.write("  " + stripPreField.getText() + i + stripPostField.getText() + ":\r\n");
                                                         if (i == startLight) {
                                                                       writer.write("    number: " + startNumberField.getText() + "\r\n");
                                                         } else {
                                                                       writer.write("    previous: " + stripPreField.getText() + (i - 1) + stripPostField.getText() + "\r\n");
                                                         }
                                                         writer.write("    subtype: " + ledSubtypeField.getText() + "\r\n");
                                                         writer.write("    type: " + ledTypeField.getText() + "\r\n");
                                                         writer.write("    tags: " + tagsField.getText() + "\r\n");
                                          }
                            } catch (IOException e) {
                                          e.printStackTrace();
                            }
              }

}