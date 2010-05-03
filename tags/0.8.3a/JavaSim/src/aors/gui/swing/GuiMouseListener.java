package aors.gui.swing;

import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

public abstract class GuiMouseListener implements MouseListener {

  protected ActionListener actionListener;

  public GuiMouseListener(ActionListener actionListener) {
    this.actionListener = actionListener;
  }

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

  protected abstract void showPopupMenu(MouseEvent e);

}
