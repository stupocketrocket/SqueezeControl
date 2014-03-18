package com.example.squeezecontrol;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.zip.GZIPInputStream;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicHeader;
import org.apache.http.protocol.HTTP;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;


import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.media.MediaPlayer;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends Activity 
{


	private static final String URL = "http://10.0.2.2:9001/jsonrpc.js";	
	public static ProgressBar progressBar;
	public static String m_strHostName = "10.0.2.2";
	public static int m_iPortNumber = 9001;
	private static String strURL = null;
	private String strURLFormat = new String("http://%s:%d/jsonrpc.js");
	private String strURLMP3StreamFormat = new String("http://%s:%d/stream.mp3");	
	private EditText editHostName;
	private EditText editPortNumber;
	
	private List<CAlbum> m_albumList = new ArrayList<CAlbum>();
	private List<CSong> m_songList = new ArrayList<CSong>();
	private List<String> m_playeridList = new ArrayList<String>();
	private MediaPlayer m_mediaPlayer;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		progressBar = (ProgressBar)findViewById(R.id.progressBar1);
		progressBar.setVisibility(View.GONE);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) 
	{
	       if (item.getItemId() == R.id.action_settings)
	       {
	           Intent intent = new Intent()
	           		.setClass(this,
	                CSettingsPreferenceActivity.class);
	           this.startActivityForResult(intent, 0);
	       }
	       return true;
	}


	 public void OnPlayPause(View view) throws IOException
	 {
		 
		 
	 }
	
	
	 public void SelectPlaylist(View view) throws IOException 
	 {
		ListView list = (ListView) findViewById(R.id.albumsListView);
		list.setVisibility(View.INVISIBLE);
		
		loadPreferences();
		
		String strURLMP3Stream = String.format(strURLMP3StreamFormat, m_strHostName, m_iPortNumber);
		
		m_mediaPlayer = new MediaPlayer();
		//m_mediaPlayer.setDataSource(strURLMP3Stream);
		//m_mediaPlayer.prepare();
		//if (!m_mediaPlayer.isPlaying())
		//{
		//	m_mediaPlayer.start();
		//	m_mediaPlayer.setVolume(1.0f, 1.0f);
		//}
		String strServerStatusMessage = SendServerStatusMessage(0, 999);
		new MyAsyncTask().execute(strServerStatusMessage, strURL);	
	 }
	 
	 @Override
	 protected void onActivityResult(int requestCode, int resultCode, Intent data) 
	 {
		// TODO Auto-generated method stub
		//super.onActivityResult(requestCode, resultCode, data);
		 loadPreferences();
	 }

	 private void loadPreferences()
	 {
		  	SharedPreferences mySharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
		  
		  //boolean my_checkbox_preference = mySharedPreferences.getBoolean("checkbox_preference", false);
		  //prefCheckBox.setChecked(my_checkbox_preference);

		  	String my_edittext_serverAddress = mySharedPreferences.getString("edittext_serverAddress", "");
		     
		  	String my_edittext_serverPort = mySharedPreferences.getString("edittext_serverPort", "");

	  		String strHostName = my_edittext_serverAddress;		  	
		  	if (strHostName == "")
		  		strHostName = m_strHostName;
 
		  	int iPortNumber = m_iPortNumber; 
		  	if (my_edittext_serverPort != "")
		  		iPortNumber = Integer.parseInt(my_edittext_serverPort);
			
			strURL = String.format(strURLFormat, strHostName, iPortNumber);
	 }	 
	 
	 private void ProcessJSONResponse(JSONObject result)
	 {
		 try {
			Log.w("STU", result.toString(2));
			
			String params = result.getString("params");
			
			if (params.contains("serverstatus"))
			{
				JSONObject serverResult = result.getJSONObject("result");			
				String strNumAlbums = serverResult.getString("info total albums");
				
				int iMaxAlbums = 0;
				iMaxAlbums = Integer.parseInt(strNumAlbums);
				
				// Get the list of players
				JSONArray playerList = serverResult.getJSONArray("players_loop");				
		
				m_playeridList.clear();
				
				for (int i = 0; i < playerList.length(); i++) 
				{
				    JSONObject row = playerList.getJSONObject(i);
				    String strPlayerId = row.getString("playerid");
				    m_playeridList.add(strPlayerId);
				}
				
				String strAlbumDetailListMessage = SendAlbumDetailListMessage(0, iMaxAlbums);
				new MyAsyncTask().execute(strAlbumDetailListMessage, strURL);
				
				//{"info total songs":22,"other player count":0,"lastscan":"1392642768","info total artists":2,"info total genres":2,"sn player count":0,"players_loop":[{"model":"http","seq_no":0,"name":"stagefright from 127.0.0.1","connected":1,"power":1,"canpoweroff":0,"playerid":"127.0.0.1","uuid":null,"isplayer":0,"ip":"127.0.0.1:61141"}],"player count":1,"info total albums":2,"uuid":"06592e63-6816-4243-8008-fc8852bf94b0","version":"7.7.3"}				
			}
			else
			if (params.contains("albums"))
			{
				//{"id":1,"result":{"albums_loop":[{"id":2,"year":0,"album":"James Blake- James Blake- [2011]","artist":"Music"},{"id":1,"year":2013,"artist":"James Blake","artwork_track_id":"7d10afd0","album":"Overgrown (Deluxe Edition)"}],"count":2},"method":"slim.request","params":[null,["albums","0",2,"tags:ljya"]]}
				
				JSONObject albumsResult = result.getJSONObject("result");
				JSONArray albumList = albumsResult.getJSONArray("albums_loop");
				
				m_albumList.clear();
				
				for (int i = 0; i < albumList.length(); i++) 
				{
				    JSONObject row = albumList.getJSONObject(i);
				    int iAlbumId = row.getInt("id");
				    int iAlbumYear = row.getInt("year");
				    String strAlbum = row.getString("album");
				    String strArtist = row.getString("artist");
				    String strArtworkId = null;
				    try
				    {
				    	strArtworkId = row.getString("artwork_track_id");
				    }
				    catch (Exception e)
				    {
						// TODO Auto-generated catch block
						e.printStackTrace();
				    }
				
				    CAlbum newAlbum = new CAlbum(iAlbumId, iAlbumYear, strAlbum, strArtist, strArtworkId);
				    m_albumList.add(newAlbum);
				}
			    RedrawAlbumList();
			    RegisterClickCallback();				
			}
			else
			if (params.contains("songs"))
			{
				//{"params":[null,["songs","0","100000","tags:psdtyJualekojwxNpg","album_id:1"]],"method":"slim.request","id":1,"result":{"count":11,"titles_loop":[{"id":7,"title":"Digital Lion","genre_id":"1","artist_id":"4","duration":"287.016","tracknum":"7","year":"2013","artwork_track_id":"7d10afd0","url":"file:///E:/Music/James%20Blake%20-%20Overgrown%20(Deluxe%20Edition)%202013/07%20James%20Blake%20-%20Digital%20Lion.mp3","artist":"James Blake","album":"Overgrown (Deluxe Edition)","album_id":"1","type":"mp3","coverart":"1","remote":"0","genre":"Dance"},{"id":6,"title":"Dlm","genre_id":"1","artist_id":"4","duration":"145.92","tracknum":"6","year":"2013","artwork_track_id":"7d10afd0","url":"file:///E:/Music/James%20Blake%20-%20Overgrown%20(Deluxe%20Edition)%202013/06%20James%20Blake%20-%20Dlm.mp3","artist":"James Blake","album":"Overgrown (Deluxe Edition)","album_id":"1","type":"mp3","coverart":"1","remote":"0","genre":"Dance"},{"id":11,"title":"Every Day I Ran (Bonus Track)","genre_id":"1","artist_id":"4","duration":"204.409","tracknum":"11","year":"2013","artwork_track_id":"7d10afd0","url":"file:///E:/Music/James%20Blake%20-%20Overgrown%20(Deluxe%20Edition)%202013/11%20James%20Blake%20-%20Every%20Day%20I%20Ran%20(Bonus%20Track).mp3","artist":"James Blake","album":"Overgrown (Deluxe Edition)","album_id":"1","comment":"(Bonus Track) / (Bonus Track)","type":"mp3","coverart":"1","remote":"0","genre":"Dance"},{"id":2,"title":"I Am Sold","genre_id":"1","artist_id":"4","duration":"244.476","tracknum":"2","year":"2013","artwork_track_id":"7d10afd0","url":"file:///E:/Music/James%20Blake%20-%20Overgrown%20(Deluxe%20Edition)%202013/02%20James%20Blake%20-%20I%20Am%20Sold.mp3","artist":"James Blake","album":"Overgrown (Deluxe Edition)","album_id":"1","type":"mp3","coverart":"1","remote":"0","genre":"Dance"},{"id":3,"title":"Life Round Here","genre_id":"1","artist_id":"4","duration":"217.117","tracknum":"3","year":"2013","artwork_track_id":"7d10afd0","url":"file:///E:/Music/James%20Blake%20-%20Overgrown%20(Deluxe%20Edition)%202013/03%20James%20Blake%20-%20Life%20Round%20Here.mp3","artist":"James Blake","album":"Overgrown (Deluxe Edition)","album_id":"1","type":"mp3","coverart":"1","remote":"0","genre":"Dance"},{"id":10,"title":"Our Love Comes Back","genre_id":"1","artist_id":"4","duration":"219.021","tracknum":"10","year":"2013","artwork_track_id":"7d10afd0","url":"file:///E:/Music/James%20Blake%20-%20Overgrown%20(Deluxe%20Edition)%202013/10%20James%20Blake%20-%20Our%20Love%20Comes%20Back.mp3","artist":"James Blake","album":"Overgrown (Deluxe Edition)","album_id":"1","type":"mp3","coverart":"1","remote":"0","genre":"Dance"},{"id":1,"title":"Overgrown","genre_id":"1","artist_id":"4","duration":"300.514","tracknum":"1","year":"2013","artwork_track_id":"7d10afd0","url":"file:///E:/Music/James%20Blake%20-%20Overgrown%20(Deluxe%20Edition)%202013/01%20James%20Blake%20-%20Overgrown.mp3","artist":"James Blake","album":"Overgrown (Deluxe Edition)","album_id":"1","type":"mp3","coverart":"1","remote":"0","genre":"Dance"},{"id":5,"title":"Retrograde","genre_id":"1","artist_id":"4","duration":"223.596","tracknum":"5","year":"2013","artwork_track_id":"7d10afd0","url":"file:///E:/Music/James%20Blake%20-%20Overgrown%20(Deluxe%20Edition)%202013/05%20James%20Blake%20-%20Retrograde.mp3","artist":"James Blake","album":"Overgrown (Deluxe Edition)","album_id":"1","type":"mp3","coverart":"1","remote":"0","genre":"Dance"},{"id":4,"title":"Take a Fall For Me (feat. RZA)","genre_id":"1","artist_id":"4","duration":"213.954","tracknum":"4","year":"2013","artwork_track_id":"7d10afd0","url":"file:///E:/Music/James%20Blake%20-%20Overgrown%20(Deluxe%20Edition)%202013/04%20James%20Blake%20-%20Take%20a%20Fall%20For%20Me%20(feat.%20RZA).mp3","artist":"James Blake","album":"Overgrown (Deluxe Edition)","album_id":"1","type":"mp3","coverart":"1","remote":"0","genre":"Dance"},{"id":9,"title":"To the Last","genre_id":"1","artist_id":"4","duration":"259.686","tracknum":"9","year":"2013","artwork_track_id":"7d10afd0","url":"file:///E:/Music/James%20Blake%20-%20Overgrown%20(Deluxe%20Edition)%202013/09%20James%20Blake%20-%20To%20the%20Last.mp3","artist":"James Blake","album":"Overgrown (Deluxe Edition)","album_id":"1","type":"mp3","coverart":"1","remote":"0","genre":"Dance"},{"id":8,"title":"Voyeur","genre_id":"1","artist_id":"4","duration":"257.748","tracknum":"8","year":"2013","artwork_track_id":"7d10afd0","url":"file:///E:/Music/James%20Blake%20-%20Overgrown%20(Deluxe%20Edition)%202013/08%20James%20Blake%20-%20Voyeur.mp3","artist":"James Blake","album":"Overgrown (Deluxe Edition)","album_id":"1","type":"mp3","coverart":"1","remote":"0","genre":"Dance"}]}}				
				
				JSONObject songResult = result.getJSONObject("result");
				JSONArray songList = songResult.getJSONArray("titles_loop");
				
				m_songList.clear();
				
				for (int i = 0; i < songList.length(); i++) 
				{
				    JSONObject row = songList.getJSONObject(i);
				    
					int iId = row.getInt("id");
					String strSongTitle = row.getString("title");
					int iGenreId = row.getInt("genre_id");
					int iArtistId = row.getInt("artist_id");
					double dDuration = row.getDouble("duration");
					int iTrackNum = row.getInt("tracknum");
					int iYear = row.getInt("year");
					String urlLocation = row.getString("url");
					String strArtist = row.getString("artist");
					String strAlbum = row.getString("album");
					int iAlbumId = row.getInt("album_id");
					String strType = row.getString("type");
					int iCoverartId = row.getInt("coverart");
					int iRemote = row.getInt("remote");
					String strGenre = row.getString("genre");
					
				    String strArtworkId = null;
				    try
				    {
				    	strArtworkId = row.getString("artwork_track_id");
				    }
				    catch (Exception e)
				    {
						// TODO Auto-generated catch block
						e.printStackTrace();
				    }
				
				    CSong newSong = new CSong(iId, strSongTitle, iGenreId, iArtistId, dDuration, iTrackNum, iYear, strArtworkId, urlLocation, strArtist, strAlbum, iAlbumId, strType, iCoverartId, iRemote, strGenre);
				    
				    m_songList.add(newSong);
				}
				
			    Collections.sort(m_songList);
			    
			    RedrawTrackList();
			    RegisterTrackClickCallback();
			    
			    // TO DO 
			    // Draw the track list and register callback
			    
			}
			else
			if (params.contains("playlist") && params.contains("play"))
			{
				if (m_mediaPlayer.isPlaying())
				{
					m_mediaPlayer.stop();
					//m_mediaPlayer.reset();
				}
				
				try {
					String strURLMP3Stream = String.format(strURLMP3StreamFormat, m_strHostName, m_iPortNumber);
					m_mediaPlayer.setDataSource(strURLMP3Stream);					
					m_mediaPlayer.prepare();
				} catch (IllegalStateException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				m_mediaPlayer.start();
				m_mediaPlayer.setVolume(1.0f, 1.0f);
				
			}
			progressBar.setVisibility(View.GONE);
			System.out.println(result.toString(2));
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} 
	 }
	 
	 
	 
	 private void RegisterTrackClickCallback() 
	 {
		// TODO Auto-generated method stub
		 ListView list = (ListView) findViewById(R.id.albumsListView);
		 list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
			 @Override
			 public void onItemClick(AdapterView<?> parent, View viewClicked, int position, long id)
			 {
				 CSong pickedSong = m_songList.get(position);
				 String message = "You clicked position " + position + " which is song "
						 + pickedSong.m_strSongTitle;
				 Toast.makeText(MainActivity.this, message, Toast.LENGTH_LONG).show();
				 
				 for (int i = 0; i < m_playeridList.size(); i++) 
				 {
					 String strPlayerId = m_playeridList.get(i);

					String strTrackPlayMessage = MainActivity.SendTrackPlayMessage(strPlayerId, 0, 100000, pickedSong);
					new MyAsyncTask().execute(strTrackPlayMessage, strURL);					 
				 }
			 }
		 });	 
	}
	 
	
	 public static String SendTrackPlayMessage(String strPlayerId, int iStart, int iItemsPerResponse, CSong song)
	 {
		 MainActivity.progressBar.setVisibility(View.VISIBLE);
		 // Send play message with track to play
		 JSONObject object = new JSONObject();
		 
		 String paramStr = String.format("[\"%s\",[\"playlist\",\"play\",\"%s\"]]", strPlayerId, song.m_urlLocation);
		 
		 try {
			 object.put("id", 1);
			 object.put("method", "slim.request");
			 object.put("params", paramStr);
		 } catch(JSONException e) {
			 e.printStackTrace();
		 }

		 System.out.println(object);
		
		 String strJsonMessage = object.toString();
		 String strJsonMessageResult = strJsonMessage.replace("\\","");
		 strJsonMessage = strJsonMessageResult;
		 strJsonMessageResult = strJsonMessage.replace("\"[","[");
		 strJsonMessage = strJsonMessageResult;
		 strJsonMessageResult = strJsonMessage.replace("]\"","]");		 
		 
		 Log.w("STU", strJsonMessageResult); 
		 
		 return strJsonMessageResult;		 	 
	 }
	 	 
	 
	 private void RedrawTrackList()
	 {
		 ArrayAdapter<CSong> adapter = new CTrackListAdapter(this, m_songList);
		 ListView list = (ListView) findViewById(R.id.albumsListView);
		 list.setVisibility(View.VISIBLE);
		 list.setAdapter(adapter);
	 }
	 
	private void RedrawAlbumList()
	 {
		 ArrayAdapter<CAlbum> adapter = new CMyListAdapter();
		 ListView list = (ListView) findViewById(R.id.albumsListView);
		 list.setVisibility(View.VISIBLE);
		 list.setAdapter(adapter);
	 }

	
	
	 private class CMyListAdapter extends ArrayAdapter<CAlbum>
	 {
	    public ImageLoader imageLoader;
	    
		public CMyListAdapter(){
			super(MainActivity.this, R.layout.item_view, m_albumList); 
	        imageLoader=new ImageLoader(MainActivity.this);
		 }

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
	 }
	 

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
	 
	 //Request artists
	 //{"id":1,"method":"slim.request","params":[null,["artists","0","2"]]}
	 
	 //Request players
	 // {"id":1,"method":"slim.request","params":[null,["players","0","100000"]]}
	 
	 //Request album details
	 //{"id":1,"method":"slim.request","params":[null,["songs","0","100000","tags:psdtyJualekojwxNpg","album_id:1"]]}	 
	 
	 // Request songs from album 
	 //{"id":1,"method":"slim.request","params":[null,["songs","0","100000","tags:psdtyJualekojwxNpg","album_id:1"]]}

	 public static String SendSongListMessage(int iStart, int iItemsPerResponse, CAlbum album)
	 {
		 // Request songs from album 
		 //{"id":1,"method":"slim.request","params":[null,["songs","0","100000","tags:psdtyJualekojwxNpg","album_id:1"]]}
		 MainActivity.progressBar.setVisibility(View.VISIBLE);
		 JSONObject object = new JSONObject();
		 
		 String paramStr = String.format("[null,[\"songs\",%d,%d,\"tags:psdtyJualekojwxNpg\",\"album_id:%d\"]]", iStart, iItemsPerResponse, album.m_iId);
		 
		 try {
			 object.put("id", 1);
			 object.put("method", "slim.request");
			 object.put("params", paramStr);
		 } catch(JSONException e) {
			 e.printStackTrace();
		 }

		 System.out.println(object);
		
		 String strJsonMessage = object.toString();
		 String strJsonMessageResult = strJsonMessage.replace("\\","");
		 strJsonMessage = strJsonMessageResult;
		 strJsonMessageResult = strJsonMessage.replace("\"[","[");
		 strJsonMessage = strJsonMessageResult;
		 strJsonMessageResult = strJsonMessage.replace("]\"","]");		 
		 
		 Log.w("STU", strJsonMessageResult); 
		 
		 return strJsonMessageResult;		 
	 }
	 
	 private String SendAlbumDetailListMessage(int iStart, int iItemsPerResponse)
	 {
		 //Request Albums
		 //{"id":1,"method":"slim.request","params":[null,["albums","0","2","tags:ljya"]]}
		 MainActivity.progressBar.setVisibility(View.VISIBLE);
		 JSONObject object = new JSONObject();
		 
		 String paramStr = String.format("[null,[\"albums\",%d,%d,\"tags:ljya\"]]", iStart, iItemsPerResponse);
		 
		 try {
			 object.put("id", 1);
			 object.put("method", "slim.request");
			 object.put("params", paramStr);
		 } catch(JSONException e) {
			 e.printStackTrace();
		 }

		 System.out.println(object);
		
		 String strJsonMessage = object.toString();
		 String strJsonMessageResult = strJsonMessage.replace("\\","");
		 strJsonMessage = strJsonMessageResult;
		 strJsonMessageResult = strJsonMessage.replace("\"[","[");
		 strJsonMessage = strJsonMessageResult;
		 strJsonMessageResult = strJsonMessage.replace("]\"","]");		 
		 
		 Log.w("STU", strJsonMessageResult); 
		 
		 return strJsonMessageResult;		 
	 }
	 
	 private String SendServerStatusMessage(int iStart, int iItemsPerResponse)
	 {
		 MainActivity.progressBar.setVisibility(View.VISIBLE);
		 JSONObject object = new JSONObject();
		 
		 // {"id":1,"method":"slim.request","params":["",["serverstatus",0,999]]}
		 
		 
		 String paramStr = String.format("[\"\",[\"serverstatus\",%d,%d]]", iStart, iItemsPerResponse);
		 
		 try {
			 object.put("id", 1);
			 object.put("method", "slim.request");
			 object.put("params", paramStr);
		 } catch(JSONException e) {
			 e.printStackTrace();
		 }

		 System.out.println(object);
		
		 String strJsonMessage = object.toString();
		 String strJsonMessageResult = strJsonMessage.replace("\\","");
		 strJsonMessage = strJsonMessageResult;
		 strJsonMessageResult = strJsonMessage.replace("\"[","[");
		 strJsonMessage = strJsonMessageResult;
		 strJsonMessageResult = strJsonMessage.replace("]\"","]");		 
		 
		 Log.w("STU", strJsonMessageResult); 
		 
		 return strJsonMessageResult;
	 }
	 
	 private String convertStreamToString(InputStream is) {
		    String line = "";
		    StringBuilder total = new StringBuilder();
		    BufferedReader rd = new BufferedReader(new InputStreamReader(is));
		    try {
		        while ((line = rd.readLine()) != null) {
		            total.append(line);
		        }
		    } catch (Exception e) {
		    Toast.makeText(this, "Stream Exception", Toast.LENGTH_SHORT).show();
		    }
		return total.toString();
		}
	 
	 
		private class MyAsyncTask extends AsyncTask<String, Integer, JSONObject>
		{
			
			@Override
			protected JSONObject doInBackground(String... params) {
			// TODO Auto-generated method stub
				JSONObject retVal = null;
				try {
					retVal = postData(params[0], params[1]);
					
				} catch (IllegalStateException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				return retVal;
			}
			 
			protected void onPostExecute(JSONObject result)
			{
				progressBar.setVisibility(View.GONE);
				Toast.makeText(getApplicationContext(), "command sent", Toast.LENGTH_LONG).show();
				ProcessJSONResponse(result);
			}
			protected void onProgressUpdate(Integer... progress)
			{
				progressBar.setProgress(progress[0]);
			}
			 
			public JSONObject postData(String valueIWantToSend, String strUrl) throws IllegalStateException, IOException 
			{
				// Create a new HttpClient and Post Header

				 DefaultHttpClient httpclient = new DefaultHttpClient();
				 HttpPost httppostreq = new HttpPost(strUrl);
				 StringEntity se;

				try {
		 			se = new StringEntity(valueIWantToSend);
					 se.setContentType("application/json;charset=UTF-8");
					 se.setContentEncoding(new BasicHeader(HTTP.CONTENT_TYPE, "application/json;charset=UTF-8"));
					 httppostreq.setEntity(se);
				} catch (UnsupportedEncodingException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}

				HttpResponse httpresponse = null;
				
				//try {
				 try {
					httpresponse = httpclient.execute(httppostreq);
				} catch (ClientProtocolException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}		
			
				 HttpEntity resultentity = httpresponse.getEntity();
				 InputStream inputstream = resultentity.getContent();
				 org.apache.http.Header contentencoding = httpresponse.getFirstHeader("Content-Encoding");
				 if(contentencoding != null && contentencoding.getValue().equalsIgnoreCase("gzip")) {
				     inputstream = new GZIPInputStream(inputstream);
				 }
				 String resultstring = convertStreamToString(inputstream);
				 inputstream.close();
				 
				 JSONObject recvdjson = null;
				 
				 try {
					recvdjson = new JSONObject(resultstring);
					System.out.println(recvdjson.toString(2));
				} catch (JSONException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				return recvdjson;
			}
	 
		}
}
