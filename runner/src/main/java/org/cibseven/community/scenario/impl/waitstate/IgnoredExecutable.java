package org.cibseven.community.scenario.impl.waitstate;

import org.cibseven.bpm.engine.history.HistoricActivityInstance;
import org.cibseven.community.scenario.ProcessScenario;
import org.cibseven.community.scenario.act.Action;
import org.cibseven.community.scenario.impl.ProcessRunnerImpl;
import org.cibseven.community.scenario.impl.WaitstateExecutable;

import java.util.Date;

/**
 * @author Martin Schimak
 */
public class IgnoredExecutable extends WaitstateExecutable<Object> {

  public IgnoredExecutable(ProcessRunnerImpl runner, HistoricActivityInstance instance) {
    super(runner, instance);
  }

  @Override
  @SuppressWarnings("unchecked")
  public void execute() {
    runner.setExecuted(this);
  }

  @Override
  protected Action<IgnoredExecutable> action(ProcessScenario scenario) {
    return null;
  }

  @Override
  protected Object getDelegate() {
    return null;
  }

  @Override
  public Date isExecutableAt() {
    return new Date(Long.MAX_VALUE);
  }

}
