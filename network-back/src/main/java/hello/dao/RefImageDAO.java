package hello.dao;

import org.bytedeco.javacpp.opencv_core;
import org.bytedeco.javacpp.opencv_core.KeyPointVector;
import org.bytedeco.javacpp.opencv_core.Mat;

public class RefImageDAO {

    private Mat baseImage;

    private KeyPointVector keyPointVectors;

    private String filename;
    private Mat descriptors;


    public RefImageDAO() {
        this.setKeyPointVectors(new KeyPointVector());
        this.setDescriptors(new Mat());
    }

    public Mat getBaseImage() {
        return baseImage;
    }

    public void setBaseImage(Mat baseImage) {
        this.baseImage = baseImage;
    }

    public String getFilename() {
        return filename;
    }

    public void setFilename(String filename) {
        this.filename = filename;
    }

    public KeyPointVector getKeyPointVectors() {
        return keyPointVectors;
    }

    public void setKeyPointVectors(KeyPointVector keyPointVectors) {
        this.keyPointVectors = keyPointVectors;
    }

    public Mat getDescriptors() {
        return descriptors;
    }

    public void setDescriptors(Mat descriptors) {
        this.descriptors = descriptors;
    }

    /**
     * Returns the classifier name.
     *
     * @return
     */
    public String classifier() {

        String[] split = this.getFilename().split("/");
        String withoutPath = split[split.length - 1];
        return withoutPath.split("_")[0];

    }
}
