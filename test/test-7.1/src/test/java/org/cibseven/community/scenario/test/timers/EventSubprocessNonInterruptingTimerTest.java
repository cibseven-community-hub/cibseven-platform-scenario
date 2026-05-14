package org.cibseven.community.scenario.test.timers;

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
public class EventSubprocessNonInterruptingTimerTest extends AbstractTest {

  @Test
  @Deployment(resources = {"org/camunda/bpm/scenario/test/timers/EventSubprocessNonInterruptingTimerTest.bpmn"})
  public void testCompleteTask() {

    when(scenario.waitsAtUserTask("UserTask")).thenReturn(new UserTaskAction() {
      @Override
      public void execute(TaskDelegate task) {
        task.complete();
      }
    });

    Scenario.run(scenario).startByKey("EventSubprocessNonInterruptingTimerTest").execute();

    verify(scenario, times(1)).waitsAtUserTask("UserTask");
    verify(scenario, times(1)).hasStarted("UserTask");
    verify(scenario, times(1)).hasFinished("UserTask");
    verify(scenario, times(1)).hasFinished("EndEventCompleted");
    verify(scenario, never()).hasFinished("EndEventAdditional");

  }

  @Test
  @Deployment(resources = {"org/camunda/bpm/scenario/test/timers/EventSubprocessNonInterruptingTimerTest.bpmn"})
  public void testExactlyReachingMaximalTimeForTask() {

    when(scenario.waitsAtUserTask("UserTask")).thenReturn(new UserTaskAction() {
      @Override
      public void execute(final TaskDelegate task) {
        task.defer("PT5M", new Deferred() {
          @Override
          public void execute() {
            task.complete();
          }
        });
      }
    });

    Scenario.run(scenario).startByKey("EventSubprocessNonInterruptingTimerTest").execute();

    verify(scenario, times(1)).waitsAtUserTask("UserTask");
    verify(scenario, times(1)).hasStarted("UserTask");
    verify(scenario, times(1)).hasFinished("UserTask");
    verify(scenario, times(1)).hasFinished("EndEventCompleted");
    verify(scenario, never()).hasFinished("EndEventAdditional");

  }

  @Test
  @Deployment(resources = {"org/camunda/bpm/scenario/test/timers/EventSubprocessNonInterruptingTimerTest.bpmn"})
  public void testTakeMuchTooLongForTask() {

    when(scenario.waitsAtUserTask("UserTask")).thenReturn(new UserTaskAction() {
      @Override
      public void execute(final TaskDelegate task) {
        task.defer("PT6M", new Deferred() {
          @Override
          public void execute() {
            task.complete();
          }
        });
      }
    });

    Scenario.run(scenario).startByKey("EventSubprocessNonInterruptingTimerTest").execute();

    verify(scenario, times(1)).waitsAtUserTask("UserTask");
    verify(scenario, times(1)).hasStarted("UserTask");
    verify(scenario, times(1)).hasFinished("UserTask");
    verify(scenario, times(1)).hasFinished("EndEventCompleted");
    verify(scenario, times(1)).hasFinished("EndEventAdditional");

  }

  @Test
  @Deployment(resources = {"org/camunda/bpm/scenario/test/timers/EventSubprocessNonInterruptingTimerTest.bpmn"})
  public void testTakeABitTimeForTask() {

    when(scenario.waitsAtUserTask("UserTask")).thenReturn(new UserTaskAction() {
      @Override
      public void execute(final TaskDelegate task) {
        task.defer("PT4M", new Deferred() {
          @Override
          public void execute() {
            task.complete();
          }
        });
      }
    });

    Scenario.run(scenario).startByKey("EventSubprocessNonInterruptingTimerTest").execute();

    verify(scenario, times(1)).waitsAtUserTask("UserTask");
    verify(scenario, times(1)).hasStarted("UserTask");
    verify(scenario, times(1)).hasFinished("UserTask");
    verify(scenario, times(1)).hasFinished("EndEventCompleted");
    verify(scenario, never()).hasFinished("EndEventAdditional");

  }

  @Test
  @Deployment(resources = {"org/camunda/bpm/scenario/test/timers/EventSubprocessNonInterruptingTimerTest.bpmn"})
  public void testDoNothing() {

    when(scenario.waitsAtUserTask("UserTask")).thenReturn(new UserTaskAction() {
      @Override
      public void execute(TaskDelegate task) {
        // Deal with task but do nothing here
      }
    });

    Scenario.run(scenario).startByKey("EventSubprocessNonInterruptingTimerTest").execute();

    verify(scenario, times(1)).waitsAtUserTask("UserTask");
    verify(scenario, times(1)).hasStarted("UserTask");
    verify(scenario, never()).hasFinished("UserTask");
    verify(scenario, never()).hasFinished("EndEventCompleted");
    verify(scenario, times(1)).hasFinished("EndEventAdditional");

  }

  @Test(expected = AssertionError.class)
  @Deployment(resources = {"org/camunda/bpm/scenario/test/timers/EventSubprocessNonInterruptingTimerTest.bpmn"})
  public void testDoNotDealWithTask() {

    Scenario.run(scenario).startByKey("EventSubprocessNonInterruptingTimerTest").execute();

  }

  @Test
  @Deployment(resources = {"org/camunda/bpm/scenario/test/timers/EventSubprocessNonInterruptingTimerTest.bpmn"})
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

    Scenario.run(otherScenario).startByKey("EventSubprocessNonInterruptingTimerTest").execute();
    Scenario.run(scenario).startByKey("EventSubprocessNonInterruptingTimerTest").execute();

    verify(scenario, times(1)).waitsAtUserTask("UserTask");
    verify(scenario, times(1)).hasStarted("UserTask");
    verify(scenario, times(1)).hasFinished("UserTask");
    verify(scenario, times(1)).hasFinished("EndEventCompleted");
    verify(scenario, never()).hasFinished("EndEventAdditional");

  }

}
