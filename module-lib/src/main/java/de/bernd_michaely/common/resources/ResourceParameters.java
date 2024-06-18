/*
 * Copyright 2024 Bernd Michaely (info@bernd-michaely.de).
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package de.bernd_michaely.common.resources;

import org.checkerframework.checker.nullness.qual.Nullable;

import static java.util.Objects.requireNonNull;
import static java.util.Objects.requireNonNullElse;

/**
 * Class to provide actual resource parameter info.
 *
 * @author Bernd Michaely (info@bernd-michaely.de)
 */
public class ResourceParameters
{
	private final @Nullable StringResources stringResources;
	private final @Nullable BinaryResources binaryResources;
	private final String resourceHolderPackageName;

	/**
	 * Constructor for runtime injection.
	 *
	 * @param c the resource holder class
	 */
	ResourceParameters(Class<? extends ResourceHolder> c)
	{
		this(requireNonNull(c, "ResourceParameters: ResourceHolder class is null")
			.getAnnotation(StringResources.class), c.getAnnotation(BinaryResources.class),
			c.getPackageName());
	}

	/**
	 * Constructor for annotation processor.
	 *
	 * @param stringResources           the @StringResources annotation of the
	 *                                  resource holder class
	 * @param binaryResources           the @BinaryResources annotation of the
	 *                                  resource holder class
	 * @param resourceHolderPackageName the package name of the resource holder
	 *                                  class
	 */
	public ResourceParameters(@Nullable StringResources stringResources,
		@Nullable BinaryResources binaryResources,
		String resourceHolderPackageName)
	{
		this.stringResources = stringResources;
		this.binaryResources = binaryResources;
		this.resourceHolderPackageName = resourceHolderPackageName;
	}

	String getAbsolutePackage(String packageName)
	{
		final String p = requireNonNullElse(packageName, "");
		final boolean isRelative = p.startsWith(".");
		return isRelative ? this.resourceHolderPackageName + p : p;
	}

	@Nullable
	StringResources getStringResources()
	{
		return this.stringResources;
	}

	public boolean hasStringResources()
	{
		return this.stringResources != null;
	}

	public String getStringResourcePackageName()
	{
		return (this.stringResources != null) ?
			getAbsolutePackage(this.stringResources.packagename()) : "";
	}

	public String getBasename()
	{
		return ((this.stringResources != null) && (this.stringResources.basename() != null)) ?
			this.stringResources.basename() : "";
	}

	Module getStringResourcesModule(Module defaultModule)
	{
		return (this.stringResources != null &&
			(this.stringResources.modulename() != null) &&
			!this.stringResources.modulename().isBlank()) ?
			ModuleLayer.boot().findModule(this.stringResources.modulename()).orElse(defaultModule) :
			defaultModule;
	}

	@Nullable
	BinaryResources getBinaryResources()
	{
		return this.binaryResources;
	}

	public boolean hasBinaryResources()
	{
		return this.binaryResources != null;
	}

	public String getBinaryResourcePackageName()
	{
		return (this.binaryResources != null) ?
			getAbsolutePackage(this.binaryResources.packagename()) : "";
	}

	public String getDefaultExtension()
	{
		return ((this.binaryResources != null) && (this.binaryResources.defaultExtension() != null)) ?
			this.binaryResources.defaultExtension() : "";
	}

	Module getBinaryResourcesModule(Module defaultModule)
	{
		return (this.binaryResources != null &&
			(this.binaryResources.modulename() != null) &&
			!this.binaryResources.modulename().isBlank()) ?
			ModuleLayer.boot().findModule(this.binaryResources.modulename()).orElse(defaultModule) :
			defaultModule;
	}

	boolean hasAnyResources()
	{
		return hasStringResources() || hasBinaryResources();
	}
}
