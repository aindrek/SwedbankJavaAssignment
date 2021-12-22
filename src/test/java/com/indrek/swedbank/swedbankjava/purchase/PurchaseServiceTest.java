package com.indrek.swedbank.swedbankjava.purchase;

import org.assertj.core.data.Offset;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.annotation.DirtiesContext;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
public class PurchaseServiceTest {

    @Autowired
    private PurchaseService purchaseService;

    @Autowired
    private PurchaseRepository purchaseRepository;

    private Purchase purchase1;
    private Purchase purchase2;
    private Purchase purchase3;

    @BeforeEach
    public void initialize() {
        purchase1 = new Purchase(1L, 1.2, 23, LocalDate.parse("2020-05-05"), "ER001", "Isemasin", "ISE", "EE1234567890", "Estonia", "Agriculture");
        purchase2 = new Purchase(2L, 2.2, 14, LocalDate.parse("2020-07-08"), "VA224", "Sahkerdis", "SSH", "EE0987654321", "Estonia", "Manufacturing");
        purchase3 = new Purchase(3L, 2.6, 7, LocalDate.parse("2020-05-12"), "VA224", "Lahvatus", "LVH", "EE2345665432", "Estonia", "Technology");
    }
    
    @AfterEach
    public void deinitialize() {
        purchaseRepository.deleteAll();
    }
    
    @Test
    @DirtiesContext
    public void testAddPurchase(){
        int before = purchaseRepository.findAll().size();
        purchaseService.addPurchase(purchase1);
        int after = purchaseRepository.findAll().size();
        assertThat(after-before).isEqualTo(1);
        assertThat(purchaseRepository.findAll().get(0).getId()).isEqualTo(1L);
        purchaseService.addPurchase(purchase2);
        after = purchaseRepository.findAll().size();
        assertThat(after-before).isEqualTo(2);
    }

    @Test
    @DirtiesContext
    public void testGetPurchases(){
        int before = purchaseService.getPurchases().size();
        purchaseRepository.save(purchase1);
        int after = purchaseService.getPurchases().size();
        assertThat(after-before).isEqualTo(1);
        assertThat(purchaseService.getPurchases().get(0).getId()).isEqualTo(1L);
        purchaseRepository.save(purchase2);
        after = purchaseService.getPurchases().size();
        assertThat(after-before).isEqualTo(2);
    }

    @Test
    @DirtiesContext
    public void testAddPurchaseList(){
        int before = purchaseRepository.findAll().size();
        purchaseService.addPurchaseList(List.of(purchase1, purchase2));
        int after = purchaseRepository.findAll().size();
        assertThat(after-before).isEqualTo(2);
    }

    @Test
    @DirtiesContext
    public void testAddPurchaseCsv() throws IOException {
        int before = purchaseRepository.findAll().size();
        Path path = Paths.get("src/test/resources/testcsv.csv");
        byte[] content = null;
        try {
            content = Files.readAllBytes(path);
        } catch (final IOException e){
        }
        purchaseService.addPurchaseCsv(new MockMultipartFile("testcsv.csv", content));

        int after = purchaseRepository.findAll().size();
        assertThat(after-before).isEqualTo(2);
    }

    @Test
    @DirtiesContext
    public void testGetPurchasesByEmployeeId(){
        int before1 = purchaseService.getPurchasesByEmployeeId("ER001").size();
        int before2 = purchaseService.getPurchasesByEmployeeId("VA224").size();
        purchaseRepository.save(purchase1);
        purchaseRepository.save(purchase2);
        int after1 = purchaseService.getPurchasesByEmployeeId("ER001").size();
        int after2 = purchaseService.getPurchasesByEmployeeId("VA224").size();
        assertThat(after1-before1).isEqualTo(1);
        assertThat(after2-before2).isEqualTo(1);
        purchaseRepository.save(purchase3);
        after1 = purchaseService.getPurchasesByEmployeeId("ER001").size();
        after2 = purchaseService.getPurchasesByEmployeeId("VA224").size();
        assertThat(after1-before1).isEqualTo(1);
        assertThat(after2-before2).isEqualTo(2);
    }

    @Test
    @DirtiesContext
    public void testFindPurchasesByMonth(){
        purchaseRepository.save(purchase1);
        purchaseRepository.save(purchase2);
        purchaseRepository.save(purchase3);

        int responseSize = purchaseService.findPurchasesByMonth(LocalDate.parse("2020-05-01"), null).size();
        assertThat(responseSize).isEqualTo(2);
        responseSize = purchaseService.findPurchasesByMonth(LocalDate.parse("2020-07-01"), null).size();
        assertThat(responseSize).isEqualTo(1);
        responseSize = purchaseService.findPurchasesByMonth(LocalDate.parse("2020-05-01"), "ER001").size();
        assertThat(responseSize).isEqualTo(1);
    }

    @Test
    @DirtiesContext
    public void testFindTotalSpendingByMonth(){
        purchaseRepository.save(purchase1);
        purchaseRepository.save(purchase2);
        purchaseRepository.save(purchase3);

        Map<String, Double> totalSpending = purchaseService.findTotalSpendingByMonth(null);

        assertThat(totalSpending.get("2020-05")).isCloseTo(45.8, Offset.offset(0.01));
        assertThat(totalSpending.get("2020-07")).isCloseTo(30.8, Offset.offset(0.01));

        totalSpending = purchaseService.findTotalSpendingByMonth("VA224");

        assertThat(totalSpending.get("2020-05")).isCloseTo(18.2, Offset.offset(0.01));
    }

    @Test
    @DirtiesContext
    public void testFindPurchasesGrouped(){
        purchaseRepository.save(purchase1);

        Map<String, Map<String, Map<String, Object>>> groupedTree = purchaseService.findPurchasesGrouped(null);
        double totalPrice = (double)groupedTree.get("2020-05").get("EE1234567890").get("totalPrice");
        assertThat(totalPrice).isCloseTo(27.6, Offset.offset(0.01));

        groupedTree = purchaseService.findPurchasesGrouped("ER001");
        totalPrice = (double)groupedTree.get("2020-05").get("EE1234567890").get("totalPrice");
        assertThat(totalPrice).isCloseTo(27.6, Offset.offset(0.01));
    }

}
