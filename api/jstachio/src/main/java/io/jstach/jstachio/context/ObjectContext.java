package io.jstach.jstachio.context;

import java.util.Iterator;
import java.util.Map;

import org.eclipse.jdt.annotation.Nullable;

import io.jstach.jstache.JStache;
import io.jstach.jstachio.context.Internal.ObjectContextNode;

/**
 * Extend this class to make {@link JStache} model act like JSON object or a
 * java.util.Map.
 *
 * @see ContextNode
 * @author agentgt
 */
public non-sealed abstract class ObjectContext implements ObjectContextNode {

	/**
	 * Do nothing constructor
	 */
	protected ObjectContext() {
	}

	/**
	 * Get a value by key. This is analagous to {@link Map#get(Object)}.
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

	@Override
	public final boolean isFalsey() {
		return ObjectContextNode.super.isFalsey();
	}

}
