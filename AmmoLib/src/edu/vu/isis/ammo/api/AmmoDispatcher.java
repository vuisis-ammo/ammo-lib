package edu.vu.isis.ammo.api;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.OutputStream;
import java.util.Calendar;

import android.app.PendingIntent;
import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.os.Environment;
import android.os.Parcel;
import android.util.Log;
import android.widget.Toast;

import com.google.gson.Gson;

import edu.vu.isis.ammo.core.provider.DistributorSchema.PostalTableSchema;
import edu.vu.isis.ammo.core.provider.DistributorSchema.PublicationTableSchema;
import edu.vu.isis.ammo.core.provider.DistributorSchema.RetrievalTableSchema;
import edu.vu.isis.ammo.core.provider.DistributorSchema.SubscriptionTableSchema;

/**
 * see https://ammo.isis.vanderbilt.edu/redmine/boards/2/topics/3
 * 
 * @author phreed
 * 
 */
public class AmmoDispatcher {
	
	/**
	 * Access mode for the file. 
	 * May be "r" for read-only access, 
	 * "w" for write-only access (erasing whatever data is currently in the file), 
	 * "wa" for write-only access to append to any existing data, 
	 * "rw" for read and write access on any existing data, and 
	 * "rwt" for read and write access that truncates any existing file.
	 */
	private static AmmoDispatcher instance = null;
	private final static long MAXIMUM_FIELD_SIZE = 9046;
	/**
	 * Posting information which is not persistent on the mobile device. 
	 * Once the item has been sent it is removed.
	 */
	// posting with explicit expiration and worth
	static private File dir = new File(Environment.getExternalStorageDirectory(),"ammo_distributor_cache");
	
	// static final private String selectUri = "\""+RetrievalTableSchema.URI+"\" = '?'";
	static final private String selectRetrievalUri = "\""+RetrievalTableSchema.URI+"\" = ";
	static final private String selectSubscriptionUri = "\""+SubscriptionTableSchema.URI+"\" = ";
	static final private String selectPublicationUri = "\""+PublicationTableSchema.URI+"\" = ";

	public static AmmoDispatcher getInstance(Context context) {
		if (instance == null) {
			instance = new AmmoDispatcher(context, context.getContentResolver());
		}
		return instance;
	}

	final private Context context;

	final private ContentResolver resolver;
	private AmmoDispatcher(Context context, ContentResolver resolver) {
		this.context = context;
		this.resolver = resolver;
	}
	
	private byte[] serializePendingIntent(PendingIntent pi) {
		if (pi != null) return null;
	    Parcel parcel = Parcel.obtain();
		PendingIntent.writePendingIntentOrNullToParcel(pi, parcel);
		return parcel.marshall();
	}
	
	/**
	 * Posting with implicit expiration and worth, delivery is ASAP.
	 *  
	 * @param mimeType
	 * @param value
	 * @return
	 */
	public boolean post(String mimeType, String serializedString) {
		return this.post(mimeType, serializedString, null, Double.NaN);
	}

	/**
	 * Directly post a string.
	 * 
	 * @param mimeType
	 * @param value
	 * @param expiration
	 * @param worth
	 * @return
	 */
	public boolean post(String mimeType, String serializedString, Calendar expiration, double worth) {
		return post(mimeType, serializedString, expiration, worth, null);
	}
	public boolean post(String mimeType, String serializedString, Calendar expiration, double worth, PendingIntent notice) 
	{
//		File filename = new File(dir, Long.toHexString(System.currentTimeMillis()));
//        try {
//            // put data into file
//            FileOutputStream fileStream = new FileOutputStream(filename);
//            byte[] buffer = serializedString.getBytes();
//            fileStream.write(buffer, 0, buffer.length);
//            fileStream.close();
//        } catch (IOException ex) {
//			ex.printStackTrace();
//		}
		if (expiration == null) {
			expiration = Calendar.getInstance();
			expiration.setTimeInMillis(System.currentTimeMillis() + (120 * 1000));
		}
		
		ContentValues values = new ContentValues();
		values.put(PostalTableSchema.CP_TYPE, mimeType);
		// values.put(PostalTableSchema.URI, uri.toString());
		values.put(PostalTableSchema.DISPOSITION, PostalTableSchema.DISPOSITION_PENDING);
		values.put(PostalTableSchema.SERIALIZE_TYPE, PostalTableSchema.SERIALIZE_TYPE_DIRECT);
		values.put(PostalTableSchema.EXPIRATION, expiration.getTimeInMillis());
		// System.currentTimeMillis() + (120 * 1000));
		values.put(PostalTableSchema.UNIT, 50);
		values.put(PostalTableSchema.VALUE, worth);
		if (notice != null) 
		    values.put(PostalTableSchema.NOTICE, serializePendingIntent(notice));
		values.put(PostalTableSchema.CREATED_DATE, System.currentTimeMillis());
		
		Uri uri;
		if (serializedString.length() < MAXIMUM_FIELD_SIZE) {
			values.put(PostalTableSchema.DATA, serializedString);
			uri = resolver.insert(PostalTableSchema.CONTENT_URI, values);
			return true;
		}
		// the data field will be left null
		uri = resolver.insert(PostalTableSchema.CONTENT_URI, values);
		try {
			OutputStream ostream = resolver.openOutputStream(uri);
			byte[] buffer = serializedString.getBytes();
			ostream.write(buffer, 0, buffer.length);
			ostream.flush();
			ostream.close();
		} catch (FileNotFoundException e) {
			Log.e("AmmoDispatcher","distributor could not open file");
			return false;
		} catch (IOException e) {
			Log.e("AmmoDispatcher","could not write to distributor");
			return false;
		}
		return (uri != null);
	}
	
