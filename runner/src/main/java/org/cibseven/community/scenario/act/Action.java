package org.cibseven.community.scenario.act;


/**
 * @author Martin Schimak
 */
public interface Action<D> {

  void execute(final D runtimeObject) throws Exception;

}
