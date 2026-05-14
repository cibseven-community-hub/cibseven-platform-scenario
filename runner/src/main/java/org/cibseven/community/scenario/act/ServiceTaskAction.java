package org.cibseven.community.scenario.act;

import org.cibseven.community.scenario.delegate.ExternalTaskDelegate;

/**
 * @author Martin Schimak
 */
public interface ServiceTaskAction extends Action<ExternalTaskDelegate> {

  /**
   * Implement this action with custom code to be executed when the
   * process instance arrives at a service task (external task waitstate).
   *
   * @param externalTask the service task is implemented with.
   * @throws Exception in case your custom code throws checked exceptions.
   *                   Such exceptions will be wrapped into RuntimeExceptions and rethrown.
   */
  @Override
  void execute(final ExternalTaskDelegate externalTask) throws Exception;

}
