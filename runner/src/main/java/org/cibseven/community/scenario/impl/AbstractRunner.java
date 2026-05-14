package org.cibseven.community.scenario.impl;

import org.cibseven.community.scenario.run.Runner;

import java.util.List;

/**
 * @author Martin Schimak
 */
public abstract class AbstractRunner implements Runner {

  public abstract List<Executable> next();

}
