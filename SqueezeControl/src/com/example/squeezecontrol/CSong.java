package com.example.squeezecontrol;

public class CSong implements Comparable{

	private String m_strSongArtFormat = new String("http://%s:%d/music/%s/cover_%dx%d.jpg");
	private static int m_iLargeIconSize = 350;
	private static int m_iSmallIconSize = 75;

	int		m_iId;
	String	m_strSongTitle;
	int 	m_iGenreId;
	int 	m_iArtistId;
	double 	m_dDuration;
	int 	m_iTrackNum;
	int 	m_iYear;
	String	m_strArtworkId;
	String	m_strArtworkUrl;
	String	m_strArtworkUrlSmall;
	String	m_urlLocation;
	String	m_strArtist;
	String	m_strAlbum;
	int 	m_iAlbumId;
	String 	m_strType;
	int 	m_iCoverartId;
	int 	m_iRemote;
	String 	m_strGenre;	
	
	public CSong(int m_iId, String m_strSongTitle,
			int m_iGenreId, int m_iArtistId, double m_dDuration,
			int m_iTrackNum, int m_iYear, String m_strArtworkId,
			String m_urlLocation, String m_strArtist, String m_strAlbum,
			int m_iAlbumId, String m_strType, int m_iCoverartId, int m_iRemote,
			String m_strGenre) 
	{
		super();
		this.m_iId = m_iId;
		this.m_strSongTitle = m_strSongTitle;
		this.m_iGenreId = m_iGenreId;
		this.m_iArtistId = m_iArtistId;
		this.m_dDuration = m_dDuration;
		this.m_iTrackNum = m_iTrackNum;
		this.m_iYear = m_iYear;
		this.m_strArtworkId = m_strArtworkId;
		this.m_urlLocation = m_urlLocation;
		this.m_strArtist = m_strArtist;
		this.m_strAlbum = m_strAlbum;
		this.m_iAlbumId = m_iAlbumId;
		this.m_strType = m_strType;
		this.m_iCoverartId = m_iCoverartId;
		this.m_iRemote = m_iRemote;
		this.m_strGenre = m_strGenre;
		
		this.m_strArtworkUrl = GetLargeAlbumArtwork();
		this.m_strArtworkUrlSmall = GetSmallAlbumArtwork();		
	}
	
	private String GetAlbumArtPath(int iSize)
	{
		String strArtworkId = "0";
		if (m_strArtworkId != null)
			strArtworkId = m_strArtworkId;
		
		String strAlbumArtPath = String.format(m_strSongArtFormat, MainActivity.m_strUserHostName, MainActivity.m_iUserPortNumber, strArtworkId, iSize, iSize);
		return strAlbumArtPath;
	}
	
	private String GetLargeAlbumArtwork()
	{
		return GetAlbumArtPath(m_iLargeIconSize);
	}

	private String GetSmallAlbumArtwork()
	{
		return GetAlbumArtPath(m_iSmallIconSize);
	}
	
	@Override
	public int compareTo(Object another) 
	{
		CSong song = (CSong)another;
		
		if (m_iId > song.m_iId)
		{
			return 1;
		}
		else
		if (m_iId < song.m_iId)
		{
			return -1;
		}
		
		// TODO Auto-generated method stub
		return 0;
	}

	
	
}
