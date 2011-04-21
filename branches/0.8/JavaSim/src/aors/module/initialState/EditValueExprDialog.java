package aors.module.initialState;
import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Frame;
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
import javax.swing.JPanel;
import javax.swing.JTextField;
import aors.module.initialState.gui.InitialStateUITab;
import aors.module.initialState.gui.ValueExprPropertyContainer;




public class EditValueExprDialog extends JDialog{
	
	 
	  private static final long serialVersionUID = 1L;
	  private JPanel contentPanel;//used to contain language choice ComboBox and correspondent field
	  private JComboBox ranTypesBox;//language choice ComboBox
	  private String  selectedLan;//the selected programming language
	  private InitialStateUITab initialStateUITab;//
	  private int cRow;//the selected row number 
	  private String type;//the entity type
	  private String property;//the property of element Slot that contains ValueExpr sub element
	  private HashMap<String,JTextField> rFieldMap = new HashMap<String,JTextField>();//field mapping
	  private boolean createNew;//used to determine creation or edition 
	  private ValueExprPropertyContainer newContainer;//the container of ValueExprs for each slot element 
	  private String objectType;
	  private String tempVContainerKey;
	  
	   
	  public EditValueExprDialog(
			  
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
	  
	  
	  public void init(){
	    
	    
	  if(objectType != null){
	    
	     tempVContainerKey = objectType + type;
	     
	  }else{
	    
	     tempVContainerKey = type;
	    
	  }
		//get all containers that hold whole column of ValueExpr   
		Vector<ValueExprPropertyContainer> tempContainers = 
		initialStateUITab.getValueExprPropertyContainerMap().get(property+tempVContainerKey);
		//get the selected container with the help of selected row number
		ValueExprPropertyContainer tempContainer = tempContainers.get(cRow);
		
		//String editPropertyType = tempContainer.getPropertyType();
		HashMap<String,HashSet<String>> editPropertyMap = tempContainer.getValueExprPropertyMap();
		HashMap<String,String> editValueMap = tempContainer.getValueExprValueMap();
		
		if(!createNew){//process edition 
		
			process(editPropertyMap,editValueMap);
		
		}else{// process creation
			
		
			HashMap<String,HashSet<String>> newPropertyMap = new HashMap<String,HashSet<String>>();
			HashMap<String,String> newValueMap = new HashMap<String,String>();
			
			HashSet<String> newTempContainer = new HashSet<String>();
			newTempContainer.add("Java");
			newTempContainer.add("JavaScript");
			newTempContainer.add("PHP");
			
			newPropertyMap.put(property+tempVContainerKey,newTempContainer);
			for(Iterator<String> it= newTempContainer.iterator(); it.hasNext();){
        
            String tempLan = it.next();
            newValueMap.put(property+tempVContainerKey+tempLan, "");
            
    }
			
			
			newContainer = new ValueExprPropertyContainer(property+tempVContainerKey,newPropertyMap,newValueMap);
			tempContainers.add(cRow+1,newContainer);
			process(newPropertyMap,newValueMap);
			
		}
		
	}	
	  
	  public void process(final HashMap<String,HashSet<String>> tempPropertyMap,
			                  final HashMap<String,String> tempValueMap){
		  
		  contentPanel = new JPanel();
		  contentPanel.setLayout(new BoxLayout(contentPanel,BoxLayout.Y_AXIS));
		  Vector<String> tempLanContainer = null;
		  
		  tempLanContainer = transferContainer(tempPropertyMap.get(property+tempVContainerKey));
			  
		  for(int i=0; i<tempLanContainer.size(); i++){
				  
				  String tempContent = tempValueMap.get(property+tempVContainerKey+tempLanContainer.get(i));
				  JTextField tempField = new JTextField(15);
				  tempField.setText(tempContent);
				  rFieldMap.put(property+tempVContainerKey+tempLanContainer.get(i), tempField);
				  
		  }
		 
		  
		 			
			ranTypesBox = new JComboBox(tempLanContainer);
			ranTypesBox.setSelectedItem(tempLanContainer.get(0));
			contentPanel.add(ranTypesBox);
			contentPanel.add(rFieldMap.get(property+tempVContainerKey+tempLanContainer.get(0)));
			
			JPanel buttonPanel = new JPanel();
			buttonPanel.setLayout(new FlowLayout(FlowLayout.RIGHT));
			JButton okButton = new JButton("OK");
			JButton cancelButton = new JButton("Cancle");
			buttonPanel.add(okButton);
			buttonPanel.add(cancelButton);
			
			this.add(contentPanel, BorderLayout.CENTER);
			this.add(buttonPanel, BorderLayout.SOUTH);
			this.pack();
			
			ranTypesBox.addItemListener(new ItemListener(){
				
				@Override
				public void itemStateChanged(ItemEvent e) {
					
					if(ItemEvent.SELECTED == e.getStateChange()){
						
						contentPanel.removeAll();
						
						selectedLan = (String)e.getItem();
				    	
				    	ranTypesBox.setSelectedItem(selectedLan);
			    		contentPanel.add(ranTypesBox);
			    		contentPanel.add(rFieldMap.get(property+tempVContainerKey+selectedLan));
			    		contentPanel.revalidate();
			    		
			    		
			    		
				     }
				}
							
			});
			
		    okButton.addActionListener(new ActionListener(){

				@Override
				public void actionPerformed(ActionEvent e) {
					
					System.out.println("Before OK the valueOf tempPropertyMap:=> " + tempPropertyMap);
					System.out.println("Before OK the valueOf tempValueMap:=> " + tempValueMap);
					
					String tempKey = property+tempVContainerKey;
					Vector<String> tempLans = transferContainer(tempPropertyMap.get(tempKey));
						
					for(int i=0; i<tempLans.size();i++){
							
						String tempLan = tempLans.get(i);
						String tempValue = rFieldMap.get(tempKey+tempLan).getText();
						tempValueMap.put(tempKey+tempLan, tempValue);
					
					}
						
					System.out.println("After of OK the valueOf tempPropertyMap:=> " + tempPropertyMap);
					System.out.println("After of OK the valueOf tempValueMap:=> " + tempValueMap);	
						
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
	  
   public Vector<String> transferContainer(HashSet<String> lanSet){
      
      Vector<String> lanContainer = new Vector<String>();
      lanContainer.addAll(lanSet);
      
      return lanContainer;
      
    }
	  
}

