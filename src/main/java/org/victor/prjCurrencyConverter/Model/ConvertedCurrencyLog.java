package org.victor.prjCurrencyConverter.Model;

import java.util.List;

import javax.persistence.Entity;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import io.quarkus.hibernate.orm.panache.PanacheEntity;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter @Setter
@ToString
@JsonIgnoreProperties(ignoreUnknown = true)
@Entity
public class ConvertedCurrencyLog extends PanacheEntity {
	
	public String idUser;
	public String fromCurrency;
	public String fromValue;
	public String toCurrency;
	public String quote;
	public String time;
	
	public static List<ConvertedCurrencyLog> findByIdUser(String idUser) {
		return find("idUser", idUser).list();
	}

}
