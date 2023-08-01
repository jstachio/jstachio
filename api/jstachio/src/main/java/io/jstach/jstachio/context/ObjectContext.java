package io.jstach.jstachio.context;

import java.util.Iterator;

import org.eclipse.jdt.annotation.Nullable;

import io.jstach.jstachio.context.Internal.ObjectContextNode;

/**
 * Extend this class to make a model more like JSON/Map
 */
public non-sealed abstract class ObjectContext implements ObjectContextNode {

	/**
	 * Get value like map.
	 * @param key not null
	 * @return value mapped to key.
	 */
	@Override
	public abstract @Nullable Object getValue(String key);

	@Override
	public final Object object() {
		return this;
	}

	@Override
	public final @Nullable ContextNode get(String field) {
		return ObjectContextNode.super.get(field);
	}

	@Override
	public final @Nullable ContextNode find(String field) {
		return ObjectContextNode.super.find(field);
	}

	@Override
	public final @Nullable ContextNode parent() {
		return null;
	}

	@SuppressWarnings("exports")
	@Override
	public final Iterator<@Nullable ContextNode> iterator() {
		return ObjectContextNode.super.iterator();
	}

}
