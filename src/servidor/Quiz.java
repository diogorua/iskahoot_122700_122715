package servidor;

import java.util.List;

/**
 * Representa um Quiz (um conjunto de perguntas), lido do JSON.
 */

public class Quiz {
    
    private String name;
    private List<Pergunta> questions;

  
    public String getName() {
        return name;
    }

    public List<Pergunta> getQuestions() {
        return questions;
    }
    
}

