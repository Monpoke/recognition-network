package hello.aerospike.domain;


import javax.persistence.*;
import java.security.MessageDigest;
import java.util.UUID;

@Entity
public class RefImage {

    @Id
    @Column
    private String id;

    @Column
    private String name;

    @Column
    private String classifier;

    @Column
    private String metadata_hash;

    @OneToOne(cascade = CascadeType.ALL,fetch = FetchType.LAZY)
    private RefImageMetadata metadata;


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

    public String getMetadata_hash() {
        return metadata_hash;
    }

    public void setMetadata_hash(String metadata_hash) {
        this.metadata_hash = metadata_hash;
    }

    public RefImageMetadata getMetadata() {
        return metadata;
    }

    public void setMetadata(RefImageMetadata metadata) {
        this.metadata = metadata;
    }

    public void setMetadata(String data){
        setMetadata(new RefImageMetadata(data));
    }


}