	/**
	 * Posting with implicit expiration and worth, delivery is ASAP.
	 *  
	 * @param mimeType
	 * @param value
	 * @return
	 */
	public boolean post(String mimeType, ContentValues value) {
		return this.post(mimeType, value, null, Double.NaN);
	}

	/**
	 * Directly post a set of content values.
	 * 
	 * @param mimeType
	 * @param value
	 * @param expiration
	 * @param worth
	 * @return
	 */
	public boolean post(String mimeType, ContentValues value, Calendar expiration, double worth) 
	{
		Gson gson = new Gson();
        String serializedString = gson.toJson(value);
        return post(mimeType, serializedString, expiration, worth);
	}

	/**
	 * Posting with implicit expiration and worth, their values are obtained from the content provider.
	 * The mime type is obtained from the content provider.
	 * 
	 * @param uri
	 * @return
	 */
	public boolean post(Uri uri) {
		return this.post(uri, this.resolver.getType(uri), null, Double.NaN);
	}

	/**
	 * /**
	 * Posting information which is persistent on the mobile device.
	 * Is the distribution policy is retained?
	 * 
	 * Posting with explicit expiration and worth
	 * 
	 * @param uri
	 * @param expiration
	 * @param worth
	 * @return
	 */
	public boolean post(Uri uri, Calendar expiration, double worth) {
		return this.post(uri, this.resolver.getType(uri), expiration, worth);
	}
	
	/**
	 * Posting some information to be distributed from a content provider.
	 * 
	 * @param uri of the specific item to be posted, the content provider tuple identifier
	 * @param mimeType the mime type of the tuple being posted, used for distribution by the gateway
	 * @param expiration how long before the item must be posted, the journal time
	 * @param worth how valuable is the information
	 * @return was the distribution content provider updated correctly.
	 */
	public boolean post(Uri uri, String mimeType, Calendar expiration, double worth) {
		return post(uri,mimeType,expiration,worth,null);
	}
	public boolean post(Uri uri, String mimeType, Calendar expiration, double worth, PendingIntent notice) {
		// check that the uri is valid
		if (uri == null) return false;
		
		if (null == resolver.getType(PostalTableSchema.CONTENT_URI)) {
			return false;
		}
		
		if (expiration == null) {
			expiration = Calendar.getInstance();
			expiration.setTimeInMillis(System.currentTimeMillis() + (120 * 1000));
		}
		
		ContentValues values = new ContentValues();
		values.put(PostalTableSchema.CP_TYPE, mimeType);
		values.put(PostalTableSchema.URI, uri.toString());
		values.put(PostalTableSchema.SERIALIZE_TYPE, PostalTableSchema.SERIALIZE_TYPE_INDIRECT);
		values.put(PostalTableSchema.DISPOSITION, PostalTableSchema.DISPOSITION_PENDING);
		values.put(PostalTableSchema.EXPIRATION, expiration.getTimeInMillis());
		// System.currentTimeMillis() + (120 * 1000));
		values.put(PostalTableSchema.UNIT, 50);
		values.put(PostalTableSchema.VALUE, worth);
		if (notice != null) 
		    values.put(PostalTableSchema.NOTICE, serializePendingIntent(notice));
		values.put(PostalTableSchema.CREATED_DATE, System.currentTimeMillis());
		
		resolver.insert(PostalTableSchema.CONTENT_URI, values);
		return true;
	}
	
