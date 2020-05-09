package fractal;

import java.awt.image.BufferedImage;

import org.apache.commons.math3.complex.Complex;

import timer.Timer;

public class FractalCalculator implements Runnable {
    private final double maxRealValue;
    private final double realValuesRange;
    private final double maxImaginaryValue;
    private final double imaginaryValuesRange;
    private final int threadNumber;
    private final int numberOfThreads;
    private final boolean quietMode;
    private final int[] colorPalette;
    private final int chunkSize;
    private final int chunksCount;

    private BufferedImage fractalImage;
    private Timer timer;

    public FractalCalculator(BufferedImage fractalImage, int threadNumber, int numberOfThreads, double minRealValue,
            double maxRealValue, double minImaginaryValue, double maxImaginaryValue, boolean quietMode,
            int[] colorPalette, int chunkSize, int chunksCount) {
        this.fractalImage = fractalImage;
        this.threadNumber = threadNumber;
        this.numberOfThreads = numberOfThreads;
        this.maxRealValue = maxRealValue;
        realValuesRange = Math.abs(maxRealValue - minRealValue);
        this.maxImaginaryValue = maxImaginaryValue;
        imaginaryValuesRange = Math.abs(maxImaginaryValue - minImaginaryValue);
        this.quietMode = quietMode;
        this.colorPalette = colorPalette;
        this.chunkSize = chunkSize;
        this.chunksCount = chunksCount;
        timer = new Timer();
    }

    @Override
    public void run() {
        startTimer();

        final int height = fractalImage.getHeight();
        final int width = fractalImage.getWidth();

        for (int chunk = 1; chunk <= chunksCount; chunk++) {
            if (chunk % numberOfThreads == threadNumber) {
                for (int i = (chunk - 1) * chunkSize; i < chunk * chunkSize && i < height ; i++) {
                    double imaginary = (i - height / maxImaginaryValue) * (imaginaryValuesRange / height);

                    for (int j = 0; j < width; j++) {
                        double real = (j - width / maxRealValue) * (realValuesRange / width);

                        int steps = calculateSteps(new Complex(real, imaginary));

                        fractalImage.setRGB(j, i, colorPalette[steps % colorPalette.length]);
                    }
                }
            }
        }

        stopTimer();
    }

    private void startTimer() {
        if (!quietMode) {
            timer.start();
            System.out.println("Thread-" + threadNumber + " started.");
        }
    }

    private void stopTimer() {
        if (!quietMode) {
            timer.stop();
            System.out.println("Thread-" + threadNumber + " stopped.");
            System.out.print("Thread-" + threadNumber + " execution time: ");
            timer.printResult();
        }
    }

    private int calculateSteps(Complex point) {
        final int maxSteps = colorPalette.length;
        int steps = 0;

        Complex currentPoint = new Complex(0.0, 0.0);

        while (steps < maxSteps) {
            currentPoint = calculateNextPoint(currentPoint, point);

            if (isOutOfMandelbrotSet(currentPoint)) {
                return steps;
            }

            steps++;
        }

        return steps;
    }

    private Complex calculateNextPoint(Complex currentPoint, Complex startingPoint) {
        return currentPoint.cos().multiply(startingPoint);
    }

    private boolean isOutOfMandelbrotSet(Complex point) {
        return point.isInfinite() || point.isNaN();
    }

}
