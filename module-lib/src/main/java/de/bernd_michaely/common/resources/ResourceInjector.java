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

import java.io.IOException;
import java.io.InputStream;
import java.lang.System.Logger;
import java.lang.System.Logger.Level;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.AbstractMap;
import java.util.EnumMap;
import java.util.Locale;
import java.util.Map;
import java.util.MissingResourceException;
import java.util.Objects;
import java.util.Optional;
import java.util.ResourceBundle;
import java.util.Set;
import java.util.TreeMap;
import java.util.TreeSet;
import java.util.function.BiConsumer;
import org.checkerframework.checker.nullness.qual.KeyFor;
import org.checkerframework.checker.nullness.qual.MonotonicNonNull;
import org.checkerframework.checker.nullness.qual.Nullable;

import static de.bernd_michaely.common.resources.SharedConstants.SEPARATOR_ENUM_KEY;
import static java.util.Collections.unmodifiableMap;
import static java.util.Objects.requireNonNullElse;

/**
 * Class to perform runtime resource injection via reflection.
 *
 * @author Bernd Michaely (info@bernd-michaely.de)
 */
public class ResourceInjector implements SharedConstants
{
	private static final Logger LOGGER = System.getLogger(ResourceInjector.class.getName());
	private final Locale locale;
	private @MonotonicNonNull ResourceParameters param;

	/**
	 * Same as {@link #ResourceInjector(Locale) ResourceInjector(null)}.
	 */
	public ResourceInjector()
	{
		this(null);
	}

	/**
	 * Creates a new ResourceInjector instance.
	 *
	 * @param locale the locale of the resources to be injected, null will be
	 *               treated as {@link Locale#ROOT}
	 */
	public ResourceInjector(@Nullable Locale locale)
	{
		this.locale = Objects.requireNonNullElse(locale, Locale.ROOT);
	}

	/**
	 * Returns the locale given with the constructor.
	 *
	 * @return the locale given with the constructor
	 */
	public Locale getLocale()
	{
		return this.locale;
	}

	/**
	 * Instantiates a ResourceHolder class and injects the resources according to
	 * its annotations.
	 *
	 * @param <R>                 the type of the ResourceHolder
	 * @param resourceHolderClass the ResourceHolder class
	 * @return a ResourceHolder instance injected with resources or null, if the
	 *         class could not be instantiated
	 * @see #injectResourcesInto(ResourceHolder)
	 */
	public @Nullable
	<R extends ResourceHolder> R injectResourcesInto(Class<R> resourceHolderClass)
	{
		try
		{
			final R newInstance = resourceHolderClass.getDeclaredConstructor().newInstance();
			injectResourcesInto(newInstance);
			return newInstance;
		}
		catch (ReflectiveOperationException | SecurityException ex)
		{
			LOGGER.log(Logger.Level.WARNING,
				"Error trying to create ResourceHolder instance", ex);
			return null;
		}
	}

	/**
	 * Injects resources into the given ResourceHolder according to its
	 * annotations.
	 *
	 * @param resourceHolder the given ResourceHolder
	 * @see #injectResourcesInto(Class)
	 */
	public void injectResourcesInto(ResourceHolder resourceHolder)
	{
		resourceHolder.setLocale(this.locale);
		this.param = new ResourceParameters(resourceHolder.getClass());
		if (!this.param.hasAnyResources())
		{
			LOGGER.log(Level.WARNING,
				"Top level ResourceHolder instance »{0}« used without @[String|Binary]Resources",
				resourceHolder);
		}
		handleResourceHolder(resourceHolder);
	}

