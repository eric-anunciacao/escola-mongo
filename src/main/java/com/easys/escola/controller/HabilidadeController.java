package com.easys.escola.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import com.easys.escola.model.Habilidade;
import com.easys.escola.repository.AlunoRepository;

@Controller
@RequestMapping("/habilidade")
public class HabilidadeController {

	@Autowired
	private AlunoRepository alunoRepository;

	@GetMapping("/cadastrar/{id}")
	public String cadastrar(@PathVariable String id, Model model) {
		model.addAttribute("aluno", alunoRepository.findById(id));
		model.addAttribute("habilidade", new Habilidade());
		return "habilidade/cadastrar";
	}

	@PostMapping("/salvar/{id}")
	public String salvar(@PathVariable String id, @ModelAttribute Habilidade habilidade) {
		var aluno = alunoRepository.findById(id);
		alunoRepository.salvar(aluno.adiciona(habilidade));
		return "redirect:/aluno/listar";
	}
}
