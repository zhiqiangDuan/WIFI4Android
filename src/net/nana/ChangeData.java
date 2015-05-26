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
		String data = t1.getText().toString();//+" "+t2.getText()+" "+ t3.getText()+" "+ t4.getText();
		 Intent intent=new Intent();  
         intent.setClass(ChangeData.this, Sendmsg.class);  
         intent.putExtra("str", data);
        // ChangeData.this.startActivityForResult(intent,1);
        setResult(RESULT_OK, intent);
		ChangeData.this.finish();
		
	}

}
