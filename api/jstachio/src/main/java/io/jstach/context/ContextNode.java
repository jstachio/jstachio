package io.jstach.context;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

import org.eclipse.jdt.annotation.Nullable;

/**
 * This interface serves two puproses:
 *
 * <ol>
 * <li>A way to represent the current context stack (see {@link #parent()})
 * <li>Allow you to simulate JSON/Javscript object node like trees without being coupled
 * to a particularly JSON lib.
 * </ol>
 * The interface simply wraps {@link Map} and {@link Iterable} (and arrays) lazily through
 * composition but generally cannot wrap other context nodes. If an object is wrapped that
 * is not a Map or Iterable it becomes a leaf node similar to JSON.
 * <p>
 * It is not recommended you use this interface as it avoids much of the type checking
 * safty of this library as well as increase coupling however it does provide a slightly
 * better bridge to legacy {@code Map<String,?>} models over using the maps directly.
 *
 * @apiNote The parents do not know anything about their children as it is the child that
 * has reference to the parent.
 * @author agentgt
 *
 */
public interface ContextNode extends Iterable<ContextNode> {

	/**
	 * Creates the root node which has no name.
	 * @apiNote Unlike the other methods in this class if the passed in object is a
	 * context node it is simply returned if it is a root node otherwise it is rewrapped.
	 * @param o the object to be wrapped. Maybe <code>null</code>.
	 * @return <code>null</code> if the root object is null otherwise a new root node.
	 */
	public static @Nullable ContextNode ofRoot(@Nullable Object o) {
		if (o == null) {
			return null;
		}
		if (o instanceof ContextNode n) {
			if (n.parent() != null) {
				return ofRoot(n.object());
			}
			return n;
		}
		return new RootContextNode(o);

	}

	/**
	 * Creates a named child node off of this node where the return child nodes parent
	 * will be this node.
	 * @param name the context name.
	 * @param o the object to be wrapped.
	 * @return <code>null</code> if the child object is null otherwise a new child node.
	 * @throws IllegalArgumentException if the input object is a {@link ContextNode}
	 */
	default @Nullable ContextNode ofChild(String name, @Nullable Object o) throws IllegalArgumentException {
		if (o == null) {
			return null;
		}
		if (o instanceof ContextNode) {
			throw new IllegalArgumentException("Cannot wrap ContextNode around another ContextNode");
		}
		return new NamedContextNode(this, o, name);
	}

	/**
	 * Creates an indexed child node off of this node where the return child nodes parent
	 * will be this node.
	 * @apiNote there is no checking to see if the same index is reused as the parent
	 * knows nothing of the child.
	 * @param index a numeric index
	 * @param o the object to be wrapped. Maybe <code>null</code>.
	 * @return <code>null</code> if the child object is null otherwise a new child node.
	 * @throws IllegalArgumentException if the input object is a {@link ContextNode}
	 */
	default @Nullable ContextNode ofChild(int index, @Nullable Object o) {
		if (o == null) {
			return null;
		}
		if (o instanceof ContextNode) {
			throw new IllegalArgumentException("Cannot wrap ContextNode around another ContextNode");
		}
		return new IndexedContextNode(this, o, index);
	}

	/**
	 * Gets a field from a {@link Map} if ContextNode is wrapping one. This is direct
	 * access (end of a dotted path) and does not check the parents.
	 *
	 * Just like {@link Map} <code>null</code> will be returned if no field is found.
	 * @param field the name of the field
	 * @return a new child node. Maybe <code>null</code>.
	 */
	default @Nullable ContextNode get(String field) {
		Object o = object();
		ContextNode child = null;
		if (o instanceof Map<?, ?> m) {
			child = ofChild(field, m.get(field));
		}
		return child;
	}

	/**
	 * Will search up the tree for a field starting at this nodes children first.
	 * @param field context name (e.g. section name)
	 * @return <code>null</code> if not found otherwise creates a new node from the map or
	 * object containing the field.
	 */
	default @Nullable ContextNode find(String field) {
		/*
		 * In theory we could make a special RenderingContext for ContextNode to go up the
		 * stack (generated code) but it would probably look similar to the following.
		 */
		ContextNode child = get(field);
		if (child != null) {
			return child;
		}
		var parent = parent();
		if (parent != null && parent != this) {
			child = parent.find(field);
			if (child != null) {
				child = ofChild(field, child.object());
			}
		}
		return child;
	}

