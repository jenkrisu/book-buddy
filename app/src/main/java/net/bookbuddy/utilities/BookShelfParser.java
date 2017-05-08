package net.bookbuddy.utilities;

import net.bookbuddy.data.Shelf;

import net.bookbuddy.data.BestBook;
import net.bookbuddy.data.Work;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Jenni on 3.5.2017.
 */

public class BookShelfParser {

    /**
     * Parses document to ArrayList of Shelf objects.
     *
     * @param doc Document
     * @return List<Work>
     */
    public static List<Shelf> docToShelves(Document doc) {
        List<Shelf> shelves = new ArrayList<Shelf>();
        NodeList shelvesNodeList = doc.getElementsByTagName("user_shelf");

        if (shelvesNodeList != null) {

            for (int i = 0; i < shelvesNodeList.getLength(); i++) {

                Element e = (Element) shelvesNodeList.item(i);

                String name = null;
                String id = null;
                String bookAmount = null;

                if (e.getElementsByTagName("name") != null) {
                    name = e.getElementsByTagName("name").item(0).getTextContent();
                }

                if (e.getElementsByTagName("id") != null) {
                    id = e.getElementsByTagName("id").item(0).getTextContent();
                }

                if (e.getElementsByTagName("book_count") != null) {
                    bookAmount = e.getElementsByTagName("book_count").item(0).getTextContent();
                }

                if (name != null && id != null) {
                    shelves.add(new Shelf(name, id, bookAmount));
                }
            }
        }
        return shelves;
    }

    /**
     * Adds shelves of book to list.
     *
     * @param doc Document to parse
     * @return List with shelves
     */
    public static List<Shelf> docToShelvesOfBook(Document doc) {
        List<Shelf> shelves = new ArrayList<Shelf>();
        NodeList shelvesNodeList = doc.getElementsByTagName("shelf");

        if (shelvesNodeList != null) {

            for (int i = 0; i < shelvesNodeList.getLength(); i++) {

                NamedNodeMap map = shelvesNodeList.item(i).getAttributes();
                Node nameNode = map.getNamedItem("name");
                String name = nameNode.getNodeValue();
                Node exclusiveNode = map.getNamedItem("exclusive");
                boolean exclusive = Boolean.getBoolean(exclusiveNode.getNodeValue());
                shelves.add(new Shelf(name, exclusive));
            }
        }

        return shelves;
    }

}
