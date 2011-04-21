package aors.util.collection;

/**
 * @author Stefan Boecker
 * 
 *         Simple generic bounded buffer with a maximum capacity. If capacity is
 *         reached, a new item will overwrite the oldest one in the buffer.
 */
public class BoundedBuffer<E> {

  /**
   * Class represents one item in BoundedBuffer.
   * 
   * @author Stefan Boecker
   * 
   */
  private class ListItem {
    public E o;
    public ListItem next;
  }

  /**
   * Capacity of bounded buffer. If capacity is -1, bounded buffer has no limit.
   */
  private int capacity;

  private int size;

  private ListItem head = null;

  private ListItem tail = null;

  /**
   * Constructor of generic bounded buffer.
   * 
   * @param capacity
   *          Capacity of bounded buffer. Set it to -1 if you don't want a
   *          maximum capacity.
   */
  public BoundedBuffer(int capacity) {
    this.capacity = capacity;
    size = 0;
  }

  /**
   * Adds new item o to bounded buffer. If maximum capacity is reached, this new
   * item will overwrite the oldest one in the buffer.
   * 
   * @param o
   *          Item to add
   */
  public void add(E o) {

    // if there is no head, create one
    if (head == null) {
      ListItem item = new ListItem();
      item.o = o;
      item.next = null;
      size++;
      head = tail = item;

      return;
    }

    // if capacity is reached, forget first item (head) in list
    if (size >= capacity && capacity > 0) {
      head = head.next;
      size--;
    }

    // add new item to end of list
    tail.next = new ListItem();
    tail.next.o = o;
    tail.next.next = null;
    tail = tail.next;
    if (head == null)
      head = tail;
    size++;

  }

  /**
   * Get item with index i.
   * 
   * @param i
   *          Index of item.
   * @return Item
   */
  public E get(int i) {
    if (i >= size || i < 0) // index is greater than size of buffer
      return null;

    ListItem item = head;
    for (int j = 0; j != i; j++) {
      item = item.next;
    }

    return item.o;
  }

  /**
   * Removes an Item from bounded buffer.
   * 
   * @param o
   *          Item to remove
   * @return true, if item was removed. false, if item was not found in bounded
   *         buffer.
   */
  public boolean remove(E o) {

    ListItem item = head;
    ListItem prev = null;
    while (item != null) {
      if (item.o == o) {
        if (prev != null) {
          prev.next = item.next;
        }
        // item found - remove from list
        if (item == head) { // we have to remove head
          head = head.next;
        }

        if (item == tail) {
          tail = prev;
        }

        size--;
        return true;
      }
      prev = item;
      item = item.next;
    }

    return false;
  }

  /**
   * Remove item with index i.
   * 
   * @param i
   *          Index of item
   * @return true, if item was removed. false, if item was not found in bounded
   *         buffer.
   */
  public boolean remove(int i) {

    return remove(get(i));

  }

  /**
   * Returns current size of bounded buffer.
   * 
   * @return Current size of bounded buffer.
   */
  public int size() {
    return size;
  }

  public int capacity() {
    return capacity;
  }

  public boolean isEmpty() {
    return size == 0;
  }

  /*
   * public void debug() { ListItem item = head;
   * System.out.println("----------------------------------------------");
   * System.out.println("head: " + ((head!=null)?head.o:head) + " tail: " +
   * ((tail!=null)?tail.o:tail)); while(item != null) {
   * System.out.println("item: " + item.o); System.out.println(" |");
   * System.out.println("\\/"); item = item.next; } System.out.println("null");
   * System.out.println("----------------------------------------------"); }
   */

}
