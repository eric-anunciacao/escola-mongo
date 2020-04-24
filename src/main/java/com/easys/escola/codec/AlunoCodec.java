package com.easys.escola.codec;

import java.util.ArrayList;
import java.util.List;

import org.bson.BsonReader;
import org.bson.BsonString;
import org.bson.BsonValue;
import org.bson.BsonWriter;
import org.bson.Document;
import org.bson.codecs.Codec;
import org.bson.codecs.CollectibleCodec;
import org.bson.codecs.DecoderContext;
import org.bson.codecs.EncoderContext;

import com.easys.escola.model.Aluno;
import com.easys.escola.model.Contato;
import com.easys.escola.model.Curso;
import com.easys.escola.model.Habilidade;
import com.easys.escola.model.Nota;

public class AlunoCodec implements CollectibleCodec<Aluno> {

	private static final String CONTATO = "contato";
	private Codec<Document> codec;

	public AlunoCodec(Codec<Document> codec) {
		this.codec = codec;
	}

	@Override
	public void encode(BsonWriter writer, Aluno aluno, EncoderContext encoder) {
		var id = aluno.getId();
		var nome = aluno.getNome();
		var dataNascimento = aluno.getDataNascimento();
		var curso = aluno.getCurso();
		var habilidades = aluno.getHabilidades();
		var notas = aluno.getNotas();
		var contato = aluno.getContato();

		var documento = new Document();
		documento.put("_id", id);
		documento.put("nome", nome);
		documento.put("data_nascimento", dataNascimento);
		documento.put("curso", new Document("nome", curso.getNome()));

		if (habilidades != null) {
			var habilidadesDocument = new ArrayList<Document>();
			habilidades.stream().forEach(
					h -> habilidadesDocument.add(new Document("nome", h.getNome()).append("nivel", h.getNivel())));
			documento.put("habilidades", habilidadesDocument);
		}

		if (notas != null) {
			var notasParaSalvar = new ArrayList<>();
			notas.stream().map(Nota::getValor).forEach(notasParaSalvar::add);
			documento.put("notas", notasParaSalvar);
		}

		var coordinates = new ArrayList<Double>();
		contato.getCoordinates().stream().forEach(coordinates::add);

		documento.put(CONTATO, new Document().append("endereco", contato.getEndereco())
				.append("coordinates", coordinates).append("type", contato.getType()));

		codec.encode(writer, documento, encoder);
	}

	@Override
	public Class<Aluno> getEncoderClass() {
		return Aluno.class;
	}

	@Override
	@SuppressWarnings("unchecked")
	public Aluno decode(BsonReader reader, DecoderContext decoder) {
		var document = codec.decode(reader, decoder);
		var aluno = new Aluno();
		aluno.setId(document.getObjectId("_id"));
		aluno.setNome(document.getString("nome"));
		var dataNascimento = document.getDate("data_nascimento");
		aluno.setDataNascimento(dataNascimento);
		var curso = (Document) document.get("curso");
		var habilidades = (List<Document>) document.get("habilidades");
		var notas = (List<Double>) document.get("notas");
		var contato = (Document) document.get(CONTATO);

		if (curso != null) {
			var nomeCurso = curso.getString("nome");
			aluno.setCurso(new Curso(nomeCurso));
		}

		if (habilidades != null) {
			var habilidadesDoAluno = new ArrayList<Habilidade>();
			habilidades.stream()
					.forEach(h -> habilidadesDoAluno.add(new Habilidade(h.getString("nome"), h.getString("nivel"))));
			aluno.setHabilidades(habilidadesDoAluno);
		}

		if (notas != null) {
			var notasDoAluno = new ArrayList<Nota>();
			notas.stream().forEach(n -> notasDoAluno.add(new Nota(n)));
			aluno.setNotas(notasDoAluno);
		}

		if (contato != null) {
			var endereco = contato.getString(CONTATO);
			var coordinates = (List<Double>) contato.get("coordinates");
			aluno.setContato(new Contato(endereco, coordinates));
		}

		return aluno;
	}

	@Override
	public Aluno generateIdIfAbsentFromDocument(Aluno aluno) {
		return documentHasId(aluno) ? aluno.criaId() : aluno;
	}

	@Override
	public boolean documentHasId(Aluno aluno) {
		return aluno.getId() == null;
	}

	@Override
	public BsonValue getDocumentId(Aluno aluno) {
		if (!documentHasId(aluno)) {
			throw new IllegalStateException("Esse Document não tem id");
		}
		return new BsonString(aluno.getId().toHexString());
	}

}
