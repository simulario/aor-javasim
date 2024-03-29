package aors.module.visopengl.space.model;

import javax.media.opengl.GL2;
import javax.media.opengl.glu.GLU;

import aors.GeneralSpaceModel.SpaceType;
import aors.module.visopengl.space.component.Track;
import aors.module.visopengl.space.view.Alignment;
import aors.module.visopengl.space.view.OneDimSpaceView;
import aors.module.visopengl.space.view.SpaceView;

/**
 * The OneDimensional class is a specialization of the general space model
 * class, describing a one dimensional space model and its attributes.
 * 
 * @author Sebastian Mucha
 * @since February 15th, 2010
 * 
 */
public class OneDimSpaceModel extends SpaceModel {

  // Space view
  private OneDimSpaceView oneDimSpaceView;

  /**
   * Create a new OneDimensional instance and initialize its members.
   */
  public OneDimSpaceModel() {
    spaceType = SpaceType.OneD;
  }

  @Override
  public void initializeSpaceModel(GL2 gl, GLU glu) {
    // Clear space component list
    if (spaceComponents != null) {
      spaceComponents.clear();
    }

    // Set up the space components with respect to the alignment
    if (oneDimSpaceView.getAlignment().equals(Alignment.horizontal)
        || oneDimSpaceView.getAlignment().equals(Alignment.vertical)) {
      initializeLinearTracks();
    } else {
      initializeCircularTracks();
    }
  }

  /**
   * Creates and initializes linear space components.
   */
  private void initializeLinearTracks() {
    // Minimal distance between tracks
    final double MIN_DISTANCE = 30;

    // Distance between tracks
    double distance = MIN_DISTANCE;

    if (oneDimSpaceView.getAlignment().equals(Alignment.horizontal))
      distance = drawingArea.getHeight() / multiplicity;
    else
      distance = drawingArea.getWidth() / multiplicity;

    // Make sure the distance is not smaller than the minimal distance
    if (distance < MIN_DISTANCE)
      distance = MIN_DISTANCE;

    // Determine the track width
    double trackWidth = computeTrackWidth();

    /*
     * Calculate the position of the first track, taking into account if the
     * multiplicity is even or not.
     */
    double position[] = new double[2];

    if (multiplicity % 2 != 0) {
      if (oneDimSpaceView.getAlignment().equals(Alignment.horizontal)) {
        position[0] = drawingArea.x1;
        position[1] = -distance * (multiplicity / 2);
      } else {
        position[0] = distance * (multiplicity / 2);
        position[1] = drawingArea.y1;
      }
    } else {
      if (oneDimSpaceView.getAlignment().equals(Alignment.horizontal)) {
        position[0] = drawingArea.x1;
        position[1] = (-distance * (multiplicity / 2)) + (distance / 2);
      } else {
        position[0] = (distance * (multiplicity / 2)) - (distance / 2);
        position[1] = drawingArea.y1;
      }
    }

    // Create the tracks and set up their attributes
    for (int i = 0; i < multiplicity; i++) {
      if (oneDimSpaceView.getAlignment().equals(Alignment.horizontal)) {
        spaceComponents.add(new Track(position[0], position[1], drawingArea.x2,
            position[1]));

        // Adjust the position for the next track
        position[1] += distance;
      } else {
        spaceComponents.add(new Track(position[0], position[1], position[0],
            drawingArea.y2));

        // Adjust the position for the next track
        position[0] -= distance;
      }

      // Set up the tracks properties
      ((Track) spaceComponents.get(i)).setTrackColor(oneDimSpaceView
          .getTrackColor());
      ((Track) spaceComponents.get(i)).setTrackWidth(trackWidth);
      ((Track) spaceComponents.get(i)).setAlignment(oneDimSpaceView
          .getAlignment());
      ((Track) spaceComponents.get(i)).setSpaceModel(this);
      ((Track) spaceComponents.get(i)).setupTrackDimensions(xMax);
    }
  }

  /**
   * Creates and initializes circular space components.
   */
  private void initializeCircularTracks() {
    // Minimal distance between tracks
    final double MIN_DISTANCE = 30;

    // Distance between tracks
    double distance = (drawingArea.getHeight() / 2) / multiplicity;

    // Distance in percent
    double distancePercentage = distance / (drawingArea.getHeight() / 2);

    // Make sure the distance is not smaller than the minimal distance
    if (distance < MIN_DISTANCE)
      distance = MIN_DISTANCE;

    // Determine the track width
    double trackWidth = computeTrackWidth();

    // Radius of the outside track
    double radius = distance * multiplicity;

    // Centers of the half circles
    double leftCenter[] = new double[2];
    double rightCenter[] = new double[2];

    // Displacement between both circle centers
    double displacement = (0.5 * drawingArea.getWidth());

    // Make sure that the displacement is not too small
    if (displacement < 50)
      displacement = 50;

    // Both centers are vertically centered on the screen
    leftCenter[1] = rightCenter[1] = 0;

    // Horizontal position of the the circle centers
    leftCenter[0] = -displacement / 2;
    rightCenter[0] = displacement / 2;

    double worldLength = rightCenter[0] - leftCenter[0];
    double worldCircumference = (Math.PI / 2) * 2 * radius;
    double ratio = worldLength / worldCircumference;
    double largestSpaceLength = (ratio / 2) * (xMax / 2);

    // Create the tracks and set up their attributes
    for (int i = 0; i < multiplicity; i++) {
      Track track = new Track(leftCenter[0], leftCenter[1], rightCenter[0],
          rightCenter[1], radius);

      // Set up the tracks properties
      track.setTrackColor(oneDimSpaceView.getTrackColor());
      track.setTrackWidth(trackWidth);
      track.setAlignment(oneDimSpaceView.getAlignment());
      track.setDistancePercentage(distancePercentage);
      track.setGlobalRatio(ratio);
      track.setSpaceModel(this);
      track.setupTrackDimensions(xMax);
      track.setLargestSpaceLength(largestSpaceLength);

      // Add the track into the list
      spaceComponents.add(track);

      // Adjust the radius of the next track
      radius -= distance;
    }
  }

  private double computeTrackWidth() {
    if (oneDimSpaceView.getAbsoluteTrackWidth() != 0
        && oneDimSpaceView.getRelativeTrackWidth() != 0) {
      return oneDimSpaceView.getAbsoluteTrackWidth();
    }

    else if (oneDimSpaceView.getAbsoluteTrackWidth() != 0) {
      return oneDimSpaceView.getAbsoluteTrackWidth();
    }

    else if (oneDimSpaceView.getRelativeTrackWidth() != 0) {
      if (oneDimSpaceView.getAlignment().equals(Alignment.horizontal)) {
        return (oneDimSpaceView.getRelativeTrackWidth() * drawingArea
            .getHeight()) / 100;
      } else {
        return (oneDimSpaceView.getRelativeTrackWidth() * drawingArea
            .getWidth()) / 100;
      }
    }

    return 1;
  }

  @Override
  public SpaceView getSpaceView() {
    return oneDimSpaceView;
  }

  @Override
  public void setSpaceView(SpaceView spaceView) {
    oneDimSpaceView = (OneDimSpaceView) spaceView;
  }

}
