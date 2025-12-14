package cliente;

import java.awt.*;
import javax.swing.*;
import javax.swing.border.EmptyBorder;

import perguntas.Pergunta;

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
    private JTextArea txtPlacar;
    
    private javax.swing.Timer swingTimer;
    private int tempoRestante;
    
    private Cliente cliente;

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
                    //se passam os 30s e o jogador nao responde, entao envia -1 (erro)
                    handleAnswerSelection(-1);
                }
            }
        };
        
        swingTimer = new javax.swing.Timer(1000, timerListener);
        
        frame.setSize(850,550);
        frame.setResizable(false);
        frame.setLocationRelativeTo(null);
    }
    
   

    public void setCliente(Cliente cliente) {
        this.cliente = cliente;
    }

    public void open() {
        frame.setVisible(true);
    }
    
    public void close() {
    	frame.dispose();
    }
    
    
    // Metodo principal chamado pelo Cliente quando chega uma nova pergunta
    public void showQuestion(Pergunta p) {
        lblQuestion.setText("<html><center>" + p.getQuestion() + "</center></html>");
        
        List<String> opcoes = p.getOptions();
        for (int i = 0; i < optionButtons.length; i++) {
            if (i < opcoes.size()) {
            	optionButtons[i].setText("<html><center>" + opcoes.get(i) + "</center></html>");
                optionButtons[i].setEnabled(true);
                optionButtons[i].setVisible(true);
            } else {
                optionButtons[i].setVisible(false);
            }
        }
        
        this.answered = false;

        this.tempoRestante = 30;
        lblTimer.setText("Tempo: 30s");
        swingTimer.restart();
    }
    
    
    public void atualizarPlacar(String textoPlacar) {
        txtPlacar.setText(textoPlacar);
    }


    public void setTextPlayerAndTeam(String nome, String equipa) {
        lblPlayerName.setText("Jogador: " + nome + " -" + " Equipa: " + equipa);
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
    
    
    private void bloquearBotoes() {
        for (JButton btn : optionButtons) {
            btn.setEnabled(false);
        }
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
        lblQuestion.setFont(new Font("Arial", Font.BOLD, 18));
        lblQuestion.setBorder(new EmptyBorder(20, 0, 10, 0));
        
        topPanel.add(infoPanel, BorderLayout.NORTH);
        topPanel.add(lblQuestion, BorderLayout.CENTER);
        
        frame.add(topPanel, BorderLayout.NORTH);
        
        JPanel painelGrid = new JPanel(new GridLayout(2, 2, 15, 15));
        painelGrid.setBorder(new EmptyBorder(10, 15, 15, 15));
        painelGrid.setBackground(new Color(245, 245, 245));
        
        optionButtons = new JButton[4];
        optionButtons[0] = createOptionButton(0, new Color(255, 92, 92));
        optionButtons[1] = createOptionButton(1, new Color(92, 153, 255));
        optionButtons[2] = createOptionButton(2, new Color(255, 204, 92));
        optionButtons[3] = createOptionButton(3, new Color(120, 220, 130));
        
        for (JButton btn : optionButtons)
            painelGrid.add(btn);
        
        frame.add(painelGrid, BorderLayout.CENTER);
        
        
        JPanel sidePanel = new JPanel(new BorderLayout());
        sidePanel.setBorder(new EmptyBorder(10, 0, 10, 15));
        sidePanel.setBackground(new Color(245, 245, 245));
        sidePanel.setPreferredSize(new Dimension(180, 0));
        
        JLabel lblTituloPlacar = new JLabel("Classificação", SwingConstants.CENTER);
        lblTituloPlacar.setFont(new Font("Arial", Font.BOLD, 14));
        sidePanel.add(lblTituloPlacar, BorderLayout.NORTH);
        
        txtPlacar = new JTextArea();
        txtPlacar.setEditable(false);
        txtPlacar.setBackground(new Color(255, 255, 240));
        txtPlacar.setBorder(BorderFactory.createLineBorder(Color.GRAY));
        txtPlacar.setFont(new Font("Monospaced", Font.PLAIN, 12));
        txtPlacar.setText("A aguardar...");
        
        sidePanel.add(txtPlacar, BorderLayout.CENTER);
        frame.add(sidePanel, BorderLayout.EAST);
        
        
        JPanel bottomPanel = new JPanel(new BorderLayout());
        bottomPanel.setBorder(new EmptyBorder(10, 15, 15, 15));
        bottomPanel.setBackground(new Color(245, 245, 245));
        
        frame.add(bottomPanel, BorderLayout.SOUTH);
    }
    
    
    private JButton createOptionButton(int indice, Color bgColor) {
        JButton btn = new JButton("...");
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
        
        btn.addActionListener(e -> handleAnswerSelection(indice));
        
        return btn;
    }
    
    
    private void handleAnswerSelection(int indiceBotao) {
    	if (answered) return; 
        answered = true;
        
        swingTimer.stop(); 
        bloquearBotoes(); 
        
        // Enviar o indice da resposta para o servidor
        if (cliente != null) {
        	if (indiceBotao == -1) {
                System.out.println("[GUI] Tempo esgotado! A enviar Timeout (-1) ao servidor...");
            } else {
                System.out.println("[GUI] O jogador escolheu a opção: " + indiceBotao);
            }
            cliente.sendAnswer(indiceBotao);
        }
    }
 
    
    public void mostrarFimDeJogo(String placarFinal) {
        atualizarPlacar(placarFinal);
        

        JOptionPane.showMessageDialog(frame, 
            "JOGO TERMINADO!\n\n" + placarFinal,
            "Fim do Jogo",
            JOptionPane.INFORMATION_MESSAGE);

        close();
    }
    
}