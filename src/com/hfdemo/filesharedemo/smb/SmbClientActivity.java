package com.hfdemo.filesharedemo.smb;




import com.hfdemo.filesharedemo.R;

import android.app.Activity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

public class SmbClientActivity extends Activity
{
	private JCIFSHelper	mJCIFSHelper;
	private FileBrowserView mFilebrowserView;
	private FileBrowserAdapter mFileBrowserAdapter;
	
	
	@Override
	public void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		
		mFilebrowserView = new FileBrowserView(this);
		View view = mFilebrowserView.getView();
		setContentView(view);
		
		
		mFileBrowserAdapter = new FileBrowserAdapter(this);
		mFilebrowserView.setFileBrowserAdapter(mFileBrowserAdapter);
		
		openSettingDialog();
	}

	@Override
	protected void onDestroy()
	{
		super.onDestroy();
	}

	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {

		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.smb_client_menu, menu);
		return true;
	}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.xml.
		int id = item.getItemId();
		if (id == R.id.smb_client_settings) {
			openSettingDialog();
			return true;
		}
		return super.onOptionsItemSelected(item);
	}
	
	void openSettingDialog() 
	{
		SmbLoginInfoSettingDialog dlg = new SmbLoginInfoSettingDialog(this);
		dlg.setOnClickListener(new SmbLoginInfoSettingDialog.OnSmbLoginInfoSetListener()
		{
			
			@Override
			public void onSmbLoginInfoSetComplete(SmbLoginInfoSettingDialog dialog)
			{
				final String serverName = dialog.getServerName();
				final String serverPort = dialog.getServerPort();
				final String username = dialog.getUsername();
				final String password = dialog.getPassword();
				
				mJCIFSHelper = new JCIFSHelper(serverName, serverPort, username, password);
				mFileBrowserAdapter.setJCIFSHelper(mJCIFSHelper);
			}
		});
		
		dlg.show();
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
	 * More informations: see http://jcifs.samba.org/src/docs/api/overview-summary.html#scp
	 */

	//Setting Name Resolution Properties
	/**
	 * Server names can be resolved using NetBIOS broadcast queries, WINS
	 * queries, lmhosts files, or DNS. The individual methods as well as the
	 * order in which they will be used can be set using the jcifs.resolveOrder
	 * property. Several other properties are important to how server names are
	 * resolved and are discussed below.
	 */
	


}
