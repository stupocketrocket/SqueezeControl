package com.example.squeezecontrol;

import org.json.JSONObject;

public class CAlbum {
	
	private String m_strAlbumArtFormat = new String("http://%s:%d/music/%s/cover_%dx%d.jpg");
	
	private static int m_iLargeIconSize = 350;
	private static int m_iSmallIconSize = 75;
	
	int	m_iId;
	int m_iYear;
	String	m_strAlbumName;
	String	m_strArtist;
	String	m_strArtworkId;
	String	m_strArtworkUrl;
	String	m_strArtworkUrlSmall;
	
	
	public CAlbum(int m_iId, int m_iYear, String m_strAlbumName,
			String m_strArtist, String m_strArtworkId) 
	{
		super();
		this.m_iId = m_iId;
		this.m_iYear = m_iYear;
		this.m_strAlbumName = m_strAlbumName;
		this.m_strArtist = m_strArtist;
		this.m_strArtworkId = m_strArtworkId;
		
		this.m_strArtworkUrl = GetLargeAlbumArtwork();
		this.m_strArtworkUrlSmall = GetSmallAlbumArtwork();
	}

	private String GetAlbumArtPath(int iSize)
	{
		String strArtworkId = "0";
		if (m_strArtworkId != null)
			strArtworkId = m_strArtworkId;
		
		String strAlbumArtPath = String.format(m_strAlbumArtFormat, MainActivity.m_strHostName, MainActivity.m_iPortNumber, strArtworkId, iSize, iSize);
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

}
