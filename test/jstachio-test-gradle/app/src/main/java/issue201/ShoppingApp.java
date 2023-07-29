package issue201;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.util.List;

import io.jstach.jstachio.JStachio;

public class ShoppingApp {

	public static void main(
			String[] args) {
		render(System.out);
	}

	public static void render(
			Appendable a) {
		var order = new Order("#123", //
				List.of(
						new Order.Item("TV Set"), //
						new Order.Item("Books")));
		try {
			JStachio.render(order, a);
		} catch (IOException e) {
			throw new UncheckedIOException(e);
		}
	}
}
