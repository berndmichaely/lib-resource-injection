package de.bernd_michaely.common.resources.itest.resourceholders;

import de.bernd_michaely.common.resources.ResourceHolder;
import de.bernd_michaely.common.resources.StringResources;

@StringResources(
	modulename = "this.module.does.not.exist",
	packagename = ".strings",
	basename = "string")
public class TestFallBackNoModule extends ResourceHolder
{
	public String testFallBackStringResource;
}
