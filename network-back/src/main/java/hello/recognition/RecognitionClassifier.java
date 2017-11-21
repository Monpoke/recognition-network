/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package hello.recognition;

import hello.aerospike.domain.RefImage;
import hello.dao.RefImageDAO;
import hello.service.ImagesService;
import hello.utils.GZIPCompression;
import org.bytedeco.javacpp.BytePointer;
import org.bytedeco.javacpp.opencv_xfeatures2d;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import static org.bytedeco.javacpp.opencv_core.FileStorage;
import static org.bytedeco.javacpp.opencv_core.Mat;
import static org.bytedeco.javacpp.opencv_imgcodecs.imread;

/**
 * @author lionel
 */
public class RecognitionClassifier {

    private static final Logger log = LoggerFactory.getLogger(RecognitionClassifier.class);

    private final String basedir;

    private final List<String> filesList;

    private final List<RefImageDAO> listImages;


    // Can't be autowired, coz' it is not a component
    private ImagesService imagesService;


    // OPEN CV STUFF
    private opencv_xfeatures2d.SIFT SIFT;


    /**
     * @param basedir
     * @param filesList
     * @param imagesService
     */
    public RecognitionClassifier(String basedir, List<String> filesList, ImagesService imagesService) {
        this.basedir = basedir;
        this.filesList = filesList;
        this.imagesService = imagesService;
        this.listImages = new ArrayList<>();
    }

    /**
     * Loads all images.
     *
     * @throws Exception
     */
    public void start() throws Exception {

        createSIFT();

        loadImages();

        saveClassifier();
    }


    /**
     * Create SIFT from openCV
     */
    private void createSIFT() {
        int nFeatures = 0;
        int nOctaveLayers = 3;
        double contrastThreshold = 0.03;
        int edgeThreshold = 10;
        double sigma = 1.6;
        this.SIFT = opencv_xfeatures2d.SIFT.create(nFeatures, nOctaveLayers, contrastThreshold, edgeThreshold, sigma);
    }

    /**
     *
     */
    private void saveClassifier() {

        for (RefImageDAO refImageDAO :
                listImages) {


            RefImage refImage = new RefImage();
            refImage.setClassifier(refImageDAO.classifier());
            refImage.setName(refImageDAO.getFilename());


            // GET POINTS
            FileStorage fs = new FileStorage("./kp.xml", FileStorage.WRITE|FileStorage.MEMORY);
            org.bytedeco.javacpp.opencv_core.write(fs, "base", refImageDAO.getBaseImage());
            org.bytedeco.javacpp.opencv_core.write(fs, "desc", refImageDAO.getDescriptors());
            org.bytedeco.javacpp.opencv_core.write(fs, "kp", refImageDAO.getKeyPointVectors());

            BytePointer bytePointer = fs.releaseAndGetString();

            refImage.setMetadata(bytePointer.getString());

            imagesService.finalize(refImage);
            imagesService.save(refImage);

        }


    }


    /**
     * Loads all images.
     *
     * @throws Exception
     */
    private void loadImages() throws Exception {
        for (String file : filesList) {
            String fullpath = this.basedir + file;
            Mat imread = imread(fullpath);

            if (imread.cols() == 0 || imread.rows() == 0) {
                throw new Exception("Image not existing... " + fullpath);
            }

            log.info("Loaded: " + fullpath);

            // register the image in object
            RefImageDAO refImageDAO = new RefImageDAO();
            refImageDAO.setBaseImage(imread);
            refImageDAO.setFilename(file);

            analyseImage(refImageDAO);
            log.info("Image analysed...");

            // set to list
            listImages.add(refImageDAO);


        }

    }

    // detect interests points
    private void analyseImage(RefImageDAO refImageDAO) {

        SIFT.detect(refImageDAO.getBaseImage(), refImageDAO.getKeyPointVectors());
        SIFT.compute(refImageDAO.getBaseImage(), refImageDAO.getKeyPointVectors(), refImageDAO.getDescriptors());

    }


}
