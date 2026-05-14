package org.cibseven.community.scenario.test.waitstates;

import org.cibseven.bpm.engine.test.Deployment;
import org.cibseven.community.scenario.Scenario;
import org.cibseven.community.scenario.act.MessageEndEventAction;
import org.cibseven.community.scenario.delegate.ExternalTaskDelegate;
import org.cibseven.community.scenario.test.AbstractTest;
import org.junit.Test;

import static org.mockito.Mockito.*;

/**
 * @author Martin Schimak
 */
@Deployment(resources = {"org/camunda/bpm/scenario/test/waitstates/MessageEndEventTest.bpmn"})
public class MessageEndEventTest extends AbstractTest {

  @Test
  public void testCompleteTask() {

    when(scenario.waitsAtMessageEndEvent("MessageEndEvent")).thenReturn(new MessageEndEventAction() {
      @Override
      public void execute(ExternalTaskDelegate externalTask) {
        externalTask.complete();
      }
    });

    Scenario.run(scenario).startByKey("MessageEndEventTest").execute();

    verify(scenario, times(1)).hasCompleted("MessageEndEvent");

  }

  @Test
  public void testDoNothing() {

    when(scenario.waitsAtMessageEndEvent("MessageEndEvent")).thenReturn(new MessageEndEventAction() {
      @Override
      public void execute(ExternalTaskDelegate externalTask) {
        // Deal with externalTask but do nothing here
      }
    });

    Scenario.run(scenario).startByKey("MessageEndEventTest").execute();

    verify(scenario, times(1)).hasStarted("MessageEndEvent");
    verify(scenario, never()).hasFinished("MessageEndEvent");
    verify(scenario, never()).hasFinished("EndEvent");

  }

  @Test(expected = AssertionError.class)
  public void testDoNotDealWithTask() {

    Scenario.run(scenario).startByKey("MessageEndEventTest").execute();

  }

  @Test
  public void testWhileOtherProcessInstanceIsRunning() {

    when(scenario.waitsAtMessageEndEvent("MessageEndEvent")).thenReturn(new MessageEndEventAction() {
      @Override
      public void execute(ExternalTaskDelegate externalTask) {
        externalTask.complete();
      }
    });

    when(otherScenario.waitsAtMessageEndEvent("MessageEndEvent")).thenReturn(new MessageEndEventAction() {
      @Override
      public void execute(ExternalTaskDelegate externalTask) {
      }
    });

    Scenario.run(otherScenario).startByKey("MessageEndEventTest").execute();
    Scenario.run(scenario).startByKey("MessageEndEventTest").execute();

    verify(scenario, times(1)).hasCompleted("MessageEndEvent");
    verify(otherScenario, never()).hasCompleted("MessageEndEvent");

  }

}
