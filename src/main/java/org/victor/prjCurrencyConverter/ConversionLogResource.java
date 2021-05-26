package org.victor.prjCurrencyConverter;

import java.util.List;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import org.eclipse.microprofile.openapi.annotations.Operation;
import org.eclipse.microprofile.openapi.annotations.enums.SchemaType;
import org.eclipse.microprofile.openapi.annotations.media.Content;
import org.eclipse.microprofile.openapi.annotations.media.Schema;
import org.eclipse.microprofile.openapi.annotations.responses.APIResponse;
import org.eclipse.microprofile.openapi.annotations.tags.Tag;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.victor.prjCurrencyConverter.Model.ConvertedCurrencyLog;

@Path("/conversionLog")
public class ConversionLogResource {

	private static final Logger LOGGER = LoggerFactory.getLogger(ConversionLogResource.class);

    @GET
    @Path("/getByIdUser/{idUser}")
    @Produces(MediaType.APPLICATION_JSON)
    @Tag(name = "Buscar logs por usuário")
    @Operation(summary = "Retorna o histórico de conversões realizadas por usuário (usando IdUsuario informado na solicitação de conversão)")
    @APIResponse(responseCode = "200", //
        content = @Content(//
            mediaType = MediaType.APPLICATION_JSON, //
            schema = @Schema(//
                implementation = ConvertedCurrencyLog.class, //
                type = SchemaType.ARRAY)))
    public List<ConvertedCurrencyLog> getLogByIdUser(@PathParam(value = "idUser") String idUser) {
    	LOGGER.info("Consulta de logs de conversao, idUser: " + idUser.toString());
        return ConvertedCurrencyLog.findByIdUser(idUser);
    }
}
