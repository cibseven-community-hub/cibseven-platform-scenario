package org.cibseven.community.scenario.impl.waitstate;

import org.cibseven.bpm.engine.history.HistoricActivityInstance;
import org.cibseven.bpm.engine.runtime.ProcessInstance;
import org.cibseven.community.scenario.ProcessScenario;
import org.cibseven.community.scenario.act.Action;
import org.cibseven.community.scenario.act.MockedCallActivityAction;
import org.cibseven.community.scenario.delegate.ProcessInstanceDelegate;
import org.cibseven.community.scenario.impl.MockedProcessRunnerImpl;
import org.cibseven.community.scenario.impl.ProcessRunnerImpl;
import org.cibseven.community.scenario.impl.delegate.AbstractProcessInstanceDelegate;

/**
 * @author Martin Schimak
 */
public class CallActivityExecutable extends AbstractProcessInstanceDelegate {

  public CallActivityExecutable(ProcessRunnerImpl runner, HistoricActivityInstance instance) {
    super(runner, instance);
  }

  @Override
  protected ProcessInstance getDelegate() {
    return getRuntimeService().createProcessInstanceQuery().processInstanceId(historicDelegate.getCalledProcessInstanceId()).singleResult();
  }

  @Override
  protected Action<ProcessInstanceDelegate> action(final ProcessScenario scenario) {
    final ProcessRunnerImpl mocked = (ProcessRunnerImpl) scenario.runsCallActivity(getActivityId());
    final MockedCallActivityAction action = scenario.waitsAtMockedCallActivity(getActivityId());
    final ProcessRunnerImpl runner = mocked != null ? mocked : (action != null ? new MockedProcessRunnerImpl(action) : null);
    if (runner != null) {
      return new Action<ProcessInstanceDelegate>() {
        @Override
        public void execute(ProcessInstanceDelegate processInstance) {
          runner.running((CallActivityExecutable) processInstance);
        }
      };
    }
    return null;
  }

  @Override
  public String getRootProcessInstanceId() {
    return getProcessInstance().getRootProcessInstanceId();
  }
}