	/**
	 * The object being wrapped.
	 * @return the Map, Iterable or object that was wrapped. Never <code>null</code>.
	 */
	public Object object();

	/**
	 * Convenience method for calling <code>toString</code> on the wrapped object.
	 * @return a toString on the wrapped object.
	 */
	default String renderString() {
		return String.valueOf(object());
	}

	/**
	 * The parent node.
	 * @return the parent node or <code>null</code> if this is the root.
	 */
	default @Nullable ContextNode parent() {
		return null;
	}

	/**
	 * If the node is a Map or a non iterable/array a singleton iterator will be returned.
	 * Otherwise if it is an interable/array new child context nodes will be created
	 * lazily.
	 * @return lazy iterator of context nodes.
	 */
	@Override
	default Iterator<ContextNode> iterator() {
		Object o = object();
		if (o instanceof Iterable<?> it) {
			AtomicInteger index = new AtomicInteger();
			return StreamSupport.stream(it.spliterator(), false).map(i -> this.ofChild(index.getAndIncrement(), i))
					.iterator();
		}
		else if (o == null || Boolean.FALSE.equals(o)) {
			return Collections.emptyIterator();
		}
		else if (o.getClass().isArray()) {
			/*
			 * There is probably an easier way to do this
			 */
			Stream<? extends Object> s;
			if (o instanceof int[] a) {
				s = Arrays.stream(a).boxed();
			}
			else if (o instanceof long[] a) {
				s = Arrays.stream(a).boxed();
			}
			else if (o instanceof double[] a) {
				s = Arrays.stream(a).boxed();
			}
			else if (o instanceof boolean[] a) {
				List<Boolean> b = new ArrayList<>();
				for (var _a : a) {
					b.add(_a);
				}
				s = b.stream();
			}
			else if (o instanceof char[] a) {
				List<Character> b = new ArrayList<>();
				for (var _a : a) {
					b.add(_a);
				}
				s = b.stream();
			}
			else if (o instanceof byte[] a) {
				List<Byte> b = new ArrayList<>();
				for (var _a : a) {
					b.add(_a);
				}
				s = b.stream();
			}
			else if (o instanceof float[] a) {
				List<Float> b = new ArrayList<>();
				for (var _a : a) {
					b.add(_a);
				}
				s = b.stream();
			}
			else if (o instanceof short[] a) {
				List<Short> b = new ArrayList<>();
				for (var _a : a) {
					b.add(_a);
				}
				s = b.stream();
			}
			else if (o instanceof Object[] a) {
				s = Arrays.asList(a).stream();
			}
			else {
				throw new IllegalArgumentException("array type not supported: " + o.getClass());
			}
			AtomicInteger index = new AtomicInteger();
			return s.map(i -> this.ofChild(index.getAndIncrement(), i)).iterator();
		}

		return Collections.singletonList(this).iterator();
	}

	/**
	 * Determines if an object is falsey based on mustache spec semantics where:
	 * <code>null</code>, empty iterables, empty arrays and boolean <code>false</code> are
	 * falsey.
	 * @param context a context object. ContextNode are allowed as input as well as
	 * <code>null</code>.
	 * @return true if the object is falsey.
	 */
	static boolean isFalsey(@Nullable Object context) {
		if (context == null) {
			return true;
		}
		if (Boolean.FALSE.equals(context)) {
			return true;
		}
		if (context instanceof Collection<?> c) {
			return c.isEmpty();
		}
		if (context instanceof Iterable<?> it) {
			return !it.iterator().hasNext();
		}
		if (context.getClass().isArray() && Array.getLength(context) == 0) {
			return false;
		}
		return false;
	}

}

record RootContextNode(Object object) implements ContextNode {
	@Override
	public String toString() {
		return renderString();
	}
}

record NamedContextNode(ContextNode parent, Object object, String name) implements ContextNode {
	@Override
	public String toString() {
		return renderString();
	}
}

record IndexedContextNode(ContextNode parent, Object object, int index) implements ContextNode {
	@Override
	public String toString() {
		return renderString();
	}
}
