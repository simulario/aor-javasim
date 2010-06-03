package interaction.agentControl;

 
public class CarInteractionController extends aors.model.agtsim.AgentSubject.AgentController {
  private model.agtsim.CarAgentSubject agentSubject;

  public CarInteractionController(model.agtsim.CarAgentSubject agentSubject) {
    super(agentSubject);
    this.agentSubject = agentSubject;
    this.suspendedRules.add("SpeedUp_Rule");
    this.suspendedRules.add("SlowDown_Rule");
    this.addKeyEvent("Up", "SpeedUp");
    this.addMouseEvent("SpeedUp", "click", "SpeedUp");
    this.addKeyEvent("Down", "SlowDown");
    this.addMouseEvent("SlowDown", "click", "SlowDown");
  }

  public aors.model.intevt.InternalEvent createEvent(String eventName, java.util.Map<String, String> eventData) {
    if(("SpeedUp".compareTo(eventName) == 0L)) {
      model.agtsim.CarAgentSubject.__SpeedUpInteractionEvent speedUpInteractionEvent = this.agentSubject.new __SpeedUpInteractionEvent(this.getCurrentSimulationStep());
      speedUpInteractionEvent.setSpeed(aors.model.dataTypes.AORSInteger.valueOf(eventData.get("speed")).getValue());
      return speedUpInteractionEvent;
    }
    if(("SlowDown".compareTo(eventName) == 0L)) {
      model.agtsim.CarAgentSubject.__SlowDownInteractionEvent slowDownInteractionEvent = this.agentSubject.new __SlowDownInteractionEvent(this.getCurrentSimulationStep());
      slowDownInteractionEvent.setSpeed(aors.model.dataTypes.AORSInteger.valueOf(eventData.get("speed")).getValue());
      return slowDownInteractionEvent;
    }
    return null;
  }
}
