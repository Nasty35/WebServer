package com.web.network;

import com.web.WebServer;
import java.net.InetSocketAddress;
import java.util.concurrent.Executors;

import org.jboss.netty.bootstrap.ServerBootstrap;
import org.jboss.netty.channel.ChannelPipeline;
import org.jboss.netty.channel.ChannelPipelineFactory;
import org.jboss.netty.channel.Channels;
import org.jboss.netty.channel.socket.nio.NioServerSocketChannelFactory;

/**
*
* @author Nasty(Daniel)
*/
public class Server {
    
    private static ServerBootstrap bootstrap;
	
	public static void run() {
		// Configure the server.
		bootstrap = new ServerBootstrap(
				new NioServerSocketChannelFactory(
						Executors.newCachedThreadPool(),
						Executors.newCachedThreadPool()));
		
		// Set up the pipeline factory.
		bootstrap.setPipelineFactory(new ChannelPipelineFactory() {
			public ChannelPipeline getPipeline() throws Exception {
				return Channels.pipeline(new Connection());
				}
			});
		
		// Bind and start to accept incoming connections.
		bootstrap.bind(new InetSocketAddress(Integer.parseInt(WebServer.port)));
		WebServer.Write("Server started on port ".concat(WebServer.port));
	}
        
    public static void Close() {
        bootstrap = null;
        System.exit(0);
    }

}
