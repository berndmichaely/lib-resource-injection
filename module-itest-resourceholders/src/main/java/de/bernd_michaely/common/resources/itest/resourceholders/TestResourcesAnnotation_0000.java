package de.bernd_michaely.common.resources.itest.resourceholders;

import de.bernd_michaely.common.resources.FileExt;

public class TestResourcesAnnotation_0000
{
	// The following dummy annotation is included to suppress annotation processor warnings like
	// »The following options were not recognized by any processor: '[warnOnlyMissingResources, showCheckedResourceKeys]'«
	// which seem to occur if there are actually no annotations to be processed in the source code.
	@FileExt(".test")
	public byte[] dummyAnnotatedField;
}
