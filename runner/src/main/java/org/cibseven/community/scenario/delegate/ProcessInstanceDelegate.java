package org.cibseven.community.scenario.delegate;

import org.cibseven.bpm.engine.runtime.ProcessInstance;
import org.cibseven.community.scenario.defer.Deferrable;

/**
 * @author Martin Schimak
 */
public interface ProcessInstanceDelegate extends ProcessInstance, VariablesAwareDelegate, Deferrable {

}
