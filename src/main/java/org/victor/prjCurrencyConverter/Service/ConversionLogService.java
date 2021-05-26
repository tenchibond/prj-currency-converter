package org.victor.prjCurrencyConverter.Service;

import javax.enterprise.context.ApplicationScoped;
import javax.transaction.Transactional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.victor.prjCurrencyConverter.Model.ConvertedCurrencyLog;

import io.quarkus.vertx.ConsumeEvent;
import io.smallrye.common.annotation.Blocking;

@ApplicationScoped
public class ConversionLogService {
	
	private static final Logger LOGGER = LoggerFactory.getLogger(ConversionLogService.class);
	
	@ConsumeEvent("object")
	@Blocking
	@Transactional
	public void consume(ConvertedCurrencyLog object) {
		object.id = null;
		object.persist();
		LOGGER.info("Recebido do EventBus e gravado: " + object.toString());
	}

}
