package com.mclebtec.demo.integration.camel.recipientlist.route.bean;

import org.apache.camel.RecipientList;
import org.apache.camel.language.XPath;

public class RecipientListBean {
	@RecipientList
	   public String[] route(@XPath("/order/@customer") String customer) {
        if (isGoldCustomer(customer)) {
            return new String[] {"activemq:queue:accounting2", "activemq:queue:production2"};
        } else {
            return new String[] {"activemq:queue:accounting2"};
        }
    }

    private boolean isGoldCustomer(String customer) {
        return customer.equals("joe's bikes");
    }
}
