package org.cibseven.community.scenario.impl.waitstate;


import org.cibseven.bpm.engine.history.HistoricActivityInstance;
import org.cibseven.bpm.engine.runtime.EventSubscription;
import org.cibseven.community.scenario.ProcessScenario;
import org.cibseven.community.scenario.act.Action;
import org.cibseven.community.scenario.delegate.EventSubscriptionDelegate;
import org.cibseven.community.scenario.impl.ProcessRunnerImpl;
import org.cibseven.community.scenario.impl.delegate.AbstractEventSubscriptionDelegate;
import org.cibseven.community.scenario.impl.delegate.EventSubscriptionDelegateImpl;

import java.util.Map;

/**
 * @author Martin Schimak
 */
public class SignalIntermediateCatchEventExecutable extends AbstractEventSubscriptionDelegate {

  private EventSubscriptionDelegate eventSubscriptionDelegate;

  public SignalIntermediateCatchEventExecutable(ProcessRunnerImpl runner, HistoricActivityInstance instance) {
    super(runner, instance);
    eventSubscriptionDelegate = EventSubscriptionDelegateImpl.newInstance(this, delegate);
  }

  @Override
  protected EventSubscription getDelegate() {
    return getRuntimeService().createEventSubscriptionQuery().eventType("signal").activityId(getActivityId()).executionId(getExecutionId()).singleResult();
  }

  @Override
  protected Action<EventSubscriptionDelegate> action(ProcessScenario scenario) {
    return scenario.waitsAtSignalIntermediateCatchEvent(getActivityId());
  }

  @Override
  public void receive() {
    eventSubscriptionDelegate.receive();
  }

  @Override
  public void receive(Map<String, Object> variables) {
    eventSubscriptionDelegate.receive(variables);
  }

}
