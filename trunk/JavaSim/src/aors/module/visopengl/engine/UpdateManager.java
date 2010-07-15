package aors.module.visopengl.engine;

import java.io.File;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import javax.xml.bind.JAXBElement;

import org.w3c.dom.Node;

import aors.GeneralSpaceModel.SpaceType;
import aors.controller.InitialState;
import aors.logger.model.AgtType;
import aors.logger.model.EnvSimInputEventType;
import aors.logger.model.EnvironmentSimulatorStep;
import aors.logger.model.ObjType;
import aors.logger.model.ObjectType;
import aors.logger.model.PhysAgtType;
import aors.logger.model.PhysicalObjType;
import aors.logger.model.PhysicsSimulationType;
import aors.logger.model.ResultingStateChangesType;
import aors.logger.model.SimulationStep;
import aors.logger.model.EnvSimInputEventType.Activities;
import aors.logger.model.EnvSimInputEventType.Activities.FinalizeActivity;
import aors.logger.model.EnvSimInputEventType.Activities.StartActivity;
import aors.logger.model.PhysicsSimulationType.ResultingStateChanges;
import aors.logger.model.SimulationStep.AgentSimResultingStateChanges;
import aors.model.envsim.Objekt;
import aors.model.envsim.Physical;
import aors.model.envsim.PhysicalAgentObject;
import aors.model.envsim.PhysicalObject;
import aors.module.visopengl.shape.DisplayInfo;
import aors.module.visopengl.shape.Shape2D;
import aors.module.visopengl.shape.View;
import aors.module.visopengl.space.model.GridSpaceModel;
import aors.module.visopengl.space.model.SpaceModel;
import aors.module.visopengl.xml.XMLReader;

/**
 * Handles updates from simulation steps (also for grids).
 * 
 * @author Sebastian Mucha
 * @since March 20th, 2010
 * 
 */
public class UpdateManager {

  // Space model
  private SpaceModel spaceModel;

  // Project director
  private File projectDirectory;

  // Map containing view nodes and associated ID's or type
  private Map<String, Node> viewNodeMap;

  // Map containing the actual view of an object and it's associated ID
  private Map<Long, View> viewMap = new ConcurrentHashMap<Long, View>();

  // Map containing objects and their associated ID
  private Map<Long, Objekt> objMap = new ConcurrentHashMap<Long, Objekt>();

  // Map containing newly created object views and the step they where created
  // in
  private Map<Long, ArrayList<View>> createdMap = new ConcurrentHashMap<Long, ArrayList<View>>();

  // Map containing destroyed object IDs and the step they where destroyed in
  private Map<Long, ArrayList<Long>> destroyMap = new ConcurrentHashMap<Long, ArrayList<Long>>();

  /**
   * Clears all maps used by the UpdateManager.
   */
  public void reset() {
    if (viewNodeMap != null) {
      viewNodeMap.clear();
    }

    if (viewMap != null) {
      viewMap.clear();
    }

    if (objMap != null) {
      objMap.clear();
    }

    if (createdMap != null) {
      createdMap.clear();
    }

    if (destroyMap != null) {
      destroyMap.clear();
    }
  }

  /**
   * Retrieves all objects that are available at the simulations initial state
   * and stores a local copy of them inside of a hash map. Also the view that is
   * associated to an objects ID is loaded.
   * 
   * @param initialState
   * @param reader
   */
  public void initializeObjects(InitialState initialState, XMLReader reader) {
    // Retrieve a list of all objects
    List<Objekt> objList = initialState.getObjectsByType(Objekt.class);

    // Each element with its ID is stored inside of the object map
    for (Objekt obj : objList) {
      // Load the objects view
      loadObjView(obj.getId(), obj.getType(), reader);

      // Initial objects are always visible at the beginning
      if (viewMap.get(obj.getId()) != null) {
        viewMap.get(obj.getId()).setVisible(true);
      }

      // Get initial display info (if variables should be displayed)
      getInitialDisplayInfo(obj);

      if (obj instanceof PhysicalAgentObject) {
        PhysicalAgentObject physAgt = ((PhysicalAgentObject) obj).clone();
        objMap.put(physAgt.getId(), physAgt);

        // Determine initial shape (for Shape2DMaps)
        getInitialShape(obj);

        // Get initial shape properties
        getInitialShapeProperties(obj);

        // Get initial display info (for physical properties)
        getInitialDisplayInfo((Physical) physAgt);
      }

      else if (obj instanceof PhysicalObject) {
        PhysicalObject physObj = ((PhysicalObject) obj).clone();
        objMap.put(physObj.getId(), physObj);

        // Determine initial shape (for Shape2DMaps)
        getInitialShape(obj);

        // Get initial shape properties
        getInitialShapeProperties(obj);

        // Get initial display info (for physical properties)
        getInitialDisplayInfo((Physical) physObj);
      }

      else {
        Objekt nonPhysObj = (Objekt) obj.clone();
        objMap.put(obj.getId(), nonPhysObj);

        // Determine initial shape (for Shape2DMaps)
        getInitialShape(obj);

        // Get initial shape properties
        getInitialShapeProperties(obj);
      }
    }
  }

