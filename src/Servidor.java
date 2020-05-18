

import java.awt.event.*;
import java.io.*;
import java.net.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;

import javax.swing.*;


public class Servidor {

	public static void main(String[] args) {

		ServidorFrame frame = new ServidorFrame();
		frame.setDefaultCloseOperation(3);
		frame.setVisible(true);
		
	}

}
class ServidorFrame extends JFrame implements Runnable{
	
	private JTextArea area;
	
	public ServidorFrame() {
		
		setBounds(400,400,800,600);
		
		area = new JTextArea();
		
		add(area);
		
		Thread t = new Thread(this);
			
		t.start();
		
		
		
	}

	public void run() {
		
		//System.out.println("Estoy a la escucha");
		
		try {
			
			ServerSocket servidor = new ServerSocket(8080);
			
			String nick , ip , mensaje;
			
			Paquete paqueterecibido;
			
			HashMap<String,String> listaip = new HashMap<String,String>();
			
			while(true) {
			
				Socket socket = servidor.accept();
				
				ObjectInputStream paquetedatos = new ObjectInputStream(socket.getInputStream());
				
				paqueterecibido = (Paquete)paquetedatos.readObject();
				
				nick = paqueterecibido.getNick();
				
				ip = paqueterecibido.getIp();
				
				mensaje = paqueterecibido.getMensaje();
				
				//System.out.println("Todo ok");
				
				if (!mensaje.equals("Online!")) {
				
					area.append("\n" + nick + " : " + mensaje + " para " + ip);
				
					Socket destinatario = new Socket(ip, 8080);
				
					ObjectOutputStream paquetedestinatario = new ObjectOutputStream(destinatario.getOutputStream());
					
					paquetedestinatario.writeObject(paqueterecibido);
				
					paquetedatos.close();
				
					destinatario.close();
				
					socket.close();
				
				}else {
					//----------------Detecta Online ----------------------
					
					InetAddress direccion = socket.getInetAddress();
					
					String ipremota = direccion.getHostAddress();
					
					area.append("Online : " + paqueterecibido.getNick() + " --- " + ipremota + "\n");
					
					listaip.put(ipremota, paqueterecibido.getNick());
					
					paqueterecibido.setIps(listaip);
					
					for(Map.Entry<String, String> e : listaip.entrySet()) {
						//System.out.println(e);
						
						Socket destinatario = new Socket(e.getKey(), 8080);
						
						ObjectOutputStream paquetedestinatario = new ObjectOutputStream(destinatario.getOutputStream());
						
						paquetedestinatario.writeObject(paqueterecibido);
					
						paquetedatos.close();
					
						destinatario.close();
					
						socket.close();
					}
				//-------------------------------------------------------
				}
			
				/*DataInputStream entrada = new DataInputStream(socket.getInputStream());
			
				String mensaje = entrada.readUTF();
			
				area.append("\n" + mensaje);
			
				entrada.close();
				
				*/
			
			}
			
		} catch (IOException e) {
			System.out.println(e.getMessage());
			e.printStackTrace();
		} catch (ClassNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}
	
}
