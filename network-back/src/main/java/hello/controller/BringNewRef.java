package hello.controller;

import hello.service.ImagesService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.UUID;

@Controller
public class BringNewRef {
    private static final Logger log = LoggerFactory.getLogger(BringNewRef.class);


    @Autowired
    private ImagesService imagesService;

    @RequestMapping("/newimage")
    @ResponseBody
    public String test() {


        log.info(UUID.randomUUID().toString());

        imagesService.processImage();
        return "coucou;";
    }


}
