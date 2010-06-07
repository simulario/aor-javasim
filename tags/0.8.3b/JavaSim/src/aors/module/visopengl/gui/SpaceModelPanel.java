package aors.module.visopengl.gui;

import java.awt.FlowLayout;

import javax.swing.JLabel;
import javax.swing.JPanel;

import aors.GeneralSpaceModel.Geometry;
import aors.GeneralSpaceModel.SpaceType;
import aors.GeneralSpaceModel.SpatialDistanceUnit;

/**
 * Panel containing information about the space model.
 * 
 * @author Sebastian Mucha
 * @since March 1st, 2010
 * 
 */
public class SpaceModelPanel extends JPanel {

  private static final long serialVersionUID = 8825082104734239414L;

  // Space type label
  JLabel spaceType = new JLabel("Type:");

  // Geometry label
  JLabel geometry = new JLabel("Geometry:");

  // Spatial distance unit label
  JLabel distanceUnit = new JLabel("Distance Unit:");

  // Multiplicity label
  JLabel multiplicity = new JLabel("Multiplicity:");

  // Dimension labels
  JLabel xMax = new JLabel("xMax:");
  JLabel yMax = new JLabel("yMax:");

  /**
   * Creates a panel containing information about the space model.
   */
  public SpaceModelPanel() {
    setLayout(new FlowLayout(FlowLayout.LEADING, 20, 5));

    // Add components to the panel
    add(spaceType);
    add(geometry);
    add(distanceUnit);
    add(multiplicity);
    add(xMax);
    add(yMax);
  }

  /**
   * Updates the text of the space type label.
   * 
   * @param type
   *          Type of the space model.
   */
  public void updateSpaceTypeLabel(SpaceType type) {
    spaceType.setText("Type: " + type.toString());
  }

  /**
   * Updates the text of the geometry label.
   * 
   * @param type
   *          Type of geometry.
   */
  public void updateGeometryLabel(Geometry type) {
    geometry.setText("Geometry: " + type.toString());
  }

  /**
   * Updates the text of the distance unit label.
   * 
   * @param unit
   *          Distance unit.
   */
  public void updateDistanceUnitLabel(SpatialDistanceUnit unit) {
    distanceUnit.setText("Distance Unit: " + unit.toString());
  }

  /**
   * Updates the text of the multiplicity label.
   * 
   * @param value
   *          Spaces multiplicity.
   */
  public void updateMultiplicityLabel(int value) {
    multiplicity.setText("Multiplicity: " + value);
  }

  /**
   * Updates the text of the xMax label.
   * 
   * @param type
   *          Horizontal dimension of the space.
   */
  public void updateXMaxLabel(double value) {
    xMax.setText("xMax: " + value);
  }

  /**
   * Updates the text of the yMax label.
   * 
   * @param value
   *          Vertical dimension of the space.
   */
  public void updateYMaxLabel(double value) {
    yMax.setText("yMax: " + value);
  }

}
