/*
 * This file is part of TaskMan
 *
 * Copyright (C) 2012 Jed Barlow, Mark Galloway, Taylor Lloyd, Braeden Petruk
 *
 * TaskMan is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * TaskMan is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with TaskMan.  If not, see <http://www.gnu.org/licenses/>.
 */

package ca.cmput301.team13.taskman.model.storage;

import java.io.ByteArrayOutputStream;
import java.io.Serializable;
import java.util.Date;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.graphics.Bitmap;
import android.util.Log;
import ca.cmput301.team13.taskman.model.storage.Requirement.contentType;

/**
 * Represents a fulfillment to a task requirement;
 * aggregated by {@link Requirement}.
 */
public class Fulfillment extends BackedObject implements Serializable {

	private static final long serialVersionUID = -455954352936661277L;
	private contentType content;
    private Bitmap image;
    private String text;
    private short[] audioBuffer;
    private short[] videoBuffer;

    /**
     * Constructor to initialize with no actual data
     * @param id The id of the fulfillment
     * @param created Timestamp of when this object is created
     * @param lastModified Last time this object was modified
     * @param content ContentType of this fulfillment (inherited)
     * @param creator User that created this fulfillment
     * @param repo Link to the Repository
     */
    Fulfillment(String id, Date created, Date lastModified, contentType content, User creator, VirtualRepository repo) {
        super(id, created, lastModified, creator, repo);
        this.content = content;
    }

    /**
     * Constructor to initialize with an Image
     * @param id The id of the fulfillment
     * @param created Timestamp of when this object is created
     * @param lastModified Last time this object was modified
     * @param image The image represented by this fulfillment
     * @param creator User that created this fulfillment
     * @param repo Link to the Repository
     */
    public Fulfillment(String id, Date created, Date lastModified, Bitmap image, User creator, VirtualRepository repo) {
        super(id, created, lastModified, creator, repo);
        this.content = contentType.image;
        this.image = image;
    }

    /**
     * Constructor to initialize with Text
     * @param id The id of the fulfillment
     * @param created Timestamp of when this object is created
     * @param lastModified Last time this object was modified
     * @param content ContentType of this fulfillment (inherited)
     * @param creator User that created this fulfillment
     * @param repo Link to the Repository
     */
    public Fulfillment(String id, Date created, Date lastModified, String text, User creator, VirtualRepository repo) {
        super(id, created, lastModified, creator, repo);
        this.content = contentType.text;
        this.text = text;
    }

    /**
     * Constructor to initialize with Audio
     * @param id The id of the fulfillment
     * @param created Timestamp of when this object is created
     * @param lastModified Last time this object was modified
     * @param content ContentType of this fulfillment (inherited)
     * @param creator User that created this fulfillment
     * @param repo Link to the Repository
     */
    public Fulfillment(String id, Date created, Date lastModified, short[] buffer, contentType mediaType, User creator, VirtualRepository repo) {
        super(id, created, lastModified, creator, repo);
        this.content = mediaType;
        switch(mediaType) {
        	case audio:
        		this.audioBuffer = buffer;
        	break;
        	case video:
        		this.videoBuffer = buffer;
        	break;
        	default:
        		this.videoBuffer = null;
        		this.audioBuffer = null;
        }
    }
    
    public JSONObject toJSON() {
    	JSONObject json = new JSONObject();
    	try {
    		json.put("id", getId());
    		json.put("parentId", getParentId());
    		json.put("parentWebID", getParentWebID());
    		json.put("created", dateFormat.format(getCreatedDate()));
    		json.put("lastModified", dateFormat.format(getLastModifiedDate()));
			json.put("contentType", content.ordinal());
			json.put("creator", getCreator().toString());
			//Serialize the contained data
			switch(content) {
				case text:
					json.put("data", text);
				break;
				case audio:
					JSONArray audioByteArray = new JSONArray();
					for(int i=0; i<audioBuffer.length; i++) {
						audioByteArray.put((int)audioBuffer[i]);
					}
					json.put("data", audioByteArray);
				break;
				case image:
					ByteArrayOutputStream s = new ByteArrayOutputStream();
					image.compress(Bitmap.CompressFormat.PNG, 100, s);
					byte[] bitmapBytes = s.toByteArray();
					JSONArray bitmapByteArray = new JSONArray();
					for(int i=0; i<bitmapBytes.length; i++) {
						bitmapByteArray.put((int)bitmapBytes[i]);
					}
					json.put("data", bitmapByteArray);
				break;
				case video:
					JSONArray videoByteArray = new JSONArray();
					for(int i=0; i<videoBuffer.length; i++) {
						videoByteArray.put((int)videoBuffer[i]);
					}
					json.put("data", videoByteArray);
				break;
			}
		} catch (JSONException e) {
			e.printStackTrace();
		}
    	return json;
    }
    
