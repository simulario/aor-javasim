package model.agtsim;

import aors.util.Random;
import controller.SimModel;
import controller.SimParameter;
import controller.Simulator;
import model.envsim.Car;
import model.envsim.SpeedLimitCancelationSign;
import model.envsim.SpeedLimitSign;
import model.envsim.Truck;
import model.envsim.TruckSpeedLimitSign;

public class VehicleAgentSubject extends aors.model.agtsim.AgentSubject {
  private String receivedMessage;
  private long maxVelocity;
  private long speedLimit;
  private long myVelocity;
  public final static int MEMORY_SIZE = 0;

  public VehicleAgentSubject(long __id, String __name, String receivedMessage, long maxVelocity, long speedLimit, long myVelocity) {
    super(__id, __name);
    this.setReceivedMessage(receivedMessage);
    this.setMaxVelocity(maxVelocity);
    this.setSpeedLimit(speedLimit);
    this.setMyVelocity(myVelocity);
    this.reactionRules = new java.util.ArrayList<aors.model.agtsim.ReactionRule>();
    this.reactionRules.add(this.new SlowDown_Rule("SlowDown_Rule", this));
    this.reactionRules.add(this.new SpeedUp_Rule("SpeedUp_Rule", this));
    this.reactionRules.add(this.new SpeedLimitStart_Rule("SpeedLimitStart_Rule", this));
    this.reactionRules.add(this.new SpeedLimitEnd_Rule("SpeedLimitEnd_Rule", this));
  }

  public void setReceivedMessage(String receivedMessage) {
    if((this.receivedMessage != receivedMessage)) {
      this.receivedMessage = receivedMessage;
      this.propertyChangeSupport.firePropertyChange(new java.beans.PropertyChangeEvent(this, "receivedMessage", null, this.receivedMessage));
    }
  }

  public void setMaxVelocity(long maxVelocity) {
    if((this.maxVelocity != maxVelocity)) {
      this.maxVelocity = maxVelocity;
      this.propertyChangeSupport.firePropertyChange(new java.beans.PropertyChangeEvent(this, "maxVelocity", null, this.maxVelocity));
    }
  }

  public void setSpeedLimit(long speedLimit) {
    if((this.speedLimit != speedLimit)) {
      this.speedLimit = speedLimit;
      this.propertyChangeSupport.firePropertyChange(new java.beans.PropertyChangeEvent(this, "speedLimit", null, this.speedLimit));
    }
  }

  public void setMyVelocity(long myVelocity) {
    if((this.myVelocity != myVelocity)) {
      this.myVelocity = myVelocity;
      this.propertyChangeSupport.firePropertyChange(new java.beans.PropertyChangeEvent(this, "myVelocity", null, this.myVelocity));
    }
  }

  public String getReceivedMessage() {
    return this.receivedMessage;
  }

  public long getMaxVelocity() {
    return this.maxVelocity;
  }

  public long getSpeedLimit() {
    return this.speedLimit;
  }

  public long getMyVelocity() {
    return this.myVelocity;
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
    beliefProperties.put("receivedMessage", this.getReceivedMessage());
    beliefProperties.put("maxVelocity", this.getMaxVelocity());
    beliefProperties.put("speedLimit", this.getSpeedLimit());
    beliefProperties.put("myVelocity", this.getMyVelocity());
    return beliefProperties;
  }

  public class SlowDown__ActionRule {
    private VehicleAgentSubject vehicle;
    private model.envevt.SlowDown e;

    public SlowDown__ActionRule(VehicleAgentSubject vehicle, model.envevt.SlowDown e) {
      this.vehicle = vehicle;
      this.e = e;
    }

    public boolean execute() {
      if(this.condition1()) {
        this.then1UpdateAgt1();
        return true;
      }
      this.elseUpdateEvt1();
      this.elseUpdateAgt1();
      return true;
    }

    private boolean condition1() {
      return (vehicle.getMyVelocity() - e.getVelocity()) >= 0;
    }

    private void then1UpdateAgt1() {
      this.vehicle.setMyVelocity(vehicle.getMyVelocity() - e.getVelocity());
    }

    private void elseUpdateAgt1() {
      this.vehicle.setMyVelocity(0);
    }

    private void elseUpdateEvt1() {
      this.e.setVelocity(vehicle.getMyVelocity());
    }
  }

  public class SpeedUp__ActionRule {
    private VehicleAgentSubject vehicle;
    private model.envevt.SpeedUp e;

