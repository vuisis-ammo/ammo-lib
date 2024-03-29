/* Copyright (c) 2010-2015 Vanderbilt University
 * 
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 * 
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 * 
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */

package edu.vu.isis.ammo.api.type;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicBoolean;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.BadParcelableException;
import android.os.Parcel;
import android.os.Parcelable;

/**
 * The Notice is used to specify intents to generate as certain thresholds are
 * crossed, and the delivery mode to use.
 * <p>
 * A Notice.Item consists of two parts:
 * <p>
 * the [[threshold]], which may trigger the notification, and the associated
 * [[mode]].
 * <p>
 * <code>
 * final AmmoRequest.Builder ab = AmmoRequest.newBuilder(); 
 * final AmmoRequest ar = ab .topic("flintstones", "wilma")
 *      .provider("content://flintstones/characters/wilma")
 *      .notice(Notice.Threshold.SENT, Notice.Via.BROADCAST)
 *      .post(); 
 *      </code>
 * <p>
 * When the message is sent (leaves the handheld) the following broadcast intent
 * will be generated.
 * <p>
 * <code>
 * final Intent notice = new Intent()
 *      .setAction(ACTION_MSG_SENT)
 *      .setData(Uri.Builder()
 *                .scheme("ammo") 
 *                .authority(ack.topic)
 *                .path(ack.subtopic) 
 *                .build()) 
 *      .putExtra(EXTRA_STATUS, ack.status.toString())
 *      .putExtra(EXTRA_TOPIC, ack.topic.toString())
 *      .putExtra(EXTRA_UID, ack.auid.toString()) 
 *      .putExtra(EXTRA_CHANNEL, ack.channel.toString()); 
 *      </code>
 * <p>
 * For more examples for each of the intents generated see Threshold. Depending
 * on the Via the intent will be sent (see Via).
 */

public class Notice extends AmmoType {
	static final Logger logger = LoggerFactory.getLogger("type.notice");

	static final public Notice RESET = null;

	/**
	 * The following constants are used to produce intents.
	 */
	public static final String ACTION_MSG_SENT = "edu.vu.isis.ammo.ACTION_MESSAGE_SENT";
	public static final String ACTION_GW_DELIVERED = "edu.vu.isis.ammo.ACTION_MESSAGE_GATEWAY_DELIVERED";
	public static final String ACTION_HH_DELIVERED = "edu.vu.isis.ammo.ACTION_MESSAGE_DEVICE_DELIVERED";
	public static final String ACTION_PLUGIN_DELIVERED = "edu.vu.isis.ammo.ACTION_MESSAGE_PLUGIN_DELIVERED";

	public static final String EXTRA_TOPIC = "topic";
	public static final String EXTRA_SUBTOPIC = "subtopic";
	public static final String EXTRA_UID = "uid";
	public static final String EXTRA_CHANNEL = "channel";
	public static final String EXTRA_STATUS = "status";
	public static final String EXTRA_DEVICE = "device";
	public static final String EXTRA_OPERATOR = "operator";

	/**
	 * A class to build those intents which may be generated in response to the
	 * message crossing a noted threshold.
	 */
	public static IntentBuilder getIntentBuilder(final Notice notice) {
		return new IntentBuilder(notice);
	}

	static public class IntentBuilder {

		@SuppressWarnings("unused")
		private final Notice notice;
		private String topic = null;
		private String subtopic = null;
		private String auid = null;
		private String channel = null;
		private String status = null;
		private String device = null;
		private String operator = null;

		private IntentBuilder(final Notice notice) {
			this.notice = notice;
		}

		public IntentBuilder topic(final Topic topic) {
			this.topic = topic.toString();
			return this;
		}

		public IntentBuilder topic(final String topic) {
			this.topic = topic;
			return this;
		}

		public IntentBuilder subtopic(final Topic subtopic) {
			this.subtopic = subtopic.toString();
			return this;
		}

		public IntentBuilder subtopic(final String subtopic) {
			this.subtopic = subtopic;
			return this;
		}

		public IntentBuilder auid(final String auid) {
			this.auid = auid;
			return this;
		}

		public IntentBuilder channel(final String channel) {
			this.channel = channel;
			return this;
		}

		public IntentBuilder status(final String status) {
			this.status = status;
			return this;
		}

		public IntentBuilder device(final String device) {
			this.device = device;
			return this;
		}

		public IntentBuilder operator(final String operator) {
			this.operator = operator;
			return this;
		}

		public Intent buildSent(final Context context) {
			return this.build(context, ACTION_MSG_SENT);
		}

