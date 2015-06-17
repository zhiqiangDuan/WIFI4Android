package net.nana;

import java.security.acl.Group;

import android.R.string;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RadioGroup.OnCheckedChangeListener;
import android.widget.TextView;

public class Change1 extends Activity implements OnClickListener {
	private RadioGroup rg= null;
	private EditText et1 = null;
	private EditText et2 = null;
	private EditText et3 = null;
	private Button butOK = null;
	private String data1 = "0";
	private String data2 = null;
	private String data3 = null;
	private String data4 = null;
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.change1);
		rg = (RadioGroup)findViewById(R.id.radioGroup1);
		et1 = (EditText)findViewById(R.id.Text1);
		et2 = (EditText)findViewById(R.id.Text2);
		et3 = (EditText)findViewById(R.id.Text3);
		butOK = (Button)findViewById(R.id.butOK);
		butOK.setOnClickListener(Change1.this);
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
		String string = data1+":"+data2+":"+data3+":"+data4;
		Intent intent=new Intent();  
        intent.setClass(Change1.this, Sendmsg.class);  
        intent.putExtra("data1", string);
        setResult(RESULT_OK, intent);
		Change1.this.finish();
		
	}
}
