/**
 * Spring Boot MVC application using JStachio.
 * <p>
 * Application has the following:
 * <ul>
 * <li>{@linkplain io.jstach.opt.spring.example.hello.HelloController Controller}</li>
 * <li>{@linkplain io.jstach.opt.spring.example.hello.HelloModel Model}</li>
 * <li>io.jstach.opt.spring.example.HelloModelView (jstachio generated renderer)</li>
 * </ul>
 * <strong> Make sure to take note of the {@linkplain io.jstach.opt.spring.example/
 * annotations on this module as they define the jstachio config} needed to integrate with
 * Spring. </strong> This is because much of JStachio config is <em>not runtime driven but
 * compile time driven</em>.
 * <p>
 * <em>This package is only exported for documenting the Spring Example. In a real world
 * app you probably would not export a package like this. </em>
 */
package io.jstach.opt.spring.example;
