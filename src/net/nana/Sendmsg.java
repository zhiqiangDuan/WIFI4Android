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
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
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
import android.app.ListActivity;
import android.app.ProgressDialog;
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
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;

public class Sendmsg extends Activity implements OnClickListener{
	
	private Button connectButton;	
	private Button sendButton;
	private Button sendData;
	private Button searchData;
	private Button changeData;
	private EditText sendMsg;
	private TextView recvMsg;
	private ListView lv_bleList;
	private ListView lv_test;
	private Context mContext;
	private ProgressDialog progressDialog;
	private boolean isConnecting = false;
	private List<Map<String, Object>> list;  // list ������� �������listview��Ҫ��ʵ������
	private final String STR_SHAKE_HAND ="55020057";
	private final String STR_SEARCH = "5518006D";
	private final String STR_SEARCH2 = "5519006E";
	private final String STR_SEARCH3 = "551A006F";
	private final String[] SERCH_CMDS = {"5518006D","5519006E","551A006F","551B0070"};
	private final String STR_DATA_CHANGE = "5518200000D5560017025807DF0081475E43CD23AF2E6301C834A201C831112000001486";
	private int[][] pages;
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
	private String[] dataString;
	private byte[] dataChangedB = null;
	private String dataChangedS = null;
	private String[] motorArgs = null;
	//Ĭ�ϵ������
	private final int MAXWORKVOLTAGE = 24;
	private final int REWORKVOLTAGE = 24;
	private final int MINWORKVOLTAGE = 24;
	//�������
	private int maxWorkVoltage;	//��������ѹ(V)	Max work voltage(V)	������ֵ��V��	24��120	8	6
	private int reworkVoltage;	//��ѹ�˳���ѹ(V)	OV rework voltage(V)	������ֵ��V��	24��120	8	7
	private int minWorkVoltage;//��С������ѹ(V)	Min work voltage(V)	������ֵ��V��	24��120	8	8
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.sendmsg);
		lv_test = (ListView) findViewById(R.id.test);
		list = new ArrayList<Map<String, Object>>();
		
		
        mContext = this;
        recvMsg = (TextView)findViewById(R.id.showText);
        changeData = (Button)findViewById(R.id.changeData);
        changeData.setOnClickListener(Sendmsg.this);
        searchData = (Button)findViewById(R.id.searchData);
        searchData.setOnClickListener(Sendmsg.this);
        sendData = (Button)findViewById(R.id.sendData);
        sendData.setOnClickListener(Sendmsg.this);
       // lv_bleList = (ListView) findViewById(R.id.lv_bleList);
		String[] strs = new String[] {"first", "second", "third"};
		pages = new int[4][16];

        motorArgs = new String[3];
        main = new Main();
        String sIP = "192.168.4.1";
		String sPort = "8080";
		mSocketClient = main.getSocket();
		selector = main.getSelector();
		//���ӷ�����
		mSocketClient = main.getSocket();
		//ȡ�����롢�����
		try {
			inputStream = mSocketClient.getInputStream();
			op  = mSocketClient.getOutputStream();
			if(mSocketClient == null)
			{
				mSocketClient = new Socket(sIP, 8080);
				System.out.println("Error,Please ensure that  you have opened the rechi controler and connected to the wifi");
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		progressDialog = ProgressDialog.show(Sendmsg.this, "Read the current data",
				"Reading,Please wait!");
		//��activity������ʱ����Ĭ�϶�ȡ����������
		flagShake = false;
		flagOver = false;
		checkFlag = SEARCH_DATA;
		searchThread = new Thread(searchRun);
		searchThread.start();
		statusThread = new Thread(backrun);
		statusThread.start();
	}
	private List<Map<String, Object>> getData() {
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("title", "G1");
		map.put("info1", "google 1");
		map.put("info2", "google 2");
		map.put("info3", "google 3");
		map.put("info4", "google 4");
		list.add(map);

		map = new HashMap<String, Object>();
		map.put("title", "G2");
		map.put("info1", "google 1");
		map.put("info2", "google 2");
		map.put("info3", "google 3");
		//map.put("info4", "google 4");
		list.add(map);

		map = new HashMap<String, Object>();
		map.put("title", "G3");
		map.put("info1", "google 1");
		map.put("info2", "google 2");
		map.put("info3", "google 3");
		//map.put("info4", "google 4");
		list.add(map);
		
		return list;
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
			dataRecv[0] = 0x55;
			dataRecv[1] = 0x28;
			//dataRecv[13] = 0;
			//dataRecv[14] = (byte)(a & 0xff);
			dataToBeSend();
			dataRecv[35] = setCheckSum(dataRecv);
			flagShake = false;
			flagOver = false;
			changeThread = new Thread(changeRun);
			changeThread.start();
			statusThread = new Thread(backrun);
			statusThread.start();
			break;
		case R.id.searchData:
			// start the search thread
			// start the backrun thread
			progressDialog = ProgressDialog.show(Sendmsg.this, "Read the current data",
					"Reading,Please wait!");
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
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		resultData = data.getExtras().getString("str");//�õ���Activity �رպ󷵻ص�����
		dataString = resultData.split(":"); 
		System.out.println(dataString[0]+" "+dataString[1]+" "+dataString[2]);
    }
	/*
	 * ���������������ť�����Ӱ�ť��ȡIP��port�˿ںš� Ȼ���һ��runnable�߳�
	 * ���߳��ȴ���һ��socket Ȼ��ѭ����ȡserver����Ϣ��
	 * ��һ����ť��ֻ����server��������
	 * */
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
	private void dataToBeSend()
	{
		int temp = 0;
		int tempH = 0;
		int tempL = 0;
		if(dataString[0] != null)
		{
			temp = Integer.parseInt(dataString[0]);
			if(temp > 120 || temp < 24)
			{
				temp = MAXWORKVOLTAGE;
			}
		}
		else {
			temp = MAXWORKVOLTAGE;
		}
		tempH = temp >> 8;
		tempL = temp & 0xff;
		dataRecv[13] = (byte)tempH;
		dataRecv[14] = (byte)tempL;
		if(dataString[1] != null)
		{
			temp = Integer.parseInt(dataString[1]);
			if(temp > 120 || temp < 24)
			{
				temp = MAXWORKVOLTAGE;
			}
		}
		else {
			temp = MAXWORKVOLTAGE;
		}
		tempH = temp >> 8;
		tempL = temp & 0xff;
		dataRecv[15] = (byte)tempH;
		dataRecv[16] = (byte)tempL;
		if(dataString[2] != null)
		{
			temp = Integer.parseInt(dataString[2]);
			if(temp > 120 || temp < 24)
			{
				temp = REWORKVOLTAGE;
			}
		}
		else {
			temp = MINWORKVOLTAGE;
		}
		tempH = temp >> 8;
		tempL = temp & 0xff;
		dataRecv[17] = (byte)tempH;
		dataRecv[18] = (byte)tempL;
	}
	private Runnable	searchRun	= new Runnable()
	{
		@Override
		public void run() {
			// TODO Auto-generated method stub
			secrchData();
		}
	};
	private void  secrchData() {
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
				for(int i = 0;i <SERCH_CMDS.length;i++)
				{
					writeData(SERCH_CMDS[i]);  // write the search cmd!
					if((count = inputStream.read(buffer))> 0)
					{
						check = cpoyData(buffer,count);
						if(checkSum(check))
						{
							getDataFromPage(i,check);
							dataRecv = check;
							snedMessage(3+i);
						}
						else {
							snedMessage(0);
						}
					}
				}
				flagOver = true;
				snedMessage(3);
			}
			catch (Exception e)
			{
				//"�����쳣:" + e.getMessage() + "\n";
				Message msg = new Message();
                msg.what = 1;
				mHandler.sendMessage(msg);
			}
	}
	void getDataFromPage(int count,byte[] data)
	{
		int dataLength = 16;
		int tempH;
		int tempL;
		for(int i = 0;i <dataLength;i++)
		{
			tempH = data[3+2*i] &0xff;
			tempL = data[3+2*i+1]&0xff;
			pages[count][i] = (tempH << 6)|tempL;
		}
	}
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
						else
						{
							snedMessage(0);
						}
					}
					op.write(dataRecv);
					if((count = inputStream.read(buffer))> 0)
					{
						check = cpoyData(buffer,count);
						if(checkSum(check))
						{
							flagOver = true;
							snedMessage(3);
						}
						else
						{
							snedMessage(0);
						}
						
					}
				}
				catch (Exception e)
				{
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
                SelectionKey key = (SelectionKey) ite.next();  
                // ɾ����ѡ��key,�Է��ظ�����  
                ite.remove();  
                // �����¼�����  
                if (key.isConnectable())
                {  
                    SocketChannel channel = (SocketChannel)key.channel();  
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
					break;
				  case 2:
					recvMsg.setText("���ֳɹ�!\n");
					break;
				  case 3:
					  setTheFirstPage();
					 break;
				  case 4:
					  setTheSecondPage();
					  break;
				  case 5:
					  setTheThirdPage();
					  break;
				  case 6:
					  setTheForthPage();
					  showListView();
					  progressDialog.dismiss();
					  break;
				  case 100:
					  recvMsg.setText("�����޸ĳɹ���");
					break;
			}
		  }									
	 };
	 private void showListView()
	 {
		 SimpleAdapter adapter = new SimpleAdapter(this,list,R.layout.vlist,
					new String[]{"title","info1","info2","info3","info4"},
					new int[]{R.id.title,R.id.info1,R.id.info2,R.id.info3,R.id.info4});
			lv_test.setAdapter(adapter);
	 }
	 private List<Map<String, Object>> setTheFirstPage()
	 {
		 Map<String, Object> map = new HashMap<String, Object>();
			map.put("title", "�������");
			map.put("info1", "HALL����"+pages[0][0]);
			map.put("info2", "������"+pages[0][1]);
			map.put("info3", "������:"+pages[0][2]);
			map.put("info4", "���ת��:"+pages[0][3]);
			list.add(map);
		 return list;
	 }
	 private List<Map<String, Object>> setTheSecondPage()
	 {
		 Map<String, Object> map = new HashMap<String, Object>();
			map.put("title", "�������");
			map.put("info1", "HALL����"+pages[0][0]);
			map.put("info2", "������"+pages[0][1]);
			map.put("info3", "������:"+pages[0][2]);
			map.put("info4", "���ת��:"+pages[0][3]);
			list.add(map);
		 return list;
	 }
	 private List<Map<String, Object>> setTheThirdPage()
	 {
		 Map<String, Object> map = new HashMap<String, Object>();
			map.put("title", "�������");
			map.put("info1", "HALL����"+pages[0][0]);
			map.put("info2", "������"+pages[0][1]);
			map.put("info3", "������:"+pages[0][2]);
			//map.put("info4", "���ת��:"+pages[0][3]);
			list.add(map);
		 return list;
	 }
	 private List<Map<String, Object>> setTheForthPage()
	 {
		 Map<String, Object> map = new HashMap<String, Object>();
			map.put("title", "�������");
			map.put("info1", "HALL����"+pages[0][0]);
			map.put("info2", "������"+pages[0][1]);
			map.put("info3", "������:"+pages[0][2]);
			map.put("info4", "���ת��:"+pages[0][3]);
			list.add(map);
		 return list;
	 }
	 private void analysisData()
	 {
		String showString = null;
		int tempH = 0; //high 8
		int tempL = 0; // low 8
		int temp = 0;
		tempH = dataRecv[13] & 0xff;
		tempL = dataRecv[14] & 0xff;
		maxWorkVoltage = (tempH << 8) | tempL;
		tempH = dataRecv[15] & 0xff;
		tempL = dataRecv[16] & 0xff;
		reworkVoltage = (tempH << 8) | tempL;
		tempH = dataRecv[17] & 0xff;
		tempL = dataRecv[18] & 0xff;
		minWorkVoltage = (tempH << 8) | tempL;
		showString  = "������������"+temp;
		recvMsg.append(showString);
		motorArgs[0] = "������������"+maxWorkVoltage;
		motorArgs[1] = "��ѹ�˳���ѹ��"+reworkVoltage;
		motorArgs[2] = "��С������ѹ:"+minWorkVoltage;
	 }
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