  /**
   * When an object is created during runtime, a local copy of it is stored and
   * its view is loaded.
   * 
   * @param source
   * @return id of created object
   */
  public Long initializeCreatedObject(Object source, XMLReader reader) {
    if (source instanceof Objekt) {
      Objekt obj = (Objekt) source;

      // Load the objects view
      loadObjView(obj.getId(), obj.getType(), reader);

      // Get initial display info
      getInitialDisplayInfo(obj);

      if (obj instanceof PhysicalAgentObject) {
        PhysicalAgentObject physAgt = ((PhysicalAgentObject) obj).clone();
        objMap.put(physAgt.getId(), physAgt);

        // Determine initial shape (for Shape2DMaps)
        getInitialShape(obj);

        // Get initial shape properties
        getInitialShapeProperties(obj);

        // Get initial display info (for physical properties)
        getInitialDisplayInfo((Physical) physAgt);
      }

      else if (obj instanceof PhysicalObject) {
        PhysicalObject physObj = ((PhysicalObject) obj).clone();
        objMap.put(physObj.getId(), physObj);

        // Determine initial shape (for Shape2DMaps)
        getInitialShape(obj);

        // Get initial shape properties
        getInitialShapeProperties(obj);

        // Get initial display info (for physical properties)
        getInitialDisplayInfo((Physical) physObj);
      }

      else {
        Objekt nonPhysObj = (Objekt) obj.clone();
        objMap.put(nonPhysObj.getId(), nonPhysObj);

        // Determine initial shape (for Shape2DMaps)
        getInitialShape(obj);

        // Get initial shape properties
        getInitialShapeProperties(obj);
      }

      return obj.getId();
    }

    return null;
  }

  public void destroyObject(Object source, long currentSimulationStep) {
    if (source instanceof Objekt) {
      Objekt obj = (Objekt) source;

      // Add destroyed objects IDs together with the step it was destroyed
      // into a map
      if (destroyMap.get(currentSimulationStep) != null) {
        destroyMap.get(currentSimulationStep).add(obj.getId());
      } else {
        ArrayList<Long> idList = new ArrayList<Long>();
        idList.add(obj.getId());
        destroyMap.put(currentSimulationStep, idList);
      }
    }
  }

  /**
   * Loads an objects view and adds it, together with the objects ID, into a
   * hash map.
   * 
   * @param id
   * @param type
   * @param reader
   */
  private void loadObjView(long id, String type, XMLReader reader) {
    // Search for the view node by ID
    Node node = viewNodeMap.get(Long.toString(id));

    // When no view node was found by ID, search by type
    if (node == null)
      node = viewNodeMap.get(type);

    /*
     * When a view node was found, read the actual view definition and store it
     * together with the objects ID in a hash map.
     */
    if (node != null) {
      if (node.getNodeName().equals(View.PHYSICAL_OBJECT_VIEW)) {
        View view = reader.readPhysicalObjectView(node);
        if (view != null) {
          viewMap.put(id, view);
        }
      }

      else if (node.getNodeName().equals(View.OBJECT_VIEW)) {
        View view = reader.readObjectView(node);
        if (view != null) {
          viewMap.put(id, view);
        }
      }
    }
  }

  private void getInitialShape(Objekt obj) {
    // Get the objects view
    View view = viewMap.get(obj.getId());

    if (view != null) {
      if (view.getShape2DMap() != null) {
        view.getShape2DMap().determineShape(obj, view);

        if (obj instanceof Physical) {
          view.getShape2DMap().determineShape((Physical) obj, view);
        }
      }

      // EmbeddedViews
      if (view.getEmbeddedList() != null) {

        for (View embeddedView : view.getEmbeddedList()) {
          if (embeddedView.getShape2DMap() != null) {
            embeddedView.getShape2DMap().determineShape(obj, embeddedView);
          }

          if (embeddedView.getEmbeddedList() != null) {
            for (View embeddedView2 : embeddedView.getEmbeddedList()) {
              if (embeddedView2.getShape2DMap() != null) {
                embeddedView2.getShape2DMap()
                    .determineShape(obj, embeddedView2);
              }
            }
          }
        }
      }
    }
  }

