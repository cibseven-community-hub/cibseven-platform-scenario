package org.cibseven.community.scenario.test.errors;

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
@Deployment(resources = {"org/camunda/bpm/scenario/test/errors/BoundaryErrorEventTest.bpmn"})
public class UserTaskBoundaryErrorEventTest extends AbstractTest {

  @Test
  public void testCompleteTask() {

    when(scenario.waitsAtUserTask("UserTask")).thenReturn(new UserTaskAction() {
      @Override
      public void execute(TaskDelegate task) {
        task.complete();
      }
    });

    Scenario.run(scenario).startByKey("BoundaryErrorEventTest").execute();

    verify(scenario, times(1)).hasFinished("EndEventCompleted");
    verify(scenario, never()).hasFinished("EndEventError");

  }

  @Test
  public void testHandleBpmnError() {

    when(scenario.waitsAtUserTask("UserTask")).thenReturn(new UserTaskAction() {
      @Override
      public void execute(TaskDelegate task) {
        task.handleBpmnError("errorCode");
      }
    });

    Scenario.run(scenario).startByKey("BoundaryErrorEventTest").execute();

    verify(scenario, never()).hasFinished("EndEventCompleted");
    verify(scenario, times(1)).hasFinished("EndEventError");

  }

}