	/**
	 * Pulling with explicit expiration, worth, and query.
	 *  
	 * @param uri
	 * @param expiration
	 * @param worth
	 * @param query
	 * @return
	 */
	public boolean pull(Uri uri, Calendar expiration, double worth, String query) {
		return this.pull(uri, this.resolver.getType(uri), expiration, worth, query, null);
	}
	
	/**
	 * Sets the lifetime in seconds.
	 * 
	 * @param uri
	 * @param lifetime
	 * @param worth
	 * @param query
	 * @return
	 */
	public boolean pull(Uri uri, int lifetime, double worth, String query) {
		return this.pull(uri, Calendar.SECOND, lifetime, worth, query);
	}

	/**
	 * Rather than providing an absolute time this method specifies a
	 * relative time (starting now) of how long the request should remain active.
	 * This also uses the mime type from the content provider.
	 * 
	 * e.g.
	 * ammo_dispatcher.pull(EventTableSchemaBase.CONTENT_URI, Calendar.MINUTE, 500, 10.0, ":event");
	 * 
	 * EventTableSchemaBase.CONTENT_URI = "content://edu.vu.isis.ammo.collector.provider.incidentprovider/event"
	 * 
	 * @param uri
	 * @param field {@link Calendar} 
	 * @param expiration
	 * @param worth
	 * @param query
	 * @return
	 */
	public boolean pull(Uri uri, int field, int lifetime, double worth, String query) {
		return this.pull(uri, null , field, lifetime, worth, query);
	}
	/**
	 * Sets the lifetime to one hour.
	 * This is the simplest use of the call.
	 *  e.g.
	 * ammo_dispatcher.pull(EventTableSchemaBase.CONTENT_URI, "urn:something:api/service/location/");
	 * 
	 * 
	 * @param uri
	 * @param lifetime
	 * @param worth
	 * @param query
	 * @return
	 */
	public boolean pull(Uri uri, String mime) {
		return this.pull(uri, mime, Calendar.HOUR, 1, 0.0, "");
	}
	
	/**
	 * Force the mime type rather than using the resolver to acquire it.
	 * 
	 * @param uri
	 * @param mime
	 * @param field
	 * @param lifetime
	 * @param worth
	 * @param query
	 * @return
	 */
	public boolean pull(Uri uri, String mime, int field, int lifetime, double worth, String query) {
		Calendar expiration = Calendar.getInstance(); 
		expiration.add(field, lifetime);
		if (mime == null) mime = this.resolver.getType(uri);
		return this.pull(uri, mime, expiration, worth, query, null);
	}
	
