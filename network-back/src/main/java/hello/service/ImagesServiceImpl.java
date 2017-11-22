package hello.service;


import hello.aerospike.domain.RefImage;
import hello.aerospike.domain.RefImageMetadata;
import hello.aerospike.repositories.RefImageMetadataRepository;
import hello.aerospike.repositories.RefImageRepository;
import hello.recognition.OneImage;
import org.bytedeco.javacpp.BytePointer;
import org.bytedeco.javacpp.opencv_core;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

@Service
public class ImagesServiceImpl implements ImagesService {


    @Autowired
    private RefImageRepository refImageRepository;

    @Autowired
    private RefImageMetadataRepository refImageMetadataRepository;


    /**
     * Service constructor.
     */
    public ImagesServiceImpl() {
    }


    @Override
    public void processImage() {

    }


    /**
     * Save an image in db.
     *
     * @param refImage
     */
    @Override
    public void save(RefImage refImage) {


        try {
            refImageMetadataRepository.save(refImage.getMetadata());

            refImageRepository.save(refImage);

        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }


    /**
     * Mainly hash the content.
     * @param refImage
     */
    @Override
    public void finalize(RefImage refImage) {

        MessageDigest messageDigest = null;
        try {
            messageDigest = MessageDigest.getInstance("SHA-1");

            messageDigest.update(refImage.getMetadata().getMetadata().getBytes());
            String metadata_hash = new String(messageDigest.digest());
            refImage.setMetadata_hash(metadata_hash);

        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }

    }

    @Override
    public Iterable<RefImage> getAll() {
        return refImageRepository.findAll();
    }

    @Override
    public RefImage find(String id) {
        return refImageRepository.findById(id);
    }



    @Override
    public void test() {
        RefImage byId = refImageRepository.findById("e00b9bc3-715c-48fe-8fa2-34de6dda567b");

        opencv_core.FileStorage fs = new opencv_core.FileStorage("./kp.xml", opencv_core.FileStorage.WRITE| opencv_core.FileStorage.MEMORY);

        RefImageMetadata metadata = byId.getMetadata();

        BytePointer bp = new BytePointer(metadata.getMetadata());

        opencv_core.FileStorage fileStorage = new opencv_core.FileStorage(bp, opencv_core.FileStorage.READ);


        OneImage.fromFilestorage("name",fileStorage);


    }
}
