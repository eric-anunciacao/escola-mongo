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

import com.easys.escola.model.Nota;
import com.easys.escola.repository.AlunoRepository;

@Controller
@RequestMapping("/nota")
public class NotaController {

	@Autowired
	private AlunoRepository alunoRepository;

	@GetMapping("/cadastrar/{id}")
	public String cadastrar(@PathVariable String id, Model model) {
		var aluno = alunoRepository.findById(id);
		model.addAttribute("aluno", aluno);
		model.addAttribute("nota", new Nota());
		return "nota/cadastrar";
	}

	@PostMapping("/salvar/{id}")
	public String salvar(@PathVariable String id, @ModelAttribute Nota nota) {
		var aluno = alunoRepository.findById(id);
		alunoRepository.salvar(aluno.adiciona(nota));
		return "redirect:/aluno/listar";
	}

	@GetMapping("/iniciarpesquisa")
	public String iniciarPesquisa() {
		return "nota/pesquisar";
	}

	@GetMapping("/pesquisar")
	public String pesquisar(@RequestParam String classificacao, @RequestParam("notacorte") String notaCorte,
			Model model) {
		var alunos = alunoRepository.pesquisarPor(classificacao, Double.parseDouble(notaCorte));
		model.addAttribute("alunos", alunos);
		return "nota/pesquisar";
	}
}
