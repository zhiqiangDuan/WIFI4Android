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
import java.util.zip.Checksum;

import java.nio.ByteBuffer;  
import java.nio.channels.SelectionKey;  
import java.nio.channels.SocketChannel;  

import android.R.integer;
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
	private final int ACTIVITY1 = 1;
	private final int ACTIVITY2 = 2;
	private final int ACTIVITY3 = 3;
	private final int ACTIVITY4 = 4;
	private final int ACTIVITY5 = 5;
	private final int ACTIVITY6 = 6;
	private final int ACTIVITY7 = 7;
	private final int ACTIVITY8 = 8;
	private final int ACTIVITY9 = 9;
	private final int ACTIVITY10 = 10;
	private final int ACTIVITY11 = 11;
	private final int ACTIVITY12 = 12;
	private final int ACTIVITY13 = 13;
	private final int ACTIVITY14 = 14;
	private final int ACTIVITY15 = 15;
	private int[][] pages;
	private byte[][] controlerData;
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
		controlerData = new byte[4][16];
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
				startActivityForResult(intent, ACTIVITY1);
				break;
			case 1:
				intent.setClass(Sendmsg.this, Change2.class);
				startActivityForResult(intent, ACTIVITY2);
				break;
			case 2:
				intent.setClass(Sendmsg.this, Change3.class);
				startActivityForResult(intent, ACTIVITY3);
				break;
			case 3:
				intent.setClass(Sendmsg.this, Change4.class);
				startActivityForResult(intent, ACTIVITY4);
				break;
			case 4:
				intent.setClass(Sendmsg.this, Change5.class);
				startActivityForResult(intent, ACTIVITY5);
				break;
			case 5:
				intent.setClass(Sendmsg.this, Change6.class);
				startActivityForResult(intent, ACTIVITY6);
				break;
			case 6:
				intent.setClass(Sendmsg.this, Change7.class);
				startActivityForResult(intent, ACTIVITY7);
				break;
			case 7:
				Toast.makeText(getApplicationContext(), "该数据不可修改",
					     Toast.LENGTH_SHORT).show();
				intent.setClass(Sendmsg.this, Change8.class);
				startActivityForResult(intent, ACTIVITY8);
				break;
			case 8:
				intent.setClass(Sendmsg.this, Change9.class);
				startActivityForResult(intent, ACTIVITY9);
				break;
			case 9:
				intent.setClass(Sendmsg.this, Change10.class);
				startActivityForResult(intent, ACTIVITY10);
				break;
			case 10:
				intent.setClass(Sendmsg.this, Change11.class);
				startActivityForResult(intent, ACTIVITY11);
				break;
			case 11:
				intent.setClass(Sendmsg.this, Change12.class);
				startActivityForResult(intent, ACTIVITY12);
				break;
			case 12:
				intent.setClass(Sendmsg.this, Change13.class);
				startActivityForResult(intent, ACTIVITY13);
				break;
			case 13:
				intent.setClass(Sendmsg.this, Change14.class);
				startActivityForResult(intent, ACTIVITY14);
				break;
			case 14:
				intent.setClass(Sendmsg.this, Change15.class);
				startActivityForResult(intent, ACTIVITY15);
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
			setChecksum();
			flagShake = false;
			flagOver = false;
			changeThread = new Thread(changeRun);
			changeThread.start();
			statusThread = new Thread(backrun);
			statusThread.start();
			break;
		case R.id.searchData:
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
			break;
		default:
			break;
		}
	} 

	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
	}
	protected void onActivityResult(int requestCode, int id, Intent data) {
		switch (id) {
		case ACTIVITY1:
			activityData1(data);
			break;
		case ACTIVITY2:
			activityData2(data);
			break;
		case ACTIVITY3:
			activityData3(data);
			break;
		case ACTIVITY4:
			activityData4(data);
			break;
		case ACTIVITY5:
			activityData5(data);
			break;
		case ACTIVITY6:
			activityData6(data);
			break;
		case ACTIVITY7:
			activityData7(data);
			break;
		case ACTIVITY9:
			activityData9(data);
			break;
		case ACTIVITY10:
			activityData10(data);
			break;
		case ACTIVITY11:
			activityData11(data);
			break;
		case ACTIVITY12:
			activityData12(data);
			break;
		case ACTIVITY13:
			activityData13(data);
			break;
		case ACTIVITY14:
			activityData14(data);
			break;
		case ACTIVITY15:
			activityData15(data);
			break;
		default:
			break;
		}
    }
	private void activityData1(Intent data)
	{
		resultData = data.getExtras().getString("str1");//得到新Activity关闭后返回的数据
		if(resultData != null)
		{
			dataString = resultData.split(":"); 
		}
		// HALL类型
		if(dataString[0].equals("0")) //===========这里需要修改============
		{
			controlerData[0][4] = 60;
		}
		else {
			controlerData[0][4] = 120;
		}
		if(!dataString[1].equals("null"))
		{
			int temp = Integer.parseInt(dataString[1]);
			if(temp >= -180 && temp <= 180)
			{
				controlerData[0][5] = (byte)((temp >> 8) & 0xff);
				controlerData[0][6] = (byte)(temp & 0xff);
			}
		}
		if(!dataString[2].equals("null"))
		{
			int temp = Integer.parseInt(dataString[2]);
			if(temp >= 2 && temp <= 28)
			{
				controlerData[0][8] = (byte)(temp & 0xff);
			}
		}
		if(!dataString[3].equals("null"))
		{
			int temp = Integer.parseInt(dataString[3]);
			if(temp >= 100 && temp <= 10000)
			{
				controlerData[0][9] = (byte)((temp >> 8) & 0xff);
				controlerData[0][10] = (byte)(temp & 0xff);
			}
		}
	}
	private void activityData2(Intent data)
	{
		resultData = data.getExtras().getString("str2");//得到新Activity关闭后返回的数据
		if(resultData != null)
		{
			dataString = resultData.split(":"); 
		}
		// HALL类型
		if(!dataString[0].equals("null"))
		{
			int temp = Integer.parseInt(dataString[0]);
			if(temp >= 24 && temp <= 120)
			{
				controlerData[0][15] = (byte)((temp >> 8) & 0xff); //H
				controlerData[0][16] = (byte)(temp & 0xff);//L 
			}
		}
		if(!dataString[1].equals("null"))
		{
			int temp = Integer.parseInt(dataString[1]);
			if(temp >= 24 && temp <= 120)
			{
				controlerData[0][17] = (byte)((temp >> 8) & 0xff); //H
				controlerData[0][18] = (byte)(temp & 0xff);//L 
			}
		}
		if(!dataString[2].equals("null"))
		{
			int temp = Integer.parseInt(dataString[2]);
			if(temp >= 24 && temp <= 120)
			{
				controlerData[0][19] = (byte)((temp >> 8) & 0xff); //H
				controlerData[0][20] = (byte)(temp & 0xff);//L 
			}
		}
		if(!dataString[3].equals("null"))
		{
			int temp = Integer.parseInt(dataString[3]);
			if(temp >= 24 && temp <= 120)
			{
				controlerData[0][21] = (byte)((temp >> 8) & 0xff); //H
				controlerData[0][22] = (byte)(temp & 0xff);//L 
			}
		}
		if(!dataString[4].equals("null"))
		{
			int temp = Integer.parseInt(dataString[4]);
			if(temp >= 1 && temp <= 3)
			{
				controlerData[0][23] = (byte)((temp >> 8) & 0xff); //H
				controlerData[0][24] = (byte)(temp & 0xff);//L 
			}
		}
		if(dataString[5].equals("0")) //===========这里需要修改============
		{
			controlerData[0][26] = 0;
		}
		else {
			controlerData[0][26] = 1;
		}
		if(!dataString[6].equals("null"))
		{
			int temp = Integer.parseInt(dataString[6]);
			if(temp >= 1 && temp <= 3)
			{
				controlerData[0][27] = (byte)((temp >> 8) & 0xff); //H
				controlerData[0][28] = (byte)(temp & 0xff);//L 
			}
		}
		if(!dataString[7].equals("null"))
		{
			int temp = Integer.parseInt(dataString[7]);
			if(temp >= 24 && temp <= 120)
			{
				controlerData[0][29] = (byte)((temp >> 8) & 0xff); //H
				controlerData[0][30] = (byte)(temp & 0xff);//L 
			}
		}
	}
	private void activityData3(Intent data)
	{
		resultData = data.getExtras().getString("str3");//得到新Activity关闭后返回的数据
		if(resultData != null)
		{
			dataString = resultData.split(":"); 
		}
		if(!dataString[0].equals("null"))
		{
			int temp = Integer.parseInt(dataString[0]);
			if(temp >= 100 && temp <= 500)
			{
				controlerData[1][3] = (byte)((temp >> 8) & 0xff);
				controlerData[1][4] = (byte)(temp & 0xff);
			}
		}
		if(!dataString[1].equals("null"))
		{
			int temp = Integer.parseInt(dataString[1]);
			if(temp >= 100 && temp <= 500)
			{
				controlerData[1][7] = (byte)((temp >> 8) & 0xff);
				controlerData[1][8] = (byte)(temp & 0xff);
			}
		}
		if(!dataString[2].equals("null"))
		{
			int temp = Integer.parseInt(dataString[2]);
			if(temp >= 2 && temp <= 10)
			{
				controlerData[1][9] = (byte)((temp >> 8) & 0xff);
				controlerData[1][10] = (byte)(temp & 0xff);
			}
		}
		if(!dataString[3].equals("null"))
		{
			int temp = Integer.parseInt(dataString[3]);
			if(temp >= 100 && temp <= 500)
			{
				controlerData[1][11] = (byte)((temp >> 8) & 0xff);
				controlerData[1][12] = (byte)(temp & 0xff);
			}
		}
		if(!dataString[4].equals("null"))
		{
			int temp = Integer.parseInt(dataString[4]);
			if(temp >= 100 && temp <= 200)
			{
				controlerData[1][13] = (byte)((temp >> 8) & 0xff);
				controlerData[1][14] = (byte)(temp & 0xff);
			}
		}
	}
	private void activityData4(Intent data)
	{
		resultData = data.getExtras().getString("str4");//得到新Activity关闭后返回的数据
		if(resultData != null)
		{
			dataString = resultData.split(":"); 
		}
		// HALL类型
		if(!dataString[0].equals("null"))
		{
			int temp = Integer.parseInt(dataString[0]);
			if(temp >= 10 && temp <= 80)
			{
				controlerData[1][15] = (byte)((temp >> 8) & 0xff);
				controlerData[1][16] = (byte)(temp & 0xff);
			}
		}
		if(!dataString[1].equals("null"))
		{
			int temp = Integer.parseInt(dataString[1]);
			if(temp >= 10 && temp <= 80)
			{
				controlerData[1][17] = (byte)((temp >> 8) & 0xff);
				controlerData[1][18] = (byte)(temp & 0xff);
			}
		}
		if(!dataString[2].equals("null"))
		{
			int temp = Integer.parseInt(dataString[2]);
			if(temp >= 10 && temp <= 120)
			{
				controlerData[1][19] = (byte)((temp >> 8) & 0xff);
				controlerData[1][20] = (byte)(temp & 0xff);
			}
		}
		if(!dataString[3].equals("null"))
		{
			int temp = Integer.parseInt(dataString[3]);
			if(temp >= 10 && temp <= 120)
			{
				controlerData[1][21] = (byte)((temp >> 8) & 0xff);
				controlerData[1][22] = (byte)(temp & 0xff);
			}
		}
		if(!dataString[4].equals("null"))
		{
			int temp = Integer.parseInt(dataString[4]);
			if(temp >= 10 && temp <= 120)
			{
				controlerData[1][23] = (byte)((temp >> 8) & 0xff);
				controlerData[1][24] = (byte)(temp & 0xff);
			}
		}
		if(!dataString[5].equals("null"))
		{
			int temp = Integer.parseInt(dataString[5]);
			if(temp >= 2 && temp <= 10)
			{
				controlerData[1][25] = (byte)((temp >> 8) & 0xff);
				controlerData[1][26] = (byte)(temp & 0xff);
			}
		}
	}
	private void activityData5(Intent data)
	{
		resultData = data.getExtras().getString("str5");//得到新Activity关闭后返回的数据
		if(resultData != null)
		{
			dataString = resultData.split(":"); 
		}
		// HALL类型
		if(dataString[0].equals("0")) 
		{
			controlerData[0][32] = 0;
		}
		else {
			controlerData[0][32] = 1;
		}
		if(!dataString[1].equals("null"))
		{
			int temp = Integer.parseInt(dataString[1]);
			if(temp >= 1 && temp <= 4)
			{
				controlerData[0][33] = (byte)((temp >> 8) & 0xff);
				controlerData[0][34] = (byte)(temp & 0xff);
			}
		}
	}
	//===========暂未修改===============
	private void activityData6(Intent data)
	{
		resultData = data.getExtras().getString("str6");//得到新Activity关闭后返回的数据
		if(resultData != null)
		{
			dataString = resultData.split(":"); 
		}
		// HALL类型
		if(dataString[0].equals("0")) 
		{
			controlerData[0][4] = 0;
		}
		else {
			controlerData[0][4] = 1;
		}
		
		if(dataString[1].equals("0")) 
		{
			controlerData[0][4] = 60;
		}
		else {
			controlerData[0][4] = 120;
		}
		
		if(!dataString[1].equals("null"))
		{
			int temp = Integer.parseInt(dataString[1]);
			if(temp >= -180 && temp <= 180)
			{
				controlerData[0][5] = (byte)((temp >> 8) & 0xff);
				controlerData[0][6] = (byte)(temp & 0xff);
			}
		}
	}
	
	private void activityData7(Intent data)
	{
		resultData = data.getExtras().getString("str7");//得到新Activity关闭后返回的数据
		if(resultData != null)
		{
			dataString = resultData.split(":"); 
		}

		if(!dataString[0].equals("null"))
		{
			int temp = Integer.parseInt(dataString[0]);
			if(temp >= 50 && temp <= 200)
			{
				controlerData[2][31] = (byte)((temp >> 8) & 0xff);
				controlerData[2][32] = (byte)(temp & 0xff);
			}
		}
		if(!dataString[1].equals("null"))
		{
			int temp = Integer.parseInt(dataString[1]);
			if(temp >= 100 && temp <= 1200)
			{
				controlerData[2][33] = (byte)((temp >> 8) & 0xff);
				controlerData[2][34] = (byte)(temp & 0xff);
			}
		}
		
	}
	//=====================================
	private void activityData8(Intent data)
	{
		resultData = data.getExtras().getString("str8");//得到新Activity关闭后返回的数据
		if(resultData != null)
		{
			dataString = resultData.split(":"); 
		}

		if(!dataString[0].equals("null"))
		{
			int temp = Integer.parseInt(dataString[0]);
			if(temp >= 0 && temp <= 65535)
			{
				controlerData[0][5] = (byte)((temp >> 8) & 0xff);
				controlerData[0][6] = (byte)(temp & 0xff);
			}
		}
		if(!dataString[0].equals("null"))
		{
			int temp = Integer.parseInt(dataString[0]);
			if(temp >= 0 && temp <= 65535)
			{
				controlerData[0][5] = (byte)((temp >> 8) & 0xff);
				controlerData[0][6] = (byte)(temp & 0xff);
			}
		}if(!dataString[0].equals("null"))
		{
			int temp = Integer.parseInt(dataString[0]);
			if(temp >= 0 && temp <= 65535)
			{
				controlerData[0][5] = (byte)((temp >> 8) & 0xff);
				controlerData[0][6] = (byte)(temp & 0xff);
			}
		}
		if(!dataString[0].equals("null"))
		{
			int temp = Integer.parseInt(dataString[0]);
			if(temp >= 0 && temp <= 65535)
			{
				controlerData[0][5] = (byte)((temp >> 8) & 0xff);
				controlerData[0][6] = (byte)(temp & 0xff);
			}
		}
		if(!dataString[0].equals("null"))
		{
			int temp = Integer.parseInt(dataString[0]);
			if(temp >= 0 && temp <= 65535)
			{
				controlerData[0][5] = (byte)((temp >> 8) & 0xff);
				controlerData[0][6] = (byte)(temp & 0xff);
			}
		}
	}
	
	private void activityData9(Intent data)
	{
		resultData = data.getExtras().getString("str9");//得到新Activity关闭后返回的数据
		if(resultData != null)
		{
			dataString = resultData.split(":"); 
		}
		if(dataString[0].equals("0"))
		{
			controlerData[3][4] = 0;
		}
		else {
			controlerData[3][4] = 1;
		}
		if(!dataString[1].equals("null"))
		{
			int temp = Integer.parseInt(dataString[1]);
			if(temp >= 30 && temp <= 120)
			{
				controlerData[3][5] = (byte)((temp >> 8) & 0xff);
				controlerData[3][6] = (byte)(temp & 0xff);
			}
		}
		if(!dataString[2].equals("null"))
		{
			int temp = Integer.parseInt(dataString[2]);
			if(temp >= 5 && temp <= 20)
			{
				controlerData[3][7] = (byte)((temp >> 8) & 0xff);
				controlerData[3][8] = (byte)(temp & 0xff);
			}
		}
	}
	private void activityData10(Intent data)
	{
		resultData = data.getExtras().getString("str1");//得到新Activity关闭后返回的数据
		if(resultData != null)
		{
			dataString = resultData.split(":"); 
		}
		// HALL类型
		if(dataString[0].equals("0")) //===========这里需要修改============
		{
			controlerData[0][4] = 60;
		}
		else {
			controlerData[0][4] = 120;
		}
		if(!dataString[1].equals("null"))
		{
			int temp = Integer.parseInt(dataString[1]);
			if(temp >= 10 && temp <= 100)
			{
				controlerData[3][17] = (byte)((temp >> 8) & 0xff);
				controlerData[3][18] = (byte)(temp & 0xff);
			}
		}
	}
	
	private void activityData11(Intent data)
	{
		resultData = data.getExtras().getString("str11");//得到新Activity关闭后返回的数据
		if(resultData != null)
		{
			dataString = resultData.split(":"); 
		}
		// HALL类型
		if(dataString[0].equals("0")) //===========这里需要修改============
		{
			controlerData[0][4] = 60;
		}
		else {
			controlerData[0][4] = 120;
		}
		if(dataString[1].equals("0")) //===========这里需要修改============
		{
			controlerData[0][4] = 60;
		}
		else {
			controlerData[0][4] = 120;
		}
		if(dataString[2].equals("0")) //===========这里需要修改============
		{
			controlerData[0][4] = 60;
		}
		else {
			controlerData[0][4] = 120;
		}
		if(!dataString[3].equals("null"))
		{
			float temp = Float.parseFloat(dataString[3]);;
			if(temp >= 2.0 && temp <= 4.3)// 实数 不是这样转换的
			{
				//转换成16进制
				controlerData[3][23] = (byte)(((int)temp >> 8) & 0xff);
				controlerData[3][24] = (byte)((int)temp & 0xff);
			}
		}
		if(!dataString[4].equals("null"))
		{
			int temp = Integer.parseInt(dataString[4]);
			if(temp >= 20 && temp <= 100)
			{
				controlerData[3][25] = (byte)((temp >> 8) & 0xff);
				controlerData[3][26] = (byte)(temp & 0xff);
			}
		}
		if(!dataString[5].equals("null"))
		{
			int temp = Integer.parseInt(dataString[5]);
			if(temp >= 4 && temp <= 10)
			{
				controlerData[3][28] = (byte)(temp & 0xff);
			}
		}
		
	}
	private void activityData12(Intent data)
	{
		resultData = data.getExtras().getString("str12");//得到新Activity关闭后返回的数据
		if(resultData != null)
		{
			dataString = resultData.split(":"); 
		}
		// HALL类型
		if(dataString[0].equals("0")) //===========这里需要修改============
		{
			controlerData[0][4] = 60;
		}
		else {
			controlerData[0][4] = 120;
		}
		if(dataString[1].equals("0")) //===========这里需要修改============
		{
			controlerData[0][4] = 60;
		}
		else {
			controlerData[0][4] = 120;
		}
		if(!dataString[2].equals("null"))
		{
			int temp = Integer.parseInt(dataString[2]);
			if(temp >= 10 && temp <= 100)
			{
				controlerData[3][29] = (byte)((temp >> 8) & 0xff);
				controlerData[3][30] = (byte)(temp & 0xff);
			}
		}
		if(!dataString[3].equals("null"))
		{
			int temp = Integer.parseInt(dataString[3]);
			if(temp >= 10 && temp <= 100)
			{
				controlerData[3][31] = (byte)((temp >> 8) & 0xff);
				controlerData[3][32] = (byte)(temp & 0xff);
			}
		}
		if(!dataString[4].equals("null"))
		{
			int temp = Integer.parseInt(dataString[4]);
			if(temp >= 10 && temp <= 100)
			{
				controlerData[3][33] = (byte)((temp >> 8) & 0xff);
				controlerData[3][34] = (byte)(temp & 0xff);
			}	 
		}
	}
	
	private void activityData13(Intent data)
	{
		resultData = data.getExtras().getString("str1");//得到新Activity关闭后返回的数据
		if(resultData != null)
		{
			dataString = resultData.split(":"); 
		}
		// HALL类型
		if(dataString[0].equals("0")) //===========这里需要修改============
		{
			controlerData[0][4] = 60;
		}
		else {
			controlerData[0][4] = 120;
		}
		if(!dataString[1].equals("null"))
		{
			int temp = Integer.parseInt(dataString[1]);
			if(temp >= 10 && temp <= 100)
			{
				controlerData[4][3] = (byte)((temp >> 8) & 0xff);
				controlerData[4][4] = (byte)(temp & 0xff);
			}
		}
		if(!dataString[2].equals("null"))
		{
			int temp = Integer.parseInt(dataString[2]);
			if(temp >= 10 && temp <= 100)
			{
				controlerData[4][5] = (byte)((temp >> 8) & 0xff);
				controlerData[4][6] = (byte)(temp & 0xff);
			}
		}
	}
	private void activityData14(Intent data)
	{
		resultData = data.getExtras().getString("str14");//得到新Activity关闭后返回的数据
		if(resultData != null)
		{
			dataString = resultData.split(":"); 
		}
		// HALL类型
		if(dataString[0].equals("0")) //===========这里需要修改============
		{
			controlerData[0][4] = 60;
		}
		else {
			controlerData[0][4] = 120;
		}
		if(!dataString[1].equals("null"))
		{
			int temp = Integer.parseInt(dataString[1]);
			if(temp >= 10 && temp <= 100)
			{
				controlerData[4][7] = (byte)((temp >> 8) & 0xff);
				controlerData[4][8] = (byte)(temp & 0xff);
			}
		}
		if(!dataString[2].equals("null"))
		{
			int temp = Integer.parseInt(dataString[2]);
			if(temp >= 10 && temp <= 100)
			{
				controlerData[4][9] = (byte)((temp >> 8) & 0xff);
				controlerData[4][10] = (byte)(temp & 0xff);
			}
		}
		if(!dataString[3].equals("null"))
		{
			int temp = Integer.parseInt(dataString[3]);
			if(temp >= 1 && temp <= 100)
			{
				controlerData[4][10] = (byte)((temp >> 8) & 0xff);
				controlerData[4][11] = (byte)(temp & 0xff);
			}
		}
		if(!dataString[4].equals("null"))//  float
		{
			float temp = Float.parseFloat(dataString[4]);
			int tempInt = (int)((temp*33/48)*(65536/3.3));
			if(temp >= 2.0 && temp <= 4.3)
			{
				controlerData[4][10] = (byte)((tempInt >> 8) & 0xff);
				controlerData[4][11] = (byte)(tempInt & 0xff);
			}
		}
	}
	private void activityData15(Intent data)
	{
		resultData = data.getExtras().getString("str15");//得到新Activity关闭后返回的数据
		if(resultData != null)
		{
			dataString = resultData.split(":"); 
		}
		if(dataString[0].equals("0")) //===========这里需要修改============
		{
			controlerData[4][20] = 60;
		}
		else {
			controlerData[4][20] = 120;
		}
		if(dataString[1].equals("0")) //===========这里需要修改============
		{
			controlerData[4][22] = 60;
		}
		else {
			controlerData[4][22] = 120;
		}
		if(!dataString[2].equals("null"))
		{
			int temp = Integer.parseInt(dataString[2]);
			if(temp >= 10 && temp <= 500)
			{
				controlerData[4][23] = (byte)((temp >> 8) & 0xff);
				controlerData[4][24] = (byte)(temp & 0xff);
			}
		}
		if(!dataString[3].equals("null"))
		{
			int temp = Integer.parseInt(dataString[3]);
			if(temp >= 1 && temp <= 60)
			{
				controlerData[4][25] = (byte)((temp >> 8) & 0xff);
				controlerData[4][26] = (byte)(temp & 0xff);
			}
		}
		if(!dataString[4].equals("null"))
		{
			int temp = Integer.parseInt(dataString[4]);
			if(temp >= 120 && temp <= 600)
			{
				controlerData[4][27] = (byte)((temp >> 8) & 0xff);
				controlerData[4][28] = (byte)(temp & 0xff);
			}
		}
	}
	private Runnable	changeRun	= new Runnable()
	{
		int count = 0;
		byte[] buffer = new byte[256];
		byte[] check;
		@Override
		public void run() 
		{
			// TODO Auto-generated method stub
			flagShake = true;
			setChecksum();
			try {
				for(int i = 0;i < 4;i++)
				{
					op.write(controlerData[i]);
					if((count = inputStream.read(buffer))> 0)
					{
						check = copyData(buffer,count);
						if(!checkSum(check)) // 其实这里还需要检验返回状态。到时候记得要处理
						{
							snedMessage(0);
						}
					}
				}
			} 
			catch (IOException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
			dataRecv = check;
			flagOver = true;
			snedMessage(5);
		}
	};
	private void setChecksum()
	{
		int sum = 0;
		int temp = 0;
		for(int i = 0;i < 4;i++)
		{
			controlerData[i][1] = (byte)(0x28 + i);
			for(int j = 0;j < controlerData[i].length -1 ;j++)
			{
				temp = 0xff & controlerData[i][j];
				sum+=temp;
			}
			controlerData[i][35] =(byte) (sum & 0xff);
		}
	}
	
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
					check = copyData(buffer,count);
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
						check = copyData(buffer,count);
						controlerData[i] = copyData(buffer,count);
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
			}
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
						check = copyData(buffer,count);
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
						check = copyData(buffer,count);
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
	private boolean readData(byte[] buffer)
	{
		int count = 0;
		byte[] check = null;
		try {
			if((count = inputStream.read(buffer))> 0)
			{
				check = copyData(buffer,count);
				return checkSum(check);
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return false;
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
	 	private byte[] copyData(byte[] dataRecv,int length)
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
