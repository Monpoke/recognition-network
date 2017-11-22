package hello.recognition;

import org.bytedeco.javacpp.opencv_core;

import static org.bytedeco.javacpp.opencv_core.*;

public class OneImage {
    private String file;
    private Mat image;

    private float score = -1f;

    private KeyPointVector keyPointVector = new KeyPointVector();

    private Mat descriptors = new Mat();
    private DMatchVector matches;


    public OneImage(String file, Mat image) {
        this.file = file;
        this.image = image;
    }


    public Mat getImage() {
        return image;
    }

    public void setImage(Mat image) {
        this.image = image;
    }

    public KeyPointVector getKeyPointVector() {
        return keyPointVector;
    }

    public void setKeyPointVector(KeyPointVector keyPointVector) {
        this.keyPointVector = keyPointVector;
    }

    public Mat getDescriptors() {
        return descriptors;
    }

    public void setDescriptors(Mat descriptors) {
        this.descriptors = descriptors;
    }

    public String getFile() {
        return file;
    }

    public float getScore() {
        return score;
    }

    public void setScore(float score) {
        this.score = score;
    }

    public String getClassName() {
        String[] parts = this.getFile().split("\\\\");

        return parts[parts.length - 1].split("_")[0];
    }

    public void setMatches(DMatchVector matches) {
        this.matches = matches;
    }

    public DMatchVector getMatches() {
        return matches;
    }

    public static OneImage fromFilestorage(String name, FileStorage fileStorage) {

        opencv_core.FileNode base = fileStorage.getNode("base");
        opencv_core.FileNode desc = fileStorage.getNode("desc");
        opencv_core.FileNode kp = fileStorage.getNode("kp");
        new opencv_core.KeyPointVector(kp.asBytePointer());

        OneImage im = new OneImage(name, new Mat(base));
        im.setDescriptors(new Mat(desc));
        im.setKeyPointVector(new KeyPointVector(kp));

        return im;
    }
}
