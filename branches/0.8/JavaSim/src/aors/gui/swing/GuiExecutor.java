package aors.gui.swing;

import java.util.List;
import java.util.concurrent.AbstractExecutorService;
import java.util.concurrent.TimeUnit;

import javax.swing.SwingUtilities;

/**
 * GuiExecutor - This class helps to execute methods in a separate thread as the
 * Swing GUI.
 * 
 * The implementation was taken from the book "Java Concurrency in Practice".
 * (Listing 9.2, s.194)
 * 
 * @author Marco Pehla
 * @since 25.08.2008
 * @version $Revision$
 */
public class GuiExecutor extends AbstractExecutorService {

  private static final GuiExecutor instance = new GuiExecutor();

  /**
   * 
   * Usage: This method return the only instance of the GuiExecutor.
   * 
   * 
   * Comments: see also: Singleton design pattern
   * 
   * 
   * 
   * @return
   */
  public static GuiExecutor instance() {
    return instance;
  }

  /**
   * Usage: The method can be used to execute {@Runnable} object in
   * the Swing thread.
   * 
   * 
   * Comments: Overrides method {@code execute} from super class
   * 
   * @param runnable
   */
  @Override
  public void execute(Runnable runnable) {
    if (SwingUtilities.isEventDispatchThread()) {
      runnable.run();
    } else {
      SwingUtilities.invokeLater(runnable);
    }

  }

  /**
   * Usage: not implemented
   * 
   * 
   * Comments: Overrides method {@code awaitTermination} from super class
   * 
   * 
   * 
   * @param arg0
   * @param arg1
   * @return
   * @throws InterruptedException
   */
  @Override
  public boolean awaitTermination(long arg0, TimeUnit arg1)
      throws InterruptedException {
    throw new UnsupportedOperationException();
  }

  /**
   * Usage: not implemented
   * 
   * 
   * Comments: Overrides method {@code isShutdown} from super class
   * 
   * 
   * 
   * @return
   */
  @Override
  public boolean isShutdown() {
    return false;
  }

  /**
   * Usage: not implemented
   * 
   * 
   * Comments: Overrides method {@code isTerminated} from super class
   * 
   * 
   * 
   * @return
   */
  @Override
  public boolean isTerminated() {
    return false;
  }

  /**
   * Usage: not implemented
   * 
   * 
   * Comments: Overrides method {@code shutdown} from super class
   * 
   * 
   * 
   */
  @Override
  public void shutdown() {
    // TODO Auto-generated method stub

  }

  /**
   * Usage: not implemented
   * 
   * 
   * Comments: Overrides method {@code shutdownNow} from super class
   * 
   * 
   * 
   * @return
   */
  @Override
  public List<Runnable> shutdownNow() {
    throw new UnsupportedOperationException();
  }

}
