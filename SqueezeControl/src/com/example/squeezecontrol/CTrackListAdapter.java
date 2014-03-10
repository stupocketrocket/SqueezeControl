package com.example.squeezecontrol;

import java.util.List;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;


public class CTrackListAdapter extends ArrayAdapter<CSong>
{
	public ImageLoader imageLoader;
	private MainActivity m_mainActivity;
	private List<CSong> m_songList;
	private static LayoutInflater m_inflater=null;	
   
	public CTrackListAdapter(MainActivity activity, List<CSong> songList)	
	{
		super(activity, R.layout.item_view, songList);
		m_mainActivity = activity;
		m_songList = songList;
		
		m_inflater = (LayoutInflater)activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		
		imageLoader=new ImageLoader(activity);
	}
	

	@Override
	public View getView(int position, View convertView, ViewGroup parent) 
	{
		// TODO Auto-generated method stub
		// Make sure we
		View itemView = convertView;
		if (itemView == null)
		{
			itemView = m_inflater.inflate(R.layout.track_view, parent, false);
		}
		
		// Find the album to work with
		
		CSong currentSong = m_songList.get(position);
		
		// Fill the view

		ImageView imageView = (ImageView) itemView.findViewById(R.id.item_trackicon);
		
	    imageLoader.DisplayImage(currentSong.m_strArtworkUrlSmall, imageView);
		//imageView.setImageResource(currentAlbum.m_iId);
		//imageView.setImageResource(R.drawable.ic_android);			
		
		// Album
		TextView albumText = (TextView) itemView.findViewById(R.id.text_trackName);
		albumText.setText(currentSong.m_strSongTitle);

		// Artist
		TextView artistText = (TextView) itemView.findViewById(R.id.text_artist);
		artistText.setText(currentSong.m_strArtist);
		
		// Year
		TextView yearText = (TextView) itemView.findViewById(R.id.text_time);
		yearText.setText(Integer.toString((int)currentSong.m_dDuration));
		
		return itemView;
	}
}	


/*
private void RegisterClickCallback() 
{
	// TODO Auto-generated method stub
	 ListView list = (ListView) findViewById(R.id.albumsListView);
	 list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
		 @Override
		 public void onItemClick(AdapterView<?> parent, View viewClicked, int position, long id)
		 {
			 CAlbum pickedAlbum = m_albumList.get(position);
			 String message = "You clicked position " + position + " which is album "
					 + pickedAlbum.m_strAlbumName;
			 Toast.makeText(MainActivity.this, message, Toast.LENGTH_LONG).show();
			 
			String strSongListMessage = MainActivity.SendSongListMessage(0, 100000, pickedAlbum);
			new MyAsyncTask().execute(strSongListMessage, strURL);				
		 }
	 });	 
}
*/