package jsonresourceanalyzer;

import jsonresourceanalyzer.json.JsonReader;

public class JsonResourceAnalyzer {

  public static void main(String[] args) {
    ArgParser argParser = new ArgParser(args);

    JsonReader jsonReader = new JsonReader(argParser);
    jsonReader.readFile();
  }
}
