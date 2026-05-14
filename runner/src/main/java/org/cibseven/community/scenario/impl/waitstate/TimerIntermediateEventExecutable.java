package org.cibseven.community.scenario.impl.waitstate;


import org.cibseven.bpm.engine.history.HistoricActivityInstance;
import org.cibseven.bpm.engine.runtime.ProcessInstance;
import org.cibseven.community.scenario.ProcessScenario;
import org.cibseven.community.scenario.act.Action;
import org.cibseven.community.scenario.impl.ProcessRunnerImpl;
import org.cibseven.community.scenario.impl.delegate.AbstractProcessInstanceDelegate;
import org.cibseven.community.scenario.impl.util.Log;
import org.cibseven.community.scenario.impl.util.Time;

/**
 * @author Martin Schimak
 */
public class TimerIntermediateEventExecutable extends AbstractProcessInstanceDelegate {

  public TimerIntermediateEventExecutable(ProcessRunnerImpl runner, HistoricActivityInstance instance) {
    super(runner, instance);
  }

  @Override
  protected ProcessInstance getDelegate() {
    return getRuntimeService().createProcessInstanceQuery().processInstanceId(getProcessInstance().getId()).singleResult();
  }

  @Override
  protected Action action(ProcessScenario scenario) {
    return scenario.waitsAtTimerIntermediateEvent(getActivityId());
  }

  @SuppressWarnings("unchecked")
  public void execute() {
    Action action = action();
    Time.set(isExecutableAt());
    try {
      if (action != null) {
        Log.Action.ActingOn.log(
          historicDelegate.getActivityType(),
          historicDelegate.getActivityName(),
          historicDelegate.getActivityId(),
          runner.getProcessDefinitionKey(),
          historicDelegate.getProcessInstanceId(),
          null,
          null
        );
        action.execute(this);
      }
    } catch (Exception e) {
      throw e instanceof RuntimeException ? (RuntimeException) e : new RuntimeException(e);
    }
    runner.setExecuted(this);
  }

  @Override
  public String getRootProcessInstanceId() {
    return getProcessInstance().getRootProcessInstanceId();
  }
}
