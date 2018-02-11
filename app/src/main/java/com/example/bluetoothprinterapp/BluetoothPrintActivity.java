package com.example.bluetoothprinterapp;

import android.app.Activity;
import android.bluetooth.BluetoothSocket;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;

import java.io.IOException;
import java.io.OutputStream;


public class BluetoothPrintActivity extends AppCompatActivity
{       
    /** Called when the activity is first created. */       

    Button printbtn;

	byte FONT_TYPE;
	private static BluetoothSocket btsocket;
	private static OutputStream btoutputstream;
	private EditText accountNoEditText;
	private EditText accountNameEditText;
	private EditText amountEditText;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_print_bluetooth);
		printbtn = (Button)findViewById(R.id.printButton);
		accountNoEditText = (EditText) findViewById(R.id.accountNoEditText);
		accountNameEditText = (EditText) findViewById(R.id.accountNameEditText);
		amountEditText = (EditText) findViewById(R.id.amountEditText);


		printbtn.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				connect();
			}
		});
	}

	protected void connect() {
		if(btsocket == null){
			Intent BTIntent = new Intent(getApplicationContext(), BTDeviceListActivity.class);
			this.startActivityForResult(BTIntent, BTDeviceListActivity.REQUEST_CONNECT_BT);
		}
		else{
            
			OutputStream opstream = null;
			try {
				opstream = btsocket.getOutputStream();
			} catch (IOException e) { 
				e.printStackTrace();
			}
			btoutputstream = opstream;
			print_bt();

		}

	}


	private void print_bt() {
		try {
			try {
				Thread.sleep(1000);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			
			btoutputstream = btsocket.getOutputStream();

			byte[] printformat = { 0x1B, 0x21, FONT_TYPE };
			btoutputstream.write(printformat);
			String msg = getMessage();
			btoutputstream.write(msg.getBytes());
			btoutputstream.write(0x0D);
			btoutputstream.write(0x0D);
			btoutputstream.write(0x0D);
			btoutputstream.flush();
		} catch (IOException e) {
			e.printStackTrace();
		}

	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		try {
			if(btsocket!= null){
				btoutputstream.close();
				btsocket.close();
				btsocket = null;
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
    
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		try {
			btsocket = BTDeviceListActivity.getSocket();
			if(btsocket != null){
				print_bt();
			}

		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private String getMessage() {
		StringBuilder stringBuilder = new StringBuilder();

		double amount = Double.parseDouble(amountEditText.getText().toString());
		stringBuilder.append(String.format("Deposited amount: %.2f\n",amount));

		stringBuilder.append(String.format("Account No: %s\n",accountNoEditText.getText().toString()));

		stringBuilder.append(String.format("Account Name: %s\n",accountNameEditText.getText().toString()));


		stringBuilder.append(String.format("Printed by: %s\n","Babul"));

		stringBuilder.append("\n\n\n\n\n");

		return stringBuilder.toString();
	}
}         
