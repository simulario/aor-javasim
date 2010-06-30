package aors.module.initialState.gui;
import java.awt.*;
import java.awt.event.*;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Vector;

import javax.swing.*;
import javax.swing.table.*;
 
/*We will use this class to render the table cell as JButton and 
when the user click the button a new JDialog window will be pop 
up*/


public class ExprValueButtonEditor extends DefaultCellEditor {
  
  
  private static final long serialVersionUID = -7906091828974872584L;
  protected JButton button;// used to render the table cell as JButton
  private String    label;// the label of JButton
  private boolean   isPushed;//test if user has clicked the Button or not
  private ShowValueExprDialog sspd ;//JDialog Box used to show the content of element ValueExpr
  private int cRow; //choose the correspondent row that contained the cell of ValueExpr
  private String type;// the type of Entity that contains ValueExpr
  //map between a enetity and its ValueExpr, we use it to check which table cell is ValueExpr 
  //and will be render as JButton
  private HashMap<String,HashSet<String>> ValueExprTypePropertyMap = null;
  //map between a property and its ValueExpr container
  private HashMap<String,Vector<ValueExprPropertyContainer>> ValueExprPropertyContainerMap = null;
  
  private String colHeadValue;//table head value of ValueExpr
  private String objectType; 
  private HashMap<String,HashSet<String>>lanTypeMap;//
  private HashMap<String,String> labelMap;// map between a property and its table head value in certain language
 
  
  
  public ExprValueButtonEditor(JCheckBox checkBox, final JTable table,String type, 
		                           HashMap<String,HashSet<String>> ValueExprTypePropertyMap,
		                           HashMap<String,Vector<ValueExprPropertyContainer>> ValueExprPropertyContainerMap,
		                           HashMap<String,HashSet<String>> lanTypeMap,
		                           HashMap<String,String> labelMap,
		                           String objectType) {
    
    
    super(checkBox);
    
    button = new JButton();
    button.setOpaque(true);
    
    this.type = type;
    this.ValueExprTypePropertyMap = ValueExprTypePropertyMap;
    this.ValueExprPropertyContainerMap = ValueExprPropertyContainerMap;
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
    
	  if (isPushed)  {
    	
      
       Frame frame = (Frame)SwingUtilities.getRoot(button);
       sspd = new ShowValueExprDialog(frame,
    		                          "ValueExprDialog",
    		                          true,
    		                          cRow,
    		                          type,
    		                          colHeadValue,
    		                          ValueExprTypePropertyMap,
    		                          ValueExprPropertyContainerMap,
    		                          lanTypeMap,
    		                          labelMap,
    		                          objectType);
       
       sspd.setVisible(true);
       
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
