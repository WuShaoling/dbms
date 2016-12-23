package ui;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;

import preprocess.PreProcess;

/**
 * 界面类
 * @author WSL
 *
 */
public class MainFrame extends JFrame {

	private static final long serialVersionUID = 1L;
	private JFrame frame; // 主窗体
	public static JTextArea output;// 输出框
	private JTextArea input;// 输入框
	private JButton submit;// 提交按钮
	private JButton clear;// 清空按钮

	public MainFrame() {
		initialize();
	}

	/**
	 * 初始化窗口
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

		// 提交按钮
		submit = new JButton("确定");
		submit.setBounds(200, 430, 80, 30);
		submit.addActionListener(new listenSubmit());
		frame.add(submit);

		// 清空按钮
		clear = new JButton("清空");
		clear.setBounds(400, 430, 80, 30);
		clear.addActionListener(new listenClear());
		frame.add(clear);

		// 输出框
		output = new JTextArea();
		output.setBounds(5, 5, 670, 300);
		JScrollPane jsp = new JScrollPane(output);
		jsp.setAutoscrolls(true);
		jsp.setBounds(5, 5, 670, 300);
		frame.add(jsp);

		// 输入框
		input = new JTextArea();
		input.setBounds(5, 320, 670, 100);
		JScrollPane jsp1 = new JScrollPane(input);
		jsp1.setBounds(5, 320, 670, 100);
		frame.add(jsp1);
		frame.setVisible(true);
	}

	/**
	 * 监听确定按钮事件
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
	 * 监听清空按钮事件
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
