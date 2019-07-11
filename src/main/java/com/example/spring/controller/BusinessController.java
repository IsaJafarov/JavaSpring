package com.example.spring.controller;


import com.example.spring.exceptions.BadRequestException;
import com.example.spring.service.BusinessService;
import com.example.spring.service.dto.BusinessDTO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api")
public class BusinessController {

    private BusinessService businessService;
    private Logger logger = LoggerFactory.getLogger(BusinessController.class);

    public BusinessController(BusinessService businessService) {
        this.businessService = businessService;
    }

    @PostMapping("/business/save")
    public ResponseEntity<BusinessDTO> saveBusiness(@RequestBody BusinessDTO businessDTO) {
        logger.debug("Rest request to save business");
        if (businessDTO.getId() != null){
            throw new BadRequestException("Business already exists");
        }
        businessService.save(businessDTO);
        return new ResponseEntity<>(HttpStatus.CREATED);
    }

    @GetMapping("/business/{id}")
    public ResponseEntity<Optional<BusinessDTO>> getBusinessById(@PathVariable Long id) {
        if (id == null){
            throw new BadRequestException("Enter valid id");
        }

        Optional<BusinessDTO> result = businessService.getById(id);

        return ResponseEntity.ok().body(result);

    }


    @PutMapping("/business/update")
    public ResponseEntity<BusinessDTO> updateBusiness(@RequestBody BusinessDTO businessDTO){

        if ( businessService.getById( businessDTO.getId() ).isPresent() ){
            businessService.update(businessDTO);
            return new ResponseEntity<>( businessDTO, HttpStatus.OK);
        }
        else
            return new ResponseEntity<>( businessDTO, HttpStatus.NOT_FOUND);

    }

    @DeleteMapping("/business/{id}")
    public ResponseEntity deleteBusinessById(@PathVariable("id") long id){
        businessService.deleteById(id);
        return new ResponseEntity(HttpStatus.ACCEPTED);
    }

    @GetMapping("/business")
    public ResponseEntity<List<BusinessDTO>> getAllBusinessList(){
        List<BusinessDTO> businessDTOList = businessService.getAllBusinessList();
        return new ResponseEntity<>(businessDTOList, HttpStatus.OK);
    }
}
