package org.cibseven.community.scenario.impl.job;

import org.cibseven.bpm.engine.runtime.Job;
import org.cibseven.community.scenario.impl.JobExecutable;
import org.cibseven.community.scenario.impl.ProcessRunnerImpl;
import org.cibseven.community.scenario.impl.util.Time;

import java.util.Date;

/**
 * @author Martin Schimak
 */
public class TimerJobExecutable extends JobExecutable {

  public TimerJobExecutable(ProcessRunnerImpl runner, Job job) {
    super(runner, job);
  }

  @Override
  public Date isExecutableAt() {
    return Time.correct(delegate.getDuedate());
  }

  @Override
  public void executeJob() {
    Time.set(isExecutableAt());
    super.executeJob();
  }

}
