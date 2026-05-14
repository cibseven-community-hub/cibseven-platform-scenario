package org.cibseven.community.scenario.test.combinations;

import org.cibseven.bpm.engine.test.Deployment;
import org.cibseven.community.scenario.Scenario;
import org.cibseven.community.scenario.act.UserTaskAction;
import org.cibseven.community.scenario.delegate.TaskDelegate;
import org.cibseven.community.scenario.test.AbstractTest;
import org.junit.Test;

import static org.mockito.Mockito.*;

/**
 * @author Martin Schimak
 */
public class UserTaskAndMockedServiceTest extends AbstractTest {

  @Test
  @Deployment(resources = {"org/camunda/bpm/scenario/test/combinations/UserTaskAndMockedServiceTest.bpmn"})
  public void testCompleteTask() {

    when(scenario.waitsAtUserTask("UserTask")).thenReturn(new UserTaskAction() {
      @Override
      public void execute(final TaskDelegate task) throws Exception {
        task.complete();
      }
    });

    Scenario.run(scenario).startByKey("UserTaskAndMockedServiceTest").execute();

    verify(scenario, times(1)).hasFinished("EndEvent");

  }

}