		public Intent buildDeviceDelivered(final Context context) {
			return this.build(context, ACTION_HH_DELIVERED);
		}

		public Intent buildGatewayDelivered(final Context context) {
			return this.build(context, ACTION_GW_DELIVERED);
		}

		public Intent buildPluginDelivered(final Context context) {
			return this.build(context, ACTION_PLUGIN_DELIVERED);
		}

		private Intent build(final Context context, final String action) {
			final Uri.Builder uriBuilder = new Uri.Builder().scheme("ammo")
					.authority(this.topic);

			if (this.subtopic != null)
				uriBuilder.path(this.subtopic);

			final Intent noticed = new Intent().setAction(action)
					.setData(uriBuilder.build())
					.putExtra(EXTRA_TOPIC, this.topic);

			if (this.subtopic != null)
				noticed.putExtra(EXTRA_SUBTOPIC, this.subtopic);

			if (this.auid != null)
				noticed.putExtra(EXTRA_UID, this.auid);

			if (this.channel != null)
				noticed.putExtra(EXTRA_CHANNEL, this.channel);

			if (this.status != null)
				noticed.putExtra(EXTRA_STATUS, this.status);

			if (this.device != null)
				noticed.putExtra(EXTRA_DEVICE, this.status);

			if (this.operator != null)
				noticed.putExtra(EXTRA_OPERATOR, this.operator);

			return noticed;
		}
	}

	/**
	 * Class for adding items to the notice. An item consists of a threshold
	 * which may be crossed and the aggregate of those intents to be generated
	 * when that happens.
	 */
	public class Item {
		public final Threshold threshold;
		private Via via;

		public Via getVia() {
			return via;
		}

		public void setVia(Via via) {
			this.dirtyHashcode.set(true);
			this.via = via;
		}

		private Item(Threshold threshold, Via via) {
			this.dirtyHashcode.set(true);
			this.threshold = threshold;
			this.via = via;
		}

		@Override
		public String toString() {
			return new StringBuilder()
                    .append("@").append(this.threshold)
					.append("->").append("[").append(this.via).append(']')
					.toString();
		}

		public void writeParcel(Parcel dest, int flags) {
			dest.writeInt(this.threshold.id);
			dest.writeInt(this.via.v);
		}

		/**
		 * check that the two objects are logically equal.
		 */
		@Override
		public boolean equals(Object obj) {
			if (this == obj)
				return true;
			if (!(obj instanceof Item))
				return false;
			final Item that = (Item) obj;
			if (!this.threshold.equals(that.threshold))
				return false;
			if (!this.via.equals(that.via))
				return false;
			return true;
		}

		protected volatile int hashcode = 0;
		protected AtomicBoolean dirtyHashcode = new AtomicBoolean(true);

		@Override
		public synchronized int hashCode() {
			if (!this.dirtyHashcode.getAndSet(false))
				return this.hashcode;
			this.hashcode = AmmoType.HashBuilder.newBuilder()
                    .increment(this.threshold)
                    .increment(this.via)
                    .hashCode();
			return this.hashcode;
		}
	}

	public static Notice newInstance() {
		return new Notice();
	}

	/**
	 * The request progresses through the system. As it does, it crosses certain
	 * thresholds. These thresholds specify triggers where acknowledgments may
	 * be generated.
	 * <p>
	 * message thresholds indicating progress
	 * <p>
	 * <dl>
	 * <dt>NONE</dt>
	 * <dd>no acknowledgment</dd>
	 * <dt>SENT</dt>
	 * <dd>once the message has been sent by a channel</dd>
	 * <dt>DISPATCHED</dt>
	 * <dd>placed under the control of an Android plugin</dd>
	 * <dt>DELIVERED</dt>
	 * <dd>a plugin acknowledges delivery of a message</dd>
	 * <dt>RECEIVED</dt>
	 * <dd>a target device acknowledges receipt of a message</dd>
	 * </dl>
	 * <p>
	 * Each of the events propagate the acknowledgment to the application in a
	 * number of different ways. Regardless of the mechanism the intent is
	 * formed in a consistent way for the particular type.
	 * <p>
	 * NONE: no acknowledgment is generated and thus no intent is produced
	 * <p>
	 * SENT:
	 * <p>
	 * <p>
	 * <code>
	 * final Intent notice = new Intent()
	 * .setAction(ACTION_MSG_SENT) 
	 * .setData(Uri.Builder() 
	 *    .scheme("ammo")
	 *    .authority(ack.topic) 
	 *    .path(ack.subtopic) 
	 *    .build())
	 * .putExtra(EXTRA_STATUS, ack.status.toString()) 
	 * .putExtra(EXTRA_TOPIC, ack.topic.toString()) 
	 * .putExtra(EXTRA_UID, ack.auid.toString())
	 * .putExtra(EXTRA_CHANNEL, ack.channel.toString()); 
	 * </code>
	 * <p>
	 * RECEIVED:
	 * <p>
	 * <code>
	 * final Intent
	 * notice = new Intent() 
	 * .setAction(ACTION_MSG_RECEIVED)
	 * .setData(Uri.Builder() 
	 *      .scheme("ammo") 
	 *      .authority(ack.topic)
	 *      .path(ack.subtopic) 
	 *      .build()) 
	 * .putExtra(EXTRA_STATUS, ack.status.toString()) 
	 * .putExtra(EXTRA_TOPIC, ack.topic.toString())
	 * .putExtra(EXTRA_UID, ack.auid.toString()) 
	 * .putExtra(EXTRA_CHANNEL, ack.channel.toString()) 
	 * .putExtra(EXTRA_DEVICE, ack.device.toString());
	 * </code>
	 */

