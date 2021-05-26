package org.victor.prjCurrencyConverter;

import static io.restassured.RestAssured.given;
import static org.hamcrest.CoreMatchers.is;

import org.junit.jupiter.api.Test;

import io.quarkus.test.junit.QuarkusTest;

@QuarkusTest
public class ConversionLogResouceTest {
	
	@Test
	public void testGetLogByIdUserEndpoint() {
		
		given()
			.when().get("/currencyConverter/convert/999/USD/BRL/10")
			.then()
				.statusCode(200);
		
		given()
			.when().get("/conversionLog/getByIdUser/999")
			.then()
				.statusCode(200)
				.body("$.size()", is(1));
	}

}
