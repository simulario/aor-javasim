/**
 * 
 */
package examples.carsandtrucks.controller;

import aors.GeneralSpaceModel;
import aors.space.Space;

/**
 * We need this class, for spacemodelextensions; if we don't have a grid (or
 * other extensions) than it is empty; otherwise we have here e.g. for the real
 * GridCellImplementation
 * 
 * @author Jens Werner
 * @since May 25, 2008
 * @version $Revision$
 * 
 */
public class SpaceModel extends GeneralSpaceModel {

  public SpaceModel(Dimensions dimensions) {
    super(dimensions);
  }

  @Override
  public Space getSpace() {
    // TODO Auto-generated method stub
    return null;
  }

  public void initSpace(double size, double size2, double size3) {
    // TODO Auto-generated method stub

  }

}
