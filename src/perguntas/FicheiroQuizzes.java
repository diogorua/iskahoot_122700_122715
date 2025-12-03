package perguntas;

import java.util.List;

/**
 * Representa o objeto raiz (o ficheiro completo), lido do JSON.
 */
public class FicheiroQuizzes {

    private List<Quiz> quizzes;

    public List<Quiz> getQuizzes() {
        return quizzes;
    }
}