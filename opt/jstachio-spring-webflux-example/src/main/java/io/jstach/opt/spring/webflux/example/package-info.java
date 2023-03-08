/**
 * Spring Boot MVC application using JStachio.
 * <p>
 * Application has the following:
 * <ul>
 * <li>{@link io.jstach.opt.spring.webflux.example.hello.HelloController Controller}</li>
 * <li>{@link io.jstach.opt.spring.webflux.example.hello.HelloModel Model}</li>
 * <li>io.jstach.opt.spring.webflux.example.hello.HelloModelView (jstachio generated
 * renderer)</li>
 * <li>{@link io.jstach.opt.spring.webflux.example.SpringTemplateConfig Bean
 * configuration}</li>
 * <li>{@link io.jstach.opt.spring.webflux.example.WebConfig Web configuration}</li>
 * </ul>
 * <strong> Make sure to take note of the annotations on this module as they define the
 * jstachio config needed to integrate with Spring. </strong> This is because much of
 * JStachio config is <em>not runtime driven but compile time driven</em>.
 * <p>
 * <em>This package is only exported for documenting the Spring Example. In a real world
 * app you probably would not export a package like this. </em>
 */
@JStacheConfig(using = App.class)
package io.jstach.opt.spring.webflux.example;

import io.jstach.jstache.JStacheConfig;
