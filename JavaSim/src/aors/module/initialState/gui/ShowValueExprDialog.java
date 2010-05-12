package aors.module.initialState.gui;
import java.awt.*;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Vector;
import java.util.Iterator;
import javax.swing.*;


public class ShowValueExprDialog extends JDialog {
	
	 
	  private static final long serialVersionUID = 5682449313374788800L;
	  JPanel contentPanel;
	  JComboBox ValueExprLansBox;
	  HashMap<String,HashSet<String>> ValueExprTypePropertyMap = null;
	  HashMap<String,Vector<ValueExprPropertyContainer>> ValueExprPropertyContainerMap = null;
	  String selectedPropertyType;
	  String selectedProperty;
	  HashMap<String,HashSet<String>> tempPropertyMap;
	  HashMap<String,String> tempValueMap;
	  private HashMap<String,JTextField> rFieldMap = new HashMap<String,JTextField>();
	  //private String objectType;
	  int cRow;
	  String type;
	  String colHeadValue;
	  String tempKey;
	  
	  
	  public ShowValueExprDialog(Frame frame, String title, boolean modal, 
			                          int cRow, String inputType, String colHeadValue,
			                          HashMap<String,HashSet<String>> ValueExprTypePropertyMap,
			                          HashMap<String,Vector<ValueExprPropertyContainer>> ValueExprPropertyContainerMap,
			                          HashMap<String,HashSet<String>> lanTypeMap,
			                          HashMap<String,String> labelMap,
			                          String objectType){
	    
		  
		  super(frame, title, modal);
		  this.cRow = cRow;
		  this.type = inputType;
		  this.colHeadValue = colHeadValue;
		  this.ValueExprTypePropertyMap = ValueExprTypePropertyMap;
		  this.ValueExprPropertyContainerMap = ValueExprPropertyContainerMap;
		 		  
		  this.setLayout(new BorderLayout());
		  
		  if (frame != null){
		    
		      Dimension parentSize = frame.getSize();
		      Point p = frame.getLocation();
		      setLocation(p.x + parentSize.width / 4, p.y + parentSize.height / 4);
		  }
		  
		  contentPanel = new JPanel();
		  contentPanel.setLayout(new BoxLayout(contentPanel,BoxLayout.Y_AXIS));
		  
      String tempSelectedKey = null;
      
      if(objectType != null){
        
         tempSelectedKey = objectType+type;
         
      }else{
        
         tempSelectedKey = type;
      }
		  
		  
		  HashSet<String> propertySet = ValueExprTypePropertyMap.get(tempSelectedKey);
		  HashSet<String> lanSet = lanTypeMap.get(type);
		  
		  for(Iterator<String> it = propertySet.iterator(); it.hasNext();){
			  
			   String tempProperty = it.next();
			   
			   for(Iterator<String> lans = lanSet.iterator(); lans.hasNext();){
			     
			     String tempSelectedLan = lans.next();
			   
			     if(labelMap.get(tempProperty+type+tempSelectedLan).equals(colHeadValue)){
				   
				   tempKey = tempProperty+tempSelectedKey;
				   selectedProperty = tempProperty;
				   
				   Vector<ValueExprPropertyContainer> tempVector = ValueExprPropertyContainerMap.get(tempKey);
				   
				   ValueExprPropertyContainer tempContainer = tempVector.get(cRow);
				   
				   
				   selectedPropertyType = tempContainer.getPropertyType();
				   tempPropertyMap = tempContainer.getValueExprPropertyMap();
				   Vector<String> tempLanContainer = transferContainer(tempPropertyMap.get(selectedPropertyType));
				   tempValueMap = tempContainer.getValueExprValueMap();
				   
				   for(int i=0; i<tempLanContainer.size(); i++){
					   
					    String tempLan = tempLanContainer.get(i);
					    String tempKey = selectedPropertyType+tempLan;
					    String tempText = tempValueMap.get(tempKey);
						JTextField tempField = new JTextField(15);
					    tempField.setText(tempText);
					    rFieldMap.put(tempKey, tempField);
					   
				   }
				   
				   
				  
				   
				   ValueExprLansBox = new JComboBox(tempLanContainer);
				   ValueExprLansBox.setSelectedItem(tempLanContainer.get(0));
				   contentPanel.add(ValueExprLansBox);
				  
				   
				   JTextField tempField = rFieldMap.get(selectedPropertyType+tempLanContainer.get(0));
				   				   
				   contentPanel.add(tempField);
				   contentPanel.add(Box.createVerticalStrut(10));
				   getContentPane().add(contentPanel,BorderLayout.CENTER);
				   pack();
				   
				   ValueExprLansBox.addItemListener(new ItemListener(){
						
					   public void itemStateChanged(ItemEvent e) {
						    
							if(ItemEvent.SELECTED == e.getStateChange()){
						    	
						    	contentPanel.removeAll();
						    	
						    	ValueExprLansBox.setSelectedItem(e.getItem());
					    		contentPanel.add(ValueExprLansBox);
					    		JPanel innerPanel = new JPanel();
                                innerPanel.setLayout(new GridLayout(0,1,0,0));
                                String tempLan = (String)e.getItem();
					    		innerPanel.add(rFieldMap.get(selectedPropertyType+tempLan));
					    		contentPanel.add(innerPanel);  
						    	contentPanel.revalidate();
						    		
						    		
						          
					     }
					    }
					  }
					 );
				   
			   }
			 }
		   }
	  }
  
	  public Vector<String> transferContainer(HashSet<String> lanSet){
      
      Vector<String> lanContainer = new Vector<String>();
      lanContainer.addAll(lanSet);
      
      return lanContainer;
      
    }
	 
}
