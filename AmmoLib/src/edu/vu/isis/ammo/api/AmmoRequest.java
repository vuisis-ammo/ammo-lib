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
package edu.vu.isis.ammo.api;

import java.util.UUID;
import java.util.concurrent.atomic.AtomicReference;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.ReceiverCallNotAllowedException;
import android.content.ServiceConnection;
import android.net.Uri;
import android.os.IBinder;
import android.os.Parcel;
import android.os.ParcelFormatException;
import android.os.Parcelable;
import android.os.RemoteException;
import edu.vu.isis.ammo.api.type.DeliveryScope;
import edu.vu.isis.ammo.api.type.Limit;
import edu.vu.isis.ammo.api.type.Moment;
import edu.vu.isis.ammo.api.type.Notice;
import edu.vu.isis.ammo.api.type.Oid;
import edu.vu.isis.ammo.api.type.Order;
import edu.vu.isis.ammo.api.type.Payload;
import edu.vu.isis.ammo.api.type.Provider;
import edu.vu.isis.ammo.api.type.Selection;
import edu.vu.isis.ammo.api.type.TimeInterval;
import edu.vu.isis.ammo.api.type.TimeStamp;
import edu.vu.isis.ammo.api.type.TimeTrigger;
import edu.vu.isis.ammo.api.type.Topic;

/**
 * see docs/dev-guide/developer-guide.pdf
 */
public class AmmoRequest extends AmmoRequestBase implements IAmmoRequest, Parcelable {
	private static final Logger logger = LoggerFactory.getLogger("ammo-rqst");
	private static final Logger plogger = LoggerFactory.getLogger("ammo-parcel");

	// **********************
	// PUBLIC PROPERTIES
	// **********************
	final public IAmmoRequest.Action action;
	final public String uuid; // the request globally unique identifier
	final public String uid;  // the application object unique identifier

	final public Provider provider;
	final public Payload payload;
	final public Moment moment;
	final public Topic topic;
	final public Topic subtopic;

	final public Integer downsample;
	final public Integer durability;

	final public Integer priority;
	final public Order order;

	final public TimeTrigger start;
	final public TimeTrigger expire;
	final public Limit limit;

	final public DeliveryScope scope;
	final public Integer throttle;

	final public String[] project;
	final public Selection select;

