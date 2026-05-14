package org.cibseven.community.scenario.test.processes;

import org.cibseven.bpm.engine.test.Deployment;
import org.cibseven.community.scenario.Scenario;
import org.cibseven.community.scenario.act.UserTaskAction;
import org.cibseven.community.scenario.delegate.TaskDelegate;
import org.cibseven.community.scenario.test.AbstractTest;
import org.junit.Test;

import static org.mockito.Mockito.*;

/**
 * @author Martin Schimak
 */
public class SubProcessTest extends AbstractTest {

  @Test
  @Deployment(resources = {"org/camunda/bpm/scenario/test/processes/SubProcessTest.bpmn"})
  public void testCompleteTask() {

    when(scenario.waitsAtUserTask("UserTask")).thenReturn(new UserTaskAction() {
      @Override
      public void execute(TaskDelegate task) {
        task.complete();
      }
    });

    Scenario.run(scenario).startByKey("SubProcessTest").execute();

    verify(scenario, times(1)).hasFinished("SubProcess");
    verify(scenario, times(1)).hasFinished("EndEvent");

  }

  @Test
  @Deployment(resources = {"org/camunda/bpm/scenario/test/processes/SubProcessTest.bpmn"})
  public void testDoNothing() {

    when(scenario.waitsAtUserTask("UserTask")).thenReturn(new UserTaskAction() {
      @Override
      public void execute(TaskDelegate task) {
        // Deal with task but do nothing here
      }
    });

    Scenario.run(scenario).startByKey("SubProcessTest").execute();

    verify(scenario, times(1)).hasStarted("SubProcess");
    verify(scenario, never()).hasFinished("SubProcess");
    verify(scenario, never()).hasFinished("EndEvent");

  }

  @Test(expected = AssertionError.class)
  @Deployment(resources = {"org/camunda/bpm/scenario/test/processes/SubProcessTest.bpmn"})
  public void testDoNotDealWithTask() {

    Scenario.run(scenario).startByKey("SubProcessTest").execute();

  }

  @Test
  @Deployment(resources = {"org/camunda/bpm/scenario/test/processes/SubProcessTest.bpmn"})
  public void testWhileOtherProcessInstanceIsRunning() {

    when(scenario.waitsAtUserTask("UserTask")).thenReturn(new UserTaskAction() {
      @Override
      public void execute(TaskDelegate task) {
        task.complete();
      }
    });

    when(otherScenario.waitsAtUserTask("UserTask")).thenReturn(new UserTaskAction() {
      @Override
      public void execute(TaskDelegate task) {
      }
    });

    Scenario
      .run(scenario).startByKey("SubProcessTest")
      .run(otherScenario).startByKey("SubProcessTest")
      .execute();

    verify(scenario, times(1)).hasFinished("SubProcess");
    verify(scenario, times(1)).hasFinished("EndEvent");
    verify(otherScenario, times(1)).hasStarted("SubProcess");
    verify(otherScenario, never()).hasFinished("SubProcess");

  }

}
