package io.jstach.jstachio.context;

/**
 * A marker interface to signify something is context aware. If JStachio finds either the
 * model or output to implement this interface it will be bound to the context with the
 * name {@value ContextNode#CONTEXT_BINDING_NAME}.
 *
 * @author agentgt
 */
public interface ContextSupplier {

	/**
	 * A context node never null but maybe {@linkplain ContextNode#empty() empty}.
	 * @return context node.
	 */
	public ContextNode context();

}
