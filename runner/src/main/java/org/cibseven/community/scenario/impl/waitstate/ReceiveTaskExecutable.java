package org.cibseven.community.scenario.impl.waitstate;


import org.cibseven.bpm.engine.history.HistoricActivityInstance;
import org.cibseven.bpm.engine.runtime.EventSubscription;
import org.cibseven.community.scenario.ProcessScenario;
import org.cibseven.community.scenario.act.Action;
import org.cibseven.community.scenario.delegate.EventSubscriptionDelegate;
import org.cibseven.community.scenario.impl.ProcessRunnerImpl;

import java.util.Date;
import java.util.Map;

/**
 * @author Martin Schimak
 */
public class ReceiveTaskExecutable extends MessageIntermediateCatchEventExecutable {

  public ReceiveTaskExecutable(ProcessRunnerImpl runner, HistoricActivityInstance instance) {
    super(runner, instance);
  }

  @Override
  protected Action<EventSubscriptionDelegate> action(ProcessScenario scenario) {
    return scenario.waitsAtReceiveTask(getActivityId());
  }

  @Override
  protected EventSubscription getDelegate() {
    return getRuntimeService().createEventSubscriptionQuery().eventType("message").activityId(getActivityId()).executionId(getExecutionId()).singleResult();
  }

  @Override
  public void receive() {
    EventSubscription eventSubscription = getDelegate();
    if (eventSubscription != null) {
      super.receive();
    } else {
      getRuntimeService().signal(getExecutionId());
    }
  }

  @Override
  public void receive(Map<String, Object> variables) {
    EventSubscription eventSubscription = getDelegate();
    if (eventSubscription != null) {
      super.receive(variables);
    } else {
      getRuntimeService().signal(getExecutionId(), variables);
    }
  }

  @Override
  public String getId() {
    return super.getId();
  }

  @Override
  public String getEventType() {
    if (delegate == null)
      throw new UnsupportedOperationException("Not supported for Receive Tasks " +
        "used without a message event subscription.");
    return super.getEventType();
  }

  @Override
  public String getEventName() {
    if (delegate == null)
      throw new UnsupportedOperationException("Not supported for Receive Tasks " +
        "used without a message event subscription.");
    return super.getEventName();
  }

  @Override
  public String getProcessInstanceId() {
    if (delegate == null)
      throw new UnsupportedOperationException("Not supported for Receive Tasks " +
        "used without a message event subscription.");
    return super.getProcessInstanceId();
  }

  @Override
  public String getTenantId() {
    if (delegate == null)
      throw new UnsupportedOperationException("Not supported for Receive Tasks " +
        "used without a message event subscription.");
    return super.getTenantId();
  }

  @Override
  public Date getCreated() {
    if (delegate == null)
      throw new UnsupportedOperationException("Not supported for Receive Tasks " +
        "used without a message event subscription.");
    return super.getCreated();
  }

}
