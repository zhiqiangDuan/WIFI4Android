package net.nana;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.net.Socket;
import java.nio.channels.Selector;
import java.util.ArrayList;
import java.util.List;
import java.util.Iterator;  

import java.nio.ByteBuffer;  
import java.nio.channels.SelectionKey;  
import java.nio.channels.SocketChannel;  

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.AbsListView;
import android.widget.BaseExpandableListAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ExpandableListView;
import android.widget.ExpandableListView.OnChildClickListener;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

public class Sendmsg extends Activity implements OnClickListener{
	
	private Button sendData;
	private Button searchData;
	private Button changeData;
	private TextView recvMsg;
	private Context mContext;
	private ProgressDialog progressDialog;
	private ExpandableListView expandableListView;
	private  List<String> groupArray;  
    private  List<List<String>> childArray;
	private final String STR_SHAKE_HAND ="55020057";
	private final String[] SERCH_CMDS = {"5518006D","5519006E","551A006F","551B0070"};
	private int[][] pages;
	private List<String> tempArray1;
	private List<String> tempArray2;
	private List<String> tempArray3;
	private List<String> tempArray4;
	private List<String> tempArray5;
	private List<String> tempArray6;
	private List<String> tempArray7;
	private List<String> tempArray8;
	private List<String> tempArray9;
	private List<String> tempArray10;
	private List<String> tempArray11;
	private List<String> tempArray12;
	private List<String> tempArray13;
	private List<String> tempArray14;
	private List<String> tempArray15;
	private final int SEARCH_DATA = 1;
	private Thread mThreadClient = null;
	private static Socket mSocketClient = null;
	static BufferedReader mBufferedReaderClient	= null;
	static PrintWriter mPrintWriterClient = null;
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
	private String dataChangedS = null;
	private String[] motorArgs = null;
	//默认电机参数
	private final int MAXWORKVOLTAGE = 24;
	private final int REWORKVOLTAGE = 24;
	private final int MINWORKVOLTAGE = 24;
	//电机参数
	private int maxWorkVoltage;	//最大允许电压(V)	Max work voltage(V)	正整数值（V）	24～120	8	6
	private int reworkVoltage;	//过压退出电压(V)	OV rework voltage(V)	正整数值（V）	24～120	8	7
	private int minWorkVoltage;//最小工作电压(V)	Min work voltage(V)	正整数值（V）	24～120	8	8
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.sendmsg);
		//lv_test = (ListView) findViewById(R.id.test);
		
		
        mContext = this;
        recvMsg = (TextView)findViewById(R.id.showText);
        changeData = (Button)findViewById(R.id.changeData);
        changeData.setOnClickListener(Sendmsg.this);
        searchData = (Button)findViewById(R.id.searchData);
        searchData.setOnClickListener(Sendmsg.this);
        sendData = (Button)findViewById(R.id.sendData);
        sendData.setOnClickListener(Sendmsg.this);
        expandableListView = (ExpandableListView)findViewById(R.id.list);
        expandableListView.setOnChildClickListener(childListener);
       // lv_bleList = (ListView) findViewById(R.id.lv_bleList);
		pages = new int[4][16];
		initList();
		initSocket();
		motorArgs = new String[3];
		progressDialog = ProgressDialog.show(Sendmsg.this, "Read the current data",
				"Reading,Please wait!");
		//该activity启动的时候先默认读取控制器数据
		flagShake = false;
		flagOver = false;
		checkFlag = SEARCH_DATA;
		searchThread = new Thread(searchRun);
		searchThread.start();
		statusThread = new Thread(backrun);
		statusThread.start();
	}
	OnChildClickListener childListener = new OnChildClickListener() {
		@Override
		public boolean onChildClick(ExpandableListView parent, View v,
			int groupPosition, int childPosition, long id) {
				System.out.println(v+"  "+groupPosition+" " +childPosition+ " "+id);
				startCildActivity(groupPosition);
				
				return flagOver;
		}
		};
		private void startCildActivity(int parent)
		{
			Intent intent = new Intent();
			
			switch (parent) {
			case 0:
				intent.setClass(Sendmsg.this, Change1.class);
				startActivity(intent);
				break;
			case 1:
				intent.setClass(Sendmsg.this, Change2.class);
				startActivity(intent);
				break;
			case 2:
				intent.setClass(Sendmsg.this, Change3.class);
				startActivity(intent);
				break;
			case 3:
				intent.setClass(Sendmsg.this, Change4.class);
				startActivity(intent);
				break;
			case 4:
				intent.setClass(Sendmsg.this, Change5.class);
				startActivity(intent);
				break;
			case 5:
				intent.setClass(Sendmsg.this, Change6.class);
				startActivity(intent);
				break;
			case 6:
				intent.setClass(Sendmsg.this, Change7.class);
				startActivity(intent);
				break;
			case 7:
				intent.setClass(Sendmsg.this, Change8.class);
				startActivity(intent);
				break;
			case 8:
				intent.setClass(Sendmsg.this, Change9.class);
				startActivity(intent);
				break;
			case 9:
				intent.setClass(Sendmsg.this, Change10.class);
				startActivity(intent);
				break;
			case 10:
				intent.setClass(Sendmsg.this, Change11.class);
				startActivity(intent);
				break;
			case 11:
				intent.setClass(Sendmsg.this, Change12.class);
				startActivity(intent);
				break;
			case 12:
				intent.setClass(Sendmsg.this, Change13.class);
				startActivity(intent);
				break;
			case 13:
				intent.setClass(Sendmsg.this, Change14.class);
				startActivity(intent);
				break;
			case 14:
				intent.setClass(Sendmsg.this, Change15.class);
				startActivity(intent);
				break;
			default:
				break;
			}
		}
	private void initList()
	{
		groupArray = new ArrayList<String>();
		childArray = new ArrayList<List<String>>();
		tempArray1= new ArrayList<String>();
		tempArray2= new ArrayList<String>();
		tempArray3= new ArrayList<String>();
		tempArray4= new ArrayList<String>();
		tempArray5= new ArrayList<String>();
		tempArray6= new ArrayList<String>();
		tempArray7= new ArrayList<String>();
		tempArray8= new ArrayList<String>();
		tempArray9= new ArrayList<String>();
		tempArray10= new ArrayList<String>();
		tempArray11= new ArrayList<String>();
		tempArray12= new ArrayList<String>();
		tempArray13= new ArrayList<String>();
		tempArray14= new ArrayList<String>();
		tempArray15= new ArrayList<String>();
		groupArray.add("      电机参数");
		groupArray.add("      电压参数");
		groupArray.add("      电流参数");
		groupArray.add("      温度参数");
		groupArray.add("      堵转设置");
		groupArray.add("      转把设置");
		groupArray.add("      速度参数");
		groupArray.add("      产品信息");
		groupArray.add("      EBS功能");
		groupArray.add("      限速功能");
		groupArray.add("      巡航功能");
		groupArray.add("      三速功能");
		groupArray.add("      倒车功能");
		groupArray.add("      弱磁功能");
		groupArray.add("      飙车功能");
		childArray.clear();
	}
	void initSocket()
	{
        String sIP = "192.168.4.1";
		String sPort = "8080";
		main = new Main();
		mSocketClient = main.getSocket();
		selector = main.getSelector();
		//连接服务器
		mSocketClient = main.getSocket();
		//取得输入、输出流
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
	}
	public class ExpandableAdapter extends BaseExpandableListAdapter {

			Activity activity;  
	   	    public  ExpandableAdapter(Activity a)  
	   	    {  
	   	        activity = a;  
	   	    }  
	   	    public  Object getChild(int  groupPosition, int  childPosition)  
	   	    {  
	   	        return  childArray.get(groupPosition).get(childPosition);  
	   	    }  
	   	    public  long  getChildId(int  groupPosition, int  childPosition)  
	   	    {  
	   	        return  childPosition;  
	   	    }  
	   	    public  int  getChildrenCount(int  groupPosition)  
	   	    {  
	   	        return  childArray.get(groupPosition).size();  
	   	    }  
	   	    public  View getChildView(int  groupPosition, int  childPosition,  
	   	            boolean  isLastChild, View convertView, ViewGroup parent)  
	   	    {  
	   	        String string = childArray.get(groupPosition).get(childPosition);  
	   	        return  getGenericView(string);  
	   	    }  
	   	    // group method stub   
	   	    public  Object getGroup(int  groupPosition)  
	   	    {  
	   	        return  groupArray.get(groupPosition);  
	   	    }  
	   	    public  int  getGroupCount()  
	   	    {  
	   	        return  groupArray.size();  
	   	    }  
	   	    public  long  getGroupId(int  groupPosition)  
	   	    {  
	   	        return  groupPosition;  
	   	    }  
	   	    public  View getGroupView(int  groupPosition, boolean  isExpanded,  
	   	            View convertView, ViewGroup parent)  
	   	    {  
	   	        String string = groupArray.get(groupPosition);  
	   	        return  getGenericView(string);  
	   	    }  
	   	    // View stub to create Group/Children 's View   
	   	    public  TextView getGenericView(String string)  
	   	    {  
	   	        // Layout parameters for the ExpandableListView   
	   	        AbsListView.LayoutParams layoutParams = new  AbsListView.LayoutParams(  
	   	                ViewGroup.LayoutParams.FILL_PARENT, 120 );  
	   	        TextView text = new  TextView(activity);  
	   	        text.setLayoutParams(layoutParams);  
	   	        // Center the text vertically   
	   	        text.setGravity(Gravity.CENTER_VERTICAL | Gravity.LEFT);  
	   	        // Set the text starting position   
	   	        text.setPadding(36 , 0 , 0 , 0 );  
	   	        text.setText(string);  
	   	        text.setTextSize(25);
	   	        //text.setTextSize(30, 10.0);
	   	        return  text;  
	   	    }  
	   	    public  boolean  hasStableIds()  
	   	    {  
	   	        return  false ;  
	   	    }  
	   	    public  boolean  isChildSelectable(int  groupPosition, int  childPosition)  
	   	    {  
	   	        return  true ;  
	   	    }  
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
		resultData = data.getExtras().getString("str");//得到新Activity 关闭后返回的数据
		if(resultData != null)
		{
			dataString = resultData.split(":"); 
		}
		
		System.out.println("here!!  "+dataString[0]+" "+dataString[1]+" "+dataString[2]);
    }
	/*
	 * 这个里面有两个按钮，连接按钮获取IP与port端口号。 然后打开一个runnable线程
	 * 此线程先创建一个socket 然后循环读取server的消息。
	 * 另一个按钮，只是向server发送数据
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
		if(!dataString[0].equals("null"))
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
		if(!dataString[1].equals("null"))
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
		if(!dataString[2].equals("null"))
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
				//"接收异常:" + e.getMessage() + "\n";
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
        // 轮询访问selector  
        while (true)
        {  
            selector.select();  
            // 获得selector中选中的项的迭代器  
            Iterator ite = this.selector.selectedKeys().iterator();  
            while (ite.hasNext())
            {  
                SelectionKey key = (SelectionKey) ite.next();  
                // 删除已选的key,以防重复处理  
                ite.remove();  
                // 连接事件发生  
                if (key.isConnectable())
                {  
                    SocketChannel channel = (SocketChannel)key.channel();  
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
					break;
				  case 2:
					recvMsg.setText("握手成功!\n");
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
					    
				      expandableListView.setAdapter(new  ExpandableAdapter(Sendmsg.this )); 
					  progressDialog.dismiss();
					  
					  break;
				  case 100:
					  recvMsg.setText("数据修改成功！");
					break;
			}
		  }									
	 };

	private void setTheFirstPage() {
		tempArray1.add("HALL类型:" + pages[0][0]);
		tempArray1.add("相移量:" + pages[0][1]);
		tempArray1.add("极对数:" + pages[0][2]);
		tempArray1.add("最高转速:" + pages[0][3]);
		tempArray2.add("最大允许电压:"+pages[0][4]);
		tempArray2.add("过压退出电压:"+pages[0][5]);
		tempArray2.add("最小工作电压:"+pages[0][6]);
		tempArray2.add("电池欠压电压:"+pages[0][7]);
		tempArray2.add("电池欠压回差:"+pages[0][8]);
		tempArray2.add("软欠压保护功能:"+pages[0][9]);
		tempArray2.add("软欠压回差:"+pages[0][10]);
		tempArray2.add("软欠压回差:"+pages[0][12]);
		tempArray3.add("电机启动电流:"+pages[0][13]);
		tempArray3.add("最大相电流:"+pages[0][14]);
		tempArray3.add("最大电流保持时间:"+pages[0][15]);
		tempArray3.add("运行电流:"+pages[0][15]);
		tempArray3.add("平均电流:"+pages[0][15]);
	}
	private void setTheSecondPage() {

		tempArray4.add("温控电流比例1:" + pages[1][0]);
		tempArray4.add("温控电流比例2:" + pages[1][1]);
		tempArray4.add("过温保护温度:" + pages[1][2]);
		tempArray4.add("过温控流温度1:" + pages[1][3]);
		tempArray4.add("过温控流温度2:"+pages[1][4]);
		tempArray4.add("温度保护回差:"+pages[1][5]);
		tempArray5.add("堵转保护使能:"+pages[1][6]);
		tempArray5.add("堵转保护时间:"+pages[1][7]);
		tempArray6.add("转把模式选择1:"+pages[1][8]);
		tempArray6.add("转把模式选择2:"+pages[1][9]);
		tempArray6.add("转把上限故障值:"+pages[1][10]);
		tempArray6.add("转把上限故障退出值:"+pages[1][12]);
		tempArray6.add("转把下限故障值:"+pages[1][13]);
		tempArray6.add("转把下限故障退出值:"+pages[1][14]);
		tempArray6.add("转把最高有效值:"+pages[1][15]);
		tempArray6.add("转把最低有效值:"+pages[1][15]);
	}
	private void setTheThirdPage() {

		tempArray7.add("加速度:" + pages[1][0]);
		tempArray7.add("減速度:" + pages[1][1]);
		tempArray8.add("产品代码:" + pages[1][2]);
		tempArray8.add("产品名称:" + pages[1][3]);
		tempArray8.add("产品版本:"+pages[1][4]);
		tempArray8.add("保修期(年):"+pages[1][5]);
		tempArray8.add("保修期(月日):"+pages[1][6]);
		tempArray9.add("EBS功能使能:"+pages[1][7]);
		tempArray9.add("EBS反充电压:"+pages[1][8]);
		tempArray9.add("EBS反充电流:"+pages[1][9]);
		tempArray10.add("限速功能使能:"+pages[1][12]);
		tempArray10.add("限速比例(%):"+pages[1][10]);
		tempArray11.add("巡航功能使能:"+pages[1][13]);
		tempArray11.add("巡航手动模式使能:"+pages[1][14]);
		tempArray11.add("巡航自动模式使能:"+pages[1][15]);
		tempArray11.add("巡航进入转把值:"+pages[1][15]);
		tempArray11.add("巡航进入速度比例(%):"+pages[1][15]);
		tempArray11.add("自动巡航响应时间(s):"+pages[1][15]);
	}
	private void setTheForthPage() {

		tempArray12.add("三速功能使能:" + pages[1][0]);
		tempArray12.add("开关模式:" + pages[1][1]);
		tempArray12.add("高档位(%):" + pages[1][2]);
		tempArray12.add("中档位(%):" + pages[1][3]);
		tempArray12.add("低档位(%):"+pages[1][4]);
		tempArray13.add("倒车工能使能:"+pages[1][5]);
		tempArray13.add("倒车速度比例:"+pages[1][6]);
		tempArray13.add("倒车电流比例:"+pages[1][7]);
		tempArray14.add("弱磁功能使能:"+pages[1][8]);
		tempArray14.add("弱磁进入速度比例:"+pages[1][9]);
		tempArray14.add("弱磁退出速度比例:"+pages[1][12]);
		tempArray14.add("弱磁最大电流(A):"+pages[1][10]);
		tempArray14.add("弱磁进入转把值:"+pages[1][13]);
		tempArray15.add("飙车功能使能:"+pages[1][14]);
		tempArray15.add("飙车模式:"+pages[1][15]);
		tempArray15.add("飙车电流(A):"+pages[1][15]);
		tempArray15.add("飙车保持时间(S):"+pages[1][15]);
		tempArray15.add("飙车间隔时间(s):"+pages[1][15]);
		childArray.add(tempArray1); 
		childArray.add(tempArray2);
		childArray.add(tempArray3);
		childArray.add(tempArray4);
		childArray.add(tempArray5);
		childArray.add(tempArray6);
		childArray.add(tempArray7);
		childArray.add(tempArray8);
		childArray.add(tempArray9); 
		childArray.add(tempArray10);
		childArray.add(tempArray11);
		childArray.add(tempArray12);
		childArray.add(tempArray13);
		childArray.add(tempArray14);
		childArray.add(tempArray15);
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
