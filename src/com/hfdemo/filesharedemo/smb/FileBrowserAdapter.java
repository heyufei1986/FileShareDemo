package com.hfdemo.filesharedemo.smb;


import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.List;

import com.hfdemo.filesharedemo.R;

import jcifs.smb.SmbFile;
import android.content.Context;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

public class FileBrowserAdapter extends BaseAdapter{

	public interface OnCurrentPathChanged{
		public void onCurrentPathChanged(String newPath);
	}
	
	private String TAG = "FileBrowserAdapter";
	private Context mContext;
	private LayoutInflater mInflater;
	private List<String> mSubFiles;
	
	private int mCurrentPosition;
	
	private JCIFSHelper mJCIFSHelper;
	
	private String mCurrentPath;
	private String mPendingPath;
	
	OnCurrentPathChanged mPathChangeListener;
	
	private Handler mHandler;
	
	
	
	
	public FileBrowserAdapter(Context context){
		mCurrentPosition = -1;
		mContext = context;
		mInflater = LayoutInflater.from(mContext);
		mSubFiles = new ArrayList<String>();
		mHandler = new Handler();
	}
	
	@Override
	public int getCount() {
		synchronized (mSubFiles)
		{
			return mSubFiles.size();
		}
	}

	@Override
	public Object getItem(int position) {
		synchronized (mSubFiles)
		{
			return mSubFiles.get(position);
		}
	}

	@Override
	public long getItemId(int position) {
		return -1;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		Log.d(TAG, "getView position=" + position);
		if(convertView == null){
			convertView = mInflater.inflate(R.layout.smb_file_browser_item, null);
		}
		
		
		View menuView = (View)convertView.findViewById(R.id.ctrList);
		
		TextView filename = (TextView)convertView.findViewById(R.id.smb_file_browser_filename);
		filename.setText(mSubFiles.get(position));
		
		if(position == mCurrentPosition){
			menuView.setVisibility(View.VISIBLE);
			Button deleteBt = (Button)menuView.findViewById(R.id.delete);
			Button downloadBt = (Button)menuView.findViewById(R.id.download);
			Button detailBt = (Button)menuView.findViewById(R.id.detail);
			
			
			
			
			downloadBt.setOnClickListener(new View.OnClickListener() {
				
				@Override
				public void onClick(View v) {/*
					Intent intent = new Intent();
					intent.setClass(mContext, SelectAppActivity.class);
					((Activity)mContext).startActivityForResult(intent, TimeSubjectActivity.ACTIVITY_REQUEST_SELECT_APP);
				*/}
			});
			
			
			deleteBt.setOnClickListener(new View.OnClickListener() {
				
				@Override
				public void onClick(View v) {
					mCurrentPosition = -1;
					notifyDataSetChanged();
				}
			});
			
			
			detailBt.setOnClickListener(new View.OnClickListener() {
				
				@Override
				public void onClick(View v) {  }
			});
		}else{
			menuView.setVisibility(View.GONE);
		}
		
		return convertView;
	}
	
	
	public void goToItem(int pos)
	{
		mPendingPath = mCurrentPath + "/" + mSubFiles.get(pos);
		scanFolder(mPendingPath);
	}
	
	void setPathChangeListener(OnCurrentPathChanged onPathChangeListener)
	{
		mPathChangeListener = onPathChangeListener;
	}
	
	void scanFolder(String path)
	{
		Log.d(TAG, "scanFolder:" + path);
		mPendingPath = path;
		new Thread(new Runnable()
		{

			@Override
			public void run()
			{
				SmbFile smbFile = mJCIFSHelper.getSmbFile(mPendingPath);

				try
				{
					if (smbFile.isDirectory())
					{
						if(!mPendingPath.endsWith("/"))
						{
							smbFile = mJCIFSHelper.getSmbFile(mPendingPath + "/");
						}
						
						String[] subFiles = smbFile.list();
						mSubFiles.clear();
						synchronized (mSubFiles)
						{
							for (String subFile : subFiles)
							{
								mSubFiles.add(subFile);

								Log.d(TAG, "list subfile for mPendingPath: " + subFile);
							}
						}


						mHandler.post(new Runnable()
						{

							@Override
							public void run()
							{
								FileBrowserAdapter.this.notifyDataSetChanged();
								
								mCurrentPath = mPendingPath;
								mPendingPath = null;
								if(mPathChangeListener != null)
									mPathChangeListener.onCurrentPathChanged(mCurrentPath);
							}
						});
					}
				}
				catch (Exception e)
				{
					e.printStackTrace();
				}
			}
		}).start();
	}
	
	void setJCIFSHelper(JCIFSHelper jCIFSHelper)
	{
		mJCIFSHelper = jCIFSHelper;
		scanFolder("");
	}
	
	void setShowContextMenuAt(int position)
	{
		mCurrentPosition = position;
		notifyDataSetChanged();
	}
}