  private void getShape(ObjectType obj) {
    // Get the objects view
    View view = viewMap.get(obj.getId());

    if (view != null) {
      if (view.getShape2DMap() != null) {
        view.getShape2DMap().determinePropertyValue(obj, view);
      }

      // EmbeddedViews
      if (view.getEmbeddedList() != null) {
        for (View embeddedView : view.getEmbeddedList()) {
          if (embeddedView.getShape2DMap() != null) {
            embeddedView.getShape2DMap().determinePropertyValue(obj,
                embeddedView);
          }

          if (embeddedView.getEmbeddedList() != null) {
            for (View embeddedView2 : embeddedView.getEmbeddedList()) {
              if (embeddedView2.getShape2DMap() != null) {
                embeddedView2.getShape2DMap().determinePropertyValue(obj,
                    embeddedView2);
              }
            }
          }
        }
      }
    }
  }

  private void getShape(ObjType obj) {
    // Get the objects view
    View view = viewMap.get(obj.getId());

    if (view != null) {
      if (view.getShape2DMap() != null) {
        view.getShape2DMap().determinePropertyValue(obj, view);
      }

      // EmbeddedViews
      if (view.getEmbeddedList() != null) {
        for (View embeddedView : view.getEmbeddedList()) {
          if (embeddedView.getShape2DMap() != null) {
            embeddedView.getShape2DMap().determinePropertyValue(obj,
                embeddedView);
          }

          if (embeddedView.getEmbeddedList() != null) {
            for (View embeddedView2 : embeddedView.getEmbeddedList()) {
              if (embeddedView2.getShape2DMap() != null) {
                embeddedView2.getShape2DMap().determinePropertyValue(obj,
                    embeddedView2);
              }
            }
          }
        }
      }
    }
  }

  private void getShape(AgtType agt) {
    // Get the objects view
    View view = viewMap.get(agt.getId());

    if (view != null) {
      if (view.getShape2DMap() != null) {
        view.getShape2DMap().determinePropertyValue(agt, view);
      }

      // EmbeddedViews
      if (view.getEmbeddedList() != null) {
        for (View embeddedView : view.getEmbeddedList()) {
          if (embeddedView.getShape2DMap() != null) {
            embeddedView.getShape2DMap().determinePropertyValue(agt,
                embeddedView);
          }

          if (embeddedView.getEmbeddedList() != null) {
            for (View embeddedView2 : embeddedView.getEmbeddedList()) {
              if (embeddedView2.getShape2DMap() != null) {
                embeddedView2.getShape2DMap().determinePropertyValue(agt,
                    embeddedView2);
              }
            }
          }
        }
      }
    }
  }

  /**
   * Detects grid property changes and updates the grid accordingly.
   * 
   * @param change
   */
  private void getGridPropertyChanges(AgentSimResultingStateChanges change) {
    if (spaceModel.getSpaceType().equals(SpaceType.TwoDGrid)) {
      if (change.getGridCells() != null) {
        if (change.getGridCells().getGridCell() != null) {
          // Apply grid property changes
          GridSpaceModel gsm = (GridSpaceModel) spaceModel;
          gsm.applyPropertyMaps(change.getGridCells());
        }
      }
    }
  }

  /**
   * Detects grid property changes and updates the grid accordingly.
   * 
   * @param changeList
   */
  private void getGridPropertyChanges(ResultingStateChangesType change) {
    if (spaceModel.getSpaceType().equals(SpaceType.TwoDGrid)) {
      if (change.getGridCells() != null) {
        if (change.getGridCells().getGridCell() != null) {
          // Apply grid property changes
          GridSpaceModel gsm = (GridSpaceModel) spaceModel;
          gsm.applyPropertyMaps(change.getGridCells());
        }
      }
    }
  }

  /**
   * Determines the initial values of shape properties affected by shape
   * property maps.
   */
  private void getInitialShapeProperties(Objekt obj) {
    // Get the objects view
    View view = viewMap.get(obj.getId());

    if (view != null) {
      // Apply initial property map to all shapes
      if (view.getShape2DMap() != null) {
        Collection<Shape2D> collection = view.getShape2DMap().getMap().values();

        for (Shape2D shape : collection) {
          shape.applyPropertyMaps(obj);
        }
      } else {

        // Get the associated shape
        Shape2D shape = view.getShape();

        if (shape != null) {
          // Apply property maps
          shape.applyPropertyMaps(obj);
        }
      }

      // EmbeddedViews
      if (view.getEmbeddedList() != null) {
        for (View embeddedView : view.getEmbeddedList()) {
          if (embeddedView.getShape2DMap() != null) {
            for (Shape2D shape : embeddedView.getShape2DMap().getMap().values()) {
              shape.applyPropertyMaps(obj);
            }

            if (embeddedView.getEmbeddedList() != null) {
              for (View embeddedView2 : embeddedView.getEmbeddedList()) {
                if (embeddedView2.getShape2DMap() != null) {
                  for (Shape2D shape : embeddedView2.getShape2DMap().getMap()
                      .values()) {
                    shape.applyPropertyMaps(obj);
                  }
                }
              }
            }
          } else if (embeddedView.getShape() != null) {
            embeddedView.getShape().applyPropertyMaps(obj);

            if (embeddedView.getEmbeddedList() != null) {
              for (View embeddedView2 : embeddedView.getEmbeddedList()) {
                if (embeddedView2.getShape() != null) {
                  embeddedView2.getShape().applyPropertyMaps(obj);
                }
              }
            }
          }
        }
      }
    }
  }

