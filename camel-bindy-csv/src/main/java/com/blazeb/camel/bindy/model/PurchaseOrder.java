package com.blazeb.camel.bindy.model;

import java.math.BigDecimal;

import org.apache.camel.dataformat.bindy.annotation.CsvRecord;
import org.apache.camel.dataformat.bindy.annotation.DataField;

@CsvRecord(separator = ",", crlf = "UNIX")
public class PurchaseOrder {

    @DataField(pos = 1)
    private String name;

    @DataField(pos = 2, precision = 2)
    private BigDecimal price;

    @DataField(pos = 3)
    private int amount;

    public PurchaseOrder() {
    }

    public PurchaseOrder(String name, BigDecimal price, int amount) {
        this.name = name;
        this.price = price;
        this.amount = amount;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public BigDecimal getPrice() {
        return price;
    }

    public void setPrice(BigDecimal price) {
        this.price = price;
    }

    public int getAmount() {
        return amount;
    }

    public void setAmount(int amount) {
        this.amount = amount;
    }

    @Override
    public String toString() {
        return "PurchaseOrder{name='%s', price=%s, amount=%d}".formatted(name, price, amount);
    }
}
