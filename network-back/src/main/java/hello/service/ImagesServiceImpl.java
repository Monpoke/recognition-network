package hello.service;


import hello.aerospike.domain.RefImage;
import hello.aerospike.repositories.RefImageRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class ImagesServiceImpl implements ImagesService {


    @Autowired
    private RefImageRepository refImageRepository;


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




        /*try {
            refImageRepository.save(refImage);

            System.out.println("Saved under id: "+refImage.getId());

        } catch(Exception ex){
            ex.printStackTrace();
        }*/
    }
}
