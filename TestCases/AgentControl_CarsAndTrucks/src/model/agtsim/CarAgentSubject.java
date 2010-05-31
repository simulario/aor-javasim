package model.agtsim;

import aors.util.Random;
import controller.SimModel;
import controller.SimParameter;
import controller.Simulator;

public class CarAgentSubject extends model.agtsim.VehicleAgentSubject {
  private String sentMessage;
  public final static int MEMORY_SIZE = 0;

  public CarAgentSubject(long __id, String __name, String sentMessage, String receivedMessage, long maxVelocity, long speedLimit, long myVelocity) {
    super(__id, __name, receivedMessage, maxVelocity, speedLimit, myVelocity);
    this.setController(new interaction.agentControl.CarInteractionController(this));
    this.setSentMessage(sentMessage);
    this.reactionRules.add(this.new __SpeedUpInteractionRule("__SpeedUpInteractionRule", this));
    this.reactionRules.add(this.new __SlowDownInteractionRule("__SlowDownInteractionRule", this));
  }

  public void setSentMessage(String sentMessage) {
    if((this.sentMessage != sentMessage)) {
      this.sentMessage = sentMessage;
      this.propertyChangeSupport.firePropertyChange(new java.beans.PropertyChangeEvent(this, "sentMessage", null, this.sentMessage));
    }
  }

  public String getSentMessage() {
    return this.sentMessage;
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
    beliefProperties.put("sentMessage", this.getSentMessage());
    return beliefProperties;
  }
  private abstract class __AbstractInteractionEvent extends aors.model.intevt.InternalEvent {
    private long speed;

    public __AbstractInteractionEvent(long occurrenceTime) {
      super(occurrenceTime);
    }

    public long getSpeed() {
      return this.speed;
    }

    public void setSpeed(long speed) {
      this.speed = speed;
    }
  }
  public class __SpeedUpInteractionEvent extends model.agtsim.CarAgentSubject.__AbstractInteractionEvent {

    public __SpeedUpInteractionEvent(long occurrenceTime) {
      super(occurrenceTime);
    }
  }
  public class __SlowDownInteractionEvent extends model.agtsim.CarAgentSubject.__AbstractInteractionEvent {

    public __SlowDownInteractionEvent(long occurrenceTime) {
      super(occurrenceTime);
    }
  }
  public class __SpeedUpInteractionRule extends aors.model.agtsim.ReactionRule {
    private model.agtsim.CarAgentSubject.__SpeedUpInteractionEvent gui;
    private model.agtsim.CarAgentSubject __agentSubject;
    private boolean __changesSucceeded = true;

    public __SpeedUpInteractionRule(String __ruleName, aors.model.agtsim.AgentSubject __agentSubject) {
      super(__ruleName, __agentSubject);
      this.__agentSubject = ((model.agtsim.CarAgentSubject)this.getAgentSubject());
    }

    protected java.util.ArrayList<aors.model.envevt.ActionEvent> doResultingActionEvents() {
      java.util.ArrayList<aors.model.envevt.ActionEvent> __actionEvents = new java.util.ArrayList<aors.model.envevt.ActionEvent>();
      long __agentId = this.__agentSubject.getId();
      long __occurrenceTime = this.gui.getOccurrenceTime();
      aors.model.envsim.PhysicalAgentObject __agentRef = ((aors.model.envsim.PhysicalAgentObject)this.__agentSubject.getAgentObject());
      if(this.__changesSucceeded) {
        model.envevt.SpeedUp __speedUp_1 = new model.envevt.SpeedUp((__occurrenceTime + 1L), __agentId, __agentRef);
        __speedUp_1.setVelocity(gui.getSpeed());
        model.agtsim.CarAgentSubject.SpeedUp__ActionRule __speedUp_1__ActionRule = this.__agentSubject.new SpeedUp__ActionRule(this.__agentSubject, __speedUp_1);
        if(__speedUp_1__ActionRule.execute()) {
          __actionEvents.add(__speedUp_1);
        } else {
          this.__changesSucceeded = false;
        }
      }
      return __actionEvents;
    }

    public String getTriggeringEventType() {
      return "__SpeedUpInteractionEvent";
    }

    public void setTriggeringEvent(aors.model.AtomicEvent atomicEvent) {
      this.gui = ((model.agtsim.CarAgentSubject.__SpeedUpInteractionEvent)atomicEvent);
      this.setTriggeredTime(this.gui.getOccurrenceTime());
    }

    public void execute() {
      this.resultingActionEvents.addAll(this.doResultingActionEvents());
      this.__changesSucceeded = true;
    }

    public String getMessageType() {
      return "";
    }
  }
  public class __SlowDownInteractionRule extends aors.model.agtsim.ReactionRule {
    private model.agtsim.CarAgentSubject.__SlowDownInteractionEvent gui;
    private model.agtsim.CarAgentSubject __agentSubject;
    private boolean __changesSucceeded = true;

    public __SlowDownInteractionRule(String __ruleName, aors.model.agtsim.AgentSubject __agentSubject) {
      super(__ruleName, __agentSubject);
      this.__agentSubject = ((model.agtsim.CarAgentSubject)this.getAgentSubject());
    }

    protected java.util.ArrayList<aors.model.envevt.ActionEvent> doResultingActionEvents() {
      java.util.ArrayList<aors.model.envevt.ActionEvent> __actionEvents = new java.util.ArrayList<aors.model.envevt.ActionEvent>();
      long __agentId = this.__agentSubject.getId();
      long __occurrenceTime = this.gui.getOccurrenceTime();
      aors.model.envsim.PhysicalAgentObject __agentRef = ((aors.model.envsim.PhysicalAgentObject)this.__agentSubject.getAgentObject());
      if(this.__changesSucceeded) {
        model.envevt.SlowDown __slowDown_1 = new model.envevt.SlowDown((__occurrenceTime + 1L), __agentId, __agentRef);
        __slowDown_1.setVelocity(gui.getSpeed());
        model.agtsim.CarAgentSubject.SlowDown__ActionRule __slowDown_1__ActionRule = this.__agentSubject.new SlowDown__ActionRule(this.__agentSubject, __slowDown_1);
        if(__slowDown_1__ActionRule.execute()) {
          __actionEvents.add(__slowDown_1);
        } else {
          this.__changesSucceeded = false;
        }
      }
      return __actionEvents;
    }

    public String getTriggeringEventType() {
      return "__SlowDownInteractionEvent";
    }

    public void setTriggeringEvent(aors.model.AtomicEvent atomicEvent) {
      this.gui = ((model.agtsim.CarAgentSubject.__SlowDownInteractionEvent)atomicEvent);
      this.setTriggeredTime(this.gui.getOccurrenceTime());
    }

    public void execute() {
      this.resultingActionEvents.addAll(this.doResultingActionEvents());
      this.__changesSucceeded = true;
    }

    public String getMessageType() {
      return "";
    }
  }
}
