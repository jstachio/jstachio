package io.jstach.opt.spring.webmvc;

import java.io.IOException;
import java.io.OutputStream;
import java.nio.charset.Charset;

import io.jstach.jstachio.output.ThresholdEncodedOutput;
import jakarta.servlet.http.HttpServletResponse;

/**
 * If you would like to use this class and make it public please file an issue.
 *
 * @author agent
 */
class ServletThresholdEncodedOutput extends ThresholdEncodedOutput.OutputStreamThresholdEncodedOutput {

	private final HttpServletResponse response;

	public ServletThresholdEncodedOutput(Charset charset, HttpServletResponse response, int bufferLimit) {
		super(charset, calculateLimit(response, bufferLimit));
		this.response = response;
	}

	private static int calculateLimit(HttpServletResponse response, int bufferLimit) {
		return Math.max(response.getBufferSize(), bufferLimit);
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