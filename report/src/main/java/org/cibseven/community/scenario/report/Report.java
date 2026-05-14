package org.cibseven.community.scenario.report;

import org.cibseven.bpm.model.bpmn.BpmnModelInstance;
import org.cibseven.community.scenario.report.bpmn.ProcessCoverageReport;
import org.cibseven.community.scenario.report.bpmn.ProcessScenarioReport;
import org.cibseven.community.scenario.report.bpmn.ProcessScenarioTestReportGenerator;

/**
 * @author Martin Schimak
 */
public interface Report<R> {

  R generate(String id);

  static Report<BpmnModelInstance> processScenarioReport() {
    return new ProcessScenarioReport();
  }

  static Report<BpmnModelInstance> processCoverageReport() {
    return new ProcessCoverageReport();
  }

}
