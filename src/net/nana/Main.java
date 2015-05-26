package net.nana;




import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.SocketChannel;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Message;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ScrollView;
import android.widget.TextView;

public class Main extends Activity implements OnClickListener{
	    // 右侧滚动条按钮
		private ScrollView sView;
		private Button openwifi;
		private Button closewifi;
		private Button scanwifi;
		private Button connect;
		private Button disconnect;
		private Button sendmsg;
		private Button shake;
		private TextView scanResult;

		private ArrayAdapter<String> wifilistArray;
		
		private String mScanResult;
		private WifiAdmin mWifiAdmin;
	
		 private static Selector selector; //NIO selector
		//=================================
		public static  Socket mSocketClient = null;
//		static BufferedReader mBufferedReaderServer	= null;
//		static PrintWriter mPrintWriterServer = null;
		static BufferedReader mBufferedReaderClient	= null;
		static PrintWriter mPrintWriterClient = null;
		public OutputStream op = null;
	/** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        
        sendmsg = (Button)findViewById(R.id.send);
        sendmsg.setOnClickListener(new SendButton());
        mWifiAdmin = new WifiAdmin(Main.this);
		init();
    }
    
    /**
	 * 按钮等控件的初始化
	 */
	public void init() {
		sView = (ScrollView) findViewById(R.id.mScrollView);
		openwifi= (Button) findViewById(R.id.openwifi);
		closewifi = (Button) findViewById(R.id.closewifi);
		scanwifi = (Button) findViewById(R.id.scanwifi);
		scanResult = (TextView) findViewById(R.id.scanresult);
		connect = (Button) findViewById(R.id.connect);
		disconnect = (Button) findViewById(R.id.disconnect);
		shake = (Button)findViewById(R.id.butshake);
		//sendmsg = (Button)findViewById(R.id.send);

		openwifi.setOnClickListener(Main.this);
		closewifi.setOnClickListener(Main.this);
		scanwifi.setOnClickListener(Main.this);
		connect.setOnClickListener(Main.this);
		disconnect.setOnClickListener(Main.this);
		shake.setOnClickListener(Main.this);
		//sendmsg.setOnClickListener(new SendButton());
	}
	public Socket getSocket()
	{
		return this.mSocketClient;
	}
	public Selector getSelector()
	{
		return this.selector;
		
	}
	//将以下内部类捆绑在send按钮上，按下时跳转到Sendmsg界面
    class SendButton implements OnClickListener{
		@Override
		public void onClick(View v) {
			// TODO Auto-generated method stub
			
			String sIP = "192.168.4.1";
			String sPort = "8080";

			try 
			{				
				//连接服务器
				mSocketClient = new Socket(sIP, 8080);
				  Intent intent = new Intent();
				intent.setClass(Main.this, Sendmsg.class);
				Main.this.startActivity(intent);
			}
			catch (Exception e) 
			{
				new AlertDialog.Builder(Main.this).setTitle("下位机错误")//设置对话框标题  
				  
			     .setMessage("下位机WIFI断线，请重启控制器重新连接")//设置显示的内容  
			  
			     .setPositiveButton("确定",new DialogInterface.OnClickListener() {//添加确定按钮  
			         @Override  
			         public void onClick(DialogInterface dialog, int which) {//确定按钮的响应事件  
			  
				         // TODO Auto-generated method stub  
			             //finish();  
			         } 
			     }).show();//在按键响应事件中显示此对话框  
				return;
			}			

		}    	
    }
	/**
	 * WIFI_STATE_DISABLING 0
	 * WIFI_STATE_DISABLED 1 
	 * WIFI_STATE_ENABLING 2
	 * WIFI_STATE_ENABLED 3
	 */
	public void openwifi() {
		mWifiAdmin.openwifi();
	}

	public void closewifi() {
		mWifiAdmin.closewifi();
	}

	
	//获取扫描网络结果，text列表显示，待修改为listview显示
	public void scanwifi() {
		//mWifiAdmin.scanwifi();
		mScanResult = mWifiAdmin.scanwifi();
		scanResult.setText(mScanResult);
	}

	public void connect() {
		mWifiAdmin.connect();
//		startActivityForResult(new Intent(
//				android.provider.Settings.ACTION_WIFI_SETTINGS), 0);
		startActivity(new Intent(android.provider.Settings.ACTION_WIFI_SETTINGS));
	}
	public void disconnect() {
		mWifiAdmin.disconnectWifi();
	}
	public void shakeHand()
	{
		try {
			mPrintWriterClient = new PrintWriter(mSocketClient.getOutputStream(), true);
			op  = mSocketClient.getOutputStream();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		byte[] test = toStringHex("79808182");

			//mPrintWriterClient.write(test[i]);
			try {
				op.write(test);
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		mPrintWriterClient.flush();
	}
	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		switch (v.getId()) {
		case R.id.openwifi:
			openwifi();
			break;
		case R.id.closewifi:
			closewifi();
			break;
		case R.id.scanwifi:
			scanwifi();
			break;
		case R.id.connect:
			connect();
			break;
		case R.id.disconnect:
			disconnect();
			break;
		case R.id.butshake:
			shakeHand();
		default:
			break;
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
	
	 public void initClient(String ip,int port) throws IOException {  
	        // 获得一个Socket通道  
	        SocketChannel channel = SocketChannel.open();  
	        // 设置通道为非阻塞  
	        channel.configureBlocking(false);  
	        // 获得一个通道管理器  
	        this.selector = Selector.open();  
	          
	        // 客户端连接服务器,其实方法执行并没有实现连接，需要在listen（）方法中调  
	        //用channel.finishConnect();才能完成连接  
	        channel.connect(new InetSocketAddress(ip,port));  
	        //将通道管理器和该通道绑定，并为该通道注册SelectionKey.OP_CONNECT事件。  
	        channel.register(selector, SelectionKey.OP_CONNECT);  
	    }  
}