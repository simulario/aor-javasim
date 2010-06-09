package aors.module.initialState;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Frame;
import java.awt.GridLayout;
import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Vector;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;
import javax.swing.table.DefaultTableModel;
import org.w3c.dom.Document;
import aors.controller.SimulationDescription;
import aors.data.java.ObjektDestroyEvent;
import aors.data.java.ObjektInitEvent;
import aors.data.java.SimulationEvent;
import aors.data.java.SimulationStepEvent;
import aors.model.envevt.EnvironmentEvent;
import aors.module.Module;
import aors.controller.InitialState;
import aors.module.initialState.gui.InitialStateUITab;
import aors.module.initialState.gui.RanVarPropertyContainer;
import aors.module.initialState.gui.ValueExprPropertyContainer;

public class InitialStateUIController implements Module, ActionListener {

  public InitialStateUIController() {
    
    this.tabScroll = new InitialStateUITab(this);

  }

  public Object getGUIComponent() {
    return this.tabScroll;
  }
  
  
  //constructor for each entity as table style
  public InitialStateUIController(JTable table, DefaultTableModel model,
	      String type, Vector<JButton> buttonContainer, String objectType) {

	    this.type = type;
	    this.table = table;
	    this.model = model;
	    this.buttonContainer = buttonContainer;
	    this.objectType = objectType;

	  }

  //get the DOM of scenario.xml file and initialize the InitialStateUITab
  public void simulationDomOnlyInitialization(
      SimulationDescription simulationDescription) {

    System.out.println("Here is InitialStateUIController SimDomOnly!");

    this.sd = simulationDescription;
    this.dom = sd.getDom();

    if (dom == null) {
      return;
    }

    ((InitialStateUITab) getGUIComponent()).initial(dom, sd, tabScroll,
        scenario);
  }

  @Override
  public void simulationStepEnd(SimulationStepEvent simulationStepEvent) {
    // TODO Auto-generated method stub

  }

  @Override
  public void simulationStepStart(long stepNumber) {
    // TODO Auto-generated method stub

  }

  @Override
  public void simulationEnded() {
    // TODO Auto-generated method stub

  }

  @Override
  public void simulationEnvironmentEventOccured(
      EnvironmentEvent environmentEvent) {
    // TODO Auto-generated method stub

  }

  @Override
  public void simulationInfosEvent(SimulationEvent simulationEvent) {
    // TODO Auto-generated method stub

  }

  @Override
  public void simulationInitialize(InitialState initialState) {
    // TODO Auto-generated method stub

  }

  @Override
  public void simulationPaused(boolean pauseState) {
    // TODO Auto-generated method stub

  }

  @Override
  public void simulationProjectDirectoryChanged(File projectDirectory) {
    // TODO Auto-generated method stub
    this.scenario = new File(projectDirectory, "scenario.xml");

  }

  @Override
  public void simulationStarted() {
    // TODO Auto-generated method stub

  }

 

  public void actionPerformed(ActionEvent e) {

	    JButton selectButton = (JButton) e.getSource();
	    if (selectButton == buttonContainer.get(0)) {
	      copyRow(); // process copy operation
	    } else if (selectButton == buttonContainer.get(1)) {
	      delRow(); // process del operation
	    } else if (selectButton == buttonContainer.get(2)) {
	      createNewRow( selectButton); // process create operation
	    } else if (selectButton == buttonContainer.get(3)) {
	      createEditRow(selectButton); // process edit operation
	    }
	  }

