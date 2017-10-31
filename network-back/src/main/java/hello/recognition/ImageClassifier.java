package hello.recognition;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Pierre on 31/10/2017.
 */
public class ImageClassifier {

    private String className;

    private List<OneImage> images;
    private float scoreMatching;

    public ImageClassifier(String className){

        this.className = className;
        images=new ArrayList<>();
    }


    public void addImage(OneImage img){
        images.add(img);
    }

    public String getClassName() {
        return className;
    }

    public void setClassName(String className) {
        this.className = className;
    }

    public List<OneImage> getImages() {
        return images;
    }

    public void setImages(List<OneImage> images) {
        this.images = images;
    }


    public void setScoreMatching(float scoreMatching) {
        this.scoreMatching = scoreMatching;
    }

    public float getScoreMatching() {
        return scoreMatching;
    }
}
