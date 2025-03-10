import io.jstach.jstache.JStache;

@JStache(template = """
		Hello {{message}}
		""")
public record LocalPackageRecord(String message) {

	@JStache
	public record InnerLocal(String message) {

	}

}
