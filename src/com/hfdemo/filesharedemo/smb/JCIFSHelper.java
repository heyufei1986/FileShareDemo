package com.hfdemo.filesharedemo.smb;

import jcifs.smb.NtlmPasswordAuthentication;
import jcifs.smb.SmbFile;

import com.hfdemo.filesharedemo.utils.StringUtils;

public class JCIFSHelper
{
	private String mServerName;
	private String mServerPort;
	private String mUsername;
	private String mPassword;
	
	private String mInitPath;
	NtlmPasswordAuthentication mAuth = null;
	
	public JCIFSHelper(String serverName, String serverPort, String username, String password)
	{
		mServerName = serverName;
		mServerPort = serverPort;
		mUsername = username;
		mPassword = password;
		
		init();
	}
	
	void init()
	{
		mInitPath = "smb://" + mServerName + "/";
		if(!StringUtils.isEmpty(mServerPort))
		{
			mInitPath += ":" + mServerPort;
		}
		
		if(!StringUtils.isEmpty(mUsername) && !StringUtils.isEmpty(mPassword))
		{
			try{
				mAuth = new NtlmPasswordAuthentication("domain", mUsername, mPassword);
			}catch(Throwable e)
			{
				
			}
		}
		
	}
	
	SmbFile getRootSmbFile()
	{
		return getSmbFile("");
	}
	
	SmbFile getSmbFile(String path)
	{
		String smbPath = mInitPath + path;
	
		SmbFile smbFile = null;
		
		try{
			if(mAuth != null)
				smbFile = new SmbFile(smbPath, mAuth);
			else
				smbFile = new SmbFile(smbPath);
		}
		catch (Exception e) {
		}
		
		return smbFile;
	}
}
