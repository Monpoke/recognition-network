package hello.controller;

import hello.aerospike.domain.RefImage;
import hello.aerospike.domain.RefImageMetadata;
import hello.service.ImagesService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.Iterator;

@Controller
public class AnalyseImageController {
    private static final Logger log = LoggerFactory.getLogger(AnalyseImageController.class);


    @Autowired
    private ImagesService imagesService;

    @RequestMapping("/analyse/{img}")
    @ResponseBody
    public String loadAllRefs(@PathVariable  String img) {

        imagesService.test();

        return "cool";
    }



}
