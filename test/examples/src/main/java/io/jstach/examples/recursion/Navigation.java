package io.jstach.examples.recursion;

import java.util.ArrayDeque;
import java.util.Deque;
import java.util.Iterator;
import java.util.List;
import java.util.Spliterator;
import java.util.Spliterators.AbstractSpliterator;
import java.util.function.Consumer;
import java.util.stream.StreamSupport;

import org.eclipse.jdt.annotation.NonNull;
import org.eclipse.jdt.annotation.Nullable;

import io.jstach.examples.recursion.Navigation.Node;

public record Navigation(NavNode tree) implements Iterable<Node> {

	public static Navigation of(List<NavNode> topLevel) {
		NavNode root = NavNode.of("root", topLevel);
		return new Navigation(root);
	}

	public record NavNode(String label, String url, List<NavNode> nodes) {

		public static NavNode ofLeaf(String label) {
			return new NavNode(label, label, List.<NavNode>of());
		}

		public static NavNode of(String label, List<NavNode> nodes) {
			return new NavNode(label, label, nodes);
		}
	}

	public enum NodeType {

		ROOT, START, VALUE, END

	}

	public record Node(NodeType type, @Nullable NavNode value, int depth) {
		public static Node start(int depth) {
			return new Node(NodeType.START, null, depth);
		}

		public static Node end(int depth) {
			return new Node(NodeType.END, null, depth);
		}

		public String indent() {
			StringBuilder sb = new StringBuilder();
			for (int i = depth; i > 0; i--) {
				sb.append("\t");
			}
			return sb.toString();
		}
	}

	@Override
	public @NonNull Iterator<Node> iterator() {
		return StreamSupport.stream(new NavNodeSpliterator(tree), false).iterator();
	}

	class NavNodeSpliterator extends AbstractSpliterator<Node> {

		private Deque<Node> stack;

		public NavNodeSpliterator(NavNode root) {
			super(Long.MAX_VALUE, 0);
			this.stack = new ArrayDeque<>();
			this.stack.push(new Node(NodeType.ROOT, root, 0));
		}

		@Override
		public boolean tryAdvance(Consumer<? super Node> action) {
			if (stack.isEmpty()) {
				return false;
			}
			Node node = stack.pop();
			action.accept(node);
			NavNode v = node.value;
			int depth = node.depth + 1;
			if (v != null) {
				List<NavNode> childNodes = v.nodes();
				if (!childNodes.isEmpty()) {
					stack.push(Node.end(depth));
					for (int i = childNodes.size() - 1; i >= 0; i--) {
						stack.push(new Node(NodeType.VALUE, childNodes.get(i), depth));
					}
					stack.push(Node.start(depth));
				}

			}
			return true;
		}

		@Override
		public Spliterator<Node> trySplit() {
			return null;
		}

	}

}
