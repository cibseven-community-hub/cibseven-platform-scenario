package org.cibseven.community.scenario.test.variables;

import org.cibseven.bpm.engine.test.Deployment;
import org.cibseven.community.scenario.Scenario;
import org.cibseven.community.scenario.act.ReceiveTaskAction;
import org.cibseven.community.scenario.act.UserTaskAction;
import org.cibseven.community.scenario.delegate.EventSubscriptionDelegate;
import org.cibseven.community.scenario.delegate.TaskDelegate;
import org.cibseven.community.scenario.test.AbstractTest;
import org.junit.Test;

import static org.mockito.Mockito.*;
import static org.assertj.core.api.Assertions.assertThat;
import static org.cibseven.bpm.engine.test.assertions.ProcessEngineTests.*;

/**
 * @author Martin Schimak
 */
public class VariablesTest extends AbstractTest {

  @Test
  @Deployment(resources = {"org/camunda/bpm/scenario/test/variables/VariablesTest.bpmn"})
  public void testNoVariables() {

    when(scenario.waitsAtUserTask("UserTask")).thenReturn(new UserTaskAction() {
      @Override
      public void execute(TaskDelegate task) {
        task.complete();
      }
    });

    when(scenario.waitsAtReceiveTask("Waitstate")).thenReturn(new ReceiveTaskAction() {
      @Override
      public void execute(EventSubscriptionDelegate delegate) {
        assertThat(delegate.getVariables())
          .hasSize(0);
      }
    });

    Scenario.run(scenario).startByKey("VariablesTest").execute();

    verify(scenario, times(1)).hasFinished("SubProcess");
    verify(scenario, never()).hasFinished("EndEvent");

  }

  @Test
  @Deployment(resources = {"org/camunda/bpm/scenario/test/variables/VariablesTest.bpmn"})
  public void testVariableSetAtInstanceLevelSuccess() {

    variables.put("globalVariable", "globalVariableValue");

    when(scenario.waitsAtUserTask("UserTask")).thenReturn(new UserTaskAction() {
      @Override
      public void execute(TaskDelegate task) {
        assertThat(task.getVariables()).containsEntry("globalVariable", "globalVariableValue");
        task.complete(withVariables("locallySetGlobalVariable", "locallySetGlobalVariableValue"));
      }
    });

    when(scenario.waitsAtReceiveTask("Waitstate")).thenReturn(new ReceiveTaskAction() {
      @Override
      public void execute(EventSubscriptionDelegate delegate) {
        assertThat(delegate.getVariables())
          .hasSize(2)
          .containsEntry("globalVariable", "globalVariableValue")
          .containsEntry("locallySetGlobalVariable", "locallySetGlobalVariableValue");
      }
    });

    Scenario.run(scenario).startByKey("VariablesTest", variables).execute();

    verify(scenario, times(1)).hasFinished("SubProcess");
    verify(scenario, never()).hasFinished("EndEvent");

  }

  @Test
  @Deployment(resources = {"org/camunda/bpm/scenario/test/variables/VariablesTest.bpmn"})
  public void testVariableSetAtInstanceLevelFailure() {

    variables.put("globalVariable", "globalVariableValue");

    when(scenario.waitsAtUserTask("UserTask")).thenReturn(new UserTaskAction() {
      @Override
      public void execute(TaskDelegate task) {
        assertThat(task.getVariables()).hasSize(1)
            .containsEntry("globalVariable", "globalVariableValue");
        task.complete(withVariables("locallySetGlobalVariable", "locallySetGlobalVariableValue"));
      }
    });

    when(scenario.waitsAtReceiveTask("Waitstate")).thenReturn(new ReceiveTaskAction() {
      @Override
      public void execute(EventSubscriptionDelegate delegate) {
        assertThat(delegate.getVariables())
          .hasSize(2)
          .containsEntry("globalVariable", "globalVariableValue")
          .containsEntry("locallySetGlobalVariable", "locallySetGlobalVariableValue");
      }
    });

    Scenario.run(scenario).startByKey("VariablesTest", variables).execute();

  }

}
