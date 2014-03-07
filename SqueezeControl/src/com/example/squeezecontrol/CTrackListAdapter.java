package com.example.squeezecontrol;

import java.util.List;

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
   
	public CTrackListAdapter(MainActivity activity, List<CSong> songList)	
	{
		super(activity, R.layout.item_view, songList);
		m_mainActivity = activity;
		m_songList = songList;
		
       imageLoader=new ImageLoader(activity);
	}
	
/*
	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		// TODO Auto-generated method stub
		// Make sure we
		View itemView = convertView;
		if (itemView == null)
		{
			itemView = getLayoutInflater().inflate(R.layout.item_view, parent, false);
		}
		
		// Find the album to work with
		
		CAlbum currentAlbum = m_albumList.get(position);
		
		// Fill the view

		ImageView imageView = (ImageView) itemView.findViewById(R.id.item_icon);
		
	    imageLoader.DisplayImage(currentAlbum.m_strArtworkUrlSmall, imageView);
		//imageView.setImageResource(currentAlbum.m_iId);
		//imageView.setImageResource(R.drawable.ic_android);			
		
		// Album
		TextView albumText = (TextView) itemView.findViewById(R.id.item_textAlbumName);
		albumText.setText(currentAlbum.m_strAlbumName);

		// Artist
		TextView artistText = (TextView) itemView.findViewById(R.id.item_textArtist);
		artistText.setText(currentAlbum.m_strArtist);
		
		// Year
		TextView yearText = (TextView) itemView.findViewById(R.id.item_textYear);
		yearText.setText(Integer.toString(currentAlbum.m_iYear));
		
		return itemView;
	}
*/	
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