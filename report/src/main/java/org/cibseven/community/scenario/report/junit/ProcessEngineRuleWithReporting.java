package org.cibseven.community.scenario.report.junit;

import org.cibseven.bpm.engine.test.ProcessEngineRule;
import org.cibseven.community.scenario.report.bpmn.ProcessScenarioTestReportGenerator;
import org.junit.runner.Description;

/**
 * @author Martin Schimak
 */
public class ProcessEngineRuleWithReporting extends ProcessEngineRule {

  @Override
  protected void succeeded(Description description) {
    generateProcessScenarioTestReport(description);
    super.succeeded(description);
  }

  @Override
  protected void failed(Throwable throwable, Description description) {
    generateProcessScenarioTestReport(description);
    super.failed(throwable, description);
  }

  private void generateProcessScenarioTestReport(Description description) {
    Package featurePackage = description.getTestClass().getPackage();
    String featurePackageName = featurePackage != null ? featurePackage.getName() : null;
    new ProcessScenarioTestReportGenerator(
      featurePackageName,
      description.getTestClass().getSimpleName(),
      description.getMethodName()
    ).generate(deploymentId);
  }

}
