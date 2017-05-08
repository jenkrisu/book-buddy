package net.bookbuddy.utilities;

import java.io.IOException;
import java.io.InputStream;
import java.io.StringReader;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Document;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

/**
 * Helps handle InputStreams.
 */
public class InputStreamParser {

    /**
     * Converts InputStream to Document.
     *
     * @param is InputStream
     * @return Document
     */
    public static Document streamToXmlDoc(InputStream is) {
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();

        // Settings for factory
        factory.setValidating(false);
        factory.setIgnoringComments(true);
        factory.setIgnoringElementContentWhitespace(true);
        factory.setNamespaceAware(true);

        DocumentBuilder builder;
        Document document = null;

        try {
            builder = factory.newDocumentBuilder();
            document = builder.parse(is);
        } catch (ParserConfigurationException | SAXException | IOException ex) {
            ex.printStackTrace();
        }

        return document;
    }

    /**
     * Converts string to document.
     *
     * @param string string to convert
     * @return Document document
     */
    public static Document stringToDoc(String string) {
        Document doc = null;

        DocumentBuilderFactory factory = DocumentBuilderFactory
                .newInstance();
        factory.setNamespaceAware(true);

        try {
            doc = factory
                    .newDocumentBuilder()
                    .parse(new InputSource(new StringReader(string)));
        } catch (Exception ex) {
            ex.printStackTrace();
        }

        return doc;
    }

}