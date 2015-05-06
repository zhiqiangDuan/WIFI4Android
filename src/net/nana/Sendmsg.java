package net.nana;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.method.ScrollingMovementMethod;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

public class Sendmsg extends Activity{
	
	private Button connectButton;	
	private EditText IPText;
	private Button sendButton;	
	private EditText sendMsg;
	private TextView recvMsg;
		
	private Context mContext;
	private boolean isConnecting = false;
	
	private Thread mThreadClient = null;
	private Socket mSocketClient = null;
//	static BufferedReader mBufferedReaderServer	= null;
//	static PrintWriter mPrintWriterServer = null;
	static BufferedReader mBufferedReaderClient	= null;
	static PrintWriter mPrintWriterClient = null;
	private  String recvMessageClient = "";
	private  String recvMessageServer = "";
	

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.sendmsg);
        mContext = this;
        
        //初始化控件,设置文本默认值及按钮监听器
        IPText= (EditText)findViewById(R.id.ipText);
        //IPText.setText("10.0.2.15:");
        IPText.setText("192.168.2.101:59671");

        connectButton= (Button)findViewById(R.id.connectButton);
        connectButton.setOnClickListener(StartClickListener);
        
        sendMsg= (EditText)findViewById(R.id.sendMsg);	   
        sendMsg.setText("up");
               
        sendButton= (Button)findViewById(R.id.sendButton);
        sendButton.setOnClickListener(SendClickListenerClient);
        //为文本添加setMovementMethod方法
        recvMsg= (TextView)findViewById(R.id.recvMsg);       
        recvMsg.setMovementMethod(ScrollingMovementMethod.getInstance());
	}
	
	//连接服务器按钮监听器
	private OnClickListener StartClickListener = new OnClickListener() {
		@Override
		public void onClick(View arg0) {
			// TODO Auto-generated method stub				
			if (isConnecting) 
			{				
				isConnecting = false;
				try {
					if(mSocketClient!=null)
					{
						mSocketClient.close();
						mSocketClient = null;
						
						mPrintWriterClient.close();
						mPrintWriterClient = null;
					}
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				mThreadClient.interrupt();
				
				connectButton.setText("开始连接");					
				IPText.setEnabled(true);		
				recvMsg.setText("信息:\n");
			}
			else
			{				
				isConnecting = true;
				connectButton.setText("停止连接");						
				IPText.setEnabled(false);
				
				mThreadClient = new Thread(mRunnable);
				mThreadClient.start();				
			}
		}
	};
	
	private OnClickListener SendClickListenerClient = new OnClickListener() {
		@Override
		public void onClick(View arg0) {
			// TODO Auto-generated method stub				
			if ( isConnecting && mSocketClient!=null) 
			{
				String msgText =sendMsg.getText().toString();//取得编辑框中我们输入的内容
				if(msgText.length()<=0)
				{
					Toast.makeText(mContext, "发送内容不能为空！", Toast.LENGTH_SHORT).show();
				}
				else
				{
					try 
					{				    	
				    	mPrintWriterClient.print(msgText);//发送给服务器
				    	mPrintWriterClient.flush();
					}
					catch (Exception e) 
					{
						// TODO: handle exception
						Toast.makeText(mContext, "发送异常：" + e.getMessage(), Toast.LENGTH_SHORT).show();
					}
				}
			}
			else
			{
				Toast.makeText(mContext, "没有连接", Toast.LENGTH_SHORT).show();
			}
		}
	};
	
	//线程:监听服务器发来的消息
	private Runnable	mRunnable	= new Runnable() 
	{
		public void run()
		{
			String msgText =IPText.getText().toString();
			if(msgText.length()<=0)
			{
				//Toast.makeText(mContext, "IP不能为空！", Toast.LENGTH_SHORT).show();
				recvMessageClient = "IP不能为空！\n";//消息换行
				Message msg = new Message();
                msg.what = 1;
				mHandler.sendMessage(msg);
				return;
			}
			int start = msgText.indexOf(":");
			if( (start == -1) ||(start+1 >= msgText.length()) )
			{
				recvMessageClient = "IP地址不合法\n";//消息换行
				Message msg = new Message();
                msg.what = 1;
				mHandler.sendMessage(msg);
				return;
			}
			String sIP = msgText.substring(0, start);
			String sPort = msgText.substring(start+1);
			int port = Integer.parseInt(sPort);				
			
			Log.d("gjz", "IP:"+ sIP + ":" + port);		

			try 
			{				
				//连接服务器
				mSocketClient = new Socket(sIP, port);	//portnum
				//取得输入、输出流
				mBufferedReaderClient = new BufferedReader(new InputStreamReader(mSocketClient.getInputStream()));
				
				mPrintWriterClient = new PrintWriter(mSocketClient.getOutputStream(), true);
				
				recvMessageClient = "已经连接server!\n";//消息换行
				Message msg = new Message();
                msg.what = 1;
				mHandler.sendMessage(msg);		
				//break;
			}
			catch (Exception e) 
			{
				recvMessageClient = "连接IP异常:" + e.toString() + e.getMessage() + "\n";//消息换行
				Message msg = new Message();
                msg.what = 1;
				mHandler.sendMessage(msg);
				return;
			}			

			char[] buffer = new char[256];
			int count = 0;
			
			//================发一次，读一次。没有读到数据就跳过======================
			while (isConnecting)
			{
				try
				{
					//if ( (recvMessageClient = mBufferedReaderClient.readLine()) != null )
					if((count = mBufferedReaderClient.read(buffer))>0)
					{						
						recvMessageClient = getInfoBuff(buffer, count) + "\n";//消息换行
						Message msg = new Message();
		                msg.what = 1;
						mHandler.sendMessage(msg);
					}
				}
				catch (Exception e)
				{
					recvMessageClient = "接收异常:" + e.getMessage() + "\n";//消息换行
					Message msg = new Message();
	                msg.what = 1;
					mHandler.sendMessage(msg);
				}
			}
		}
	};
	
	Handler mHandler = new Handler()
	{										
		  public void handleMessage(Message msg)										
		  {											
			  super.handleMessage(msg);			
			  if(msg.what == 0)
			  {
				  recvMsg.append("Server: "+recvMessageServer);	// 刷新
			  }
			  else if(msg.what == 1)
			  {
				  recvMsg.append("Client: "+recvMessageClient);	// 刷新

			  }
		  }									
	 };
		
	 
	 //线程:监听服务器发来的消息
	/*	private Runnable	mcreateRunnable	= new Runnable() 
		{
			public void run()
			{				
				try {
					serverSocket = new ServerSocket(0);
					
					SocketAddress address = null;	
					if(!serverSocket.isBound())	
					{
						serverSocket.bind(address, 0);
					}
					
					
					getLocalIpAddress();

	                //方法用于等待客服连接 
	                mSocketServer = serverSocket.accept();	                	               
	                
	                //接受客服端数据BufferedReader对象
	                mBufferedReaderServer = new BufferedReader(new InputStreamReader(mSocketServer.getInputStream()));
	                //给客服端发送数据
	                mPrintWriterServer = new PrintWriter(mSocketServer.getOutputStream(),true);
	                //mPrintWriter.println("服务端已经收到数据！");

	                Message msg = new Message();
	                msg.what = 0;
	                recvMessageServer = "client已经连接上！\n";
	                mHandler.sendMessage(msg);
	                
				} catch (IOException e) {
					// TODO Auto-generated catch block
					//e.printStackTrace();
					Message msg = new Message();
	                msg.what = 0;
					recvMessageServer = "创建异常:" + e.getMessage() + e.toString() + "\n";//消息换行
					mHandler.sendMessage(msg);
					return;
				}
				char[] buffer = new char[256];
				int count = 0;
				while(serverRuning)
				{
					try
					{
						//if( (recvMessageServer = mBufferedReaderServer.readLine()) != null )//获取客服端数据
						if((count = mBufferedReaderServer.read(buffer))>0);
						{						
							recvMessageServer = getInfoBuff(buffer, count) + "\n";//消息换行
							Message msg = new Message();
			                msg.what = 0;
							mHandler.sendMessage(msg);
						}
					}
					catch (Exception e)
					{
						recvMessageServer = "接收异常:" + e.getMessage() + "\n";//消息换行
						Message msg = new Message();
		                msg.what = 0;
						mHandler.sendMessage(msg);
						return;
					}
				}
			}
		};*/
		
		 private String getInfoBuff(char[] buff, int count)
		{
			char[] temp = new char[count];
			for(int i=0; i<count; i++)
			{
				temp[i] = buff[i];
			}
			return new String(temp);
		} 		

}