    public SpeedUp__ActionRule(VehicleAgentSubject vehicle, model.envevt.SpeedUp e) {
      this.vehicle = vehicle;
      this.e = e;
    }

    public boolean execute() {
      if(this.condition1()) {
        this.then1UpdateAgt1();
        return true;
      }
      this.elseUpdateEvt1();
      this.elseUpdateAgt1();
      return true;
    }

    private boolean condition1() {
      return (vehicle.getMyVelocity() + e.getVelocity()) <= vehicle.getMaxVelocity();
    }

    private void then1UpdateAgt1() {
      this.vehicle.setMyVelocity(vehicle.getMyVelocity() + e.getVelocity());
    }

    private void elseUpdateAgt1() {
      this.vehicle.setMyVelocity(vehicle.getMaxVelocity());
    }

    private void elseUpdateEvt1() {
      this.e.setVelocity(vehicle.getMaxVelocity() - vehicle.getMyVelocity());
    }
  }

  public class SlowDown_Rule extends aors.model.agtsim.ReactionRule {
    private aors.model.intevt.EachSimulationStep __eachSimulationStep;
    private model.agtsim.VehicleAgentSubject vehicle;
    private boolean __changesSucceeded = true;

    public SlowDown_Rule(String __ruleName, aors.model.agtsim.AgentSubject __agentSubject) {
      super(__ruleName, __agentSubject);
      this.vehicle = ((model.agtsim.VehicleAgentSubject)this.getAgentSubject());
    }

    protected boolean condition() {
      try {
        return vehicle.getSpeedLimit() > 0 && vehicle.getMyVelocity() > vehicle.getSpeedLimit();
      } catch(java.lang.Exception e) {
        return false;
      }
    }

    protected java.util.ArrayList<aors.model.envevt.ActionEvent> thenResultingActionEvents() {
      java.util.ArrayList<aors.model.envevt.ActionEvent> __actionEvents = new java.util.ArrayList<aors.model.envevt.ActionEvent>();
      long __agentId = this.vehicle.getId();
      long __occurrenceTime = this.__eachSimulationStep.getOccurrenceTime();
      aors.model.envsim.PhysicalAgentObject __agentRef = ((aors.model.envsim.PhysicalAgentObject)this.vehicle.getAgentObject());
      if(this.__changesSucceeded) {
        model.envevt.SlowDown __slowDown_1 = new model.envevt.SlowDown((__occurrenceTime + 1L), __agentId, __agentRef);
        __slowDown_1.setVelocity(7L);
        model.agtsim.VehicleAgentSubject.SlowDown__ActionRule __slowDown_1__ActionRule = this.vehicle.new SlowDown__ActionRule(this.vehicle, __slowDown_1);
        if(__slowDown_1__ActionRule.execute()) {
          __actionEvents.add(__slowDown_1);
        } else {
          this.__changesSucceeded = false;
        }
      }
      return __actionEvents;
    }

    public String getTriggeringEventType() {
      return "EachSimulationStep";
    }

    public void setTriggeringEvent(aors.model.AtomicEvent atomicEvent) {
      this.__eachSimulationStep = ((aors.model.intevt.EachSimulationStep)atomicEvent);
      this.setTriggeredTime(this.__eachSimulationStep.getOccurrenceTime());
    }

    public void execute() {
      this.vehicle.activateMonitoring();
      if(this.condition()) {
        this.resultingActionEvents.addAll(this.thenResultingActionEvents());
      } else {
      }
      if(this.__changesSucceeded) {
        this.vehicle.acceptChanges();
      } else {
        this.vehicle.rejectChanges();
        this.resultingInternalEvents.clear();
        this.resultingActionEvents.clear();
        this.__changesSucceeded = true;
      }
    }

    public String getMessageType() {
      return "";
    }
  }

  public class SpeedUp_Rule extends aors.model.agtsim.ReactionRule {
    private aors.model.intevt.EachSimulationStep __eachSimulationStep;
    private model.agtsim.VehicleAgentSubject vehicle;
    private boolean __changesSucceeded = true;

    public SpeedUp_Rule(String __ruleName, aors.model.agtsim.AgentSubject __agentSubject) {
      super(__ruleName, __agentSubject);
      this.vehicle = ((model.agtsim.VehicleAgentSubject)this.getAgentSubject());
    }

    protected boolean condition() {
      try {
        return vehicle.getSpeedLimit() == 0;
      } catch(java.lang.Exception e) {
        return false;
      }
    }