	/**
	 * The base pull mechanism with all parameters.
	 * The uri is the name of the content provider which will deserialize the response.
	 * The mimeType is the name in which the target service has expressed an interest.
	 * 
	 * The expiration indicates when the the pull request is no longer relevant.
	 * The worth indicates the value of the data requested.
	 * The query will typically be a json string and will contain whatever 
	 * additional information the interest expressing service needs.
	 * 
	 * e.g. 
	 *   Suppose there is a table 'people' in content provider 'nevada' with sponsor 'com.aterrasys.nevada'.
	 *   The client program wishes to initialize the table with a request to service.
	 *   
	 *   In preparation the service has expressed interest in the type
	 *   'urn:aterrasys.com:/api/rtc/people/list/'
	 *   The call here provides this same type as the mimeType to the pull request.
	 *   
	 * With pull, a request for information to be placed into a content provider owned by the dispatcher.
	 * This mechanism establishes a relationship between mime type and target uri.
	 * The target uri is not sent to the gateway, rather the content uri of the 
	 * subscriber::querytable tuple is sent as the request_id.
	 * Subsequently when a pull response is received the request_id is used to
	 * look up the entry in the subscriber::queryTable which provides the target 
	 * content provider uri.
	 * 
	 * @param uri of the the content provider to receive the tuples
	 * @param mimeType the mime type of the tuples being requested, used for retrieval by the gateway
	 * @param expiration how long does the subscription last?
	 * @param worth how valuable is the information
	 * @param query
	 * @return was the subscriber content provider updated correctly.
	 */
	private boolean pull(Uri uri, String mimeType, Calendar expiration, double worth, String query, PendingIntent notice) {
	    if (expiration == null) {
			expiration = Calendar.getInstance();
			expiration.setTimeInMillis(System.currentTimeMillis() + (120 * 1000));
	    }
	    ContentValues values = new ContentValues();
	    values.put(RetrievalTableSchema.MIME, mimeType);
	    values.put(RetrievalTableSchema.URI, uri.toString());
	    values.put(RetrievalTableSchema.DISPOSITION, RetrievalTableSchema.DISPOSITION_PENDING);
	    values.put(RetrievalTableSchema.EXPIRATION, expiration.getTimeInMillis());
		
	    values.put(RetrievalTableSchema.SELECTION, query);
	    values.put(RetrievalTableSchema.PROJECTION, "");
	    values.put(RetrievalTableSchema.CREATED_DATE, System.currentTimeMillis());
	    if (notice != null) 
		    values.put(RetrievalTableSchema.NOTICE, serializePendingIntent(notice));
		
	    String[] projection = {RetrievalTableSchema._ID};
	    String[] selectArgs = {uri.toString()};
	    // Cursor queryCursor = resolver.query(RetrievalTableSchema.CONTENT_URI, projection, selectUri, selectArgs, null);
	    Cursor queryCursor = resolver.query(RetrievalTableSchema.CONTENT_URI, projection, selectRetrievalUri+"'"+uri.toString()+"'",null, null);
	    if (queryCursor == null) {
		Toast.makeText(context, "missing pull content provider", Toast.LENGTH_LONG).show();
		return false;
	    }
	    if (queryCursor.getCount() == 1) {
		Log.d("AmmoLib", "found an existing pull request in the retrieval table ... updating ...");
		for (boolean more = queryCursor.moveToFirst(); more; ) {
		    long queryId = queryCursor.getLong(queryCursor.getColumnIndex(RetrievalTableSchema._ID));
		    Uri queryUri = ContentUris.withAppendedId(RetrievalTableSchema.CONTENT_URI, queryId);
		    resolver.update(queryUri, values, null, null);
		    break; // there is only one
		}
	    } else if  (queryCursor.getCount() > 1) {
			Toast.makeText(context, "corrupted subscriber content provider; removing offending tuples", Toast.LENGTH_LONG).show();
			resolver.delete(RetrievalTableSchema.CONTENT_URI, selectRetrievalUri, selectArgs);
			resolver.insert(RetrievalTableSchema.CONTENT_URI, values);
	    } else {
			Log.d("AmmoLib", "creating a pull request in retrieval table ... inserting ...");
			resolver.insert(RetrievalTableSchema.CONTENT_URI, values);
	    }
	    return true;
	}
	
	
	
	
	/**
	 * Subscribing with explicit expiration, worth, and filter.
	 *  
	 * @param uri
	 * @param expiration
	 * @param worth
	 * @param filter
	 * @return
	 */
	public boolean subscribe(Uri uri, Calendar expiration, double worth, String filter) {
		return this.subscribe(uri, this.resolver.getType(uri), expiration, worth, filter);
	}
	

	/**
	 * Sets the lifetime in seconds.
	 * 
	 * @param uri
	 * @param lifetime
	 * @param worth
	 * @param filter
	 * @return
	 */
	public boolean subscribe(Uri uri, int lifetime, double worth, String filter) {
		return this.subscribe(uri, Calendar.SECOND, lifetime, worth, filter);
	}

