package org.cibseven.community.scenario.test.escalations;

import org.cibseven.bpm.engine.test.Deployment;
import org.cibseven.community.scenario.Scenario;
import org.cibseven.community.scenario.act.UserTaskAction;
import org.cibseven.community.scenario.defer.Deferred;
import org.cibseven.community.scenario.delegate.TaskDelegate;
import org.cibseven.community.scenario.test.AbstractTest;
import org.junit.Test;

import static org.mockito.Mockito.*;

/**
 * @author Martin Schimak
 */
public class BoundaryEscalationEventTriggeredTwiceTest extends AbstractTest {

  @Test
  @Deployment(resources = {"org/camunda/bpm/scenario/test/escalations/BoundaryEscalationEventTriggeredTwiceTest.bpmn"})
  public void testCompleteTask1First() {

    when(scenario.waitsAtUserTask("UserTask1")).thenReturn(new UserTaskAction() {
      @Override
      public void execute(TaskDelegate task) throws Exception {
        task.complete();
      }
    });

    when(scenario.waitsAtUserTask("UserTask2")).thenReturn(new UserTaskAction() {
      @Override
      public void execute(final TaskDelegate task) throws Exception {
        task.defer("PT1M", new Deferred() {
          @Override
          public void execute() throws Exception {
            task.complete();
          }
        });
      }
    });

    Scenario.run(scenario).startByKey("BoundaryEscalationEventTriggeredTwiceTest").execute();

    verify(scenario, times(2)).hasFinished("EndEventEscalated");

  }

  @Test
  @Deployment(resources = {"org/camunda/bpm/scenario/test/escalations/BoundaryEscalationEventTriggeredTwiceTest.bpmn"})
  public void testCompleteTask2First() {

    when(scenario.waitsAtUserTask("UserTask1")).thenReturn(new UserTaskAction() {
      @Override
      public void execute(final TaskDelegate task) throws Exception {
        task.defer("PT1M", new Deferred() {
          @Override
          public void execute() throws Exception {
            task.complete();
          }
        });
      }
    });

    when(scenario.waitsAtUserTask("UserTask2")).thenReturn(new UserTaskAction() {
      @Override
      public void execute(TaskDelegate task) throws Exception {
        task.complete();
      }
    });

    Scenario.run(scenario).startByKey("BoundaryEscalationEventTriggeredTwiceTest").execute();

    verify(scenario, times(2)).hasFinished("EndEventEscalated");

  }

}
