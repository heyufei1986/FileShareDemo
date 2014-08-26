package com.hfdemo.filesharedemo.smb;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.util.Enumeration;

import jcifs.smb.NtlmPasswordAuthentication;
import jcifs.smb.SmbFile;
import jcifs.smb.SmbFileInputStream;

import org.apache.ftpserver.FtpServer;
import org.apache.ftpserver.FtpServerFactory;
import org.apache.ftpserver.ftplet.FtpException;
import org.apache.ftpserver.listener.ListenerFactory;
import org.apache.ftpserver.ssl.SslConfigurationFactory;
import org.apache.ftpserver.usermanager.PropertiesUserManagerFactory;

import com.hfdemo.filesharedemo.R;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.widget.TextView;

public class SmbClientActivity extends Activity
{


	private FtpServer	mFtpServer;
	private String		ftpConfigDir	= Environment.getExternalStorageDirectory().getAbsolutePath() + "/ftpConfig/";

	@Override
	public void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.ftp_server_activity);

		downloadOneFile();
	}

	void downloadOneFile()
	{
		jcifs.Config.setProperty("jcifs.netbios.wins", "192.168.1.220");
		NtlmPasswordAuthentication auth = new NtlmPasswordAuthentication("domain", "username", "password");

		SmbFile sbmFile = null;
		SmbFileInputStream in = null;

		try
		{
			sbmFile = new SmbFile("smb://host/c/My Documents/somefile.txt", auth);
			in = new SmbFileInputStream(sbmFile);

			byte[] b = new byte[8192];
			int n;
			while ((n = in.read(b)) > 0)
			{
				System.out.write(b, 0, n);
			}
		}
		catch (Exception e)
		{

		}


	}

	@Override
	protected void onDestroy()
	{
		super.onDestroy();

		if (null != mFtpServer)
		{
			mFtpServer.stop();
			mFtpServer = null;
		}
	}


	// Setting Client Properties
	/**
	 * It may be necessary to set various properties for the client to function
	 * properly. For example, to connect to a server on a remote subnet the IP
	 * address of a WINS server is required to retrieve the target address
	 * although DNS names and direct IP addresses are also valid server
	 * components within an SMB URL.
	 * 
	 * There are three ways to specify any of the available properties listed
	 * the table at the bottom of this page.
	 * 
	 * The traditional System properties are read when the client is first used.
	 * This means you can specify an individual property (the full property
	 * name) on the command line like:
	 * $ java -Djcifs.netbios.wins=192.168.1.220 MyApp
	 * OR set a System property from within your application before any jCIFS
	 * classes are instantiated like:
	 * 
	 * System.setProperty( "jcifs.netbios.wins", "192.168.1.220" );
	 * OR use the jcifs.Config class which is the class that maintains this
	 * information internally however, again, properties must be set before
	 * jCIFS client classes are referenced:
	 * jcifs.Config.setProperty( "jcifs.netbios.wins", "192.168.1.220" );
	 * OR if the property "jcifs.properties" defines the location of a
	 * properties file, all properties will be loaded from it. For example:
	 * $ cat miallen.prp
	 * jcifs.netbios.wins=192.168.1.220
	 * jcifs.smb.client.username=miallen
	 * jcifs.smb.client.password=legos56
	 * ;jcifs.netbios.baddr=192.168.1.255
	 * ;jcifs.util.loglevel = 10
	 * $ java -Djcifs.properties=miallen.prp MyApp
	 * Any properties specified using the
	 * -Djcifs.properties=myjcifsproperties.prp file may be overridden using the
	 * System properties as will any -Dfull.property.name=value VM parameters
	 * which in turn may then be overridden by the direct manipulation of
	 * properties using the Config class.
	 */

}