	final public Integer worth;
	final public Notice notice;

	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append(this.action.toString()).append(" Request ");
		sb.append(this.uuid).append(" ");
		sb.append(this.uid).append(" ");
		sb.append(this.topic).append(' ');
		return sb.toString();
	}

	// ****************************
	// Parcelable Support
	// ****************************

	public static final Parcelable.Creator<AmmoRequest> CREATOR = new Parcelable.Creator<AmmoRequest>() {

		@Override
		public AmmoRequest createFromParcel(Parcel source) {
		    try {
			return new AmmoRequest(source);
		    } catch (Throwable ex) {
		    	final int capacity = source.dataCapacity();
		    	final byte[] data = new byte[capacity];
		    	source.unmarshall(data, 0, capacity);
			    plogger.error("PARCEL UNMARSHALLING PROBLEM: {} {}", 
				data, ex); 
			return null;
		    }
		}

		@Override
		public AmmoRequest[] newArray(int size) {
			return new AmmoRequest[size];
		}
	};

	/**
	 * The this.provider.writeToParcel(dest, flags) form is not used rather
	 * Class.writeToParcel(this.provider, dest, flags) so that when the null
	 * will will be handled correctly.
	 */
	private final byte VERSION = (byte) 0x03;

	@Override
	public void writeToParcel(Parcel dest, int flags) {
		plogger.trace("marshall ammo request {} {}", this.uuid, this.action);
		dest.writeByte(VERSION);

		dest.writeValue(this.uuid);
		dest.writeValue(this.uid);
		Action.writeToParcel(dest, this.action);

		plogger.trace("provider {}", this.provider);
		Provider.writeToParcel(this.provider, dest, flags);
		plogger.trace("payload {}", this.payload);
		Payload.writeToParcel(this.payload, dest, flags);
		plogger.trace("moment {}", this.moment);
		Moment.writeToParcel(this.moment, dest, flags);
		plogger.trace("topic {}", this.topic);
		Topic.writeToParcel(this.topic, dest, flags);
		Topic.writeToParcel(this.subtopic, dest, flags);

		plogger.trace("downsample {}", this.downsample);
		dest.writeValue(this.downsample);
		plogger.trace("durabliity {}", this.durability);
		dest.writeValue(this.durability);

		plogger.trace("priority {}", this.priority);
		dest.writeValue(this.priority);
		plogger.trace("order {}", this.order);
		Order.writeToParcel(this.order, dest, flags);

		plogger.trace("start {}", this.start);
		TimeTrigger.writeToParcel(this.start, dest, flags);
		plogger.trace("expire {}", this.expire);
		TimeTrigger.writeToParcel(this.expire, dest, flags);
		plogger.trace("limit {}", this.limit);
		Limit.writeToParcel(this.limit, dest, flags);

		plogger.trace("scope {}", this.scope);
		DeliveryScope.writeToParcel(this.scope, dest, flags);
		plogger.trace("throttle {}", this.throttle);
		dest.writeValue(this.throttle);
		plogger.trace("worth {}", this.worth);
		dest.writeValue(this.worth);
		
		plogger.trace("notice {}", this.notice);
		Notice.writeToParcel(this.notice, dest, flags);
		
		plogger.trace("selection {}", this.select);
		Selection.writeToParcel(this.select, dest, flags);
		plogger.trace("projection {}", this.project);
		dest.writeStringArray(this.project);
	}

	/**
	 * 
	 * @param in
	 */
	private AmmoRequest(Parcel in)  {
	    
		final byte version = in.readByte();
		if (version < VERSION) {
		    plogger.warn("AMMO REQUEST VERSION MISMATCH, received {}, expected {}",
				version, VERSION);
        } else if (version > VERSION ){
		    plogger.warn("AMMO REQUEST VERSION MISMATCH, received {}, expected {}",
				version, VERSION);
			throw new ParcelFormatException("AMMO REQUEST VERSION MISMATCH");
        } else {
		    plogger.info("AMMO REQUEST VERSION MATCH {}", version);
		}
		
		this.uuid = (String) in.readValue(String.class.getClassLoader());
		this.uid  = (version < (byte)3) ? this.uuid : (String) in.readValue(String.class.getClassLoader());
		plogger.trace("unmarshall ammo request {} {}", this.uuid, this.uid);
		
		this.action = Action.getInstance(in);
		plogger.trace("action {}", this.action);

		this.provider = Provider.readFromParcel(in);
		plogger.trace("provider {}", this.provider);
		this.payload = Payload.readFromParcel(in);
		plogger.trace("payload {}", this.payload);	
		this.moment = (version < (byte) 3) ? Moment.LAZY : Moment.readFromParcel(in);
		plogger.trace("moment {}", this.moment);
		this.topic = Topic.readFromParcel(in);
		plogger.trace("topic {}", this.topic);
		this.subtopic = Topic.readFromParcel(in);
		plogger.trace("subtopic {}", this.subtopic);

		this.downsample = (Integer) in.readValue(Integer.class.getClassLoader());
		plogger.trace("downsample {}", this.downsample);
		this.durability = (Integer) in.readValue(Integer.class.getClassLoader());
		plogger.trace("durability {}", this.durability);

		this.priority = (Integer) in.readValue(Integer.class.getClassLoader());
		plogger.trace("priority {}", this.priority);
		this.order = Order.readFromParcel(in);
		plogger.trace("order {}", this.order);

		this.start = TimeTrigger.readFromParcel(in);
		plogger.trace("start {}", this.start);
		this.expire = TimeTrigger.readFromParcel(in);
		plogger.trace("expire {}", this.expire);
		
		this.limit = (version < (byte)2) ? new Limit(100) : Limit.readFromParcel(in);
		plogger.trace("limit {}", this.limit);

		this.scope = DeliveryScope.readFromParcel(in);
		plogger.trace("scope {}", this.scope);
		this.throttle = (Integer) in.readValue(Integer.class.getClassLoader());
		plogger.trace("throttle {}", this.throttle);
		this.worth = (Integer) in.readValue(Integer.class.getClassLoader());
		plogger.trace("worth {}", this.worth);

		this.notice = Notice.readFromParcel(in);
		plogger.trace("notice {}", this.notice);
		
		this.select = Selection.readFromParcel(in);
		plogger.trace("selection {}", this.select);
		this.project = in.createStringArray();
		plogger.trace("projection {}", this.project);
	   
	}

	@Override
	public int describeContents() {
		return 0;
	}

	// *********************************
	// IAmmoReques Support
	// *********************************

	private AmmoRequest(IAmmoRequest.Action action, Builder builder) {
		this.action = action;
		this.uid = builder.uid;

		this.provider = builder.provider;
		this.payload = builder.payload;
		this.moment = builder.moment;

		this.topic = builder.topic;
		this.subtopic = builder.subtopic;

		this.downsample = builder.downsample;
		this.durability = builder.durability;

		this.priority = builder.priority;
		this.order = builder.order;

		this.start = builder.start;
		this.expire = builder.expire;
		this.limit = builder.limit;

		this.scope = builder.scope;
		this.throttle = builder.throttle;

		this.project = builder.project;
		this.select = builder.select;

		this.worth = builder.worth;
		this.notice = builder.notice;

		this.uuid = UUID.randomUUID().toString();
	}

	@Override
	public IAmmoRequest replace(IAmmoRequest req) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public IAmmoRequest replace(String uuid) {
		// TODO Auto-generated method stub
		return null;
	}

	public static Builder newBuilder(Context context) {
		return new AmmoRequest.Builder(context).reset();
	}

	public static Builder newBuilder(Context context, BroadcastReceiver receiver) {
		return new AmmoRequest.Builder(context, receiver).reset();
	}

	// public static Builder newBuilder(IBinder service) {
	// return new AmmoRequest.Builder(service).reset();
	// }
	//  

	// **************
	// CONTROL
	// **************
	@Override
	public void metricTimespan(Integer val) {
		// TODO Auto-generated method stub

	}

	@Override
	public void resetMetrics(Integer val) {
		// TODO Auto-generated method stub
	}

	// **************
	// STATISTICS
	// **************

	@Override
	public TimeStamp lastMessage() {
		// TODO Auto-generated method stub
		return null;
	}

	/**
	 * The builder makes requests to the Distributor via AIDL methods.
	 * 
	 */
	private static final Intent DISTRIBUTOR_SERVICE = new Intent(IDistributorService.class.getCanonicalName());
	private static final Intent MAKE_DISTRIBUTOR_REQUEST = new Intent("edu.vu.isis.ammo.api.MAKE_REQUEST");

	public static class Builder implements IAmmoRequest.Builder {

		private enum ConnectionMode {
			BIND, PEEK, COMMAND, NONE;
		}

		private final AtomicReference<ConnectionMode> mode;
		private final AtomicReference<IDistributorService> distributor;
		private final Context context;

		private Builder(Context context, BroadcastReceiver receiver) {

			this.mode = new AtomicReference<ConnectionMode>(ConnectionMode.COMMAND);
			this.distributor = new AtomicReference<IDistributorService>(null);
			this.context = context;

			final IBinder service = receiver.peekService(context, DISTRIBUTOR_SERVICE);
			if (service == null) {
				logger.warn("ammo not peekable");
				return;
			}
			logger.warn("ammo available");
			this.distributor.set(IDistributorService.Stub.asInterface(service));
			this.mode.set(ConnectionMode.PEEK);
		}

		final private ServiceConnection conn = new ServiceConnection() {
			@Override
			public void onServiceConnected(ComponentName name, IBinder service) {
				logger.trace("service connected");
				Builder.this.distributor.set(IDistributorService.Stub.asInterface(service));
				Builder.this.mode.set(ConnectionMode.BIND);
			}

			@Override
			public void onServiceDisconnected(ComponentName name) {
				logger.trace("service {} disconnected", name.flattenToShortString());
				Builder.this.mode.set(ConnectionMode.COMMAND);
				Builder.this.distributor.set(null);
			}
		};

		private Builder(Context context) {
			this.mode = new AtomicReference<ConnectionMode>(ConnectionMode.COMMAND);
			this.distributor = new AtomicReference<IDistributorService>(null);
			this.context = context;
			try {
				final boolean isBound = this.context.bindService(DISTRIBUTOR_SERVICE, this.conn, Context.BIND_AUTO_CREATE);
				logger.info("is the service bound? {}", isBound);
			} catch (ReceiverCallNotAllowedException ex) {
				logger.info("the service cannot be bound");

			}
		}

		private String uid;
		
		private Provider provider;
		private Payload payload;
		private Moment moment;
		private Topic topic;
		private Topic subtopic;

		private Integer downsample;
		private Integer durability;

		private Integer priority;
		private Order order;

		private TimeTrigger start;
		private TimeTrigger expire;
		private Limit limit;

		private DeliveryScope scope;
		private Integer throttle;

		private String[] project;
		private Selection select;

		private Integer worth;
		private Notice notice;

		// ***************
		// ACTIONS
		// ***************

		/**
		 * Generally the BIND approach should be used as it has the best
		 * performance. Sometimes this is not possible and the
		 * getService()/COMMAND method must be used (in the case of
		 * BroadcastReceiver). There are cases where the PEEK method may be used
		 * (BroadcastReceiver) but it is not particularly reliable so the
		 * fall-back to COMMAND is necessary. It may also be the case that the
		 * service has not yet started and the binder has not yet been obtained.
		 * In that interim case the COMMAND mode should be used.
		 * 
		 */
		private IAmmoRequest makeRequest(final AmmoRequest request) throws RemoteException {
			switch (this.mode.get()) {
			case BIND:
			case PEEK:
				final String ident = this.distributor.get().makeRequest(request);
				logger.info("request {} {}", request, ident);
				break;
			case COMMAND:
			default:
				final Intent parcelIntent = MAKE_DISTRIBUTOR_REQUEST.cloneFilter();
				parcelIntent.putExtra("request", request);
				ComponentName componentName = this.context.startService(parcelIntent);
				if (componentName != null) {
					logger.debug("service command : {}", componentName.getClassName());
				} else {
					logger.error("service command : {}", parcelIntent);
				}
				break;
			}
			return request;
		}

		@Override
		public IAmmoRequest post() throws RemoteException {
			return this.makeRequest(new AmmoRequest(IAmmoRequest.Action.POSTAL, this));
		}


		@Override
		public IAmmoRequest retrieve() throws RemoteException {
			return this.makeRequest(new AmmoRequest(IAmmoRequest.Action.RETRIEVAL, this));
		}

		@Override
		public IAmmoRequest subscribe() throws RemoteException {
			return this.interest();
		}
		
		@Override
		public IAmmoRequest interest() throws RemoteException {
			return this.makeRequest(new AmmoRequest(IAmmoRequest.Action.INTEREST, this));
		}

		@Override
		public IAmmoRequest duplicate() throws RemoteException {
			// TODO Auto-generated method stub
			return null;
		}

		@Override
		public IAmmoRequest getInstance(String uuid) throws RemoteException {
			// TODO Auto-generated method stub
			return null;
		}

		@Override
		public void releaseInstance() {
			try {
				this.context.unbindService(this.conn);	
			} catch (IllegalArgumentException e) {
				logger.warn("the service is not bound or registered {}", e.getLocalizedMessage());
			}
			
		}

		// **************
		// SET PROPERTIES
		// **************
		@Override
		public Builder reset() {
			this.downsample(DOWNSAMPLE_DEFAULT);
			this.durability(DURABILITY_DEFAULT);
			this.order(ORDER_DEFAULT);
			this.payload(PAYLOAD_DEFAULT);
			this.moment(MOMENT_DEFAULT);
			this.priority(PRIORITY_DEFAULT);
			this.provider(PROVIDER_DEFAULT);
			this.scope(SCOPE_DEFAULT);
			this.start(START_DEFAULT);
			this.throttle(THROTTLE_DEFAULT);
			this.topic(TOPIC_DEFAULT);
			this.subtopic(SUBTOPIC_DEFAULT);
			this.uid(UID_DEFAULT);
			this.expire(EXPIRE_DEFAULT);
			this.project(PROJECT_DEFAULT);
			this.select(SELECT_DEFAULT);
			this.filter(FILTER_DEFAULT);
			this.worth(WORTH_DEFAULT);
			return this;
		}

		public Builder downsample(String max) {
			if (max == null)
				return this;
			this.downsample = Integer.parseInt(max);
			return this;
		}

		@Override
		public Builder downsample(Integer maxSize) {
			this.downsample = maxSize;
			return this;
		}

		public Builder durability(String val) {
			if (val == null)
				return this;
			this.durability = Integer.parseInt(val);
			return this;
		}

		@Override
		public Builder durability(Integer val) {
			this.durability = val;
			return this;
		}

		public Builder order(String val) {
			if (val == null)
				return this;
			return this.order(new Order(val));
		}

		@Override
		public Builder order(Order val) {
			this.order = val;
			return this;
		}

		@Override
		public Builder payload(String val) {
			if (val == null)
				return this;
			this.payload = new Payload(val);
			return this;
		}

		@Override
		public Builder payload(byte[] val) {
			if (val == null)
				return this;
			this.payload = new Payload(val);
			return this;
		}

		@Override
		public Builder payload(ContentValues val) {
			if (val == null)
				return this;
			this.payload = new Payload(val);
			return this;
		}

		@Override
		public Builder payload(AmmoValues val) {
			if (val == null)
				return this;
			return this.payload(val.asContentValues());
		}
		
		@Override
		public Builder moment(String val) {
			if (val == null)
				return this;
			return this.moment(new Moment(val));
		}

		@Override
		public Builder moment(Moment val) {
			this.moment = val;
			return this;
		}


		public Builder priority(String val) {
			if (val == null)
				return this;
			return this.priority(Integer.parseInt(val));
		}

		@Override
		public Builder priority(Integer val) {
			this.priority = val;
			return this;
		}

		public Builder provider(String val) {
			if (val == null)
				return this;
			return this.provider(Uri.parse(val));
		}

		@Override
		public Builder provider(Uri val) {
			this.provider = new Provider(val);
			this.moment = Moment.APRIORI;
			return this;
		}

		public Builder scope(String val) {
			if (val == null)
				return this;
			return this.scope(new DeliveryScope(val));
		}

		@Override
		public Builder scope(DeliveryScope val) {
			this.scope = val;
			return this;
		}

		public Builder throttle(String val) {
			if (val == null)
				return this;
			this.throttle = Integer.parseInt(val);
			return this;
		}

		@Override
		public Builder throttle(Integer val) {
			this.throttle = val;
			return this;
		}

		@Override
		public Builder topic(String val) {
			this.topic = new Topic(val);
			return this;
		}

		@Override
		public Builder topic(Oid val) {
			this.topic = new Topic(val);
			return this;
		}


		@Override
		public Builder subtopic(String val) {
			this.subtopic = new Topic(val);
			return this;
		}

		@Override
		public Builder subtopic(Oid val) {
			this.subtopic = new Topic(val);
			return this;
		}

		@Override
		public Builder topic(String topic, String subtopic) {
			this.topic(topic);
			this.subtopic(subtopic);
			return this;
		}

		@Override
		public Builder topic(Oid topic, Oid subtopic) {
			this.topic(topic);
			this.subtopic(subtopic);
			return this;
		}
		
		public Builder topicFromProvider() {
			if (this.provider == null) {
				logger.error("you must first set the provider");
				return this;
			}
			final String topic = this.context
					 .getContentResolver()
					 .getType(this.provider.asUri());
			this.topic(topic);
			return this;
		}
		
		
		@Override
		public Builder uid(String val) {
			this.uid = val;
			return this;
		}

		public Builder start(String val) {
			if (val == null)
				return this;
			return this.start(new TimeStamp(val));
		}

		@Override
		public Builder start(TimeStamp val) {
			this.start = new TimeTrigger(val);
			return this;
		}

		@Override
		public Builder start(TimeInterval val) {
			this.start = new TimeTrigger(val);
			return this;
		}

		public Builder expire(String val) {
			if (val == null)
				return this;
			return this.expire(new TimeStamp(val));
		}

		@Override
		public Builder expire(TimeInterval val) {
			this.expire = new TimeTrigger(val);
			return this;
		}

		@Override
		public Builder expire(TimeStamp val) {
			this.expire = new TimeTrigger(val);
			return this;
		}

		public Builder limit(String val) {
			this.limit = new Limit(val);
			return null;
		}

		@Override
		public Builder limit(int val) {
			this.limit = new Limit(Limit.Type.NEWEST, val);
			return this;
		}

		@Override
		public Builder limit(Limit val) {
			this.limit = val;
			return this;
		}

		public Builder project(String val) {
			if (val == null)
				return this;
			if (val.length() < 1)
				return this;
			this.project(val.substring(1).split(val.substring(0, 1)));
			return this;
		}

		@Override
		public Builder project(String[] val) {
			this.project = val;
			return this;
		}

		public Builder select(String val) {
			if (val == null)
				return this;
			this.select = new Selection(val);
			return this;
		}

		@Override
		public Builder select(Query val) {
			this.select = new Selection(val);
			return this;
		}

		@Override
		public Builder select(Form val) {
			this.select = new Selection(val);
			return this;
		}

		@Override
		public Builder filter(String val) {
			// this.filter = new Filter(val);
			return this;
		}

		public Builder worth(String val) {
			if (val == null)
				return this;
			this.worth = Integer.parseInt(val);
			return null;
		}

		@Override
		public Builder worth(Integer val) {
			this.worth = val;
			return this;
		}
		
		/**
		 *  This notice method is cumulative.
		 *  To clear the notice use the other notice().
		 */
		public Builder notice(Notice.Threshold threshold, Notice.Via mode) {
			if (this.notice == null) this.notice = Notice.newInstance();
			this.notice.setItem(threshold, mode);
			return this;
		}

		/**
		 *  This notice method is *not* cumulative.
		 *  It replaces the current notice object with the argument.
		 *  The notice set can be cleared by using this method
		 *  with a null object.
		 */
		@Override
		public Builder notice(Notice val) {
			this.notice = val;
			return this;
		}
	}

	@Override
	public void cancel() {
		// TODO Auto-generated method stub
		
	}

}
