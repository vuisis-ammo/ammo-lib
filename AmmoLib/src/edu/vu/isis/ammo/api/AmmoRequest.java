package edu.vu.isis.ammo.api;

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
import android.os.Parcelable;
import android.os.RemoteException;
import edu.vu.isis.ammo.api.type.Anon;
import edu.vu.isis.ammo.api.type.DeliveryScope;
import edu.vu.isis.ammo.api.type.Oid;
import edu.vu.isis.ammo.api.type.Payload;
import edu.vu.isis.ammo.api.type.Provider;
import edu.vu.isis.ammo.api.type.Selection;
import edu.vu.isis.ammo.api.type.TimeTrigger;
import edu.vu.isis.ammo.api.type.TimeInterval;
import edu.vu.isis.ammo.api.type.TimeStamp;
import edu.vu.isis.ammo.api.type.Topic;


/**
 * see docs/dev-guide/developer-guide.pdf
 */
public class AmmoRequest extends AmmoRequestBase implements IAmmoRequest, Parcelable {
	private static final Logger logger = LoggerFactory.getLogger(AmmoRequest.class);
	private static final Logger plogger = LoggerFactory.getLogger( "ammo-parcel" );

	// **********************
	// PUBLIC PROPERTIES
	// **********************
	final public IAmmoRequest.Action action;
	final public String uuid;

	final public Provider provider;
	final public Payload payload;
	final public Topic topic;

	final public Integer downsample;
	final public Integer durability;

	final public Anon recipient;
	final public Anon originator;

	final public Integer priority;
	final public Integer order;

	final public TimeTrigger start;
	final public TimeTrigger expire;

	final public DeliveryScope scope;
	final public Integer throttle;

	final public String[] project;
	final public Selection select;

