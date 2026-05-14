package org.cibseven.community.scenario.test.report.dinner;

import org.cibseven.bpm.engine.test.Deployment;
import org.cibseven.community.scenario.ProcessScenario;
import org.cibseven.community.scenario.report.junit.ProcessEngineExtensionWithReporting;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import static org.cibseven.community.scenario.Scenario.run;
import static org.mockito.Mockito.*;

/**
 * @author Martin Schimak
 */
@ExtendWith(ProcessEngineExtensionWithReporting.class)
@Deployment(resources = {"org/camunda/bpm/scenario/test/report/dinner/Dinner.bpmn"})
public class DinnerTest {

  ProcessScenario process = mock(ProcessScenario.class);

  @BeforeEach
  public void everything_works_out_as_expected() {
    when(process.waitsAtServiceTask("PrepareDinner"))
      .thenReturn(task -> task.complete());
    when(process.waitsAtUserTask("HaveDinnerTogether"))
      .thenReturn(task -> task.complete());
  }

  @Test
  public void happy_dinner_together() {
    when_a_dinner_is_upcoming();
    then_the_meal_is_prepared();
    then_we_have_meal_together();
    then_the_meal_is_finished();
  }

  @Test
  public void ingredients_are_missing() {
    given_ingredients_are_missing();
    when_a_dinner_is_upcoming();
    then_the_meal_is_not_prepared();
    then_we_dont_have_meal_together();
  }

  public void given_ingredients_are_missing() {
    when(process.waitsAtServiceTask("PrepareDinner"))
      .thenReturn(task -> task.handleBpmnError("IngredientsMissing"));
  }

  public void when_a_dinner_is_upcoming() {
    run(process).startByKey("Dinner").execute();
  }

  public void then_the_meal_is_prepared() {
    verify(process, times(1)).hasFinished("DinnerPrepared");
    verify(process, never()).hasFinished("DinnerNotPrepared");
  }

  public void then_the_meal_is_not_prepared() {
    verify(process, times(1)).hasFinished("DinnerNotPrepared");
    verify(process, never()).hasFinished("DinnerPrepared");
  }

  public void then_we_have_meal_together() {
    verify(process, times(1)).hasFinished("HaveDinnerTogether");
  }

  public void then_we_dont_have_meal_together() {
    verify(process, never()).hasFinished("HaveDinnerTogether");
  }

  public void then_the_meal_is_finished() {
    verify(process, times(1)).hasFinished("DinnerFinished");
  }

}
