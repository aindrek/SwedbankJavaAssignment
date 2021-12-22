package com.indrek.swedbank.swedbankjava.purchase;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.web.servlet.MockMvc;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDate;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


@SpringBootTest
@AutoConfigureMockMvc
public class PurchaseControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private PurchaseService purchaseService;

    @Autowired
    private PurchaseRepository purchaseRepository;

    @Autowired
    private ObjectMapper objectMapper;

    private Purchase purchase1;
    private Purchase purchase2;
    private Purchase purchase3;

    @BeforeEach
    public void initialize() {
        purchase1 = new Purchase(1L, 1, 23, LocalDate.parse("2020-05-05"), "ER001", "Isemasin", "ISE", "EE1234567890", "Estonia", "Agriculture");
        purchase2 = new Purchase(2L, 2, 14, LocalDate.parse("2020-07-08"), "VA224", "Sahkerdis", "SSH", "EE0987654321", "Estonia", "Manufacturing");
        purchase3 = new Purchase(3L, 3, 7, LocalDate.parse("2020-05-12"), "VA224", "Lahvatus", "LVH", "EE2345665432", "Estonia", "Technology");
    }

    @AfterEach
    public void deinitialize() {
        purchaseRepository.deleteAll();
    }

    @Test
    @DirtiesContext
    public void testPostPurchase() throws Exception {
        int before = purchaseRepository.findAll().size();

        mockMvc.perform(post("/api/purchase/post").contentType(MediaType.APPLICATION_JSON).content(objectMapper.writeValueAsString(purchase1))).andExpect(status().isOk());

        int after = purchaseRepository.findAll().size();
        assertThat(after-before).isEqualTo(1);
    }

    @Test
    @DirtiesContext
    public void testPostPurchases() throws Exception {
        int before = purchaseRepository.findAll().size();
        Path path = Paths.get("src/test/resources/testcsv.csv");
        byte[] content = null;
        try {
            content = Files.readAllBytes(path);
        } catch (final IOException e){
        }
        mockMvc.perform(multipart("/api/purchase/post/file").file(new MockMultipartFile("file", content))).andExpect(status().isOk());

        int after = purchaseRepository.findAll().size();
        assertThat(after-before).isEqualTo(2);
    }

    @Test
    @DirtiesContext
    public void testGetPurchasesByMonth() throws Exception {
        purchaseRepository.save(purchase1);
        purchaseRepository.save(purchase2);
        purchaseRepository.save(purchase3);

        mockMvc.perform(get("/api/purchase/get/groupBy/2020/05")).andExpect(status().isOk())
                .andExpect(content().json("["+objectMapper.writeValueAsString(purchase1)+","+objectMapper.writeValueAsString(purchase3)+"]"));
        mockMvc.perform(get("/api/purchase/get/groupBy/2020/05?employeeId=ER001")).andExpect(status().isOk())
                .andExpect(content().json("["+objectMapper.writeValueAsString(purchase1)+"]"));
    }

    @Test
    @DirtiesContext
    public void testGetPurchasesTotalByMonth() throws Exception {
        purchaseRepository.save(purchase1);
        purchaseRepository.save(purchase2);
        purchaseRepository.save(purchase3);

        mockMvc.perform(get("/api/purchase/get/total")).andExpect(status().isOk())
                .andExpect(content().json("{\"2020-05\":44,\"2020-07\":28}"));
        mockMvc.perform(get("/api/purchase/get/total?employeeId=ER001")).andExpect(status().isOk())
                .andExpect(content().json("{\"2020-05\":23}"));
    }

    @Test
    @DirtiesContext
    public void testGetPurchasesByShare() throws Exception {
        purchaseRepository.save(purchase1);
        mockMvc.perform(get("/api/purchase/get/groupByShare")).andExpect(status().isOk())
                .andExpect(content().json("{\"2020-05\":{\"EE1234567890\": {\"totalVolume\":23,\"shareIsin\":\"EE1234567890\",\"country\":\"Estonia\",\"totalPrice\":23.0,\"companyName\":\"Isemasin\",\"fieldOfEconomicActivity\":\"Agriculture\",\"shareName\":\"ISE\",\"averagePrice\":1.0}}}"));
        mockMvc.perform(get("/api/purchase/get/groupByShare?employeeId=ER001")).andExpect(status().isOk())
                .andExpect(content().json("{\"2020-05\":{\"EE1234567890\": {\"totalVolume\":23,\"shareIsin\":\"EE1234567890\",\"country\":\"Estonia\",\"totalPrice\":23.0,\"companyName\":\"Isemasin\",\"fieldOfEconomicActivity\":\"Agriculture\",\"shareName\":\"ISE\",\"averagePrice\":1.0}}}"));
        mockMvc.perform(get("/api/purchase/get/groupByShare?employeeId=VA224")).andExpect(status().isOk())
                .andExpect(content().json("{}"));
    }
}
