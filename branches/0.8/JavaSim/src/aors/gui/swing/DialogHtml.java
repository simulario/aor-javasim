package aors.gui.swing;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.Frame;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JEditorPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.event.HyperlinkEvent;
import javax.swing.event.HyperlinkListener;
import javax.swing.text.html.HTMLEditorKit;

import edu.stanford.ejalbert.BrowserLauncher;
import edu.stanford.ejalbert.exception.BrowserLaunchingInitializingException;
import edu.stanford.ejalbert.exception.UnsupportedOperatingSystemException;

public class DialogHtml extends JDialog implements HyperlinkListener {

  private static final long serialVersionUID = -239470564092747938L;

  public final String DIALOG_ABOUT_OK_BUTTON = "Dialog About Ok Button";
  private final String htmlContent;
  private Frame owner;

  public DialogHtml(Frame owner, String htmlContent, String title) {
    super(owner, true);
    this.owner = owner;
    this.htmlContent = htmlContent;
    this.setTitle(title);
    this.setSize(500, 500);

    // when no owner is able to handle the window closing
    if (owner == null) {
      addWindowListener(new WindowAdapter() {
        public void windowClosing(WindowEvent e) {
          System.exit(0);
        }
      });
    }

    // calculate the dialog window position
    double percent = 0.5;
    int windowWidth = (int) (this.getToolkit().getScreenSize().width * percent);
    int windowHeight = (int) (this.getToolkit().getScreenSize().height * percent);

    // set size to ?% of the screen size dimensions
    this.setSize(windowWidth, windowHeight);

    // centre the window
    this.setLocation(
        (this.getToolkit().getScreenSize().width - windowWidth) / 2, (this
            .getToolkit().getScreenSize().height - windowHeight) / 2);

    JPanel panel = new JPanel(new BorderLayout());

    panel.add(new JScrollPane(this.getHTMLPanel()), BorderLayout.CENTER);
    panel.add(this.getButtonPanel(), BorderLayout.SOUTH);

    this.add(panel);
    this.setVisible(true);
  }

  private JEditorPane getHTMLPanel() {
    JEditorPane panel = new JEditorPane();
    panel.setEditorKit(new HTMLEditorKit());
    panel.setText(this.htmlContent);
    panel.setEditable(false);
    panel.setCaretPosition(0);
    panel.addHyperlinkListener(this);

    return panel;
  }

  private JPanel getButtonPanel() {
    JPanel panel = new JPanel();

    panel.setLayout(new FlowLayout(FlowLayout.LEFT));
    panel.setSize(200, 100);

    JButton okButton = new JButton("Ok");
    okButton.setActionCommand(DIALOG_ABOUT_OK_BUTTON);
    okButton.addActionListener(new ActionListener() {
      @Override
      public void actionPerformed(ActionEvent evt) {
        setVisible(false);

        // when no owner is able to handle the window closing
        if (owner == null) {
          // exit the application
          System.exit(0);
        }
      }
    });

    panel.add(okButton);

    return panel;
  }

  @Override
  public void hyperlinkUpdate(HyperlinkEvent e) {

    if (e.getEventType() == HyperlinkEvent.EventType.ACTIVATED) {
      try {
        BrowserLauncher browserLauncher = new BrowserLauncher();
        browserLauncher.openURLinBrowser(e.getURL().toExternalForm());
      } catch (BrowserLaunchingInitializingException e1) {
        // TODO Auto-generated catch block
        // e1.printStackTrace();
        System.err.println(e1.getLocalizedMessage());
      } catch (UnsupportedOperatingSystemException e1) {
        // TODO Auto-generated catch block
        // e1.printStackTrace();
        System.err.println(e1.getLocalizedMessage());
      }
    }
  }

}
