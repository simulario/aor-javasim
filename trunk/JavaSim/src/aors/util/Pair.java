package aors.util;

/**
 * This class represents a simple pair of values.
 * @para M the first value's type
 * @para N the second value's type
 * @author Thomas Grundmann
 */
public class Pair<M, N> {

	/**
	 * The first value.
	 * @type M
	 */
	public final M value1;

	/**
	 * The second value.
	 * @type N
	 */
	public final N value2;

	/**
	 * Creates a new pair of values.
	 * @param value1
	 * @param value2
	 */
	public Pair(M value1, N value2) {
		this.value1 = value1;
		this.value2 = value2;
	}

	/**
	 * Returns a string representation of the pair.
	 * @return the string representation
	 */
	@Override
	public String toString() {
		return "(" + this.value1 + ", " + this.value2 + ")";
	}
}
