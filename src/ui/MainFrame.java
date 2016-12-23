package ui;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;

import preprocess.PreProcess;

/**
 * ������
 * @author WSL
 *
 */
public class MainFrame extends JFrame {

	private static final long serialVersionUID = 1L;
	private JFrame frame; // ������
	public static JTextArea output;// �����
	private JTextArea input;// �����
	private JButton submit;// �ύ��ť
	private JButton clear;// ��հ�ť

	public MainFrame() {
		initialize();
	}

	/**
	 * ��ʼ������
	 */
	private void initialize() {
		frame = new JFrame();
		try {
			  
		} catch (Exception e) {
			e.printStackTrace();
		}
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setBounds(350, 50, 700, 500);
		frame.getContentPane().setLayout(null);
		frame.setTitle("MyDBMS");

		// �ύ��ť
		submit = new JButton("ȷ��");
		submit.setBounds(200, 430, 80, 30);
		submit.addActionListener(new listenSubmit());
		frame.add(submit);

		// ��հ�ť
		clear = new JButton("���");
		clear.setBounds(400, 430, 80, 30);
		clear.addActionListener(new listenClear());
		frame.add(clear);

		// �����
		output = new JTextArea();
		output.setBounds(5, 5, 670, 300);
		JScrollPane jsp = new JScrollPane(output);
		jsp.setAutoscrolls(true);
		jsp.setBounds(5, 5, 670, 300);
		frame.add(jsp);

		// �����
		input = new JTextArea();
		input.setBounds(5, 320, 670, 100);
		JScrollPane jsp1 = new JScrollPane(input);
		jsp1.setBounds(5, 320, 670, 100);
		frame.add(jsp1);
		frame.setVisible(true);
	}

	/**
	 * ����ȷ����ť�¼�
	 * 
	 * @author WSL
	 *
	 */
	private class listenSubmit implements ActionListener {
		public void actionPerformed(ActionEvent e) {
			String sql = input.getText();
			if (!sql.equals("")) {
				PreProcess.preprocessSql(sql);
				input.setText("");
			}
		}
	}

	/**
	 * ������հ�ť�¼�
	 * 
	 * @author WSL
	 *
	 */
	private class listenClear implements ActionListener {
		public void actionPerformed(ActionEvent e) {
			input.setText("");
			output.setText("");
		}
	}
}
