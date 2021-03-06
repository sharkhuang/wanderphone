package com.wanderphone.minesweep;

import java.util.ArrayList;
import java.util.List;

import android.app.ListActivity;
import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.widget.ListView;
import android.widget.Toast;

//import com.minesweep.R;
import com.wanderphone.minesweep.listAdapter.RankListAdapter;
import com.wanderphone.minesweep.xmlparse.HttpClientConnector;
import com.wanderphone.minesweep.xmlparse.RankInfo;
import com.wanderphone.minesweep.xmlparse.RankInfoParse;

public class NormalRankActivity extends ListActivity{
	private List<RankInfo> rankInfos= new  ArrayList<RankInfo>();
	private ListView rankeasy_list;
	private ProgressDialog dialog;
	public void onCreate(Bundle savedInstanceState)
	{
		 super.onCreate(savedInstanceState);
		 setContentView(R.layout.normal_rank);
		 
		 dialog = new ProgressDialog(this);		 
		 rankeasy_list=(ListView)findViewById(android.R.id.list);
		 
		 showEasyRankList();
	}
	
	public void showEasyRankList(){
		new AsyncTask<Void, Void, Boolean>(){

			@Override
			protected Boolean doInBackground(Void... arg0) {
				// TODO Auto-generated method stub
				try{
					final String rankInfoUrl = getResources().getString(R.string.websit) + "?level=normal&which_use=4";
					String rankInfoMessage = HttpClientConnector.getStringByUrl(rankInfoUrl);
					
					if(rankInfoMessage != ""&&rankInfoMessage!=null)
					{
						rankInfos = RankInfoParse.parse(rankInfoMessage);
						Log.v("rankInfos", rankInfos.toString());
						return true;
					}
					
				}catch (Exception e){
					e.printStackTrace();
				}
				return false;
			}
			
			protected void onPostExecute(Boolean result){
				super.onPostExecute(result);
				dialog.hide();
				
				if(result)
				{
					setListAdapter(new RankListAdapter(NormalRankActivity.this,
							getListView(),rankInfos));
					
					for(int i=0;i<rankInfos.size();i++)
						Log.v("rankInfo", rankInfos.get(i).getUsername());
					
				}else{
					Toast.makeText(NormalRankActivity.this, R.string.failed_connect, Toast.LENGTH_LONG).show();
				}
				
			}
			
			protected void onPreExecute(){
				super.onPreExecute();
				dialog.setMessage(getResources().getString(R.string.loading_rank));
				dialog.show();
			}			
		}.execute();
	}
}

