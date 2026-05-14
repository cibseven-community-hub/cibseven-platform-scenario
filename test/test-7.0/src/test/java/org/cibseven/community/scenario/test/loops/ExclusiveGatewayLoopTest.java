package org.cibseven.community.scenario.test.loops;

import org.cibseven.bpm.engine.test.Deployment;
import org.cibseven.community.scenario.Scenario;
import org.cibseven.community.scenario.act.UserTaskAction;
import org.cibseven.community.scenario.delegate.TaskDelegate;
import org.cibseven.community.scenario.test.AbstractTest;
import org.junit.Before;
import org.junit.Test;

import static org.mockito.Mockito.*;

/**
 * @author Martin Schimak
 */
public class ExclusiveGatewayLoopTest extends AbstractTest {

  private int loop = 0;

  @Before
  public void setVariable() {
    variables.put("leave", true);
  }

  @Test
  @Deployment(resources = {"org/camunda/bpm/scenario/test/loops/ExclusiveGatewayLoopTest.bpmn"})
  public void testDoNotLoop() {

    when(scenario.waitsAtUserTask("UserTask")).thenReturn(new UserTaskAction() {
      @Override
      public void execute(TaskDelegate task) {
        task.complete(variables);
      }
    });

    Scenario.run(scenario).startByKey("ExclusiveGatewayLoopTest").execute();

    verify(scenario, times(1)).hasFinished("UserTask");
    verify(scenario, times(1)).hasFinished("EndEvent");

  }

  @Test
  @Deployment(resources = {"org/camunda/bpm/scenario/test/loops/ExclusiveGatewayLoopTest.bpmn"})
  public void testDoLoopASingleTime() {

    when(scenario.waitsAtUserTask("UserTask")).thenReturn(new UserTaskAction() {
      @Override
      public void execute(TaskDelegate task) {
        variables.put("leave", false);
        task.complete(variables);
      }
    }).thenReturn(new UserTaskAction() {
      @Override
      public void execute(TaskDelegate task) {
        variables.put("leave", true);
        task.complete(variables);
      }
    });

    Scenario.run(scenario).startByKey("ExclusiveGatewayLoopTest").execute();

    verify(scenario, times(2)).hasFinished("UserTask");
    verify(scenario, times(1)).hasFinished("EndEvent");

  }

  @Test
  @Deployment(resources = {"org/camunda/bpm/scenario/test/loops/ExclusiveGatewayLoopTest.bpmn"})
  public void testDoTaskTenTimes() {

    when(scenario.waitsAtUserTask("UserTask")).thenReturn(new UserTaskAction() {
      @Override
      public void execute(TaskDelegate task) {
        variables.put("leave", ++loop == 10);
        task.complete(variables);
      }
    });

    Scenario.run(scenario).startByKey("ExclusiveGatewayLoopTest").execute();

    verify(scenario, times(10)).hasFinished("UserTask");
    verify(scenario, times(1)).hasFinished("EndEvent");

  }

}
