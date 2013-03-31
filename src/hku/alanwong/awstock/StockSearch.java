package hku.alanwong.awstock;

import android.os.Bundle;
import android.os.StrictMode;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.view.View;
import android.widget.*;

/*
 * Search interface for user input stock code
 */

public class StockSearch extends Activity {	
	
	// Widgets
	private Button searchButton, resetButton;
	private EditText inputEditText;
	private ProgressDialog progressDialog;
	
	String input;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.search);
		progressDialog = new ProgressDialog(StockSearch.this);
		progressDialog.setMessage("Loading...");
		StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
		StrictMode.setThreadPolicy(policy);	
		
		inputEditText = (EditText) findViewById(R.id.edittext_input);
		
		searchButton = (Button) findViewById(R.id.button_search);
		searchButton.setOnClickListener(
		    new View.OnClickListener(){
		        @Override
		        public void onClick(View v){
		        	progressDialog.show();
		        	
		        	input = inputEditText.getText().toString();
		        	while (input.length() < 4){
		        		input = "0" + input;
		        	}
		        	input += ".HK";
		        	inputEditText.setText(input);
		        	
	        		// Explicit intent
		        	Intent intent = new Intent(getBaseContext(), StockQuote.class);
		        	intent.putExtra("input", input);
		        	intent.putExtra("from", "search");
	        		try{
	        			startActivity(intent);
	        			progressDialog.dismiss();
	        		} catch(android.content.ActivityNotFoundException e){
						Toast.makeText(getBaseContext(), "quote action not found", Toast.LENGTH_SHORT).show();
					}
		        }
		    }
		);
		
		resetButton = (Button) findViewById(R.id.button_reset);
		resetButton.setOnClickListener(
		    new View.OnClickListener(){
		        @Override
		        public void onClick(View v){
		        	inputEditText.setText("");
		        }
		    }
		);
	}
	
	protected void onPause(Bundle savedInstanceState) {
		super.onPause();
		progressDialog.dismiss();
	}
	protected void onResume(Bundle savedInstanceState) {
		super.onResume();
		progressDialog.dismiss();
	}
}