  @SuppressWarnings("unchecked")
  public void copyRow() {

	    Vector<Vector<Object>> tempData = (Vector<Vector<Object>>) model
	        .getDataVector();
	    int cRow = processCRow(table.getSelectedRow(), tempData);

	    if (!(tempData.isEmpty())) {

	      Vector<Object> tempRow = tempData.elementAt(cRow);
	      tempRow = (Vector<Object>) tempRow.clone();
	      model.insertRow(cRow + 1, tempRow);

	      HashSet<String> tempLanSet = tabScroll.getLanType().get(type);
	      String tempKey = null;
	      tempKey = typeTransfer(objectType, type);

	      
	      // specially to process RandomVariable
	      processCopyAndDelete(tabScroll.getRanTypePropertyMap(), tempKey,
	          tempLanSet, "copy", cRow, tabScroll.getRanVarPropertyContainerMap(),
	          null);
	      
          // specially to process ValueExpr
	      processCopyAndDelete(tabScroll.getValueExprTypePropertyMap(), tempKey,
	          tempLanSet, "copy", cRow, null, tabScroll
	              .getValueExprPropertyContainerMap());

	    }
	  }

  @SuppressWarnings("unchecked")
  public void delRow() {

	    int cRow = table.getSelectedRow();

	    Vector<Vector<Object>> tempData = (Vector<Vector<Object>>) model.getDataVector();
	    boolean flag;

	    if (cRow < 0 || cRow > tempData.size()) {
	      flag = false;
	    } else {
	      flag = true;
	    }

	    if (flag & (tempData.size() > 1)) {

	      HashSet<String> tempLanSet = tabScroll.getLanType().get(type);
	      String tempKey = typeTransfer(objectType, type);

	      processCopyAndDelete(tabScroll.getRanTypePropertyMap(), tempKey,
	          tempLanSet, "delete", cRow,
	          tabScroll.getRanVarPropertyContainerMap(), null);

	      processCopyAndDelete(tabScroll.getValueExprTypePropertyMap(), tempKey,
	          tempLanSet, "delete", cRow, null, tabScroll
	              .getValueExprPropertyContainerMap());

	      model.removeRow(cRow);
	    }

	  }
  
  
  public void processCopyAndDelete(HashMap<String, HashSet<String>> tempMap,
	      String tempKey, HashSet<String> tempLanSet, String action, int cRow,
	      HashMap<String, Vector<RanVarPropertyContainer>> rContainerMap,
	      HashMap<String, Vector<ValueExprPropertyContainer>> vContainerMap) {

	    String tempLabel = null;

	    if (tempMap.containsKey(tempKey)) {

	      HashSet<String> tempPropertySet = tempMap.get(tempKey);

	      for (int j = 0; j < model.getColumnCount(); j++) {

	        String colHeadValue = (String) table.getColumnModel().getColumn(j)
	            .getHeaderValue();

	        for (Iterator<String> it = tempPropertySet.iterator(); it.hasNext();) {

	          String tempProperty = it.next();

	          for (Iterator<String> lans = tempLanSet.iterator(); lans.hasNext();) {

	            String tempLan = lans.next();
	            tempLabel = tempProperty + type + tempLan;

	            if (tabScroll.getLabelMap().get(tempLabel).equals(colHeadValue)) {

	              if (action.equals("copy")) {

	                if (rContainerMap != null) {

	                  Vector<RanVarPropertyContainer> tempContainers = rContainerMap
	                      .get(tempProperty + tempKey);
	                  RanVarPropertyContainer tempContainer = tempContainers
	                      .get(cRow);
	                  tempContainers.add(cRow, tempContainer);
	                  rContainerMap.put(tempProperty + tempKey, tempContainers);

	                } else if (vContainerMap != null) {

	                  Vector<ValueExprPropertyContainer> tempContainers = vContainerMap
	                      .get(tempProperty + tempKey);
	                  ValueExprPropertyContainer tempContainer = tempContainers
	                      .get(cRow);
	                  tempContainers.add(cRow, tempContainer);
	                  vContainerMap.put(tempProperty + tempKey, tempContainers);

	                }

	              } else if (action.equals("delete")) {

	                if (rContainerMap != null) {

	                  Vector<RanVarPropertyContainer> tempContainers = rContainerMap
	                      .get(tempProperty + tempKey);
	                  tempContainers.remove(cRow);
	                  rContainerMap.put(tempProperty + tempKey, tempContainers);

	                } else if (vContainerMap != null) {

	                  Vector<ValueExprPropertyContainer> tempContainers = vContainerMap
	                      .get(tempProperty + tempKey);
	                  tempContainers.remove(cRow);
	                  vContainerMap.put(tempProperty + tempKey, tempContainers);

	                }
	              }
	            }
	          }
	        }
	      }
	    }
	  }
  
