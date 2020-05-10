package fractal;

import java.awt.image.BufferedImage;
import java.awt.image.ColorModel;
import java.awt.image.WritableRaster;

import org.apache.commons.math3.complex.Complex;

import timer.Timer;

public class FractalCalculator implements Runnable {
    private WritableRaster raster;
    private ColorModel colorModel;
    private int imageWidth;
    private int imageHeight;
    private int threadNumber;
    private int numberOfThreads;
    private int chunksCount;
    private int chunkSize;
    private double maxRealValue;
    private double maxImaginaryValue;
    private double imaginaryValueOffset;
    private double realValueOffset;
    private int[] colorPalette;
    private int maxSteps;
    private boolean quietMode;
    private Timer timer;

    public FractalCalculator(BufferedImage fractalImage, int threadNumber, int numberOfThreads, double minRealValue,
            double maxRealValue, double minImaginaryValue, double maxImaginaryValue, boolean quietMode,
            int[] colorPalette, int chunkSize, int chunksCount) {
        this.raster = fractalImage.getRaster();
        this.colorModel = fractalImage.getColorModel();
        this.imageHeight = fractalImage.getHeight();
        this.imageWidth = fractalImage.getWidth();
        this.threadNumber = threadNumber;
        this.numberOfThreads = numberOfThreads;
        this.chunkSize = chunkSize;
        this.chunksCount = chunksCount;
        this.colorPalette = colorPalette;
        this.maxSteps = colorPalette.length;
        this.maxRealValue = maxRealValue;
        this.maxImaginaryValue = maxImaginaryValue;
        this.realValueOffset = (maxRealValue - minRealValue) / imageWidth;
        this.imaginaryValueOffset = (maxImaginaryValue - minImaginaryValue) / imageHeight;
        this.quietMode = quietMode;
        this.timer = new Timer();
    }

    @Override
    public void run() {
        startTimer();

        for (int chunk = threadNumber + 1; chunk <= chunksCount; chunk += numberOfThreads) {
            calculateChunk(chunk);
        }

        stopTimer();
    }

 
    private void calculateChunk(int chunk) {
        for (int i = (chunk - 1) * chunkSize; i < chunk * chunkSize && i < imageHeight; i++) {
            double imaginary = maxImaginaryValue - i * imaginaryValueOffset;

            for (int j = 0; j < imageWidth; j++) {

                double real = j * realValueOffset - maxRealValue;

                int steps = calculateSteps(new Complex(real, imaginary));
                
                raster.setDataElements(j, i, colorModel.getDataElements(colorPalette[steps % maxSteps], null));
            }
        }
    }

    private int calculateSteps(Complex point) {
        Complex currentPoint = new Complex(0.0, 0.0);
        int steps = 0;

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
}