	static private final int SENT_ID = 0x01;
	static private final int GATEWAY_ID = 0x02;
	static private final int PLUGIN_ID = 0x04;
	static private final int DEVICE_ID = 0x08;

	public enum Threshold {
		/** the message has left the hand held */
		SENT(SENT_ID, "sent"),
		/** hand held dispatches request to android plugin */
		GATE_DELIVERY(GATEWAY_ID, "gateway in-bound"),
		/** arrived at an outgoing gateway plugin */
		PLUGIN_DELIVERY(PLUGIN_ID, "gateway to plugin"),
		/** delivered to a hand held */
		DEVICE_DELIVERY(DEVICE_ID, "handheld delivered");

		public final int id;
		public final String t;

		private Threshold(int id, String title) {
			this.id = id;
			this.t = title;
		}

		public static Threshold getInstance(int id) {
			switch (id) {
			case SENT_ID:
				return Threshold.SENT;
			case GATEWAY_ID:
				return Threshold.GATE_DELIVERY;
			case PLUGIN_ID:
				return Threshold.PLUGIN_DELIVERY;
			case DEVICE_ID:
				return Threshold.DEVICE_DELIVERY;
			}
			plogger.warn("no threshold of type=[{}]", id);
			return null;
		}
	}

	public int cv() {
		// int notice = 0; // notice nothing
		// if (atSend.)
		return 0;
	}

	final public Item atSend;
	final public Item atGatewayDelivered;
	final public Item atPluginDelivered;
	final public Item atDeviceDelivered;

	/**
	 * Used by the request builder
	 * 
	 * @param threshold
	 * @param type
	 */
	public void setItem(Threshold threshold, Via.Type type) {
		plogger.debug("set notice item: @{}->[{}]", 
                threshold, type);
		switch (threshold) {
		case SENT:
			atSend.via.set(type);
			return;
		case GATE_DELIVERY:
			atGatewayDelivered.via.set(type);
			return;
		case PLUGIN_DELIVERY:
			atPluginDelivered.via.set(type);
			return;
		case DEVICE_DELIVERY:
			atDeviceDelivered.via.set(type);
			return;
		}
	}

	/**
	 * Used by the data store Note this takes an aggregate type.
	 * 
	 * @param threshold
	 * @param aggregate
	 */
	public void setItem(Threshold threshold, int aggregate) {
		switch (threshold) {
		case SENT:
			atSend.via.set(aggregate);
			return;
		case GATE_DELIVERY:
			atGatewayDelivered.via.set(aggregate);
			return;
		case PLUGIN_DELIVERY:
			atPluginDelivered.via.set(aggregate);
			return;
		case DEVICE_DELIVERY:
			atDeviceDelivered.via.set(aggregate);
			return;
		}
	}

	/**
	 * Is some type of notice activated?
	 * 
	 * @return
	 */
	public boolean isAckNeeded() {
    	if (this.atDeviceDelivered.getVia().isActive()) return true;
    	if (this.atGatewayDelivered.getVia().isActive()) return true;
		if (this.atPluginDelivered.getVia().isActive()) return true;
		return false;
    }

	/**
	 * message acknowledgment states
	 * <p>
	 * As acknowledgments of the message are generated they will contain the
	 * threshold status.
	 */
	public enum DeliveryState {
		/** the place was reached without incident */
		SUCCESS,
		/** the place was reached, but the request failed and will be canceled */
		FAIL,
		/** the request is of an indeterminate disposition */
		UNKNOWN,
		/** the place rejected the request, another may yet accept it */
		REJECTED
	};

