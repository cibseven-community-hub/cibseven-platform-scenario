package org.cibseven.community.scenario.impl.job;

import org.cibseven.bpm.engine.runtime.Job;
import org.cibseven.community.scenario.impl.JobExecutable;
import org.cibseven.community.scenario.impl.ProcessRunnerImpl;

import java.util.Date;

/**
 * @author Martin Schimak
 */
public class ContinuationExecutable extends JobExecutable {

  public ContinuationExecutable(ProcessRunnerImpl runner, Job job) {
    super(runner, job);
  }

  @Override
  public Date isExecutableAt() {
    return new Date(0);
  }

}
