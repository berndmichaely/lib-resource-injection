/**
 * This module contains test resource holders in a separate java module to
 * ensure that access to resources is possible across modules.
 */
module de.bernd_michaely.common.resources.itest.resourceholders
{
	exports de.bernd_michaely.common.resources.itest.resourceholders;
	requires de.bernd_michaely.common.resources;
}
