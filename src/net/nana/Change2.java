package net.nana;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.RadioGroup.OnCheckedChangeListener;

public class Change2 extends Activity implements OnClickListener {
	private RadioGroup rg= null;
	private EditText et1 = null;
	private EditText et2 = null;
	private EditText et3 = null;
	private EditText et4 = null;
	private EditText et5 = null;
	private EditText et6 = null;
	private EditText et7 = null;
	private Button butOK = null;
	private String data1 = "0";
	private String data2 = null;
	private String data3 = null;
	private String data4 = null;
	private String data5 = null;
	private String data6 = null;
	private String data7 = null;
	private String data8 = null;
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.change2);
		rg = (RadioGroup)findViewById(R.id.radioGroup1);
		et1 = (EditText)findViewById(R.id.Text1);
		et2 = (EditText)findViewById(R.id.Text2);
		et3 = (EditText)findViewById(R.id.Text3);
		et4 = (EditText)findViewById(R.id.Text4);
		et5 = (EditText)findViewById(R.id.Text5);
		et6 = (EditText)findViewById(R.id.Text6);
		et7 = (EditText)findViewById(R.id.Text7);
		butOK = (Button)findViewById(R.id.butOK);
		butOK.setOnClickListener(Change2.this);
		rg.setOnCheckedChangeListener(new OnCheckedChangeListener()
		{

			@Override
			public void onCheckedChanged(RadioGroup groud, int id) {
				// TODO Auto-generated method stub
				if(id == R.id.radio1)
				{
					data1 = "0";
				}
				else {
					data1 = "1";
				}
			}});
	}
	@Override
	public void onClick(View arg0) {
		// TODO Auto-generated method stub
		data2 = (et1.getText().length() != 0) ?et1.getText().toString():"null";
		data3 = (et2.getText().length() != 0) ?et2.getText().toString():"null";
		data4 = (et3.getText().length() != 0) ?et3.getText().toString():"null";
		data5 = (et4.getText().length() != 0) ?et4.getText().toString():"null";
		data6 = (et5.getText().length() != 0) ?et5.getText().toString():"null";
		data7 = (et6.getText().length() != 0) ?et6.getText().toString():"null";
		data8 = (et7.getText().length() != 0) ?et7.getText().toString():"null";
		String string = data1+":"+data2+":"+data3+":"+data4+":"+data5+":"+data6+":"+data7+":"+data8;
		Intent intent=new Intent();  
        intent.setClass(Change2.this, Sendmsg.class);  
        intent.putExtra("data2", string);
        setResult(RESULT_OK, intent);
		Change2.this.finish();
	}
}
