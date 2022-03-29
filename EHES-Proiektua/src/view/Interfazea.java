package view;

import java.awt.BorderLayout;
import java.awt.EventQueue;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JSplitPane;
import java.awt.Font;
import java.awt.Color;
import javax.swing.JComboBox;
import javax.swing.JTextPane;
import javax.swing.JButton;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import javax.swing.JTextArea;
import javax.swing.border.MatteBorder;
import java.awt.ScrollPane;
import java.awt.TextArea;

import javax.swing.JScrollPane;
import java.awt.Component;
import javax.swing.ScrollPaneConstants;
import javax.swing.ImageIcon;
import javax.swing.UIManager;

public class Interfazea extends JFrame {

	
	private JPanel contentPane;

	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					Interfazea frame = new Interfazea();
					frame.setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}

	/**
	 * Create the frame.
	 */
	public Interfazea() {
		setTitle("SPAM sailkatzailea");
		setForeground(Color.WHITE);
		
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setBounds(100, 100, 450, 300);
		contentPane = new JPanel();
		contentPane.setBackground(Color.WHITE);
		contentPane.setForeground(Color.WHITE);
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		setContentPane(contentPane);
		contentPane.setLayout(null);
		
		JLabel lblIdatziHemenZure = new JLabel("Idatzi hemen zure SMS-a:");
		lblIdatziHemenZure.setForeground(new Color(255, 153, 0));
		lblIdatziHemenZure.setFont(new Font("Source Serif Pro", Font.PLAIN, 14));
		lblIdatziHemenZure.setBounds(23, 57, 232, 14);
		contentPane.add(lblIdatziHemenZure);
		
		JLabel lblSpamEdoHam = new JLabel("SPAM edo HAM?");
		lblSpamEdoHam.setForeground(new Color(255, 153, 0));
		lblSpamEdoHam.setFont(new Font("Source Serif Pro", Font.BOLD | Font.ITALIC, 40));
		lblSpamEdoHam.setBounds(60, -15, 311, 75);
		contentPane.add(lblSpamEdoHam);
		
		
		
		JLabel lblEmaitza = new JLabel("");
		lblEmaitza.setForeground(new Color(255, 153, 0));
		lblEmaitza.setFont(new Font("Source Serif Pro", Font.BOLD | Font.ITALIC, 36));
		lblEmaitza.setBounds(158, 200, 146, 50);
		contentPane.add(lblEmaitza);
		setLocationRelativeTo(null);
		
		TextArea email_sms = new TextArea();
		email_sms.setFont(UIManager.getFont("ToolBar.font"));
		email_sms.setForeground(Color.BLACK);
		email_sms.setBackground(Color.WHITE);
		email_sms.setBounds(23, 77, 380, 102);
		contentPane.add(email_sms);
		
		JScrollPane scrollPane = new JScrollPane();
		scrollPane.setBounds(0, 195, 403, -110);
		contentPane.add(scrollPane);
		
		JButton btnIragarri = new JButton("");
		btnIragarri.setIcon(new ImageIcon(Interfazea.class.getResource("/view/IRAGARRI.png")));
		btnIragarri.setBorder(null);
		btnIragarri.setFont(new Font("Source Serif Pro", Font.PLAIN, 12));
		btnIragarri.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				String nulltxt ="";
				if (email_sms.getText().equals(nulltxt)) {
					JOptionPane.showMessageDialog(null,"Idatzi SMS-ren bat mesedez.");
					email_sms.requestFocus();
				}
			}
		});
		

		btnIragarri.setBounds(158, 180, 118, 23);
		contentPane.add(btnIragarri);
	}
}
