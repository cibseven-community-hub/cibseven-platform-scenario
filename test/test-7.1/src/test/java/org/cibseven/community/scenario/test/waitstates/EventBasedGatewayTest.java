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
public class EventBasedGatewayTest extends AbstractTest {

  @Test
  @Deployment(resources = {"org/camunda/bpm/scenario/test/waitstates/EventBasedGatewayTest.bpmn"})
  public void testReceiveMessage() {

    when(scenario.waitsAtEventBasedGateway("EventBasedGateway")).thenReturn(new EventBasedGatewayAction() {
      @Override
      public void execute(EventBasedGatewayDelegate gateway) {
        gateway.getEventSubscription("MessageIntermediateCatchEvent").receive();
      }
    });

    Scenario.run(scenario).startByKey("EventBasedGatewayTest").execute();

    verify(scenario, times(1)).hasFinished("EventBasedGateway");
    verify(scenario, times(1)).hasFinished("MessageIntermediateCatchEvent");
    verify(scenario, times(1)).hasFinished("EndEvent");

  }

  @Test
  @Deployment(resources = {"org/camunda/bpm/scenario/test/waitstates/EventBasedGatewayTest.bpmn"})
  public void testDoNothing() {

    when(scenario.waitsAtEventBasedGateway("EventBasedGateway")).thenReturn(new EventBasedGatewayAction() {
      @Override
      public void execute(EventBasedGatewayDelegate gateway) {
        // Do nothing means process moves forward because of the timers
      }
    });

    Scenario.run(scenario).startByKey("EventBasedGatewayTest").execute();

    verify(scenario, times(1)).hasFinished("TimerIntermediateEvent");
    verify(scenario, times(1)).hasFinished("EndEvent");

  }

  @Test(expected = AssertionError.class)
  @Deployment(resources = {"org/camunda/bpm/scenario/test/waitstates/EventBasedGatewayTest.bpmn"})
  public void testDoNotDealWithEventBasedGateway() {

    Scenario.run(scenario).startByKey("EventBasedGatewayTest").execute();

  }

}
