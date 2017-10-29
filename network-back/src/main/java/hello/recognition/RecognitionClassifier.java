/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package hello.recognition;

import hello.ImporterData;
import java.util.List;
import org.bytedeco.javacpp.Pointer;
import org.bytedeco.javacpp.opencv_core;
import static org.bytedeco.javacpp.opencv_core.*;
import static org.bytedeco.javacpp.opencv_imgcodecs.*;
import org.bytedeco.javacpp.opencv_ml;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author lionel
 */
public class RecognitionClassifier {

    private static final Logger log = LoggerFactory.getLogger(RecognitionClassifier.class);

    private final String basedir;

    private final List<String> filesList;

    public RecognitionClassifier(String basedir, List<String> filesList) {
        this.basedir = basedir;
        this.filesList = filesList;
    }

    /**
     * Loads all images.
     *
     * @throws Exception
     */
    public void start() throws Exception {

        loadMembers();
        createClassifier();

    }

    private void loadMembers() throws Exception {
        for (String file : filesList) {
            String fullpath = this.basedir + file;
            Mat imread = imread(fullpath);

            if (imread.cols() == 0 || imread.rows() == 0) {
                throw new Exception("Image not existing... " + fullpath);
            }

            log.info("Loaded: " + fullpath);

        }

    }

    private void createClassifier() {

        
        
        
        opencv_ml.KNearest kNearest = opencv_ml.KNearest.create();
        kNearest.setIsClassifier(true);
        kNearest.setDefaultK(10);

        //kNearest.train(td)
        
//        kNearest.train(data, opencv_ml.ROW_SAMPLE);

    }

}
