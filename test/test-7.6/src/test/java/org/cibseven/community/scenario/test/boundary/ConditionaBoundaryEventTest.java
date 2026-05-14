package org.cibseven.community.scenario.test.boundary;

import org.cibseven.bpm.engine.test.Deployment;
import org.cibseven.community.scenario.Scenario;
import org.cibseven.community.scenario.act.UserTaskAction;
import org.cibseven.community.scenario.defer.Deferred;
import org.cibseven.community.scenario.delegate.TaskDelegate;
import org.cibseven.community.scenario.test.AbstractTest;
import org.junit.Before;
import org.junit.Test;

import static org.mockito.Mockito.*;

/**
 * @author Martin Schimak
 */
@Deployment(resources = {"org/camunda/bpm/scenario/test/boundary/ConditionalBoundaryEventTest.bpmn"})
public class ConditionaBoundaryEventTest extends AbstractTest {

  @Before
  public void setCondition() {
    variables.put("condition", false);
  }

  @Test
  @Deployment(resources = {"org/camunda/bpm/scenario/test/boundary/ConditionalBoundaryEventTest.bpmn"})
  public void testConditionInitiallyFalseAndDoNothing() {

    when(scenario.waitsAtUserTask("UserTask")).thenReturn(new UserTaskAction() {
      @Override
      public void execute(TaskDelegate task) {
        // Deal with processInstance but do nothing here
      }
    });

    Scenario.run(scenario)
      .startByKey("ConditionalBoundaryEventTest", variables)
      .execute();

    verify(scenario, times(1)).hasStarted("UserTask");
    verify(scenario, never()).hasFinished("UserTask");
    verify(scenario, never()).hasFinished("ConditionalBoundaryEvent");
    verify(scenario, never()).hasFinished("EndEventAfterUserTask");
    verify(scenario, never()).hasFinished("EndEventAfterBoundaryEvent");

  }

  @Test
  @Deployment(resources = {"org/camunda/bpm/scenario/test/boundary/ConditionalBoundaryEventTest.bpmn"})
  public void testConditionInitiallyFalseAndSetTrue() {

    when(scenario.waitsAtUserTask("UserTask")).thenReturn(new UserTaskAction() {
      @Override
      public void execute(TaskDelegate task) {
        rule.getRuntimeService().setVariable(task.getProcessInstance().getId(), "condition", true);
      }
    });

    Scenario.run(scenario)
      .startByKey("ConditionalBoundaryEventTest", variables)
      .execute();

    verify(scenario, times(1)).hasStarted("UserTask");
    verify(scenario, times(1)).hasFinished("UserTask");
    verify(scenario, times(1)).hasFinished("ConditionalBoundaryEvent");
    verify(scenario, never()).hasFinished("EndEventAfterUserTask");
    verify(scenario, times(1)).hasFinished("EndEventAfterBoundaryEvent");

  }

  @Test
  @Deployment(resources = {"org/camunda/bpm/scenario/test/boundary/ConditionalBoundaryEventTest.bpmn"})
  public void testConditionInitiallyFalseAndCompleteTask() {

    when(scenario.waitsAtUserTask("UserTask")).thenReturn(new UserTaskAction() {
      @Override
      public void execute(TaskDelegate task) {
        task.complete();
      }
    });

    Scenario.run(scenario).startByKey("ConditionalBoundaryEventTest", variables).execute();

    verify(scenario, times(1)).hasStarted("UserTask");
    verify(scenario, times(1)).hasFinished("UserTask");
    verify(scenario, never()).hasFinished("ConditionalBoundaryEvent");
    verify(scenario, times(1)).hasFinished("EndEventAfterUserTask");
    verify(scenario, never()).hasFinished("EndEventAfterBoundaryEvent");

  }

  @Test
  @Deployment(resources = {"org/camunda/bpm/scenario/test/boundary/ConditionalBoundaryEventTest.bpmn"})
  public void testConditionInitiallyTrueAndCompleteTask() {

    when(scenario.waitsAtUserTask("UserTask")).thenReturn(new UserTaskAction() {
      @Override
      public void execute(TaskDelegate task) {
        task.complete();
      }
    });

    variables.put("condition", true);

    Scenario.run(scenario).startByKey("ConditionalBoundaryEventTest", variables).execute();

    verify(scenario, times(1)).hasStarted("UserTask");
    verify(scenario, times(1)).hasFinished("ConditionalBoundaryEvent");
    verify(scenario, never()).hasFinished("EndEventAfterUserTask");
    verify(scenario, times(1)).hasFinished("EndEventAfterBoundaryEvent");
    verify(scenario, times(1)).hasFinished("UserTask");
    verify(scenario, times(1)).hasCanceled("UserTask");
    verify(scenario, never()).hasCompleted("UserTask");

  }

  @Test(expected = AssertionError.class)
  @Deployment(resources = {"org/camunda/bpm/scenario/test/boundary/ConditionalBoundaryEventTest.bpmn"})
  public void testConditionInitiallyFalseAndDoNotDealWithTask() {

    Scenario.run(scenario).startByKey("ConditionalBoundaryEventTest", variables).execute();

  }

  @Test
  @Deployment(resources = {"org/camunda/bpm/scenario/test/boundary/ConditionalBoundaryEventTest.bpmn"})
  public void testConditionInitiallyTrueAndDoNotDealWithTask() {

    variables.put("condition", true);

  }

  @Test
  @Deployment(resources = {"org/camunda/bpm/scenario/test/boundary/ConditionalBoundaryEventTest.bpmn"})
  public void testConditionInitiallyTrueAndDoNothing() {

    when(scenario.waitsAtUserTask("UserTask")).thenReturn(new UserTaskAction() {
      @Override
      public void execute(TaskDelegate processInstance) {
        // Deal with processInstance but do nothing here
      }
    });

    variables.put("condition", true);

    Scenario.run(scenario)
      .startByKey("ConditionalBoundaryEventTest", variables)
      .execute();

    verify(scenario, times(1)).hasStarted("UserTask");
    verify(scenario, times(1)).hasFinished("UserTask");
    verify(scenario, times(1)).hasFinished("ConditionalBoundaryEvent");
    verify(scenario, never()).hasFinished("EndEventAfterUserTask");
    verify(scenario, times(1)).hasFinished("EndEventAfterBoundaryEvent");

  }

  @Test(expected = Exception.class)
  @Deployment(resources = {"org/camunda/bpm/scenario/test/boundary/ConditionalBoundaryEventTest.bpmn"})
  public void testDoSomethingDeferred() {

    when(scenario.waitsAtUserTask("UserTask")).thenReturn(new UserTaskAction() {
      @Override
      public void execute(final TaskDelegate task) {
        task.defer("PT3M", new Deferred() {
          @Override
          public void execute() throws Exception {
            throw new Exception(); // expected
          }
        });
      }
    });

    Scenario.run(scenario).startByKey("ConditionalBoundaryEventTest").execute();

  }

}
