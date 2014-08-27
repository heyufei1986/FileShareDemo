package com.hfdemo.filesharedemo.smb;

import com.hfdemo.filesharedemo.R;

import android.app.Activity;
import android.content.Context;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ContextMenu.ContextMenuInfo;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemLongClickListener;

public class FileBrowserView
{
	static final int MENU_DELETE = 1;
	static final int MENU_COPY = 2;
	static final int MENU_DETAIL= 3;
	
	
	private Context mContext;
	private View mRootView;
	private ListView mListView;
	private TextView mPathTextView;
	
	
	FileBrowserAdapter mFileBrowserAdapter;
	
	public FileBrowserView(Context context)
	{
		mContext = context;
		setupUIViews();
	}
	
	public void setFileBrowserAdapter(FileBrowserAdapter adapter)
	{
		mFileBrowserAdapter = adapter;
		
		mListView.setAdapter(mFileBrowserAdapter);
		
		mFileBrowserAdapter.setPathChangeListener(new FileBrowserAdapter.OnCurrentPathChanged()
		{
			
			@Override
			public void onCurrentPathChanged(String newPath)
			{
				mPathTextView.setText(newPath);
			}
		});
	}

	public void setupUIViews()
	{
		LayoutInflater inflater = LayoutInflater.from(mContext);
		mRootView = inflater.inflate(R.layout.file_browser_view, null);
		
		mPathTextView = (TextView)mRootView.findViewById(R.id.smb_client_current_path);
		mListView = (ListView)mRootView.findViewById(R.id.smb_client_file_list);
		
		
//		mListView.setOnCreateContextMenuListener(new View.OnCreateContextMenuListener()
//		{
//			
//			@Override
//			public void onCreateContextMenu(ContextMenu menu, View v, ContextMenuInfo menuInfo)
//			{
//				menu.add(Menu.NONE, MENU_DELETE, Menu.NONE, "删除");
//				menu.add(Menu.NONE, MENU_COPY, Menu.NONE, "复制");
//				menu.add(Menu.NONE, MENU_DETAIL, Menu.NONE, "详情");
//			}
//		});
//		
		
		mListView.setOnItemLongClickListener(new OnItemLongClickListener() {

			@Override
			public boolean onItemLongClick(AdapterView<?> listView, View args1	,
					int position, long arg3) {
				mFileBrowserAdapter.setShowContextMenuAt(position);
				return true;
			}
		});
		
		
		mListView.setOnItemClickListener(new OnItemClickListener()
		{

			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3)
			{
				mFileBrowserAdapter.goToItem(arg2);
			}
		});
		
		
		mListView.setAdapter(mFileBrowserAdapter);
	}
	
	public View getView()
	{
		return mRootView;	
	}
}
