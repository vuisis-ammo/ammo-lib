/*Copyright (C) 2010-2012 Institute for Software Integrated Systems (ISIS)
This software was developed by the Institute for Software Integrated
Systems (ISIS) at Vanderbilt University, Tennessee, USA for the 
Transformative Apps program under DARPA, Contract # HR011-10-C-0175.
The United States Government has unlimited rights to this software. 
The US government has the right to use, modify, reproduce, release, 
perform, display, or disclose computer software or computer software 
documentation in whole or in part, in any manner and for any 
purpose whatsoever, and to have or authorize others to do so.
*/
package edu.vu.isis.ammo.api.type;

import java.util.ArrayList;
import java.util.List;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * The Notice is used to specify intents to generate
 * as certain thresholds are crossed, and the 
 * delivery mode to use.
 * 
 * A Notice.Item consists of two parts:
 *  the [[threshold]], which may trigger the notification, and 
 *  the associated [[mode]].
 *  
 */

public class Notice extends AmmoType  {
      
	public class Item {	   
		public final Threshold threshold;
		public final Via mode;
		
		private Item(Threshold threshold, Via mode) {
			this.threshold = threshold;
			this.mode = mode;
		}

		@Override
		public String toString() {
		return new StringBuilder()
			.append("threshold [").append(this.threshold).append("], ")
			.append("mode [").append(this.mode).append("]")
			.toString();
		}
	}
	
	public static Notice newInstance() {
		return new Notice();
	}

	/**
	 * 
	\paragraph
	The request progresses through the system.   
	As it does, it crosses certain thresholds.
	These thresholds specify triggers where acknowledgements may be generated.
	\begin{table}[h]
	\center
	\begin{tabular}{rl}
	  Name & Meaning \\ \hline
	  NONE         & placed under the control of the distributor \\
	  SENT         & sent over a channel \\
	  DISPATCHED   & placed under the control of an Android plugin \\
	  DELIVERED    & a plugin acknowledges delivery of a message  \\
	  RECEIVED     & a target device acknowledges receipt of a message \\
	\end{tabular}
	\caption{message thresholds indicating progress}
	\end{table}
	
	*/
    public enum Threshold { 
      SENT, DISPATCHED, 
      DELIVERED, RECEIVED 
    };
    
    final private List<Item> items;
 	private Item whenSent; 
 	private Item whenDispatched;
 	private Item whenDelivered; 
 	private Item whenReceived;
 	
 	public Item whenSent() { return whenSent; }
 	public Item whenDispatched() { return whenDispatched; }
 	public Item whenDelivered() { return whenDelivered; }
 	public Item whenReceived() { return whenReceived; }
	
	public Item setItem(Threshold threshold, Via via) {
		final Item item = new Item(threshold, via);
		//this.items.add(item);
		switch (threshold) {
		case SENT:
			whenSent = item;
			break;
		case DISPATCHED:
			whenDispatched = item;
			break;
		case DELIVERED:
			whenDelivered = item;
			break;
		case RECEIVED:
			whenReceived = item;
		    break;
		}
		return item;
	}
	
    /**
     * 
    \paragraph
    As acknowledgments of the message are generated they
    will contain the threshold status.

    \begin{table}[h]
    \center
    \begin{tabular}{rl}
      Name & Meaning \\ \hline
      SUCCESS   & the place was reached without incident \\
      FAIL      & the place was reached, but the request failed and will be canceled   \\
      UNKNOWN   & the request is of an indeterminate disposition \\
      REJECTED  & the place rejected the request, another may yet accept it \\
    \end{tabular}
    \caption{message acknowledgment states}
    \end{table}
    */
    public enum DeliveryState { SUCCESS, FAIL,  UNKNOWN, REJECTED };
    
    /**
     *  
	As the request reaches the places mentioned,
	it will be marked in one of several delivery states.
	These are all final delivery state, with the exception of "REJECTED".
	"REJECTED" is mutable because although one target rejects a task, 
	another receiver may accept.
	
	\begin{description}
	  \item[ACTIVITY] start an activity with the intent
	  \item[BROADCAST] broadcast the intent  
	  \item[SERVICE] start a service with the intent
	\end{description}
	
	The structure of the intent varies based on the NoticeThreshold type.
	*/
    
    public enum Via { NONE, ACTIVITY, BROADCAST, SERVICE };
 

	// *********************************
	// Parcelable Support
	// *********************************

	public static final Parcelable.Creator<Notice> CREATOR = 
			new Parcelable.Creator<Notice>() {

		@Override
		public Notice createFromParcel(Parcel source) {
			return new Notice(source);
		}
		@Override
		public Notice[] newArray(int size) {
			return new Notice[size];
		}
	};
	
	public static Notice readFromParcel(Parcel source) {
		if (AmmoType.isNull(source)) return null;
		return new Notice(source);
	}

	@Override
	public void writeToParcel(Parcel dest, int flags) {
		plogger.trace("marshall notice {}", this);
		dest.writeInt(this.items.size());
		
		for (Item item : this.items ) {
			dest.writeInt(item.threshold.ordinal());
			dest.writeInt(item.mode.ordinal());
		}
	}

	private Notice(Parcel in) {
		final int count = in.readInt();
		this.items = new ArrayList<Item>(count);
		for (int ix = 0; ix < count; ++ix) {
			final Threshold threshold = Threshold.values()[in.readInt()];
			final Via via = Via.values()[in.readInt()];
			this.items.add(this.setItem(threshold, via));
		}
		plogger.trace("unmarshall notice {}", this);
	}

	// *********************************
	// Standard Methods
	// *********************************
	@Override
	public String toString() {
		final StringBuilder sb = new StringBuilder();
	        sb.append("notice :");
		for (Item item : this.items){ 
		    sb.append('\n')
		      .append("@ [").append(item.threshold).append("]")
			  .append("->[").append(item.mode).append("]");
		}
		return sb.toString();
	}

	// *********************************
	// IAmmoReques Support
	// *********************************

	private Notice() {
		this.items = new ArrayList<Item>();
		this.setItem(Threshold.SENT, Via.NONE);
	 	this.setItem(Threshold.DISPATCHED, Via.NONE);
	 	this.setItem(Threshold.DELIVERED, Via.NONE);
	 	this.setItem(Threshold.RECEIVED, Via.NONE);
	}

}
