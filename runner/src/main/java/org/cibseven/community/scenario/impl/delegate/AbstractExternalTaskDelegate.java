package org.cibseven.community.scenario.impl.delegate;

import org.cibseven.bpm.engine.externaltask.ExternalTask;
import org.cibseven.bpm.engine.history.HistoricActivityInstance;
import org.cibseven.community.scenario.delegate.ExternalTaskDelegate;
import org.cibseven.community.scenario.impl.ProcessRunnerImpl;
import org.cibseven.community.scenario.impl.WaitstateExecutable;

import java.util.Date;
import java.util.Map;

/**
 * @author Martin Schimak
 */
public abstract class AbstractExternalTaskDelegate extends WaitstateExecutable<ExternalTask> implements ExternalTaskDelegate {

  public AbstractExternalTaskDelegate(ProcessRunnerImpl runner, HistoricActivityInstance instance) {
    super(runner, instance);
  }

  public String getId() {
    return delegate.getId();
  }

  public String getTopicName() {
    return delegate.getTopicName();
  }

  public String getWorkerId() {
    return delegate.getWorkerId();
  }

  public Date getLockExpirationTime() {
    return delegate.getLockExpirationTime();
  }

  public String getProcessInstanceId() {
    return delegate.getProcessInstanceId();
  }

  public String getActivityInstanceId() {
    return delegate.getActivityInstanceId();
  }

  public String getProcessDefinitionId() {
    return delegate.getProcessDefinitionId();
  }

  public String getProcessDefinitionKey() {
    return delegate.getProcessDefinitionKey();
  }

  public Integer getRetries() {
    return delegate.getRetries();
  }

  public String getErrorMessage() {
    return delegate.getErrorMessage();
  }

  public boolean isSuspended() {
    return delegate.isSuspended();
  }

  public String getTenantId() {
    return delegate.getTenantId();
  }

  public long getPriority() {
    return delegate.getPriority();
  }

  public Map<String, String> getExtensionProperties() {
    return delegate.getExtensionProperties();
  }

}
