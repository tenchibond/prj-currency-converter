package org.victor.prjCurrencyConverter;

import static io.restassured.RestAssured.given;
import static org.hamcrest.CoreMatchers.is;

import org.junit.jupiter.api.Test;

import io.quarkus.test.junit.QuarkusTest;

@QuarkusTest
public class CurrencyConverterResourceTest {

	@Test
    public void testConvertEndpoint() {
    	given()
    		.when().get("/currencyConverter/convert/123/USD/BRL/1")
    		.then()
    			.statusCode(200)
    			.body("IdUser", is("123"))
    			.body("FromCurrency", is("USD"))
    			.body("ToCurrency", is("BRL"))
    			.body("Value", is("1"));
    			
    }

}