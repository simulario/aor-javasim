package aors.module.visopengl3d.space.model;

import javax.media.opengl.GL2;
import javax.media.opengl.glu.GLU;

import aors.GeneralSpaceModel.SpaceType;
import aors.module.visopengl3d.space.component.Track;
import aors.module.visopengl3d.space.view.Alignment;
import aors.module.visopengl3d.space.view.OneDimSpaceView;
import aors.module.visopengl3d.space.view.SpaceView;
import aors.module.visopengl3d.utility.Offset;

/**
 * The OneDimensional class is a specialization of the general space model
 * class, describing a one dimensional space model and its attributes.
 * 
 * @author Sebastian Mucha, Susanne Schölzel
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
      initializeLinearTracks(gl, glu);
    } else {
      initializeCircularTracks(gl, glu);
    }
  }

  /**
   * Creates and initializes linear space components.
   */
  private void initializeLinearTracks(GL2 gl, GLU glu) {
    // Determine the track width
    double trackWidth = computeTrackWidth();
    
    // Minimal distance between middle lines of the tracks
    final double MIN_DISTANCE = trackWidth + 10;
    
    // distance between middle lines of the tracks
    double distance = MIN_DISTANCE;
    
    if (oneDimSpaceView.getAlignment().equals(Alignment.horizontal))
      distance = drawingArea.getHeight() / multiplicity;
    else
      distance = drawingArea.getWidth() / multiplicity;

    // Make sure the distance is not smaller than the minimal distance
    if (distance < MIN_DISTANCE) {
      distance = MIN_DISTANCE;
    }
    
    
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
      ((Track) spaceComponents.get(i)).setTrackHeight(oneDimSpaceView.getTrackHeight());
      ((Track) spaceComponents.get(i)).setAlignment(oneDimSpaceView
          .getAlignment());
      ((Track) spaceComponents.get(i)).setSpaceModel(this);
      ((Track) spaceComponents.get(i)).setupTrackDimensions(xMax);
      
      ((Track) spaceComponents.get(i)).generateDisplayList(gl, glu);
    }
    
    // Get the bottom left and top right corner of the used drawing area for the tracks
    if(oneDimSpaceView.getAlignment().equals(Alignment.horizontal)) {
      double x1 = ((Track) spaceComponents.get(0)).getOffset().x1;
      double y1 = ((Track) spaceComponents.get(0)).getOffset().y1;
      double x2 = ((Track) spaceComponents.get(multiplicity-1)).getOffset().x2;
      double y2 = ((Track) spaceComponents.get(multiplicity-1)).getOffset().y2;
      usedDrawingArea = new Offset(x1, y1, x2, y2);
    } else {
      double x1 = ((Track) spaceComponents.get(multiplicity-1)).getOffset().x1;
      double y1 = ((Track) spaceComponents.get(multiplicity-1)).getOffset().y1;
      double x2 = ((Track) spaceComponents.get(0)).getOffset().x2;
      double y2 = ((Track) spaceComponents.get(0)).getOffset().y2;
      usedDrawingArea = new Offset(x1, y1, x2, y2);
    }
  }

  /**
   * Creates and initializes circular space components.
   */
  private void initializeCircularTracks(GL2 gl, GLU glu) {
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
    
    usedDrawingArea = new Offset(leftCenter[0]-radius-trackWidth/2, leftCenter[1]-radius-trackWidth/2, 
                                 rightCenter[0]+radius+trackWidth/2, rightCenter[1]+radius+trackWidth/2);

    double worldLength = rightCenter[0] - leftCenter[0];
    double worldCircumference = (Math.PI / 2) * 2 * radius;
    double ratio = worldLength / worldCircumference;
    //double largestSpaceLength = (ratio / 2) * (xMax / 2);
    double largestSpaceLength = (ratio * xMax) / (2* (1+ratio));

    // Create the tracks and set up their attributes
    for (int i = 0; i < multiplicity; i++) {
      Track track = new Track(leftCenter[0], leftCenter[1], rightCenter[0],
          rightCenter[1], radius);

      // Set up the tracks properties
      track.setTrackColor(oneDimSpaceView.getTrackColor());
      track.setTrackWidth(trackWidth);
      track.setTrackHeight(oneDimSpaceView.getTrackHeight());
      track.setAlignment(oneDimSpaceView.getAlignment());
      track.setDistancePercentage(distancePercentage);
      track.setGlobalRatio(ratio);
      track.setSpaceModel(this);
      track.setupTrackDimensions(xMax);
      track.setLargestSpaceLength(largestSpaceLength);
      track.generateDisplayList(gl, glu);

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