	/**
	 * Returns the resource string or a fallback value, if it does not exist.
	 *
	 * @param resourceHolder the ResourceHolder
	 * @param key            the String resource key
	 * @param hasEnumPostfix true, if the key contains an enum postfix
	 * @return the resource string
	 */
	private String getStringResource(Object resourceHolder, String key, boolean hasEnumPostfix)
	{
		final Module module;
		final String baseName;
		if (this.param != null)
		{
			module = this.param.getStringResourcesModule(resourceHolder.getClass().getModule());
			final String packageName = this.param.getStringResourcePackageName();
			baseName = packageName.isBlank() ? this.param.getBasename() :
				(packageName + '.' + this.param.getBasename());
		}
		else
		{
			module = null;
			baseName = "";
		}
		try
		{
			final ResourceBundle bundle = module != null ?
				ResourceBundle.getBundle(baseName, getLocale(), module) :
				ResourceBundle.getBundle(baseName, getLocale());
			final String value = (bundle != null) ? bundle.getString(key) : null;
			return (value != null) ? value :
				new FallBackValueUtil(getLocale()).getFallBackValue(key, hasEnumPostfix);
		}
		catch (MissingResourceException ex)
		{
			final String fallbackValue = new FallBackValueUtil(getLocale())
				.getFallBackValue(key, hasEnumPostfix);
			LOGGER.log(Level.WARNING, String.format(
				"Missing string resource [»%s.properties« → %s] → using fallback value »%s« – for »%s«",
				baseName, key, fallbackValue, module));
			return fallbackValue;
		}
	}

	/**
	 * Returns the resource data.
	 *
	 * @param resourceHolder the module of the ResourceHolder
	 * @param key            the String resource key
	 * @param fileExt        the resource file name extension
	 * @return a byte array containing the resource file content
	 */
	private Optional<byte[]> getBinaryResource(Object resourceHolder, String key, @Nullable FileExt fileExt)
	{
		if (this.param != null)
		{
			final Module module = this.param.getBinaryResourcesModule(resourceHolder.getClass().getModule());
			final String p = this.param.getBinaryResourcePackageName();
			final String ext = fileExt != null ? fileExt.value() : this.param.getDefaultExtension();
			final String packageName = p.isEmpty() ? getClass().getPackageName() : p;
			final String fileName = "/" + packageName.replaceAll("\\.", "/") + "/" + key + ext;
			try (InputStream inputStream = module.getResourceAsStream(fileName))
			{
				if (inputStream != null)
				{
					return Optional.of(inputStream.readAllBytes());
				}
			}
			catch (IOException ex)
			{
				LOGGER.log(Level.WARNING, "Missing binary resource »{0}«", fileName);
			}
		}
		return Optional.empty();
	}

	private static boolean isResourceHolder(Field field)
	{
		return (field == null) ? false :
			ResourceHolder.class.isAssignableFrom(field.getType());
	}

	private static boolean isGenericResource(Field field)
	{
		return (field == null) ? false :
			field.getType().isAnnotationPresent(GenericResources.class);
	}

	private static String getResourceKey(@Nullable String keyPrefix, Field field)
	{
		final ResourceKey annotation = field.getAnnotation(ResourceKey.class);
		final String strResourceKey = (annotation != null && !annotation.value().isBlank()) ?
			annotation.value() : field.getName();
		final String prefix = requireNonNullElse(keyPrefix, "") + strResourceKey;
		return isGenericResource(field) ? (prefix + SEPARATOR_KEY_NESTED) : prefix;
	}

	private void handleResourceHolder(ResourceHolder resourceHolder)
	{
		handleResourceHolder(resourceHolder, null, null);
	}

	private static Map<String, Class<? extends Enum>> getMapEnumTypes(
		@Nullable Field fieldRef, BiConsumer<EnumTypes, String> onDuplicateFieldName)
	{
		final EnumTypes[] arrEnumTypes = (fieldRef != null) ?
			fieldRef.getAnnotationsByType(EnumTypes.class) : null;
		final Map<String, Class<? extends Enum>> map = new TreeMap<>();
		final Set<String> duplicateFieldNames = new TreeSet<>();
		if (arrEnumTypes != null)
		{
			for (EnumTypes annEnumTypes : arrEnumTypes)
			{
				final Class<? extends Enum> type = annEnumTypes.enumType();
				for (String fieldName : annEnumTypes.fieldNames())
				{
					if (!duplicateFieldNames.contains(fieldName))
					{
						if (map.containsKey(fieldName))
						{
							map.remove(fieldName);
							duplicateFieldNames.add(fieldName);
							if (onDuplicateFieldName != null)
							{
								onDuplicateFieldName.accept(annEnumTypes, fieldName);
							}
						}
						else
						{
							map.put(fieldName, type);
						}
					}
				}
			}
		}
		return unmodifiableMap(map);
	}

