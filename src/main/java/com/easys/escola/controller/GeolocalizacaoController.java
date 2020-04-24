package com.easys.escola.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.easys.escola.repository.AlunoRepository;

@Controller
@RequestMapping("/geolocalizacao")
public class GeolocalizacaoController {

	@Autowired
	private AlunoRepository alunoRepository;

	@GetMapping("/iniciarpesquisa")
	public String inicializarPesquisa(Model model) {
		model.addAttribute("alunos", alunoRepository.findAll());
		return "geolocalizacao/pesquisar";
	}

	@GetMapping("/pesquisar")
	public String pesquisar(@RequestParam("alunoId") String alunoId, Model model) {
		var aluno = alunoRepository.findById(alunoId);
		model.addAttribute("alunosProximos", alunoRepository.pesquisaPorGeolocalizacao(aluno));
		return "geolocalizacao/pesquisar";
	}
}