    protected java.util.ArrayList<aors.model.envevt.ActionEvent> thenResultingActionEvents() {
      java.util.ArrayList<aors.model.envevt.ActionEvent> __actionEvents = new java.util.ArrayList<aors.model.envevt.ActionEvent>();
      long __agentId = this.vehicle.getId();
      long __occurrenceTime = this.__eachSimulationStep.getOccurrenceTime();
      aors.model.envsim.PhysicalAgentObject __agentRef = ((aors.model.envsim.PhysicalAgentObject)this.vehicle.getAgentObject());
      if(this.__changesSucceeded) {
        model.envevt.SpeedUp __speedUp_1 = new model.envevt.SpeedUp((__occurrenceTime + 1L), __agentId, __agentRef);
        __speedUp_1.setVelocity(7L);
        model.agtsim.VehicleAgentSubject.SpeedUp__ActionRule __speedUp_1__ActionRule = this.vehicle.new SpeedUp__ActionRule(this.vehicle, __speedUp_1);
        if(__speedUp_1__ActionRule.execute()) {
          __actionEvents.add(__speedUp_1);
        } else {
          this.__changesSucceeded = false;
        }
      }
      return __actionEvents;
    }

    public String getTriggeringEventType() {
      return "EachSimulationStep";
    }

    public void setTriggeringEvent(aors.model.AtomicEvent atomicEvent) {
      this.__eachSimulationStep = ((aors.model.intevt.EachSimulationStep)atomicEvent);
      this.setTriggeredTime(this.__eachSimulationStep.getOccurrenceTime());
    }

    public void execute() {
      this.vehicle.activateMonitoring();
      if(this.condition()) {
        this.resultingActionEvents.addAll(this.thenResultingActionEvents());
      } else {
      }
      if(this.__changesSucceeded) {
        this.vehicle.acceptChanges();
      } else {
        this.vehicle.rejectChanges();
        this.resultingInternalEvents.clear();
        this.resultingActionEvents.clear();
        this.__changesSucceeded = true;
      }
    }

    public String getMessageType() {
      return "";
    }
  }

  public class SpeedLimitStart_Rule extends aors.model.agtsim.ReactionRule {
    private aors.model.envevt.PhysicalObjectPerceptionEvent e;
    private model.agtsim.VehicleAgentSubject vehicle;

    public SpeedLimitStart_Rule(String __ruleName, aors.model.agtsim.AgentSubject __agentSubject) {
      super(__ruleName, __agentSubject);
      this.vehicle = ((model.agtsim.VehicleAgentSubject)this.getAgentSubject());
    }

    protected boolean condition() {
      return true;
    }

    protected void doStateEffects() {
      this.vehicle.setSpeedLimit(((SpeedLimitSign)e.getPerceivedPhysicalObject()).getAdmMaxVelocity());
    }

    public String getTriggeringEventType() {
      return "PhysicalObjectPerceptionEvent";
    }

    public void setTriggeringEvent(aors.model.AtomicEvent atomicEvent) {
      this.e = ((aors.model.envevt.PhysicalObjectPerceptionEvent)atomicEvent);
      this.setTriggeredTime(this.e.getOccurrenceTime());
    }

    public void execute() {
      if(!((this.e.getPerceivedPhysicalObject() instanceof model.envsim.SpeedLimitSign))) {
        return;
      }
      this.doStateEffects();
    }

    public String getMessageType() {
      return "";
    }
  }

  public class SpeedLimitEnd_Rule extends aors.model.agtsim.ReactionRule {
    private aors.model.envevt.PhysicalObjectPerceptionEvent e;
    private model.agtsim.VehicleAgentSubject vehicle;

    public SpeedLimitEnd_Rule(String __ruleName, aors.model.agtsim.AgentSubject __agentSubject) {
      super(__ruleName, __agentSubject);
      this.vehicle = ((model.agtsim.VehicleAgentSubject)this.getAgentSubject());
    }

    protected boolean condition() {
      return true;
    }

    protected void doStateEffects() {
      this.vehicle.setSpeedLimit(0L);
    }

    public String getTriggeringEventType() {
      return "PhysicalObjectPerceptionEvent";
    }

    public void setTriggeringEvent(aors.model.AtomicEvent atomicEvent) {
      this.e = ((aors.model.envevt.PhysicalObjectPerceptionEvent)atomicEvent);
      this.setTriggeredTime(this.e.getOccurrenceTime());
    }

    public void execute() {
      if(!((this.e.getPerceivedPhysicalObject() instanceof model.envsim.SpeedLimitCancelationSign))) {
        return;
      }
      this.doStateEffects();
    }

    public String getMessageType() {
      return "";
    }
  }
}
