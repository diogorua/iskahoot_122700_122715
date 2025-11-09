package cliente;

import java.awt.*;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.util.List;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;


public class GUI {
    
    private JFrame frame;
    private JButton[] optionButtons;
    private boolean answered = false;
    
    private JLabel lblQuestion;
    private JLabel lblTimer;
    private JLabel lblPlayerName;
    private JLabel lblScore;
    
    private javax.swing.Timer swingTimer;
    private int tempoRestante;
    
    private Main main;

    public GUI() {
        frame = new JFrame("IsKahoot");
        
        frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        frame.setLayout(new BorderLayout(15, 15));
        frame.getContentPane().setBackground(new Color(245, 245, 245));
        
        addContent();
        
        ActionListener timerListener = new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (tempoRestante > 0) {
                    tempoRestante--;
                    lblTimer.setText("Tempo: " + tempoRestante + "s");
                } else {
                    swingTimer.stop();
                    lblTimer.setText("Tempo: 0s");
                    handleAnswerSelection(null);
                }
            }
        };
        
        swingTimer = new javax.swing.Timer(1000, timerListener);
        
        frame.setSize(450,400);
        frame.setResizable(false);
        frame.setLocationRelativeTo(null);
    }

    public void setMain(Main main) {
        this.main = main;
    }

    public void open() {
        frame.setVisible(true);
    }
    
    public void close() {
    	frame.dispose();
    }

    public void startTimer(int segundos) {
        this.tempoRestante = segundos;
        lblTimer.setText("Tempo: " + tempoRestante + "s");
        this.answered = false; 
        for (JButton btn : optionButtons) {
            btn.setEnabled(true);
        }
        swingTimer.start();
    }
    
    private void addContent() {
        JPanel topPanel = new JPanel(new BorderLayout());
        topPanel.setBorder(new EmptyBorder(10, 15, 15, 15));
        topPanel.setBackground(new Color(245, 245, 245));
        
        lblTimer = new JLabel("Tempo: --", SwingConstants.RIGHT);
        lblTimer.setFont(new Font("Arial", Font.PLAIN, 14));
        
        lblPlayerName = new JLabel("Jogador: --");
        lblPlayerName.setFont(new Font("Arial", Font.PLAIN, 14));
        
        JPanel infoPanel = new JPanel(new GridLayout(1, 2));
        infoPanel.setBackground(new Color(245, 245, 245));
        infoPanel.add(lblPlayerName);
        infoPanel.add(lblTimer);
        
        lblQuestion = new JLabel("A aguardar início do jogo...", SwingConstants.CENTER);
        lblQuestion.setFont(new Font("Arial", Font.BOLD, 20));
        lblQuestion.setBorder(new EmptyBorder(20, 0, 10, 0));
        
        topPanel.add(infoPanel, BorderLayout.NORTH);
        topPanel.add(lblQuestion, BorderLayout.CENTER);
        
        frame.add(topPanel, BorderLayout.NORTH);
        
        JPanel painelGrid = new JPanel(new GridLayout(2, 2, 15, 15));
        painelGrid.setBorder(new EmptyBorder(10, 15, 15, 15));
        painelGrid.setBackground(new Color(245, 245, 245));
        
        optionButtons = new JButton[4];
        optionButtons[0] = createOptionButton("...", new Color(255, 92, 92));
        optionButtons[1] = createOptionButton("...", new Color(92, 153, 255));
        optionButtons[2] = createOptionButton("...", new Color(255, 204, 92));
        optionButtons[3] = createOptionButton("...", new Color(120, 220, 130));
        
        for (JButton btn : optionButtons)
            painelGrid.add(btn);
        
        frame.add(painelGrid, BorderLayout.CENTER);
        
        JPanel bottomPanel = new JPanel(new BorderLayout());
        bottomPanel.setBorder(new EmptyBorder(10, 15, 15, 15));
        bottomPanel.setBackground(new Color(245, 245, 245));
        
        lblScore = new JLabel("Pontuação da equipa: 0", SwingConstants.CENTER);
        lblScore.setFont(new Font("Arial", Font.PLAIN, 14));
        
        bottomPanel.add(lblScore);
        
        frame.add(bottomPanel, BorderLayout.SOUTH);
    }
    
    private JButton createOptionButton(String text, Color bgColor) {
        JButton btn = new JButton(text);
        btn.setFont(new Font("Arial", Font.BOLD, 14));
        btn.setForeground(Color.WHITE);
        btn.setBackground(bgColor);
        btn.setFocusPainted(false);
        btn.setBorderPainted(false);
        btn.setOpaque(true);
        
        
        btn.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                if (!answered && btn.isEnabled()) {
                    btn.setBackground(bgColor.darker());
                }
            }
            public void mouseExited(java.awt.event.MouseEvent evt) {
                if (!answered && btn.isEnabled()) {
                    btn.setBackground(bgColor);
                }
            }
        });
        
        btn.addActionListener(e -> handleAnswerSelection(btn));
        
        return btn;
    }
    
    private void handleAnswerSelection(JButton clickedButton) {
        if (answered) return;
        answered = true;
        
        swingTimer.stop();
        
        if (clickedButton != null) {
            System.out.println("[GUI] Resposta enviada: " + clickedButton.getText());
        } else {
            System.out.println("[GUI] Tempo esgotado!");
        }
        
        for (JButton btn : optionButtons) {
            btn.setEnabled(false);
        }

        if (main != null) {
            Timer delay = new Timer(1000, e -> 
                main.onRespostaSubmetida()
            );
            delay.setRepeats(false);
            delay.start();
        }
    }
    
    public void setTextQuestion(String texto) {
        lblQuestion.setText("<html><center>" + texto + "</center></html>"); 
    }
    
    public void setTextButtons(List<String> opcoes) {
        if (opcoes != null && opcoes.size() == 4) {
            optionButtons[0].setText(opcoes.get(0));
            optionButtons[1].setText(opcoes.get(1));
            optionButtons[2].setText(opcoes.get(2));
            optionButtons[3].setText(opcoes.get(3));
        }
    }

    public void setTextPlayer(String nome) {
        lblPlayerName.setText("Jogador: " + nome);
    }
    
    public void setTextPoints(String texto) {
        lblScore.setText(texto);
    }
}