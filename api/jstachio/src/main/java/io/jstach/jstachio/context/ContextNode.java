package io.jstach.jstachio.context;

import java.lang.reflect.Array;
import java.util.Arrays;
import java.util.Collections;
import java.util.Iterator;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

import org.eclipse.jdt.annotation.NonNull;
import org.eclipse.jdt.annotation.Nullable;

import io.jstach.jstache.JStacheType;
import io.jstach.jstachio.Appender;
import io.jstach.jstachio.Formatter;
import io.jstach.jstachio.Formatter.Formattable;
import io.jstach.jstachio.Output;
import io.jstach.jstachio.context.Internal.ContextNodeFactory;
import io.jstach.jstachio.context.Internal.EmptyContextNode;
import io.jstach.jstachio.context.Internal.FunctionContextNode;

/**
 * This interface serves three puproses:
 *
 * <ol>
 * <li>A way to represent the current context stack (see {@link #parent()})
 * <li>Allow you to simulate JSON/Javscript object node like trees without being coupled
 * to a particularly JSON lib.
 * <li>Represent per request context data in a web framework like CSRF tokens.
 * </ol>
 * The interface simply wraps {@link Map} and {@link Iterable} (and arrays) lazily through
 * composition but generally cannot wrap other context nodes. If an object is wrapped that
 * is not a Map or Iterable it becomes a leaf node similar to JSON.
 * <p>
 * It is not recommended you use this interface as it avoids much of the type checking
 * safty of this library, decreases performance as well as increase coupling however it
 * does provide a slightly better bridge to legacy {@code Map<String,?>} models over using
 * the maps directly.
 * <p>
 * Context Node while similar to a Map does not follow the same rules of resolution where
 * Map resolves bindings always last. It will resolve first and thus it is easy to
 * accidentally get stuck in the Context Node context. To prevent this it is highly
 * recommended you do not open a context node with a section block and prefer dotted
 * notation to access it.
 *
 * <h2>Example:</h2>
 *
 * <pre><code class="language-hbs">
 * {{message}}
 * {{#&#64;context}}
 * {{message}} {{! message here will only ever resolve against &#64;context and not the parent }}
 * {{/&#64;context}}
 * </code> </pre>
 *
 * @apiNote The parents do not know anything about their children as it is the child that
 * has reference to the parent. This interface unlike most of JStachio API is very
 * <code>null</code> heavy because JSON and Javascript allow <code>null</code>.
 * @author agentgt
 * @see ContextJStachio
 */
public sealed interface ContextNode extends Formattable, Iterable<@Nullable ContextNode> {

	/**
	 * The default binding name in mustache for the context parameter. The context comes
	 * from the context parameter from either
	 * {@link ContextJStachio#execute(Object, ContextNode, Output)} or
	 * {@link ContextJStachio#write(Object, ContextNode, io.jstach.jstachio.Output.EncodedOutput)}.
	 * <p>
	 * <strong>This variable is not bound if the generated template is
	 * {@link JStacheType#STACHE}</strong>
	 */
	public static final String CONTEXT_BINDING_NAME = "@context";

	/**
	 * Creates a root context node with the given function to look up children.
	 * @param function used to find children with a given name
	 * @return root context node powered by a function
	 * @apiNote Unlike many other methods in this class this is not nullable.
	 */
	public static ContextNode of(Function<String, ?> function) {
		if (Objects.isNull(function)) {
			throw new NullPointerException("function is required");
		}
		return new FunctionContextNode(function);
	}

	/**
	 * An empty context node that is safe to use identify comparison.
	 * @return empty singleton context node
	 */
	public static ContextNode empty() {
		return EmptyContextNode.EMPTY;
	}

	/**
	 * Resolves the context node from an object.
	 * @param o object that maybe a context or have a context.
	 * @return {@link #empty()} if not found.
	 */
	public static ContextNode resolve(Object o) {
		if (o instanceof ContextSupplier cs) {
			return cs.context();
		}
		if (o instanceof ContextNode n) {
			return n;
		}
		return ContextNode.empty();
	}

