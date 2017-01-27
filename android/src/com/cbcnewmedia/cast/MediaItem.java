package com.cbcnewmedia.cast;

import java.util.ArrayList;

import android.net.Uri;

import com.google.android.gms.cast.MediaInfo;
import com.google.android.gms.cast.MediaMetadata;
import com.google.android.gms.common.images.WebImage;


public class MediaItem {
	   	
	private String title;
    private String subTitle;
    private String studio;
    private String url;
    private String contentType;
    private long duration;
    private ArrayList<String> imageList = new ArrayList<String>();
    
	public String getTitle() { return title; }
	public void setTitle(String title) { this.title = title; }
	
	public String getSubTitle() { return subTitle; }
	public void setSubTitle(String subTitle) { this.subTitle = subTitle; }
	
	public String getStudio() { return studio; }
	public void setStudio(String studio) { this.studio = studio; }
	
	public String getUrl() { return url; }
	public void setUrl(String url) { this.url = url; }
	
	public String getContentType() { return contentType; }
	public void setContentType(String contentType) { this.contentType = contentType; }
	
	public long getDuration() { return duration; }
	public void setDuration(int duration) { this.duration = duration; }
	
	public ArrayList<String> getImageList() { return imageList; }
	public void setImageList(ArrayList<String> imageList) { this.imageList = imageList; }
	
	public MediaInfo buildMediaInfo()
	{
		MediaMetadata metadata = new MediaMetadata();
		for(String image : imageList)
		{
			WebImage webImage = new WebImage(Uri.parse(image));
			metadata.addImage(webImage);
		}
		
		metadata.putString(MediaMetadata.KEY_TITLE, title);
		metadata.putString(MediaMetadata.KEY_SUBTITLE, title);
		
		int streamType =  duration < 0 ? MediaInfo.STREAM_TYPE_LIVE : MediaInfo.STREAM_TYPE_BUFFERED;
		long streamDuration = duration < 0 ? MediaInfo.UNKNOWN_DURATION : duration;
			
		return new MediaInfo.Builder(url)
		.setStreamType(streamType)
		.setContentType(contentType)
		.setMetadata(metadata)
		.setStreamDuration(streamDuration)
		.build();
	}
}
