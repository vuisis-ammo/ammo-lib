/**
 * 
 */
package edu.vu.isis.ammo.api;

import java.util.Map;
import java.util.Set;

import android.content.ContentValues;
import android.os.Parcel;
import android.os.Parcelable;

/**
 * This wrapper class makes up for some limitations of the Content Values class.
 * - double brace initialization (not a final class)
 * - instrumentation
 */
public class AmmoValues {
	private ContentValues cv;

	public AmmoValues() {
		this.cv = new ContentValues();
	}

	/**
	 * Creates an empty set of values using the given initial size
	 * 
	 * @param size
	 *            the initial size of the set of values
	 */
	public AmmoValues(int size) {
		this.cv = new ContentValues(size);
	}

	/**
	 * Creates a set of values copied from the given set
	 * 
	 * @param from
	 *            the values to copy
	 */
	public AmmoValues(AmmoValues from) {
		this.cv = new ContentValues(from.cv);
	}

	public AmmoValues(ContentValues from) {
		this.cv = new ContentValues(cv);
	}

	@Override
	public boolean equals(Object object) {
		if (!(object instanceof AmmoValues)) {
			return false;
		}
		return this.cv.equals(((AmmoValues) object).cv);
	}

	@Override
	public int hashCode() {
		return this.cv.hashCode();
	}

	/**
	 * Adds a value to the set.
	 * 
	 * @param key
	 *            the name of the value to put
	 * @param value
	 *            the data for the value to put
	 */
	public void put(String key, String value) {
		this.cv.put(key, value);
	}

	/**
	 * Adds all values from the passed in AmmoValues.
	 * 
	 * @param other
	 *            the AmmoValues from which to copy
	 */
	public void putAll(AmmoValues other) {
		this.cv.putAll(other.cv);
	}

	/**
	 * Adds a value to the set.
	 * 
	 * @param key
	 *            the name of the value to put
	 * @param value
	 *            the data for the value to put
	 */
	public void put(String key, Byte value) {
		this.cv.put(key, value);
	}

	/**
	 * Adds a value to the set.
	 * 
	 * @param key
	 *            the name of the value to put
	 * @param value
	 *            the data for the value to put
	 */
	public void put(String key, Short value) {
		this.cv.put(key, value);
	}

	/**
	 * Adds a value to the set.
	 * 
	 * @param key
	 *            the name of the value to put
	 * @param value
	 *            the data for the value to put
	 */
	public void put(String key, Integer value) {
		this.cv.put(key, value);
	}

	/**
	 * Adds a value to the set.
	 * 
	 * @param key
	 *            the name of the value to put
	 * @param value
	 *            the data for the value to put
	 */
	public void put(String key, Long value) {
		this.cv.put(key, value);
	}

	/**
	 * Adds a value to the set.
	 * 
	 * @param key
	 *            the name of the value to put
	 * @param value
	 *            the data for the value to put
	 */
	public void put(String key, Float value) {
		this.cv.put(key, value);
	}

	/**
	 * Adds a value to the set.
	 * 
	 * @param key
	 *            the name of the value to put
	 * @param value
	 *            the data for the value to put
	 */
	public void put(String key, Double value) {
		this.cv.put(key, value);
	}

	/**
	 * Adds a value to the set.
	 * 
	 * @param key
	 *            the name of the value to put
	 * @param value
	 *            the data for the value to put
	 */
	public void put(String key, Boolean value) {
		this.cv.put(key, value);
	}

	/**
	 * Adds a value to the set.
	 * 
	 * @param key
	 *            the name of the value to put
	 * @param value
	 *            the data for the value to put
	 */
	public void put(String key, byte[] value) {
		this.cv.put(key, value);
	}

	/**
	 * Adds a null value to the set.
	 * 
	 * @param key
	 *            the name of the value to make null
	 */
	public void putNull(String key) {
		this.cv.putNull(key);
	}

	/**
	 * Returns the number of values.
	 * 
	 * @return the number of values
	 */
	public int size() {
		return this.cv.size();
	}

	/**
	 * Remove a single value.
	 * 
	 * @param key
	 *            the name of the value to remove
	 */
	public void remove(String key) {
		this.cv.remove(key);
	}

	/**
	 * Removes all values.
	 */
	public void clear() {
		this.cv.clear();
	}

	/**
	 * Returns true if this object has the named value.
	 * 
	 * @param key
	 *            the value to check for
	 * @return {@code true} if the value is present, {@code false} otherwise
	 */
	public boolean containsKey(String key) {
		return this.cv.containsKey(key);
	}