	/**
	 * Resolves the context node trying first and then second.
	 * @param first first object to try
	 * @param second second object to try
	 * @return {@link #empty()} if not found.
	 */
	public static ContextNode resolve(Object first, Object second) {
		var f = resolve(first);
		if (f == ContextNode.empty()) {
			return resolve(second);
		}
		return f;
	}

	/**
	 * Creates the root node from an Object.
	 * @param o the object to be wrapped. Maybe <code>null</code>.
	 * @return {@link ContextNode#empty()} if the root object is null otherwise a new root
	 * node.
	 * @apiNote this method is legacy and mainly used for testing. Prefer
	 * {@link #of(Function)}. Prior to 1.3.0 the method may return null but now it will
	 * always return nonnull.
	 */
	public static ContextNode ofRoot(@Nullable Object o) {
		if (o == null) {
			return ContextNode.empty();
		}
		return ContextNodeFactory.INSTANCE.create(null, o);
	}

	/**
	 * Gets a field from a ContextNode. This is direct access (end of a dotted path) and
	 * does not check the parents. The default implementation will check if the wrapping
	 * object is a {@link Map} and use it to return a child context node.
	 *
	 * Just like {@link Map} <code>null</code> will be returned if no field is found.
	 * @param field the name of the field
	 * @return a new child node. Maybe <code>null</code>.
	 */
	public @Nullable ContextNode get(String field);

	/**
	 * Will search up the tree for a field starting at this nodes children first.
	 * @param field context name (e.g. section name)
	 * @return <code>null</code> if not found otherwise creates a new node from the map or
	 * object containing the field.
	 */
	public @Nullable ContextNode find(String field);

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
	 * Otherwise if it is an iterable/array new child context nodes will be created
	 * lazily.
	 * @return lazy iterator of context nodes.
	 * @apiNote Notice that return iterator may return <code>null</code> elements as JSON
	 * lists may contain <code>null</code> elements.
	 */
	@SuppressWarnings("exports")
	@Override
	public Iterator<@Nullable ContextNode> iterator();

	/**
	 * Determines if the node is falsey. If falsey (return of true) inverted section
	 * blocks will be executed. The default checks if {@link #iterator()} has any next
	 * elements and if it does not it is falsey.
	 * @return true if falsey.
	 */
	@SuppressWarnings("AmbiguousMethodReference")
	default boolean isFalsey() {
		return !iterator().hasNext();
	}

	/**
	 * Determines if an object is falsey based on mustache spec semantics where:
	 * <code>null</code>, empty iterables, empty arrays and boolean <code>false</code> are
	 * falsey however <strong>empty Map is not falsey</strong>. {@link Optional} is falsey
	 * if it is empty.
	 * @param context a context object. ContextNode are allowed as input as well as
	 * <code>null</code>.
	 * @return true if the object is falsey.
	 */
	@SuppressWarnings("AmbiguousMethodReference")
	static boolean isFalsey(@Nullable Object context) {
		if ((context == null) || Boolean.FALSE.equals(context)) {
			return true;
		}
		if (context instanceof Optional<?> o) {
			return o.isEmpty();
		}
		if (context instanceof Iterable<?> it) {
			return !it.iterator().hasNext();
		}
		if (context.getClass().isArray() && Array.getLength(context) == 0) {
			return true;
		}
		if (context instanceof ContextNode n) {
			return isFalsey(n);
		}
		return false;
	}

	/**
	 * Determines if the node is falsey based on mustache spec semantics where:
	 * <code>null</code>, empty iterables, empty arrays and boolean <code>false</code> are
	 * falsey however <strong>empty Map is not falsey</strong> but
	 * {@link ContextNode#empty()} is always falsey.
	 * @param context a context node. <code>null</code>.
	 * @return true if the node is falsey.
	 */
	@SuppressWarnings("AmbiguousMethodReference")
	static boolean isFalsey(@Nullable ContextNode context) {
		if (context == null) {
			return true;
		}
		return context.isFalsey();
	}

}

@SuppressWarnings("exports")
sealed interface Internal extends ContextNode {

