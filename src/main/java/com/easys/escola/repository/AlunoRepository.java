package com.easys.escola.repository;

import java.util.ArrayList;
import java.util.List;

import org.bson.Document;
import org.bson.codecs.configuration.CodecRegistries;
import org.bson.types.ObjectId;
import org.springframework.stereotype.Repository;

import com.easys.escola.codec.AlunoCodec;
import com.easys.escola.model.Aluno;
import com.mongodb.MongoClient;
import com.mongodb.MongoClientOptions;
import com.mongodb.client.MongoCursor;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.model.Filters;
import com.mongodb.client.model.Indexes;
import com.mongodb.client.model.geojson.Point;
import com.mongodb.client.model.geojson.Position;

@Repository
public class AlunoRepository {

	private static final String ALUNOS_COLLECTION = "alunos";
	private MongoClient client;
	private MongoDatabase database;

	public void salvar(Aluno aluno) {
		criarConexao();
		var alunos = database.getCollection(ALUNOS_COLLECTION, Aluno.class);
		if (aluno.getId() == null) {
			alunos.insertOne(aluno);
		} else {
			alunos.updateOne(Filters.eq("_id", aluno.getId()), new Document("$set", aluno));
		}
		fecharConexao();
	}

	public List<Aluno> findAll() {
		criarConexao();
		var alunos = this.database.getCollection(ALUNOS_COLLECTION, Aluno.class);
		var results = alunos.find().iterator();
		var alunosEncontrados = popularAlunos(results);
		fecharConexao();
		return alunosEncontrados;
	}

	public Aluno findById(String id) {
		criarConexao();
		var alunos = this.database.getCollection(ALUNOS_COLLECTION, Aluno.class);
		var aluno = alunos.find(Filters.eq("_id", new ObjectId(id))).first();
		fecharConexao();
		return aluno;
	}

	public List<Aluno> findByNome(String nome) {
		criarConexao();
		var collection = this.database.getCollection(ALUNOS_COLLECTION);
		var results = collection.find(Filters.eq("nome", nome), Aluno.class).iterator();
		var alunos = popularAlunos(results);
		fecharConexao();
		return alunos;
	}

	private ArrayList<Aluno> popularAlunos(MongoCursor<Aluno> results) {
		var alunos = new ArrayList<Aluno>();
		results.forEachRemaining(alunos::add);
		return alunos;
	}

	public List<Aluno> pesquisarPor(String classificacao, double nota) {
		criarConexao();
		var collection = this.database.getCollection(ALUNOS_COLLECTION);
		var results = new ArrayList<Aluno>();
		MongoCursor<Aluno> iterator = null;
		if ("reprovados".equalsIgnoreCase(classificacao)) {
			iterator = collection.find(Filters.lte("notas", nota), Aluno.class).iterator();
		} else if ("aprovados".equalsIgnoreCase(classificacao)) {
			iterator = collection.find(Filters.gte("notas", nota), Aluno.class).iterator();
		}

		if (iterator != null) {
			iterator.forEachRemaining(results::add);
		}

		fecharConexao();
		return results;
	}

	public List<Aluno> pesquisaPorGeolocalizacao(Aluno aluno) {
		criarConexao();
		var alunoCollection = this.database.getCollection(ALUNOS_COLLECTION, Aluno.class);
		alunoCollection.createIndex(Indexes.geo2dsphere("contato"));
		var coordinates = aluno.getContato().getCoordinates();
		var pontoReferencia = new Point(new Position(coordinates.get(0), coordinates.get(1)));
		var resultados = alunoCollection.find(Filters.nearSphere("contato", pontoReferencia, 2000.0, 0.0)).limit(2)
				.skip(1).iterator();
		var alunos = popularAlunos(resultados);

		fecharConexao();
		return alunos;
	}

	private void criarConexao() {
		var codec = MongoClient.getDefaultCodecRegistry().get(Document.class);
		var alunoCodec = new AlunoCodec(codec);
		var registro = CodecRegistries.fromRegistries(MongoClient.getDefaultCodecRegistry(),
				CodecRegistries.fromCodecs(alunoCodec));
		var options = MongoClientOptions.builder().codecRegistry(registro).build();
		client = new MongoClient("localhost:27017", options);
		database = client.getDatabase("test");
	}

	private void fecharConexao() {
		this.client.close();
	}
}
