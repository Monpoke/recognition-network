package hello.service;



import hello.aerospike.domain.RefImage;

public interface ImagesService {


     void processImage();

    void save(RefImage refImage);
}
