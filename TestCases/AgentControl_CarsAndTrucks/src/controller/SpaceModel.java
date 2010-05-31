package controller;

 
public class SpaceModel extends aors.GeneralSpaceModel {
  private aors.space.OneDimensional space;

  public SpaceModel(Dimensions dimensions) {
    super(dimensions, aors.GeneralSpaceModel.SpaceType.OneD);
  }

  public aors.space.OneDimensional getSpace() {
    return this.space;
  }

  public void initSpace() {
    this.space = new aors.space.OneDimensional(this.getXMax());
  }
}