	private void handleResourceHolder(Object resourceHolder,
		@Nullable String keyPrefix, @Nullable Field fieldRef)
	{
		final var mapEnumTypes = getMapEnumTypes(fieldRef, (e, fieldName) ->
		{
			LOGGER.log(Level.ERROR,
				"»@EnumTypes(…) […] {0}« contains duplicate field name »{1}«",
				"" + fieldRef, fieldName);
		});
		for (Field field : resourceHolder.getClass().getFields())
		{
			final String fieldName = field.getName();
			final Class<?> fieldType = field.getType();
			final String typeName = fieldType.getName();
			final int modifiers = field.getModifiers();
			if (!Modifier.isPublic(modifiers))
			{
				LOGGER.log(Level.WARNING, "Field »{0}« must be declared public", fieldName);
			}
			else if (Modifier.isFinal(modifiers) && !TYPE_NAME_BYTE_ARRAY.equals(typeName))
			{
				LOGGER.log(Level.WARNING, "Field »{0}« must not be declared final", fieldName);
			}
			else
			{
				final String resourceKey = getResourceKey(keyPrefix, field);
				try
				{
					switch (typeName)
					{
						case TYPE_NAME_STRING ->
						{
							try
							{
								final String stringResource = getStringResource(
									resourceHolder, resourceKey, false);
								field.set(resourceHolder, stringResource);
							}
							catch (IllegalArgumentException | IllegalAccessException ex)
							{
								LOGGER.log(Level.WARNING,
									"Error when setting value of field »" + resourceKey + "«", ex);
							}
						}
						case TYPE_NAME_BYTE_ARRAY ->
						{
							final Optional<byte[]> binaryResource = getBinaryResource(
								resourceHolder, resourceKey, field.getAnnotation(FileExt.class));
							if (binaryResource.isPresent())
							{
								System.out.println("Field: " + field);
								final Class<?> type = field.getType();
								System.out.println("field.getType() : " + type);
								System.out.println("--> getName() : " + type.getName());
								final Method method = type.getMethod("set", Optional.class);
								System.out.println("Method : " + method);
								method.invoke(field, binaryResource);
							}
						}
						case TYPE_NAME_ENUM_STRING_MAP ->
							handleFieldEnumStrings(resourceHolder, resourceKey, field, mapEnumTypes);
						case TYPE_NAME_ENUM_BYTE_ARRAY_MAP ->
							handleFieldEnumByteArrays(resourceHolder, resourceKey, field, mapEnumTypes);
						case TYPE_NAME_ENUM_RESOURCE_HOLDER_MAP ->
							handleFieldEnumResourceHolder(resourceHolder, resourceKey, field);
						default ->
						{
							if (isGenericResource(field))
							{
								final Object newInstance = fieldType.getDeclaredConstructor().newInstance();
								field.set(resourceHolder, newInstance);
								handleResourceHolder(newInstance, resourceKey, field);
							}
							else
							{
								LOGGER.log(Level.WARNING, "Invalid type of field »{0} {1}« in ResourceHolder",
									typeName, fieldName);
							}
						}
					}
				}
				catch (ReflectiveOperationException | IllegalArgumentException ex)
				{
					LOGGER.log(Level.WARNING,
						"Error when setting value of field »" + fieldName + "«", ex);
				}
			}
		}
	}

	@SuppressWarnings("unchecked")
	private void handleFieldEnumStrings(Object resourceHolder, String resourceKeyPrefix,
		Field field, Map<String, Class<? extends Enum>> mapEnumTypes)
		throws IllegalArgumentException, IllegalAccessException
	{
		final String fieldName = field.getName();
		final EnumType enumType = field.getAnnotation(EnumType.class);
		final Class<? extends Enum> enumClass = (enumType != null) ?
			enumType.value() : mapEnumTypes.get(fieldName);
		if (enumClass != null)
		{
			class EnumStringMapImpl<E extends Enum<E>>
				extends AbstractMap<E, String>
				implements EnumStringMap<E>
			{
				private final EnumMap<@KeyFor("this") E, String> enumMap = new EnumMap<>(enumClass);

				@Override
				public Set<Map.Entry<@KeyFor("this") E, String>> entrySet()
				{
					return enumMap.entrySet();
				}
			}
			final var enumStringMap = new EnumStringMapImpl();
			final var enumConstants = enumClass.getEnumConstants();
			if (enumConstants != null)
			{
				for (Enum enumConstant : enumConstants)
				{
					final String resourceKey = resourceKeyPrefix + SEPARATOR_ENUM_KEY + enumConstant.name();
					final String stringResource = getStringResource(resourceHolder, resourceKey, true);
					enumStringMap.enumMap.put(enumConstant, stringResource);
				}
				field.set(resourceHolder, enumStringMap);
			}
		}
		else
		{
			LOGGER.log(Level.WARNING,
				"Invalid declaration of field »EnumStringMap<Enum> {0}«", fieldName);
		}
	}

