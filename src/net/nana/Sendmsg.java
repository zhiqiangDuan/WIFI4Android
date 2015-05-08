package net.nana;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.StringTokenizer;

import org.apache.http.impl.conn.tsccm.WaitingThread;

import android.R.string;
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
	private final String STR_SHAKE_HAND ="55020057";
	private Thread mThreadClient = null;
	private static Socket mSocketClient = null;
//	static BufferedReader mBufferedReaderServer	= null;
//	static PrintWriter mPrintWriterServer = null;
	static BufferedReader mBufferedReaderClient	= null;
	static PrintWriter mPrintWriterClient = null;
	private  String recvMessageClient = "";
	private  String recvMessageServer = "";
	public Main main = null;
	public InputStream inputStream;
	public OutputStream op = null;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.sendmsg);
        mContext = this;
        
        //��ʼ���ؼ�,�����ı�Ĭ��ֵ����ť������
        IPText= (EditText)findViewById(R.id.ipText);
        //IPText.setText("10.0.2.15:");
        IPText.setText("192.168.168.103:1234");

        connectButton= (Button)findViewById(R.id.connectButton);
        connectButton.setOnClickListener(StartClickListener);
        
        sendMsg= (EditText)findViewById(R.id.sendMsg);	   
        sendMsg.setText("up");
               
        sendButton= (Button)findViewById(R.id.sendButton);
        sendButton.setOnClickListener(SendClickListenerClient);
        //Ϊ�ı����setMovementMethod����
        recvMsg= (TextView)findViewById(R.id.recvMsg);       
        recvMsg.setMovementMethod(ScrollingMovementMethod.getInstance());
        main = new Main();
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
	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
	}
	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		mSocketClient = main.getSocket();
		if(mSocketClient == null)
		{
			System.out.println("Error,Please ensure that  you have opened the rechi controler and connected to the wifi");
		}
		super.onResume();
	}

	/*
	 * ���������������ť�����Ӱ�ť��ȡIP��port�˿ںš� Ȼ���һ��runnable�߳�
	 * ���߳��ȴ���һ��socket Ȼ��ѭ����ȡserver����Ϣ��
	 * ��һ����ť��ֻ����server��������
	 * */
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
			int count = 0;
			byte[] buffer = new byte[256];
			byte[] check;
			try 
			{				
				//���ӷ�����
				mSocketClient = main.getSocket();
				//ȡ�����롢�����
				inputStream = mSocketClient.getInputStream();
				op  = mSocketClient.getOutputStream();
				recvMessageClient = "�Ѿ�����server!\n";//��Ϣ����
				Message msg = new Message();
                msg.what = 1;
				mHandler.sendMessage(msg);		
			}
			catch (Exception e) 
			{
				Message msg = new Message();
                msg.what = 1;
				mHandler.sendMessage(msg);
				return;
			}			
			//write the data to the controler
			writeData(STR_SHAKE_HAND);
			//sleep 10ms
			//wait10ms();
			while (isConnecting)
			{
				try
				{
					if((count = inputStream.read(buffer))> 0)
					{
						check = cpoyData(buffer,count);
						System.out.println(checkSum(check));
						snedMessage(2);
					}	
					else {
						continue;
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
	private boolean startPing(String ip){   Log.e("Ping", "startPing...");  
    boolean success=false;  
    Process p =null;  
      
     try {   
            p = Runtime.getRuntime().exec("ping -c 1 -i 0.2 -W 1 " +ip);   
            int status = p.waitFor();   
            if (status == 0) {   
                success=true;   
            } else {   
                success=false;    
            }   
            } catch (IOException e) {   
                success=false;     
            } catch (InterruptedException e) {   
                success=false;     
            }finally{  
                p.destroy();  
            }  
           
     return success;  
} 
	Handler mHandler = new Handler()
	{										
		  public void handleMessage(Message msg)										
		  {											
			  super.handleMessage(msg);			
			  switch (msg.what)
			  {
			  case 0:
				recvMsg.append("Server: "+recvMessageServer);
				break;
			  case 1:
				recvMsg.append("Client: "+recvMessageClient);
				break;
			  case 2:
				recvMsg.append("���ֳɹ���");
				break;
			}
		  }									
	 };

	 	private byte[] cpoyData(byte[] dataRecv,int length)
	 	{
	 		byte[] check = new byte[length];
			for(int i = 0;i < length;i++) // copy the data to a matched array
			{
				check[i] = dataRecv[i];
			}
	 		return check;
	 	}
			private boolean checkSum(byte[] byt)
			{
				int sum = 0;
				int temp = 0;
				int last = 0;
				for(int i = 0;i < byt.length -1;i++)
				{
					
					temp = 0xff & byt[i];
					
						sum+=temp;
				}
				int  check = sum & 0xff;
				last = 0xff & byt[byt.length -1];
				if(check == last)
				{
					return true;
				}
				return false;
			}
			public void writeData(String s)
			{
				try {
					op.write(toStringHex(s));
				} catch (IOException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
				
			}
			public void snedMessage(int falg)
			{
				Message msg = new Message();
                msg.what = falg;
				mHandler.sendMessage(msg);
				
			}
			private void wait10ms()
			{
				try {
					Thread.sleep(20*1000);
				} catch (InterruptedException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
			}
			public static byte[] toStringHex(String s) 
			{ 
				byte[] baKeyword = new byte[s.length()/2]; 
				for(int i = 0; i < baKeyword.length; i++) 
				{ 
					try 
					{ 
						baKeyword[i] = (byte)(0xff & Integer.parseInt(s.substring(i*2, i*2+2),16)); 
					} 	
					catch(Exception e) 
					{ 
						e.printStackTrace(); 
					} 
				} 
				return baKeyword; 
			} 

}
