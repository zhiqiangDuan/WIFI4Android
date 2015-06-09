package net.nana;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;

public class ChangeData extends Activity implements OnClickListener {

	private Button butOK = null;
	private TextView t1 = null;
	private TextView t2 = null;
	private TextView t3 = null;
	private TextView t4 = null;
	String s1,s2,s3,s4;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.data);
		butOK = (Button)findViewById(R.id.butOK);
		butOK.setOnClickListener(ChangeData.this);
		t1 = (TextView)findViewById(R.id.Text1);
		t2 = (TextView)findViewById(R.id.Text2);
		t3 = (TextView)findViewById(R.id.Text3);
		t4 = (TextView)findViewById(R.id.Text4);
	}
	@Override
	public void onClick(View v) {	
		// TODO Auto-generated method stub
		s1 = (t1.getText().length() != 0) ?t1.getText().toString():"null";
		s2 = (t2.getText().length() != 0) ?t2.getText().toString():"null";
		s3 = (t3.getText().length() != 0) ?t3.getText().toString():"null";
		s4 = (t4.getText().length() != 0) ?t4.getText().toString():"null";
		String data = s1+":"+s2+":"+s3+":"+s4;
		Intent intent=new Intent();  
        intent.setClass(ChangeData.this, Sendmsg.class);  
        intent.putExtra("str", data);
        setResult(RESULT_OK, intent);
		ChangeData.this.finish();
	}
}
