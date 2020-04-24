package com.easys.escola.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.easys.escola.model.Aluno;
import com.easys.escola.repository.AlunoRepository;
import com.easys.escola.service.GeolocalizacaoService;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Controller
@RequestMapping("/aluno")
public class AlunoController {

	@Autowired
	private AlunoRepository repository;

	@Autowired
	private GeolocalizacaoService geoService;

	@GetMapping("/cadastrar")
	public String cadastrar(Model model) {
		model.addAttribute("aluno", new Aluno());
		return "aluno/cadastrar";
	}

	@PostMapping("/salvar")
	public String salvar(@ModelAttribute Aluno aluno) {
		log.info("Aluno para salvar: " + aluno);
		try {
			var coordinates = geoService.obterLatELongPor(aluno.getContato());
			aluno.getContato().setCoordinates(coordinates);
			repository.salvar(aluno);
		} catch (Exception e) {
			log.error("Endereço não localizado. " + e);
		}
		return "redirect:/";
	}

	@GetMapping("/listar")
	public String listar(Model model) {
		model.addAttribute("alunos", repository.findAll());
		return "aluno/listar";
	}

	@GetMapping("/visualizar/{id}")
	public String visualizar(@PathVariable String id, Model model) {
		var aluno = repository.findById(id);
		model.addAttribute("aluno", aluno);
		return "aluno/visualizar";
	}

	@GetMapping("/pesquisarnome")
	public String pesquisarNome() {
		return "aluno/pesquisarnome";
	}

	@GetMapping("/pesquisar")
	public String pesquisar(@RequestParam String nome, Model model) {
		var alunos = repository.findByNome(nome);
		model.addAttribute("alunos", alunos);
		return "aluno/pesquisarnome";
	}
}
