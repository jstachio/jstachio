/**
 * We statically configure all annotated JStache in this package to follow the builtin
 * dropwizard config.
 * @see io.jstach.opt.dropwizard.DropwizardJStacheConfig
 */
@io.jstach.jstache.JStacheConfig(using = io.jstach.opt.dropwizard.DropwizardJStacheConfig.class)
package io.jstach.opt.dropwizard.example;