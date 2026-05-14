package org.cibseven.community.scenario.test.callactivities;

import org.cibseven.bpm.engine.test.Deployment;
import org.cibseven.community.scenario.ProcessScenario;
import org.cibseven.community.scenario.Scenario;
import org.cibseven.community.scenario.test.AbstractTest;
import org.junit.Test;
import org.mockito.Mock;

import static org.mockito.Mockito.*;

/**
 * @author Martin Schimak
 */
public class CallActivityChildNonBlockingTest extends AbstractTest {

  @Mock
  ProcessScenario childScenario;

  @Mock
  ProcessScenario otherChildScenario;

  @Test
  @Deployment(resources = {
    "org/camunda/bpm/scenario/test/callactivities/CallActivityTest.bpmn",
    "org/camunda/bpm/scenario/test/callactivities/CallActivityChildNonBlockingTest.bpmn"
  })
  public void testCompleteCallActivity() {

    when(scenario.runsCallActivity("CallActivity")).thenReturn(Scenario.use(childScenario));

    Scenario.run(scenario).startByKey("CallActivityTest").execute();

    verify(scenario, times(1)).hasFinished("CallActivity");
    verify(scenario, times(1)).hasFinished("EndEvent");

  }

  @Test
  @Deployment(resources = {
    "org/camunda/bpm/scenario/test/callactivities/CallActivityTest.bpmn",
    "org/camunda/bpm/scenario/test/callactivities/CallActivityChildNonBlockingTest.bpmn"
  })
  public void testDoNothing() {

    when(scenario.runsCallActivity("CallActivity")).thenReturn(Scenario.use(childScenario));

    Scenario.run(scenario).startByKey("CallActivityTest").execute();

    verify(scenario, times(1)).hasStarted("CallActivity");
    verify(scenario, times(1)).hasFinished("CallActivity");
    verify(scenario, times(1)).hasFinished("EndEvent");

  }

  @Test
  @Deployment(resources = {
    "org/camunda/bpm/scenario/test/callactivities/CallActivityTest.bpmn",
    "org/camunda/bpm/scenario/test/callactivities/CallActivityChildNonBlockingTest.bpmn"
  })
  public void testDoNotDealWithCallActivity() {

    Scenario.run(scenario).startByKey("CallActivityTest").execute();

    verify(scenario, times(1)).hasStarted("CallActivity");
    verify(scenario, times(1)).hasFinished("CallActivity");
    verify(scenario, times(1)).hasFinished("EndEvent");

  }

  @Test
  @Deployment(resources = {
    "org/camunda/bpm/scenario/test/callactivities/CallActivityTest.bpmn",
    "org/camunda/bpm/scenario/test/callactivities/CallActivityChildNonBlockingTest.bpmn"
  })
  public void testWhileOtherProcessInstanceIsRunning() {

    when(scenario.runsCallActivity("CallActivity"))
      .thenReturn(Scenario.use(childScenario));
    when(otherScenario.runsCallActivity("CallActivity"))
      .thenReturn(Scenario.use(otherChildScenario));

    Scenario
      .run(scenario).startByKey("CallActivityTest")
      .run(otherScenario).startByKey("CallActivityTest")
      .execute();

    verify(scenario, times(1)).hasFinished("CallActivity");
    verify(scenario, times(1)).hasFinished("EndEvent");
    verify(otherScenario, times(1)).hasStarted("CallActivity");
    verify(otherScenario, times(1)).hasFinished("CallActivity");
    verify(otherScenario, times(1)).hasFinished("EndEvent");

  }

}
