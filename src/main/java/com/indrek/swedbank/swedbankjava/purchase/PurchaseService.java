package com.indrek.swedbank.swedbankjava.purchase;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.validation.Valid;
import java.io.IOException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.*;

@Service
public class PurchaseService {

    private final PurchaseRepository purchaseRepository;

    @Autowired
    public PurchaseService(PurchaseRepository purchaseRepository){
        this.purchaseRepository = purchaseRepository;
    }

    public Purchase addPurchase(@Valid Purchase purchase){
        return purchaseRepository.save(purchase);
    }

    public List<Purchase> addPurchaseList(List<@Valid Purchase> purchases){
        return purchaseRepository.saveAll(purchases);
    }

    public List<Purchase> addPurchaseCsv(MultipartFile bulkPurchases) throws IOException {
        String[] labels = null;
        List<Purchase> purchases = new ArrayList<>();
        Scanner scanner = new Scanner(bulkPurchases.getInputStream());
        if (scanner.hasNextLine()){
            labels = scanner.nextLine().split(",");
        }
        while (scanner.hasNextLine()) {
            String[] data = scanner.nextLine().split(",");
            if (labels.length == data.length){
                Purchase purchase = new Purchase();
                for (int i = 0; i < labels.length; i++) {
                    switch (labels[i].toLowerCase()) {
                        case "pricepershare" -> purchase.setPricePerShare(Double.parseDouble(data[i]));
                        case "volumeofshares" -> purchase.setVolumeOfShares(Integer.parseInt(data[i]));
                        case "acquireddate" -> purchase.setAcquiredDate(LocalDate.parse(data[i]));
                        case "employeeid" -> purchase.setEmployeeId(data[i]);
                        case "companyname" -> purchase.setCompanyName(data[i]);
                        case "sharename" -> purchase.setShareName(data[i]);
                        case "shareisin" -> purchase.setShareIsin(data[i]);
                        case "country" -> purchase.setCountry(data[i]);
                        case "fieldofeconomicactivity" -> purchase.setFieldOfEconomicActivity(data[i]);
                    }
                }
                purchases.add(purchase);
            }
        }
        scanner.close();
        return addPurchaseList(purchases);
    }

    public List<Purchase> getPurchases(){
        return purchaseRepository.findAll();
    }

    public List<Purchase> getPurchasesByEmployeeId(String employeeId){
        return purchaseRepository.findPurchaseByEmployeeId(employeeId);
    }

    public List<Purchase> findPurchasesByMonth(LocalDate date, String employeeId){
        LocalDate start = date.withDayOfMonth(1);
        LocalDate end = date.plusMonths(1).withDayOfMonth(1).minusDays(1);
        if (employeeId == null){
        return purchaseRepository.findPurchaseByAcquiredDateBetween(start, end);}
        else {
            return purchaseRepository.findPurchaseByEmployeeIdAndAcquiredDateBetween(employeeId, start, end);
        }
    }

    public Map<String, Double> findTotalSpendingByMonth(String employeeId){
        Map<String, Double> totalSpendingByMonth = new HashMap<>();
        List<Purchase> purchases;
        if (employeeId == null){
            purchases = getPurchases();
        } else {
            purchases = getPurchasesByEmployeeId(employeeId);
        }
        purchases.forEach(purchase -> {
            String month = purchase.getAcquiredDate().format(DateTimeFormatter.ofPattern("yyyy-MM"));
            if (totalSpendingByMonth.containsKey(month)) {
                totalSpendingByMonth.put(month, totalSpendingByMonth.get(month) + purchase.getTotalPrice());
            } else {
                totalSpendingByMonth.put(month, purchase.getTotalPrice());
            }
        });

        return totalSpendingByMonth;
    }

    public Map<String, Map<String, Map<String, Object>>> findPurchasesGrouped(String employeeId) {
        Map<String, Map<String, Map<String, Object>>> groupedInfo = new HashMap<>();
        List<Purchase> purchases;
        if (employeeId == null){
            purchases = getPurchases();
        } else {
            purchases = getPurchasesByEmployeeId(employeeId);
        }
        purchases.forEach(purchase -> {
            String month = purchase.getAcquiredDate().format(DateTimeFormatter.ofPattern("yyyy-MM"));
            if (!groupedInfo.containsKey(month)) {
                groupedInfo.put(month, new HashMap<>());
            }
            Map<String, Map<String, Object>> groupedInfoByMonth = groupedInfo.get(month);
            String isin = purchase.getShareIsin();
            if (!groupedInfoByMonth.containsKey(isin)) {
                groupedInfoByMonth.put(isin, new HashMap<>(){{
                    put("companyName", purchase.getCompanyName());
                    put("shareName", purchase.getShareName());
                    put("shareIsin", purchase.getShareIsin());
                    put("country", purchase.getCountry());
                    put("fieldOfEconomicActivity", purchase.getFieldOfEconomicActivity());
                    put("totalVolume", 0);
                    put("totalPrice", 0.0);
                    put("averagePrice", 0.0);
                }});
            }
            Map<String, Object> data = groupedInfoByMonth.get(isin);
            data.put("totalVolume", (int)data.get("totalVolume") + purchase.getVolumeOfShares());
            data.put("totalPrice", (double)data.get("totalPrice") + purchase.getTotalPrice());
            data.put("averagePrice", (double)data.get("totalPrice") / (int)data.get("totalVolume"));
        });
        return groupedInfo;
    }
}
