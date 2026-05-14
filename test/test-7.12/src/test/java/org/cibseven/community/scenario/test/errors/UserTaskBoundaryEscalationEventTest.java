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
public class UserTaskBoundaryEscalationEventTest extends AbstractTest {

  @Test
  @Deployment(resources = {"org/camunda/bpm/scenario/test/escalations/UserTaskBoundaryEscalationEventTest.bpmn"})
  public void testCompleteTask() {

    when(scenario.waitsAtUserTask("UserTask")).thenReturn(new UserTaskAction() {
      @Override
      public void execute(TaskDelegate callActivity) {
        callActivity.complete();
      }
    });

    Scenario.run(scenario).startByKey("UserTaskBoundaryEscalationEventTest").execute();

    verify(scenario, times(1)).hasFinished("UserTask");
    verify(scenario, times(1)).hasFinished("EndEventCompleted");
    verify(scenario, never()).hasFinished("EndEventInterrupted");
    verify(scenario, never()).hasFinished("EndEventNotInterrupted");

  }

  @Test
  @Deployment(resources = {"org/camunda/bpm/scenario/test/escalations/UserTaskBoundaryEscalationEventTest.bpmn"})
  public void testEscalateNonInterrupting() {

    when(scenario.waitsAtUserTask("UserTask")).thenReturn(new UserTaskAction() {
      @Override
      public void execute(final TaskDelegate callActivity) {
        callActivity.handleEscalation("escNonInterrupting");
        callActivity.complete();
      }
    });

    Scenario.run(scenario).startByKey("UserTaskBoundaryEscalationEventTest").execute();

    verify(scenario, times(1)).hasFinished("UserTask");
    verify(scenario, times(1)).hasFinished("EndEventCompleted");
    verify(scenario, never()).hasFinished("EndEventInterrupted");
    verify(scenario, times(1)).hasFinished("EndEventNotInterrupted");

  }

  @Test
  @Deployment(resources = {"org/camunda/bpm/scenario/test/escalations/UserTaskBoundaryEscalationEventTest.bpmn"})
  public void testEscalateInterrupting() {

    when(scenario.waitsAtUserTask("UserTask")).thenReturn(new UserTaskAction() {
      @Override
      public void execute(final TaskDelegate callActivity) {
        callActivity.handleEscalation("escInterrupting");
      }
    });

    Scenario.run(scenario).startByKey("UserTaskBoundaryEscalationEventTest").execute();

    verify(scenario, times(1)).hasStarted("UserTask");
    verify(scenario, never()).hasCompleted("UserTask");
    verify(scenario, times(1)).hasCanceled("UserTask");

    verify(scenario, never()).hasFinished("EndEventCompleted");
    verify(scenario, times(1)).hasFinished("EndEventInterrupted");
    verify(scenario, never()).hasFinished("EndEventNotInterrupted");

  }

}
