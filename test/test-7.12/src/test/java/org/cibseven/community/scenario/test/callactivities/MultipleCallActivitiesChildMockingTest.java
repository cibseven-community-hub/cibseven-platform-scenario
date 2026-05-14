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
public class MultipleCallActivitiesChildMockingTest extends AbstractTest {

  @Test
  @Deployment(resources = {"org/camunda/bpm/scenario/test/callactivities/MultipleCallActivitiesTest.bpmn"})
  public void testCompleteCallActivities() {

    when(scenario.waitsAtMockedCallActivity("CallActivity1")).thenReturn(new MockedCallActivityAction() {
      @Override
      public void execute(MockedCallActivityDelegate callActivity) {
        callActivity.complete();
      }
    });

    when(scenario.waitsAtMockedCallActivity("CallActivity2")).thenReturn(new MockedCallActivityAction() {
      @Override
      public void execute(MockedCallActivityDelegate callActivity) {
        callActivity.complete();
      }
    });

    when(scenario.waitsAtMockedCallActivity("CallActivity3")).thenReturn(new MockedCallActivityAction() {
      @Override
      public void execute(MockedCallActivityDelegate callActivity) {
        callActivity.complete();
      }
    });

    Scenario.run(scenario)
      .withMockedProcess("Child1")
      .withMockedProcess("Child2")
      .startByKey("MultipleCallActivitiesTest")
      .execute();

    verify(scenario, times(1)).hasFinished("CallActivity1");
    verify(scenario, times(1)).hasFinished("CallActivity2");
    verify(scenario, times(1)).hasFinished("CallActivity3");
    verify(scenario, times(1)).hasFinished("EndEvent");

  }

}
