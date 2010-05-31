package interaction.agentControl;

 
public class CarInteractionController extends aors.module.agentControl.AgentController<model.agtsim.CarAgentSubject> {

  public CarInteractionController(model.agtsim.CarAgentSubject agentSubject) {
    super(agentSubject);
    this.suspendedRules.add("SpeedUp_Rule");
    this.suspendedRules.add("SlowDown_Rule");
    this.addKeyEvent("Up", "SpeedUp");
    this.addMouseEvent("SpeedUp", "click", "SpeedUp");
    this.addKeyEvent("Down", "SlowDown");
    this.addMouseEvent("SlowDown", "click", "SlowDown");
  }

  public aors.model.intevt.InternalEvent createEvent(long occurrenceTime, String eventName, java.util.Map<String, String> eventData) {
    if(("SpeedUp".compareTo(eventName) == 0L)) {
      model.agtsim.CarAgentSubject.__SpeedUpInteractionEvent speedUpInteractionEvent = this.agentSubject.new __SpeedUpInteractionEvent(occurrenceTime);
      speedUpInteractionEvent.setSpeed(aors.model.dataTypes.AORSInteger.valueOf(eventData.get("speed")).getValue());
      return speedUpInteractionEvent;
    }
    if(("SlowDown".compareTo(eventName) == 0L)) {
      model.agtsim.CarAgentSubject.__SlowDownInteractionEvent slowDownInteractionEvent = this.agentSubject.new __SlowDownInteractionEvent(occurrenceTime);
      slowDownInteractionEvent.setSpeed(aors.model.dataTypes.AORSInteger.valueOf(eventData.get("speed")).getValue());
      return slowDownInteractionEvent;
    }
    return null;
  }
}
