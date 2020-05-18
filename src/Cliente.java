
import javax.swing.*;

import java.awt.Toolkit;
import java.awt.event.*;
import java.io.*;
import java.net.*;
import java.util.*;

public class Cliente {

	public static void main(String[] args) {
		
		ClienteFrame frame = new ClienteFrame();
		frame.setDefaultCloseOperation(3);
		frame.setVisible(true);
		
	}

}
class ClienteFrame extends JFrame{
	
	public ClienteFrame() {
		
		setTitle("Chat");
		setBounds(400,400,350,500);
		add(new ClientePanel());
		addWindowListener(new EnvioOnline());
	}
	
}

class EnvioOnline extends WindowAdapter{
	
	public void windowOpened(WindowEvent e) {
		
		try {
			
			Socket socket = new Socket("192.168.100.12",8080);
			
			Paquete paquetito = new Paquete();
			
			paquetito.setMensaje("Online!");
			
			paquetito.setNick(ClientePanel.nombre);
			
			ObjectOutputStream paqueteenvio = new ObjectOutputStream(socket.getOutputStream());
			
			
			paqueteenvio.writeObject(paquetito);
			
			socket.close();
			
			paqueteenvio.close();
			
			
			
		} catch (IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
			System.out.println("Error aca");
		}
		
	}
	
}

class ClientePanel extends JPanel implements Runnable{
	
	private JTextField mensaje ;
	public JLabel nick;
	private JTextArea areachat;
	private JComboBox ip;
	public static String nombre;
	String ipdestino ;
	private Paquete paqueterecibido;
	
	public ClientePanel(){
		
		JLabel Nick = new JLabel("Nick :");
		add(Nick);
		
		nombre = JOptionPane.showInputDialog("Introduzca su nick");
		
		nick = new JLabel(nombre);
		add(nick);
		
		JLabel texto = new JLabel("Online : ");
		add(texto);
		
		ip = new JComboBox<	>();
		add(ip);
		
		areachat = new JTextArea(20, 30);
		add(areachat);
		
		mensaje = new JTextField(20);
		add(mensaje);
		
		JButton enviar = new JButton("Enviar");
		add(enviar);
		enviar.addActionListener(new EnviarTexto());
		
		Thread threadchat = new Thread(this);
		
		threadchat.start();
		
		
	}
	
	public void run() {
		
		try {
			
			ServerSocket socketservidor = new ServerSocket(8080);
			
			Socket socketcliente;
			
			//Paquete paqueterecibido;
			
			while(true) {
				
				socketcliente = socketservidor.accept();
				
				ObjectInputStream objetoentrada = new ObjectInputStream(socketcliente.getInputStream());
					
				paqueterecibido = (Paquete)objetoentrada.readObject();
				
				if (!paqueterecibido.getMensaje().equals("Online!")) {
				
					areachat.append(paqueterecibido.getNick() + ": " + paqueterecibido.getMensaje() + "\n");
				
				}else {
				
				//areachat.append("\n" + paqueterecibido.getIps());
				
					ip.removeAllItems();
					
					HashMap<String, String> maparecibido = new HashMap<String,String>();
					
					maparecibido = paqueterecibido.getIps();
				
					for(Map.Entry<String, String> listasip : maparecibido.entrySet()) {
					
						ip.addItem(listasip.getValue());
					
					}
					
				}
				
			}
			
		}catch(Exception e) {
			
			System.out.println(e.getMessage());
			
		}
		
	}
	
	private class EnviarTexto implements ActionListener{
		public void actionPerformed(ActionEvent e) {
			
			//System.out.println(campo.getText());
			
			
			try {
				
				Socket socket = new Socket("192.168.100.12", 8080);
				
				HashMap<String,String> mapaipdestino = paqueterecibido.getIps();
				
				for(Map.Entry<String, String> elegirip : mapaipdestino.entrySet()) {
					
					if(elegirip.getValue().equals(ip.getSelectedItem())) {
						
						ipdestino = elegirip.getKey();
						
					}
					
				}
				
				JOptionPane.showMessageDialog(ClientePanel.this, ipdestino);
				
				Paquete paquete = new Paquete(nick.getText(),ipdestino,mensaje.getText());
				
				ObjectOutputStream paquetedatos = new ObjectOutputStream(socket.getOutputStream());
				
				areachat.append("\n"+ nick.getText() + ": " + mensaje.getText());
				
				paquetedatos.writeObject(paquete);
				
				paquetedatos.close();
				
				socket.close();
				
				/*DataOutputStream salida = new DataOutputStream(socket.getOutputStream());
				
				salida.writeUTF(campo.getText());
				
				salida.close();
				
				*/
				
			} catch (UnknownHostException e1) {
				
				e1.printStackTrace();
			} catch (IOException e1) {
				
				System.out.println(e1.getMessage());
				e1.printStackTrace();
			}
			
			mensaje.setText("");
			
		}
	}
}

class Paquete implements Serializable{
	
	private String nick, ip, mensaje;
	
	private HashMap<String,String> ips;
	
	
	public Paquete(String nick, String ip, String mensaje) {
		this.nick = nick;
		this.ip = ip;
		this.mensaje = mensaje;
	}
	
	

	public HashMap<String, String> getIps() {
		return ips;
	}



	public void setIps(HashMap<String, String> ips) {
		this.ips = ips;
	}



	public Paquete() {
		
	}

	

	public String getNick() {
		return nick;
	}

	public void setNick(String nick) {
		this.nick = nick;
	}

	public String getIp() {
		return ip;
	}

	public void setIp(String ip) {
		this.ip = ip;
	}

	public String getMensaje() {
		return mensaje;
	}

	public void setMensaje(String mensaje) {
		this.mensaje = mensaje;
	}
	
	
}