	@SuppressWarnings("unchecked")
	private void handleFieldEnumByteArrays(Object resourceHolder, String resourceKeyPrefix,
		Field field, Map<String, Class<? extends Enum>> mapEnumTypes)
		throws IllegalArgumentException, IllegalAccessException
	{
		final String fieldName = field.getName();
		final EnumType enumType = field.getAnnotation(EnumType.class);
		final Class<? extends Enum> enumClass = (enumType != null) ?
			enumType.value() : mapEnumTypes.get(fieldName);
		if (enumClass != null)
		{
			class EnumByteArrayMapImpl<E extends Enum<E>>
				extends AbstractMap<E, byte[]>
				implements EnumByteArrayMap<E>
			{
				private final EnumMap<@KeyFor("this") E, byte[]> enumMap = new EnumMap<>(enumClass);

				@Override
				public Set<Entry<@KeyFor("this") E, byte[]>> entrySet()
				{
					return enumMap.entrySet();
				}
			}
			final var enumByteArrayMap = new EnumByteArrayMapImpl();
			final var enumConstants = enumClass.getEnumConstants();
			if (enumConstants != null)
			{
				for (Enum enumConstant : enumConstants)
				{
					final String resourceKey = resourceKeyPrefix + SEPARATOR_ENUM_FILE + enumConstant.name();
					getBinaryResource(resourceHolder, resourceKey, field.getAnnotation(FileExt.class))
						.ifPresent(binaryResource -> enumByteArrayMap.enumMap.put(enumConstant, binaryResource));
				}
				field.set(resourceHolder, enumByteArrayMap);
			}
		}
		else
		{
			LOGGER.log(Level.WARNING,
				"Invalid declaration of field »EnumByteArrayMap<Enum> {0}«", fieldName);
		}
	}

	@Deprecated
	@SuppressWarnings("unchecked")
	private void handleFieldEnumResourceHolder(Object resourceHolder,
		String resourceKeyPrefix, Field field) throws ReflectiveOperationException
	{
		final String fieldName = field.getName();
		final EnumType enumType = field.getAnnotation(EnumType.class);
		final Class<? extends Enum> enumClass = (enumType != null) ? enumType.value() : null;
		final ResourceHolderType resourceHolderType = field.getAnnotation(ResourceHolderType.class);
		final Class<? extends ResourceHolder> resourceHolderClass = (resourceHolderType != null) ?
			resourceHolderType.value() : null;
		if (enumClass != null)
		{
			if (resourceHolderClass != null)
			{
				class EnumResourceHolderMapImpl<E extends Enum<E>, R extends ResourceHolder>
					extends AbstractMap<E, R> implements EnumResourceHolderMap<E, R>
				{
					private final EnumMap<@KeyFor("this") E, R> enumMap = new EnumMap<>(enumClass);

					@Override
					public Set<Entry<@KeyFor("this") E, R>> entrySet()
					{
						return enumMap.entrySet();
					}
				}
				final var enumResourceHolderMap = new EnumResourceHolderMapImpl();
				final var enumConstants = enumClass.getEnumConstants();
				if (enumConstants != null)
				{
					for (Enum enumConstant : enumConstants)
					{
						final String prefix = resourceKeyPrefix + SEPARATOR_ENUM_FILE +
							enumConstant.name() + SEPARATOR_KEY_NESTED;
						final ResourceHolder newInstance = resourceHolderClass.getDeclaredConstructor().newInstance();
						enumResourceHolderMap.enumMap.put(enumConstant, newInstance);
						handleResourceHolder(newInstance, prefix, field);
					}
				}
			}
			else
			{
				LOGGER.log(Level.WARNING,
					"Missing @ResourceHolderType on field »EnumResourceHolderMap<E extends Enum<E>> {0}«",
					fieldName);
			}
		}
		else
		{
			LOGGER.log(Level.WARNING,
				"Missing @EnumType on field »EnumResourceHolderMap<E extends Enum<E>> {0}«",
				fieldName);
		}
	}
}
