import jakarta.json.JsonObject;
import jakarta.json.spi.JsonProvider;
import jakarta.json.stream.JsonParser;
import org.junit.jupiter.api.Test;

import java.io.ByteArrayInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class ParseTest {

    /**
     * From https://gist.github.com/tomwhoiscontrary/c59f278215a5fc8b2320879d8415f3ca
     */
    @Test
    void parseThreeTimes() throws IOException {
        // JSON kindly supplied by https://jsonplaceholder.typicode.com/
        byte[] content = loadResource("comments.json");

        JsonProvider json = JsonProvider.provider();

        for (int i = 0; i < 3; i++) {
            System.out.println(i);
            try (InputStream in = new ByteArrayInputStream(content)) {
                try (JsonParser parser = json.createParser(in)) { // exhibit 1
                    JsonParser.Event firstEvent = parser.next();
                    assert firstEvent == JsonParser.Event.START_ARRAY;

                    while (parser.hasNext()) {
                        JsonParser.Event event = parser.next();
                        if (event == JsonParser.Event.START_OBJECT) {
                            JsonObject object = parser.getObject();
                            object.toString(); // exhibit 2
                        }
                    }

                    parser.close(); // exhibit 3
                }
            }
        }

        System.out.println("finished");
    }

    /**
     * From https://github.com/eclipse-ee4j/parsson/issues/25#issuecomment-915898390
     */
    @Test
    public void doubleClose() throws IOException {
        byte[] content = "[\"test\"]".getBytes();

        JsonProvider json = JsonProvider.provider();

        for (int i = 0; i < 3; i++) {
            System.out.println(i);
            try (InputStream in = new ByteArrayInputStream(content)) {
                try (JsonParser parser = json.createParser(in)) {
                    JsonParser.Event firstEvent = parser.next();
                    assertEquals(JsonParser.Event.START_ARRAY, firstEvent);
                    while (parser.hasNext()) {
                        JsonParser.Event event = parser.next();
                        if (event == JsonParser.Event.START_OBJECT) {
                            JsonObject object = parser.getObject();
                            object.toString();
                        }
                    }
                    parser.close();
                }
            }
        }

        System.out.println("finished");
    }

    @Test
    public void doubleCloseWithMoreContent() throws IOException {
        byte[] content = loadResource("comments.json");

        JsonProvider json = JsonProvider.provider();

        for (int i = 0; i < 3; i++) {
            System.out.println(i);
            try (InputStream in = new ByteArrayInputStream(content)) {
                try (JsonParser parser = json.createParser(in)) {
                    JsonParser.Event firstEvent = parser.next();
                    assertEquals(JsonParser.Event.START_ARRAY, firstEvent);
                    while (parser.hasNext()) {
                        JsonParser.Event event = parser.next();
                        if (event == JsonParser.Event.START_OBJECT) {
                            JsonObject object = parser.getObject();
                            object.toString();
                        }
                    }
                    parser.close();
                }
            }
        }

        System.out.println("finished");
    }

    private byte[] loadResource(String name) throws IOException {
        try (InputStream in = openResource(name)) {
            return in.readAllBytes();
        }
    }

    private InputStream openResource(String name) throws FileNotFoundException {
        InputStream in = ParseTest.class.getResourceAsStream(name);
        if (in == null) throw new FileNotFoundException(name);
        return in;
    }

}
