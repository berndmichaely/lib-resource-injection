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

import java.util.Objects;
import java.util.Optional;
import java.util.function.Consumer;
import java.util.stream.Stream;
import org.checkerframework.checker.initialization.qual.UnderInitialization;
import org.checkerframework.checker.nullness.qual.EnsuresNonNull;
import org.checkerframework.checker.nullness.qual.Nullable;

/**
 * Object to wrap an (optional) byte array. Similar to the Optional type, but
 * mutable, so it is suitable to be installed final in a constructor and having
 * a binary resource injected at runtime.
 *
 * @author Bernd Michaely (info@bernd-michaely.de)
 */
public class OptionalBinaryObject
{
	private Optional<byte[]> optionalData;

	public OptionalBinaryObject()
	{
		this.optionalData = Optional.empty();
	}

	public OptionalBinaryObject(byte[] data)
	{
		set(data);
	}

	@Override
	public boolean equals(@Nullable Object object)
	{
		if (object instanceof OptionalBinaryObject other)
		{
			return Objects.equals(this.optionalData, other.optionalData);
		}
		else
		{
			return false;
		}
	}

	@Override
	public int hashCode()
	{
		return Objects.hashCode(this.optionalData);
	}

	public byte[] get()
	{
		return optionalData.get();
	}

	@EnsuresNonNull("optionalData")
	public void set(@UnderInitialization OptionalBinaryObject this,
		byte[] data)
	{
		this.optionalData = data != null ? Optional.of(data) : Optional.empty();
	}

	public void set(Optional<byte[]> data)
	{
		this.optionalData = data != null ? data : Optional.empty();
	}

	public boolean isEmpty()
	{
		return optionalData.isEmpty();
	}

	public boolean isPresent()
	{
		return optionalData.isPresent();
	}

	public void ifPresent(Consumer<byte[]> consumer)
	{
		final byte[] value = get();
		if (value != null)
		{
			consumer.accept(value);
		}
	}

	public void ifPresentOrElse(Consumer<byte[]> consumer, Runnable emptyAction)
	{
		final byte[] value = get();
		if (value != null)
		{
			consumer.accept(value);
		}
		else
		{
			emptyAction.run();
		}
	}

	public Stream<Optional<byte[]>> streamOptional()
	{
		return Stream.of(optionalData);
	}

	public Stream<byte[]> stream()
	{
		final byte[] value = get();
		return value != null ? Stream.of(value) : Stream.empty();
	}
}
