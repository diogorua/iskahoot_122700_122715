package perguntas;

import java.io.Serializable;
import java.util.List;

/**
 * Representa uma única pergunta, lida do JSON.
 */

public class Pergunta implements Serializable {
   
    private static final long serialVersionUID = 1L;
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
