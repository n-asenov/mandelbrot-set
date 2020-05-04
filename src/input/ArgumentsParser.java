package input;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.DefaultParser;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;

public class ArgumentsParser {
    private CommandLineParser parser;
    private Options options;
    
    public ArgumentsParser() {
        parser = new DefaultParser();
        options = new Options();
        addOptions();
    }
    
    public CommandLine parse(String[] arguments) throws ParseException {
        return parser.parse(options, arguments);
    }

    private void addOptions() {
        options.addOption(getImageSizeOption());
        options.addOption(getComplexPlaneRestrictionOption());
        options.addOption(getThreadsCountOption());
        options.addOption(getGranularityOption());
        options.addOption(getOutputFileNameOption());
        options.addOption(getQuietOption());
    }
    
    private Option getImageSizeOption() {
        return Option.builder("s")
                .hasArg(true)
                .numberOfArgs(2)
                .valueSeparator('x')
                .desc("set image width and height")
                .longOpt("size")
                .build();
    }
    
    private Option getComplexPlaneRestrictionOption() {
        return Option.builder("r")
                .hasArg(true)
                .numberOfArgs(4)
                .valueSeparator(':')
                .desc("set complex plane restrictions")
                .longOpt("rect")
                .build();
    }
    
    private Option getThreadsCountOption() {
        return Option.builder("t")
                .hasArg(true)
                .numberOfArgs(1)
                .desc("set the maximum number of threads the program will use")
                .longOpt("tasks")
                .build();
    }
    
    private Option getGranularityOption() {
        return Option.builder("g")
                .hasArg(true)
                .numberOfArgs(1)
                .desc("set granularity")
                .longOpt("granularity")
                .build();
    }
    
    private Option getOutputFileNameOption() {
        return Option.builder("o")
                .hasArg(true)
                .numberOfArgs(1)
                .desc("set output file name")
                .longOpt("output")
                .build();
    }
    
    private Option getQuietOption() {
        return Option.builder("q")
                .hasArg(false)
                .desc("quiet mode")
                .longOpt("quiet")
                .build();
    }
}
