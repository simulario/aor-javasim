package aors.module.visopengl3d.space.model;

import javax.media.opengl.GL2;
import javax.media.opengl.glu.GLU;

import aors.GeneralSpaceModel.SpaceType;
import aors.module.visopengl3d.space.component.Margin;
import aors.module.visopengl3d.space.view.SpaceView;
import aors.module.visopengl3d.space.view.TwoDimSpaceView;

/**
 * Two dimensional, continuous space model.
 * 
 * @author Sebastian Mucha
 * @since March 25th, 2010
 * 
 */
public class TwoDimSpaceModel extends SpaceModel {

  // Space view
  private TwoDimSpaceView twoDimSpaceView;

  /**
   * Creates a two dimensional, continuous space model.
   */
  public TwoDimSpaceModel() {
    spaceType = SpaceType.TwoD;
  }

  @Override
  public void initializeSpaceModel(GL2 gl, GLU glu) {
    // Clear space component list
    if (spaceComponents != null) {
      spaceComponents.clear();
    }

    // Create a margin
    Margin margin = new Margin(drawingArea.x1, drawingArea.y1, drawingArea.x2,
        drawingArea.y2);
    margin.setSpaceModel(this);
    margin.setBackgroundImg(twoDimSpaceView.getBackgroundImg());
    margin.setBackgroundColor(twoDimSpaceView.getBackgroundColor());
    margin.setBorderColor(twoDimSpaceView.getBorderColor());

    // Add the margin into the space components list
    spaceComponents.add(margin);
  }

  @Override
  public SpaceView getSpaceView() {
    return twoDimSpaceView;
  }

  @Override
  public void setSpaceView(SpaceView spaceView) {
    twoDimSpaceView = (TwoDimSpaceView) spaceView;
  }

}
