package com.mclebtec.demo.route;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.extern.slf4j.Slf4j;
import org.apache.camel.Exchange;
import org.apache.camel.Predicate;
import org.apache.camel.Processor;
import org.apache.camel.builder.RouteBuilder;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component
@Data
@EqualsAndHashCode(callSuper = true)
@Slf4j
public class CamelDemoSQLRoute extends RouteBuilder {
    @Override
    public void configure() throws Exception {
        /*
         * @formatter:off
         * SELECT name FROM person WHERE id=1;
         * Table creation script
         * CREATE TABLE `person` ( `id` INT(11) NOT NULL AUTO_INCREMENT, `Name`
         * VARCHAR(5) NOT NULL, PRIMARY KEY (`id`) ) COLLATE='latin1_swedish_ci'
         * ENGINE=InnoDB AUTO_INCREMENT=12 ;
         */

        from("timer://dbQueryTimer?period=500000").routeId("SqlPaginationRoute")
                // .setBody(new ParameterMapSupplier())
                // .to("sql:SELECT version()?dataSource=#dataSource")
                // .to("sql:SELECT name FROM person WHERE id=:#id?dataSource=#dataSource")
                .loopDoWhile(new CheckIfResultSetIsEmptyPredicate())
                // .setBody(new ParameterMapSupplier())
                .process(new SqlQueryParameterSetterProcessor())
                // .to("sql:SELECT NAME FROM PERSON WHERE ID = 1 limit:#startRowNum,:#pageSize"
                .to("sql:SELECT ID,NAME FROM(SELECT per.*,ROW_NUMBER() OVER(ORDER BY per.id ASC) RANK FROM person per)WHERE RANK BETWEEN :#startRowNum AND :#pageSize ORDER BY RANK"
                        + "?dataSource=#dataSource")
                .log("******STEP 20: Database query executed - body:${body}******").end()
                .log("******STEP 100: Pagination Completed!!!******");
        Thread.sleep(10000);
    }

    private static final class SqlQueryParameterSetterProcessor implements Processor {
        public void process(Exchange exchange) throws Exception {
            int camelLoopIndex = exchange.getProperty(Exchange.LOOP_INDEX, Integer.class);
            int startRowNum = camelLoopIndex * 1;
            int pageSize = 75;
            log.info(Exchange.LOOP_INDEX);
            log.info(String.valueOf(camelLoopIndex));
            Map<String, Integer> sqlQueryParameterMap = new HashMap<String, Integer>();
            sqlQueryParameterMap.put("startRowNum", startRowNum);
            log.info(String.valueOf(startRowNum));
            sqlQueryParameterMap.put("pageSize", pageSize);
            log.info(String.valueOf(sqlQueryParameterMap.get("pageSize")));
            exchange.getIn().setBody(sqlQueryParameterMap);
        }
    }

    private static final class CheckIfResultSetIsEmptyPredicate implements Predicate {
        public boolean matches(Exchange exchange) {
            List resultSetList = exchange.getIn().getBody(List.class);
            boolean resultSetIsNotEmpty = (!resultSetList.isEmpty());
            return resultSetIsNotEmpty;
        }
    }
}
