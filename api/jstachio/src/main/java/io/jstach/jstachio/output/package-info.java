/**
 * Output utility classes particularly for leveraging pre-encoding efficiently and shared
 * integration logic for plugging into web application frameworks.
 *
 * <h2>Blocking frameworks</h2>
 * <strong>{@link io.jstach.jstachio.output.LimitEncodedOutput}</strong>
 * <p>
 * For blocking frameworks {@link io.jstach.jstachio.output.LimitEncodedOutput} provides a
 * good solution over just writing to directly to the body {@link java.io.OutputStream} if
 * the length of output is needed apriori while avoiding running out of memory for rare
 * large outputs (in those cases the length will not be determined before writing to the
 * stream).
 *
 * <h2>Non-blocking frameworks</h2>
 * <strong>{@link io.jstach.jstachio.output.BufferedEncodedOutput}</strong>
 * <p>
 * Because JStachio does not have a reactive model the only reliable solution is to
 * completely buffer the output. As long as template output is not that large this is
 * generally not much of a problem as memory is often cheap and GC pretty fast these days.
 * For this model {@link io.jstach.jstachio.output.BufferedEncodedOutput} can allow the
 * complete output to be read either as a ByteBuffer or a channel.
 *
 * <h2>By type</h2>
 *
 * In some cases frameworks only allow returning output by a certain type. Below is rough
 * guide based on type.
 * <table border="1">
 * <caption><strong>Output choice by type</strong></caption>
 * <tr>
 * <th>Type</th>
 * <th>Strategy</th>
 * </tr>
 * <tr>
 * <td>{@link java.nio.ByteBuffer} or <code>byte[]</code></td>
 * <td>Use {@link io.jstach.jstachio.output.ByteBufferEncodedOutput}.</td>
 * </tr>
 * <tr>
 * <td>{@link java.io.OutputStream}</td>
 * <td>
 * <ul>
 * <li>If size must ALWAYS be known before writing use
 * {@link io.jstach.jstachio.output.ChunkEncodedOutput}.</li>
 * <li>If size must be known for MOST outputs use
 * {@link io.jstach.jstachio.output.LimitEncodedOutput}.</li>
 * <li>If size is not needed use
 * {@link io.jstach.jstachio.Output.EncodedOutput#of(java.io.OutputStream, java.nio.charset.Charset)}.
 * </li>
 * </ul>
 * </td>
 * </tr>
 * <tr>
 * <td>{@link java.io.Writer}</td>
 * <td>Use {@link io.jstach.jstachio.Template#execute(Object, Appendable)} (pre-encoding
 * will not be used).</td>
 * </tr>
 * <tr>
 * <td>{@link java.nio.channels.ReadableByteChannel} or
 * <code>Iterable&lt;byte[]&gt;</code></td>
 * <td>Use {@link io.jstach.jstachio.output.ChunkEncodedOutput}. The iterable of
 * <code>byte[]</code> can also be converted to a reactive data type.</td>
 * </tr>
 * </table>
 *
 *
 * @see io.jstach.jstachio.output.BufferedEncodedOutput
 * @see io.jstach.jstachio.output.ThresholdEncodedOutput
 * @apiNote As with most IO the classes in this package are not thread safe unless noted.
 */
@org.eclipse.jdt.annotation.NonNullByDefault
package io.jstach.jstachio.output;