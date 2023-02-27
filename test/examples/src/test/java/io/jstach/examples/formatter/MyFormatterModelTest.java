package io.jstach.examples.formatter;

import static org.junit.Assert.assertEquals;

import java.time.LocalDate;

import org.junit.Test;

public class MyFormatterModelTest {

	@Test
	public void test() {
		MyFormatterModel m = new MyFormatterModel(LocalDate.ofYearDay(2023, 1));
		String actual = MyFormatterModelRenderer.of().execute(m);
		assertEquals("2023-01-01", actual);
	}

}
