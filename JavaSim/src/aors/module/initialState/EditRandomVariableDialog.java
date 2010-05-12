package aors.module.initialState;
import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Frame;
import java.awt.GridLayout;
import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Vector;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import aors.module.initialState.gui.InitialStateUITab;
import aors.module.initialState.gui.RanVarPropertyContainer;




public class EditRandomVariableDialog extends JDialog implements RanVarConstant{
	
	 
    private static final long serialVersionUID = -1611854728481662291L;
    private JPanel contentPanel;
	  private JComboBox ranTypesBox;
	  private String  selectedType;
	  private String  selectedLan;
	  private Vector<String> lanContainer = null;
      private InitialStateUITab initialStateUITab;
	  private int cRow;
	  private String type;
	  private String property;
	  private HashMap<String,JLabel> rLabelMap = new HashMap<String,JLabel>();
	  private HashMap<String,JTextField> rFieldMap = new HashMap<String,JTextField>();
	  private boolean createNew;
	  private RanVarPropertyContainer newContainer,oldContainer;
	  private String objectType;
	  private String tempRContainerKey;
	  
	  
	  
	  public EditRandomVariableDialog(
			        Frame frame, String title, boolean modal, 
              int cRow, String type, String property,
              InitialStateUITab initialStateUITab,
              boolean createNew,
              String objectType
	  )
	  
	  {
	    
		super(frame, title, modal);
		this.cRow = cRow;
		this.type =type;
		this.property = property;
		this.initialStateUITab = initialStateUITab;
		this.createNew = createNew;
		this.objectType = objectType;
		this.setLayout(new BorderLayout());
		
		JDialog.setDefaultLookAndFeelDecorated(true);
	    
		if (frame != null) {
			
	      Dimension parentSize = frame.getSize();
	      Point p = frame.getLocation();
	      setLocation(p.x + parentSize.width / 4, p.y + parentSize.height / 4);
	      
	    }
	         
	     init();
	     
	     
	     
   }
	  
	  
     public Vector<String> transferContainer(HashSet<String> lanSet){
    	  
    	  Vector<String> lanContainer = new Vector<String>();
    	  lanContainer.addAll(lanSet);
    	  
    	  return lanContainer;
    	  
      }
	  
	  
	  
	  
	  public void init(){
	    
	    if(objectType != null){
	      
	       tempRContainerKey = objectType + type;
	       
	    }else{
	      
	       tempRContainerKey = type;
	      
	    }  
	    
		  
		Vector<RanVarPropertyContainer> tempContainers = initialStateUITab.getRanVarPropertyContainerMap().get(property+tempRContainerKey);
		oldContainer = tempContainers.get(cRow);
		
		selectedType = oldContainer.getSelectedType();
		
		HashMap<String,HashSet<String>> editPropertyMap = oldContainer.getRanVarPropertyMap();
		HashMap<String,HashSet<String>> editLanMap = oldContainer.getRanVarLanMap();
		HashMap<String,String> editValueMap = oldContainer.getRanVarValueMap();
		
		if(!createNew){
		  
		  if(editLanMap==null){
		    
		    process(editPropertyMap,editValueMap,null);
		  
		  }else{
		
			  process(editPropertyMap,editValueMap,editLanMap);
		  
		  }
		  
		}else{
		  
		  HashMap<String,HashSet<String>> newPropertyMap = new HashMap<String,HashSet<String>>();
		  HashMap<String,String> newValueMap = new HashMap<String,String>();
		  
		  for(Iterator<String> it = editPropertyMap.keySet().iterator(); it.hasNext();){
        
		    String tempProperty = it.next();
        HashSet<String> tempVariables =  editPropertyMap.get(tempProperty);
        newPropertyMap.put(tempProperty, tempVariables);
        
     }
		  
		  for(Iterator<String> it = editValueMap.keySet().iterator(); it.hasNext();){
        
        String tempvalueKey = it.next();
        String tempValue = "";
        newValueMap.put(tempvalueKey, tempValue);
        
     }
		  
		  
      if(editLanMap==null){
			
        newContainer = new RanVarPropertyContainer(selectedType,newPropertyMap,newValueMap);
        tempContainers.add(cRow+1,newContainer);
        process(newPropertyMap,newValueMap,null);
        
        
      }else{
        
        HashMap<String,HashSet<String>> newLanMap = new HashMap<String,HashSet<String>>();
        HashSet<String> newLanContainer = new HashSet<String>();
        for(int a=0; a<ranLan.length; a++){
          
          newLanContainer.add(ranLan[a]);
        }
        
        for(Iterator<String> it = editPropertyMap.keySet().iterator(); it.hasNext();){
          
          String tempProperty = it.next();
          newLanMap.put(tempProperty, newLanContainer);
          
       }
        
        newContainer = new RanVarPropertyContainer(selectedType,newPropertyMap,newLanMap,newValueMap);
        tempContainers.add(cRow+1,newContainer);
        process(newPropertyMap,newValueMap,newLanMap);
        
        
      }
		 		
		}
		
	}	
	  
