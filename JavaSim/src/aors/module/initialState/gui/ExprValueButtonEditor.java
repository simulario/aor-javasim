package aors.module.initialState.gui;
import java.awt.*;
import java.awt.event.*;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Vector;

import javax.swing.*;
import javax.swing.table.*;
 
/**
 * @version 1.0 11/09/98
 */
public class ExprValueButtonEditor extends DefaultCellEditor {
  
  /**
   * 
   */
  private static final long serialVersionUID = -7906091828974872584L;
  protected JButton button;
  private String    label;
  private boolean   isPushed;
  private ShowValueExprDialog sspd ;
  private int cRow;
  private String type;
  private HashMap<String,HashSet<String>> ValueExprTypePropertyMap = null;
  private HashMap<String,Vector<ValueExprPropertyContainer>> ValueExprPropertyContainerMap = null;
  private String colHeadValue;
  private String objectType;
  private HashMap<String,HashSet<String>>lanTypeMap;
  private HashMap<String,String> labelMap;
 
  
  
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