	/**
	 * As the request reaches the places mentioned, it will cause an intent to
	 * generated and sent via one of prescribed Via.Type's. The structure of the
	 * intent varies based on the NoticeThreshold type. One of the following
	 * delivery mechanism will be used.
	 */

	static public class Via {

		public enum Type {
			/** Produce no action */
			NONE(0x00),
			/** Send an intent to an Activity, context.startActivity(notice) */
			ACTIVITY(0x01),
			/** Broadcast and intent, context.sendBroadcast(notice) */
			BROADCAST(0x02),
			/**
			 * Broadcast an intent which persists ,
			 * context.sendStickyBroadcast(notice)
			 */
			STICKY_BROADCAST(0x04),
			/** Start a service with the intent, context.startService(notice) */
            SERVICE(0x08),
            /** what is heartbeat? */
            HEARTBEAT(0x10);
			// maybe RECORD indicating that the individual acknowledgments are
			// to be recorded, in the RECIPIENT table

			public int v;

			private Type(int v) {
				this.v = v;
			}
		};

		public int v;

		public String asBits() {
			return Integer.toBinaryString(this.v);
		}

		/**
		 * multi-value flag, NONE resets it.
		 * 
		 * @param via
		 */
		public void set(Type type) {
			if (type.v == Type.NONE.v) {
				this.v = Type.NONE.v;
				return;
			}
			this.v |= type.v;
		}

		public void set(int aggregate) {
			this.v = aggregate;
		}

		public boolean isActive() {
			return !(this.v == Type.NONE.v);
		}

		public boolean hasHeartbeat() {
			return (0 < (this.v & Type.HEARTBEAT.v));
		}

		private Via(int val) {
			this.v = val;
		}

		public static Via newInstance(int val) {
			return new Via(val);
		}

		private Via() {
			this.v = Type.NONE.v;
		}

		public static Via newInstance() {
			return new Via();
		}

		@Override
		public String toString() {
			if (this.v == Type.NONE.v)
				return "NONE";

			final StringBuilder sb = new StringBuilder().append(':');
			if (0 < (this.v & Type.ACTIVITY.v)) {
				sb.append("activity").append(':');
			}
			if (0 < (this.v & Type.SERVICE.v)) {
				sb.append("service").append(':');
			}
			if (0 < (this.v & Type.BROADCAST.v)) {
				sb.append("broadcast").append(':');
			}
			if (0 < (this.v & Type.STICKY_BROADCAST.v)) {
				sb.append("sticky").append(':');
			}
			if (0 < (this.v & Type.HEARTBEAT.v)) {
				sb.append("heartbeat").append(':');
			}
			return sb.toString();
		}

	}

	// *********************************
	// Parcelable Support
	// *********************************

	public static final Parcelable.Creator<Notice> CREATOR = new Parcelable.Creator<Notice>() {

		@Override
		public Notice createFromParcel(Parcel source) {
			return new Notice(source);
		}

		@Override
		public Notice[] newArray(int size) {
			return new Notice[size];
		}
	};

	/**
	 * symmetric with AmmoType:pickle()
	 * 
	 * @param bytes
	 * @return
	 */
	static public Notice unpickle(byte[] bytes) {
		Parcel np = null;
		try {
			np = Parcel.obtain();
			np.unmarshall(bytes, 0, bytes.length);
			np.setDataPosition(0);
			return Notice.readFromParcel(np);
		} catch (BadParcelableException ex) {
			return Notice.RESET;
		} finally {
			if (np != null)
				np.recycle();
		}
	}

	public static Notice readFromParcel(Parcel source) {
		if (AmmoType.isNull(source)) {
			return new Notice();
		}
		return new Notice(source);
	}

	@Override
	public void writeToParcel(Parcel dest, int flags) {
		plogger.trace("origin notice: {}", this);

		dest.writeInt(4); // the number of items

		this.atSend.writeParcel(dest, flags);
		this.atGatewayDelivered.writeParcel(dest, flags);
		this.atPluginDelivered.writeParcel(dest, flags);
		this.atDeviceDelivered.writeParcel(dest, flags);
	}

