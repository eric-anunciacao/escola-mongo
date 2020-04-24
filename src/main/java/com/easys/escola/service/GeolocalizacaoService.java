package com.easys.escola.service;

import java.io.IOException;
import java.util.List;

import org.springframework.stereotype.Service;

import com.easys.escola.model.Contato;
import com.google.maps.GeoApiContext;
import com.google.maps.GeocodingApi;
import com.google.maps.errors.ApiException;

@Service
public class GeolocalizacaoService {

	public List<Double> obterLatELongPor(Contato contato) throws ApiException, InterruptedException, IOException {
		var context = new GeoApiContext().setApiKey("AIzaSyAIPw0BOFbsavl2S0XJm366GqIL8WaL4xw");
		var request = GeocodingApi.newRequest(context).address(contato.getEndereco());
		var results = request.await();
		var result = results[0];
		var location = result.geometry.location;
		return List.of(location.lat, location.lng);
	}
}
