package net.nana;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioGroup;
import android.widget.RadioGroup.OnCheckedChangeListener;

public class Change11 extends Activity implements OnClickListener {
	private RadioGroup rg1= null;
	private RadioGroup rg2= null;
	private RadioGroup rg3= null;
	private EditText et1 = null;
	private EditText et2 = null;
	private EditText et3 = null;
	private Button butOK = null;
	private String data1 = "0";
	private String data2 = "0";
	private String data3 = "0";
	private String data4 = null;
	private String data5 = null;
	private String data6 = null;
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.change11);
		rg1 = (RadioGroup)findViewById(R.id.radioGroup1);
		rg1 = (RadioGroup)findViewById(R.id.radioGroup2);
		rg1 = (RadioGroup)findViewById(R.id.radioGroup3);
		et1 = (EditText)findViewById(R.id.Text1);
		et2 = (EditText)findViewById(R.id.Text2);
		et3 = (EditText)findViewById(R.id.Text3);
		butOK = (Button)findViewById(R.id.butOK);
		butOK.setOnClickListener(Change11.this);
		rg1.setOnCheckedChangeListener(new OnCheckedChangeListener()
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
		rg2.setOnCheckedChangeListener(new OnCheckedChangeListener()
		{

			@Override
			public void onCheckedChanged(RadioGroup groud, int id) {
				// TODO Auto-generated method stub
				if(id == R.id.radio1)
				{
					data2 = "0";
				}
				else {
					data2 = "1";
				}
			}});
		rg3.setOnCheckedChangeListener(new OnCheckedChangeListener()
		{

			@Override
			public void onCheckedChanged(RadioGroup groud, int id) {
				// TODO Auto-generated method stub
				if(id == R.id.radio1)
				{
					data3 = "0";
				}
				else {
					data3 = "1";
				}
			}});
	}
	@Override
	public void onClick(View arg0) {
		// TODO Auto-generated method stub
		data4 = (et1.getText().length() != 0) ?et1.getText().toString():"null";
		data5 = (et2.getText().length() != 0) ?et2.getText().toString():"null";
		data6 = (et3.getText().length() != 0) ?et3.getText().toString():"null";
		String string = data1+":"+data2+":"+data3+":"+data4+":"+data5+":"+data6;
		Intent intent=new Intent();  
        intent.setClass(Change11.this, Sendmsg.class);  
        intent.putExtra("data11", string);
        setResult(RESULT_OK, intent);
		Change11.this.finish();
		
	}
}
