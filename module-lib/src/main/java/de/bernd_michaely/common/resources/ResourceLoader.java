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

import java.lang.System.Logger;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Objects;
import java.util.function.Consumer;
import org.checkerframework.checker.nullness.qual.Nullable;

import static java.lang.System.Logger.Level.*;

/**
 * Class to centrally manage resource injection.
 *
 * @author Bernd Michaely (info@bernd-michaely.de)
 */
public class ResourceLoader
{
	private static final Logger LOGGER = System.getLogger(ResourceLoader.class.getName());
	private final List<ResourceHolderCallback> listCallbacks = new ArrayList<>();
	private Locale locale = Locale.ROOT;

	/**
	 * Class to encapsulate a ResourceHolder callback method with its
	 * corresponding class.
	 *
	 * @param <C> the ResourceHolder type
	 */
	private static class ResourceHolderCallback<C extends ResourceHolder>
	{
		private final Consumer<C> callback;
		private final Class<C> resourceHolderClass;

		/**
		 * Creates a new callback wrapper instance.
		 *
		 * @param callback            the callback to encapsulate
		 * @param resourceHolderClass the ResourceHolder class
		 */
		private ResourceHolderCallback(Consumer<C> callback, Class<C> resourceHolderClass)
		{
			this.callback = Objects.requireNonNull(callback,
				getClass().getName() + " : callback must not be null");
			this.resourceHolderClass = Objects.requireNonNull(resourceHolderClass,
				getClass().getName() + " : resourceHolderClass must not be null");
		}

		@Override
		public boolean equals(@Nullable Object object)
		{
			if (object instanceof ResourceHolderCallback other)
			{
				return Objects.equals(this.callback, other.callback);
			}
			else
			{
				return false;
			}
		}

		@Override
		public int hashCode()
		{
			return (callback != null) ? callback.hashCode() : 0;
		}

		@Override
		public String toString()
		{
			return getClass().getName() + "[" + callback + "]";
		}

		/**
		 * Runs the encapsulated callback. Loads the resources using the given
		 * ResourceInjector and calls the encapsulated callback method.
		 *
		 * @param resourceInjector the given ResourceInjector
		 */
		private void runCallback(ResourceInjector resourceInjector)
		{
			final C newInstance = resourceInjector.injectResourcesInto(this.resourceHolderClass);
			if (newInstance != null)
			{
				this.callback.accept(newInstance);
			}
			else
			{
				LOGGER.log(ERROR, "Error trying to create ResourceHolder instance");
			}
		}
	}

	/**
	 * Registers a callback method for a ResourceHolder class. If the callback
	 * method was already registered before, it will be moved to the end of the
	 * list. (This implies that the list will not contain any duplicates).
	 *
	 * @param <R>                 the ResourceHolder type
	 * @param resourceHolderClass the given ResourceHolder class
	 * @param callback            the callback
	 * @return true, if the callback was already registered before, in which case
	 *         it will be moved to the end of the list
	 */
	public <R extends ResourceHolder> boolean register(
		Class<R> resourceHolderClass, Consumer<R> callback)
	{
		LOGGER.log(INFO, "Register callback for class »{0}«", resourceHolderClass);
		final ResourceHolderCallback<R> resourceHolderCallback =
			new ResourceHolderCallback<>(callback, resourceHolderClass);
		final boolean isDuplicate = this.listCallbacks.remove(resourceHolderCallback);
		this.listCallbacks.add(resourceHolderCallback);
		if (this.locale != null)
		{
			resourceHolderCallback.runCallback(new ResourceInjector(this.locale));
		}
		return isDuplicate;
	}

	/**
	 * Unregisters a previously registered callback.
	 *
	 * @param <R>      the ResourceHolder type
	 * @param callback the callback to unregister
	 * @return true, if the callback was unregistered, false, if it was not
	 *         contained in the list
	 */
	public <R extends ResourceHolder> boolean unregister(Consumer<R> callback)
	{
		final boolean removed = this.listCallbacks.removeIf(item -> callback.equals(item.callback));
		if (removed)
		{
			LOGGER.log(INFO, "Callback »{0}« was unregistered.", callback);
		}
		else
		{
			LOGGER.log(WARNING, "Callback »{0}« was NOT unregistered!", callback);
		}
		return removed;
	}

	/**
	 * Returns the current locale.
	 *
	 * @return the current locale (which is never null)
	 */
	public Locale getLocale()
	{
		return locale;
	}

	/**
	 * Sets a new locale. If the new locale is different from the current, all
	 * registered callbacks will be called with the resources for the new locale.
	 *
	 * @param locale the locale to set – a null value will be treated as
	 *               {@link Locale#ROOT}
	 */
	public void setLocale(Locale locale)
	{
		final Locale l = Objects.requireNonNullElse(locale, Locale.ROOT);
		if (!l.equals(this.locale))
		{
			this.locale = l;
			final ResourceInjector resourceInjector = new ResourceInjector(l);
			this.listCallbacks.forEach(c -> c.runCallback(resourceInjector));
		}
	}
}
