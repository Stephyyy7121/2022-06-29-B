package it.polito.tdp.itunes.model;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.jgrapht.Graphs;
import org.jgrapht.graph.DefaultWeightedEdge;
import org.jgrapht.graph.SimpleDirectedWeightedGraph;

import it.polito.tdp.itunes.db.ItunesDAO;

public class Model {
	
	//PARTE 1 punto A
	private ItunesDAO dao;
	private SimpleDirectedWeightedGraph<Album, DefaultWeightedEdge> graph;
	private List<Album> allAlbum;
	
	
	public Model() {
		this.dao = new ItunesDAO();
		this.graph = new SimpleDirectedWeightedGraph<>(DefaultWeightedEdge.class);
		this.allAlbum = new ArrayList<Album>();
	}
	
	
	//NODI
	//input = n = durata totale di un album
	public void loadNodes(int n) {
		
		//riempire la lista dei nodi  --> creare il metodo nel dao
		if (this.allAlbum.isEmpty()) {
			this.allAlbum = dao.getFilteredAlbum(n);
		}
	}
	
	//creazione grafo
	public void buildGraph(int n) {
		
		clearGraph();
		loadNodes(n);
		
		//aggiungere i nodi
		Graphs.addAllVertices(this.graph, this.allAlbum);
		
		//aggiungere gli archi
		
		//due album sonon collegati se : 
		// 1) durata diversa 
		// 2) la somma delle durate > 4*n
		
		for (Album a1 : allAlbum) {
			for (Album a2 : allAlbum) {
				int sommaSecondi = a1.getTotSecondi() + a2.getTotSecondi();
				if (a1.getTotSecondi()!= a2.getTotSecondi() && sommaSecondi > 4*n) {
					if (a1.getTotSecondi() < a2.getTotSecondi()) {
						Graphs.addEdgeWithVertices(this.graph, a1, a2, sommaSecondi);
					}
					
				}
			}
		}
		
	}
	
	//pulizia grafo 
	public void clearGraph() {
		this.allAlbum = new ArrayList<Album>();
		this.graph = new SimpleDirectedWeightedGraph<>(DefaultWeightedEdge.class); 
	}
	
	//PARTE 1 PUNTO D 
	public int getBilancio(Album a) {
		
		int bilancio = 0;
		//restituisce tutti gli edge che ENTRANO in album
		List<DefaultWeightedEdge> edgesin  = new ArrayList<>(this.graph.incomingEdgesOf(a))  ; 
		List<DefaultWeightedEdge> edgesOut = new ArrayList<>(this.graph.outgoingEdgesOf(a));
		
		for (DefaultWeightedEdge edge : edgesin) {
			bilancio += this.graph.getEdgeWeight(edge);
		}
		
		for (DefaultWeightedEdge edge : edgesOut) {
			bilancio -= this.graph.getEdgeWeight(edge);
		}
		
		return bilancio;
	}
	
	//album adiacenti ad un nodo --> NECESSARIO CREARE UNA CLASSE BilancioAlbum per poter creare una lista ordinata rispetto al bilancio
		
	public List<BilancioAlbum> getAdiacenti(Album album ) {
		
		/*List<Album> successors = Graphs.successorListOf(this.graph, a);
		
		List<BilancioAlbum> adiacenti = new ArrayList<>();
		
		for (Album a1 : successors) {
			adiacenti.add(new BilancioAlbum(a, getBilancio(a)));
		}
		
		Collections.sort(adiacenti);
		return adiacenti;*/
		
		List<Album> successori = Graphs.successorListOf(this.graph, album); //no neighbour perche' fornisce anche quelli entranti
		List<BilancioAlbum> bilancioSuccessori = new ArrayList<>();
		
		for (Album a : successori) {
			bilancioSuccessori.add(new BilancioAlbum(a, getBilancio(a)));
		}
		
		Collections.sort(bilancioSuccessori);
		return bilancioSuccessori;
		
		
	}


	public int getVertices() {
		// TODO Auto-generated method stub
		return this.graph.vertexSet().size();
	}
	
	public int getEdge() {
		return this.graph.edgeSet().size();
	}


	public List<Album> getVerticesSet() {
		// TODO Auto-generated method stub
		List<Album> risultato = new ArrayList<Album>(this.graph.vertexSet());
		Collections.sort(risultato);
		return risultato;
	}
}
