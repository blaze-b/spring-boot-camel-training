package com.blazeb.camel.bindy.model;

import java.math.BigDecimal;

import org.apache.camel.dataformat.bindy.annotation.CsvRecord;
import org.apache.camel.dataformat.bindy.annotation.DataField;

@CsvRecord(separator = ",", crlf = "UNIX", generateHeaderColumns = true)
public class CatalogueItem {

    @DataField(pos = 1, columnName = "sku")
    private String sku;

    @DataField(pos = 2, columnName = "title")
    private String title;

    @DataField(pos = 3, columnName = "category")
    private String category;

    @DataField(pos = 4, precision = 2, columnName = "unitPrice")
    private BigDecimal unitPrice;

    public CatalogueItem() {
    }

    public CatalogueItem(String sku, String title, String category, BigDecimal unitPrice) {
        this.sku = sku;
        this.title = title;
        this.category = category;
        this.unitPrice = unitPrice;
    }

    public String getSku() {
        return sku;
    }

    public void setSku(String sku) {
        this.sku = sku;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public BigDecimal getUnitPrice() {
        return unitPrice;
    }

    public void setUnitPrice(BigDecimal unitPrice) {
        this.unitPrice = unitPrice;
    }
}
