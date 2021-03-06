package org.victor.prjCurrencyConverter;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.Instant;
import java.util.UUID;

import javax.annotation.PostConstruct;
import javax.inject.Inject;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import org.apache.http.HttpStatus;
import org.eclipse.microprofile.openapi.annotations.Operation;
import org.eclipse.microprofile.openapi.annotations.enums.SchemaType;
import org.eclipse.microprofile.openapi.annotations.media.Content;
import org.eclipse.microprofile.openapi.annotations.media.Schema;
import org.eclipse.microprofile.openapi.annotations.responses.APIResponse;
import org.eclipse.microprofile.openapi.annotations.tags.Tag;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.victor.prjCurrencyConverter.Model.ConvertedCurrencyLog;
import org.victor.prjCurrencyConverter.Model.LiveCurrency;

import io.smallrye.mutiny.Uni;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.web.client.WebClientOptions;
import io.vertx.mutiny.core.Vertx;
import io.vertx.mutiny.core.eventbus.EventBus;
import io.vertx.mutiny.ext.web.client.WebClient;


@Path("/currencyConverter")
public class CurrencyConverterResource {
	
	private static final Logger LOGGER = LoggerFactory.getLogger(CurrencyConverterResource.class);
	
	@Inject Vertx vertx;
	
	private WebClient webClient;
	
	@Inject EventBus eventBus;
	
	@PostConstruct
	void initialize() {
		this.webClient = WebClient.create(vertx, new WebClientOptions()
				.setDefaultHost("api.currencylayer.com").setTrustAll(true));
		LOGGER.info("Webclient criado!");		
	}

	@GET
	@Produces(MediaType.APPLICATION_JSON)
	@Path("/convert/{idUser}/{fromCurrency}/{toCurrency}/{value}")
	@Tag(name = "Conversor entre moedas")
    @Operation(summary = "Converte determinado valor informado entre duas moedas")
    @APIResponse(responseCode = "200", //
        content = @Content(//
            mediaType = MediaType.APPLICATION_JSON, //
            schema = @Schema(//
                implementation = JsonObject.class, //
                type = SchemaType.ARRAY)))
	public Uni<JsonObject> convert(@PathParam(value = "idUser") Integer idUser,
			@PathParam(value = "fromCurrency") String fromCurrency,
			@PathParam(value = "toCurrency") String toCurrency,
			@PathParam(value = "value") BigDecimal value) {
		
		return webClient.get("/live?access_key=acaa5f36cac3803f24a16697188ec056").send()
				.onItem().transform(response -> {
					LOGGER.info("Dentro do response");
					if(response.statusCode() == 200) {
						//conversao do response em objeto java
						LiveCurrency live = response.bodyAsJson(LiveCurrency.class);
						
						/*
						 * para realizar a conversao, faz-se o seguinte:
						 * 1) caso toCurrency seja USD: busca fromCurrency no Map, divide pelo valor
						 * 2) caso fromCurrency seja USD: busca toCurrency no Map, multiplica pelo valor
						 * 3) fromCurrency e toCurrency diferentes de USD: busca a cotacao de ambas, no Map, para
						 * USD e converte o valor inicialmente para USD, depois converte o resultado para a
						 * moeda final 
						 * (valor / fromCurrency -> USD) * USD -> toCurrency
						 *   
						 * */
						
						//caso 1: toCurrency == USD
						if(toCurrency.equalsIgnoreCase("USD")) {
							BigDecimal quote = live.getQuotes().get("USD"+fromCurrency.toUpperCase());
							BigDecimal valueConverted = value.divide(quote, 2, RoundingMode.HALF_EVEN);
							
							JsonObject obj = new JsonObject()
									.put("Id", UUID.randomUUID().toString())
									.put("IdUser", idUser.toString())
									.put("FromCurrency", fromCurrency)
									.put("Value", value.toString())
									.put("ToCurrency", toCurrency)
									.put("ValueConverted", valueConverted.toString())
									.put("Quote", quote.toString())
									.put("Time", Instant.now().toString());
							
							sendConvertedCurrencyToLog(obj);
							
							return obj;
						}
						
						//caso 2: fromCurrency == USD
						if(fromCurrency.equalsIgnoreCase("USD")) {
							BigDecimal quote = live.getQuotes().get("USD"+toCurrency.toUpperCase());
							BigDecimal valueConverted = value.multiply(quote).setScale(2, RoundingMode.HALF_EVEN);
							
							JsonObject obj = new JsonObject()
									.put("Id", UUID.randomUUID().toString())
									.put("IdUser", idUser.toString())
									.put("FromCurrency", fromCurrency)
									.put("Value", value.toString())
									.put("ToCurrency", toCurrency)
									.put("ValueConverted", valueConverted.toString())
									.put("Quote", quote.toString())
									.put("Time", Instant.now().toString());
							
							sendConvertedCurrencyToLog(obj);
							
							return obj;
						}
						
						//caso 3: toCurrency && fromCurrency <> USD
						if(!toCurrency.equalsIgnoreCase("USD") && !fromCurrency.equalsIgnoreCase("USD")) {
							BigDecimal quoteToUSD = live.getQuotes().get("USD"+fromCurrency.toUpperCase());
							BigDecimal quoteFromUSD = live.getQuotes().get("USD"+toCurrency.toUpperCase());
							BigDecimal valueConverted = value
									.divide(quoteToUSD, 3, RoundingMode.HALF_EVEN)
									.multiply(quoteFromUSD).setScale(2, RoundingMode.HALF_EVEN);
							JsonObject obj = new JsonObject()
									.put("Id", UUID.randomUUID().toString())
									.put("IdUser", idUser.toString())
									.put("FromCurrency", fromCurrency)
									.put("Value", value.toString())
									.put("ToCurrency", toCurrency)
									.put("ValueConverted", valueConverted.toString())
									.put("Quote", "0")
									.put("Time", Instant.now().toString());
							
							sendConvertedCurrencyToLog(obj);
							
							return obj;
						}
						
						return new JsonObject()
								.put("code", HttpStatus.SC_BAD_REQUEST)
								.put("message", "no conversion currency found!");
					} else {
						return new JsonObject()
								.put("code", HttpStatus.SC_BAD_REQUEST)
								.put("message", response.bodyAsJsonObject());
					}
				});
	}
	
	private void sendConvertedCurrencyToLog(JsonObject jsonObject) {
		ConvertedCurrencyLog converted = new ConvertedCurrencyLog();
		converted.setFromCurrency( jsonObject.getString("FromCurrency") );
		converted.setFromValue( jsonObject.getString("Value") );
		converted.setIdUser( jsonObject.getString("IdUser") );
		converted.setQuote( jsonObject.getString("Quote") );
		converted.setTime( jsonObject.getString("Time") );
		converted.setToCurrency( jsonObject.getString("ToCurrency") );
		eventBus.sendAndForget("object", converted);
	}
}