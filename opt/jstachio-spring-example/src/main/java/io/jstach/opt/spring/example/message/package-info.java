/**
 * Spring Boot MVC components using JStachio.
 * <p>
 * Has the following:
 * <ul>
 * <li>{@link MessageController Controller}</li>
 * <li>{@link MessageConfiguration View Configurer}</li>
 * </ul>
 * <strong> Make sure to take note of the {@link io.jstach.opt.spring.example/ annotations
 * on this module as they define the jstachio config} needed to integrate with Spring.
 * </strong> This is because much of JStachio config is <em>not runtime driven but compile
 * time driven</em>.
 * <p>
 * <em>This package is only exported for documenting the Spring Example. In a real world
 * app you probably would not export a package like this. </em>
 */
@JStacheInterfaces(templateAnnotations = { Component.class })
package io.jstach.opt.spring.example.message;

import org.springframework.stereotype.Component;

import io.jstach.jstache.JStacheInterfaces;
