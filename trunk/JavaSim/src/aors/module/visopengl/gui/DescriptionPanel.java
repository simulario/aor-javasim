package aors.module.visopengl.gui;

import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.border.EtchedBorder;

import aors.module.visopengl.lang.LanguageManager;

/**
 * Panel containing information about the space model.
 * 
 * @author Mircea Diaconescu
 * @since March 1st, 2010
 * 
 */
public class DescriptionPanel extends JScrollPane {
  /**
   * Serial UID required by the JPanel extension
   */
  private static final long serialVersionUID = 948510175725651028L;

  // the text contained in the description panel
  private final JLabel descriptionLabel = new JLabel();

  /**
   * Default constructor.
   */
  public DescriptionPanel() {
    super(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED,
        JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);

    descriptionLabel.setAlignmentX(JLabel.LEFT_ALIGNMENT);

    JPanel containerPanel = new JPanel();
    containerPanel.add(descriptionLabel);
    containerPanel.setBorder(new EtchedBorder());

    this.getViewport().add(containerPanel);
    this.setDescriptionData("<br/><br/><br/><center><b>"
        + LanguageManager.getMessage("messageNoDescription") + "<br/>"
        + LanguageManager.getMessage("messageNoViews")
        + "</center></b>");
  }

  /**
   * Set the description panel content. This may be simple text or HTML. Note
   * that the HTML will be interpreted by the Swing library, therefore it may be
   * the case that not all HTML elements are interpreted or correct displayed.
   * 
   * @param description
   *          the text/html to be displayed
   */
  public void setDescriptionData(String description) {
    this.descriptionLabel.setText("<html>" + description + "</html>");
  }
}
