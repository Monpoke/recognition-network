/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package hello.recognition;

import org.bytedeco.javacpp.*;

import java.util.*;
import java.util.Arrays;

import static org.bytedeco.javacpp.opencv_core.*;
import static org.bytedeco.javacpp.opencv_features2d.drawKeypoints;
import static org.bytedeco.javacpp.opencv_features2d.drawMatches;
import static org.bytedeco.javacpp.opencv_highgui.*;
import static org.bytedeco.javacpp.opencv_imgcodecs.imread;

import static org.bytedeco.javacpp.opencv_xfeatures2d.SIFT;

public class RecognitionClassifier_2 {

    private boolean SHOW_IMGS = false;

    public static void main(String[] args) {
        RecognitionClassifier_2 tp4 = new RecognitionClassifier_2(100);
    }

    public RecognitionClassifier_2(int BEST_MATCHES) {

        String[] files = new String[]{
            // "church01.jpg",
            "data\\tmp\\DATASET_20171028\\images\\Coca_1.jpg",
            "data\\tmp\\DATASET_20171028\\images\\Coca_2.jpg",
            "data\\tmp\\DATASET_20171028\\images\\Coca_3.jpg",
            "data\\tmp\\DATASET_20171028\\images\\Coca_4.jpg",
            "data\\tmp\\DATASET_20171028\\images\\Coca_5.jpg",
            "data\\tmp\\DATASET_20171028\\images\\Coca_6.jpg",
            "data\\tmp\\DATASET_20171028\\images\\Pepsi_1.jpg",
            "data\\tmp\\DATASET_20171028\\images\\Pepsi_2.jpg",
            "data\\tmp\\DATASET_20171028\\images\\Pepsi_3.jpg",
            "data\\tmp\\DATASET_20171028\\images\\Pepsi_4.jpg",
        };

        // load lib
        loadLibraries();

        OneImage[] images = readImages(files);

        /**
         * CREATE SIFT
         */
        SIFT sift = createSIFT();

        for (int i = 0; i < images.length; i++) {
            System.out.println("ANALYSE for " + images[i].getFile());
            System.out.println("Classname: "+ images[i].getClassName());
            analyseImage(images[i], sift);

            // =============
            System.out.println("DESCRIPTORS");
            analyseDescriptors(images[i], sift);

            // show keypoints
            Mat result = new Mat();
            drawKeypoints(images[i].getImage(), images[i].getKeyPointVector(), result, new Scalar(255, 255, 255, 1), opencv_features2d.DrawMatchesFlags.DRAW_RICH_KEYPOINTS);
            showImage(images[i], result);

        }

        // PICS
        comparePICS(images);

        // score list
        findBestScore(images);

        
        
        
        
        
    }

    /**
     * @param
     */
    private void findBestScore(OneImage[] images) {

        float best_score = -1;
        OneImage bestClose = null;

        for (int i = 0; i < images.length; i++) {

            OneImage img = images[i];
            if (img == null) {
                continue;
            } else if (img.getScore() == -1) {
                System.out.println("Skipping " + img.getFile() + " => score -1");
                continue;
            }

            float score = img.getScore();

            // set best one
            if (best_score == -1 || score < best_score) {
                if (best_score == -1) {
                    System.out.println("Setting " + img.getFile() + " as better... first");
                } else {
                    System.out.println("Setting " + img.getFile() + "  better than " + bestClose.getFile() + " [" + best_score + "-" + score + "]");
                }

                best_score = score;
                bestClose = img;

            } else {
                System.out.println("No,  " + img.getFile() + " isn't better than " + bestClose.getFile() + " [" + best_score + "-" + score + "]");

            }

        }

        if (best_score > -1) {
            System.out.println("ONE MATCH");
            System.out.println("SCORE:" + best_score);
            System.out.println("Matched with: " + bestClose.getFile());
        } else {
            System.out.println("No match found...");
        }

    }

    private void comparePICS(OneImage[] images) {

        // 0 is always base
        opencv_features2d.BFMatcher matcher = new opencv_features2d.BFMatcher(NORM_L1, false);

        // loop
        for (int i = 1; i < images.length; i++) {

            // entre 1 et 0
            DMatchVector matches = new DMatchVector();
            matcher.match(images[0].getDescriptors(), images[i].getDescriptors(), matches);

            System.out.println("Matches between " + images[0].getFile() + "& " + images[i].getFile() + ": " + matches.size());

            DMatchVector bestMatches = selectBest(matches, 100);
            System.out.println("Best matches between " + images[0].getFile() + "& " + images[i].getFile() + ": " + bestMatches.size());

            // is it a good match?
            //isGoodMatch(bestMatches);
            float score = scoreImage(images[0], images[i], bestMatches);
            images[i].setScore(score);

            Mat matchImage = new Mat();
            drawMatches(images[0].getImage(), images[0].getKeyPointVector(), images[i].getImage(), images[i].getKeyPointVector(), bestMatches, matchImage);
            showImage(images[i], matchImage);

        }

    }

    /**
     * @param bestMatches
     */
    private void isGoodMatch(DMatchVector bestMatches) {
        System.out.println("IS GOOD MATCH?");

        // 1. sort by distance
        List<DMatch> sorted = new ArrayList<>();

        // pour tous les points
        for (int i = 0; i < bestMatches.size(); i++) {
            sorted.add(bestMatches.get(i));
            sorted.sort((o1, o2) -> o1.distance() < o2.distance() ? -1 : 1);
        }

        int nbTotalPoints = sorted.size();
        int wellMatched = 0;

        // 2. for each points
        for (int i = 0; i < sorted.size() - 1; i++) {
            DMatch p1 = sorted.get(i);
            DMatch p2 = sorted.get(i + 1);

            // System.out.println("p1:" + p1.distance());
            // System.out.println("p2:" + p2.distance());
            if (p1.distance() > (0.6f * p2.distance())) {
                //System.out.println("Well matched!");
                wellMatched++;
            } else {
                //System.out.println("Not matched...");
            }
        }

        float pcent = (1.0f * wellMatched / nbTotalPoints) * 100;
        System.out.println("Matched at " + pcent + "%");

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

        return sum;
    }

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
     * Create SIFT
     * @return
     */
    private SIFT createSIFT() {
        int nFeatures = 0;
        int nOctaveLayers = 3;
        double contrastThreshold = 0.03;
        int edgeThreshold = 10;
        double sigma = 1.6;
        return SIFT.create(nFeatures, nOctaveLayers, contrastThreshold, edgeThreshold, sigma);
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
            exitError("One image is null...");
        }

        System.out.println("SIFT starting...");
        sift.detect(image, oneImage.getKeyPointVector());
        System.out.println("SIFT done...");

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
    private void showImage(OneImage oneImage) {
        showImage(oneImage, oneImage.getImage());
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