	/**
	 * Receive a parcel and generate a Notice object from it.
	 * 
	 * @param in
	 */
	private Notice(Parcel in) {
		final Map<Threshold, Item> items = new HashMap<Threshold, Item>(4);
		try {
			final int count = in.readInt();

			for (int ix = 0; ix < count; ++ix) {
				final Threshold threshold = Threshold.getInstance(in.readInt());
				if (threshold == null) {
					plogger.error("damaged notice parcel=[{}]", in.marshall());
					continue;
				}
				final Via via = Via.newInstance(in.readInt());
				plogger.trace("notice threshold=[{}] via=[{}]", threshold, via);
				items.put(threshold, new Item(threshold, via));
			}
		} catch (Exception ex) {
			// most likely exception is IllegalArgumentException
			plogger.error("damaged/missing notice parcel", ex);
		}
		final Item sent = items.get(Threshold.SENT);
		this.atSend = (sent != null) ? sent : new Item(Threshold.SENT,
				Via.newInstance());

		final Item gwDelivery = items.get(Threshold.GATE_DELIVERY);
		this.atGatewayDelivered = (gwDelivery != null) ? gwDelivery : new Item(
				Threshold.GATE_DELIVERY, Via.newInstance());

		final Item plugDelivery = items.get(Threshold.PLUGIN_DELIVERY);
		this.atPluginDelivered = (plugDelivery != null) ? plugDelivery
				: new Item(Threshold.PLUGIN_DELIVERY, Via.newInstance());

		final Item devDelivery = items.get(Threshold.DEVICE_DELIVERY);
		this.atDeviceDelivered = (devDelivery != null) ? devDelivery
				: new Item(Threshold.DEVICE_DELIVERY, Via.newInstance());

		plogger.trace("decoded notice: {}", this);
	}

	// *********************************
	// Standard Methods
	// *********************************
	@Override
	public String toString() {
		final StringBuilder sb = new StringBuilder();
		boolean existsAnActive = false;
		if (this.atSend.via.isActive()) {
			sb.append(this.atSend.toString()).append(' ');
			existsAnActive = true;
		}
		if (this.atGatewayDelivered.via.isActive()) {
			sb.append(this.atGatewayDelivered.toString()).append(' ');
			existsAnActive = true;
		}
		if (this.atPluginDelivered.via.isActive()) {
			sb.append(this.atPluginDelivered.toString()).append(' ');
			existsAnActive = true;
		}
		if (this.atDeviceDelivered.via.isActive()) {
			sb.append(this.atDeviceDelivered.toString()).append(' ');
			existsAnActive = true;
		}
		if (!existsAnActive) {
			return "<none requested>";
		}
		return sb.toString();
	}

	/**
	 * This indicates whether there exists an active remote threshold to be
	 * noticed. (The sent threshold is local not remote)
	 * 
	 * @return
	 */
	public boolean isRemoteActive() {
		if (this.atGatewayDelivered.via.isActive()) {
			return true;
		}
		if (this.atPluginDelivered.via.isActive()) {
			return true;
		}
		if (this.atDeviceDelivered.via.isActive()) {
			return true;
		}
		return false;
	}

	/**
	 * The default constructor Sets all the thresholds.
	 */
	public Notice() {
		this.atSend = new Item(Threshold.SENT, Via.newInstance());
		this.atGatewayDelivered = new Item(Threshold.GATE_DELIVERY,
				Via.newInstance());
		this.atPluginDelivered = new Item(Threshold.PLUGIN_DELIVERY,
				Via.newInstance());
		this.atDeviceDelivered = new Item(Threshold.DEVICE_DELIVERY,
				Via.newInstance());
	}

	/**
	 * check that the two objects are logically equal.
	 */
	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (!(obj instanceof Notice))
			return false;
		final Notice that = (Notice) obj;
		if (AmmoType.differ(this.atSend, that.atSend))
			return false;
		if (AmmoType.differ(this.atGatewayDelivered, that.atGatewayDelivered))
			return false;
		if (AmmoType.differ(this.atPluginDelivered, that.atPluginDelivered))
			return false;
		if (AmmoType.differ(this.atDeviceDelivered, that.atDeviceDelivered))
			return false;
		return true;
	}

	@Override
	public synchronized int hashCode() {
		if (!this.dirtyHashcode.getAndSet(false))
			return this.hashcode;
		this.hashcode = AmmoType.HashBuilder.newBuilder()
                .increment(this.atSend)
                .increment(this.atGatewayDelivered)
				.increment(this.atPluginDelivered)
                .increment(this.atDeviceDelivered)
                .hashCode();
		return this.hashcode;
	}

	@Override
	public String asString() {
		logger.error("asString() not implemented");
		return null;
	}

}
