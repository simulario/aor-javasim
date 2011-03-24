package aors.util;

import java.util.Collection;
import java.util.NoSuchElementException;

/**
 * ArrayList
 * 
 * @author Jens Werner
 * @since 28.02.2011
 * @version $Revision: 1.0 $
 */
public class ArrayList<E> extends java.util.ArrayList<E> implements
    ArrayListInterface<E> {

  private static final long serialVersionUID = 1430027927105074835L;
  
  public ArrayList() {
    super();
  }
  
  public ArrayList(int initialCapacity) {
    super(initialCapacity);
  }
  
  public ArrayList(Collection<? extends E> c) {
    super(c);
  }
  

  /**
   * Inserts the specified element at the beginning of this list.
   */
  @Override
  public void addFirst(E element) {
    this.add(0, element);
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

  @Override
  public void addLast(E element) {
    this.add(element);
  }

  public boolean removeObject(E element) {
    return this.remove(element);
  }

  public void assign(int index, E element) {
    this.add(index, element);
  }

}
