package org.cibseven.community.scenario.test.escalations;

import org.cibseven.bpm.engine.runtime.ProcessInstance;
import org.cibseven.bpm.engine.task.Task;
import org.cibseven.bpm.engine.test.Deployment;
import org.cibseven.community.scenario.test.AbstractTest;
import org.junit.Ignore;
import org.junit.Test;

import java.util.List;

import static org.cibseven.bpm.engine.test.assertions.ProcessEngineTests.*;

/**
 * @author Martin Schimak
 */
public class EscalationEventSubProcessTriggeredTwiceIntermediateConventionalTest extends AbstractTest {

  @Test
  @Ignore // In my mind should work, but does not work due to a Camunda NullpointerExecption
  @Deployment(resources = {"org/camunda/bpm/scenario/test/escalations/EscalationEventSubProcessTriggeredTwiceIntermediateTest.bpmn"})
  public void testCompleteTask1First_Conventional() {

    ProcessInstance pi = rule.getRuntimeService()
      .startProcessInstanceByKey("EscalationEventSubProcessTriggeredTwiceIntermediateTest");

    complete(task("UserTask1", pi)); // --> Test Case fails here with a NullPointerException

    List<Task> tasks = taskQuery().processInstanceId(pi.getId()).taskDefinitionKey("UserTask2").list();
    complete(tasks.get(0)); // necessary as query returns two tasks at this point

    complete(task("UserTask2", pi));
    complete(task("UserTask3", pi));

    assertThat(pi)
      .hasPassed("EndEvent")
      .hasPassedInOrder("EndEventEscalated", "EndEventEscalated");

  }

  @Test
  @Deployment(resources = {"org/camunda/bpm/scenario/test/escalations/EscalationEventSubProcessTriggeredTwiceIntermediateTest.bpmn"})
  public void testCompleteTask2First_Conventional() {

    ProcessInstance pi = rule.getRuntimeService()
      .startProcessInstanceByKey("EscalationEventSubProcessTriggeredTwiceIntermediateTest");

    complete(task("UserTask2", pi));
    complete(task("UserTask1", pi));

    complete(task("UserTask2", pi));
    complete(task("UserTask3", pi));

    assertThat(pi)
      .hasPassed("EndEvent")
      .hasPassedInOrder("EndEventEscalated", "EndEventEscalated");

  }

}
