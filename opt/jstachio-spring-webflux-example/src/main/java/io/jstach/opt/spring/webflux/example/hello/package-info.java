/**
 * Spring Boot MVC components using JStachio.
 * <p>
 * Has the following:
 * <ul>
 * <li>{@link io.jstach.opt.spring.webflux.example.hello.HelloController Controller}</li>
 * <li>{@link io.jstach.opt.spring.webflux.example.hello.HelloModel Model}</li>
 * <li>io.jstach.opt.spring.webflux.example.HelloModelView (jstachio generated
 * renderer)</li>
 * </ul>
 * <strong> Make sure to take note of the annotations on this module as they define the
 * jstachio config needed to integrate with Spring. </strong> This is because much of
 * JStachio config is <em>not runtime driven but compile time driven</em>.
 * <p>
 * <em>This package is only exported for documenting the Spring Example. In a real world
 * app you probably would not export a package like this. </em>
 */
@JStacheInterfaces(templateAnnotations = { Component.class })
@JStacheConfig(using = App.class, naming = @JStacheName(suffix = "View"))
package io.jstach.opt.spring.webflux.example.hello;

import org.springframework.stereotype.Component;

import io.jstach.jstache.JStacheConfig;
import io.jstach.jstache.JStacheInterfaces;
import io.jstach.jstache.JStacheName;
import io.jstach.opt.spring.webflux.example.App;
