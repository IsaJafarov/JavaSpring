package com.example.spring.controller;

import com.example.spring.Application;
import com.example.spring.domain.Business;
import com.example.spring.service.dto.BusinessDTO;
import com.google.gson.Gson;
import org.json.JSONException;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.skyscreamer.jsonassert.JSONAssert;
import org.skyscreamer.jsonassert.JSONCompareMode;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.http.*;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.MatcherAssert.assertThat;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest(classes = Application.class, webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)

public class BusinessControllerIntegrationTest {

    @LocalServerPort
    private int localPort;

    private TestRestTemplate testRestTemplate = new TestRestTemplate();

    //test to save mock Business object
    @Test
    public void save() {

        BusinessDTO businessDTO = new BusinessDTO("Facebook", "Social Media");
        ResponseEntity<BusinessDTO> dto = testRestTemplate.postForEntity(createURI("/api/business/save"), businessDTO, BusinessDTO.class);
        assertThat(dto.getStatusCode(), equalTo(HttpStatus.CREATED));
    }

    //test to get mock Business by id
    @Test
    public void getBusinessById() throws JSONException {

        String result = "{\n" +
                "    \"id\": 1,\n" +
                "    \"name\": \"Facebook\",\n" +
                "    \"info\": \"Social Media\"\n" +
                "}";

        ResponseEntity<String> dto = testRestTemplate.getForEntity(createURI("/api/business/1"), String.class);
        //JSONAssert converts your string into JSON object and the compares
        JSONAssert.assertEquals(result, dto.getBody(), false);
    }

    @Test
    public void updateBusiness() throws Exception{
        testRestTemplate.put(createURI("/api/business/update"), BusinessDTO.class); //doesn't return ResponseEntity

        BusinessDTO businessDTO = new BusinessDTO(1L, "Sparkle", "A new startup");
        ResponseEntity<BusinessDTO> responseEntity = testRestTemplate.exchange(createURI("/api/business/update"), HttpMethod.PUT, new HttpEntity<>(businessDTO), BusinessDTO.class);

        assertThat(responseEntity.getStatusCode(), equalTo(HttpStatus.OK)); //check status code
        String expectedJsonResponse = new Gson().toJson( businessDTO );
        String actualJsonResponse = new Gson().toJson(responseEntity.getBody());

        JSONAssert.assertEquals(actualJsonResponse, expectedJsonResponse, JSONCompareMode.LENIENT); //check response body
    }

    @Test
    public void deleteBusinessById() throws Exception{
        testRestTemplate.delete(createURI("/api/business/4"));
    }

    @Test
    public void getAllBusinessList() throws Exception{
        ResponseEntity<BusinessDTO> responseEntity = testRestTemplate.getForEntity(createURI("/api/business"), BusinessDTO.class);

        String expectedJsonResponse = new Gson().toJson("");  //fixme how can I get json format of business list
        String actualJsonResponse = new Gson().toJson(responseEntity.getBody());

        JSONAssert.assertEquals(actualJsonResponse, expectedJsonResponse, JSONCompareMode.LENIENT);

    }

    private String createURI(String uri) {
        return "http://localhost:" + localPort + uri;
    }
}
