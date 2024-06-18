package de.bernd_michaely.common.resources.itest.resourceholders;

import de.bernd_michaely.common.resources.ResourceHolder;
import de.bernd_michaely.common.resources.StringResources;

@StringResources(
	modulename = "de.bernd_michaely.common.resources.itest.resources",
	packagename = ".strings",
	basename = "nonexisting")
public class TestFallBackNoFile extends ResourceHolder
{
	public String testFallBackStringResource;
}
