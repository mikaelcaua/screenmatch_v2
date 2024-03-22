package br.com.alura.screenmatch.repository;

import br.com.alura.screenmatch.model.Categoria;
import br.com.alura.screenmatch.model.Episodio;
import br.com.alura.screenmatch.model.Serie;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface SerieRepository extends JpaRepository<Serie, Long> {
    Optional<Serie> findByTituloContainingIgnoreCase(String tituloSerie);
    Optional<List<Serie>> findByAtoresContainingIgnoreCase(String nomeAutor);
    Optional<List<Serie>> findTop5ByOrderByAvaliacaoDesc();
    Optional<List<Serie>> findByGenero(Categoria categoria);
    @Query("SELECT episodes FROM Serie s JOIN s.episodios episodes ORDER BY episodes.avaliacao DESC LIMIT 5")
    Optional<List<Episodio>> orderEpisodes(String nomeSerie);
}
