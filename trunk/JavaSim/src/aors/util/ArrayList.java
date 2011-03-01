package aors.util;

import java.util.List;
import java.util.NoSuchElementException;

/**
 * ArrayList
 * 
 * @author Jens Werner
 * @since 28.02.2011
 * @version $Revision: 1.0 $
 */
public class ArrayList<E> extends java.util.ArrayList<E> implements
    AORArrayList<E>, List<E> {

  private static final long serialVersionUID = 1430027927105074835L;

  /**
   * Inserts the specified element at the beginning of this list.
   */
  @Override
  public void addFirst(E element) {
    this.add(1, element);
  }

  /**
   * Removes and returns the first element from this list.
   */
  @Override
  public E removeFirst() {

    if (this.size() == 0)
      throw new NoSuchElementException();

    return this.remove(0);
  }

  /**
   * Removes and returns the last element from this list.
   */
  @Override
  public E removeLast() {

    if (this.size() == 0)
      throw new NoSuchElementException();

    return this.remove(this.size() - 1);
  }

}
