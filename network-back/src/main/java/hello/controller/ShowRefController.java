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
import java.util.List;
import java.util.UUID;

@Controller
public class ShowRefController {
    private static final Logger log = LoggerFactory.getLogger(ShowRefController.class);


    @Autowired
    private ImagesService imagesService;

    @RequestMapping("/all")
    @ResponseBody
    public String loadAllRefs() {

        Iterable<RefImage> images = imagesService.getAll();
        Iterator<RefImage> iterator = images.iterator();

        StringBuilder builder = new StringBuilder("<table>");

        while (iterator.hasNext()) {
            RefImage next = iterator.next();

            builder.append("<tr>" +
                    "<td>" + next.getClassifier() + " </td>" +
                    "<td><a href='/voir/" + next.getId() + "'>" + next.getId() + "</a></td>" +
                    "<td><a href='/analyse/" + next.getId() + "'>" + next.getId() + "</a></td>" +
                    "</tr>\n");

        }

        builder.append("</table>");
        return builder.toString();
    }


    @RequestMapping("/voir/{id}")
    @ResponseBody
    public String loadRef(@PathVariable String id) {

        RefImage image = imagesService.find(id);

        StringBuilder builder = new StringBuilder();

        builder.append("<h1>" + image.getId() + "</h1>" +
                "<h2>" + image.getClassifier() + "</h2>");

        RefImageMetadata metadata = image.getMetadata();

        builder.append("<pre>\n" +
                metadata.getMetadata() + "\n" +
                "</pre>");

        return builder.toString();
    }


}
