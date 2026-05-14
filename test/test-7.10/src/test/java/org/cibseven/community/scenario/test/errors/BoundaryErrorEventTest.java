package org.cibseven.community.scenario.test.errors;

import org.assertj.core.api.Assertions;
import org.cibseven.bpm.engine.history.HistoricVariableInstance;
import org.cibseven.bpm.engine.runtime.ProcessInstance;
import org.cibseven.bpm.engine.test.Deployment;
import org.cibseven.community.scenario.Scenario;
import org.cibseven.community.scenario.act.ServiceTaskAction;
import org.cibseven.community.scenario.delegate.ExternalTaskDelegate;
import org.cibseven.community.scenario.test.AbstractTest;
import org.junit.Test;

import java.util.HashMap;
import java.util.Map;

import static org.cibseven.bpm.engine.test.assertions.ProcessEngineTests.processEngine;
import static org.mockito.Mockito.*;

/**
 * @author Martin Schimak
 */
@Deployment(resources = {"org/camunda/bpm/scenario/test/errors/BoundaryErrorEventTest.bpmn"})
public class BoundaryErrorEventTest extends AbstractTest {

  @Test
  public void testHandleBpmnErrorWithVariables() {

    when(scenario.waitsAtServiceTask("ServiceTask")).thenReturn(new ServiceTaskAction() {
      @Override
      public void execute(ExternalTaskDelegate externalTask) {
        Map<String, Object> variables = new HashMap<>();
        variables.put("key", "value");
        externalTask.handleBpmnError("errorCode", variables);
      }
    });

    ProcessInstance pi = Scenario
      .run(scenario)
      .startByKey("BoundaryErrorEventTest")
      .execute()
      .instance(scenario);

    verify(scenario, never()).hasFinished("EndEventCompleted");
    verify(scenario, times(1)).hasFinished("EndEventError");

    HistoricVariableInstance hvi = processEngine().getHistoryService().createHistoricVariableInstanceQuery()
      .processInstanceId(pi.getId())
      .variableName("key")
      .singleResult();
    Assertions.assertThat(hvi.getValue()).isEqualTo("value");

  }

}