	/**
	 * Rather than providing a absolute time this method specifies a
	 * relative time (starting now) of how long the request should remain active.
	 * e.g.
	 * ammo_dispatcher.subscribe(EventTableSchemaBase.CONTENT_URI, Calendar.MINUTE, 500, 10.0, ":event");
	 * 
	 * EventTableSchemaBase.CONTENT_URI = "content://edu.vu.isis.ammo.collector.provider.incidentprovider/event"
	 * 
	 * @param uri
	 * @param field {@link Calendar} 
	 * @param expiration
	 * @param worth
	 * @param filter
	 * @return
	 */
	public boolean subscribe(Uri uri, int field, int lifetime, double worth, String filter) {
		return this.subscribe(uri, null , field, lifetime, worth, filter);
	}
	/**
	 * Defaults for pretty much everything.
	 * 
	 * @param uri
	 * @param lifetime
	 * @param worth
	 * @param filter
	 * @return
	 */
	public boolean subscribe(Uri uri, String mime) {
		if (mime == null) mime = this.resolver.getType(uri);
		return this.subscribe(uri, mime, Calendar.HOUR, 1, 0, "");
	}
	/**
	 * Subscribe to a topic, a request for information to be placed into a content provider.
	 * This mechanism establishes a relationship between mime type and target uri.
	 * However the uri is not sent to the gateway, rather the content uri of the 
	 * subscriber::filtertable tuple is sent as the request_id.
	 * Subsequently when a pull response is received the request_id is used to
	 * look up the entry in the subscriber::filterTable which provides the target 
	 * content provider uri.
	 * 
	 * @param uri of the the content provider to receive the tuples
	 * @param mimeType the mime type of the tuples being requested, used for retrieval by the gateway
	 * @param expiration how long does the subscription last?
	 * @param worth how valuable is the information
	 * @param filter
	 * @return was the subscriber content provider updated correctly.
	 */
	private boolean subscribe(Uri uri, String mimeType, Calendar expiration, double worth, String filter) {
		return subscribe(uri, mimeType, expiration, worth, filter, null);
	}
	
	private boolean subscribe(Uri uri, String mimeType, Calendar expiration, double worth, String filter, PendingIntent notice) {
		if (expiration == null) {
			expiration = Calendar.getInstance();
			expiration.setTimeInMillis(System.currentTimeMillis() + (120 * 1000));
		}
		Log.d("AmmoDispatcher", "::subscribe with uri: " + uri.toString() + " mime: " + mimeType);
		
		ContentValues values = new ContentValues();
		values.put(SubscriptionTableSchema.MIME, mimeType);
		values.put(SubscriptionTableSchema.URI, uri.toString());
		values.put(SubscriptionTableSchema.EXPIRATION, expiration.getTimeInMillis());
		
		values.put(SubscriptionTableSchema.SELECTION, filter);
		if (notice != null) 
		    values.put(SubscriptionTableSchema.NOTICE, serializePendingIntent(notice));
		values.put(SubscriptionTableSchema.CREATED_DATE, System.currentTimeMillis());
		
		String[] projection = {SubscriptionTableSchema._ID,SubscriptionTableSchema.EXPIRATION};
		String[] selectArgs = {uri.toString()};
		// Cursor filterCursor = resolver.query(SubscriptionTableSchema.CONTENT_URI, projection, selectUri, selectArgs, null);

		String selection = selectSubscriptionUri + "'" + uri.toString() + "'" + " AND \"" + SubscriptionTableSchema.MIME + "\" = '" + mimeType + "'";

		Cursor queryCursor = resolver.query(SubscriptionTableSchema.CONTENT_URI, projection, selection, null, null);
		if (queryCursor == null) {
           Toast.makeText(context, "missing subscriber content provider", Toast.LENGTH_LONG).show();
           return false;
	    }
		if (queryCursor.getCount() == 1) {
			for (boolean more = queryCursor.moveToFirst(); more; ) {
				long queryId = queryCursor.getLong(queryCursor.getColumnIndex(SubscriptionTableSchema._ID));
				long queryExpiration = queryCursor.getLong(queryCursor.getColumnIndex(SubscriptionTableSchema.EXPIRATION));

				if (queryExpiration == 0) // if this was an expired subscription then set its DISPOSITION to PENDING
				    values.put(SubscriptionTableSchema.DISPOSITION, SubscriptionTableSchema.DISPOSITION_PENDING);

				Uri queryUri = ContentUris.withAppendedId(SubscriptionTableSchema.CONTENT_URI, queryId);
				resolver.update(queryUri, values, null, null);
				break; // there is only one
			}
		} else if  (queryCursor.getCount() > 1) {
			Toast.makeText(context, "corrupted subscriber content provider; removing offending tuples", Toast.LENGTH_LONG).show();
			resolver.delete(SubscriptionTableSchema.CONTENT_URI, selectSubscriptionUri, selectArgs);
			resolver.insert(SubscriptionTableSchema.CONTENT_URI, values);
		} else {
		    // if its a new entry set the DISPOSITION to pending - else leave it as is ...
		    values.put(SubscriptionTableSchema.DISPOSITION, SubscriptionTableSchema.DISPOSITION_PENDING);

		    resolver.insert(SubscriptionTableSchema.CONTENT_URI, values);
		}
		return true;
	}
	
