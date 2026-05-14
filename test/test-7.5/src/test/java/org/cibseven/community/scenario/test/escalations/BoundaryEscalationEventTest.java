package org.cibseven.community.scenario.test.escalations;

import org.cibseven.bpm.engine.test.Deployment;
import org.cibseven.community.scenario.Scenario;
import org.cibseven.community.scenario.act.UserTaskAction;
import org.cibseven.community.scenario.delegate.TaskDelegate;
import org.cibseven.community.scenario.test.AbstractTest;
import org.junit.Test;

import java.util.Map;

import static org.cibseven.bpm.engine.test.assertions.ProcessEngineTests.withVariables;
import static org.mockito.Mockito.*;

/**
 * @author Martin Schimak
 */
public class BoundaryEscalationEventTest extends AbstractTest {

  void complete(final Map<String, Object> variables) {
    when(scenario.waitsAtUserTask("UserTask")).thenReturn(new UserTaskAction() {
      @Override
      public void execute(TaskDelegate task) {
        task.complete(variables);
      }
    });
  }

  @Test
  @Deployment(resources = {"org/camunda/bpm/scenario/test/escalations/BoundaryEscalationEventTest.bpmn"})
  public void testCompleteTask() {

    complete(withVariables("escalate", false));

    Scenario.run(scenario).startByKey("BoundaryEscalationEventTest").execute();

    verify(scenario, times(1)).hasFinished("UserTask");
    verify(scenario, times(1)).hasFinished("EndEventCompleted");
    verify(scenario, never()).hasFinished("EndEventInterrupted");
    verify(scenario, never()).hasFinished("EndEventNotInterrupted");

  }

  @Test
  @Deployment(resources = {"org/camunda/bpm/scenario/test/escalations/BoundaryEscalationEventTest.bpmn"})
  public void testEscalateNonInterrupting() {

    complete(withVariables("escalate", true, "interrupt", false));

    Scenario.run(scenario).startByKey("BoundaryEscalationEventTest").execute();

    verify(scenario, times(1)).hasFinished("UserTask");
    verify(scenario, times(1)).hasFinished("EndEventCompleted");
    verify(scenario, never()).hasFinished("EndEventInterrupted");
    verify(scenario, times(1)).hasFinished("EndEventNotInterrupted");

  }

  @Test
  @Deployment(resources = {"org/camunda/bpm/scenario/test/escalations/BoundaryEscalationEventTest.bpmn"})
  public void testEscalateInterrupting() {

    complete(withVariables("escalate", true, "interrupt", true));

    Scenario.run(scenario).startByKey("BoundaryEscalationEventTest").execute();

    verify(scenario, times(1)).hasFinished("UserTask");
    verify(scenario, never()).hasFinished("EndEventCompleted");
    verify(scenario, times(1)).hasFinished("EndEventInterrupted");
    verify(scenario, never()).hasFinished("EndEventNotInterrupted");

  }

}
