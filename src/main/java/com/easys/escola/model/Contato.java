package com.easys.escola.model;

import java.util.List;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class Contato {

	private String endereco;
	private List<Double> coordinates;
	private String type = "Point";

	public Contato(String endereco, List<Double> coordinates) {
		this.endereco = endereco;
		this.coordinates = coordinates;
	}

}