     public void createNewRow(JButton button) {

	    processCreateAndEditor(true, button);

	  }

	  
	  public void createEditRow(JButton button) {
	    
	    processCreateAndEditor(false, button);

	  }

	  @SuppressWarnings("unchecked")
	public void processCreateAndEditor(boolean create, JButton button){
		     
		     HashMap<String,HashSet<Integer>> constrainMap = new HashMap<String,HashSet<Integer>>();
		     HashSet<Integer> booleanPosition = new HashSet<Integer>();
		     HashSet<Integer> integerPosition = new HashSet<Integer>();
		     
		     
		     
		     Vector<Vector<Object>> tempData = (Vector<Vector<Object>>) model.getDataVector();
		     int cRow = processCRow(table.getSelectedRow(),tempData);
		     
		     Vector<Object> tempRow = null;
		     
		     
		     if(create){
		       
		       tempRow = tempData.elementAt(cRow);
		       tempRow = (Vector<Object>)tempRow.clone();
		       
		     }else{
		       
		       tempRow = tempData.elementAt(cRow);
		     
		     }
		     for (int i = 0; i < tempRow.size(); i++) {
		           
		      if(tempRow.get(i)!= null){ 
		        
		         if((tempRow.get(i).getClass().getName()).equals("java.lang.Boolean")){
		           
		           booleanPosition.add(i);
		           
		         }else if((tempRow.get(i).getClass().getName()).equals("java.lang.Integer")){
		           
		           integerPosition.add(i);   
		         }
		         
		           if(create){
		             
		             tempRow.set(i, "");
		             
		           }else{
		         
		             tempRow.set(i, String.valueOf(tempRow.get(i)));
		          
		           }
		           
		      }else{
		              
		             tempRow.set(i, "");
		      }
		          
		      }
		      
		     constrainMap.put("boolean", booleanPosition);
		     constrainMap.put("integer", integerPosition);
		     Frame frame = (Frame)SwingUtilities.getRoot(button);
		     
		     JDialog editJDialog  = null;
		     
		     if(create){
		        
		       editJDialog = new EditJDialog(frame, true, table, tempRow,
		                                           tempData, model, cRow,constrainMap,
		                                           tabScroll,type,objectType,true);
		     }else{
		       
		       editJDialog = new EditJDialog(frame, true, table, tempRow,
		                                            tempData, model, cRow,constrainMap,
		                                            tabScroll,type,objectType,false);
		     }
		     
		     editJDialog.pack();
		     editJDialog.setVisible(true);
		     
		     
		   }
		   
		  public String typeTransfer(String objectType, String type) {

		    String returnType = null;

		    if (objectType != null) {

		      returnType = objectType + type;

		    } else {

		      returnType = type;

		    }

		    return returnType;

		  }
		  
		  
		  public int processCRow(int row, Vector<Vector<Object>> tempData) {

		    if (row < 0) {
		      row = 0;
		    }

		    if (row > tempData.size()) {
		      row = tempData.size();
		    }

		    return row;

		  }
  

  
  private Document dom;   //Dom of the scenario.xml file
  private JTable table;   // table container to hold the content of each entity
  private DefaultTableModel model;
  private File scenario;  // the scenario.xml file
  private String type;    // the type of each entity
  private SimulationDescription sd; // Utility class that provides method for xpath parse and namespace support
  private static InitialStateUITab tabScroll; // scrollPane of InitialStateUITab
  private String objectType; // 
  private Vector<JButton> buttonContainer;// ButtonContainer hold button instances for each entity(copy, del, create, edit)

  @Override
  public void objektDestroyEvent(ObjektDestroyEvent objektDestroyEvent) {
    // TODO Auto-generated method stub

  }

  @Override
  public void objektInitEvent(ObjektInitEvent objInitEvent) {
    // TODO Auto-generated method stub

  }

}