	/**
	 * Creates a named child node off of this node where the return child nodes parent
	 * will be this node.
	 * @param name the context name.
	 * @param o the object to be wrapped.
	 * @return <code>null</code> if the child object is null otherwise a new child node.
	 * @throws IllegalArgumentException if the input object is a {@link ContextNode}
	 */
	default @Nullable ContextNode ofChild(String name, @Nullable Object o) throws IllegalArgumentException {
		return ContextNodeFactory.INSTANCE.ofChild(this, name, o);
	}

	/**
	 * Creates an indexed child node off of this node where the return child nodes parent
	 * will be this node.
	 * @param index a numeric index
	 * @param o the object to be wrapped. Maybe <code>null</code>.
	 * @return <code>null</code> if the child object is null otherwise a new child node.
	 * @throws IllegalArgumentException if the input object is a {@link ContextNode}
	 * @apiNote there is no checking to see if the same index is reused as the parent
	 * knows nothing of the child.
	 */
	default @Nullable ContextNode ofChild(int index, @Nullable Object o) {
		return ContextNodeFactory.INSTANCE.ofChild(this, index, o);
	}

	@Override
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

	enum ContextNodeFactory {

		INSTANCE;

		@Nullable
		ContextNode ofChild(ContextNode parent, String name, @Nullable Object o) {
			if (o == null) {
				return null;
			}
			return create(parent, o);
		}

		@Nullable
		ContextNode ofChild(ContextNode parent, int index, @Nullable Object o) {
			if (o == null) {
				return null;
			}
			return create(parent, o);

		}

		ContextNode create(@Nullable ContextNode parent, Object o) {
			if (o instanceof ContextNode) {
				throw new IllegalArgumentException("Cannot wrap ContextNode around another ContextNode");
			}
			if (o instanceof Iterable<?> it) {
				return new IterableContextNode(it, parent);
			}
			if (o instanceof Map<?, ?> m) {
				return new MapContextNode(m, parent);
			}
			if (o instanceof Optional<?> opt) {
				return new OptionalContextNode(opt, parent);
			}
			return new ValueContextNode(o, parent);

		}

		public Iterator<@Nullable ContextNode> iteratorOf(ContextNode parent, Iterable<?> it) {
			int[] j = { -1 };
			return StreamSupport.stream(it.spliterator(), false) //
					.<@Nullable ContextNode>map(i -> this.ofChild(parent, (j[0] += 1), i)) //
					.iterator();
		}

		public Iterator<@Nullable ContextNode> iteratorOf(ContextNode parent, @Nullable Object o) {

			if (o == null || Boolean.FALSE.equals(o)) {
				return Collections.emptyIterator();
			}
			else if (o instanceof Iterable<?> it) {
				return iteratorOf(parent, o);
			}
			else if (o instanceof Optional<?> opt) {
				return opt.stream() //
						.<@Nullable ContextNode>map(i -> this.ofChild(parent, 0, i)) //
						.iterator();
			}
			else if (o.getClass().isArray()) {

				Stream<? extends @Nullable Object> s = arrayToStream(o);
				int[] j = { -1 };
				return s.<@Nullable ContextNode>map(i -> this.ofChild(parent, (j[0] += 1), i)).iterator();
			}

			return Collections.<@Nullable ContextNode>singletonList(parent).iterator();

		}

		private static Stream<? extends @Nullable Object> arrayToStream(Object o) {

			if (o instanceof int[] a) {
				return Arrays.stream(a).boxed();
			}
			else if (o instanceof long[] a) {
				return Arrays.stream(a).boxed();
			}
			else if (o instanceof double[] a) {
				return Arrays.stream(a).boxed();
			}
			else if (o instanceof Object[] a) {
				return Arrays.asList(a).stream();
			}

			/*
			 * There is probably an easier way to do this
			 */
			final Stream.Builder<@Nullable Object> b = Stream.builder();

			if (o instanceof boolean[] a) {
				for (var _a : a) {
					b.add(_a);
				}
			}
			else if (o instanceof char[] a) {
				for (var _a : a) {
					b.add(_a);
				}
			}
			else if (o instanceof byte[] a) {
				for (var _a : a) {
					b.add(_a);
				}
			}
			else if (o instanceof float[] a) {
				for (var _a : a) {
					b.add(_a);
				}
			}
			else if (o instanceof short[] a) {
				for (var _a : a) {
					b.add(_a);
				}
			}
			else {
				throw new IllegalArgumentException("array type not supported: " + o.getClass());
			}
			return b.build();
		}

	}

