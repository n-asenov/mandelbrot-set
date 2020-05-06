package fractal;

import org.apache.commons.cli.ParseException;

import input.ArgumentsParser;

public class Main {
    public static void main(String[] args) {
        ArgumentsParser parser = new ArgumentsParser();
        Fractal fractal;
        
        try {
            fractal = new Fractal(parser.parse(args));
        } catch (ParseException e) {
            fractal = new Fractal();
        }
        
        fractal.generate();
    }
}
