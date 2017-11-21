package hello.service;


import hello.aerospike.domain.RefImage;
import hello.aerospike.repositories.RefImageMetadataRepository;
import hello.aerospike.repositories.RefImageRepository;
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
}
