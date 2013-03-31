package hku.alanwong.awstock;

import java.io.*;
import java.net.*;
import java.util.*;

import android.net.Uri;
import android.os.Bundle;
import android.os.StrictMode;
import android.app.Activity;
import android.content.Intent;
import android.view.View;
import android.widget.*;

/*
 * Result interface
 */

public class StockQuote extends Activity {	
	private List<String> results;
	String input;
	String result;
	String from;
	
	// Widgets
	private Button backButton, addButton, addStockButton, newsButton;
	private TextView codeValue, nameValue, askValue, bidValue, changeValue;
	private EditText stockEditText;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
		StrictMode.setThreadPolicy(policy);
		
		input = getIntent().getExtras().getString("input");
		from = getIntent().getExtras().getString("from");
//		System.out.println(input + " received");
		setContentView(R.layout.quote);
		
		//http://www.gummy-stuff.org/Yahoo-data.htm
		//http://finance.yahoo.com/d/quotes.csv?s=%5EHSI&f=l1
		
		String queryUrl = "http://finance.yahoo.com/d/quotes.csv?s=";
		if (input.equals("HSI")) {
			queryUrl = queryUrl + "%5EHSI&f=l1";
		} else {
			queryUrl = queryUrl + input + "&f=nb2b3c6";
		}

		try {
		    URL url = new URL(queryUrl);
		    BufferedReader in = new BufferedReader(new InputStreamReader(url.openStream()));
		   
		    while ((result = in.readLine()) != null) {
		    	results = Arrays.asList(result.split(","));
		    }
		    in.close();
		} catch (MalformedURLException e) {
		} catch (IOException e) {
		}
		
		if (from.equals("portfolio")) {
			finish();
		} else {
			if (results.get(0).replaceAll("\"", "").equals(getIntent().getExtras().getString("input"))){
				Toast.makeText(getBaseContext(), "Code not exist", Toast.LENGTH_SHORT).show();
				// Explicit intent
	        	Intent intent = new Intent(getBaseContext(), StockSearch.class);
	    		try{
	    			startActivity(intent);
	    			finish();
	    		} catch(android.content.ActivityNotFoundException e){
					Toast.makeText(getBaseContext(), "search action not found", Toast.LENGTH_SHORT).show();
				}
			}
			
			codeValue = (TextView) findViewById(R.id.textView_codeValue);
			codeValue.setText(getIntent().getExtras().getString("input"));
			
			nameValue = (TextView) findViewById(R.id.textView_nameValue);
			nameValue.setText(results.get(0).replaceAll("\"", ""));
			
			askValue = (TextView) findViewById(R.id.textView_askValue);
			askValue.setText(results.get(1));
			
			bidValue = (TextView) findViewById(R.id.textView_bidValue);
			bidValue.setText(results.get(2));
			
			changeValue = (TextView) findViewById(R.id.textView_changeValue);
			changeValue.setText(results.get(3).replaceAll("\"", ""));
			
			newsButton = (Button) findViewById(R.id.button_news);
			newsButton.setOnClickListener(
			    new View.OnClickListener(){
			        @Override
			        public void onClick(View v){
			        	String newsLink = "http://finance.yahoo.com/q/h?s=" + getIntent().getExtras().getString("input") + "+Headlines";
			    		// Implicit intent
			    		Intent newsIntent = new Intent (Intent.ACTION_VIEW, Uri.parse(newsLink)); 
			    		startActivity(newsIntent);
			        }
			    }
			);
			
			backButton = (Button) findViewById(R.id.button_back);
			backButton.setOnClickListener(
			    new View.OnClickListener(){
			        @Override
			        public void onClick(View v){
			        	// Explicit intent
			        	Intent intent = new Intent(getBaseContext(), StockSearch.class);
		        		try{
		        			startActivity(intent);
		        			finish();
		        		} catch(android.content.ActivityNotFoundException e){
							Toast.makeText(getBaseContext(), "search action not found", Toast.LENGTH_SHORT).show();
						}
			        }
			    }
			);
			
			addButton = (Button) findViewById(R.id.button_add);
			addButton.setOnClickListener(
			    new View.OnClickListener(){
			        @Override
			        public void onClick(View v){
			        	addStockButton.setVisibility(View.VISIBLE);
			        	stockEditText.setVisibility(View.VISIBLE);
			        	addButton.setVisibility(View.GONE);
			        }
			    }
			);
			
			stockEditText = (EditText) findViewById(R.id.editText_stocks);
			stockEditText.setVisibility(View.GONE);
			addStockButton = (Button) findViewById(R.id.button_addStock);
			addStockButton.setVisibility(View.GONE);
			addStockButton.setOnClickListener(
			    new View.OnClickListener(){
			        @Override
			        public void onClick(View v){
			        	String stocksToAdd = stockEditText.getText().toString();
			        	// Custom intent to load external app
			        	Intent intent = new Intent("hku.alanwong.awstock.welcome");
			        	intent.putExtra("stocksToAdd", stocksToAdd);
			        	intent.putExtra("code", input);
			        	intent.putExtra("ask", results.get(1));
			        	intent.putExtra("bid", results.get(2));
			        	intent.putExtra("from", "quote");
		        		try{
		        			startActivity(intent);
		        		} catch(android.content.ActivityNotFoundException e){
							Toast.makeText(getBaseContext(), "portfolio action not found", Toast.LENGTH_SHORT).show();
						}
			        }
			    }
			);
		}
	}
	
	@Override
	public void finish() {
		if (from.equals("portfolio")) {
			Intent data = new Intent();
			if (input.equals("HSI")) {
				data.putExtra("hsi", results.get(0));
			} else {
				data.putExtra("ask", results.get(1));
				data.putExtra("bid", results.get(2));
				data.putExtra("from", "quote");
				data.putExtra("input", input);
			}
			setResult(RESULT_OK, data);
		}
		super.finish();
	}
}
