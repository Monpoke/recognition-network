package hello.aerospike.domain;

import hello.utils.Gzip;

import javax.persistence.*;
import java.io.IOException;
import java.io.Serializable;

@Entity
public class RefImageMetadata implements Serializable {

    @Id
    @Column
    @GeneratedValue
    private int id;

    // virtual field
    @Transient
    private String metadata;

    @Column(name = "metadata_compress", columnDefinition = "LONGBLOB")
    private byte[] metadata_compress;

    public RefImageMetadata() {
    }

    public RefImageMetadata(String data) {
        this.metadata = data;
    }

    public String getMetadata() {
        return metadata;
    }

    public void setMetadata(String metadata) {
        this.metadata = metadata;
    }


    @PrePersist
    @PreUpdate
    public void onSaveCompress() {
        System.out.println("pre persist...");
        if(metadata==null || metadata.length()==0) return;
        try {
            this.metadata_compress = Gzip.compress(this.getMetadata());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @PostLoad
    public void onLoadUncompress() {
        System.out.println("Uncompress");
        try {
            this.setMetadata(Gzip.decompress(this.metadata_compress));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
