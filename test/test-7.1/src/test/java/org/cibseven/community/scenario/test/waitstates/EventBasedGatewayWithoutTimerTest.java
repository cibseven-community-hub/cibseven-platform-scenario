package org.cibseven.community.scenario.test.waitstates;

import org.cibseven.bpm.engine.test.Deployment;
import org.cibseven.community.scenario.Scenario;
import org.cibseven.community.scenario.act.EventBasedGatewayAction;
import org.cibseven.community.scenario.delegate.EventBasedGatewayDelegate;
import org.cibseven.community.scenario.test.AbstractTest;
import org.junit.Test;

import static org.mockito.Mockito.*;

/**
 * @author Martin Schimak
 */
public class EventBasedGatewayWithoutTimerTest extends AbstractTest {

  @Test
  @Deployment(resources = {"org/camunda/bpm/scenario/test/waitstates/EventBasedGatewayWithoutTimerTest.bpmn"})
  public void testReceiveMessage() {

    when(scenario.waitsAtEventBasedGateway("EventBasedGateway")).thenReturn(new EventBasedGatewayAction() {
      @Override
      public void execute(EventBasedGatewayDelegate gateway) {
        gateway.getEventSubscription("MessageIntermediateCatchEvent").receive();
      }
    });

    Scenario.run(scenario).startByKey("EventBasedGatewayWithoutTimerTest").execute();

    verify(scenario, times(1)).hasFinished("EventBasedGateway");
    verify(scenario, times(1)).hasFinished("MessageIntermediateCatchEvent");
    verify(scenario, times(1)).hasFinished("EndEvent");

  }

  @Test
  @Deployment(resources = {"org/camunda/bpm/scenario/test/waitstates/EventBasedGatewayWithoutTimerTest.bpmn"})
  public void testDoNothing() {

    when(scenario.waitsAtEventBasedGateway("EventBasedGateway")).thenReturn(new EventBasedGatewayAction() {
      @Override
      public void execute(EventBasedGatewayDelegate gateway) {
        // Do nothing means process remains here because of no timers
      }
    });

    Scenario.run(scenario).startByKey("EventBasedGatewayWithoutTimerTest").execute();

    verify(scenario, times(1)).hasStarted("EventBasedGateway");
    verify(scenario, never()).hasFinished("EventBasedGateway");
    verify(scenario, never()).hasFinished("EndEvent");

  }

  @Test(expected = AssertionError.class)
  @Deployment(resources = {"org/camunda/bpm/scenario/test/waitstates/EventBasedGatewayWithoutTimerTest.bpmn"})
  public void testDoNotDealWithEventBasedGateway() {

    Scenario.run(scenario).startByKey("EventBasedGatewayWithoutTimerTest").execute();

  }

  @Test
  @Deployment(resources = {"org/camunda/bpm/scenario/test/waitstates/EventBasedGatewayWithoutTimerTest.bpmn"})
  public void testWhileOtherProcessInstanceIsRunning() {

    when(scenario.waitsAtEventBasedGateway("EventBasedGateway")).thenReturn(new EventBasedGatewayAction() {
      @Override
      public void execute(EventBasedGatewayDelegate gateway) {
        gateway.getEventSubscription("MessageIntermediateCatchEvent").receive();
      }
    });

    when(otherScenario.waitsAtEventBasedGateway("EventBasedGateway")).thenReturn(new EventBasedGatewayAction() {
      @Override
      public void execute(EventBasedGatewayDelegate gateway) {
      }
    });

    Scenario.run(otherScenario).startByKey("EventBasedGatewayWithoutTimerTest").execute();
    Scenario.run(scenario).startByKey("EventBasedGatewayWithoutTimerTest").execute();

    verify(scenario, times(1)).hasFinished("EventBasedGateway");
    verify(scenario, times(1)).hasFinished("EndEvent");
    verify(otherScenario, never()).hasFinished("EventBasedGateway");

  }

}
