package com.hfdemo.filesharedemo.smb;


import com.hfdemo.filesharedemo.R;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;

public class SmbLoginInfoSettingDialog {
	
	public interface 	OnSmbLoginInfoSetListener{
		public void onSmbLoginInfoSetComplete(SmbLoginInfoSettingDialog dialog);
	}
	
	
	private Context mContext;
	private View mContextView = null;
	private EditText mServerName = null;
	private EditText mServerPort = null;
	private EditText mUsername = null;
	private EditText mPassword = null;
	
	
	OnSmbLoginInfoSetListener mListener;
	
	SmbLoginInfoSettingDialog(Context context){
		mContext = context;
	}
	
	public String getServerName()
	{
		return mServerName != null?mServerName.getText().toString():"";
	}
	
	public String getServerPort()
	{
		return mServerPort != null?mServerPort.getText().toString():"";
	}
	
	public String getUsername()
	{
		return mUsername != null?mUsername.getText().toString():"";
	}
	
	public String getPassword()
	{
		return mPassword != null?mPassword.getText().toString():"";
	}

	void setOnClickListener(OnSmbLoginInfoSetListener listener){
		mListener = listener;
	}
	
	void show(){
		if(mContextView == null)
		{
			LayoutInflater inflater = LayoutInflater.from(mContext);
			mContextView = inflater.inflate(R.layout.smb_client_setting, null);
			
			mServerName = (EditText)mContextView.findViewById(R.id.smb_client_server_name);
			mServerPort = (EditText)mContextView.findViewById(R.id.smb_client_server_port);
			mUsername = (EditText)mContextView.findViewById(R.id.smb_client_username);
			mPassword = (EditText)mContextView.findViewById(R.id.smb_client_password);
		}
		
		new AlertDialog.Builder(mContext)
		.setTitle("Smb连接设置")
		.setView(mContextView)
		.setPositiveButton(R.string.yes, new DialogInterface.OnClickListener() {
			
			@Override
			public void onClick(DialogInterface dialog, int which) {
				if(mListener != null){
					mListener.onSmbLoginInfoSetComplete(SmbLoginInfoSettingDialog.this);
				}
			}
		})
		.setNegativeButton(R.string.cancel, null)
		.show();
	}
}
