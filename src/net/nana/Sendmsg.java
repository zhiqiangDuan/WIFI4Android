package net.nana;

import java.io.BufferedReader;
import java.io.Closeable;
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

import android.R.integer;
import android.R.string;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
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

public class Sendmsg extends Activity implements OnClickListener{
	
	private Button connectButton;	
	private EditText IPText;
	private Button sendButton;
	private Button sendData;
	private Button searchData;
	private Button changeData;
	private EditText sendMsg;
	private TextView recvMsg;
		
	private Context mContext;
	private boolean isConnecting = false;
	private final String STR_SHAKE_HAND ="55020057";
	private final String STR_SEARCH = "5518006D";
	private final String STR_SEARCH2 = "5519006E";
	private final String STR_DATA_CHANGE = "5518200000D5560017025807DF0081475E43CD23AF2E6301C834A201C831112000001486";
	private final int SEARCH_DATA = 1;
	private final int SEND_DATA = 2;
	private Thread mThreadClient = null;
	private static Socket mSocketClient = null;
	static BufferedReader mBufferedReaderClient	= null;
	static PrintWriter mPrintWriterClient = null;
	private  String recvMessageServer = "";
	public Main main = null;
	public InputStream inputStream;
	public OutputStream op = null;
	public Selector selector = null;
	private boolean flagShake = false;
	private boolean flagOver = false;
	private Thread statusThread = null;
	private Thread searchThread = null;
	private Thread changeThread = null;
	private int checkFlag = -1;
	private String snedDataString = null;
	private byte[] dataRecv = null;
	private String resultData;
	private byte[] dataChangedB = null;
	private String dataChangedS = null;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.sendmsg);
        mContext = this;
        
        //��ʼ���ؼ�,�����ı�Ĭ��ֵ����ť������
        IPText= (EditText)findViewById(R.id.ipText);
        //IPText.setText("10.0.2.15:");
        IPText.setText("192.168.168.102:1234");

        connectButton= (Button)findViewById(R.id.connectButton);
        connectButton.setOnClickListener(StartClickListener);
        changeData = (Button)findViewById(R.id.changeData);
        changeData.setOnClickListener(Sendmsg.this);
        searchData = (Button)findViewById(R.id.searchData);
        searchData.setOnClickListener(Sendmsg.this);
        sendData = (Button)findViewById(R.id.sendData);
        sendData.setOnClickListener(Sendmsg.this);
        // sendMsg= (EditText)findViewById(R.id.sendMsg);	   
        //sendMsg.setText("up");
               
        //sendButton= (Button)findViewById(R.id.sendData);
        //sendButton.setOnClickListener(SendClickListenerClient);
        //Ϊ�ı����setMovementMethod����
        recvMsg= (TextView)findViewById(R.id.recvMsg);       
        recvMsg.setMovementMethod(ScrollingMovementMethod.getInstance());
        dataChangedB = new byte[36];
        main = new Main();
	}
	//���ӷ�������ť������
	private OnClickListener StartClickListener = new OnClickListener() {
		@Override
		public void onClick(View v) {
			// TODO Auto-generated method stub	
			flagShake = false;
			mThreadClient = new Thread(mRunnable);
			mThreadClient.start();
			statusThread = new Thread(backrun);
			statusThread.start();
		}
	};
	public void onClick(View v) {
		// TODO Auto-generated method stub
		switch (v.getId()) {
		case R.id.sendData:
			if(resultData == null)
			{
				resultData = "24";
			}
			int a = Integer.parseInt(resultData);
			if(a < 24 || a > 120)
			{
				showDialog("��������Ч������");
			}
			dataRecv[0] = 0x55;
			dataRecv[1] = 0x28;
			dataRecv[13] = 0;
			dataRecv[14] = (byte)(a & 0xff);
			dataRecv[35] = setCheckSum(dataRecv);
			System.out.println("a = "+ a + dataRecv[35]+"----------\n");
			//dataChangedS = byteToString(dataChangedB);
			flagShake = false;
			flagOver = false;
			for(int k = 0;k < 36;k++)
			{
				System.out.println((dataRecv[k] & 0xff) +  "  ");
			}
			changeThread = new Thread(changeRun);
			changeThread.start();
			statusThread = new Thread(backrun);
			statusThread.start();
			break;
		case R.id.searchData:
			// start the search thread
			// start the backrun thread
			flagShake = false;
			flagOver = false;
			checkFlag = SEARCH_DATA;
			searchThread = new Thread(searchRun);
			searchThread.start();
			statusThread = new Thread(backrun);
			statusThread.start();
			break;
		case R.id.changeData:
			Intent intent2 = new Intent();
			intent2.setClass(Sendmsg.this, ChangeData.class);
			startActivityForResult(intent2, 1);
			//startActivity(intent2);
			break;
		default:
			break;
		}
	} 
	private String byteToString(byte[] byt) {
		
		return null;
	}
	private byte setCheckSum(byte[] byt) {
		int sum = 0;
		int temp = 0;
		for(int i = 0;i < byt.length-1;i++)
		{
				temp = byt[i] & 0xff;
				sum +=temp;
		}
		return (byte)(sum & 0xff);
	}
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
		//���ӷ�����
		mSocketClient = main.getSocket();
		//ȡ�����롢�����
		try {
			inputStream = mSocketClient.getInputStream();
			op  = mSocketClient.getOutputStream();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		if(mSocketClient == null)
		{
			System.out.println("Error,Please ensure that  you have opened the rechi controler and connected to the wifi");
		}
		super.onResume();
	}
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		resultData = data.getExtras().getString("str");//�õ���Activity �رպ󷵻ص�����
        //showDialog(result);
        //Log.i(TAG, result);
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
	private Runnable	changeRun	= new Runnable()
	{

		int count = 0;
		byte[] buffer = new byte[256];
		byte[] check;
		@Override
		public void run() {
			// TODO Auto-generated method stub
			flagShake = true;
			try {
				op.write(dataRecv);
				if((count = inputStream.read(buffer))> 0)
				{
					check = cpoyData(buffer,count);
					if(checkSum(check))
					{
						dataRecv = check;
						flagOver = true;
						snedMessage(5);
					}
					else {
						snedMessage(0);
					}
				}
			} catch (IOException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
		}
		
		
	};
	private Runnable	searchRun	= new Runnable()
	{
		int count = 0;
		byte[] buffer = new byte[256];
		byte[] check;
		@Override
		public void run() {
			// TODO Auto-generated method stub
			writeData(STR_SHAKE_HAND);
			try
			{
				if((count = inputStream.read(buffer))> 0)
				{
					check = cpoyData(buffer,count);
					if(checkSum(check))
					{
						flagShake = true;
						snedMessage(2);
					}
					else {
						snedMessage(0);
					}	
			}	
				Thread.sleep(300); // wait for 300ms
				writeData(STR_SEARCH);  // write the search cmd!
				if((count = inputStream.read(buffer))> 0)
				{
					check = cpoyData(buffer,count);
					if(checkSum(check))
					{
						dataRecv = check;
						flagOver = true;
						snedMessage(3);
					}
					else {
						snedMessage(0);
					}
				}
			}
			catch (Exception e)
			{
				//"�����쳣:" + e.getMessage() + "\n";
				Message msg = new Message();
                msg.what = 1;
				mHandler.sendMessage(msg);
			}
		}
	};
	//�߳�:������������������Ϣ
	private Runnable	backrun	= new Runnable()
	{
		int count = 200;
		//@SuppressWarnings("deprecation")
		@Override
		public void run() {
			// TODO Auto-generated method stub
			while(count > 0)
			{
				System.out.println(count+"===");
				wait10ms();
				if(flagShake) //�յ�����
				{
					//System.out.println("Received!");
					flagShake = false; 
					//�յ���Ϣ����UI�̷߳���message
					break;
				}
				count--;
			}
			if(count == 0)
			{
				snedMessage(1);
				
			}
			count = 400;
			while(count > 0)
			{
				wait10ms();
				if(flagOver)
				{
					flagOver = false;
					//snedMessage(5);
					return;
				}
				count--;
				System.out.println("count = "+count);
			}
			System.out.println("count = "+count);
			if(count == 0)
			{
				snedMessage(1);
				return;
				
			}
		}
	};
	private Runnable mRunnable	= new Runnable() 
	{
		public void run()
		{
			int count = 0;
			byte[] buffer = new byte[256];
			byte[] check;
				writeData(STR_SHAKE_HAND);
				try
				{
					if((count = inputStream.read(buffer))> 0)
					{
						check = cpoyData(buffer,count);
						if(checkSum(check))
						{
							flagShake = true;
							
							snedMessage(2);
						}
						else {
							snedMessage(0);
						}
					}
					System.out.println("recv"+dataRecv.length);
					op.write(dataRecv);
					System.out.println("222");
					if((count = inputStream.read(buffer))> 0)
					{
						System.out.println("333");
						check = cpoyData(buffer,count);
						if(checkSum(check))
						{
							System.out.println("444");
							flagOver = true;
							snedMessage(3);
						}
						else {
							snedMessage(0);
						}
						
					}
				}
				catch (Exception e)
				{
					//"�����쳣:" + e.getMessage() + "\n";
					Message msg = new Message();
	                msg.what = 1;
					mHandler.sendMessage(msg);
				}
			}
	};
	public void listen() throws IOException {  
        // ��ѯ����selector  
        while (true)
        {  
            selector.select();  
            // ���selector��ѡ�е���ĵ�����  
            Iterator ite = this.selector.selectedKeys().iterator();  
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
	
	private boolean readData(byte[] buffer)
	{
		int count = 0;
		byte[] check = null;
		try {
			if((count = inputStream.read(buffer))> 0)
			{
				check = cpoyData(buffer,count);
				return checkSum(check);
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return false;
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
		String showString = null;
		int tempH = 0; //high 8
		int tempL = 0; // low 8
		int temp = 0;
		  public void handleMessage(Message msg)										
		  {											
			  super.handleMessage(msg);			
			  switch (msg.what)
			  {
				  case 0:		//ERROR!!!
					  showDialog("�������ݣ�");
					break;
				  case 1:  // ��ʱ��
					  showDialog("��ʱ��");
					//recvMsg.append("Client: "+recvMessageClient);
					break;
				  case 2:
					recvMsg.append("���ֳɹ�!\n");
					break;
				  case 3:
					 recvMsg.append("��ȡ�ɹ���\n");
					 //�������ص�����
					 //��ʾ���ص�����
					 tempH = dataRecv[13] & 0xff;
					 tempL = dataRecv[14] & 0xff;
					 System.out.println(tempH+" "+tempL+"\n");
					 temp = (tempH << 8) | tempL;
					 showString  = "������������"+temp;
					 recvMsg.append(showString);
					 break;
					 /*
					 for(int i = 3;i < dataRecv.length - 1;i++)
					 {
						 tempH = dataRecv[i] & 0xff;
						 tempL = dataRecv[i+1] & 0xff;
						 temp = (tempH << 8) | tempL;
						 showString  = " "+temp + " "+ ((i-1)/2-1) ;
						 i++;
						 recvMsg.append(showString+"\n");
						 
						// showString += dataRecv[i] & 0xff; 
						// showString+=" ";
					 }*/
					// recvMsg.append(showString);
				  case 5:
					  recvMsg.append("�����޸ĳɹ���");
					break;
			}
		  }									
	 };
	 private String strToCMD(String str)
	 {
		 String a[] = str.split(" "); 
		 for(int i = 0;i < a.length;i++)
		 {
			 dataRecv[2*i+3] = 0x01;
			 dataRecv[2*i+4] = 0x01;
			 
		 }
		 return str;
	 }
	 private  void showDialog(String err)
	 {
		     new AlertDialog.Builder(Sendmsg.this).setTitle("������ʾ")//���öԻ������  
		  
		     .setMessage(err)//������ʾ������  
		  
		     .setPositiveButton("ȷ��",new DialogInterface.OnClickListener() {//���ȷ����ť  
		         @Override  
		         public void onClick(DialogInterface dialog, int which) {//ȷ����ť����Ӧ�¼�  
		  
		             // TODO Auto-generated method stub  
		        	// System.out.println("Error!");
		             finish();  
		         } 
		     }).show();//�ڰ�����Ӧ�¼�����ʾ�˶Ի���  
	 }
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
					Thread.sleep(10);
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