	final public Integer worth;

	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append(this.action.toString()).append(" Request ");
		sb.append(this.uuid).append(" ");
		sb.append(this.topic).append(' ');
		return sb.toString();
	}


	// ****************************
	// Parcelable Support
	// ****************************

	public static final Parcelable.Creator<AmmoRequest> CREATOR = 
			new Parcelable.Creator<AmmoRequest>() {

		@Override
		public AmmoRequest createFromParcel(Parcel source) {
			return new AmmoRequest(source);
		}

		@Override
		public AmmoRequest[] newArray(int size) {
			return new AmmoRequest[size];
		}
	};

	/**
	 * The this.provider.writeToParcel(dest, flags) form is not used
	 * rather Class.writeToParcel(this.provider, dest, flags) so 
	 * that when the null will will be handled correctly.
	 */
	@Override
	public void writeToParcel(Parcel dest, int flags) {
		plogger.debug("marshall ammo request {} {}", this.uuid, this.action);
		dest.writeValue(this.uuid);
		Action.writeToParcel(dest, this.action);

		plogger.debug("provider {}", this.provider);
		Provider.writeToParcel(this.provider, dest, flags);
		plogger.debug("payload {}", this.payload);
		Payload.writeToParcel(this.payload, dest, flags);
		plogger.debug("topic {}", this.topic);
		Topic.writeToParcel(this.topic, dest, flags);

		plogger.debug("recipient {}", this.recipient);
		Anon.writeToParcel(this.recipient, dest, flags);
		plogger.debug("originator {}", this.originator);
		Anon.writeToParcel(this.originator, dest, flags);

		plogger.debug("downsample {}", this.downsample);
		dest.writeValue(this.downsample);
		plogger.debug("durabliity {}", this.durability);
		dest.writeValue(this.durability);

		plogger.debug("priority {}", this.priority);
		dest.writeValue(this.priority);
		plogger.debug("order {}", this.order);
		dest.writeValue(this.order);

		plogger.debug("start {}", this.start);
		TimeTrigger.writeToParcel(this.start, dest, flags);
		plogger.debug("expire {}", this.expire);
		TimeTrigger.writeToParcel(this.expire, dest, flags);

		plogger.debug("scope {}", this.scope);
		DeliveryScope.writeToParcel(this.scope, dest, flags);
		plogger.debug("throttle {}", this.throttle);
		dest.writeValue(this.throttle);
		plogger.debug("worth {}", this.worth);
		dest.writeValue(this.worth);


		plogger.debug("selection {}", this.select);
		Selection.writeToParcel(this.select, dest, flags);
		plogger.debug("projection {}", this.project);
		dest.writeStringArray(this.project);

		try {
		    plogger.trace("parcel {}", dest.marshall());
		} catch (RuntimeException ex) {
			logger.error("could not marshall {}", dest);
		}
	}


	/**
	 * 
	 * @param in
	 */
	private AmmoRequest(Parcel in) {
		
		try {
			plogger.trace("parcel {}", in.marshall());
		} catch (RuntimeException ex) {
			logger.error("could not marshall {}", in);
		}

		this.uuid = (String) in.readValue(Integer.class.getClassLoader());
		this.action = Action.getInstance(in);
		plogger.debug("unmarshall ammo request {} {}", this.uuid, this.action);

		this.provider = Provider.readFromParcel(in);
		plogger.debug("provider {}", this.provider);
		this.payload = Payload.readFromParcel(in);
		plogger.debug("payload {}", this.payload);
		this.topic = Topic.readFromParcel(in);
		plogger.debug("topic {}", this.topic);

		this.recipient = Anon.readFromParcel(in);
		plogger.debug("recipient {}", this.recipient);
		this.originator = Anon.readFromParcel(in);
		plogger.debug("originator {}", this.originator);

		this.downsample = (Integer) in.readValue(Integer.class.getClassLoader());
		plogger.debug("downsample {}", this.downsample);
		this.durability = (Integer) in.readValue(Integer.class.getClassLoader());
		plogger.debug("durability {}", this.durability);

		this.priority = (Integer) in.readValue(Integer.class.getClassLoader());
		plogger.debug("priority {}", this.priority);
		this.order = (Integer) in.readValue(Integer.class.getClassLoader());
		plogger.debug("order {}", this.order);

		this.start = TimeTrigger.readFromParcel(in);
		plogger.debug("start {}", this.start);
		this.expire = TimeTrigger.readFromParcel(in);
		plogger.debug("expire {}", this.expire);

		this.scope = DeliveryScope.readFromParcel(in);
		plogger.debug("scope {}", this.scope);
		this.throttle = (Integer) in.readValue(Integer.class.getClassLoader());
		plogger.debug("throttle {}", this.throttle);
		this.worth = (Integer) in.readValue(Integer.class.getClassLoader());
		plogger.debug("worth {}", this.worth);

		this.select = Selection.readFromParcel(in);
		plogger.debug("selection {}", this.select);
		this.project = in.createStringArray();
		plogger.debug("projection {}", this.project);
	}

	@Override
	public int describeContents() { return 0; }

	// *********************************
	// IAmmoReques Support
	// *********************************


	private AmmoRequest(IAmmoRequest.Action action, Builder builder) {
		this.action = action;

		this.provider = builder.provider;
		this.payload = builder.payload;

		this.topic = builder.topic;


		this.downsample = builder.downsample;
		this.durability = builder.durability;

		this.recipient = builder.recipient;
		this.originator = builder.originator;

		this.priority = builder.priority;
		this.order = 0; // TODO builder.order;

		this.start = builder.start;
		this.expire = builder.expire;

		this.scope = builder.scope;
		this.throttle = builder.throttle;

		this.project = builder.project;
		this.select = builder.select;

		this.worth = builder.worth;

		this.uuid = generateUuid();
	}

	private String generateUuid() {
		return "a-uuid";
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

	//    public static Builder newBuilder(IBinder service) {
	//        return new AmmoRequest.Builder(service).reset();
	//    }
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

	@Override
	public String uuid() {
		return this.uuid;
	}

	@Override
	public Event[] cancel() {
		// TODO Auto-generated method stub
		return null;
	}

	// **************
	// STATISTICS
	// **************
	@Override
	public Event[] eventSet() {
		// TODO Auto-generated method stub
		return null;
	}

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
				final boolean isBound = this.context.bindService(DISTRIBUTOR_SERVICE, 
						this.conn, Context.BIND_AUTO_CREATE);
				logger.info("is the service bound? {}", isBound);
			} catch (ReceiverCallNotAllowedException ex) {
				logger.info("the service cannot be bound");
				
			}
		}


		private Provider provider;
		private Payload payload;
		private Topic topic;

		private Integer downsample;
		private Integer durability;

		private Anon recipient;
		private Anon originator;

		private Integer priority;
		private Integer[] order;

		private TimeTrigger start;
		private TimeTrigger expire;

		private DeliveryScope scope;
		private Integer throttle;

		private String[] project;
		private Selection select;

		private Integer worth;

		@SuppressWarnings("unused")
		private String uid;

		// ***************
		// ACTIONS
		// ***************

		/**
		 * Generally the BIND approach should be used as it has the best performance.
		 * Sometimes this is not possible and the getService()/COMMAND method must be
		 * used (in the case of BroadcastReceiver).
		 * There are cases where the PEEK method may be used (BroadcastReceiver) but
		 * it is not particularly reliable so the fall-back to COMMAND is necessary.
		 * It may also be the case that the service has not yet started and 
		 * the binder has not yet been obtained.
		 * In that interim case the COMMAND mode should be used.
		 * 
		 */
		private IAmmoRequest makeRequest(final AmmoRequest request) throws RemoteException {
			switch (this.mode.get()){
			case BIND:
			case PEEK:
				final String ident = this.distributor.get().makeRequest(request);
				logger.info("request {} {}", request, ident);
				break;
			case COMMAND:
			default:
				final Intent parcelIntent = MAKE_DISTRIBUTOR_REQUEST.cloneFilter();
				parcelIntent.putExtra("request", request);
				this.context.startService(parcelIntent);
				logger.info("service command : {}", parcelIntent);
				break;
			}
			return request;
		}

		@Override
		public IAmmoRequest directedPost(IAmmoRequest.IAnon recipient) throws RemoteException {
			return this.makeRequest(new AmmoRequest(IAmmoRequest.Action.DIRECTED_POSTAL, this));
		}

		@Override
		public IAmmoRequest directedSubscribe(IAmmoRequest.IAnon originator) throws RemoteException {
			return this.makeRequest(new AmmoRequest(IAmmoRequest.Action.DIRECTED_SUBSCRIBE, this));
		}

		@Override
		public IAmmoRequest post() throws RemoteException {
			return this.makeRequest(new AmmoRequest(IAmmoRequest.Action.POSTAL, this));
		}

		@Override
		public IAmmoRequest publish() throws RemoteException {
			return this.makeRequest(new AmmoRequest(IAmmoRequest.Action.PUBLISH, this));
		}

		@Override
		public IAmmoRequest retrieve() throws RemoteException {
			return this.makeRequest(new AmmoRequest(IAmmoRequest.Action.RETRIEVAL, this));
		}
		@Override
		public IAmmoRequest subscribe() throws RemoteException {
			return this.makeRequest(new AmmoRequest(IAmmoRequest.Action.SUBSCRIBE, this));
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
			this.context.unbindService(this.conn);
		}


		// **************
		// SET PROPERTIES
		// **************
		@Override
		public Builder reset() {
			this.downsample(DOWNSAMPLE_DEFAULT);
			this.durability(DURABILITY_DEFAULT);
			this.order(ORDER_DEFAULT);
			this.originator(ORIGINATOR_DEFAULT);
			this.payload(PAYLOAD_DEFAULT);
			this.priority(PRIORITY_DEFAULT);
			this.provider(PROVIDER_DEFAULT);
			this.recipient(RECIPIENT_DEFAULT);
			this.scope(SCOPE_DEFAULT);
			this.start(START_DEFAULT);
			this.throttle(THROTTLE_DEFAULT);
			this.topic(TOPIC_DEFAULT);
			this.uid(UID_DEFAULT);
			this.expire(EXPIRE_DEFAULT);
			this.project(PROJECT_DEFAULT);
			this.select(SELECT_DEFAULT);
			this.filter(FILTER_DEFAULT);
			this.worth(WORTH_DEFAULT);
			return this;
		}

		public Builder downsample(String max) {
			if (max == null) return this;
			this.downsample = Integer.parseInt(max);
			return this;
		}

		@Override
		public Builder downsample(Integer maxSize) {
			this.downsample = maxSize;
			return this;
		}

		public Builder durability(String val) {
			if (val == null) return this;
			this.durability = Integer.parseInt(val);
			return this;
		}

		@Override
		public Builder durability(Integer val) {
			this.durability = val;
			return this;
		}

		public Builder order(String val) {
			if (this.order == null) return this;
			// this.order[0] = val;
			return this;
		}

		@Override
		public Builder order(Integer val) {
			if (this.order == null) this.order = new Integer[2];
			this.order[0] = val;
			return this;
		}

		@Override
		public Builder order(String[] val) {
			// TODO this.order = val;
			return this;
		}


		@Override
		public Builder originator(IAmmoRequest.IAnon val) {
			this.originator = (Anon) val;
			return this;
		}

		@Override
		public Builder originator(String val) {
			if (val == null) return this;
			this.originator =  new Anon(val);
			return this;
		}

		@Override
		public Builder recipient(IAmmoRequest.IAnon val) {
			this.recipient = (Anon) val;
			return this;
		}

		@Override
		public Builder recipient(String val) {
			if (val == null) return this;
			this.recipient =  new Anon(val);
			return this;
		}

		@Override
		public Builder payload(String val) {
			if (val == null) return this;
			this.payload = new Payload(val);
			return this;
		}

		@Override
		public Builder payload(byte[] val) {
			if (val == null) return this;
			this.payload = new Payload(val);
			return this;
		}

		@Override
		public Builder payload(ContentValues val) {
			if (val == null) return this;
			this.payload = new Payload(val);
			return this;
		}


		@Override
		public Builder payload(AmmoValues val) {
			if (val == null) return this;
			return this.payload(val.asContentValues());
		}


		public Builder priority(String val) {
			if (val == null) return this;
			return this.priority(Integer.parseInt(val));
		}

		@Override
		public Builder priority(Integer val) {
			this.priority = val;
			return this;
		}

		public Builder provider(String val) {
			if (val == null) return this;
			return this.provider(Uri.parse(val));
		}

		@Override
		public Builder provider(Uri val) {
			this.provider = new Provider(val);
			return this;
		}

		public Builder scope(String val) {
			if (val == null) return this;
			return this.scope(new DeliveryScope(val));
		}

		@Override
		public Builder scope(DeliveryScope val) {
			this.scope = val;
			return this;
		}

		public Builder throttle(String val) {
			if (val == null) return this;
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
		public Builder topic(Uri val) {
			this.topic = new Topic(val.toString());
			return this;
		}

		@Override
		public Builder uid(String val) {
			this.uid = val;
			return this;
		}


		public Builder start(String val) {
			if (val == null) return this;
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
			if (val == null) return this;
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

		public Builder project(String val) {
			if (val == null) return this;
			if (val.length() < 1) return this;
			this.project( val.substring(1).split(val.substring(0, 1)) );
			return this;
		}

		@Override
		public Builder project(String[] val) {
			this.project = val;
			return this;
		}

		public Builder select(String val) {
			if (val == null) return this;
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
			if (val == null) return this;
			this.worth = Integer.parseInt(val);
			return null;
		}

		@Override
		public Builder worth(Integer val) {
			this.worth = val;
			return this;
		}

	}

}
