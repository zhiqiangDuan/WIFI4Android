package net.nana;

import java.io.IOException;
import java.net.Socket;
import java.net.UnknownHostException;

public class SocketTest {
	public Socket mSocketClient = null;
	
	public Socket SocketInit(String ipStr,String portStr)
	{
		int port = Integer.parseInt(portStr);
		try {
			mSocketClient = new Socket(ipStr,port);
		} catch (UnknownHostException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return mSocketClient;
	}
	public Socket getSocket()
	{
		
		return mSocketClient;
	}
}
