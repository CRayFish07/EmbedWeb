package usb_ttl;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.FlowLayout;
import java.awt.Graphics;
import java.awt.GridLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Enumeration;

import javax.comm.CommPortIdentifier;
import javax.comm.SerialPort;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.border.Border;

import dao.StateDao;



public class USB_TTL_V2 extends JFrame{
	//com�ڱ�ʾ����һ���������豸�������п���������COM1��COM2��COM3������
	CommPortIdentifier portId;
	//��ȡ�豸�������е�COM1��COM2��COM3��ʶ
	Enumeration portList;
	//���洮�ڶ�������
	SerialPort serialPort;
	
	//���洮�������
	OutputStream outputStream;
	//���洮��������
	InputStream inputStream;
	//���������
	BufferedOutputStream bos = new BufferedOutputStream(outputStream);
	//���뻺����
	BufferedInputStream bis = new BufferedInputStream(inputStream);
	
	//�����ַ���
	String messageString;
	
	//�����ı���
	JTextArea send;
	//�����ı���
	JTextArea get;
	//ѡ��˿�
	JComboBox com;
	//ÿ���������ö���Ҫ�ٴ����»�ȡ���ö�ٽṹ
	Enumeration en;
	
	public USB_TTL_V2(){
		//����JFrame����
		this.setTitle("USB_TTL");
		//����Ĭ�ϵĹرղ���
		this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		//����JFrame���Ͻ�λ��
		this.setLocation(500, 300);
		//����JFrame��С
		this.setSize(600, 300);
		//����ı���
		add(BorderLayout.CENTER, addTextArea());
		//��ӷ����ı���
		add(BorderLayout.SOUTH, sendButton());
		//��ʼ������
		portInit();
		//����������Ϊ�ɼ�
		this.setVisible(true);
	}
	private void portInit() {
		System.out.println("����USB����TTL��RS232");
		//��ȡ��ǰϵͳ�еĴ���,���ڱ�ʾ��
		en = CommPortIdentifier.getPortIdentifiers();
		com.addItem("��ѡ��");
		//ѭ����鴮��,����
		while (en.hasMoreElements()) {
			//��ö��ת��Ϊcom�ڱ�ʾ��
			portId = (CommPortIdentifier)en.nextElement();
			if (portId.getPortType() == CommPortIdentifier.PORT_SERIAL) {
				//����Ŀǰ�������õ�Ӳ��ռ����COM3,����ֱ��ȷ��ΪCOM3
				String comEnum = portId.getName();
				com.addItem(comEnum);
			}
		}
		com.addActionListener(new ActionListener() {
			
			public void actionPerformed(ActionEvent e) {
				String select = (String) com.getSelectedItem();
				initCom(select);
			}
		});
	}
	private void initCom(String select){
		try {
			//���ô���Ӧ�����͵ȴ�ʱ��
			if (!"��ѡ��".equals(select)) {
				en = CommPortIdentifier.getPortIdentifiers();
				while (en.hasMoreElements()) {
					//��ö��ת��Ϊcom�ڱ�ʾ��
					portId = (CommPortIdentifier)en.nextElement();
					if (portId.getPortType() == CommPortIdentifier.PORT_SERIAL) {
						//����Ŀǰ�������õ�Ӳ��ռ����COM3,����ֱ��ȷ��ΪCOM3
						if (select.equals(portId.getName())) {
							serialPort = (SerialPort)portId.open(select, 2000);
							System.out.println(serialPort);
							//��ȡ���������
							outputStream = serialPort.getOutputStream();
							//��ȡ����������
							inputStream = serialPort.getInputStream();
							//���ô��ڲ���
							serialPort.setSerialPortParams(9600, SerialPort.DATABITS_8, SerialPort.STOPBITS_1, SerialPort.PARITY_NONE);
							//�������պͷ����߳�ά�����պͷ���������
							sendAndreceiveThread();
						}
					}
				}
			}
		} catch (Exception e) {
			System.out.println("�򿪶˿�ʧ��");
			e.printStackTrace();
		}
	}
	private void sendAndreceiveThread() {
		//���������߳�
		sendThread();
		//���������߳�
		receiveThread();
	}
	private void sendThread() {
		new Thread(){
			@Override
			public void run() {
				while (true) {
					//���͵��ַ������벻Ϊ��,�Ҳ��ǿ��ַ���
					if (messageString != null && !"".equals(messageString)) {
						try {
							if (serialPort != null) {
								//���ַ���ת��Ϊbyte���鷢��
								byte[] b = messageString.getBytes();
								//���ַ����鷢�ͳ�ȥ
								outputStream.write(b);
								//�����һ�����з�
								outputStream.write('\n');
								//���ַ����ÿ�
								messageString = "";
							}
						} catch (IOException e) {
							e.printStackTrace();
						}
					}
					try {
						//ÿ100�����鷢��һ��
						Thread.sleep(100);
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
				}
			}
		}.start();
	}
	private void receiveThread() {
		new Thread(){
			@Override
			public void run() {
				while (true) {
					try {
						if (serialPort != null) {
							//������������ת��Ϊbyte
							byte[] b = new byte[]{(byte)inputStream.read()};
							//��byte������ת��Ϊ�ַ���
							get.append(new String(b));
						}
					} catch (IOException e) {
						e.printStackTrace();
					}
				}
			}
		}.start();
	}
	private JPanel sendButton() {
		JPanel bottonButton = new JPanel();
		bottonButton.setLayout(new BorderLayout());
		JPanel textTip = new JPanel();
		textTip.setLayout(new GridLayout(1, 2));

		JLabel sendLabel = new JLabel("send");
		sendLabel.setHorizontalAlignment(JLabel.CENTER);
		JLabel getLabel = new JLabel("receive");
		getLabel.setHorizontalAlignment(JLabel.CENTER);
		JButton sendText = new JButton("sendText");
		
		sendText.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				int state = StateDao.queryState();
				messageString = ""+state;
				send.setText(messageString);
				System.out.println("���͵��ַ���"+state);
			}
		});
		
		textTip.add(sendLabel);
		textTip.add(getLabel);
		
		bottonButton.add(BorderLayout.CENTER, textTip);
		bottonButton.add(BorderLayout.SOUTH, sendText);
		return bottonButton;
	}

	private JPanel addTextArea() {
		JPanel textJPanel = new JPanel();
		textJPanel.setLayout(new FlowLayout());
		
		send = new JTextArea("send");
		send.setRows(10);
		send.setColumns(20);
		send.setText("");
		send.setLineWrap(true);
		send.setDragEnabled(true);

		get = new JTextArea("get");
		get.setRows(10);
		get.setColumns(20);
		get.setText("");
		get.setLineWrap(true);
		get.setDragEnabled(true);
		
		com = new JComboBox();
		
		
		JScrollPane sendJScrollPane = new JScrollPane(send);
		sendJScrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_ALWAYS);
		sendJScrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
		
		JScrollPane getJScrollPane = new JScrollPane(get);
		getJScrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_ALWAYS);
		getJScrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
		
		textJPanel.add(sendJScrollPane);
		textJPanel.add(com);
		textJPanel.add(getJScrollPane);
		return textJPanel;
	}
	public static void main(String[] args) {
		new USB_TTL_V2();
	}
}
