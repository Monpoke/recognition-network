package hello.recognition;

import hello.aerospike.domain.RefImage;
import hello.aerospike.domain.RefImageMetadata;
import hello.dao.RefImageDAO;
import org.bytedeco.javacpp.BytePointer;
import org.bytedeco.javacpp.indexer.UByteRawIndexer;
import org.bytedeco.javacpp.opencv_core;
import org.bytedeco.javacpp.opencv_core.KeyPointVector;
import org.bytedeco.javacpp.opencv_core.Mat;
import org.bytedeco.javacpp.opencv_xfeatures2d.SIFT;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static org.bytedeco.javacpp.opencv_imgcodecs.imread;
import static org.bytedeco.javacpp.opencv_imgcodecs.imreadmulti;

/**
 * Contient tout le process d'analyse de l'image.
 */
public abstract class ImageAnalyser {

    private static final Logger log = LoggerFactory.getLogger(ImageAnalyser.class);
    private static SIFT sift;


    static {
        sift = createSIFT();
    }


    /**
     * Get features of image.
     *
     * @param path image path
     * @return Image with extracted features
     * @throws Exception
     */
    public static RefImageDAO analyseImage(String path) throws Exception {
        String fullpath = path;

        Mat img = imread(fullpath);
        if (img.cols() == 0 || img.rows() == 0) {
            throw new Exception("Image not existing... " + fullpath);
        }

        log.info("Loaded: " + fullpath);


        // register the image in object
        RefImageDAO refImageDAO = new RefImageDAO();
        refImageDAO.setBaseImage(img);
        refImageDAO.setFilename(path);

        extractFeatures(refImageDAO);
        log.info("Image analysed...");


        return refImageDAO;
    }


    /**
     * Converts an image to ref which can be saved in DB.
     *
     * @param refImageDAO
     * @return
     */
    public static RefImage convertImageToRef(RefImageDAO refImageDAO) {
        RefImage refImage = new RefImage();
        refImage.setClassifier(refImageDAO.classifier());
        refImage.setName(refImageDAO.getFilename());


        // GET POINTS
        opencv_core.FileStorage fs = new opencv_core.FileStorage("file.xml", opencv_core.FileStorage.WRITE | opencv_core.FileStorage.MEMORY);
        org.bytedeco.javacpp.opencv_core.write(fs, "base", refImageDAO.getBaseImage());
        org.bytedeco.javacpp.opencv_core.write(fs, "desc", refImageDAO.getDescriptors());
        org.bytedeco.javacpp.opencv_core.write(fs, "kp", refImageDAO.getKeyPointVectors());

        BytePointer bytePointer = fs.releaseAndGetString();

        refImage.setMetadata(bytePointer.getString());

        return refImage;
    }


    /**
     * Converts an image to DAO.
     *
     * @param refImage
     * @return
     */
    public static RefImageDAO convertRefToImage(RefImage refImage) throws Exception {
        RefImageMetadata metadata = refImage.getMetadata();

        if (metadata.getMetadata() == null) {
            throw new Exception("Unable to get metadata...");
        }


        System.out.println(metadata.getMetadata());

        opencv_core.FileStorage fileStorage = new opencv_core.FileStorage(metadata.getMetadata(), opencv_core.FileStorage.READ | opencv_core.FileStorage.MEMORY);


        RefImageDAO dao = new RefImageDAO();
        dao.setBaseImage(new Mat());


        opencv_core.FileNode desc = fileStorage.get("desc");
        opencv_core.FileNode kp = fileStorage.get("kp");
        opencv_core.FileNode base = fileStorage.get("base");

        dao.setDescriptors(new Mat(desc.asBytePointer()));
        dao.setKeyPointVectors(new KeyPointVector(kp.asBytePointer()));
        dao.setBaseImage(new Mat(base.asBytePointer()));

        fileStorage.release();

        if (dao.getBaseImage().cols() == 0 || dao.getBaseImage().rows() == 0) {
            throw new Exception("Decompression error from DB...");
        }

        printImage(dao.getDescriptors());

        return dao;

    }

    /**
     * Creates a SIFT from defined parameters.
     *
     * @return SIFT
     */
    private static SIFT createSIFT() {
        int nFeatures = 0;
        int nOctaveLayers = 3;
        double contrastThreshold = 0.03;
        int edgeThreshold = 10;
        double sigma = 1.6;

        return SIFT.create(nFeatures, nOctaveLayers, contrastThreshold, edgeThreshold, sigma);
    }


    /**
     * Detect features from image.
     *
     * @param refImageDAO
     */
    private static void extractFeatures(RefImageDAO refImageDAO) {
        sift.detect(refImageDAO.getBaseImage(), refImageDAO.getKeyPointVectors());
        sift.compute(refImageDAO.getBaseImage(), refImageDAO.getKeyPointVectors(), refImageDAO.getDescriptors());
    }

    public static void printImage(Mat img) {

        UByteRawIndexer sI = img.createIndexer();

        for (int y = 0; y < img.rows(); y++) {

            for (int x = 0; x < img.cols(); x++) {

                //System.out.print( sI.get(y, x) +" ");
            }
            // System.out.println();
        }
    }
}
