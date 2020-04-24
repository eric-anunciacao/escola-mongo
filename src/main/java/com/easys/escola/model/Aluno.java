package com.easys.escola.model;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.bson.types.ObjectId;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class Aluno {

	private ObjectId id;
	private String nome;
	private Date dataNascimento;
	private Curso curso;
	@Getter(AccessLevel.NONE)
	private List<Nota> notas;
	@Getter(AccessLevel.NONE)
	private List<Habilidade> habilidades;
	private Contato contato;

	public Aluno criaId() {
		setId(new ObjectId());
		return this;
	}

	public List<Habilidade> getHabilidades() {
		if (this.habilidades == null) {
			this.habilidades = new ArrayList<>();
		}
		return this.habilidades;
	}

	public List<Nota> getNotas() {
		if (this.notas == null) {
			this.notas = new ArrayList<>();
		}
		return this.notas;
	}

	public Aluno adiciona(Habilidade habilidade) {
		getHabilidades().add(habilidade);
		return this;
	}

	public Aluno adiciona(Nota nota) {
		getNotas().add(nota);
		return this;
	}
}
