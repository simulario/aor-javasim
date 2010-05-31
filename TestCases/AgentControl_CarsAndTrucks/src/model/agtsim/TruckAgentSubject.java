package model.agtsim;

import aors.util.Random;
import controller.SimModel;
import controller.SimParameter;
import controller.Simulator;
import model.envsim.Car;
import model.envsim.SpeedLimitCancelationSign;
import model.envsim.SpeedLimitSign;
import model.envsim.TruckSpeedLimitSign;
import model.envsim.Vehicle;

public class TruckAgentSubject extends model.agtsim.VehicleAgentSubject {
  public final static int MEMORY_SIZE = 0;

  public TruckAgentSubject(long __id, String __name, String receivedMessage, long maxVelocity, long speedLimit, long myVelocity) {
    super(__id, __name, receivedMessage, maxVelocity, speedLimit, myVelocity);
    this.reactionRules.add(this.new TruckSpeedLimitStartPercEvtAgtRule("TruckSpeedLimitStartPercEvtAgtRule", this));
  }

  public void activateMonitoring() {
    super.activateMonitoring();
  }

  public void acceptChanges() {
    super.acceptChanges();
  }

  public void rejectChanges() {
    super.rejectChanges();
  }

  public java.util.Map<String, Object> getBeliefProperties() {
    java.util.Map<String, Object> beliefProperties = super.getBeliefProperties();
    return beliefProperties;
  }

  public class TruckSpeedLimitStartPercEvtAgtRule extends aors.model.agtsim.ReactionRule {
    private aors.model.envevt.PhysicalObjectPerceptionEvent e;
    private model.agtsim.TruckAgentSubject truck;

    public TruckSpeedLimitStartPercEvtAgtRule(String __ruleName, aors.model.agtsim.AgentSubject __agentSubject) {
      super(__ruleName, __agentSubject);
      this.truck = ((model.agtsim.TruckAgentSubject)this.getAgentSubject());
    }

    protected boolean condition() {
      return true;
    }

    protected void doStateEffects() {
      this.truck.setSpeedLimit(((TruckSpeedLimitSign)e.getPerceivedPhysicalObject()).getAdmMaxVelocity());
    }

    public String getTriggeringEventType() {
      return "PhysicalObjectPerceptionEvent";
    }

    public void setTriggeringEvent(aors.model.AtomicEvent atomicEvent) {
      this.e = ((aors.model.envevt.PhysicalObjectPerceptionEvent)atomicEvent);
      this.setTriggeredTime(this.e.getOccurrenceTime());
    }

    public void execute() {
      if(!((this.e.getPerceivedPhysicalObject() instanceof model.envsim.TruckSpeedLimitSign))) {
        return;
      }
      this.doStateEffects();
    }

    public String getMessageType() {
      return "";
    }
  }
}
