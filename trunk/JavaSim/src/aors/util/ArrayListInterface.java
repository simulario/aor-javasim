package aors.util;

import java.util.List;

/**
 * AORArrayList
 * 
 * @author Jens Werner
 * @since 28.02.2011
 * @version $Revision: 1.0 $
 */
public interface ArrayListInterface<E> extends List<E> {
  
  public void addFirst(E element);
  public void addLast(E element);
  public E removeFirst();
  public E removeLast();
  public boolean removeObject(E element);
  public void insert(int index, E element);

}
