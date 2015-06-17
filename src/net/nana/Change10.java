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

public class Change10 extends Activity implements OnClickListener {
	private RadioGroup rg= null;
	private EditText et1 = null;
	private Button butOK = null;
	private String data1 = "0";
	private String data2 = null;
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.change10);
		rg = (RadioGroup)findViewById(R.id.radioGroup1);
		et1 = (EditText)findViewById(R.id.Text1);
		butOK = (Button)findViewById(R.id.butOK);
		butOK.setOnClickListener(Change10.this);
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
		String string = data1+":"+data2;
		Intent intent=new Intent();  
        intent.setClass(Change10.this, Sendmsg.class);  
        intent.putExtra("data10", string);
        setResult(RESULT_OK, intent);
        Change10.this.finish();
	}
}
