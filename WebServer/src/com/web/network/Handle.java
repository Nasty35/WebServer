package com.web.network;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.Date;
import java.util.StringTokenizer;

import org.jboss.netty.buffer.ChannelBuffer;
import org.jboss.netty.buffer.ChannelBufferOutputStream;
import org.jboss.netty.buffer.ChannelBuffers;
import org.jboss.netty.channel.Channel;

import com.web.WebServer;

/**
*
* @author Nasty(Daniel)
*/
public class Handle {
    
    private final String user;
    private final Channel socket;
	
	public Handle(final String message, final Channel socket, final String user) {
        this.user = user;
        this.socket = socket;
		if(message.startsWith("GET /") && message.contains(" HTTP/1.1")) {
			final String headrequest = message.split("Host:")[0].replaceAll("[\n\r]","");
			final String[] instruction = headrequest.split(" /");
			final String[] param = instruction[1].split(" ");
			WebServer.Write("Request: Instruction [".concat(instruction[0]).concat("] ")
					.concat("File [").concat(param[0]).concat("] Protocol [").concat(param[1])
					.concat("]"));
			StringTokenizer st = new StringTokenizer(message);
			if ((st.countTokens() >= 2) && st.nextToken().equals("GET")) {
				final String request = st.nextToken().trim();
			    try {
			    	sendPage(request.startsWith("/") ? request.substring(1) : request);
				} catch (IOException e) {
					WebServer.Write(e.getMessage());
				}
			} else {
				WebServer.Write("400 Petici\u00f3n Incorrecta");
			}
		}
	}
	
	public void sendPage(String file) throws IOException {
        Boolean ifFavicon = false;
		ChannelBuffer buffer = ChannelBuffers.dynamicBuffer();
		ChannelBufferOutputStream out = new ChannelBufferOutputStream(buffer);
		if(file.isEmpty()) {
			WebServer.Write("Request index page at ".concat(this.user));
			file = "index.html";
		} else {
			if(!file.endsWith(".html")) {
				File folder = new File(WebServer.folder.concat("/").concat(file));
				if(folder.exists()) {
					file = file.concat(file.endsWith("/") ? "index.html" : "/index.html");
					folder = null;
				} else if(!file.contains(".ico")) {
				    WebServer.Write("Request page at ".concat(this.user)
					    .concat(": \"").concat(file).concat("\""));
				} else {
				    WebServer.Write("Request favicon.ico");
					ifFavicon = true;
				}
			} else {
				WebServer.Write("Request page at ".concat(this.user)
                    .concat(": \"").concat(file).concat("\""));
			}
		}
		File myFile = new File(WebServer.folder.concat("/").concat(file));
		out.writeBytes("HTTP/1.0 400 Not found\n");
		out.writeBytes("Server: WebServer/1.0\n");
		out.writeBytes("Date: ".concat(new Date().toString()).concat("\n"));
		out.writeBytes("Content-Type: text/html\n");
		if (myFile.exists()) {
			out.writeBytes("Content-Length: ".concat(String.valueOf(myFile.length())));
			out.writeBytes("\n\n");
			BufferedReader in = new BufferedReader(new FileReader(myFile));
			for(String linea = ""; (linea = in.readLine()) != null; ) {
				WebServer.Write("Sended at ".concat(this.user)
                    .concat(": \"").concat(linea).concat("\""));
				out.writeBytes(linea.concat("\n"));
			}
			in.close();
			in = null;
		} else {
		    if(ifFavicon) {
			    WebServer.Write("Favicon not found!");
				out.close();
				out = null;
				buffer.clear();
				buffer = null;
				file = null;
				myFile = null;
                return;
            }
			out.writeBytes("Content-Length: ".concat(String.valueOf(WebServer.mError.length())));
			out.writeBytes("\n\n");
			WebServer.Write("Sended at ".concat(this.user)
                .concat(": \"").concat(WebServer.mError).concat("\""));
			out.writeBytes(WebServer.mError);
		}
        this.socket.write(buffer);
		buffer.clear();
		buffer = null;
		out.close();
		out = null;
		file = null;
		myFile = null;
	}

}
