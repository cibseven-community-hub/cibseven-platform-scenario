package org.cibseven.community.scenario.test;

import org.cibseven.bpm.engine.test.ProcessEngineRule;
import org.cibseven.community.scenario.ProcessScenario;
import org.junit.Before;
import org.junit.Rule;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

/**
 * @author Martin Schimak
 */
public class AbstractTest {

  @Rule
  public ProcessEngineRule rule = new ProcessEngineRule();

  @Mock
  protected ProcessScenario scenario;

  @Mock
  protected ProcessScenario otherScenario;

  @Before
  public void setup() {
    MockitoAnnotations.initMocks(this);
  }

}
