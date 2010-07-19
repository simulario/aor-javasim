package aors.module.visopengl.gui;

import java.awt.FlowLayout;

import javax.swing.JLabel;
import javax.swing.JPanel;

import aors.GeneralSpaceModel.Geometry;
import aors.GeneralSpaceModel.SpaceType;
import aors.GeneralSpaceModel.SpatialDistanceUnit;
import aors.module.visopengl.lang.LanguageManager;

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
  private JLabel spaceTypeLabel = new JLabel();

  // Geometry label
  private JLabel geometryLabel = new JLabel();

  // Spatial distance unit label
  private JLabel distanceUnitLabel = new JLabel();

  // Multiplicity label
  private JLabel multiplicityLabel = new JLabel();

  // Dimension labels
  private JLabel xMaxLabel = new JLabel();
  private JLabel yMaxLabel = new JLabel();

  // properties of the space shown in this panel
  private SpaceType spaceType;
  private Geometry geometry;
  SpatialDistanceUnit distanceUnit;
  int multiplicity = 1;
  double xMax = 0;
  double yMax = 0;

  /**
   * Creates a panel containing information about the space model.
   */
  public SpaceModelPanel() {
    setLayout(new FlowLayout(FlowLayout.LEADING, 20, 5));
    
    this.refreshGUI();

    // Add components to the panel
    add(spaceTypeLabel);
    add(geometryLabel);
    add(distanceUnitLabel);
    add(multiplicityLabel);
    add(xMaxLabel);
    add(yMaxLabel);
  }

  /**
   * Updates the text of the space type label.
   * 
   * @param type
   *          Type of the space model.
   */
  public void updateSpaceTypeLabel(SpaceType type) {
    this.spaceType = type;
    if(this.spaceType == null) {
      return;
    }
    spaceTypeLabel.setText(LanguageManager.getMessage("spaceType_LABEL") + ": "
        + type.toString());
  }

  /**
   * Updates the text of the geometry label.
   * 
   * @param type
   *          Type of geometry.
   */
  public void updateGeometryLabel(Geometry type) {
    this.geometry = type;
    if(this.geometry == null) {
      return;
    }
    geometryLabel.setText(LanguageManager.getMessage("geometry_LABEL") + ": "
        + type.toString());
  }

  /**
   * Updates the text of the distance unit label.
   * 
   * @param unit
   *          Distance unit.
   */
  public void updateDistanceUnitLabel(SpatialDistanceUnit unit) {
    this.distanceUnit = unit;
    if(this.distanceUnit == null) {
      return;
    }
    distanceUnitLabel.setText(LanguageManager.getMessage("distanceUnit_LABEL")
        + ": " + unit.toString());
  }

  /**
   * Updates the text of the multiplicity label.
   * 
   * @param value
   *          Spaces multiplicity.
   */
  public void updateMultiplicityLabel(int value) {
    this.multiplicity = value;
    multiplicityLabel.setText(LanguageManager.getMessage("multiplicity_LABEL")
        + ": " + value);
  }

  /**
   * Updates the text of the xMax label.
   * 
   * @param type
   *          Horizontal dimension of the space.
   */
  public void updateXMaxLabel(double value) {
    this.xMax = value;
    xMaxLabel.setText(LanguageManager.getMessage("xMax_LABEL") + ": " + value);
  }

  /**
   * Updates the text of the yMax label.
   * 
   * @param value
   *          Vertical dimension of the space.
   */
  public void updateYMaxLabel(double value) {
    this.yMax = value;
    yMaxLabel.setText(LanguageManager.getMessage("yMax_LABEL") + ": " + value);
  }

  /**
   * This method refresh this GUI component. That implies updating all language
   * dependent messages/labels used.
   */
  public void refreshGUI() {
    this.updateSpaceTypeLabel(this.spaceType);
    this.updateGeometryLabel(this.geometry);
    this.updateDistanceUnitLabel(this.distanceUnit);
    this.updateMultiplicityLabel(this.multiplicity);
    this.updateXMaxLabel(this.xMax);
    this.updateYMaxLabel(this.yMax);
  }
}
