
import java.io.File;
import java.io.FileOutputStream;

import javax.xml.bind.*;

import aorsimulator.logger.*;


/**
 * This class demonstrates how easy the AOR Logger API can be used to write valid XML. 
 * 
 * @author Marco Pehla, http://www.informatik.tu-cottbus.de/~mpehla
 * @date   May 20, 2008
 */
public class LoggingExample {

	private final String nameSpaces = "aorsimulator.logger"; //namespaces have been set in the aorbindings.jxb
	private final ObjectFactory objectFactory = new ObjectFactory();
	private JAXBContext context;
	
	
	public LoggingExample() {
		try {
			context  = JAXBContext.newInstance(nameSpaces);//get a new instance
	
			SimulationOutput simulationOutput = this.objectFactory.createSimulationOutput();//create the root element 
			simulationOutput.setSimulationName("Example Simulation");//set the elements attribute simulationName
			
			SimulationStep simulationStep = this.objectFactory.createSimulationStep();//create an <SimulationStep>
			simulationStep.setTime("1");//set the time attribute
			simulationStep.setClock("1");//set the clock attribute
			
			simulationOutput.getSimulationStep().add(simulationStep);//set <SimulationStep> as child of the root <SimulationOutput>
			
			//...to be continued ;)
			
			Marshaller marshaller = context.createMarshaller();
			marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT,new Boolean(true));//formated output: false
            //marshaller.setProperty(Marshaller.JAXB_SCHEMA_LOCATION,MY_SCHEMA_LOCATION);//maybe useful in future
			
			marshaller.marshal(simulationOutput,new FileOutputStream(new File("ExampleLog.xml"))); //write the content to an local XML file
		} catch (Exception e) {//catch any exception
			e.printStackTrace();
		}//try-catch

	}//constructor
	
	
	/**
	 * @param args
	 */
	public static void main(String[] args) {
		new LoggingExample();//create a new instance of this very class
	}//main()

}//class: LoggingExample
