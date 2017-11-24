package hello.service;


import hello.aerospike.domain.RefImage;

public interface ImagesService {


    void processImage();

    void save(RefImage refImage);

    void finalize(RefImage refImage);

    Iterable<RefImage> getAll();

    RefImage find(String id);

    void test(String id);
}
