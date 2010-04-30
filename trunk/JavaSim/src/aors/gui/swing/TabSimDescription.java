package aors.gui.swing;

import java.awt.BorderLayout;
import java.awt.Font;
import java.awt.GraphicsEnvironment;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JEditorPane;
import javax.swing.JLabel;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTextPane;
import javax.swing.JToolBar;
import javax.swing.text.PlainDocument;

import org.bounce.text.LineNumberMargin;
import org.bounce.text.ScrollableEditorPanel;
import org.bounce.text.xml.XMLEditorKit;

/**
 * 
 * 
 */
public class TabSimDescription extends JSplitPane {

  private static final long serialVersionUID = -5141229393391171935L;

  private String name;
  private ActionListener actionListener;
  private JEditorPane editorPane;
  // private XMLEditorKit xmlEditorKit;
  private JTextPane outputTextArea;

  private JComboBox tabSize;
  private JScrollPane editorScroller;

  public static final String CONTEXT_MENU_ITEM_RELOAD = "Reload AORSL";

  /**
   * 
   * Create a new {@code TabSimDescription}.
   * 
   */
  public TabSimDescription(int orientation, ActionListener actionListener) {
    super(orientation);
    this.actionListener = actionListener;
    this.name = "";

    this.initEditor(true, 2, true);

    this.outputTextArea = new JTextPane();

    // add an pop up menu
    this.outputTextArea.addMouseListener(new MouseListener() {

      @Override
      public void mouseClicked(MouseEvent e) {
        showPopupMenu(e);
      }

      @Override
      public void mousePressed(MouseEvent e) {
        showPopupMenu(e);
      }

      @Override
      public void mouseReleased(MouseEvent e) {
        showPopupMenu(e);
      }

      @Override
      public void mouseEntered(MouseEvent e) {
      }

      @Override
      public void mouseExited(MouseEvent e) {
      }

      private void showPopupMenu(MouseEvent e) {
        if (e.isPopupTrigger()) {
          JPopupMenu popupMenu = new JPopupMenu();

          JMenuItem menuItem = new JMenuItem("Clear");
          menuItem.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent evt) {
              // clear the content in the text area
              outputTextArea.setText("");
            }

          });
          popupMenu.add(menuItem);

          // popupMenu.addSeparator();
          popupMenu.show(e.getComponent(), e.getX(), e.getY());
        }

      }
    });

    // TODO: add an menu bar for this very tab, containing a font size spinner
    // and other editor functions in future...

    editorScroller = new JScrollPane(new ScrollableEditorPanel(this.editorPane));
    editorScroller.setRowHeaderView(new LineNumberMargin(this.editorPane));

    JPanel content = new JPanel(new BorderLayout());

    content.add(this.getMenu(), BorderLayout.PAGE_START);
    content.add(editorScroller, BorderLayout.CENTER);

    this.add(content, JSplitPane.TOP);
    this.add(new JScrollPane(this.outputTextArea), JSplitPane.BOTTOM);

    // workaround since setDividerLocation(0.5D) doesn't work at all
    int dividerLocation = (this.getToolkit().getScreenSize().height / 3) + 125;

    /**
     * TODO: workaround for the setDividerLocation() Bug ID: 4182558
     * 
     * http://bugs.sun.com/bugdatabase/view_bug.do?bug_id=4182558
     */

    this.validate();
    this.setDividerLocation(dividerLocation);
  }

  private void initEditor(boolean lineNumbering, int tabSize,
      boolean lineWrapping) {
    this.editorPane = new JEditorPane();
    this.editorPane.setEditable(false);
    this.editorPane.addMouseListener(new GuiMouseListener(actionListener) {
      public void showPopupMenu(MouseEvent e) {

        if (e.isPopupTrigger()) {
          JPopupMenu popupMenu = new JPopupMenu();

          JMenuItem menuItem;

          if (!editorPane.isEnabled()) {
            // menu item to reload the simulation description from the project
            menuItem = new JMenuItem(TabSimDescription.CONTEXT_MENU_ITEM_RELOAD);
            menuItem.addActionListener(this.actionListener);
            popupMenu.add(menuItem);

            popupMenu.addSeparator();
          }

          // menu item to open the preferences dialog
          // menuItem = new JMenuItem(
          // AORJavaGui.CONTEXT_MENU_ITEM_PREFERENCES);
          // menuItem.addActionListener(this.actionListener);
          // popupMenu.add(menuItem);

          popupMenu.show(e.getComponent(), e.getX(), e.getY());
        }

      }
    });

    XMLEditorKit xmlEditorKit = new XMLEditorKit();
    // this.xmlEditorKit.setLineWrappingEnabled(lineWrapping);
    xmlEditorKit.setAutoIndentation(true);
    // xmlEditorKit.setTagCompletion(true);
    this.editorPane.setEditorKit(xmlEditorKit);

    // set the tab space
    this.editorPane.getDocument().putProperty(PlainDocument.tabSizeAttribute,
        tabSize);
  }

  private JToolBar getMenu() {
    JToolBar menu = new JToolBar(JToolBar.HORIZONTAL);

    // JLabel lineNumberingLabel = new JLabel("Line Numbering: ");
    JCheckBox lineNumbering = new JCheckBox("", true);
    lineNumbering.addActionListener(new ActionListener() {

      public void actionPerformed(ActionEvent e) {
        JCheckBox cb = (JCheckBox) e.getSource();

        String text = new String(editorPane.getText());

        editorScroller = new JScrollPane(new ScrollableEditorPanel(editorPane));
        if (cb.isSelected()) {
          editorScroller = new JScrollPane(
              new ScrollableEditorPanel(editorPane));

          editorScroller.setRowHeaderView(new LineNumberMargin(editorPane));

        } else {
          editorScroller = new JScrollPane(editorPane);
          editorScroller.setRowHeaderView(editorPane);
        }

        initEditor(cb.isSelected(), (Integer) tabSize.getSelectedItem(), true);
        editorPane.setText(text);
      }

    });

    JLabel fontNameLabel = new JLabel("  Font: ");
    JComboBox fontName = new JComboBox(GraphicsEnvironment
        .getLocalGraphicsEnvironment().getAvailableFontFamilyNames());
    fontName.addActionListener(new ActionListener() {
      public void actionPerformed(ActionEvent e) {
        JComboBox cb = (JComboBox) e.getSource();
        String name = (String) cb.getSelectedItem();
        editorPane.setFont(new Font(name, Font.PLAIN, editorPane.getFont()
            .getSize()));
      }

    });

    JComboBox fontSize = new JComboBox(new Integer[] { 12, 14, 16, 18, 20 });
    fontSize.addActionListener(new ActionListener() {
      public void actionPerformed(ActionEvent e) {
        JComboBox cb = (JComboBox) e.getSource();
        int size = (Integer) cb.getSelectedItem();

        editorPane.setFont(new Font(editorPane.getFont().getFontName(),
            Font.PLAIN, size));
      }

    });

    // JLabel tabSizeLabel = new JLabel("  Tab Size: ");
    tabSize = new JComboBox(new Integer[] { 1, 2, 3, 4, 5 });
    tabSize.setSelectedIndex(1);
    tabSize.addActionListener(new ActionListener() {
      public void actionPerformed(ActionEvent e) {
        JComboBox cb = (JComboBox) e.getSource();
        int size = (Integer) cb.getSelectedItem();
        String text = editorPane.getText();
        initEditor(true, size, true);
        editorPane.setText(text);
      }

    });

    // menu.add(lineNumberingLabel);
    // menu.add(lineNumbering);

    menu.add(fontNameLabel);
    menu.add(fontName);
    menu.add(fontSize);

    // menu.add(tabSizeLabel);
    // menu.add(tabSize);

    return menu;
  }

  /**
   * Usage:
   * 
   * 
   * Comments: Get the {@code name}.
   * 
   * 
   * 
   * @return the {@code name}.
   */
  public String getName() {
    return name;
  }

  /**
   * Usage:
   * 
   * 
   * Comments: Set the {@code name}.
   * 
   * 
   * 
   * @param name
   *          The {@code name} to set.
   */
  public void setName(String name) {
    this.name = name;
  }

  /**
   * Usage:
   * 
   * 
   * Comments: Get the {@code editorPane}.
   * 
   * 
   * 
   * @return the {@code editorPane}.
   */
  public JEditorPane getEditorPane() {
    return editorPane;
  }

  /**
   * Usage:
   * 
   * 
   * Comments: Set the {@code editorPane}.
   * 
   * 
   * 
   * @param editorPane
   *          The {@code editorPane} to set.
   */
  public void setEditorPane(JTextPane textPane) {
    this.editorPane = textPane;
  }

  /**
   * Usage:
   * 
   * 
   * Comments: Get the {@code outputTextArea}.
   * 
   * 
   * 
   * @return the {@code outputTextArea}.
   */
  public JTextPane getOutputTextPane() {
    return outputTextArea;
  }

  /**
   * Usage:
   * 
   * 
   * Comments: Set the {@code outputTextArea}.
   * 
   * 
   * 
   * @param outputTextPane
   *          The {@code outputTextArea} to set.
   */

  public void setOutputTextPane(JTextPane outputTextPane) {
    this.outputTextArea = outputTextPane;
  }

}
