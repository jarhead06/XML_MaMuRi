
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

/**
 *
 * @author Justin
 */
public class Connexion {
    
    public Connexion() throws IOException{
        ServerSocket socketserver  ;
	Socket socketduserveur ;
	boolean ok = true;
	System.out.println("Démarrage du serveur sur le port 8000");
        while(ok = true)
        {
			
            BufferedReader in;
	    PrintWriter out; 
	    socketserver = new ServerSocket(8000);
	    socketduserveur  = socketserver.accept(); 
	    in = new BufferedReader(new InputStreamReader(socketduserveur.getInputStream()));
	    String message = in.readLine();
	    System.out.println(message);
	    out = new PrintWriter(socketduserveur.getOutputStream());
	    out.println("Accusé de récéption :" + message);
	    out.flush();
	    socketserver.close();
	    socketduserveur.close();
        }    
    }
    
}
