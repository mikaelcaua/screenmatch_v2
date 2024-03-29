package br.com.alura.screenmatch.principal;

import br.com.alura.screenmatch.model.*;
import br.com.alura.screenmatch.repository.SerieRepository;
import br.com.alura.screenmatch.service.ConsumoApi;
import br.com.alura.screenmatch.service.ConverteDados;

import java.util.*;
import java.util.stream.Collectors;

public class Principal {

    public Principal() {
    }

    private SerieRepository repository;

    public Principal(SerieRepository repository) {
        this.repository = repository;
    }

    private Scanner leitura = new Scanner(System.in);
    private ConsumoApi consumo = new ConsumoApi();
    private ConverteDados conversor = new ConverteDados();
//    private List<DadosSerie> listDadosSeries = new ArrayList<>();
    private List<Serie> listSeries = new ArrayList<>();
    private final String ENDERECO = "https://www.omdbapi.com/?t=";
    private final String API_KEY = "&apikey=6585022c";

    public void exibeMenu() {
        var menu = """
                
                
                1 - Buscar séries
                2 - Buscar episódios
                3 - Mostrar Séries Buscadas
                4 - Buscar série por titulo
                5 - Buscar série por autor
                6 - Top 5 séries
                7 - Buscar Por Gênero
                8 - Top 5 episódios de uma série
                0 - Sair                                 
                """;

        int opcao;
        boolean loop = true;
        while (loop){
            listSeries = repository.findAll();
            System.out.println(menu);
            opcao = leitura.nextInt();
            leitura.nextLine();

            switch (opcao) {
                case 1:
                    buscarSerieWeb();
                    break;
                case 2:
                    buscarEpisodioPorSerie();
                    break;
                case 3:
                    mostrarSeriesBuscadas();
                    break;
                case 4:
                    buscarSeriePorTitulo();
                    break;
                case 5:
                    buscarSeriePorAutor();
                    break;
                case 6:
                    listarMelhoresSeries();
                    break;
                case 7:
                    buscarPorGenero();
                    break;
                case 8:
                    topFiveEpisodesSerie();
                    break;
                case 0:
                    System.out.println("Saindo...");
                    loop = false;
                    break;
                default:
                    System.out.println("Opção inválida");
            }
        }
    }

    private void mostrarSeriesBuscadas(){
        ordenarSeriesPorGenero();
        listSeries.forEach(System.out::println);
    }

    private void ordenarSeriesPorGenero(){
        listSeries = listSeries.stream().sorted(Comparator.comparing(Serie::getGenero))
                                .collect(Collectors.toList());
    }

    private void buscarSerieWeb() {
        DadosSerie dados = getDadosSerie();
        repository.save(new Serie(dados));
    }

    private DadosSerie getDadosSerie() {
        System.out.println("Digite o nome da série para busca");
        var nomeSerie = leitura.nextLine();
        var json = consumo.obterDados(ENDERECO + nomeSerie.replace(" ", "+") + API_KEY);
        DadosSerie dados = conversor.obterDados(json, DadosSerie.class);
        return dados;
    }

    private void buscarEpisodioPorSerie(){
        System.out.println("Escolha uma serie pelo nome para mostrar os episódios:");
        var nomeserie = leitura.nextLine();

        Optional<Serie> serieOptional = repository.findByTituloContainingIgnoreCase(nomeserie);
        List<DadosTemporada> temporadas = new ArrayList<>();

        if(serieOptional.isPresent()){
            var serieEncontrada = serieOptional.get();

            for (int i = 1; i <= serieEncontrada.getTotalTemporadas(); i++) {
                var json = consumo.obterDados(ENDERECO + serieEncontrada.getTitulo().replace(" ", "+") + "&season=" + i + API_KEY);
                DadosTemporada dadosTemporada = conversor.obterDados(json, DadosTemporada.class);
                temporadas.add(dadosTemporada);
            }


            List<Episodio> episodios = temporadas.stream().
                    flatMap(d-> d.episodios().stream().
                    map(e-> new Episodio(d.numero(),e)))
                    .collect(Collectors.toList());

            serieEncontrada.setEpisodios(episodios);
            serieEncontrada.getEpisodios().forEach(System.out::println);
            repository.save(serieEncontrada);

        }
        else{
            System.out.println("Série não encontrada");
        }

    }

    private void buscarSeriePorTitulo(){
        System.out.println("Digite o nome da série que você deseja buscar: ");
        String titulo = leitura.nextLine();
        Optional <Serie> serie = repository.findByTituloContainingIgnoreCase(titulo);
        if(serie.isPresent()){
            Serie buscada = serie.get();
            System.out.println("Série encontrada:"+ buscada);
        }
        else{
            System.out.println("Série não encontrada no banco");
        }

    }

    private void buscarSeriePorAutor(){
        System.out.println("Digite o nome do autor:");
        String nomeAutor = leitura.nextLine();

        Optional<List<Serie>> series = repository.findByAtoresContainingIgnoreCase(nomeAutor);

        if(series.isPresent()){
            List<Serie> buscadas = series.get();
            System.out.println("Séries em que "+nomeAutor+" trabalhou:");
            buscadas.forEach(s -> System.out.println(s.getTitulo()));
        }
        else{
            System.out.println("Nenhuma série foi encontrada no banco");
        }


    }

    private void listarMelhoresSeries(){
        Optional<List<Serie>> series = repository.findTop5ByOrderByAvaliacaoDesc();
        if(series.isPresent()){
            List<Serie> ordenadas = series.get();
            ordenadas.forEach(s -> System.out.println(s.getTitulo()+ " -- "+ s.getAvaliacao()));
        }
        else{
            System.out.println("Não foi possivel ordenar as series");
        }
    }

    private void buscarPorGenero(){
        System.out.println("Digite o genero das séries que você deseja buscar: ");
        String genero = leitura.nextLine();
        Categoria categoria = Categoria.fromStringPortugues(genero);
        Optional<List<Serie>> series = repository.findByGenero(categoria);
        if(series.isPresent()){
            List <Serie> buscadas = series.get();
            buscadas.forEach(s -> System.out.println(s.getTitulo() + " -- "+s.getGenero()));
        }
    }

    private void topFiveEpisodesSerie(){
        Optional<List<Episodio>> episodios = repository.orderEpisodes("Breaking Bad");
        if(episodios.isPresent()){
            List<Episodio> melhores = episodios.get();
            melhores.forEach(e -> System.out.println(e.getTitulo()+ " -- "+e.getAvaliacao()));
        }
    };
}