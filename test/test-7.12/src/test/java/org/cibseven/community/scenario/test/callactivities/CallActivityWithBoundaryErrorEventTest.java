package org.cibseven.community.scenario.test.callactivities;

import org.cibseven.bpm.engine.test.Deployment;
import org.cibseven.community.scenario.Scenario;
import org.cibseven.community.scenario.act.MockedCallActivityAction;
import org.cibseven.community.scenario.delegate.MockedCallActivityDelegate;
import org.cibseven.community.scenario.test.AbstractTest;
import org.junit.Test;

import static org.mockito.Mockito.*;

/**
 * @author Martin Schimak
 */
@Deployment(resources = {"org/camunda/bpm/scenario/test/callactivities/CallActivityWithBoundaryErrorEventTest.bpmn"})
public class CallActivityWithBoundaryErrorEventTest extends AbstractTest {

  @Test
  public void testCompleteTask() {

    when(scenario.waitsAtMockedCallActivity("CallActivity")).thenReturn(new MockedCallActivityAction() {
      @Override
      public void execute(MockedCallActivityDelegate externalTask) {
        externalTask.complete();
      }
    });

    Scenario.run(scenario).withMockedProcess("Child").startByKey("BoundaryErrorEventTest").execute();

    verify(scenario, times(1)).hasFinished("EndEventCompleted");
    verify(scenario, never()).hasFinished("EndEventError");

  }

  @Test
  public void testHandleBpmnError() {

    when(scenario.waitsAtMockedCallActivity("CallActivity")).thenReturn(new MockedCallActivityAction() {
      @Override
      public void execute(MockedCallActivityDelegate externalTask) {
        externalTask.handleBpmnError("errorCode");
      }
    });

    Scenario.run(scenario).withMockedProcess("Child").startByKey("BoundaryErrorEventTest").execute();

    verify(scenario, never()).hasFinished("EndEventCompleted");
    verify(scenario, times(1)).hasFinished("EndEventError");

  }

}
