package io.jstach.apt;

import java.io.IOException;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import org.eclipse.jdt.annotation.Nullable;

import io.jstach.apt.internal.LoggingSupport;
import io.jstach.apt.internal.MustacheToken;
import io.jstach.apt.internal.MustacheToken.TagToken;
import io.jstach.apt.internal.Position;
import io.jstach.apt.internal.PositionedToken;
import io.jstach.apt.internal.ProcessingException;
import io.jstach.apt.internal.TokenProcessor;
import io.jstach.apt.internal.token.MustacheTagKind;
import io.jstach.apt.internal.token.MustacheTokenizer;

/*
 * Process the contents inside a parent partial.
 *
 * The expectation is that we are about to receive {{< parent }} first
 */
class PartialParameterProcessor
		implements TokenProcessor<PositionedToken<MustacheToken>>, LoggingSupport.LoggingSupplier {

	private final String partialName;

	private final LoggingSupport logging;

	private final Map<String, Block> blocks = new LinkedHashMap<>();

	private Position position;

	private final ArrayDeque<Section<Integer>> sectionStack;

	private boolean done = false;

	private final StringBuilder endContent = new StringBuilder();

	private final List<PositionedToken<MustacheToken>> endTokens = new ArrayList<>();

	PartialParameterProcessor(String partialName, LoggingSupport logging) {
		super();
		this.partialName = partialName;
		this.position = Position.noPosition();
		this.logging = logging;
		this.sectionStack = new ArrayDeque<>();
	}

	public Map<String, Block> getBlocks() {
		return blocks;
	}

	public String getEndContent() {
		return endContent.toString();
	}

	@Override
	public void debug(CharSequence message) {
		LoggingSupplier.super.debug("[parameter_processor] " + message);
	}

	public List<PositionedToken<MustacheToken>> getEndTokens() {
		// return List.copyOf(getNextTokens());
		return endTokens;
	}

	record Block(Section<Integer> section, List<PositionedToken<MustacheToken>> tokens) {
		void appentRaw(StringBuilder sb) {
			for (var t : tokens) {
				t.innerToken().appendRawText(sb);
			}
		}

		public String content() {
			StringBuilder sb = new StringBuilder();
			this.appentRaw(sb);
			return sb.toString();
		}

		public String name() {
			return section.token().name();
		}
	}

	public void run(NamedReader reader) throws ProcessingException, IOException {
		TokenProcessor<@Nullable Character> processor = MustacheTokenizer.createInstance(reader.name(), this);
		int readResult;
		while (!done && (readResult = reader.read()) >= 0) {
			try {
				processor.processToken((char) readResult);
			}
			catch (ProcessingException e) {
				if (logging.isDebug()) {
					debug(e.getMessage());
					e.printStackTrace();
				}
				throw e;
			}
		}
		if (!done) {
			throw new ProcessingException(position,
					"parent partial was never closed and reached end of file. parent = " + partialName);
		}
	}

	enum ProcessorState {

		PROCESSING, DONE;

		public boolean isDone() {
			return this == DONE;
		}

	}

	public ProcessorState process(PositionedToken<MustacheToken> token) throws ProcessingException {
		processToken(token);
		if (done) {
			return ProcessorState.DONE;
		}
		return ProcessorState.PROCESSING;
	}

	public boolean isDone() {
		return done;
	}

	@Override
	public void processToken(PositionedToken<MustacheToken> token) throws ProcessingException {

		var mt = token.innerToken();
		if (isDebug()) {
			debug("token : " + mt);
		}
		this.position = token.position();
		if (done) {
			debug("Add end content token: " + mt);
			mt.appendRawText(endContent);
			endTokens.add(token);
			return;
		}

		int depth = sectionStack.size();

		if (depth < 1) {
			if (mt instanceof TagToken tt && tt.tagKind() == MustacheTagKind.BEGIN_PARENT_SECTION) {
				var beginSection = new Section<>(tt, position, depth);
				sectionStack.push(beginSection);
				return;
			}
			else {
				throw new IllegalStateException("bug expected begin parent as first token: " + token);
			}
		}

		@Nullable
		Block currentBlock = currentBlock();

		@Nullable
		Section<Integer> closedSection = null;

		if (mt instanceof TagToken tt) {
			String name = tt.name();
			if (tt.tagKind().isBeginSection()) {
				var beginSection = new Section<>(tt, position, depth);
				sectionStack.push(beginSection);
				if (tt.tagKind() == MustacheTagKind.BEGIN_BLOCK_SECTION && depth == 1) {
					if (blocks.containsKey(name)) {
						String error = "Duplicate block: " + name;
						debug(error);
						throw new ProcessingException(position, error);
					}
					else if (currentBlock == null) {
						debug("registering block: " + name);
						blocks.put(name, new Block(beginSection, new ArrayList<>()));
						/*
						 * returning
						 */
						return;
					}
				}
			}
			else if (tt.tagKind().isEndSection()) {
				if (!isCurrentSection(tt.name())) {
					debug("sectionStack " + sectionStack);
					String error = "Unexpected end section: \"" + name + "\" expecting: \"" + currentSection().name()
							+ "\"";
					debug(error);
					throw new ProcessingException(position, error);
				}
				closedSection = sectionStack.pop();
				if (sectionStack.isEmpty()) {
					done = true;
					/*
					 * returning
					 */
					return;
				}
			}
		}
		/*
		 * We are inside a block so we need to capture
		 */
		if (currentBlock != null) {
			if (closedSection != currentBlock.section()) {
				currentBlock.tokens().add(token);
			}
		}

	}

	private @Nullable Block currentBlock() {
		var it = sectionStack.descendingIterator();
		it.next(); // drop parent partial call
		if (!it.hasNext()) {
			return null;
		}
		var maybeBlock = it.next();
		if (maybeBlock.isBlock()) {
			Block block = blocks.get(maybeBlock.name());
			if (block == null) {
				throw new IllegalStateException("bug expected block to be registered");
			}
			return block;
		}
		return null;
	}

	private Section<Integer> currentSection() {
		return Objects.requireNonNull(sectionStack.peek());
	}

	private boolean isCurrentSection(String name) {
		return currentSection().name().equals(name);
	}

	@Override
	public LoggingSupport logging() {
		return logging;
	}

}
