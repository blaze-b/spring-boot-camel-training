package com.mclebtec.demo.route;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.camel.Exchange;
import org.apache.camel.LoggingLevel;
import org.apache.camel.Predicate;
import org.apache.camel.Processor;
import org.apache.camel.builder.RouteBuilder;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.mclebtec.demo.bean.PersonDetailsExtractor;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.extern.slf4j.Slf4j;

@Component
@Data
@EqualsAndHashCode(callSuper = true)
@Slf4j
public class CamelDemoRepoRoute extends RouteBuilder {

	private static final Log logger = LogFactory.getLog(CamelDemoRepoRoute.class);
	private List resultSetList = new ArrayList<>();
	private PersonDetailsExtractor personDetailsExtractor;
	@Autowired
	public CamelDemoRepoRoute(PersonDetailsExtractor personDetailsExtractor) {
		this.personDetailsExtractor = personDetailsExtractor;
	}

	@Override
	public void configure() throws Exception {
		// @formatter:off
		Date future = new Date(new Date().getTime() + 1000);
		SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss");
		String time = sdf.format(future);
		System.out.println(time);
		fromF(("timer://dbQueryTimer?period=500000&time=%s&pattern=dd-MM-yyyy HH:mm:ss"), time)
				.routeId("SqlPaginationRoute").loopDoWhile(new CheckIfResultSetIsEmptyPredicate())
				.process(new SqlQueryParameterSetterProcessor())
				// .to("sql:SELECT NAME FROM PERSON WHERE ID = 1 limit:#startRowNum,:#pageSize"
				.log("dbQueryTimer from timer at ${header.firedTime}").process(new StartTimeProcessor())
				.to("sql:SELECT ID,NAME FROM(SELECT per.*,ROW_NUMBER() OVER(ORDER BY per.id ASC) RANK FROM person per)WHERE RANK BETWEEN :#startRowNum AND :#pageSize ORDER BY RANK"
						+ "?dataSource=#dataSource")
				.process(new EndTimeProcessor()).log(LoggingLevel.INFO, "******Database query executed******")
				.log(LoggingLevel.INFO, "******Database query executed start time:${header.start}******")
				.log(LoggingLevel.INFO, "******Database query executed end time:${header.end}******")
				.log("=== route execution time: ${header.start} - ${header.end}")
				.log(LoggingLevel.INFO, "***** body class name:${body.class.name}")
				.log(LoggingLevel.INFO, "***** Actual body content:${body}")
				.log(LoggingLevel.INFO, "***** Camel headers:${headers}")
				.log(LoggingLevel.INFO, "*****STEP 10**** exchangeId:${exchangeId}, id:${id}")
				.log(LoggingLevel.INFO, "*****STEP 20 ${header.CamelFileName}*****")
				.log(LoggingLevel.INFO, "******STEP 30: Database query executed - body:${body}******").end()
				.log("***** STEP-300:\n\nbody:${body}\nstartTime:${header.startTime}\nendTime:${date:now:dd-MM-yyyy-HH:mm:ss}")
				.log(LoggingLevel.INFO, "******STEP 40: Pagination Completed!!!******").to("direct:beeet");

		fromF(("timer://dbQueryTimer?period=500000&time=%s&pattern=dd-MM-yyyy HH:mm:ss"), time)
				.routeId("beanPaginationRoute").loopDoWhile(new CheckIfResultSetIsEmptyPredicate())
				.process(new SqlQueryParameterSetterProcessor())
				// .to("bean:personDetailsExtractor?method=hello(brian)")
				.log(LoggingLevel.INFO, "***** SqlQueryParameterSetterProcessor: headers:${headers}")
				.log(LoggingLevel.INFO, "******SqlQueryParameterSetterProcessor: body:${body}******")
				.log(LoggingLevel.INFO,
						"******SqlQueryParameterSetterProcessor: startRowNum:${body.get(startRowNum)}******")
				.log(LoggingLevel.INFO, "******SqlQueryParameterSetterProcessor: pageSize:${body.get(pageSize)}******")
				.to("bean:personDetailsExtractor?method=extractDetails(*,${body.get(startRowNum)},${body.get(pageSize)})")
				.log(LoggingLevel.INFO, "***** extractPersonDetail: headers:${headers}")
				.log(LoggingLevel.INFO, "******extractPersonDetail: body:${body}******").end()
				.log(LoggingLevel.INFO, "******extractPersonDetail: body:${body}******")
				.log(LoggingLevel.INFO, "******extractPersonDetail: body:${body}******")
				.log(LoggingLevel.INFO, "******STEP 40: Pagination Completed!!!******")
				.log("***** STEP-300:\n\nbody:${body}\nstartTime:${header.startTime}\nendTime:${date:now:dd-MM-yyyy-HH:mm:ss}")
				.to("direct:beeet");

		Thread.sleep(10000);
		// @formatter:on
	}

	private final class SqlQueryParameterSetterProcessor implements Processor {
		public void process(Exchange exchange) throws Exception {
			int camelLoopIndex = exchange.getProperty(Exchange.LOOP_INDEX, Integer.class);
			int startRowNum = camelLoopIndex * 1;
			int pageSize = 75;
			logger.info(Exchange.LOOP_INDEX);
			logger.info(camelLoopIndex);
			Map<String, Integer> sqlQueryParameterMap = new HashMap<String, Integer>();
			sqlQueryParameterMap.put("startRowNum", startRowNum);
			logger.info(startRowNum);
			sqlQueryParameterMap.put("pageSize", pageSize);
			logger.info(sqlQueryParameterMap.get("pageSize"));
			exchange.getIn().setBody(sqlQueryParameterMap);
		}
	}

	private final class CheckIfResultSetIsEmptyPredicate implements Predicate {
		@SuppressWarnings("unchecked")
		public boolean matches(Exchange exchange) {
			resultSetList = exchange.getIn().getBody(List.class);
			System.out.println("resultSetList : "+resultSetList);
			boolean resultSetIsNotEmpty = (!resultSetList.isEmpty());
			System.out.println("resultSetIsNotEmpty : "+resultSetIsNotEmpty);
			return resultSetIsNotEmpty;
		}
	}
	
	private final class StartTimeProcessor implements Processor {
		@Override
		public void process(Exchange exchange) throws Exception {
			// TODO Auto-generated method stub
			Date future = new Date(new Date().getTime() + 1000);
			SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss");
			String time = sdf.format(future);
			System.out.println(time);
			exchange.getIn().setHeader("start", sdf.parse(time).getTime());
		}
	}
	
	private final class EndTimeProcessor implements Processor {
		@Override
		public void process(Exchange exchange) throws Exception {
			// TODO Auto-generated method stub
		    Date future = new Date(new Date().getTime() + 1000);
			SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss");
			String time = sdf.format(future);
			System.out.println(time);
			exchange.getIn().setHeader("end", sdf.parse(time).getTime());
		}
	}
	
	
	@Override
	public String toString() {
		return resultSetList.toString();
	}
}
