package org.cibseven.community.scenario.test.waitstates;

import org.cibseven.bpm.engine.test.Deployment;
import org.cibseven.community.scenario.Scenario;
import org.cibseven.community.scenario.act.BusinessRuleTaskAction;
import org.cibseven.community.scenario.delegate.ExternalTaskDelegate;
import org.cibseven.community.scenario.test.AbstractTest;
import org.junit.Test;

import static org.mockito.Mockito.*;

/**
 * @author Martin Schimak
 */
@Deployment(resources = {"org/camunda/bpm/scenario/test/waitstates/BusinessRuleTaskTest.bpmn"})
public class BusinessRuleTaskTest extends AbstractTest {

  @Test
  public void testCompleteTask() {

    when(scenario.waitsAtBusinessRuleTask("BusinessRuleTask")).thenReturn(new BusinessRuleTaskAction() {
      @Override
      public void execute(ExternalTaskDelegate externalTask) {
        externalTask.complete();
      }
    });

    Scenario.run(scenario).startByKey("BusinessRuleTaskTest").execute();

    verify(scenario, times(1)).hasCompleted("BusinessRuleTask");
    verify(scenario, times(1)).hasFinished("EndEvent");

  }

  @Test
  public void testDoNothing() {

    when(scenario.waitsAtBusinessRuleTask("BusinessRuleTask")).thenReturn(new BusinessRuleTaskAction() {
      @Override
      public void execute(ExternalTaskDelegate externalTask) {
        // Deal with externalTask but do nothing here
      }
    });

    Scenario.run(scenario).startByKey("BusinessRuleTaskTest").execute();

    verify(scenario, times(1)).hasStarted("BusinessRuleTask");
    verify(scenario, never()).hasFinished("BusinessRuleTask");
    verify(scenario, never()).hasFinished("EndEvent");

  }

  @Test(expected = AssertionError.class)
  public void testDoNotDealWithTask() {

    Scenario.run(scenario).startByKey("BusinessRuleTaskTest").execute();

  }

  @Test
  public void testWhileOtherProcessInstanceIsRunning() {

    when(scenario.waitsAtBusinessRuleTask("BusinessRuleTask")).thenReturn(new BusinessRuleTaskAction() {
      @Override
      public void execute(ExternalTaskDelegate externalTask) {
        externalTask.complete();
      }
    });

    when(otherScenario.waitsAtBusinessRuleTask("BusinessRuleTask")).thenReturn(new BusinessRuleTaskAction() {
      @Override
      public void execute(ExternalTaskDelegate externalTask) {
      }
    });

    Scenario.run(otherScenario).startByKey("BusinessRuleTaskTest").execute();
    Scenario.run(scenario).startByKey("BusinessRuleTaskTest").execute();

    verify(scenario, times(1)).hasCompleted("BusinessRuleTask");
    verify(scenario, times(1)).hasFinished("EndEvent");
    verify(otherScenario, never()).hasCompleted("BusinessRuleTask");

  }

}
