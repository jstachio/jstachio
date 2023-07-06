package io.jstach.jstachio.output;

import static org.junit.Assert.assertEquals;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.channels.Channels;
import java.nio.charset.StandardCharsets;
import java.util.stream.StreamSupport;

import org.junit.Test;

public class BufferedEncodedOutputTest {

	@Test
	public void testChunk() throws IOException {
		var output = ChunkEncodedOutput.ofByteArrays(StandardCharsets.UTF_8);
		fill(output);
		assertChannel(output);
		assertOutputStream(output);
		int size = StreamSupport.stream(output.getChunks().spliterator(), false).toList().size();
		assertEquals(COUNT * 3, size);
	}

	@Test
	public void testByteBuffer() throws IOException {
		// We set the buffer size small to test growing
		var output = ByteBufferEncodedOutput.ofByteArray(StandardCharsets.UTF_8, 10);
		fill(output);
		assertChannel(output);
		assertOutputStream(output);
	}

	private void assertOutputStream(BufferedEncodedOutput output) throws IOException {
		ByteArrayOutputStream os = new ByteArrayOutputStream(output.size());
		output.transferTo(os);
		String actual = os.toString(StandardCharsets.UTF_8);
		assertEquals(EXPECTED, actual);
	}

	private void assertChannel(BufferedEncodedOutput output) throws IOException {
		var channel = output.asReadableByteChannel();
		ByteArrayOutputStream os = new ByteArrayOutputStream(output.size());
		Channels.newInputStream(channel).transferTo(os);
		String actual = os.toString(StandardCharsets.UTF_8);

		assertEquals(EXPECTED, actual);
	}

	static final String EXPECTED = """
			Hello World - 1
			Hello World - 2
			Hello World - 3
			Hello World - 4
			Hello World - 5
			""";

	static final int COUNT = 5;

	private void fill(BufferedEncodedOutput output) {
		for (int i = 0; i < COUNT; i++) {
			output.append("Hello World - ");
			output.append(i + 1);
			output.append("\n");
		}
	}

}