	  public void process(final HashMap<String,HashSet<String>> tempPropertyMap,
			                  final HashMap<String,String> tempValueMap, 
			                  final HashMap<String,HashSet<String>> tempLanMap){
		  
		  
	    contentPanel = new JPanel();
		  contentPanel.setLayout(new BoxLayout(contentPanel,BoxLayout.Y_AXIS));
		  
		  //final Vector<String> tempLanContainer = null;
		  
		 
		  if(tempLanMap != null){
		    
		    lanContainer = transferContainer(tempLanMap.get(property+tempRContainerKey+selectedType));
		    selectedLan = lanContainer.get(0);
		    
		  }
		  		  
		 
		  
		  for(int i=0; i<ranTypeArr.length; i++){
			   
			  for(int k=0; k<ranExprProperty[i].length; k++){
			           	 
			      
			      String tempKey = null;
			      if(lanContainer == null){
			        
			        JLabel tempLabel = new JLabel(ranProperty[i][k]);
	            JTextField tempField = new JTextField(15);
			        
			         tempKey = property+tempRContainerKey+ranTypeArr[i]+ranProperty[i][k];
			         if(selectedType.equals(ranTypeArr[i])){
			           
			           String tempContent = tempValueMap.get(tempKey);
                 tempField.setText(tempContent);
			           
			         }
			         
			            rLabelMap.put(tempKey, tempLabel);
                  rFieldMap.put(tempKey, tempField);
			        
			      }else{
			      
			          for(int l=0; l<lanContainer.size(); l++){
			            
			            JLabel tempLabel = new JLabel(ranExprProperty[i][k]);
			            JTextField tempField = new JTextField(15);
			          		             
			             tempKey = property+tempRContainerKey+ranTypeArr[i]+ ranExprProperty[i][k]+lanContainer.get(l);
			             if(selectedType.equals(ranTypeArr[i])){
			            
			             String tempContent = tempValueMap.get(tempKey);
			             tempField.setText(tempContent);
			             }
			             
			             rLabelMap.put(tempKey, tempLabel);
			             rFieldMap.put(tempKey, tempField);
			        	   
			    }
		     }
			      
			      
			      
		  } 
		  	
		  }
			
			ranTypesBox = new JComboBox(ranTypeArr);
			ranTypesBox.setSelectedItem(selectedType);
			contentPanel.add(ranTypesBox);
			
			
			
			
			Vector<String> tempProperties = transferContainer(tempPropertyMap.get(property+tempRContainerKey+selectedType));
			
			JPanel initialPanel = new JPanel();
			initialPanel.setLayout(new GridLayout(0,2,6,0));
			
			for(int p=0; p<tempProperties.size(); p++){
			  
			  String tempKey=null;
								
				if(lanContainer == null){
				  tempKey = property+tempRContainerKey+selectedType+tempProperties.get(p);
				  
				}else{
				  tempKey = property+tempRContainerKey+selectedType+tempProperties.get(p)+lanContainer.get(0);
	      
				}
				initialPanel.add(rLabelMap.get(tempKey));
                initialPanel.add(rFieldMap.get(tempKey));
								
			}
			
			contentPanel.add(initialPanel);
			
			JComboBox lanTypesBox = null;
			JPanel buttonPanel = new JPanel();
            buttonPanel.setLayout(new FlowLayout(FlowLayout.RIGHT));
			
			if(lanContainer != null){
			  
			  lanTypesBox = new JComboBox(lanContainer);
			  buttonPanel.add(lanTypesBox);
			  
			  lanTypesBox.addItemListener(new ItemListener(){
	        
	        @Override
	        public void itemStateChanged(ItemEvent e) {
	          
	          if(ItemEvent.SELECTED == e.getStateChange()){
	            
	            contentPanel.removeAll();
	            selectedLan = (String)e.getItem();
	            ranTypesBox.setSelectedItem(selectedType);
	            contentPanel.add(ranTypesBox);
	              
	            for(int j=0; j<ranTypeArr.length; j++){
	                
	                if(selectedType.equals(ranTypeArr[j])){
	                  
	                  JPanel innerPanel = new JPanel();
	                  innerPanel.setLayout(new GridLayout(0,2,6,0));
	                  
	                  for(int a=0; a<ranExprProperty[j].length; a++){
	                  
	                      String tempKey = property+tempRContainerKey+ranTypeArr[j]+ranExprProperty[j][a]+selectedLan;
	                      innerPanel.add(rLabelMap.get(tempKey));
	                      innerPanel.add(rFieldMap.get(tempKey));
	                          
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
						
			JButton okButton = new JButton("OK");
			JButton cancelButton = new JButton("Cancle");
			buttonPanel.add(okButton);
			buttonPanel.add(cancelButton);
			
			this.add(contentPanel,BorderLayout.CENTER);
			this.add(buttonPanel, BorderLayout.SOUTH);
			this.pack();
			
			ranTypesBox.addItemListener(new ItemListener(){
				
				@Override
				public void itemStateChanged(ItemEvent e) {
					
					if(ItemEvent.SELECTED == e.getStateChange()){
						
						contentPanel.removeAll();
						selectedType = (String)e.getItem();
				    ranTypesBox.setSelectedItem(selectedType);
			    	contentPanel.add(ranTypesBox);
			    		
			    		for(int j=0; j<ranTypeArr.length; j++){
			    			
			    			if(selectedType.equals(ranTypeArr[j])){
			    				
			    				JPanel innerPanel = new JPanel();
					    		innerPanel.setLayout(new GridLayout(0,2,6,0));
					    		
					    	  if(lanContainer == null){
					    	    
                      for(int a=0; a<ranExprProperty[j].length; a++){
                      
                      String tempKey = property+tempRContainerKey+ranTypeArr[j]+ranProperty[j][a];
                      innerPanel.add(rLabelMap.get(tempKey));
                      innerPanel.add(rFieldMap.get(tempKey));
                          
                     } 
					    	  				    	    
					    	  }else{
					    	    
					    	    for(int a=0; a<ranExprProperty[j].length; a++){
		                  
                      String tempKey = property+tempRContainerKey+ranTypeArr[j]+ranExprProperty[j][a]+selectedLan;
                      innerPanel.add(rLabelMap.get(tempKey));
                      innerPanel.add(rFieldMap.get(tempKey));
                          
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
			
			
			
			
			okButton.addActionListener(new ActionListener(){

				@Override
				public void actionPerformed(ActionEvent e) {
					
					
					
					String tempKey = property+tempRContainerKey+selectedType;
					
					if(tempPropertyMap.containsKey(tempKey)){
						
						Vector<String> tempPropertyContainer = transferContainer(tempPropertyMap.get(tempKey));
						
						
						if(lanContainer == null){
						  
						  for(int i=0; i<tempPropertyContainer.size(); i++){
						    
						    String tempProperty = tempPropertyContainer.get(i);
						    String tempValue = rFieldMap.get(tempKey+tempProperty).getText();
	                        tempValueMap.put(tempKey+tempProperty, tempValue);
						   						    
						  }
						  
						}else{
						  
						  Vector<String> tempLanContainer = transferContainer(tempLanMap.get(tempKey));
	            
	            for(int i=0; i<tempPropertyContainer.size(); i++){
	              
	               String tempProperty = tempPropertyContainer.get(i);
	               
	               for(int j=0; j<tempLanContainer.size(); j++){
	               String tempLan = tempLanContainer.get(j);
	               String tempValue = rFieldMap.get(tempKey+tempProperty+tempLan).getText();
	               tempValueMap.put(tempKey+tempProperty+tempLan, tempValue);
	               
	            }
	            }  
						  
						  
						  
						}
						
						
															
					}else{
						
						
						
						tempPropertyMap.clear();
						tempValueMap.clear();
						if(tempLanMap!=null){
						tempLanMap.clear();
						}
						
						
											
						
						if(newContainer==null){
						  
						  oldContainer.setSelectedType(selectedType);
						  
						}else{
						
					  	newContainer.setSelectedType(selectedType);
					  	
						}
						
						if(lanContainer == null){
						  
						  for(int a=0; a<ranTypeArr.length; a++){
	               
	               if(ranTypeArr[a].equals(selectedType)){
	                 
	                 HashSet<String> tempSet = new HashSet<String>();
	                 for(int v=0; v<ranProperty[a].length; v++){
	                   
	                   String tempProperty = property+tempRContainerKey+selectedType+ranProperty[a][v];
	                   tempSet.add(ranProperty[a][v]);
	                   String tempValue = rFieldMap.get(tempProperty).getText();
	                   tempValueMap.put(tempProperty, tempValue);
	                   
	                }
	                 
	                 tempPropertyMap.put(property+tempRContainerKey+selectedType, tempSet);
	                   
	               }
	             }
  						  
						  
						  
						}else{
						  
						  for(int a=0; a<ranTypeArr.length; a++){
	               
	               if(ranTypeArr[a].equals(selectedType)){
	                 
	                 HashSet<String> tempPropertySet = new HashSet<String>();
	                 HashSet<String> tempLanSet = new HashSet<String>();
	                 String tempNewKey;
	                 for(int v=0; v<ranExprProperty[a].length; v++){
	                   
	                   tempPropertySet.add(ranExprProperty[a][v]);
	                   
	                   for(int r=0; r<ranLan.length; r++){
	                   
	                   tempLanSet.add(ranLan[r]);
	                   tempNewKey = property+tempRContainerKey+selectedType+ranExprProperty[a][v]+ranLan[r];
	                   
	                   String tempValue = rFieldMap.get(tempNewKey).getText();
	                   tempValueMap.put(tempNewKey, tempValue);
	                   
	                }
	                } 
	                 tempPropertyMap.put(property+tempRContainerKey+selectedType, tempPropertySet);
	                 tempLanMap.put(property+tempRContainerKey+selectedType, tempLanSet);   
	               }
	             }
						  
						  
						  
						}
						
						
					}
				         setVisible(false);
			             dispose();
					    
				       }
		    	   }
			
		    );
		    
		    
		    
		    
		    cancelButton.addActionListener(new ActionListener(){
				 
				 public void actionPerformed(ActionEvent ae){
					 
					 setVisible(false);
					 dispose();
				}
			 }
			 );
			
		         
		  
		  
		  
		  
		  
		  
		  
	  }
	  
	  
	  
}