  /**
   * Detects shape property changes and updates the shape accordingly.
   * 
   * @param obj
   */
  private void getShapeProperties(ObjectType obj) {
    // Get the objects view
    View view = viewMap.get(obj.getId());

    if (view != null) {
      // Get the associated shape
      Shape2D shape = view.getShape();

      if (shape != null) {
        // Apply property maps
        shape.applyPropertyMaps(obj);
      }

      // EmbeddedViews
      if (view.getEmbeddedList() != null) {
        for (View embeddedView : view.getEmbeddedList()) {
          if (embeddedView.getShape() != null) {
            embeddedView.getShape().applyPropertyMaps(obj);
          }

          if (embeddedView.getEmbeddedList() != null) {
            for (View embeddedView2 : embeddedView.getEmbeddedList()) {
              if (embeddedView2.getShape() != null) {
                embeddedView2.getShape().applyPropertyMaps(obj);
              }
            }
          }
        }
      }
    }
  }

  /**
   * Detects shape property changes and updates the shape accordingly.
   * 
   * @param obj
   */
  private void getShapeProperties(ObjType obj) {
    // Get the objects view
    View view = viewMap.get(obj.getId());

    if (view != null) {
      // Get the associated shape
      Shape2D shape = view.getShape();

      if (shape != null) {
        // Apply property maps
        shape.applyPropertyMaps(obj);
      }

      // EmbeddedViews
      if (view.getEmbeddedList() != null) {
        for (View embeddedView : view.getEmbeddedList()) {
          if (embeddedView.getShape() != null) {
            embeddedView.getShape().applyPropertyMaps(obj);
          }

          if (embeddedView.getEmbeddedList() != null) {
            for (View embeddedView2 : embeddedView.getEmbeddedList()) {
              if (embeddedView2.getShape() != null) {
                embeddedView2.getShape().applyPropertyMaps(obj);
              }
            }
          }
        }
      }
    }
  }

  /**
   * Detects shape property changes and updates the shape accordingly.
   * 
   * @param agt
   */
  private void getShapeProperties(AgtType agt) {
    // Get the objects view
    View view = viewMap.get(agt.getId());

    if (view != null) {
      // Get the associated shape
      Shape2D shape = view.getShape();

      if (shape != null) {
        // Apply property maps
        shape.applyPropertyMaps(agt);
      }

      // EmbeddedViews
      if (view.getEmbeddedList() != null) {
        for (View embeddedView : view.getEmbeddedList()) {
          if (embeddedView.getShape() != null) {
            embeddedView.getShape().applyPropertyMaps(agt);
          }

          if (embeddedView.getEmbeddedList() != null) {
            for (View embeddedView2 : embeddedView.getEmbeddedList()) {
              if (embeddedView2.getShape() != null) {
                embeddedView2.getShape().applyPropertyMaps(agt);
              }
            }
          }
        }
      }
    }
  }

  private void getInitialDisplayInfo(Objekt obj) {
    // Get the objects view
    View view = viewMap.get(obj.getId());

    if (view != null) {
      // Get the associated display info
      DisplayInfo info = view.getDisplayInfo();

      if (info != null) {
        // Apply property
        info.applyProperty(obj);
      }
    }
  }

  private void getInitialDisplayInfo(Physical phy) {
    // Get the objects view
    View view = viewMap.get(phy.getId());

    if (view != null) {
      // Get the associated display info
      DisplayInfo info = view.getDisplayInfo();

      if (info != null) {
        // Apply property
        info.applyProperty(phy);
      }
    }
  }

  private void getDisplayInfo(ObjectType obj) {
    // Get the objects view
    View view = viewMap.get(obj.getId());

    if (view != null) {
      // Get the associated display info
      DisplayInfo info = view.getDisplayInfo();

      if (info != null) {
        // Apply property
        info.applyProperty(obj);
      }
    }
  }

