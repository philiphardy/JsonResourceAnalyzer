package jsonresourceanalyzer;

import static java.util.Arrays.stream;

import jsonresourceanalyzer.constants.ErrorMessages;
import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;
import jsonresourceanalyzer.enums.ErrorCode;
import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.DefaultParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.OptionGroup;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;

public class ArgParser {

  private static final String FILE_ARG = "f";
  private static final String FILE_ARG_LONG = "file";
  private static final String FILE_ARG_DESC = "File path to array of JSON objects.";
  private static final String HELP_ARG = "h";
  private static final String HELP_ARG_LONG = "help";
  private static final String PROGRAM_NAME = "JsonResourceAnalyzer";
  private static final String OUTPUT_ARG = "o";
  private static final String OUTPUT_ARG_LONG = "out";
  private static final String OUTPUT_ARG_DESC = "Output file path.";
  private static final String URL_ARG = "u";
  private static final String URL_ARG_LONG = "url";
  private static final String URL_ARG_DESC = "URL to array of JSON objects.";

  private File file;
  private File outputFile;
  private URL url;

  public ArgParser(String[] args) {
    DefaultParser parser = new DefaultParser();
    HelpFormatter helpFormatter = new HelpFormatter();
    Options options = createOptions();

    try {
      CommandLine commandLine = parser.parse(options, args, true);

      if (hasHelpArg(args)) {
        // print usage
        helpFormatter.printHelp(PROGRAM_NAME, options, true);
        System.exit(0);
      }

      // get file path and do some validation on the file
      if (commandLine.getOptionValue(FILE_ARG) != null) {
        setFile(commandLine.getOptionValue(FILE_ARG));

      } else {
        // create the URL
        setUrl(commandLine.getOptionValue(URL_ARG));
      }

      setOutputFile(commandLine.getOptionValue(OUTPUT_ARG));

    } catch (ParseException parseException) {
      if (!hasHelpArg(args)) {
        System.err.println(parseException.getMessage());
      }

      // print usage
      helpFormatter.printHelp(PROGRAM_NAME, options, true);

      System.exit(ErrorCode.INVALID_ARGUMENTS.getValue());
    }
  }

  private Options createOptions() {
    // create a group of mutually exclusive options
    OptionGroup optionGroup = new OptionGroup()
        .addOption(
            Option
                .builder(FILE_ARG)
                .longOpt(FILE_ARG_LONG)
                .desc(FILE_ARG_DESC)
                .hasArg()
                .build()
        )
        .addOption(
            Option.builder(URL_ARG)
                .longOpt(URL_ARG_LONG)
                .desc(URL_ARG_DESC)
                .hasArg()
                .build()
        );

    optionGroup.setRequired(true);

    return new Options()
        .addOptionGroup(optionGroup)
        .addOption(
            Option.builder(OUTPUT_ARG)
                .longOpt(OUTPUT_ARG_LONG)
                .desc(OUTPUT_ARG_DESC)
                .hasArg()
                .required()
                .build()
        )
        .addOption(
            Option.builder(HELP_ARG)
                .longOpt(HELP_ARG_LONG)
                .hasArg(false)
                .build()
        );
  }

  public File getFile() {
    return file;
  }

  public File getOutputFile() {
    return outputFile;
  }

  public URL getUrl() {
    return url;
  }

  private boolean hasHelpArg(String[] args) {
    if (args == null) {
      return false;
    }
    return stream(args).anyMatch(arg -> "-h".equals(arg) || "--help".equals(arg));
  }

  private void setFile(String fileArg) {
    file = new File(fileArg);
    if (!file.exists() || !file.isFile() || !file.canRead()) {
      System.err.println(ErrorMessages.INVALID_FILE);
      System.exit(ErrorCode.INVALID_FILE.getValue());
    }
  }

  private void setOutputFile(String outputArg) {
    outputFile = new File(outputArg);
    if (outputFile.exists() && !outputFile.canWrite()) {
      System.err.println(ErrorMessages.INVALID_OUTPUT_FILE);
      System.exit(ErrorCode.INVALID_OUTPUT_FILE.getValue());
    }
  }

  private void setUrl(String urlArg) {
    try {
      url = new URL(urlArg);
    } catch (MalformedURLException ex) {
      System.err.println(String.format(ErrorMessages.INVALID_URL, urlArg));
      System.exit(ErrorCode.INVALID_URL.getValue());
    }
  }
}
