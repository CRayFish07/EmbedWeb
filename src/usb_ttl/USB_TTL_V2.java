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
	//com口标示符，一般我们在设备管理器中看到的是像COM1，COM2，COM3等类型
	CommPortIdentifier portId;
	//提取设备管理器中的COM1，COM2，COM3标识
	Enumeration portList;
	//保存串口对象引用
	SerialPort serialPort;
	
	//保存串口输出流
	OutputStream outputStream;
	//保存串口输入流
	InputStream inputStream;
	//输出缓冲区
	BufferedOutputStream bos = new BufferedOutputStream(outputStream);
	//输入缓冲区
	BufferedInputStream bis = new BufferedInputStream(inputStream);
	
	//缓存字符串
	String messageString;
	
	//发送文本框
	JTextArea send;
	//接收文本框
	JTextArea get;
	//选择端口
	JComboBox com;
	//每次重新利用都需要再次重新获取这个枚举结构
	Enumeration en;
	
	public USB_TTL_V2(){
		//设置JFrame标题
		this.setTitle("USB_TTL");
		//设置默认的关闭操作
		this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		//设置JFrame左上角位置
		this.setLocation(500, 300);
		//设置JFrame大小
		this.setSize(600, 300);
		//添加文本区
		add(BorderLayout.CENTER, addTextArea());
		//添加发送文本区
		add(BorderLayout.SOUTH, sendButton());
		//初始化串口
		portInit();
		//将窗口设置为可见
		this.setVisible(true);
	}
	private void portInit() {
		System.out.println("测试USB——TTL：RS232");
		//获取当前系统中的串口,并口标示符
		en = CommPortIdentifier.getPortIdentifiers();
		com.addItem("请选择");
		//循环检查串口,并口
		while (en.hasMoreElements()) {
			//将枚举转换为com口标示符
			portId = (CommPortIdentifier)en.nextElement();
			if (portId.getPortType() == CommPortIdentifier.PORT_SERIAL) {
				//由于目前本机所用的硬件占用了COM3,所以直接确定为COM3
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
			//设置串口应用名和等待时间
			if (!"请选择".equals(select)) {
				en = CommPortIdentifier.getPortIdentifiers();
				while (en.hasMoreElements()) {
					//将枚举转换为com口标示符
					portId = (CommPortIdentifier)en.nextElement();
					if (portId.getPortType() == CommPortIdentifier.PORT_SERIAL) {
						//由于目前本机所用的硬件占用了COM3,所以直接确定为COM3
						if (select.equals(portId.getName())) {
							serialPort = (SerialPort)portId.open(select, 2000);
							System.out.println(serialPort);
							//获取串口输出流
							outputStream = serialPort.getOutputStream();
							//获取串口输入流
							inputStream = serialPort.getInputStream();
							//设置串口参数
							serialPort.setSerialPortParams(9600, SerialPort.DATABITS_8, SerialPort.STOPBITS_1, SerialPort.PARITY_NONE);
							//创建接收和发送线程维护接收和发送数据流
							sendAndreceiveThread();
						}
					}
				}
			}
		} catch (Exception e) {
			System.out.println("打开端口失败");
			e.printStackTrace();
		}
	}
	private void sendAndreceiveThread() {
		//创建发送线程
		sendThread();
		//创建接收线程
		receiveThread();
	}
	private void sendThread() {
		new Thread(){
			@Override
			public void run() {
				while (true) {
					//发送的字符串必须不为空,且不是空字符串
					if (messageString != null && !"".equals(messageString)) {
						try {
							if (serialPort != null) {
								//将字符串转换为byte数组发送
								byte[] b = messageString.getBytes();
								//将字符数组发送出去
								outputStream.write(b);
								//最后发送一个换行符
								outputStream.write('\n');
								//将字符串置空
								messageString = "";
							}
						} catch (IOException e) {
							e.printStackTrace();
						}
					}
					try {
						//每100毫秒检查发送一次
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
							//将读到的数字转换为byte
							byte[] b = new byte[]{(byte)inputStream.read()};
							//将byte型数字转换为字符串
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
				System.out.println("发送的字符："+state);
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
