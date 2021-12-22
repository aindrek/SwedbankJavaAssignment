package com.indrek.swedbank.swedbankjava.purchase;

import javax.persistence.*;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Positive;
import java.time.LocalDate;

@Entity
@Table
public class Purchase {
    @Id
    @GeneratedValue(
            strategy = GenerationType.IDENTITY
    )
    private long id;
    @Positive(message = "Price per share has to be positive")
    @NotNull(message = "Price per share cannot be null")
    private double pricePerShare;
    @Positive(message = "Volume of shares has to be positive")
    @NotNull(message = "Volume of shares cannot be null")
    private int volumeOfShares;
    @NotNull(message = "Date cannot be null")
    private LocalDate acquiredDate;
    @NotBlank(message = "Employee Id cannot be blank")
    private String employeeId;
    @NotBlank(message = "Company name cannot be blank")
    private String companyName;
    @NotBlank(message = "Share name cannot be blank")
    private String shareName;
    @NotBlank(message = "Share isin cannot be blank")
    private String shareIsin;
    @NotBlank(message = "Country cannot be blank")
    private String country;
    @NotBlank(message = "Field of economic activity cannot be blank")
    private String fieldOfEconomicActivity;


    public double getTotalPrice() {
        return volumeOfShares * pricePerShare;
    }

    public Purchase() {
    }

    public Purchase(long id, double pricePerShare, int volumeOfShares, LocalDate acquiredDate, String employeeId, String companyName, String shareName, String shareIsin, String country, String fieldOfEconomicActivity) {
        this.id = id;
        this.pricePerShare = pricePerShare;
        this.volumeOfShares = volumeOfShares;
        this.acquiredDate = acquiredDate;
        this.employeeId = employeeId;
        this.companyName = companyName;
        this.shareName = shareName;
        this.shareIsin = shareIsin;
        this.country = country;
        this.fieldOfEconomicActivity = fieldOfEconomicActivity;
    }

    public Purchase(double pricePerShare, int volumeOfShares, LocalDate acquiredDate, String employeeId, String companyName, String shareName, String shareIsin, String country, String fieldOfEconomicActivity) {
        this.pricePerShare = pricePerShare;
        this.volumeOfShares = volumeOfShares;
        this.acquiredDate = acquiredDate;
        this.employeeId = employeeId;
        this.companyName = companyName;
        this.shareName = shareName;
        this.shareIsin = shareIsin;
        this.country = country;
        this.fieldOfEconomicActivity = fieldOfEconomicActivity;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public double getPricePerShare() {
        return pricePerShare;
    }

    public void setPricePerShare(double pricePerShare) {
        this.pricePerShare = pricePerShare;
    }

    public int getVolumeOfShares() {
        return volumeOfShares;
    }

    public void setVolumeOfShares(int volumeOfShares) {
        this.volumeOfShares = volumeOfShares;
    }

    public LocalDate getAcquiredDate() {
        return acquiredDate;
    }

    public void setAcquiredDate(LocalDate acquiredDate) {
        this.acquiredDate = acquiredDate;
    }

    public String getEmployeeId() {
        return employeeId;
    }

    public void setEmployeeId(String employeeId) {
        this.employeeId = employeeId;
    }

    public String getCompanyName() {
        return companyName;
    }

    public void setCompanyName(String companyName) {
        this.companyName = companyName;
    }

    public String getShareName() {
        return shareName;
    }

    public void setShareName(String shareName) {
        this.shareName = shareName;
    }

    public String getShareIsin() {
        return shareIsin;
    }

    public void setShareIsin(String shareIsin) {
        this.shareIsin = shareIsin;
    }

    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country;
    }

    public String getFieldOfEconomicActivity() {
        return fieldOfEconomicActivity;
    }

    public void setFieldOfEconomicActivity(String fieldOfEconomicActivity) {
        this.fieldOfEconomicActivity = fieldOfEconomicActivity;
    }

    @Override
    public String toString() {
        return "Purchase{" +
                "id=" + id +
                ", pricePerShare=" + pricePerShare +
                ", volumeOfShares=" + volumeOfShares +
                ", acquiredDate=" + acquiredDate +
                ", employeeId='" + employeeId + '\'' +
                ", companyName='" + companyName + '\'' +
                ", shareName='" + shareName + '\'' +
                ", shareIsin='" + shareIsin + '\'' +
                ", country='" + country + '\'' +
                ", fieldOfEconomicActivity='" + fieldOfEconomicActivity + '\'' +
                '}';
    }
}