  private void getDisplayInfo(ObjType obj) {
    // Get the objects view
    View view = viewMap.get(obj.getId());

    if (view != null) {
      // Get the associated display info
      DisplayInfo info = view.getDisplayInfo();

      if (info != null) {
        // Apply property
        info.applyProperty(obj);
      }
    }
  }

  private void getDisplayInfo(AgtType agt) {
    // Get the objects view
    View view = viewMap.get(agt.getId());

    if (view != null) {
      // Get the associated display info
      DisplayInfo info = view.getDisplayInfo();

      if (info != null) {
        // Apply property
        info.applyProperty(agt);
      }
    }
  }

  /**
   * Detects changes on objects and updates them accordingly.
   * 
   * @param change
   */
  private void getObjectUpdates(AgentSimResultingStateChanges change) {
    // Get all physical agents that have changed and update them
    if (change.getPhysicalAgents() != null) {
      if (change.getPhysicalAgents().getPhysAgt() != null) {
        List<PhysAgtType> list = change.getPhysicalAgents().getPhysAgt();

        for (PhysAgtType agt : list) {
          getShape(agt);
          getShapeProperties(agt);
          getDisplayInfo(agt);
          updatePhysicalAgent(agt);
        }
      }
    }

    // Get all physical objects that have changed and update them
    if (change.getPhysicalObjects() != null) {
      if (change.getPhysicalObjects().getPhysObj() != null) {
        List<PhysicalObjType> list = change.getPhysicalObjects().getPhysObj();

        for (PhysicalObjType obj : list) {
          getShape(obj);
          getShapeProperties(obj);
          getDisplayInfo(obj);
          updatePhysicalObject(obj);
        }
      }
    }

    // Get all non-physical objects that have changed and update them
    if (change.getObjects() != null) {
      if (change.getObjects().getObj() != null) {
        List<ObjType> objList = change.getObjects().getObj();

        for (ObjType obj : objList) {
          getShape(obj);
          getShapeProperties(obj);
          getDisplayInfo(obj);
        }
      }
    }

    if (change.getAgents() != null) {
      if (change.getAgents().getAgt() != null) {
        List<AgtType> agtList = change.getAgents().getAgt();

        for (AgtType agt : agtList) {
          getShape(agt);
          getShapeProperties(agt);
          getDisplayInfo(agt);
        }
      }
    }
  }

  /**
   * Detects changes on objects and updates them accordingly.
   * 
   * @param change
   */
  private void getObjectUpdates(ResultingStateChangesType change) {
    // Get all physical agents that have changed and update them
    if (change.getPhysicalAgents() != null) {
      if (change.getPhysicalAgents().getPhysAgt() != null) {
        List<PhysAgtType> list = change.getPhysicalAgents().getPhysAgt();

        for (PhysAgtType agt : list) {
          getShape(agt);
          getShapeProperties(agt);
          getDisplayInfo(agt);
          updatePhysicalAgent(agt);
        }
      }
    }

    // Get all physical objects that have changed and update them
    if (change.getPhysicalObjects() != null) {
      if (change.getPhysicalObjects().getPhysObj() != null) {
        List<PhysicalObjType> list = change.getPhysicalObjects().getPhysObj();

        for (PhysicalObjType obj : list) {
          getShape(obj);
          getShapeProperties(obj);
          getDisplayInfo(obj);
          updatePhysicalObject(obj);
        }
      }
    }

    // Get all non-physical objects that have changed and update them
    if (change.getObjects() != null) {
      if (change.getObjects().getObj() != null) {
        List<ObjType> objList = change.getObjects().getObj();

        for (ObjType obj : objList) {
          getShape(obj);
          getShapeProperties(obj);
          getDisplayInfo(obj);
        }
      }
    }

    if (change.getAgents() != null) {
      if (change.getAgents().getAgt() != null) {
        List<AgtType> agtList = change.getAgents().getAgt();

        for (AgtType agt : agtList) {
          getShape(agt);
          getShapeProperties(agt);
          getDisplayInfo(agt);
        }
      }
    }
  }

