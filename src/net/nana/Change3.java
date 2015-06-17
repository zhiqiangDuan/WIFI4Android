package net.nana;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;

public class Change3 extends Activity implements OnClickListener {
	private EditText et1 = null;
	private EditText et2 = null;
	private EditText et3 = null;
	private EditText et4 = null;
	private EditText et5 = null;
	private Button butOK = null;
	private String data1 = null;
	private String data2 = null;
	private String data3 = null;
	private String data4 = null;
	private String data5 = null;
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.change3);
		et1 = (EditText)findViewById(R.id.Text1);
		et2 = (EditText)findViewById(R.id.Text2);
		et3 = (EditText)findViewById(R.id.Text3);
		et4 = (EditText)findViewById(R.id.Text4);
		et5 = (EditText)findViewById(R.id.Text5);
		butOK = (Button)findViewById(R.id.butOK);
		butOK.setOnClickListener(Change3.this);
	}
	@Override
	public void onClick(View arg0) {
		// TODO Auto-generated method stub
		data1 = (et1.getText().length() != 0) ?et1.getText().toString():"null";
		data2 = (et2.getText().length() != 0) ?et2.getText().toString():"null";
		data3 = (et3.getText().length() != 0) ?et3.getText().toString():"null";
		data4 = (et4.getText().length() != 0) ?et4.getText().toString():"null";
		data5 = (et5.getText().length() != 0) ?et5.getText().toString():"null";
		String string = data1+":"+data2+":"+data3+":"+data4+":"+data5;
		Intent intent=new Intent();  
        intent.setClass(Change3.this, Sendmsg.class);  
        intent.putExtra("data3", string);
        setResult(RESULT_OK, intent);
		Change3.this.finish();
	}
}