	sealed interface ObjectContextNode extends Internal permits ObjectContext, FunctionContextNode, MapContextNode {

		/**
		 * Get value like map.
		 * @param key not null
		 * @return value mapped to key.
		 */
		public @Nullable Object getValue(String key);

		@Override
		default @Nullable ContextNode get(String field) {
			return ofChild(field, getValue(field));
		}

		@Override
		default Iterator<@Nullable ContextNode> iterator() {
			return Collections.<@Nullable ContextNode>singleton(this).iterator();
		}

		@Override
		default boolean isFalsey() {
			return false;
		}

		@Override
		default <A extends Output<E>, E extends Exception> void format(Formatter formatter, Appender downstream,
				String path, A a) throws E {
			throw new UnsupportedOperationException("ContextNode cannot be formatted. object: " + object());
		}

	}

	sealed interface ListNode extends Internal {

		@Override
		default @Nullable ContextNode get(String field) {
			return null;
		}

		@Override
		public Iterator<@Nullable ContextNode> iterator();

		@Override
		default <A extends Output<E>, E extends Exception> void format(Formatter formatter, Appender downstream,
				String path, A a) throws E {
			throw new UnsupportedOperationException(
					"Possible bug. Iterable node cannot be formatted. object: " + object());
		}

	}

	record IterableContextNode(Iterable<?> object, @Nullable ContextNode parent) implements ListNode {

		@Override
		public Iterator<@Nullable ContextNode> iterator() {
			return ContextNodeFactory.INSTANCE.iteratorOf(this, object);
		}

	}

	sealed interface ValueNode extends Internal {

		@Override
		default @Nullable ContextNode get(String field) {
			return null;
		}

	}

	record OptionalContextNode(Optional<?> object, @Nullable ContextNode parent) implements ValueNode {
		@Override
		public boolean isFalsey() {
			return object.isEmpty();
		}

		@Override
		public @NonNull Iterator<@Nullable ContextNode> iterator() {
			return object.stream().<@Nullable ContextNode>map(i -> this.ofChild(0, i)).iterator();
		}

		@Override
		public <A extends Output<E>, E extends Exception> void format(Formatter formatter, Appender downstream,
				String path, A a) throws E {
			throw new UnsupportedOperationException("Optional<?> node cannot be formatted. object: " + object());
		}

	}

	record ValueContextNode(Object object, @Nullable ContextNode parent) implements ValueNode {
		@Override
		public String toString() {
			return renderString();
		}

		@Override
		public Iterator<@Nullable ContextNode> iterator() {
			return ContextNodeFactory.INSTANCE.iteratorOf(this, object);
		}

		@Override
		public boolean isFalsey() {
			return ContextNode.isFalsey(object);
		}

		@Override
		public <A extends Output<E>, E extends Exception> void format(Formatter formatter, Appender downstream,
				String path, A a) throws E {
			var o = object();
			formatter.format(downstream, a, path, o.getClass(), o);
		}

	}

	record FunctionContextNode(Function<String, ?> object) implements ObjectContextNode {
		@Override
		public String toString() {
			return renderString();
		}

		@Override
		public @Nullable Object getValue(String key) {
			return object.apply(key);
		}
	}

	record MapContextNode(Map<?, ?> object, @Nullable ContextNode parent) implements ObjectContextNode {
		@Override
		public String toString() {
			return renderString();
		}

		@Override
		public @Nullable Object getValue(String key) {
			return object.get(key);
		}
	}

	enum EmptyContextNode implements Internal {

		EMPTY;

		@Override
		public Object object() {
			return Map.of();
		}

		@Override
		public @Nullable ContextNode get(String field) {
			return null;
		}

		@Override
		public @Nullable ContextNode find(String field) {
			return null;
		}

		@Override
		public Iterator<@Nullable ContextNode> iterator() {
			return Collections.emptyIterator();
		}

		@Override
		public <A extends Output<E>, E extends Exception> void format(Formatter formatter, Appender downstream,
				String path, A a) throws E {
			formatter.format(downstream, a, path, this.toString());
		}

	}

}
