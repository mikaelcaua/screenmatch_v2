package br.com.alura.screenmatch.model;

public class Serie {
    private String titulo;
    private Integer totalTemporadas;
    private Double avaliacao;
    private Categoria genero;
    private String atores;
    private String poster;
    private String sinopse;

    public Serie(DadosSerie dadosSerie) {
        this.titulo = dadosSerie.titulo();
        this.totalTemporadas = dadosSerie.totalTemporadas();

        try {
            this.avaliacao = Double.parseDouble(dadosSerie.avaliacao());
        }
        catch (Exception e){
            this.avaliacao = 0.0;
        }

        this.genero = Categoria.fromString(dadosSerie.genero().split(",")[0].trim());
        this.atores = dadosSerie.atores();
        this.poster = dadosSerie.poster();
        this.sinopse = dadosSerie.sinopse();
    }

    public Categoria getGenero() {
        return genero;
    }

    @Override
    public String toString() {
        return  "titulo='" + titulo + '\n' +
                "totalTemporadas=" + totalTemporadas +'\n' +
                "avaliacao=" + avaliacao +'\n' +
                "genero=" + genero +'\n' +
                "atores='" + atores + '\n' +
                "poster='" + poster + '\n' +
                "sinopse='" + sinopse + '\n' ;
    }
}