  /**
   * Detects changes on objects and updates them accordingly.
   * 
   * @param change
   */
  private void getObjectUpdates(ResultingStateChanges change) {
    // Get all physical agents that have changed and update them
    if (change.getPhysicalAgents() != null) {
      if (change.getPhysicalAgents().getPhysAgt() != null) {
        List<PhysAgtType> list = change.getPhysicalAgents().getPhysAgt();

        for (PhysAgtType agt : list) {
          getShape(agt);
          getShapeProperties(agt);
          getDisplayInfo(agt);
          updatePhysicalAgent(agt);
        }
      }
    }

    // Get all physical objects that have changed and update them
    if (change.getPhysicalObjects() != null) {
      if (change.getPhysicalObjects().getPhysObj() != null) {
        List<PhysicalObjType> list = change.getPhysicalObjects().getPhysObj();

        for (PhysicalObjType obj : list) {
          getShape(obj);
          getShapeProperties(obj);
          getDisplayInfo(obj);
          updatePhysicalObject(obj);
        }
      }
    }

    // Get all non-physical objects that have changed and update them
    if (change.getObjects() != null) {
      if (change.getObjects().getObj() != null) {
        List<ObjType> objList = change.getObjects().getObj();

        for (ObjType obj : objList) {
          getShape(obj);
          getShapeProperties(obj);
          getDisplayInfo(obj);
        }
      }
    }
  }

  /**
   * Updates a physical agent with changes from a simulation step
   * 
   * @param phyAgt
   */
  private void updatePhysicalAgent(PhysAgtType phyAgt) {
    // Get the physical agent that has to be updated
    PhysicalAgentObject obj = (PhysicalAgentObject) objMap.get(phyAgt.getId());

    boolean parsePointString = false;

    if (obj != null) {
      if (phyAgt.getX() != null) {
        double x = phyAgt.getX();
        obj.setX(x);
      }

      if (phyAgt.getY() != null) {
        double y = phyAgt.getY();
        obj.setY(y);
      }

      if (phyAgt.getZ() != null) {
        double z = phyAgt.getZ();
        obj.setZ(z);
      }

      if (phyAgt.getRotationAngleX() != null) {
        double rotationAngleX = phyAgt.getRotationAngleX();
        obj.setRotX(rotationAngleX);
      }

      if (phyAgt.getRotationAngleY() != null) {
        double rotationAngleY = phyAgt.getRotationAngleY();
        obj.setRotZ(rotationAngleY);
      }

      if (phyAgt.getRotationAngleZ() != null) {
        double rotationAngleZ = phyAgt.getRotationAngleZ();
        obj.setRotZ(rotationAngleZ);
      }

      if (phyAgt.getVx() != null) {
        double vx = phyAgt.getVx();
        obj.setVx(vx);
      }

      if (phyAgt.getVy() != null) {
        double vy = phyAgt.getVy();
        obj.setVy(vy);
      }

      if (phyAgt.getVz() != null) {
        double vz = phyAgt.getVz();
        obj.setVz(vz);
      }

      if (phyAgt.getAx() != null) {
        double ax = phyAgt.getAx();
        obj.setAx(ax);
      }

      if (phyAgt.getAy() != null) {
        double ay = phyAgt.getAy();
        obj.setAy(ay);
      }

      if (phyAgt.getAz() != null) {
        double az = phyAgt.getAz();
        obj.setAz(az);
      }

      if (phyAgt.getOmegaX() != null) {
        double omegaX = phyAgt.getOmegaX();
        obj.setOmegaX(omegaX);
      }

      if (phyAgt.getOmegaY() != null) {
        double omegaY = phyAgt.getOmegaY();
        obj.setOmegaY(omegaY);
      }

      if (phyAgt.getOmegaZ() != null) {
        double omegaZ = phyAgt.getOmegaZ();
        obj.setOmegaZ(omegaZ);
      }

      if (phyAgt.getAlphaX() != null) {
        double alphaX = phyAgt.getAlphaX();
        obj.setAlphaX(alphaX);
      }

      if (phyAgt.getAlphaY() != null) {
        double alphaY = phyAgt.getAlphaY();
        obj.setAlphaY(alphaY);
      }

      if (phyAgt.getAlphaZ() != null) {
        double alphaZ = phyAgt.getAlphaZ();
        obj.setAlphaZ(alphaZ);
      }

      if (phyAgt.getM() != null) {
        double m = phyAgt.getM();
        obj.setM(m);
      }

      if (phyAgt.getWidth() != null) {
        double width = phyAgt.getWidth();
        obj.setWidth(width);
      }

      if (phyAgt.getHeight() != null) {
        double height = phyAgt.getHeight();
        obj.setHeight(height);
      }

      if (phyAgt.getDepth() != null) {
        double depth = phyAgt.getDepth();
        obj.setDepth(depth);
      }

      if (phyAgt.getPoints() != null) {
        String points = phyAgt.getPoints();
        obj.setPoints(points);
        parsePointString = true;
      }

      // Get the objects view
      View view = viewMap.get(phyAgt.getId());

      if (view.getShape2DMap() != null) {
        view.getShape2DMap().determineShape((Physical) obj, view);
      }

      if (view.getShape() != null) {
        if (parsePointString) {
          view.getShape().setParsePointString(true);
        }

        view.getShape().applyPropertyMaps((Physical) obj);
      }
    }
  }

