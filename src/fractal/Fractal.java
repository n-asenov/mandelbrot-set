package fractal;

import java.awt.Color;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;

import org.apache.commons.cli.CommandLine;

import timer.Timer;

public class Fractal {
    public static final int DEFAULT_IMAGE_WIDTH = 640;
    public static final int DEFAULT_IMAGE_HEIGHT = 480;
    public static final double DEFAULT_MIN_REAL_VALUE = -2.0;
    public static final double DEFAULT_MAX_REAL_VALUE = 2.0;
    public static final double DEFAULT_MIN_IMAGINARY_VALUE = -2.0;
    public static final double DEFAULT_MAX_IMAGINARY_VALUE = 2.0;
    public static final int DEFAULT_NUMBER_OF_THREADS = 1;
    public static final int DEFAULT_GRANULARITY = 1;
    public static final String DEFAULT_OUTPUT_FILE_NAME = "zad18.png";
    public static final boolean DEFAULT_QUIET_MODE = false;

    private int imageWidth = DEFAULT_IMAGE_WIDTH;
    private int imageHeight = DEFAULT_IMAGE_HEIGHT;
    private double minRealValue = DEFAULT_MIN_REAL_VALUE;
    private double maxRealValue = DEFAULT_MAX_REAL_VALUE;
    private double minImaginaryValue = DEFAULT_MIN_IMAGINARY_VALUE;
    private double maxImaginaryValue = DEFAULT_MAX_IMAGINARY_VALUE;
    private int numberOfThreads = DEFAULT_NUMBER_OF_THREADS;
    private int granularity = DEFAULT_GRANULARITY;
    private String outputFileName = DEFAULT_OUTPUT_FILE_NAME;
    private boolean quietMode = DEFAULT_QUIET_MODE;

    public Fractal() {

    }

    public Fractal(CommandLine commandLine) {
        setImageSize(commandLine.getOptionValues("size"));
        setComplexPlaneRestrictions(commandLine.getOptionValues("rect"));
        setNumberOfThreads(commandLine.getOptionValue("tasks"));
        setGranularity(commandLine.getOptionValue("granularity"));
        setOutputFileName(commandLine.getOptionValue("output"));
        setQuietMode(commandLine);
    }

    public void generate() {
        BufferedImage fractalImage = new BufferedImage(imageWidth, imageHeight, BufferedImage.TYPE_INT_RGB);
        Thread[] threads = new Thread[numberOfThreads];
        int[] colorPalette = generateColorPalette();

        int chunksCount = granularity * numberOfThreads;
        int chunkSize = imageHeight / chunksCount;

        Timer timer = new Timer();
        timer.start();

        for (int threadNumber = 0; threadNumber < numberOfThreads; threadNumber++) {
            threads[threadNumber] = new Thread(
                    new FractalCalculator(fractalImage, threadNumber, numberOfThreads, minRealValue, maxRealValue,
                            minImaginaryValue, maxImaginaryValue, quietMode, colorPalette, chunkSize, chunksCount));
            threads[threadNumber].start();
        }

        waitThreadsToFinish(threads);

        timer.stop();
        printExecutionTime(timer);

        saveFractalImageInFile(fractalImage, outputFileName);
    }

    private void setImageSize(String[] imageSizes) {
        if (imageSizes != null) {
            imageWidth = Integer.parseInt(imageSizes[0]);
            imageHeight = Integer.parseInt(imageSizes[1]);
        }
    }

    private void setComplexPlaneRestrictions(String[] restrictions) {
        if (restrictions != null) {
            minRealValue = Double.parseDouble(restrictions[0]);
            maxRealValue = Double.parseDouble(restrictions[1]);
            minImaginaryValue = Double.parseDouble(restrictions[2]);
            maxImaginaryValue = Double.parseDouble(restrictions[3]);
        }
    }

    private void setNumberOfThreads(String threadsCount) {
        if (threadsCount != null) {
            numberOfThreads = Integer.parseInt(threadsCount);
        }
    }

    private void setGranularity(String granularitySize) {
        if (granularitySize != null) {
            granularity = Integer.parseInt(granularitySize);
        }
    }

    private void setOutputFileName(String name) {
        if (name != null) {
            outputFileName = name;
        }
    }

    private void setQuietMode(CommandLine commandLine) {
        quietMode = commandLine.hasOption("quiet");
    }

    private int[] generateColorPalette() {
        int maxIterations = 256;

        int[] colorPalette = new int[maxIterations];

        for (int i = 0; i < maxIterations; i++) {
            colorPalette[i] = Color.HSBtoRGB(i / 256.0f, 1, i / (i + 8.0f));
        }

        return colorPalette;
    }

    private void waitThreadsToFinish(Thread[] threads) {
        for (int index = 0; index < numberOfThreads; index++) {
            try {
                threads[index].join();
            } catch (InterruptedException e) {
                System.out.println("Could not wait threads to join: " + e);
            }
        }
    }

    private void printExecutionTime(Timer timer) {
        if (!quietMode) {
            System.out.println("Threads used in current run: " + numberOfThreads);
        }

        System.out.print("Total execution time for current run: ");
        timer.printResult();
    }

    private void saveFractalImageInFile(BufferedImage fractalImage, String fileName) {
        try {
            ImageIO.write(fractalImage, "png", new File(fileName));
        } catch (IOException e) {
            System.out.println("Could not save fractal image in file: " + e);
        }
    }
}
