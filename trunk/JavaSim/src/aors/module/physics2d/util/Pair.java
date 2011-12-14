/**
 * 
 */
package aors.module.physics.util;

/**
 * A simple pair of objects.
 * 
 * @author Holger Wuerke
 * @since 20.01.2010
 */
public class Pair<T, S> {
  private T first;
  private S second;

  public Pair(T first, S second) {
    this.first = first;
    this.second = second;
  }

  public T getFirst() {
    return first;
  }

  public S getSecond() {
    return second;
  }

  @SuppressWarnings("unchecked")
  public boolean equals(Object other) {
    if (other instanceof Pair) {
      return (((Pair<T, S>) other).getFirst().equals(first) && ((Pair<T, S>) other)
          .getSecond().equals(second));
    }

    return false;
  }

  public String toString() {
    return "(" + first.toString() + ", " + second.toString() + ")";
  }

}
