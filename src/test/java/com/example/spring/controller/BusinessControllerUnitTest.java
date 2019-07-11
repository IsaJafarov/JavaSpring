package com.example.spring.controller;

import com.example.spring.service.BusinessService;
import com.example.spring.service.dto.BusinessDTO;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.jayway.jsonpath.JsonPath;
import com.jayway.jsonpath.Option;
import org.hamcrest.Matchers;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.skyscreamer.jsonassert.JSONAssert;
import org.skyscreamer.jsonassert.JSONCompareMode;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import static org.mockito.ArgumentMatchers.isNull;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@RunWith(SpringRunner.class)
@WebMvcTest(BusinessController.class)
public class BusinessControllerUnitTest {

    @MockBean
    BusinessService businessService;

    @Autowired
    MockMvc mockMvc;

    @Test
    public void saveBusiness() throws Exception {
        BusinessDTO businessDTO = new BusinessDTO("Facebook", "Social Media");
        when(businessService.save(isNull())).thenReturn(businessDTO);

        String content = "{\n" +
                "\t\"name\": \"Facebook\",\n" +
                "\t\"info\" : \"Social Media\"\n" +
                "\t\n" +
                "}";

        mockMvc.perform(MockMvcRequestBuilders.post("/api/business/save")
                .content(content)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isCreated())
                .andDo(print());

    }

    @Test
    public void getBusinessById() throws Exception {

        BusinessDTO business = new BusinessDTO(1L, "Facebook", "Social Media");
        Optional<BusinessDTO> optional = Optional.of(business);
        when(businessService.getById(1L)).thenReturn(optional);

        MvcResult mockMvcResult = mockMvc.perform(MockMvcRequestBuilders.get("/api/business/1")
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andDo(print())
                .andReturn();

        String result = "{\n" +
                "    \"id\": 1,\n" +
                "    \"name\": \"Facebook\",\n" +
                "    \"info\": \"Social Media\"\n" +
                "}";

        JSONAssert.assertEquals(result, mockMvcResult.getResponse().getContentAsString(), false);

    }

    @Test
    public void updateBusiness() throws Exception {
        BusinessDTO businessDTO = new BusinessDTO(5L, "mover.az", "Transport company");
        // businessDTO will be returned from businessService.getById() method
        Mockito.when(businessService.getById( businessDTO.getId() )).thenReturn(Optional.of(businessDTO));
        // but return type of businessService.update method is void
        Mockito.doNothing().when(businessService).update(businessDTO);

        mockMvc.perform(MockMvcRequestBuilders
                .put("/api/business/update")
                .contentType(MediaType.APPLICATION_JSON)
                .content(new Gson().toJson(businessDTO)) //convert businessDTO into json and send with request
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andDo(print());

    }

    @Test
    public void deleteBusinessById() throws Exception {

        mockMvc.perform(MockMvcRequestBuilders.delete("/api/business/{id}", 1))
                .andExpect(status().isAccepted())
                .andDo(print());
    }

    @Test
    public void getAllBusinessList() throws Exception {

        // test data that’ll be returned as a response
        List<BusinessDTO> businessDTOList = Arrays.asList(
                new BusinessDTO(1L, "Azercell", "Mobile operator"),
                new BusinessDTO(2L, "Atenau LTD", "Smart house service"));

        Mockito.when(businessService.getAllBusinessList()).thenReturn(businessDTOList);

        MvcResult mvcResult = mockMvc.perform(MockMvcRequestBuilders.get("/api/business")
                .accept(MediaType.APPLICATION_JSON))
//                .andExpect(content().contentType(MediaType.APPLICATION_JSON)) //same as above line
                .andExpect(status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$", Matchers.hasSize(2))) // response should contain 2 items
                .andExpect(MockMvcResultMatchers.jsonPath("$[0].id", Matchers.is(1))) //  id attribute of the first element equals to 1
                .andExpect(MockMvcResultMatchers.jsonPath("$[0].name", Matchers.is("Azercell"))) //name attribute of the first element is Azercell
                .andDo(print())
                .andReturn(); //to get MvcResult

        String expectedJsonResponse = new Gson().toJson(businessDTOList);
        JSONAssert.assertEquals(expectedJsonResponse, mvcResult.getResponse().getContentAsString(), JSONCompareMode.LENIENT); //compare expected and actual response

    }
}
