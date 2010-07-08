package aors.module.initialState.gui;
import java.awt.*;
import java.awt.event.*;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Vector;

import javax.swing.*;
import javax.swing.table.*;
 
/*this class is used to render for JButton when user click
the JButton, and then a JDialog will be pop up*/

public class ButtonEditor extends DefaultCellEditor {
  
 
  private static final long serialVersionUID = 1L;
  protected JButton button;//used to render the cell 
  private String label;// label on the button
  private boolean   isPushed;//used to test if user click the button
  private ShowRandomVariableDialog showRanDialog ; //show the JDialog box to user
  private int cRow;//the row contains the button 
  private String type;//the entity type contains property whose column will be render as JButton
  private HashMap<String,HashSet<String>> ranTypePropertyMap = null;//mapp between type and property
  //mapping between the RandomVariable property of an entity type and its container
  private HashMap<String,Vector<RanVarPropertyContainer>> ranVarPropertyContainerMap = null;
  private String colHeadValue;
  private HashMap<String,HashSet<String>>lanTypeMap;//lan set for a RandomVariable property
  private HashMap<String,String> labelMap;
  private String objectType;//the object type of an objectEvent type, otherwise null
 
  
  
  public ButtonEditor(JCheckBox checkBox, final JTable table,String type, 
		                  HashMap<String,HashSet<String>> ranTypePropertyMap,
		                  HashMap<String,Vector<RanVarPropertyContainer>> ranVarPropertyContainerMap,
		                  HashMap<String,HashSet<String>> lanTypeMap,
                          HashMap<String,String> labelMap,
		                  String objectType) {
    
    
    super(checkBox);
    button = new JButton();
    button.setOpaque(true);
    this.type = type;
    this.ranTypePropertyMap = ranTypePropertyMap;
    this.ranVarPropertyContainerMap = ranVarPropertyContainerMap;
    this.lanTypeMap = lanTypeMap;
    this.labelMap = labelMap;
    this.objectType = objectType;
    
    button.addActionListener(new ActionListener() {
    
    @SuppressWarnings("unchecked")
	public void actionPerformed(ActionEvent e) {
    	  
      cRow = table.getSelectedRow(); 
      colHeadValue = (String) table.getColumnModel().getColumn(table.getSelectedColumn()).getHeaderValue();
        
         
      DefaultTableModel model = (DefaultTableModel)table.getModel();
      Vector<Vector<Object>> tempData = (Vector<Vector<Object>>) model.getDataVector();
      
			if (cRow < 0) {
				  cRow = 0;
			}
			if (cRow > tempData.size()) {
				  cRow = tempData.size();
			}
			  	  
          fireEditingStopped();
      }
    }
    );
    
   
  }
  
  public Component getTableCellEditorComponent(JTable table, Object value,
                   boolean isSelected, int row, int column) {
	  
    
	if (isSelected) {
	  
      button.setForeground(table.getSelectionForeground());
      button.setBackground(table.getSelectionBackground());
      
    } else{
      
      button.setForeground(table.getForeground());
      button.setBackground(table.getBackground());
    }
	
    label = (value ==null) ? "Show" : value.toString();
    button.setText( label );
    isPushed = true;
    return button;
    
    
  }
 
  
  public Object getCellEditorValue() {
     
	  //if user click the JButton, then pop up ShowRandomVariableDialog
	  if (isPushed)  {
    	
       Frame frame = (Frame)SwingUtilities.getRoot(button);
       //System.out.println("type: "+ type);
       //System.out.println("objectType: "+ objectType);
       
       showRanDialog = new ShowRandomVariableDialog(
                                           frame,
    		                               "RandomVariableDialog",
    		                               true,
    		                               cRow,
    		                               type,
    		                               colHeadValue,
    		                               ranTypePropertyMap,
    		                               ranVarPropertyContainerMap,
    		                               lanTypeMap,
                                           labelMap,
    		                               objectType);
       
       showRanDialog.setVisible(true); // show the JDialog box for the RandomVariable
       
    }
    
    isPushed = false;
    return new String(label);
    
}
  
  
   
  
  public boolean stopCellEditing() {
    isPushed = false;
    return super.stopCellEditing();
  }
 
  
  protected void fireEditingStopped() {
    super.fireEditingStopped();
  }
  
}

