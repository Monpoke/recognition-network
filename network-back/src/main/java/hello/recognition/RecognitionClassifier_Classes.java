/*/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package hello.recognition;

import org.bytedeco.javacpp.*;

import java.util.*;
import java.util.Arrays;

import org.bytedeco.javacpp.indexer.DoubleIndexer;
import org.bytedeco.javacpp.indexer.FloatIndexer;
import org.bytedeco.javacpp.indexer.Indexer;
import org.bytedeco.javacpp.indexer.IntIndexer;

import static org.bytedeco.javacpp.opencv_core.*;
import static org.bytedeco.javacpp.opencv_features2d.drawKeypoints;
import static org.bytedeco.javacpp.opencv_features2d.drawMatches;
import static org.bytedeco.javacpp.opencv_highgui.*;
import static org.bytedeco.javacpp.opencv_imgcodecs.imread;

import static org.bytedeco.javacpp.opencv_xfeatures2d.SIFT;

public class RecognitionClassifier_Classes {
    private boolean SHOW_IMGS = false;
    private int BEST_MATCHES = 300;


    /**
     * Start the application.
     *
     * @param args
     */
    public static void main(String[] args) {

        String[] files = new String[]{
                // "church01.jpg",
                "data\\tmp\\DATASET_20171028\\images\\Coca_1.jpg", // first file is the source file, others are references
                "data\\tmp\\DATASET_20171028\\images\\Coca_2.jpg",
                "data\\tmp\\DATASET_20171028\\images\\Coca_3.jpg",
                "data\\tmp\\DATASET_20171028\\images\\Coca_4.jpg",
                "data\\tmp\\DATASET_20171028\\images\\Coca_5.jpg",
                "data\\tmp\\DATASET_20171028\\images\\Coca_6.jpg",
                "data\\tmp\\DATASET_20171028\\images\\Pepsi_1.jpg",
                "data\\tmp\\DATASET_20171028\\images\\Pepsi_2.jpg",
                "data\\tmp\\DATASET_20171028\\images\\Pepsi_3.jpg",
                "data\\tmp\\DATASET_20171028\\images\\Pepsi_4.jpg",};


        long startTime = System.nanoTime();

        System.out.println("Starting classifier...");
        new RecognitionClassifier_Classes(files);
        System.out.println("Stopping classifier...");


        long endTime = System.nanoTime();

        long duration = (endTime - startTime) / 1000000;  //divide by 1000000 to get milliseconds.

        System.out.println("duration=" + duration + "ms");
    }


    /**
     * Calling functions here.
     *
     * @param files
     */
    public RecognitionClassifier_Classes(String[] files) {


        // load lib
        loadLibraries();

        OneImage[] images = readImages(files);


        /**
         * CREATE SIFT
         */
        SIFT sift = createSIFT();

        List<String> labelsList = new ArrayList<>();
        List<ImageClassifier> classifiers = new ArrayList<>();

        for (int i = 0; i < images.length; i++) {
            System.out.println("ANALYSE for " + images[i].getFile());
            System.out.println("Classe: " + images[i].getClassName());

            analyseImage(images[i], sift);

            String clsName = images[i].getClassName();
            int idxName = labelsList.indexOf(clsName);

            if (idxName == -1) {
                labelsList.add(clsName);
                classifiers.add(new ImageClassifier(clsName));

                idxName = labelsList.indexOf(clsName);
            }

            // Add image to current classifier
            if (i > 0)
                classifiers.get(idxName).addImage(images[i]);


            // =============
            //System.out.println("DESCRIPTORS");
            analyseDescriptors(images[i], sift);

            // show keypoints2
            Mat result = new Mat();
            drawKeypoints(images[i].getImage(), images[i].getKeyPointVector(), result, new Scalar(255, 255, 255, 1), opencv_features2d.DrawMatchesFlags.DRAW_RICH_KEYPOINTS);
            showImage(images[i], result);


        }


        // PICS
        //comparePICS(images);

        // score list
        //findBestScore(images);


        comparePicToClassifiers(images[0], classifiers);

        // sort classifiers by score
        classifiers.sort((c1, c2) -> (c1.getScoreMatching() < c2.getScoreMatching() ? -1 : 1));

        // print first one
        showResult(images[0], classifiers.get(0));

    }


    /**
     * This method shows result.
     *
     * @param image
     * @param imageClassifier
     */
    private void showResult(OneImage image, ImageClassifier imageClassifier) {

        System.out.println("\n\n\n============= RESULT ================");

        System.out.println("Image: " + image.getFile());
        System.out.println("Matched with: " + imageClassifier.getClassName());
        System.out.println("% matching: " + imageClassifier.getScoreMatching());

        System.out.println("=====================================\n\n\n");

    }

    private void findBestScore(OneImage image, List<ImageClassifier> classifiers) {
    }

    /**
     * Compare with each classifier.
     *
     * @param image
     * @param classifiers
     */
    private void comparePicToClassifiers(OneImage image, List<ImageClassifier> classifiers) {
        System.out.println("===================\n");

        for (ImageClassifier imageClassifier : classifiers) {
            System.out.println("Comparing " + image.getFile() + " to classifier: " + imageClassifier.getClassName());

            float totalScore = 0.0f;

            // moyenne des distances par rapport à chaque image du classifier
            for (OneImage refImg : imageClassifier.getImages()) {
                // score each image
                comparePIC(image, refImg);
                totalScore += refImg.getScore();
            }

            // Average of scores by classifiers
            totalScore /= imageClassifier.getImages().size();

            imageClassifier.setScoreMatching(totalScore);
            System.out.println("Total score for " + imageClassifier.getClassName() + " :" + totalScore);
        }


    }


    /**
     * Compare two images
     *
     * @param src
     * @param dst
     */
    private void comparePIC(OneImage src, OneImage dst) {

        // 0 is always base
        opencv_features2d.BFMatcher matcher = new opencv_features2d.BFMatcher(NORM_L1, false);

        // loop
        // entre 1 et 0
        DMatchVector matches = new DMatchVector();
        matcher.match(src.getDescriptors(), dst.getDescriptors(), matches);

        //System.out.println("Matches between " + src.getFile() + "& " + dst.getFile() + ": " + matches.size());

        DMatchVector bestMatches = selectBest(matches, BEST_MATCHES);
        //System.out.println("Best matches between " + src.getFile() + "& " + dst.getFile() + ": " + bestMatches.size());

        // is it a good match?
        //isGoodMatch(bestMatches);
        float score = scoreImage(src, dst, bestMatches);
        dst.setScore(score);

        Mat matchImage = new Mat();
        drawMatches(src.getImage(), src.getKeyPointVector(), dst.getImage(), dst.getKeyPointVector(), bestMatches, matchImage);
        showImage(dst, matchImage);


    }


    /**
     * Sum of all distances.
     *
     * @param image
     * @param image1
     * @param bestMatches
     * @return
     */
    private float scoreImage(OneImage image, OneImage image1, DMatchVector bestMatches) {

        float sum = 0.0f;
        for (int i = 0; i < bestMatches.size(); i++) {
            sum += bestMatches.get(i).distance();
        }

        sum /= bestMatches.size();

        return sum;
    }

    /**
     * Select best points.
     *
     * @param matches
     * @param numberToSelect
     * @return
     */
    DMatchVector selectBest(DMatchVector matches, int numberToSelect) {
        DMatch[] sorted = toArray(matches);
        java.util.Arrays.sort(sorted, (a, b) -> {
            return a.lessThan(b) ? -1 : 1;
        });
        DMatch[] best = java.util.Arrays.copyOf(sorted, numberToSelect);
        return new DMatchVector(best);
    }

    DMatch[] toArray(DMatchVector matches) {
        assert matches.size() <= Integer.MAX_VALUE;
        int n = (int) matches.size();
        //	Convert	keyPoints	to	Scala	sequence
        DMatch[] result = new DMatch[n];
        for (int i = 0; i < n; i++) {
            result[i] = new DMatch(matches.get(i));
        }
        return result;
    }

    /**
     * Create SIFT with parameters...
     *
     * @return
     */
    private static SIFT sift;

    private SIFT createSIFT() {
        if (sift != null)
            return sift;

        int nFeatures = 0;
        int nOctaveLayers = 3;
        double contrastThreshold = 0.03;
        int edgeThreshold = 10;
        double sigma = 1.6;

        sift = SIFT.create(nFeatures, nOctaveLayers, contrastThreshold, edgeThreshold, sigma);
        return sift;
    }

    /**
     * Returns keypoints for one image.
     *
     * @param oneImage
     * @param sift
     * @return
     */
    private void analyseImage(OneImage oneImage, SIFT sift) {
        Mat image = oneImage.getImage();
        System.out.println("Analyse img[" + image.size().width() + "-" + image.size().height() + "]");
        if (image.size().height() == 0 || image.size().width() == 0) {
            exitError("One image is null... -> " + oneImage.getFile());
        }

        sift.detect(image, oneImage.getKeyPointVector());

    }

    //Detect	SURF	features	and	compute	descriptors	for	both	images
    private void analyseDescriptors(OneImage image, SIFT sift) {
        sift.detect(image.getImage(), image.getKeyPointVector());
        sift.compute(image.getImage(), image.getKeyPointVector(), image.getDescriptors());
    }

    /**
     * Exit on error.
     *
     * @param s
     */
    private void exitError(String s) {
        System.err.println(s);
        System.exit(-1);
    }

    /**
     * Load libraries
     */
    private void loadLibraries() {
        Loader.load(opencv_calib3d.class);
        Loader.load(opencv_shape.class);

    }

    /**
     * Read images.
     *
     * @param files
     * @return
     */
    private OneImage[] readImages(String[] files) {
        OneImage[] toReturn = new OneImage[files.length];

        for (int i = 0; i < files.length; i++) {
            toReturn[i] = new OneImage(files[i], imread(files[i]));
        }

        return toReturn;
    }

    /**
     * Shoz mg
     *
     * @param oneImage
     */
    private void showImage(OneImage oneImage, Mat img) {
        if (SHOW_IMGS != true) {
            return;
        }
        namedWindow(oneImage.getFile(), WINDOW_AUTOSIZE);    //	Create	a	window	for	display.
        imshow(oneImage.getFile(), img);    //	Show	our	image	inside	it.
        waitKey(0);    //	Wait	for	a	keystroke	in	the	window
    }

}
