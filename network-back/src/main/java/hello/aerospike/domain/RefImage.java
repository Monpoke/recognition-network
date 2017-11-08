package hello.aerospike.domain;

import org.bytedeco.javacpp.opencv_core;
import org.springframework.data.annotation.Id;

import java.util.UUID;

public class RefImage {

    @Id
    private String id;

    private String name;

    private String classifier;
    private byte[] metadata;

    public String getId() {
        return id;
    }


    /**
     *
     */
    public RefImage() {

        // set random id
        setId(UUID.randomUUID().toString());
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getClassifier() {
        return classifier;
    }

    public void setClassifier(String classifier) {
        this.classifier = classifier;
    }

    public void setMetadata(byte[] metadata) {
        this.metadata = metadata;
    }

    public byte[] getMetadata() {
        return metadata;
    }
}
