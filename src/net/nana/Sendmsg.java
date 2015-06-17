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
		//��activity������ʱ����Ĭ�϶�ȡ����������
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
		groupArray.add("      �������");
		groupArray.add("      ��ѹ����");
		groupArray.add("      ��������");
		groupArray.add("      �¶Ȳ���");
		groupArray.add("      ��ת����");
		groupArray.add("      ת������");
		groupArray.add("      �ٶȲ���");
		groupArray.add("      ��Ʒ��Ϣ");
		groupArray.add("      EBS����");
		groupArray.add("      ���ٹ���");
		groupArray.add("      Ѳ������");
		groupArray.add("      ���ٹ���");
		groupArray.add("      ��������");
		groupArray.add("      ���Ź���");
		groupArray.add("      쭳�����");
		childArray.clear();
	}
	void initSocket()
	{
        String sIP = "192.168.4.1";
		String sPort = "8080";
		main = new Main();
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
		if(resultData != null)
		{
			dataString = resultData.split(":"); 
		}
		
		System.out.println("here!!  "+dataString[0]+" "+dataString[1]+" "+dataString[2]);
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
					    
				      expandableListView.setAdapter(new  ExpandableAdapter(Sendmsg.this )); 
					  progressDialog.dismiss();
					  
					  break;
				  case 100:
					  recvMsg.setText("�����޸ĳɹ���");
					break;
			}
		  }									
	 };

	private void setTheFirstPage() {
		tempArray1.add("HALL����:" + pages[0][0]);
		tempArray1.add("������:" + pages[0][1]);
		tempArray1.add("������:" + pages[0][2]);
		tempArray1.add("���ת��:" + pages[0][3]);
		tempArray2.add("��������ѹ:"+pages[0][4]);
		tempArray2.add("��ѹ�˳���ѹ:"+pages[0][5]);
		tempArray2.add("��С������ѹ:"+pages[0][6]);
		tempArray2.add("���Ƿѹ��ѹ:"+pages[0][7]);
		tempArray2.add("���Ƿѹ�ز�:"+pages[0][8]);
		tempArray2.add("��Ƿѹ��������:"+pages[0][9]);
		tempArray2.add("��Ƿѹ�ز�:"+pages[0][10]);
		tempArray2.add("��Ƿѹ�ز�:"+pages[0][12]);
		tempArray3.add("�����������:"+pages[0][13]);
		tempArray3.add("��������:"+pages[0][14]);
		tempArray3.add("����������ʱ��:"+pages[0][15]);
		tempArray3.add("���е���:"+pages[0][15]);
		tempArray3.add("ƽ������:"+pages[0][15]);
	}
	private void setTheSecondPage() {

		tempArray4.add("�¿ص�������1:" + pages[1][0]);
		tempArray4.add("�¿ص�������2:" + pages[1][1]);
		tempArray4.add("���±����¶�:" + pages[1][2]);
		tempArray4.add("���¿����¶�1:" + pages[1][3]);
		tempArray4.add("���¿����¶�2:"+pages[1][4]);
		tempArray4.add("�¶ȱ����ز�:"+pages[1][5]);
		tempArray5.add("��ת����ʹ��:"+pages[1][6]);
		tempArray5.add("��ת����ʱ��:"+pages[1][7]);
		tempArray6.add("ת��ģʽѡ��1:"+pages[1][8]);
		tempArray6.add("ת��ģʽѡ��2:"+pages[1][9]);
		tempArray6.add("ת�����޹���ֵ:"+pages[1][10]);
		tempArray6.add("ת�����޹����˳�ֵ:"+pages[1][12]);
		tempArray6.add("ת�����޹���ֵ:"+pages[1][13]);
		tempArray6.add("ת�����޹����˳�ֵ:"+pages[1][14]);
		tempArray6.add("ת�������Чֵ:"+pages[1][15]);
		tempArray6.add("ת�������Чֵ:"+pages[1][15]);
	}
	private void setTheThirdPage() {

		tempArray7.add("���ٶ�:" + pages[1][0]);
		tempArray7.add("�p�ٶ�:" + pages[1][1]);
		tempArray8.add("��Ʒ����:" + pages[1][2]);
		tempArray8.add("��Ʒ����:" + pages[1][3]);
		tempArray8.add("��Ʒ�汾:"+pages[1][4]);
		tempArray8.add("������(��):"+pages[1][5]);
		tempArray8.add("������(����):"+pages[1][6]);
		tempArray9.add("EBS����ʹ��:"+pages[1][7]);
		tempArray9.add("EBS�����ѹ:"+pages[1][8]);
		tempArray9.add("EBS�������:"+pages[1][9]);
		tempArray10.add("���ٹ���ʹ��:"+pages[1][12]);
		tempArray10.add("���ٱ���(%):"+pages[1][10]);
		tempArray11.add("Ѳ������ʹ��:"+pages[1][13]);
		tempArray11.add("Ѳ���ֶ�ģʽʹ��:"+pages[1][14]);
		tempArray11.add("Ѳ���Զ�ģʽʹ��:"+pages[1][15]);
		tempArray11.add("Ѳ������ת��ֵ:"+pages[1][15]);
		tempArray11.add("Ѳ�������ٶȱ���(%):"+pages[1][15]);
		tempArray11.add("�Զ�Ѳ����Ӧʱ��(s):"+pages[1][15]);
	}
	private void setTheForthPage() {

		tempArray12.add("���ٹ���ʹ��:" + pages[1][0]);
		tempArray12.add("����ģʽ:" + pages[1][1]);
		tempArray12.add("�ߵ�λ(%):" + pages[1][2]);
		tempArray12.add("�е�λ(%):" + pages[1][3]);
		tempArray12.add("�͵�λ(%):"+pages[1][4]);
		tempArray13.add("��������ʹ��:"+pages[1][5]);
		tempArray13.add("�����ٶȱ���:"+pages[1][6]);
		tempArray13.add("������������:"+pages[1][7]);
		tempArray14.add("���Ź���ʹ��:"+pages[1][8]);
		tempArray14.add("���Ž����ٶȱ���:"+pages[1][9]);
		tempArray14.add("�����˳��ٶȱ���:"+pages[1][12]);
		tempArray14.add("����������(A):"+pages[1][10]);
		tempArray14.add("���Ž���ת��ֵ:"+pages[1][13]);
		tempArray15.add("쭳�����ʹ��:"+pages[1][14]);
		tempArray15.add("쭳�ģʽ:"+pages[1][15]);
		tempArray15.add("쭳�����(A):"+pages[1][15]);
		tempArray15.add("쭳�����ʱ��(S):"+pages[1][15]);
		tempArray15.add("쭳����ʱ��(s):"+pages[1][15]);
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