	/**
	 * Gets a value. Valid value types are {@link String}, {@link Boolean}, and
	 * {@link Number} implementations.
	 * 
	 * @param key
	 *            the value to get
	 * @return the data for the value
	 */
	public Object get(String key) {
		return this.cv.get(key);
	}

	/**
	 * Gets a value and converts it to a String.
	 * 
	 * @param key
	 *            the value to get
	 * @return the String for the value
	 */
	public String getAsString(String key) {
		return this.cv.getAsString(key);
	}

	/**
	 * Gets a value and converts it to a Long.
	 * 
	 * @param key
	 *            the value to get
	 * @return the Long value, or null if the value is missing or cannot be
	 *         converted
	 */
	public Long getAsLong(String key) {
		return this.cv.getAsLong(key);
	}

	/**
	 * Gets a value and converts it to an Integer.
	 * 
	 * @param key
	 *            the value to get
	 * @return the Integer value, or null if the value is missing or cannot be
	 *         converted
	 */
	public Integer getAsInteger(String key) {
		return this.cv.getAsInteger(key);
	}

	/**
	 * Gets a value and converts it to a Short.
	 * 
	 * @param key
	 *            the value to get
	 * @return the Short value, or null if the value is missing or cannot be
	 *         converted
	 */
	public Short getAsShort(String key) {
		return this.cv.getAsShort(key);
	}

	/**
	 * Gets a value and converts it to a Byte.
	 * 
	 * @param key
	 *            the value to get
	 * @return the Byte value, or null if the value is missing or cannot be
	 *         converted
	 */
	public Byte getAsByte(String key) {
		return this.cv.getAsByte(key);
	}

	/**
	 * Gets a value and converts it to a Double.
	 * 
	 * @param key
	 *            the value to get
	 * @return the Double value, or null if the value is missing or cannot be
	 *         converted
	 */
	public Double getAsDouble(String key) {
		return this.cv.getAsDouble(key);
	}

	/**
	 * Gets a value and converts it to a Float.
	 * 
	 * @param key
	 *            the value to get
	 * @return the Float value, or null if the value is missing or cannot be
	 *         converted
	 */
	public Float getAsFloat(String key) {
		return this.cv.getAsFloat(key);
	}

	/**
	 * Gets a value and converts it to a Boolean.
	 * 
	 * @param key
	 *            the value to get
	 * @return the Boolean value, or null if the value is missing or cannot be
	 *         converted
	 */
	public Boolean getAsBoolean(String key) {
		return this.cv.getAsBoolean(key);
	}

	/**
	 * Gets a value that is a byte array. Note that this method will not convert
	 * any other types to byte arrays.
	 * 
	 * @param key
	 *            the value to get
	 * @return the byte[] value, or null is the value is missing or not a byte[]
	 */
	public byte[] getAsByteArray(String key) {
		return this.cv.getAsByteArray(key);
	}

	/**
	 * Returns a set of all of the keys and values
	 * 
	 * @return a set of all of the keys and values
	 */
	public Set<Map.Entry<String, Object>> valueSet() {
		return this.cv.valueSet();
	}

	public static final Parcelable.Creator<AmmoValues> CREATOR = new Parcelable.Creator<AmmoValues>() {
		@SuppressWarnings( { "deprecation", "unchecked" })
		public AmmoValues createFromParcel(Parcel in) {
			return new AmmoValues(ContentValues.CREATOR.createFromParcel(in));
		}

		public AmmoValues[] newArray(int size) {
			return new AmmoValues[size];
		}
	};

	public int describeContents() {
		return this.cv.describeContents();
	}

	@SuppressWarnings("deprecation")
	public void writeToParcel(Parcel parcel, int flags) {
		this.cv.writeToParcel(parcel, flags);
	}

	/**
	 * Unsupported, here until we get proper bulk insert APIs. {@hide}
	 */
	// @Deprecated
	// public void putStringArrayList(String key, ArrayList<String> value) {
	// this.cv.putStringArrayList(key, value);
	// }

	/**
	 * Unsupported, here until we get proper bulk insert APIs. {@hide}
	 */
	// @SuppressWarnings("unchecked")
	// @Deprecated
	// public ArrayList<String> getStringArrayList(String key) {
	// return (ArrayList<String>) mValues.get(key);
	// }

	@Override
	public String toString() {
		return this.cv.toString();
	}

}