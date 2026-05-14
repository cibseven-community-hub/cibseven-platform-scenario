package org.cibseven.community.scenario.impl.waitstate;


import org.cibseven.bpm.engine.history.HistoricActivityInstance;
import org.cibseven.community.scenario.ProcessScenario;
import org.cibseven.community.scenario.act.Action;
import org.cibseven.community.scenario.impl.ProcessRunnerImpl;

import java.util.Map;

/**
 * @author Martin Schimak
 */
public class SendTaskExecutable extends ServiceTaskExecutable {

  public SendTaskExecutable(ProcessRunnerImpl runner, HistoricActivityInstance instance) {
    super(runner, instance);
  }

  @Override
  protected Action action(ProcessScenario scenario) {
    return scenario.waitsAtSendTask(getActivityId());
  }

  @Override
  public void complete() {
    super.complete();
  }

  @Override
  public void complete(Map<String, Object> variables) {
    super.complete(variables);
  }

  @Override
  public void handleBpmnError(String errorCode) {
    super.handleBpmnError(errorCode);
  }

  @Override
  public void handleBpmnError(String errorCode, Map<String, Object> variables) {
    super.handleBpmnError(errorCode, variables);
  }

}
