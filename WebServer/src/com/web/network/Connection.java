package com.web.network;

import com.web.WebServer;
import org.jboss.netty.buffer.ChannelBuffer;
import org.jboss.netty.channel.Channel;
import org.jboss.netty.channel.ChannelHandlerContext;
import org.jboss.netty.channel.ChannelStateEvent;
import org.jboss.netty.channel.ExceptionEvent;
import org.jboss.netty.channel.MessageEvent;
import org.jboss.netty.channel.SimpleChannelUpstreamHandler;

/**
*
* @author Nasty(Daniel)
*/
public class Connection extends SimpleChannelUpstreamHandler {
	
	private Channel socket;
	private String user;

	@Override
	public void channelConnected(ChannelHandlerContext ctx, ChannelStateEvent e)
			throws Exception {
		this.socket = e.getChannel();
        this.user = this.socket.getRemoteAddress().toString()
            .replace("/", "").split(":")[0];
        if(WebServer.bans.contains(this.user)) {
            WebServer.Write(this.user.concat("(banned) try login!"));
		    this.socket.disconnect();
			this.user = null;
		    this.socket = null;
        }
	}

	@Override
	public void channelDisconnected(ChannelHandlerContext ctx,
			ChannelStateEvent e) throws Exception {
		this.socket.disconnect();
		this.socket = null;
		this.user = null;
	}

	@Override
	public void exceptionCaught(ChannelHandlerContext ctx, ExceptionEvent e)
			throws Exception {
		WebServer.Write(e.getCause().getMessage());
	}

	@Override
	public void messageReceived(ChannelHandlerContext ctx, MessageEvent e)
			throws Exception {
		final ChannelBuffer buffer = (ChannelBuffer) e.getMessage();
		byte[] Message = new byte[buffer.readableBytes()];
		buffer.getBytes(0, Message);
		String data = new String(Message);
		new Handle(data, this.socket, this.user);
	}

}
