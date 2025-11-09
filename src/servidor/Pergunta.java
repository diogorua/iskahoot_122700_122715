package servidor;

import java.util.List;

/**
 * Representa uma única pergunta, lida do JSON.
 */

public class Pergunta {
   
    private String question;
    private int points;
    private int correct;
    private List<String> options;


    public String getQuestion() {
        return question;
    }

    public int getPoints() {
        return points;
    }

    public int getCorrectOptionIndex() {
        return correct;
    }

    public List<String> getOptions() {
        return options;
    }
 
}
