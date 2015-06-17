package net.nana;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;

public class Change7 extends Activity implements OnClickListener {
	private EditText et1 = null;
	private EditText et2 = null;
	private Button butOK = null;
	private String data1;
	private String data2;
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.change7);
		et1 = (EditText)findViewById(R.id.Text1);
		et2 = (EditText)findViewById(R.id.Text2);
		butOK = (Button)findViewById(R.id.butOK);
		butOK.setOnClickListener(Change7.this);
	}
	@Override
	public void onClick(View arg0) {
		// TODO Auto-generated method stub
		data1 = (et1.getText().length() != 0) ?et1.getText().toString():"null";
		data2 = (et2.getText().length() != 0) ?et2.getText().toString():"null";
		String string = data1+":"+data2;
		Intent intent=new Intent();  
        intent.setClass(Change7.this, Sendmsg.class);  
        intent.putExtra("data7", string);
        setResult(RESULT_OK, intent);
		Change7.this.finish();
	}
}
