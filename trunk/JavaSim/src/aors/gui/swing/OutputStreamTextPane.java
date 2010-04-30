package aors.gui.swing;

import java.awt.Color;
import java.awt.Font;
import java.io.IOException;
import java.io.OutputStream;

import javax.swing.JTextPane;
import javax.swing.text.BadLocationException;
import javax.swing.text.Document;
import javax.swing.text.SimpleAttributeSet;
import javax.swing.text.StyleConstants;

/**
 * 
 * OutputStreamTextPane
 * 
 * @author Andreas Post, Marco Pehla, Jens Werner
 * @since 11.09.2008
 * @version $Revision: 1.0 $
 */
public class OutputStreamTextPane extends OutputStream {

  private JTextPane pane;
  private Color color;
  private Font font;
  private SimpleAttributeSet attributes = new SimpleAttributeSet();
  private Document doc;
  private byte littlebuf[] = new byte[1];

  /**
   * Creates a new instance of OutputStreamTextPane.
   * 
   * @param pane
   *          the text pane to use for output
   * @param color
   *          the color of the printed text
   */
  public OutputStreamTextPane(JTextPane pane, Color color, Font font) {
    this.pane = pane;
    this.color = color;
    this.font = font;

    StyleConstants.setForeground(attributes, this.color);
    StyleConstants.setBold(attributes, this.font.isBold());
    StyleConstants.setItalic(attributes, this.font.isItalic());
    pane.setCharacterAttributes(attributes, true);
    pane.setFont(this.font);
    pane.setVisible(true);

    this.doc = this.pane.getStyledDocument();
  }

  /**
   * Writes the specified int to this output stream.
   * 
   * @param b
   *          an integer to print to this stream
   * @throws java.io.IOException
   *           in case of any errors
   */
  public void write(int b) throws IOException {
    littlebuf[0] = (byte) b;
    String s = new String(littlebuf, 0, 1);
    StyleConstants.setForeground(attributes, color);
    try {
      doc.insertString(doc.getLength(), s, attributes);
      // this.scrollToBottom();
    } catch (BadLocationException ex) {
    }

  }

  /**
   * Writes b.length bytes from the specified byte array to this output stream.
   * 
   * @param b
   *          the byte array
   * @throws java.io.IOException
   *           case of any errors
   */
  public void write(byte b[]) throws IOException {
    String s = new String(b, 0, b.length);
    StyleConstants.setForeground(attributes, color);
    try {
      doc.insertString(doc.getLength(), s, attributes);
      // this.scrollToBottom();
    } catch (BadLocationException ex) {
    }

  }

  /**
   * Writes len bytes from the specified byte array starting at offset off to
   * this output stream.
   * 
   * @param b
   *          the byte array
   * @param off
   *          the offset value
   * @param len
   *          the length
   * @throws java.io.IOException
   *           in case of any errors
   */
  public void write(byte b[], int off, int len) throws IOException {
    String s = new String(b, off, len);
    StyleConstants.setForeground(attributes, color);
    try {
      doc.insertString(doc.getLength(), s, attributes);
      // this.scrollToBottom();
    } catch (BadLocationException ex) {
    }

  }

  /**
   * Flushes this output stream and forces any buffered output bytes to be
   * written out.
   * 
   * @throws java.io.IOException
   *           in case of any errors
   */
  public void flush() throws IOException {
  }

  /**
   * Closes this output stream and releases any system resources associated with
   * this stream.
   * 
   * @throws java.io.IOException
   *           in case of any errors
   */
  public void close() throws IOException {
  }

  /**
   * this method does'nt work correct; if there is to much output, then the
   * system could freeze TODO: search a better solution to scrolling the pane to
   * bottom
   */
  // private void scrollToBottom() {
  // this.pane.setCaretPosition(this.doc.getLength());
  // }

}