  /**
   * Updates a physical object with changes from a simulation step.
   * 
   * @param phyObj
   */
  private void updatePhysicalObject(PhysicalObjType phyObj) {
    // Get the physical object that has to be updated
    PhysicalObject obj = (PhysicalObject) objMap.get(phyObj.getId());

    boolean parsePointString = false;

    if (obj != null) {
      if (phyObj.getX() != null) {
        double x = phyObj.getX();
        obj.setX(x);
      }

      if (phyObj.getY() != null) {
        double y = phyObj.getY();
        obj.setY(y);
      }

      if (phyObj.getZ() != null) {
        double z = phyObj.getZ();
        obj.setZ(z);
      }

      if (phyObj.getRotationAngleX() != null) {
        double rotationAngleX = phyObj.getRotationAngleX();
        obj.setRotX(rotationAngleX);
      }

      if (phyObj.getRotationAngleY() != null) {
        double rotationAngleY = phyObj.getRotationAngleY();
        obj.setRotZ(rotationAngleY);
      }

      if (phyObj.getRotationAngleZ() != null) {
        double rotationAngleZ = phyObj.getRotationAngleZ();
        obj.setRotZ(rotationAngleZ);
      }

      if (phyObj.getVx() != null) {
        double vx = phyObj.getVx();
        obj.setVx(vx);
      }

      if (phyObj.getVy() != null) {
        double vy = phyObj.getVy();
        obj.setVy(vy);
      }

      if (phyObj.getVz() != null) {
        double vz = phyObj.getVz();
        obj.setVz(vz);
      }

      if (phyObj.getAx() != null) {
        double ax = phyObj.getAx();
        obj.setAx(ax);
      }

      if (phyObj.getAy() != null) {
        double ay = phyObj.getAy();
        obj.setAy(ay);
      }

      if (phyObj.getAz() != null) {
        double az = phyObj.getAz();
        obj.setAz(az);
      }

      if (phyObj.getOmegaX() != null) {
        double omegaX = phyObj.getOmegaX();
        obj.setOmegaX(omegaX);
      }

      if (phyObj.getOmegaY() != null) {
        double omegaY = phyObj.getOmegaY();
        obj.setOmegaY(omegaY);
      }

      if (phyObj.getOmegaZ() != null) {
        double omegaZ = phyObj.getOmegaZ();
        obj.setOmegaZ(omegaZ);
      }

      if (phyObj.getAlphaX() != null) {
        double alphaX = phyObj.getAlphaX();
        obj.setAlphaX(alphaX);
      }

      if (phyObj.getAlphaY() != null) {
        double alphaY = phyObj.getAlphaY();
        obj.setAlphaY(alphaY);
      }

      if (phyObj.getAlphaZ() != null) {
        double alphaZ = phyObj.getAlphaZ();
        obj.setAlphaZ(alphaZ);
      }

      if (phyObj.getM() != null) {
        double m = phyObj.getM();
        obj.setM(m);
      }

      if (phyObj.getWidth() != null) {
        double width = phyObj.getWidth();
        obj.setWidth(width);
      }

      if (phyObj.getHeight() != null) {
        double height = phyObj.getHeight();
        obj.setHeight(height);
      }

      if (phyObj.getDepth() != null) {
        double depth = phyObj.getDepth();
        obj.setDepth(depth);
      }

      if (phyObj.getPoints() != null) {
        String points = phyObj.getPoints();
        obj.setPoints(points);
        parsePointString = true;
      }

      // Get the objects view
      View view = viewMap.get(phyObj.getId());

      if (view != null) {
        if (view.getShape2DMap() != null) {
          view.getShape2DMap().determineShape((Physical) obj, view);
        }

        if (view.getShape() != null) {
          if (parsePointString) {
            view.getShape().setParsePointString(true);
          }

          view.getShape().applyPropertyMaps((Physical) obj);
        }
      }
    }
  }

