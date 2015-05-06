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
        
        //��ʼ���ؼ�,�����ı�Ĭ��ֵ����ť������
        IPText= (EditText)findViewById(R.id.ipText);
        //IPText.setText("10.0.2.15:");
        IPText.setText("192.168.2.101:59671");

        connectButton= (Button)findViewById(R.id.connectButton);
        connectButton.setOnClickListener(StartClickListener);
        
        sendMsg= (EditText)findViewById(R.id.sendMsg);	   
        sendMsg.setText("up");
               
        sendButton= (Button)findViewById(R.id.sendButton);
        sendButton.setOnClickListener(SendClickListenerClient);
        //Ϊ�ı����setMovementMethod����
        recvMsg= (TextView)findViewById(R.id.recvMsg);       
        recvMsg.setMovementMethod(ScrollingMovementMethod.getInstance());
	}
	
	//���ӷ�������ť������
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
				
				connectButton.setText("��ʼ����");					
				IPText.setEnabled(true);		
				recvMsg.setText("��Ϣ:\n");
			}
			else
			{				
				isConnecting = true;
				connectButton.setText("ֹͣ����");						
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
				String msgText =sendMsg.getText().toString();//ȡ�ñ༭�����������������
				if(msgText.length()<=0)
				{
					Toast.makeText(mContext, "�������ݲ���Ϊ�գ�", Toast.LENGTH_SHORT).show();
				}
				else
				{
					try 
					{				    	
				    	mPrintWriterClient.print(msgText);//���͸�������
				    	mPrintWriterClient.flush();
					}
					catch (Exception e) 
					{
						// TODO: handle exception
						Toast.makeText(mContext, "�����쳣��" + e.getMessage(), Toast.LENGTH_SHORT).show();
					}
				}
			}
			else
			{
				Toast.makeText(mContext, "û������", Toast.LENGTH_SHORT).show();
			}
		}
	};
	
	//�߳�:������������������Ϣ
	private Runnable	mRunnable	= new Runnable() 
	{
		public void run()
		{
			String msgText =IPText.getText().toString();
			if(msgText.length()<=0)
			{
				//Toast.makeText(mContext, "IP����Ϊ�գ�", Toast.LENGTH_SHORT).show();
				recvMessageClient = "IP����Ϊ�գ�\n";//��Ϣ����
				Message msg = new Message();
                msg.what = 1;
				mHandler.sendMessage(msg);
				return;
			}
			int start = msgText.indexOf(":");
			if( (start == -1) ||(start+1 >= msgText.length()) )
			{
				recvMessageClient = "IP��ַ���Ϸ�\n";//��Ϣ����
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
				//���ӷ�����
				mSocketClient = new Socket(sIP, port);	//portnum
				//ȡ�����롢�����
				mBufferedReaderClient = new BufferedReader(new InputStreamReader(mSocketClient.getInputStream()));
				
				mPrintWriterClient = new PrintWriter(mSocketClient.getOutputStream(), true);
				
				recvMessageClient = "�Ѿ�����server!\n";//��Ϣ����
				Message msg = new Message();
                msg.what = 1;
				mHandler.sendMessage(msg);		
				//break;
			}
			catch (Exception e) 
			{
				recvMessageClient = "����IP�쳣:" + e.toString() + e.getMessage() + "\n";//��Ϣ����
				Message msg = new Message();
                msg.what = 1;
				mHandler.sendMessage(msg);
				return;
			}			

			char[] buffer = new char[256];
			int count = 0;
			
			//================��һ�Σ���һ�Ρ�û�ж������ݾ�����======================
			while (isConnecting)
			{
				try
				{
					//if ( (recvMessageClient = mBufferedReaderClient.readLine()) != null )
					if((count = mBufferedReaderClient.read(buffer))>0)
					{						
						recvMessageClient = getInfoBuff(buffer, count) + "\n";//��Ϣ����
						Message msg = new Message();
		                msg.what = 1;
						mHandler.sendMessage(msg);
					}
				}
				catch (Exception e)
				{
					recvMessageClient = "�����쳣:" + e.getMessage() + "\n";//��Ϣ����
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
				  recvMsg.append("Server: "+recvMessageServer);	// ˢ��
			  }
			  else if(msg.what == 1)
			  {
				  recvMsg.append("Client: "+recvMessageClient);	// ˢ��

			  }
		  }									
	 };
		
	 
	 //�߳�:������������������Ϣ
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

	                //�������ڵȴ��ͷ����� 
	                mSocketServer = serverSocket.accept();	                	               
	                
	                //���ܿͷ�������BufferedReader����
	                mBufferedReaderServer = new BufferedReader(new InputStreamReader(mSocketServer.getInputStream()));
	                //���ͷ��˷�������
	                mPrintWriterServer = new PrintWriter(mSocketServer.getOutputStream(),true);
	                //mPrintWriter.println("������Ѿ��յ����ݣ�");

	                Message msg = new Message();
	                msg.what = 0;
	                recvMessageServer = "client�Ѿ������ϣ�\n";
	                mHandler.sendMessage(msg);
	                
				} catch (IOException e) {
					// TODO Auto-generated catch block
					//e.printStackTrace();
					Message msg = new Message();
	                msg.what = 0;
					recvMessageServer = "�����쳣:" + e.getMessage() + e.toString() + "\n";//��Ϣ����
					mHandler.sendMessage(msg);
					return;
				}
				char[] buffer = new char[256];
				int count = 0;
				while(serverRuning)
				{
					try
					{
						//if( (recvMessageServer = mBufferedReaderServer.readLine()) != null )//��ȡ�ͷ�������
						if((count = mBufferedReaderServer.read(buffer))>0);
						{						
							recvMessageServer = getInfoBuff(buffer, count) + "\n";//��Ϣ����
							Message msg = new Message();
			                msg.what = 0;
							mHandler.sendMessage(msg);
						}
					}
					catch (Exception e)
					{
						recvMessageServer = "�����쳣:" + e.getMessage() + "\n";//��Ϣ����
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
