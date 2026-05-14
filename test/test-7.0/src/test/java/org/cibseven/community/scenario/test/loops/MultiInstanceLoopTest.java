package org.cibseven.community.scenario.test.loops;

import org.cibseven.bpm.engine.test.Deployment;
import org.cibseven.community.scenario.Scenario;
import org.cibseven.community.scenario.act.UserTaskAction;
import org.cibseven.community.scenario.delegate.TaskDelegate;
import org.cibseven.community.scenario.test.AbstractTest;
import org.junit.Before;
import org.junit.Test;

import static org.mockito.Mockito.*;

/**
 * @author Martin Schimak
 */
public class MultiInstanceLoopTest extends AbstractTest {

  @Before
  public void setVariable() {
    variables.put("cardinality", 1);
  }

  @Test
  @Deployment(resources = {"org/camunda/bpm/scenario/test/loops/MultiInstanceLoopTest.bpmn"})
  public void testDoNotLoop() {

    when(scenario.waitsAtUserTask("UserTask")).thenReturn(new UserTaskAction() {
      @Override
      public void execute(TaskDelegate task) {
        task.complete();
      }
    });

    Scenario.run(scenario).startByKey("MultiInstanceLoopTest", variables).execute();

    verify(scenario, times(1)).hasFinished("UserTask");
    verify(scenario, times(1)).hasFinished("EndEvent");

  }

  @Test
  @Deployment(resources = {"org/camunda/bpm/scenario/test/loops/MultiInstanceLoopTest.bpmn"})
  public void testDoLoopASingleTime() {

    when(scenario.waitsAtUserTask("UserTask")).thenReturn(new UserTaskAction() {
      @Override
      public void execute(TaskDelegate task) {
        task.complete();
      }
    });

    variables.put("cardinality", 2);

    Scenario.run(scenario).startByKey("MultiInstanceLoopTest", variables).execute();

    verify(scenario, times(2)).hasFinished("UserTask");
    verify(scenario, times(1)).hasFinished("EndEvent");

  }

  @Test
  @Deployment(resources = {"org/camunda/bpm/scenario/test/loops/MultiInstanceLoopTest.bpmn"})
  public void testDoTaskTenTimes() {

    when(scenario.waitsAtUserTask("UserTask")).thenReturn(new UserTaskAction() {
      @Override
      public void execute(TaskDelegate task) {
        task.complete();
      }
    });

    variables.put("cardinality", 10);

    Scenario.run(scenario).startByKey("MultiInstanceLoopTest", variables).execute();

    verify(scenario, times(10)).hasFinished("UserTask");
    verify(scenario, times(1)).hasFinished("EndEvent");

  }

}