class EditJDialog extends JDialog {

	  private static final long serialVersionUID = 1L;

	  public EditJDialog(Frame owner, boolean modal, JTable table,
	      Vector<Object> row, Vector<Vector<Object>> rowData,
	      DefaultTableModel model, int cRow,
	      HashMap<String, HashSet<Integer>> constrainMap,
	      InitialStateUITab initialStateUITab, String type, String objectType,
	      boolean createNew) {

	    super(owner, modal);
	    JDialog.setDefaultLookAndFeelDecorated(true);
	    if (owner != null) {
	      Dimension parentSize = owner.getSize();
	      Point p = owner.getLocation();
	      setLocation(p.x + parentSize.width / 4, p.y + parentSize.height / 4);
	    }

	    this.owner = owner;
	    this.type = type;
	    this.table = table;
	    this.row = row;
	    this.model = model;
	    this.cRow = cRow;
	    this.rowData = rowData;
	    this.constrainMap = constrainMap;
	    this.initialStateUITab = initialStateUITab;
	    this.objectType = objectType;
	    this.createNew = createNew;

	    add(createContentPanel(), BorderLayout.CENTER);
	    add(createButtonPanel(), BorderLayout.SOUTH);

	  }

	  public JPanel createContentPanel() {

	    ranTypePropertyMap = initialStateUITab.getRanTypePropertyMap();
	    valueExprPropertyMap = initialStateUITab.getValueExprTypePropertyMap();
	    lanTypeMap = initialStateUITab.getLanType();
	    labelMap = initialStateUITab.getLabelMap();
	    HashSet<String> tempLanSet = lanTypeMap.get(type);
	    String tempLabel = null;

	    final JPanel contentPanel = new JPanel();
	    contentPanel.setLayout(new GridLayout(0, 2, 10, 0));
	    label = new JLabel[row.size()];
	    field = new Object[row.size()];

	    if (!ranTypePropertyMap.containsKey(type)
	        & !valueExprPropertyMap.containsKey(type)) {

	      for (int i = 0; i < row.size(); i++) {

	        String columnName = (String) table.getColumnModel().getColumn(i).getHeaderValue(); 
	        label[i] = new JLabel(columnName);
	        field[i] = new JTextField((String) row.get(i));
	        contentPanel.add(label[i]);
	        contentPanel.add((JTextField) field[i]);

	      }
	    } else {

	      out: for (int i = 0; i < row.size(); i++) {

	        String columnName = (String) table.getColumnModel().getColumn(i)
	            .getHeaderValue();
	        label[i] = new JLabel(columnName);
	        contentPanel.add(label[i]);
	        // System.out.println("The columnName: => " + columnName);

	        if (ranTypePropertyMap.containsKey(type)) {

	          HashSet<String> tempPropertySet = ranTypePropertyMap.get(type);

	          for (Iterator<String> it = tempPropertySet.iterator(); it.hasNext();) {

	            final String tempProperty = it.next();

	            for (Iterator<String> lans = tempLanSet.iterator(); lans.hasNext();) {

	              String tempLan = lans.next();
	              tempLabel = tempProperty + type + tempLan;

	              if (labelMap.get(tempLabel).equals(columnName)) {

	                field[i] = new JButton("RandomVariable");
	                contentPanel.add((JButton) field[i]);

	                final EditRandomVariableDialog sbd = new EditRandomVariableDialog(
	                    owner, "Edit RandomVariable Dialog", true, cRow, type,
	                    tempProperty, initialStateUITab, createNew, objectType);

	                ((JButton) field[i]).addActionListener(new ActionListener() {
	                  public void actionPerformed(ActionEvent ae) {

	                     sbd.setVisible(true);

	                  }

	                });

	                continue out;
	              }
	            }
	          }
	        }
	        if (valueExprPropertyMap.containsKey(type)) {

	          HashSet<String> tempPropertySet = valueExprPropertyMap.get(type);

	          for (Iterator<String> it = tempPropertySet.iterator(); it.hasNext();) {

	            final String tempProperty = it.next();

	            for (Iterator<String> lans = tempLanSet.iterator(); lans.hasNext();) {

	              String tempLan = lans.next();
	              tempLabel = tempProperty + type + tempLan;

	              if (labelMap.get(tempLabel).equals(columnName)) {

	                field[i] = new JButton("ValueExpr");
	                contentPanel.add((JButton) field[i]);
	                final EditValueExprDialog ved = new EditValueExprDialog(owner,
	                    "Edit ValueExpr Dialog", true, cRow, type, tempProperty,
	                    initialStateUITab, createNew, objectType);

	                ((JButton) field[i]).addActionListener(new ActionListener() {
	                  public void actionPerformed(ActionEvent ae) {

	                    ved.setVisible(true);

	                  }

	                });

	                continue out;

	              }

	            }

	          }
	        }

	        field[i] = new JTextField((String) row.get(i));
	        contentPanel.add((JTextField) field[i]);

	      }

	    }

	    return contentPanel;

	  }