    /**
     * Update this Fulfillment's mutable properties from the
     * provided Fulfillment
     * @param f
     */
    public boolean loadFromFulfillment(Fulfillment f) {
    	delaySaves(true);
    	setLastModifiedDate(f.getLastModifiedDate());
    	switch(content) {
	    	case text:
				setText(f.getText());
			break;
			case audio:
				setAudio(f.getAudio());
			break;
			case image:
				setImage(f.getImage());
			break;
			case video:
				setVideo(f.getVideo());
			break;
    	}
    	setWebID(f.getWebID());
    	setIsLocal(f.getIsLocal());
    	return this.delaySaves(false, false);
    }

    //TODO: constructor for video
    /**
     * Returns the image content associated with this fulfillment.
     * @return the {@link android.graphics.Bitmap} content associated with the fulfillment.
     */
    public Bitmap getImage() {
        if(content != contentType.image) {
            Log.w("Fulfillment(ID:"+getId()+")", "getImage() called on non-image Fulfillment");
            return null;
        }
        return image;
    }

    /**
     * Return the text content associated with this fulfillment.
     * @return the string content associated with the fulfillment.
     */
    public String getText() {
        if(content != contentType.text) {
            Log.w("Fulfillment(ID:"+getId()+")", "getText() called on non-text Fulfillment");
            return null;
        }
        return text;
    }

    /**
     * Returns the audio data associated with the fulfillment.
     * @return the audio data associated with the fulfillment
     */
    public short[] getAudio() {
        if(content != contentType.audio) {
            Log.w("Fulfillment(ID:"+getId()+")", "getAudio() called on non-audio Fulfillment");
            return null;
        }
        return audioBuffer;
    }

    /**
     * Returns the video data associated with the fulfillment.
     * @return the video data associated with the fulfillment
     */
    public short[] getVideo() {
        if(content != contentType.video) {
            Log.w("Fulfillment(ID:"+getId()+")", "getVideo() called on non-video Fulfillment");
            return null;
        }
        return videoBuffer;
    }
    
    /**
     * Sets the image content associated with the fulfillment.
     * @param image the {@link android.graphics.Bitmap} to associate with the fulfillment
     */
    public void setImage(Bitmap image) {
        if(content != contentType.image) {
            Log.w("Fulfillment(ID:"+getId()+")", "setImage() called on non-image Fulfillment");
            return;
        }
        this.image = image;
        saveChanges();
    }

    /**
     * Sets the text content associated with the fulfillment.
     * @param text the string of text content to associate with the fulfillment
     */
    public void setText(String text) {
        if(content != contentType.text) {
            Log.w("Fulfillment(ID:"+getId()+")", "setText() called on non-text Fulfillment");
            return;
        }
        this.text = text;
        saveChanges();
    }

    /**
     * Sets the audio data associated with the fulfillment.
     * @param buffer the audio data to associate with the fulfillment
     */
    public void setAudio(short[] buffer) {
        if(content != contentType.audio) {
            Log.w("Fulfillment(ID:"+getId()+")", "setAudio() called on non-audio Fulfillment");
            return;
        }
        this.audioBuffer = buffer;
        saveChanges();
    }
    
    /**
     * Sets the video data associated with the fulfillment.
     * @param buffer the video data to associate with the fulfillment
     */
    public void setVideo(short[] buffer) {
        if(content != contentType.video) {
            Log.w("Fulfillment(ID:"+getId()+")", "setVideo() called on non-video Fulfillment");
            return;
        }
        this.videoBuffer = buffer;
        saveChanges();
    }

    /**
     * Returns the content type of the fulfillment.
     * @return the content type of the fulfillment
     */
    public contentType getContentType() {
        return content;
    }
    
    public int getOrderValue() {
    	return 3;
    }

    /**
     * Returns a string representation of the fulfillment.
     * @return a string representation of the fulfillment
     */
    @Override
    public String toString() {
        return "Fulfillment(ID:"+getId()+")";
    }

}
