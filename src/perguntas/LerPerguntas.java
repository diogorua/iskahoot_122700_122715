package perguntas;

import com.google.gson.Gson;

import java.io.FileReader;
import java.io.IOException;
import java.io.Reader;
import java.util.Collections;
import java.util.List;

public class LerPerguntas {

    /**
     * Lê um ficheiro JSON e devolve a lista de Quizzes.
     * @return A lista de quizzes, ou uma lista vazia se falhar.
     */
	
    public static List<Quiz> carregarQuizzesDoFicheiro(String caminhoFicheiro) {
       
        Gson gson = new Gson();

      
        try (Reader reader = new FileReader(caminhoFicheiro)) {
            
            FicheiroQuizzes ficheiro = gson.fromJson(reader, FicheiroQuizzes.class);
            
            if (ficheiro != null && ficheiro.getQuizzes() != null) {
                System.out.println(ficheiro.getQuizzes().size() + " quizzes carregados.");
                return ficheiro.getQuizzes();
            }

        } catch (IOException e) {
            System.err.println("Erro a ler o ficheiro de perguntas: " + e.getMessage());
        } catch (com.google.gson.JsonSyntaxException e) {
            System.err.println("Erro de sintaxe no ficheiro JSON: " + e.getMessage());
        }

        //retorna uma lista vazia se correr mal
        return Collections.emptyList();
    }
}
