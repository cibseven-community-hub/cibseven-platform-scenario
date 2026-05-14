package org.cibseven.community.scenario.test.waitstates;

import org.cibseven.bpm.engine.test.Deployment;
import org.cibseven.community.scenario.ProcessScenario;
import org.cibseven.community.scenario.Scenario;
import org.cibseven.community.scenario.act.UserTaskAction;
import org.cibseven.community.scenario.delegate.TaskDelegate;
import org.cibseven.community.scenario.test.AbstractTest;
import org.junit.Test;
import org.mockito.Mock;

import static org.mockito.Mockito.*;

/**
 * @author Martin Schimak
 */
public class CallActivityTest extends AbstractTest {

  @Mock
  protected ProcessScenario calledScenario;

  @Test
  @Deployment(resources = {
    "org/camunda/bpm/scenario/test/waitstates/CallActivityTest.bpmn",
    "org/camunda/bpm/scenario/test/waitstates/UserTaskTest.bpmn"
  })
  public void testCompleteCallActivityUserTask() {

    when(scenario.runsCallActivity("CallActivity")).thenReturn(Scenario.use(calledScenario));
    when(calledScenario.waitsAtUserTask("UserTask")).thenReturn(new UserTaskAction() {
      @Override
      public void execute(TaskDelegate task) {
        task.complete();
      }
    });

    Scenario.run(scenario).startByKey("CallActivityTest").execute();

    verify(calledScenario, times(1)).hasFinished("UserTask");

    verify(scenario, times(1)).hasFinished("EndEvent");
    verify(scenario, times(1)).hasFinished("CallActivity");

  }

  @Test
  @Deployment(resources = {
    "org/camunda/bpm/scenario/test/waitstates/CallActivityTest.bpmn",
    "org/camunda/bpm/scenario/test/waitstates/UserTaskTest.bpmn"
  })
  public void testDoNothingCallActivityUserTask() {

    when(scenario.runsCallActivity("CallActivity")).thenReturn(Scenario.use(calledScenario));
    when(calledScenario.waitsAtUserTask("UserTask")).thenReturn(new UserTaskAction() {
      @Override
      public void execute(TaskDelegate task) {
        // Deal with task but do nothing here
      }
    });

    Scenario.run(scenario).startByKey("CallActivityTest").execute();

    verify(scenario, times(1)).hasStarted("CallActivity");
    verify(scenario, never()).hasFinished("CallActivity");
    verify(scenario, never()).hasFinished("EndEvent");

    verify(calledScenario, times(1)).hasStarted("UserTask");
    verify(calledScenario, never()).hasFinished("UserTask");

  }

  @Test(expected = AssertionError.class)
  @Deployment(resources = {
    "org/camunda/bpm/scenario/test/waitstates/CallActivityTest.bpmn",
    "org/camunda/bpm/scenario/test/waitstates/UserTaskTest.bpmn"
  })
  public void testDoNotDealWithCallActivity() {

    Scenario.run(scenario).startByKey("CallActivityTest").execute();

  }

  @Test(expected = AssertionError.class)
  @Deployment(resources = {
    "org/camunda/bpm/scenario/test/waitstates/CallActivityTest.bpmn",
    "org/camunda/bpm/scenario/test/waitstates/UserTaskTest.bpmn"
  })
  public void testDoNotDealWithCallActivityUserTask() {

    when(scenario.runsCallActivity("CallActivity")).thenReturn(Scenario.use(calledScenario));

    Scenario.run(scenario).startByKey("CallActivityTest").execute();

  }

  @Test
  @Deployment(resources = {
    "org/camunda/bpm/scenario/test/waitstates/CallActivityTest.bpmn",
    "org/camunda/bpm/scenario/test/waitstates/UserTaskTest.bpmn"
  })
  public void testWhileOtherProcessInstanceIsRunning() {

    when(scenario.runsCallActivity("CallActivity")).thenReturn(Scenario.use(calledScenario));
    when(otherScenario.runsCallActivity("CallActivity")).thenReturn(Scenario.use(calledScenario));
    when(calledScenario.waitsAtUserTask("UserTask")).thenReturn(new UserTaskAction() {
      @Override
      public void execute(TaskDelegate task) {
      }
    }).thenReturn(new UserTaskAction() {
      @Override
      public void execute(TaskDelegate task) {
        task.complete();
      }
    });

    Scenario.run(otherScenario).startByKey("CallActivityTest").execute();
    Scenario.run(scenario).startByKey("CallActivityTest").execute();

    verify(scenario, times(1)).hasFinished("CallActivity");
    verify(scenario, times(1)).hasFinished("EndEvent");
    verify(otherScenario, never()).hasFinished("CallActivity");

  }

}
