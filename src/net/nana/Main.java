package net.nana;




import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
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
		private TextView scanResult;

		private ArrayAdapter<String> wifilistArray;
		
		private String mScanResult;
		private WifiAdmin mWifiAdmin;
	
	
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
		//sendmsg = (Button)findViewById(R.id.send);

		openwifi.setOnClickListener(Main.this);
		closewifi.setOnClickListener(Main.this);
		scanwifi.setOnClickListener(Main.this);
		connect.setOnClickListener(Main.this);
		disconnect.setOnClickListener(Main.this);
		//sendmsg.setOnClickListener(new SendButton());
	}
	
	//将以下内部类捆绑在send按钮上，按下时跳转到Sendmsg界面
    class SendButton implements OnClickListener{
		@Override
		public void onClick(View v) {
			// TODO Auto-generated method stub
			Intent intent = new Intent();
			intent.setClass(Main.this, Sendmsg.class);
			Main.this.startActivity(intent);
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
		default:
			break;
		}
	}
}