  /**
   * Process the current sim step (update stuff, etc)
   * 
   * @param simStep
   *          the simulation step data to process
   */
  public void processStep(SimulationStep simStep) {

    // Make all objects visible that where created at this simulation
    // step
    ArrayList<View> viewList = createdMap.get(simStep.getStepTime());

    if (viewList != null) {
      for (View view : viewList) {
        view.setVisible(true);
      }

      createdMap.remove(simStep.getStepTime());
    }

    if (simStep.getEnvironmentSimulatorStep() != null) {
      // Update from environmental changes
      updateFromEnvironment(simStep.getEnvironmentSimulatorStep());
    }

    if (simStep.getAgentSimResultingStateChanges() != null) {
      // Get grid property changes
      getGridPropertyChanges(simStep.getAgentSimResultingStateChanges());

      // Update objects
      getObjectUpdates(simStep.getAgentSimResultingStateChanges());
    }

    // Remove objects that where destroyed in this simulation step
    ArrayList<Long> idList = destroyMap.get(simStep.getStepTime());

    if (idList != null) {
      for (Long id : idList) {
        // Remove an object from the object map
        objMap.remove(id);

        // Remove the objects view from the view map
        viewMap.remove(id);
      }

      destroyMap.remove(simStep.getStepTime());
    }
  }


  /**
   * Checks for updates from the environment simulator.
   * 
   * @param step
   */
  private void updateFromEnvironment(EnvironmentSimulatorStep step) {
    if (step.getPhysicsSimulation() != null) {
      // Update from physics simulation
      updateFromPhysicsSimulation(step.getPhysicsSimulation());
    }

    if (step.getEnvSimInputEvent() != null) {
      // Update from environmental simulation input events
      updateFromEnvSimInputEvents(step.getEnvSimInputEvent());
    }
  }

  /**
   * Checks for updates from the physics simulation.
   * 
   * @param phySim
   */
  private void updateFromPhysicsSimulation(PhysicsSimulationType phySim) {
    if (phySim.getResultingStateChanges() != null) {
      for (ResultingStateChanges change : phySim.getResultingStateChanges()) {
        // Update objects
        getObjectUpdates(change);
      }
    }
  }

  /**
   * Checks for updates from environmental input events.
   * 
   * @param envSimInputEvents
   */
  private void updateFromEnvSimInputEvents(
      List<JAXBElement<? extends EnvSimInputEventType>> envSimInputEvents) {

    for (JAXBElement<? extends EnvSimInputEventType> envSimInputEventType : envSimInputEvents) {
      if (envSimInputEventType.getValue() != null) {
        if (envSimInputEventType.getValue().getResultingStateChanges() != null) {
          List<ResultingStateChangesType> changeList = envSimInputEventType
              .getValue().getResultingStateChanges();

          for (ResultingStateChangesType change : changeList) {
            // Get grid property changes
            getGridPropertyChanges(change);

            // Update objects
            getObjectUpdates(change);
          }
        }

        if (envSimInputEventType.getValue().getActivities() != null) {
          // Update from activities
          updateFromActivities(envSimInputEventType.getValue().getActivities());
        }
      }
    }
  }

  /**
   * Checks for updates from activities.
   * 
   * @param activities
   */
  private void updateFromActivities(Activities activities) {
    if (activities.getStartActivity() != null) {
      for (StartActivity startActivity : activities.getStartActivity()) {
        if (startActivity.getResultingStateChanges() != null) {
          // Get grid property changes
          getGridPropertyChanges(startActivity.getResultingStateChanges());

          // Update objects
          getObjectUpdates(startActivity.getResultingStateChanges());
        }
      }
    }

    if (activities.getFinalizeActivity() != null) {
      for (FinalizeActivity finalizeActivity : activities.getFinalizeActivity()) {
        if (finalizeActivity.getResultingStateChanges() != null) {
          // Get grid property changes
          getGridPropertyChanges(finalizeActivity.getResultingStateChanges());

          // Update objects
          getObjectUpdates(finalizeActivity.getResultingStateChanges());
        }
      }
    }
  }

  public SpaceModel getSpaceModel() {
    return spaceModel;
  }

  public void setSpaceModel(SpaceModel spaceModel) {
    this.spaceModel = spaceModel;
  }

  public File getProjectDirectory() {
    return projectDirectory;
  }

  public void setProjectDirectory(File projectDirectory) {
    this.projectDirectory = projectDirectory;
  }

  public Map<String, Node> getViewNodeMap() {
    return viewNodeMap;
  }

  public void setViewNodeMap(Map<String, Node> viewNodeMap) {
    this.viewNodeMap = viewNodeMap;
  }

  public Map<Long, View> getViewMap() {
    return viewMap;
  }

  public void setViewMap(Map<Long, View> viewMap) {
    this.viewMap = viewMap;
  }

  public Map<Long, Objekt> getObjMap() {
    return objMap;
  }

  public void setObjMap(Map<Long, Objekt> objMap) {
    this.objMap = objMap;
  }

  public Map<Long, ArrayList<View>> getCreatedMap() {
    return createdMap;
  }

  public void setCreatedMap(Map<Long, ArrayList<View>> createdMap) {
    this.createdMap = createdMap;
  }

}
