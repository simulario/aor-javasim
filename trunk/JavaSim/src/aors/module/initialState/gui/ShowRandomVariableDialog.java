package aors.module.initialState.gui;
import java.awt.*;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Vector;

import javax.swing.*;


import aors.module.initialState.RanVarConstant;


/*We will use this class to show JDialog Box that contains
information about RandomVariable*/
public class ShowRandomVariableDialog extends JDialog implements RanVarConstant{
	
   
    private static final long serialVersionUID = 5110207145900859900L;
    private JPanel contentPanel;
    //ranTypes used to contain all probability distribution types for a RandomVariable
    //lanTypesBox used to contain language choices for a RandomVariable 
    private JComboBox ranTypesBox, lanTypesBox;
   
    private String selectedType;//selected probability distribution by user
    private String selectedLan;////selected language by user
    private HashMap<String,HashSet<String>> tempPropertyMap;
    //map between probability distribution variable and its correspondent value for a Randomvariable
    private HashMap<String,String> tempValueMap;
    //map between a RandomVariable and its correspondent language set 
    private HashMap<String,HashSet<String>> tempLanMap;
    
	private HashMap<String,JLabel> rLabelMap = new HashMap<String,JLabel>();
	private HashMap<String,JTextField> rFieldMap = new HashMap<String,JTextField>();
	private String tempKey;
	private Vector<String> tempLanContainer = null;
	 
	  
	  public ShowRandomVariableDialog(Frame frame, String title, boolean modal, 
			                              int cRow, String type, String colHeadValue,
			                              HashMap<String,HashSet<String>> ranTypePropertyMap,
			                              HashMap<String,Vector<RanVarPropertyContainer>>
	                                      ranVarPropertyContainerMap,
	                                      HashMap<String,HashSet<String>> lanTypeMap,
	                                      HashMap<String,String> labelMap,
	                                      String objectType)
	  {
		  
		  super(frame, title, modal);
		  this.setLayout(new BorderLayout());
		  
		  if (frame != null) {
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
		  
		  //get all property with RandomVariable in an entity type
		  HashSet<String> propertySet = ranTypePropertyMap.get(tempSelectedKey);
		  HashSet<String> lanSet = lanTypeMap.get(type);
		  for(Iterator<String> it = propertySet.iterator(); it.hasNext();){
			  
			   String tempProperty = it.next();
			   
			   for(Iterator<String> lans = lanSet.iterator(); lans.hasNext();){
			   
			   String tempSelectedLan = lans.next();
			     
			   if(labelMap.get(tempProperty+type+tempSelectedLan).equals(colHeadValue)){
				   
				   tempKey = tempProperty+tempSelectedKey;//
				   Vector<RanVarPropertyContainer> tempVector = ranVarPropertyContainerMap.get(tempKey);
				   //get correspondent container that contains all content for a RandomVariable for a cell
				   RanVarPropertyContainer tempContainer = tempVector.get(cRow);
				   				   
				   selectedType = tempContainer.getSelectedType();
				   tempPropertyMap = tempContainer.getRanVarPropertyMap();
				   tempValueMap = tempContainer.getRanVarValueMap();
				   tempLanMap = tempContainer.getRanVarLanMap();
				   
				  
				   
				   
				   if(tempLanMap == null){//RandomVariable without language choice
				   
				    for(int i=0; i<ranTypeArr.length; i++){
					   
						for(int k=0; k<ranProperty[i].length; k++){
					           	 
					             String tempRanProperty = ranProperty[i][k];
					             JLabel tempLabel = new JLabel(tempRanProperty);
					             
					             JTextField tempField = new JTextField(15);
					             String tempLabelText = tempKey+ranTypeArr[i]+tempRanProperty;
					             rLabelMap.put(tempLabelText, tempLabel);
					             rFieldMap.put(tempLabelText, tempField);
					    }
				   }
				 }
				 else{//RandomVariable with language choice
				   
				   tempLanContainer = transferContainer(tempLanMap.get(tempKey+selectedType));
                   selectedLan = tempLanContainer.get(0);
					   
					   
					   for(int i=0; i<ranTypeArr.length; i++){
						   
							  for(int k=0; k<ranExprProperty[i].length; k++){
								  
								   String tempRanExprProperty = ranExprProperty[i][k];
								  
								     
								   for(int t=0; t<tempLanContainer.size(); t++){
									   
									   	 String tempLabelText = tempKey+ranTypeArr[i]+tempRanExprProperty+tempLanContainer.get(t);
							             
							             JLabel tempLabel = new JLabel(tempRanExprProperty);
							             JTextField tempField = new JTextField(15);
							             
							             if(selectedType.equals(ranTypeArr[i])){
							            	 //System.out.println("the tempLabelText in constructor: => " + tempLabelText);
							            	 String tempContent = tempValueMap.get(tempLabelText);
							            	 tempField.setText(tempContent);
							             }
							             
							             rLabelMap.put(tempLabelText, tempLabel);
							             rFieldMap.put(tempLabelText, tempField);
							        	   
							        	        	 
						         }
						     }
					     }
					}
				   
				   
				   
				   ranTypesBox = new JComboBox(ranTypeArr);
				   ranTypesBox.setSelectedItem(selectedType);
				   contentPanel.add(ranTypesBox);
				   
				   if(tempLanMap != null){
				     
				   lanTypesBox = new JComboBox(tempLanContainer);
				   lanTypesBox.setSelectedItem(tempLanContainer.get(0));
                   lanTypesBox.addItemListener(
               
                   new ItemListener(){
                   //process RandomVariable with language choice state change 
                
                   public void itemStateChanged(ItemEvent e) {
                   
                   if(ItemEvent.SELECTED == e.getStateChange()){
                     
                     
                     contentPanel.removeAll();
                     ranTypesBox.setSelectedItem(selectedType);
                     contentPanel.add(ranTypesBox);
                     selectedLan = (String)e.getItem();
                     lanTypesBox.setSelectedItem(selectedLan);
                     contentPanel.add(lanTypesBox);
                     
                     for(int j=0; j<ranTypeArr.length; j++){
                        
                        if(selectedType.equals(ranTypeArr[j])){ 
                          
                         JPanel innerPanel = new JPanel();
                         innerPanel.setLayout(new GridLayout(0,2,6,0)); 
                         
                         if(tempLanMap == null){
                        
                         
                         }
                         else{
                           
                           for(int p=0; p<ranExprProperty[j].length; p++){
                              
                               String tempRanKey = tempKey+ranTypeArr[j]+ranExprProperty[j][p]+selectedLan;
                               innerPanel.add(rLabelMap.get(tempRanKey));
                               innerPanel.add(rFieldMap.get(tempRanKey));
                                                
                             }   
                          }
                          
                          contentPanel.add(innerPanel); 
                          contentPanel.revalidate();
                      }
                     }
                  }
                }
                      }
                  );
				   contentPanel.add(lanTypesBox);
				   
				   }
				   
				   
				
				   
				   Vector<String> tempPropertyContainer = transferContainer(tempPropertyMap.get(tempKey+selectedType));
				   
				   JPanel fieldPanel = new JPanel();
				   fieldPanel.setLayout(new GridLayout(0,2,6,0));
				   
				   if(tempLanMap == null){
				   
				   for(Iterator<String> properties = tempPropertyContainer.iterator(); properties.hasNext();){
						 
						 String property = properties.next();
						 String tempValue = tempValueMap.get(tempKey+selectedType+property);
						 JTextField tempField = rFieldMap.get(tempKey+selectedType+property);
						 tempField.setText(tempValue);
						 fieldPanel.add(rLabelMap.get(tempKey+selectedType+property));
						 fieldPanel.add(tempField);
									     
				   }
				   }else{
					    
					     for(int p=0; p<tempPropertyContainer.size(); p++){
					    	 
					       String tempRanKey = tempKey+selectedType+tempPropertyContainer.get(p)+tempLanContainer.get(0);
					       fieldPanel.add(rLabelMap.get(tempRanKey));
						   fieldPanel.add(rFieldMap.get(tempRanKey));
					     
				   }
				   }				   
				   contentPanel.add(fieldPanel);
				   getContentPane().add(contentPanel,BorderLayout.CENTER);
				   pack();
				   
				   ranTypesBox.addItemListener(new ItemListener(){
						
					   public void itemStateChanged(ItemEvent e) {
						    
							if(ItemEvent.SELECTED == e.getStateChange()){
						    	
						    	contentPanel.removeAll();
						    	selectedType = (String)e.getItem();
						    	ranTypesBox.setSelectedItem(selectedType);
						    	contentPanel.add(ranTypesBox);
						    	
						    	if(tempLanMap != null){
						    	  
						    	  lanTypesBox.setSelectedItem(selectedLan);
	                              contentPanel.add(lanTypesBox);
						    	  
						    	}
					    		
					    		
					    		for(int j=0; j<ranTypeArr.length; j++){
						    	
					    			if(selectedType.equals(ranTypeArr[j])){ 
					    				
					    			 JPanel innerPanel = new JPanel();
							    	 innerPanel.setLayout(new GridLayout(0,2,6,0));	
							    	 
							    	 if(tempLanMap == null){
						    				    		
						    		  for(int p=0; p<ranProperty[j].length; p++){
						    			  
						    			   String tempRanKey = tempKey+ranTypeArr[j]+ranProperty[j][p];
						    			   innerPanel.add(rLabelMap.get(tempRanKey));
						    			   innerPanel.add(rFieldMap.get(tempRanKey));
						    							    			  
						    		  }
							    	 }
							    	 else{
							    		 
							    		 for(int p=0; p<ranExprProperty[j].length; p++){
							    			  
							    			   String tempRanKey = tempKey+ranTypeArr[j]+ranExprProperty[j][p]+selectedLan;
							    			   innerPanel.add(rLabelMap.get(tempRanKey));
							    			   innerPanel.add(rFieldMap.get(tempRanKey));
							    							    			  
							    	     }	 
							    	  }
					    				
					    		
						    		
						    		contentPanel.add(innerPanel);	
						    		contentPanel.revalidate();
						    							          
					     }
					    }
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