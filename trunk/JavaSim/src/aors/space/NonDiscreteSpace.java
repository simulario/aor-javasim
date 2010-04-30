package aors.space;

public abstract class NonDiscreteSpace extends Space {

  public abstract NonDiscretePositionData getRandomPosition();

  public abstract class NonDiscretePositionData {

    protected double x;
    protected double y;
    protected double z;

    public NonDiscretePositionData(double x, double y, double z) {
      this.x = x;
      this.y = y;
      this.z = z;
    }

    /**
     * @return the x
     */
    public double getX() {
      return x;
    }

    /**
     * @return the y
     */
    public double getY() {
      return y;
    }

    /**
     * @return the z
     */
    public double getZ() {
      return z;
    }

  }

}
