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

import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Message;
import android.view.GestureDetector;
import android.view.GestureDetector.OnGestureListener;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

public class Main<DeviceListAdapter> extends Activity implements OnClickListener, OnGestureListener{
	    // 右侧滚动条按钮
		private TextView scanResult;
		private Button serialBut = null;
		private ArrayAdapter<String> wifilistArray;
		//==========================
		
		private DeviceListAdapter mDevListAdapter;
		private String mScanResult;
		private WifiAdmin mWifiAdmin;
	
		private TextView tv_QQ;
		private TextView tv_Number;
		private TextView tv_Taobao;
		private TextView tv_Weixin;
		public ClipboardManager clipboard;
		private GestureDetector gestureScanner;
		 private static Selector selector; //NIO selector
		//=================================
		public static  Socket mSocketClient = null;
@Override
		protected void onResume() {
			// TODO Auto-generated method stub
			super.onResume();
			String sIP = "192.168.4.1";
			String sPort = "8080";
			try 
			{				
				//连接服务器
				mSocketClient = new Socket(sIP, 8080);
				//Intent intent = new Intent();
				//intent.setClass(Main.this, Sendmsg.class);
				//Main.this.startActivity(intent);
			}
			catch (Exception e) 
			{
				new AlertDialog.Builder(Main.this).setTitle("WIFI未连接")//设置对话框标题  
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
		//static BufferedReader mBufferedReaderServer	= null;
		//static PrintWriter mPrintWriterServer = null;
		static BufferedReader mBufferedReaderClient	= null;
		static PrintWriter mPrintWriterClient = null;
		public OutputStream op = null;
	/** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        mWifiAdmin = new WifiAdmin(Main.this);
        serialBut = (Button)findViewById(R.id.serial);
        serialBut.setOnClickListener(Main.this);
        
        tv_QQ = (TextView)findViewById(R.id.textViewQQ2) ;// 复制QQ的内容
		tv_Number = (TextView)findViewById(R.id.textViewNum2) ;// 复制电话的内容
		tv_Weixin = (TextView)findViewById(R.id.textViewWeixin2) ;// 复制微信的内容
		tv_Taobao = (TextView)findViewById(R.id.textViewTaobao2) ;// 复制淘宝的内容
		tv_Number.setOnClickListener(this);
		tv_QQ.setOnClickListener(this);
		tv_Weixin.setOnClickListener(this);
		tv_Taobao.setOnClickListener(this);
		
		gestureScanner = new GestureDetector(this);
    }
    /**
	 * 按钮等控件的初始化
	 */
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

		}    	
    }
	public void onClick(View v) {
		// TODO Auto-generated method stub
		ClipData clip;
		switch (v.getId()) {
		case R.id.textViewQQ2:
			//copy  QQ to clipboard
	         clip = ClipData.newPlainText("text label", tv_QQ.getText().toString());
	         clipboard.setPrimaryClip(clip);
	         Toast.makeText(getApplicationContext(), "QQ已经复制到粘贴板",
	         Toast.LENGTH_SHORT).show();
			break;
		case R.id.textViewTaobao2:
			String url = "http://www.taobao.com";
			Intent i = new Intent(Intent.ACTION_VIEW);
			i.setData(Uri.parse(url));
			startActivity(i);
			break;
		case R.id.textViewWeixin2:
			clip = ClipData.newPlainText("text label", tv_Weixin.getText().toString());
	         clipboard.setPrimaryClip(clip);
	         Toast.makeText(getApplicationContext(), "微信号已经复制到粘贴板",
	         Toast.LENGTH_SHORT).show();
			break;
		case R.id.textViewNum2:
			clip = ClipData.newPlainText("text label", tv_Number.getText().toString());
	         clipboard.setPrimaryClip(clip);
	         Toast.makeText(getApplicationContext(), "电话号已经复制到粘贴板",
	         Toast.LENGTH_SHORT).show();
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
	@Override
	public boolean onDown(MotionEvent arg0) {
		// TODO Auto-generated method stub
		return false;
	}
	@Override
	public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX,
			float velocityY) {
		//不管左右滑动，都是打开Activity3只是让Activity3的出现的方式不一样而已
		if (e1.getX() > e2.getX()) { //向右滑动 
			startActivity(new Intent(this, Sendmsg.class)); 
			overridePendingTransition(R.anim.slide_left, R.anim.close); 
		}
		else if(e1.getX() < e2.getX())// 向左滑动
		{
			startActivity(new Intent(this, Sendmsg.class)); 
			overridePendingTransition(R.anim.slide_right, R.anim.close);
		}
		else
		{
			return false;
		}
		return true;
	}
	//这个onTouchEvent 是必须的。
	public boolean onTouchEvent(MotionEvent event) {
		return gestureScanner.onTouchEvent(event);
	}
	@Override
	public void onLongPress(MotionEvent arg0) {
		// TODO Auto-generated method stub
		
	}
	@Override
	public boolean onScroll(MotionEvent arg0, MotionEvent arg1, float arg2,
			float arg3) {
		// TODO Auto-generated method stub
		return false;
	}
	@Override
	public void onShowPress(MotionEvent arg0) {
		// TODO Auto-generated method stub
		
	}
	@Override
	public boolean onSingleTapUp(MotionEvent arg0) {
		// TODO Auto-generated method stub
		return false;
	}  
}