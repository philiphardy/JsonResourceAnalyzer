import com.fasterxml.jackson.core.JsonEncoding;
import com.fasterxml.jackson.core.JsonFactory;
import com.fasterxml.jackson.core.JsonGenerator;
import java.io.File;
import java.io.IOException;

public class JsonCreator {
  public static void main(String[] args) throws IOException {
    createLargeJsonFile();
    createReallyLargeJsonFile();
  }

  static void createLargeJsonFile() throws IOException {
    createJsonFile(".\\src\\test\\resources\\large.json", 1000);
  }

  static void createJsonFile(String filePath, int objectCount) throws IOException {
    JsonFactory jsonFactory = new JsonFactory();
    JsonGenerator jsonGenerator = jsonFactory.createGenerator(new File(filePath), JsonEncoding.UTF8);

    // create start of array
    jsonGenerator.writeStartArray();

    for (int i = 0; i < objectCount; i++) {
      jsonGenerator.writeStartObject();

      // write path
      jsonGenerator.writeStringField("path", "path_value_" + (i + 1));

      // write size
      jsonGenerator.writeNumberField("size", 290);

      // write path
      jsonGenerator.writeStringField("url", "http://www.google.com");

      jsonGenerator.writeEndObject();
    }

    // create end of array
    jsonGenerator.writeEndArray();
    jsonGenerator.close();
  }

  static void createReallyLargeJsonFile() throws IOException {
    createJsonFile(".\\src\\test\\resources\\really-large.json", 500000);
  }
}
