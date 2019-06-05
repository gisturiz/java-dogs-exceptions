package com.lambdaschool.dogsinitial.controller;

import com.lambdaschool.dogsinitial.DogsinitialApplication;
import com.lambdaschool.dogsinitial.exception.ResourceNotFoundException;
import com.lambdaschool.dogsinitial.model.Dog;
import com.lambdaschool.dogsinitial.model.MessageDetail;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import java.util.*;

@RestController
@RequestMapping("/dogs")
public class DogController
{
    private static final Logger logger = LoggerFactory.getLogger(DogController.class);

    @Autowired
    RabbitTemplate rt;

    // localhost:8080/dogs/dogs
    @GetMapping(value = "/dogs",
            produces = {"application/json"})
    public ResponseEntity<?> getAllDogs(HttpServletRequest request)
    {
        logger.info(request.getRequestURI());
        MessageDetail message =  new MessageDetail("/dogs/dogs accessed", 6, false);
        rt.convertAndSend(DogsinitialApplication.QUEUE_NAME_HIGH, message);

        return new ResponseEntity<>(DogsinitialApplication.ourDogList.dogList, HttpStatus.OK);
    }

    // localhost:8080/dogs/{id}
    @GetMapping(value = "dogs/{id}",
            produces = {"application/json"})
    public ResponseEntity<?> getDogDetail(HttpServletRequest request,
            @PathVariable
                  long id)
    {
        logger.trace(request.getRequestURI());

        MessageDetail message = new MessageDetail("/dogs/dogs accessed", 1, true);
        rt.convertAndSend(DogsinitialApplication.QUEUE_NAME_LOW, message);

        Dog rtnDog;
        if (DogsinitialApplication.ourDogList.findDog(e -> (e.getId() == id)) == null)
        {

            throw new ResourceNotFoundException("Dog with id " + id + " not found");
        }   else
        {
            rtnDog = DogsinitialApplication.ourDogList.findDog(d -> (d.getId() == id));
        }
        return new ResponseEntity<>(rtnDog, HttpStatus.OK);
    }

    // localhost:8080/dogs/breeds/{breed}
    @GetMapping(value = "/breeds/{breed}",
            produces = {"application/json"})
    public ResponseEntity<?> getDogBreeds(HttpServletRequest request,
            @PathVariable
                    String breed)
    {
        logger.trace(request.getRequestURI());

        MessageDetail message = new MessageDetail("/dogs/breeds accessed", 5, true);
        ArrayList<Dog> rtnDogs = DogsinitialApplication.ourDogList.
                findDogs(d -> d.getBreed().toUpperCase().equals(breed.toUpperCase()));
        if ( rtnDogs.size() == 0)
        {
            throw new ResourceNotFoundException("No dogs with the breed " + breed + " were found");
        }
        return new ResponseEntity<>(rtnDogs, HttpStatus.OK);
    }

    //localhost:8080/dogs/dogtable
    @GetMapping(value = "/dogtable")
    public ModelAndView displayDogTable()
    {
        ModelAndView mav = new ModelAndView();
        mav.setViewName("dogs");
        mav.addObject("dogList", DogsinitialApplication.ourDogList.dogList);

        return mav;
    }
}
