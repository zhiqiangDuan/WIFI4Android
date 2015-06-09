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
	    // �Ҳ��������ť
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
				//���ӷ�����
				mSocketClient = new Socket(sIP, 8080);
				//Intent intent = new Intent();
				//intent.setClass(Main.this, Sendmsg.class);
				//Main.this.startActivity(intent);
			}
			catch (Exception e) 
			{
				new AlertDialog.Builder(Main.this).setTitle("WIFIδ����")//���öԻ������  
			     .setMessage("��λ��WIFI���ߣ���������������������")//������ʾ������  
			     .setPositiveButton("ȷ��",new DialogInterface.OnClickListener() {//���ȷ����ť  
			         @Override  
			         public void onClick(DialogInterface dialog, int which) {//ȷ����ť����Ӧ�¼�  
				         // TODO Auto-generated method stub  
			             //finish();  
			         } 
			     }).show();//�ڰ�����Ӧ�¼�����ʾ�˶Ի���  
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
        
        tv_QQ = (TextView)findViewById(R.id.textViewQQ2) ;// ����QQ������
		tv_Number = (TextView)findViewById(R.id.textViewNum2) ;// ���Ƶ绰������
		tv_Weixin = (TextView)findViewById(R.id.textViewWeixin2) ;// ����΢�ŵ�����
		tv_Taobao = (TextView)findViewById(R.id.textViewTaobao2) ;// �����Ա�������
		tv_Number.setOnClickListener(this);
		tv_QQ.setOnClickListener(this);
		tv_Weixin.setOnClickListener(this);
		tv_Taobao.setOnClickListener(this);
		
		gestureScanner = new GestureDetector(this);
    }
    /**
	 * ��ť�ȿؼ��ĳ�ʼ��
	 */
	public Socket getSocket()
	{
		return this.mSocketClient;
	}
	public Selector getSelector()
	{
		return this.selector;
		
	}
	//�������ڲ���������send��ť�ϣ�����ʱ��ת��Sendmsg����
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
	         Toast.makeText(getApplicationContext(), "QQ�Ѿ����Ƶ�ճ����",
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
	         Toast.makeText(getApplicationContext(), "΢�ź��Ѿ����Ƶ�ճ����",
	         Toast.LENGTH_SHORT).show();
			break;
		case R.id.textViewNum2:
			clip = ClipData.newPlainText("text label", tv_Number.getText().toString());
	         clipboard.setPrimaryClip(clip);
	         Toast.makeText(getApplicationContext(), "�绰���Ѿ����Ƶ�ճ����",
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
	        // ���һ��Socketͨ��  
	        SocketChannel channel = SocketChannel.open();  
	        // ����ͨ��Ϊ������  
	        channel.configureBlocking(false);  
	        // ���һ��ͨ��������  
	        this.selector = Selector.open();  
	          
	        // �ͻ������ӷ�����,��ʵ����ִ�в�û��ʵ�����ӣ���Ҫ��listen���������е�  
	        //��channel.finishConnect();�����������  
	        channel.connect(new InetSocketAddress(ip,port));  
	        //��ͨ���������͸�ͨ���󶨣���Ϊ��ͨ��ע��SelectionKey.OP_CONNECT�¼���  
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
		//�������һ��������Ǵ�Activity3ֻ����Activity3�ĳ��ֵķ�ʽ��һ������
		if (e1.getX() > e2.getX()) { //���һ��� 
			startActivity(new Intent(this, Sendmsg.class)); 
			overridePendingTransition(R.anim.slide_left, R.anim.close); 
		}
		else if(e1.getX() < e2.getX())// ���󻬶�
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
	//���onTouchEvent �Ǳ���ġ�
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