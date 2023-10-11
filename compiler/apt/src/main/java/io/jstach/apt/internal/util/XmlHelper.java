package io.jstach.apt.internal.util;

import java.io.IOException;
import java.io.InputStream;
import java.util.Objects;
import java.util.Optional;
import java.util.Spliterator;
import java.util.Spliterators;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

class XmlHelper {

	private final Document doc;

	private final XPath xpath;

	private XmlHelper(Document doc, XPath xpath) {
		super();
		this.doc = doc;
		this.xpath = xpath;
	}

	public static XmlHelper of(InputStream stream) throws IOException {
		DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();

		Document doc;
		try {
			DocumentBuilder builder = factory.newDocumentBuilder();
			doc = builder.parse(stream);
		}
		catch (ParserConfigurationException e) {
			throw new IOException(e);
		}
		catch (SAXException e) {
			throw new IOException(e);
		}
		// Element root = doc.getDocumentElement();
		// http://maven.apache.org/POM/4.0.0"
		XPath xpath = XPathFactory.newInstance().newXPath();
		return new XmlHelper(doc, xpath);
	}

	public Optional<String> findString(String... paths) {
		try {
			for (String path : paths) {
				String v = (String) xpath.evaluate(path, doc, XPathConstants.STRING);
				if (v == null || v.isEmpty())
					continue;
				return Optional.of(v);
			}
			return Optional.empty();
		}
		catch (XPathExpressionException e) {
			throw new RuntimeException(e);
		}
	}

	public Stream<Element> findElements(String path) {
		try {
			NodeList list = (NodeList) xpath.evaluate(path, doc, XPathConstants.NODESET);
			Objects.requireNonNull(list); // unclear whether this can be null or not
			return toElementStream(list);
		}
		catch (XPathExpressionException e) {
			throw new RuntimeException(e);
		}

	}

	static Stream<Element> toElementStream(NodeList nodeList) {
		Spliterator<Element> spliterator = new Spliterators.AbstractSpliterator<Element>(nodeList.getLength(),
				Spliterator.ORDERED) {
			int index = 0;

			@Override
			public boolean tryAdvance(java.util.function.Consumer<? super Element> action) {
				while (index < nodeList.getLength()) {
					Node item = nodeList.item(index++);
					if (item instanceof Element e) {
						action.accept(e);
						return true;
					}
				}
				return false;
			}
		};

		return StreamSupport.stream(spliterator, false);
	}

}