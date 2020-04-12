package jsonresourceanalyzer;

import jsonresourceanalyzer.concurrency.WorkDispatcher;
import jsonresourceanalyzer.json.InputJsonObjectValidator;
import jsonresourceanalyzer.json.JsonReader;
import jsonresourceanalyzer.json.JsonWriter;

public class JsonResourceAnalyzer {

  public static void main(String[] args) {
    final ArgParser argParser = new ArgParser(args);

    final JsonWriter jsonWriter = new JsonWriter(argParser);
    final WorkDispatcher workDispatcher = new WorkDispatcher();

    final JsonReader jsonReader = new JsonReader(argParser);
    jsonReader
        .onReadStart(jsonWriter::startFile)
        .onObjectRead(inputJsonObject -> {
          // perform validation and writing to the output json stream on a worker thread
          workDispatcher.dispatch(() -> {
            // validate the json object
            InputJsonObjectValidator.validateJsonObject(inputJsonObject);

            // write the json object to the output stream
            jsonWriter.writeObject(inputJsonObject);
          });
        })
        .onReadComplete(() -> {
          // wait for all worker threads in the worker pool finish before closing writing the end of the output JSON
          // and closing the output stream
          workDispatcher.waitForWorkToComplete();
          jsonWriter.endFile();
        })
        .readFile();
  }
}
