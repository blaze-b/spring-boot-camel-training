package com.springboot.order.converter;

import org.apache.camel.Converter;
import org.apache.camel.Exchange;
import org.apache.camel.TypeConverter;

import java.math.BigDecimal;

@Converter
public class PurchaseOrderConverter {
    @Converter
    public static PurchaseOrder toPurchaseOrder(byte[] data, Exchange exchange) {
        TypeConverter converter = exchange.getContext().getTypeConverter();
        String s = converter.convertTo(String.class, data);
        if (s == null || s.length() < 30) {
            throw new IllegalArgumentException("data is invalid");
        }
        s = s.replaceAll("##START##", "");
        s = s.replaceAll("##END##", "");
        String name = s.substring(0, 9).trim();
        String s2 = s.substring(10, 19).trim();
        String s3 = s.substring(20).trim();
        BigDecimal price = new BigDecimal(s2);
        Integer amount = converter.convertTo(Integer.class, s3);
        price.setScale(2);
        PurchaseOrder order = new PurchaseOrder(name, price, amount);
        return order;
    }

}
