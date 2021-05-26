package org.victor.prjCurrencyConverter.Model;

import java.math.BigDecimal;
import java.util.Map;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class LiveCurrency {
	
	private boolean success;
	private String terms;
	private String privacy;
	private String timestamp;
	private String source;
	private Map<String, BigDecimal> quotes;

}
