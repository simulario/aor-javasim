package aors.util;

public class Pair<M, N> {

	public final M value1;
	public final N value2;

	public Pair(M value1, N value2) {
		this.value1 = value1;
		this.value2 = value2;
	}

	@Override
	public String toString() {
		return "(" + this.value1 + ", " + this.value2 + ")";
	}
}
