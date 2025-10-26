package cliente;

import java.awt.*;
import javax.swing.*;
import javax.swing.border.EmptyBorder;

public class GUI {
	private JFrame frame;
	private JButton[] optionButtons;
	private boolean answered = false;
	
	public GUI() {
		frame = new JFrame("IsKahoot");
		
		frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
		frame.setLayout(new BorderLayout(15, 15));
        frame.getContentPane().setBackground(new Color(245, 245, 245));
        
		addContent();
		
		frame.setSize(450,400);
		frame.setResizable(false);
        frame.setLocationRelativeTo(null);
		frame.setVisible(true);
	}
	
	private void addContent() {
	
		JPanel topPanel = new JPanel(new BorderLayout());
	    topPanel.setBorder(new EmptyBorder(10, 15, 15, 15));
	    topPanel.setBackground(new Color(245, 245, 245));

        JLabel lblTimer = new JLabel("Tempo: 30s", SwingConstants.RIGHT);
        lblTimer.setFont(new Font("Arial", Font.PLAIN, 14));

        JLabel lblPlayerName = new JLabel("Jogador 1");
        lblPlayerName.setFont(new Font("Arial", Font.PLAIN, 14));

        JPanel infoPanel = new JPanel(new GridLayout(1, 2));
        infoPanel.setBackground(new Color(245, 245, 245));
        infoPanel.add(lblPlayerName);
        infoPanel.add(lblTimer);
		
	
		JLabel lblQuestion = new JLabel("O que é uma thread?", SwingConstants.CENTER);
		lblQuestion.setFont(new Font("Arial", Font.BOLD, 20));
		lblQuestion.setBorder(new EmptyBorder(20, 0, 10, 0));
	    
	    topPanel.add(infoPanel, BorderLayout.NORTH);
        topPanel.add(lblQuestion, BorderLayout.CENTER);
	    
	    frame.add(topPanel, BorderLayout.NORTH);
	    	
	  
	    JPanel painelGrid = new JPanel(new GridLayout(2, 2, 15, 15));
	    painelGrid.setBorder(new EmptyBorder(10, 15, 15, 15));
	    painelGrid.setBackground(new Color(245, 245, 245));
	    
	    optionButtons = new JButton[4];
	    optionButtons[0] = createOptionButton("Processo", new Color(255, 92, 92));
	    optionButtons[1] = createOptionButton("Aplicação", new Color(92, 153, 255));
	    optionButtons[2] = createOptionButton("Programa", new Color(255, 204, 92));
	    optionButtons[3] = createOptionButton("Processo Ligeiro", new Color(120, 220, 130));

	    for (JButton btn : optionButtons)
            painelGrid.add(btn);
	    
	    frame.add(painelGrid, BorderLayout.CENTER);
	    
	  
	    JPanel bottomPanel = new JPanel(new BorderLayout());
	    bottomPanel.setBorder(new EmptyBorder(10, 15, 15, 15));
	    bottomPanel.setBackground(new Color(245, 245, 245));
	    
	    JLabel lblScore = new JLabel("Pontuação da equipa: 0", SwingConstants.CENTER);
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
		
		for (JButton btn : optionButtons) {
            btn.setEnabled(false);
            if (btn == clickedButton) {
            	btn.setBackground(btn.getBackground().brighter());
            } else {
                btn.setBackground(btn.getBackground().darker());
            }
        }
	}

	public static void main(String[] args) {
		GUI gui = new GUI();
	}
}
