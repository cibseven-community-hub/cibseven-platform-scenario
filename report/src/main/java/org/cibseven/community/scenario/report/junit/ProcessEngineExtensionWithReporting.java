package org.cibseven.community.scenario.report.junit;

import org.cibseven.bpm.engine.test.junit5.ProcessEngineExtension;
import org.cibseven.community.scenario.report.bpmn.ProcessScenarioTestReportGenerator;
import org.junit.jupiter.api.extension.ExtensionContext;

/**
 * @author Martin Schimak
 */
public class ProcessEngineExtensionWithReporting extends ProcessEngineExtension {

  public static ProcessEngineExtension builder() {
    return new ProcessEngineExtensionWithReporting();
  }

  public void afterTestExecution(ExtensionContext context) {
    generateProcessScenarioTestReport(context);
    super.afterTestExecution(context);
  }

  private void generateProcessScenarioTestReport(ExtensionContext context) {
    context.getTestMethod().ifPresent(method -> {
      Package featurePackage = method.getDeclaringClass().getPackage();
      String featurePackageName = featurePackage != null ? featurePackage.getName() : null;
      new ProcessScenarioTestReportGenerator(
        featurePackageName,
        method.getDeclaringClass().getSimpleName(),
        method.getName()
      ).generate(deploymentId);
    });
  }

}
