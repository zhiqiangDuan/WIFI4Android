package net.nana;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.net.Socket;
import java.nio.channels.Selector;
import java.util.StringTokenizer;
import java.util.Iterator;  
import org.apache.http.impl.conn.tsccm.WaitingThread;
import java.nio.ByteBuffer;  
import java.nio.channels.SelectionKey;  
import java.nio.channels.Selector;  
import java.nio.channels.SocketChannel;  
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
	public Selector selector = null;
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
			mThreadClient = new Thread(mRunnable);
			mThreadClient.start();
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
		selector = main.getSelector();
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
			try {
				listen();
			} catch (IOException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
			try 
			{				
				//���ӷ�����
				//mSocketClient = main.getSocket();
				//ȡ�����롢�����
				inputStream = mSocketClient.getInputStream();
				op  = mSocketClient.getOutputStream();
				recvMessageClient = "�Ѿ�����server!\n";//��Ϣ����
				//Message msg = new Message();
               // msg.what = 1;
				//mHandler.sendMessage(msg);		
			}
			catch (Exception e) 
			{
				Message msg = new Message();
                msg.what = 1;
				mHandler.sendMessage(msg);
				return;
			}			
			//write the data to the controler
			try {
				listen();
			} catch (IOException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
			//writeData(STR_SHAKE_HAND);
			//sleep 10ms
			//wait10ms();
			/*
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
			}*/
		}
	};
	public void listen() throws IOException {  
        // ��ѯ����selector  
        while (true)
        {  
            selector.select();  
            // ���selector��ѡ�е���ĵ�����  
            Iterator ite = this.selector.selectedKeys().iterator();  
            System.out.println("111111111111111111");
            while (ite.hasNext())
            {  
            	System.out.println("Data comming!!!");
                SelectionKey key = (SelectionKey) ite.next();  
                // ɾ����ѡ��key,�Է��ظ�����  
                ite.remove();  
                // �����¼�����  
                if (key.isConnectable())
                {  
                    SocketChannel channel = (SocketChannel) key  
                            .channel();  
                    // ����������ӣ����������  
                    if(channel.isConnectionPending())
                    {  
                        channel.finishConnect();  
                          
                    }  
                    // ���óɷ�����  
                    channel.configureBlocking(false);  
  
                    //��������Ը�����˷�����ϢŶ  
                    channel.write(ByteBuffer.wrap(new String("�����˷�����һ����Ϣ").getBytes()));  
                    //�ںͷ�������ӳɹ�֮��Ϊ�˿��Խ��յ�����˵���Ϣ����Ҫ��ͨ�����ö���Ȩ�ޡ�  
                    channel.register(this.selector, SelectionKey.OP_READ);  
                      
                    // ����˿ɶ����¼�  
                }
                else if (key.isReadable())
                {  
                	Toast.makeText(mContext, "�յ����ݣ�", Toast.LENGTH_SHORT).show();
                	readFromServer(key);  
                }  
  
            } 
        }
	}
        public void readFromServer(SelectionKey key) throws IOException{  
            // �������ɶ�ȡ��Ϣ:�õ��¼�������Socketͨ��  
            SocketChannel channel = (SocketChannel) key.channel();  
            // ������ȡ�Ļ�����  
            ByteBuffer buffer = ByteBuffer.allocate(10);  
            channel.read(buffer);  
            byte[] data = buffer.array();  
            String msg = new String(data).trim();  
            System.out.println("������յ���Ϣ��"+msg);  
            ByteBuffer outBuffer = ByteBuffer.wrap(msg.getBytes());  
            channel.write(outBuffer);// ����Ϣ���͸��ͻ���  
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
