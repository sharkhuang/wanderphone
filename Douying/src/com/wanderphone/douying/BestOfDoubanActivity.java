package com.wanderphone.douying;

import java.util.ArrayList;
import java.util.List;

import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.wanderphone.getxml.MovieSubject;
import com.wanderphone.getxml.NetUtil;
import com.wanderphone.myAdapter.ClassicListAdapter;
import com.wanderphone.myAdapter.NowPlayingListAdapter;
import com.wanderphone.sqlite.DataBaseAdapter;

/**
 * @ClassName: BestOfDoubanActivity
 * @Description: 显示豆瓣250
 * @author：
 * @version：v1.0
 */
public class BestOfDoubanActivity extends BaseListActivity {
	private List<MovieSubject> movies = new ArrayList<MovieSubject>();
	DataBaseAdapter db_adapter = new DataBaseAdapter(this);
	SQLiteDatabase db;
	boolean flag = true;// 是否有网络 标识
	private ShowBestDoubanList show_best_list;
	boolean flg = true;// listview首行显示 刷新 标识

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		db_adapter.open();

		setContentView(R.layout.movie_list);

		if (db_adapter.if_best_exists()) {
			Log.v("tv", "ok");
			addListHeaderView();
			movies = db_adapter.loadBestData();
			setListAdapter(new ClassicListAdapter(BestOfDoubanActivity.this,
					getListView(), movies));
			showListAsyncTask();
		} else {
			Log.v("tv", "bad");

			showListAsyncTask();

		}
		/*
		 * 正在放映List ClickListener
		 */
		ListView listView = (ListView) findViewById(android.R.id.list);
		listView.setOnItemClickListener(new OnItemClickListener() {
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {

				if (flag && position != 0) {
					MovieSubject subject = movies.get(position - 1);
					Intent i = new Intent(BestOfDoubanActivity.this,
							MovieSubjectView.class);
					i.putExtra("subject", subject);
					startActivity(i);
				} else if (position == 0) {
					showListAsyncTask();
				} else if (!flag && position != 0) {
					Toast.makeText(BestOfDoubanActivity.this, "数据加载失败！",
							Toast.LENGTH_SHORT).show();

				}
			}
		});
	}

	private class ShowBestDoubanList extends AsyncTask<Void, Void, Boolean> {

		@Override
		protected Boolean doInBackground(Void... arg0) {
			try {
				movies = NetUtil.getBestOfDoubanMovie();
				return true;
			} catch (Exception e) {
				e.printStackTrace();
			}
			return false;
		}

		@Override
		protected void onPostExecute(Boolean result) {
			super.onPostExecute(result);
			closeProgressBar();
			if(!flg){
				textview.setText("刷新");
			}
			if (result) {
				addListHeaderView();
				setListAdapter(new ClassicListAdapter(
						BestOfDoubanActivity.this, getListView(), movies));
				flag = true;
				saveData();
			} else {
				Toast.makeText(BestOfDoubanActivity.this, "数据加载失败！",
						Toast.LENGTH_SHORT).show();
				flag = false;
				if (db_adapter.if_best_exists()) {
					addListHeaderView();
					movies = db_adapter.loadBestData();
					setListAdapter(new ClassicListAdapter(
							BestOfDoubanActivity.this, getListView(), movies));
				}else{
					addListHeaderView();
					setListAdapter(new ClassicListAdapter(
							BestOfDoubanActivity.this, getListView(), movies));
				}
			}

		}

		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			showProgressBar();
			if(!flg){
				textview.setText("");
			}
		}

	}
	private TextView textview;
	private String name;
	private byte[] img;

	private void saveData() {
		db_adapter.delete_all_best();
		for (MovieSubject ms : movies) {
			name = ms.getTitle();
			img = ms.get_img_bytes();
			db_adapter.insertBestData(name, img);
			}
	}

	private View buildHeader() {
		textview = new TextView(this);
		textview.setText("刷新");
		textview.setTextColor(0xfffff7ff);
		textview.setTextSize(18);

		textview.setWidth(60);
		textview.setHeight(60);
		textview.setGravity(Gravity.CENTER);
		textview.setBackgroundResource(R.drawable.listviewselector);

		return (textview);
	}
	private void showListAsyncTask() {
		show_best_list = new ShowBestDoubanList();
		show_best_list.execute();
	}

	private void addListHeaderView() {
		if (flg) {
			getListView().addHeaderView(buildHeader());
			flg = false;
		}
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {

		if (keyCode == KeyEvent.KEYCODE_MENU) {
			super.openOptionsMenu();
		}
		if (keyCode == KeyEvent.KEYCODE_BACK && event.getRepeatCount() == 0) {
			doExit();
			return true;
		}
		return true;
	}

	@Override
	public void onOptionsMenuClosed(Menu menu) {
		super.onOptionsMenuClosed(menu);
	}

	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		db_adapter.close();
		super.onDestroy();
	}
}