	/**
	 * Force the mime type rather than using the resolver to acquire it.
	 * @param uri
	 * @param mime
	 * @param field
	 * @param lifetime
	 * @param worth
	 * @param filter
	 * @return
	 */
	public boolean subscribe(Uri uri, String mime, int field, int lifetime, double worth, String filter) {
		Calendar expiration = Calendar.getInstance(); 
		expiration.add(field, lifetime);
		if (mime == null) mime = this.resolver.getType(uri);
		return this.subscribe(uri, mime, expiration, worth, filter);
	}
	
	public boolean unsubscribe(Uri uri) {
		ContentValues values = new ContentValues();
		values.put(SubscriptionTableSchema.EXPIRATION, 0);
		String where = "\""+SubscriptionTableSchema.URI+"\" = '"+uri.toString()+"'";
		resolver.update(SubscriptionTableSchema.CONTENT_URI, values, where, null);
		return true;
	}
	
	public boolean unsubscribe(Uri uri, String mime) {
		ContentValues values = new ContentValues();
		values.put(SubscriptionTableSchema.EXPIRATION, 0);
		values.put(SubscriptionTableSchema.MIME, mime);
		String where = "\""+SubscriptionTableSchema.URI+"\" = '"+uri.toString()+"'" 
		+ " AND " + "\"" + SubscriptionTableSchema.MIME+"\" = \"" + mime + "\"";
		resolver.update(SubscriptionTableSchema.CONTENT_URI, values, where, null);
		return true;
	}
	
	/**
	 * Subscribe to a topic, a request for information to be placed into a content provider.
	 * This mechanism establishes a relationship between mime type and target uri.
	 * However the uri is not sent to the gateway, rather the content uri of the 
	 * subscriber::filtertable tuple is sent as the request_id.
	 * Subsequently when a pull response is received the request_id is used to
	 * look up the entry in the subscriber::filterTable which provides the target 
	 * content provider uri.
	 * 
	 * @param uri of the the content provider to receive the tuples
	 * @param mimeType the mime type of the tuples being requested, used for retrieval by the gateway
	 * @param expiration how long does the subscription last?
	 * @return was the subscriber content provider updated correctly.
	 */
	private boolean publish(Uri uri, String mimeType, Calendar expiration) {
		if (expiration == null) {
			expiration = Calendar.getInstance();
			expiration.setTimeInMillis(System.currentTimeMillis() + (120 * 1000));
		}
		ContentValues values = new ContentValues();
		values.put(PublicationTableSchema.MIME, mimeType);
		values.put(PublicationTableSchema.URI, uri.toString());
		values.put(PublicationTableSchema.DISPOSITION, PublicationTableSchema.DISPOSITION_PENDING);
		values.put(PublicationTableSchema.EXPIRATION, expiration.getTimeInMillis());
		
		values.put(PublicationTableSchema.CREATED_DATE, System.currentTimeMillis());
		
		String[] projection = {PublicationTableSchema._ID};
		String[] selectArgs = {uri.toString()};
		// Cursor queryCursor = resolver.query(PublicationTableSchema.CONTENT_URI, projection, selectUri, selectArgs, null);
		Cursor queryCursor = resolver.query(PublicationTableSchema.CONTENT_URI, projection, selectPublicationUri+"'"+uri.toString()+"'",null, null);
		if (queryCursor == null) {
           Toast.makeText(context, "missing subscriber content provider", Toast.LENGTH_LONG).show();
           return false;
	    }
		if (queryCursor.getCount() == 1) {
			for (boolean more = queryCursor.moveToFirst(); more; ) {
				long queryId = queryCursor.getLong(queryCursor.getColumnIndex(PublicationTableSchema._ID));
				Uri queryUri = ContentUris.withAppendedId(PublicationTableSchema.CONTENT_URI, queryId);
				resolver.update(queryUri, values, null, null);
				break; // there is only one
			}
		} else if  (queryCursor.getCount() > 1) {
			Toast.makeText(context, "corrupted subscriber content provider; removing offending tuples", Toast.LENGTH_LONG).show();
			resolver.delete(PublicationTableSchema.CONTENT_URI, selectPublicationUri, selectArgs);
			resolver.insert(PublicationTableSchema.CONTENT_URI, values);
		} else {
		    resolver.insert(PublicationTableSchema.CONTENT_URI, values);
		}
		return true;
	}
	
	
}
