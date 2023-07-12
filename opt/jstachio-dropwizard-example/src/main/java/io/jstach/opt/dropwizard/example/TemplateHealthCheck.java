package io.jstach.opt.dropwizard.example;

import com.codahale.metrics.health.HealthCheck;

/**
 * Ignore this health check
 *
 * @author agentgt
 * @hidden
 */
public class TemplateHealthCheck extends HealthCheck {

	private final String template;

	/**
	 * Ignore for now
	 * @param template ignore
	 */
	public TemplateHealthCheck(String template) {
		this.template = template;
	}

	@Override
	protected Result check() throws Exception {
		final String saying = String.format(template, "TEST");
		if (!saying.contains("TEST")) {
			return Result.unhealthy("template doesn't include a name");
		}
		return Result.healthy();
	}

}
