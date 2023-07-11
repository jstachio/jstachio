package io.jstach.opt.spring.webmvc;

import java.io.IOException;
import java.io.OutputStream;
import java.nio.charset.Charset;

import io.jstach.jstachio.output.ThresholdEncodedOutput;
import jakarta.servlet.http.HttpServletResponse;

class ServletThresholdEncodedOutput extends ThresholdEncodedOutput.OutputStreamThresholdEncodedOutput {

	private final HttpServletResponse response;

	public ServletThresholdEncodedOutput(Charset charset, HttpServletResponse response) {
		super(charset, calculateLimit(response));
		this.response = response;
	}

	private static int calculateLimit(HttpServletResponse response) {
		int limit = response.getBufferSize();
		if (limit <= 0) {
			/*
			 * It is probably lying here or its a unit test.
			 */
			return 1024 * 32;
		}
		return limit;
	}

	@Override
	protected OutputStream createConsumer(int size) throws IOException {
		if (size > -1) {
			response.setContentLength(size);
			/*
			 * It is already all in memory so we do not need a buffer.
			 */
			response.setBufferSize(0);
		}
		return response.getOutputStream();
	}

}