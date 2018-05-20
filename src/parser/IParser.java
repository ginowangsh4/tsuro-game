package tsuro.parser;
import org.w3c.dom.Document;

public interface IParser<T> {
    /**
     * Construct XML document representation of object of type T
     */
    Document buildXML(T obj);

    /**
     * Deconstruct XML document representation of object of type T
     */
    T fromXML(Document doc);
}