	  JPanel createButtonPanel() {

	    JPanel buttonPanel = new JPanel();
	    buttonPanel.setLayout(new FlowLayout(FlowLayout.RIGHT));
	    JButton ok = new JButton("OK");
	    ActionListener okListener = new ActionListener() {
	      public void actionPerformed(ActionEvent e) {

	        Vector<Object> insertEditRow = new Vector<Object>();

	        for (int i = 0; i < field.length; i++) {

	          if (!(field[i].getClass().getName()).equals("javax.swing.JButton")) {

	            if (((JTextField) field[i]).getText().equals("")
	                || ((JTextField) field[i]).getText() == null) {

	              
	              JOptionPane.showMessageDialog(null,
	                  "Please fill in the field with the content,now it is empty!");
	              
	              ((JTextField) field[i]).setText("!empty!");
	              
	              return;
	            } else if (constrainMap.get("boolean").contains(i)) {

	              booleanPosition = constrainMap.get("boolean");
	              for (Iterator<Integer> it = booleanPosition.iterator(); it
	                  .hasNext();) {

	                int position = it.next();

	                if (position == i) {
	                  
	                  if (!((JTextField) field[i]).getText().equals("true")
	                      & (!((JTextField) field[i]).getText().equals("false"))) {

	                    JOptionPane.showMessageDialog(null,
	                        "The content of the field " + label[i].getText()
	                            + " is only true or false");
	                    return;
	                  } else {
	                    
	                    insertEditRow.addElement(Boolean.valueOf(((JTextField) field[i]).getText()));
	                  }
	                }
	              }

	            } else if (constrainMap.get("integer").contains(i)) {
	              
	              integerPosition = constrainMap.get("integer");
	              for (Iterator<Integer> it = integerPosition.iterator(); it
	                  .hasNext();) {
	                int position = it.next();

	                if (position == i) {
	                  
	                  HashSet<String> tempPropertySet = initialStateUITab
	                      .getConstrainMap().get(type);

	                  for (Iterator<String> propertys = tempPropertySet.iterator(); propertys
	                      .hasNext();) {

	                    String property = propertys.next();
	                    String propertyType = property + type;

	                    HashSet<String> tempLanSet = initialStateUITab.getLanType()
	                        .get(type);
	                    for (Iterator<String> lans = tempLanSet.iterator(); lans
	                        .hasNext();) {

	                      String lan = lans.next();
	                      String labelKey = propertyType + lan;
	                      
	                      if (initialStateUITab.getLabelMap().get(labelKey).equals(
	                          label[i].getText())) {

	                        Vector<Integer> tempValueRange = initialStateUITab
	                            .getConstrainNameMapRange().get(property);
	                        int minValue = tempValueRange.get(0);
	                        int maxValue = tempValueRange.get(1);

	                        

	                        int currentValue = Integer
	                            .valueOf(((JTextField) field[i]).getText());
	                        
	                        if ((currentValue > maxValue)
	                            || (currentValue < minValue)) {

	                          JOptionPane.showMessageDialog(null,
	                              "The content of the field " + label[i].getText()
	                                  + " must be in the range" + "(" + minValue
	                                  + "," + maxValue + ")");
	                          return;
	                        } else {
	                          
	                          insertEditRow.addElement(Integer
	                              .valueOf(((JTextField) field[i]).getText()));
	                        }
	                      }
	                    }
	                  }
	                }
	              }
	            } else {

	              boolean enumProperty = false;
	              String propertyType = "";
	              tempStop: {
	               
	                for (Iterator<String> it = initialStateUITab.getEnumMap()
	                    .keySet().iterator(); it.hasNext();) {

	                  propertyType = it.next();
	                  //String propertyType = property + type;
	                  
	                  HashSet<String> tempLanSet = initialStateUITab.getLanType().get(type);
	                  for (Iterator<String> lans = tempLanSet.iterator(); lans
	                      .hasNext();) {

	                    String lan = lans.next();
	                    String labelKey = propertyType + lan;

	                    Collection<String> labels = initialStateUITab.getLabelMap()
	                        .values();

	                    if (labels.contains(initialStateUITab.getLabelMap().get(
	                        labelKey))) {

	                      if (initialStateUITab.getLabelMap().get(labelKey).equals(
	                          label[i].getText())) {

	                        enumProperty = true;
	                        break tempStop;
	                      }
	                    }
	                  }
	                }
	              }

	              if (!enumProperty) {

	               
	                insertEditRow.addElement(((JTextField) field[i]).getText());

	              } else {

	                HashSet<String> tempEnumContent = initialStateUITab
	                    .getEnumMap().get(propertyType);

	                

	                if (tempEnumContent.contains(((JTextField) field[i]).getText()
	                    .trim())) {

	                  insertEditRow.addElement(((JTextField) field[i]).getText());

	                } else {

	                  JOptionPane.showMessageDialog(null,
	                      "The content of the field " + label[i].getText()
	                          + " must be in the enum range");
	                  return;

	                }

	              }

	            }

	          } else {

	            insertEditRow.addElement(((JButton) field[i]).getText());

	          }
	        }
	        if (cRow < 0) {
	          cRow = 0;
	        }
	        if (cRow > rowData.size()) {
	          cRow = rowData.size();
	        }

	        if (!createNew) {
	          model.insertRow(cRow, insertEditRow);

	          boolean flag;
	          if (cRow < 0 || cRow > rowData.size()) {
	            flag = false;
	          } else {
	            flag = true;
	          }

	          if (flag) {
	            model.removeRow(cRow + 1);
	          }
	        }

	        if (createNew) {
	          model.insertRow(cRow + 1, insertEditRow);
	        }

	        setVisible(false);
	        dispose();

	      }
	    };
	    ok.addActionListener(okListener);

	    JButton cancel = new JButton("CANCEL");
	    ActionListener cancelListener = new ActionListener() {
	      public void actionPerformed(ActionEvent e) {
	        setVisible(false);
	        dispose();
	      }
	    };
	    cancel.addActionListener(cancelListener);

	    buttonPanel.add(ok);
	    buttonPanel.add(cancel);

	    return buttonPanel;

	  }

	  private JTable table;
	  private Vector<Object> row;
	  private Vector<Vector<Object>> rowData;
	  private JLabel[] label;
	  private Object[] field;
	  private DefaultTableModel model;
	  private int cRow;
	  private String type;
	  private HashMap<String, HashSet<Integer>> constrainMap;
	  private HashSet<Integer> booleanPosition;
	  private HashSet<Integer> integerPosition;
	  private InitialStateUITab initialStateUITab;
	  private String objectType;
	  private boolean createNew;
	  private HashMap<String, HashSet<String>> ranTypePropertyMap;
	  private HashMap<String, HashSet<String>> valueExprPropertyMap;
	  private HashMap<String, HashSet<String>> lanTypeMap;
	  private HashMap<String, String> labelMap;
	  private Frame owner;

	}
