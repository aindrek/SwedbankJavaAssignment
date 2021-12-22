package com.indrek.swedbank.swedbankjava.purchase;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping(path = "api/purchase")
public class PurchaseController {

    private final PurchaseService purchaseService;

    @Autowired
    public PurchaseController(PurchaseService purchaseService) {
        this.purchaseService = purchaseService;
    }

    @PostMapping("/post")
    public Purchase postPurchase(@RequestBody Purchase purchase) {
        return purchaseService.addPurchase(purchase);
    }

    @PostMapping("/post/file")
    public List<Purchase> postPurchases(@RequestParam("file") MultipartFile bulkPurchases) throws IOException {
        return purchaseService.addPurchaseCsv(bulkPurchases);
    }

    @GetMapping("/get/groupBy/{year}/{month}")
    public List<Purchase> getPurchasesByMonth(@PathVariable(value = "year") Integer year, @PathVariable(value = "month") Integer month, @RequestParam(value = "employeeId", required = false) String employeeId) {
        LocalDate findMonth = LocalDate.of(year, month, 1);
        return purchaseService.findPurchasesByMonth(findMonth, employeeId);
    }

    @GetMapping("/get/total")
    public Map<String, Double> getPurchasesTotalByMonth(@RequestParam(value = "employeeId", required = false) String employeeId) {
        return purchaseService.findTotalSpendingByMonth(employeeId);
    }

    @GetMapping("/get/groupByShare")
    public Map<String, Map<String, Map<String, Object>>> getPurchasesByShare(@RequestParam(value = "employeeId", required = false) String employeeId) {
        return purchaseService.findPurchasesGrouped(employeeId);
    }

}
