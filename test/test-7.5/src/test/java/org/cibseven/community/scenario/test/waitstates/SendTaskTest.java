package org.cibseven.community.scenario.test.waitstates;

import org.cibseven.bpm.engine.test.Deployment;
import org.cibseven.community.scenario.Scenario;
import org.cibseven.community.scenario.act.SendTaskAction;
import org.cibseven.community.scenario.delegate.ExternalTaskDelegate;
import org.cibseven.community.scenario.test.AbstractTest;
import org.junit.Test;

import static org.mockito.Mockito.*;

/**
 * @author Martin Schimak
 */
@Deployment(resources = {"org/camunda/bpm/scenario/test/waitstates/SendTaskTest.bpmn"})
public class SendTaskTest extends AbstractTest {

  @Test
  public void testCompleteTask() {

    when(scenario.waitsAtSendTask("SendTask")).thenReturn(new SendTaskAction() {
      @Override
      public void execute(ExternalTaskDelegate externalTask) {
        externalTask.complete();
      }
    });

    Scenario.run(scenario).startByKey("SendTaskTest").execute();

    verify(scenario, times(1)).hasCompleted("SendTask");
    verify(scenario, times(1)).hasFinished("EndEvent");

  }

  @Test
  public void testDoNothing() {

    when(scenario.waitsAtSendTask("SendTask")).thenReturn(new SendTaskAction() {
      @Override
      public void execute(ExternalTaskDelegate externalTask) {
        // Deal with externalTask but do nothing here
      }
    });

    Scenario.run(scenario).startByKey("SendTaskTest").execute();

    verify(scenario, times(1)).hasStarted("SendTask");
    verify(scenario, never()).hasFinished("SendTask");
    verify(scenario, never()).hasFinished("EndEvent");

  }

  @Test(expected = AssertionError.class)
  public void testDoNotDealWithTask() {

    Scenario.run(scenario).startByKey("SendTaskTest").execute();

  }

  @Test
  public void testWhileOtherProcessInstanceIsRunning() {

    when(scenario.waitsAtSendTask("SendTask")).thenReturn(new SendTaskAction() {
      @Override
      public void execute(ExternalTaskDelegate externalTask) {
        externalTask.complete();
      }
    });

    when(otherScenario.waitsAtSendTask("SendTask")).thenReturn(new SendTaskAction() {
      @Override
      public void execute(ExternalTaskDelegate externalTask) {
      }
    });

    Scenario.run(otherScenario).startByKey("SendTaskTest").execute();
    Scenario.run(scenario).startByKey("SendTaskTest").execute();

    verify(scenario, times(1)).hasCompleted("SendTask");
    verify(scenario, times(1)).hasFinished("EndEvent");
    verify(otherScenario, never()).hasCompleted("SendTask");

  }

}
