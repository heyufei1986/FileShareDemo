package com.hfdemo.filesharedemo;

import com.hfdemo.filesharedemo.ftp.ftpclient.FtpClientActivity;
import com.hfdemo.filesharedemo.ftp.ftpserver.FtpServerActivity;

import be.ppareit.swiftp.gui.FsPreferenceActivity;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;

public class MainActivity extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {

		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.xml.
		int id = item.getItemId();
		if (id == R.id.action_share_with_pc_apache) {
			Intent intent = new Intent();
			intent.setClass(this, FtpServerActivity.class);
			startActivity(intent);
			return true;
		}
		else if (id == R.id.action_share_with_pc_swiftp) {
			Intent intent = new Intent();
			intent.setClass(this, FsPreferenceActivity.class);
			startActivity(intent);
			return true;
		}
		else if (id == R.id.action_ftp_client) {
			Intent intent = new Intent();
			intent.setClass(this, FtpClientActivity.class);
			startActivity(intent);
			return true;
		}
		return super.onOptionsItemSelected(item);
	}
}
