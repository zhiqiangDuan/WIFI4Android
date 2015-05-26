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
        
        //初始化控件,设置文本默认值及按钮监听器
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
        //为文本添加setMovementMethod方法
        recvMsg= (TextView)findViewById(R.id.recvMsg);       
        recvMsg.setMovementMethod(ScrollingMovementMethod.getInstance());
        dataChangedB = new byte[36];
        main = new Main();
	}
	//连接服务器按钮监听器
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
				showDialog("请输入有效的数据");
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
		//连接服务器
		mSocketClient = main.getSocket();
		//取得输入、输出流
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
		resultData = data.getExtras().getString("str");//得到新Activity 关闭后返回的数据
        //showDialog(result);
        //Log.i(TAG, result);
    }
	/*
	 * 这个里面有两个按钮，连接按钮获取IP与port端口号。 然后打开一个runnable线程
	 * 此线程先创建一个socket 然后循环读取server的消息。
	 * 另一个按钮，只是向server发送数据
	 * */
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
				//"接收异常:" + e.getMessage() + "\n";
				Message msg = new Message();
                msg.what = 1;
				mHandler.sendMessage(msg);
			}
		}
	};
	//线程:监听服务器发来的消息
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
				if(flagShake) //收到数据
				{
					//System.out.println("Received!");
					flagShake = false; 
					//收到消息，给UI线程发个message
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
					//"接收异常:" + e.getMessage() + "\n";
					Message msg = new Message();
	                msg.what = 1;
					mHandler.sendMessage(msg);
				}
			}
	};
	public void listen() throws IOException {  
        // 轮询访问selector  
        while (true)
        {  
            selector.select();  
            // 获得selector中选中的项的迭代器  
            Iterator ite = this.selector.selectedKeys().iterator();  
            while (ite.hasNext())
            {  
            	System.out.println("Data comming!!!");
                SelectionKey key = (SelectionKey) ite.next();  
                // 删除已选的key,以防重复处理  
                ite.remove();  
                // 连接事件发生  
                if (key.isConnectable())
                {  
                    SocketChannel channel = (SocketChannel) key  
                            .channel();  
                    // 如果正在连接，则完成连接  
                    if(channel.isConnectionPending())
                    {  
                        channel.finishConnect();  
                          
                    }  
                    // 设置成非阻塞  
                    channel.configureBlocking(false);  
  
                    //在这里可以给服务端发送信息哦  
                    channel.write(ByteBuffer.wrap(new String("向服务端发送了一条信息").getBytes()));  
                    //在和服务端连接成功之后，为了可以接收到服务端的信息，需要给通道设置读的权限。  
                    channel.register(this.selector, SelectionKey.OP_READ);  
                      
                    // 获得了可读的事件  
                }
                else if (key.isReadable())
                {  
                	Toast.makeText(mContext, "收到数据！", Toast.LENGTH_SHORT).show();
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
            // 服务器可读取消息:得到事件发生的Socket通道  
            SocketChannel channel = (SocketChannel) key.channel();  
            // 创建读取的缓冲区  
            ByteBuffer buffer = ByteBuffer.allocate(10);  
            channel.read(buffer);  
            byte[] data = buffer.array();  
            String msg = new String(data).trim();  
            System.out.println("服务端收到信息："+msg);  
            ByteBuffer outBuffer = ByteBuffer.wrap(msg.getBytes());  
            channel.write(outBuffer);// 将消息回送给客户端  
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
					  showDialog("错误数据！");
					break;
				  case 1:  // 超时！
					  showDialog("超时！");
					//recvMsg.append("Client: "+recvMessageClient);
					break;
				  case 2:
					recvMsg.append("握手成功!\n");
					break;
				  case 3:
					 recvMsg.append("读取成功！\n");
					 //解析返回的数据
					 //显示返回的数据
					 tempH = dataRecv[13] & 0xff;
					 tempL = dataRecv[14] & 0xff;
					 System.out.println(tempH+" "+tempL+"\n");
					 temp = (tempH << 8) | tempL;
					 showString  = "最大允许电流："+temp;
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
					  recvMsg.append("数据修改成功！");
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
		     new AlertDialog.Builder(Sendmsg.this).setTitle("错误提示")//设置对话框标题  
		  
		     .setMessage(err)//设置显示的内容  
		  
		     .setPositiveButton("确定",new DialogInterface.OnClickListener() {//添加确定按钮  
		         @Override  
		         public void onClick(DialogInterface dialog, int which) {//确定按钮的响应事件  
		  
		             // TODO Auto-generated method stub  
		        	// System.out.println("Error!");
		             finish();  
		         } 
		     }).show();//在按键响应事件中显示此对话框  
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
