package aors.module.visopengl.engine;

import java.awt.Point;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;

/**
 * Event handler for mouse events.
 * 
 * @author Sebastian Mucha
 * @since March 16th, 2010
 * 
 */
public class MouseEventHandler implements MouseListener, MouseMotionListener {

  // Engine
  private Engine engine;

  @Override
  public void mouseClicked(MouseEvent e) {
    engine.setPickPoint(new Point(e.getX(), e.getY()));
    engine.setPickingMode(true);
  }

  @Override
  public void mouseEntered(MouseEvent e) {
  }

  @Override
  public void mouseExited(MouseEvent e) {
  }

  @Override
  public void mousePressed(MouseEvent e) {
  }

  @Override
  public void mouseReleased(MouseEvent e) {
  }

  @Override
  public void mouseDragged(MouseEvent e) {
  }

  @Override
  public void mouseMoved(MouseEvent e) {
  }

  public Engine getEngine() {
    return engine;
  }

  public void setEngine(Engine engine) {
    this.engine = engine;
  }

}
