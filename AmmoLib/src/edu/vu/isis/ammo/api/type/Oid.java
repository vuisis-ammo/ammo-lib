package edu.vu.isis.ammo.api.type;

import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;

import android.os.Parcel;
import android.os.Parcelable;

public class Oid implements List<Integer>, Parcelable {

    @Override
    public boolean add(Integer arg0) {
        // TODO Auto-generated method stub
        return false;
    }

    @Override
    public void add(int location, Integer object) {
        // TODO Auto-generated method stub

    }

    @Override
    public boolean addAll(Collection<? extends Integer> arg0) {
        // TODO Auto-generated method stub
        return false;
    }

    @Override
    public boolean addAll(int arg0, Collection<? extends Integer> arg1) {
        // TODO Auto-generated method stub
        return false;
    }

    @Override
    public void clear() {
        // TODO Auto-generated method stub

    }

    @Override
    public boolean contains(Object object) {
        // TODO Auto-generated method stub
        return false;
    }

    @Override
    public boolean containsAll(Collection<?> arg0) {
        // TODO Auto-generated method stub
        return false;
    }

    @Override
    public Integer get(int location) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public int indexOf(Object object) {
        // TODO Auto-generated method stub
        return 0;
    }

    @Override
    public boolean isEmpty() {
        // TODO Auto-generated method stub
        return false;
    }

    @Override
    public Iterator<Integer> iterator() {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public int lastIndexOf(Object object) {
        // TODO Auto-generated method stub
        return 0;
    }

    @Override
    public ListIterator<Integer> listIterator() {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public ListIterator<Integer> listIterator(int location) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public Integer remove(int location) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public boolean remove(Object object) {
        // TODO Auto-generated method stub
        return false;
    }

    @Override
    public boolean removeAll(Collection<?> arg0) {
        // TODO Auto-generated method stub
        return false;
    }

    @Override
    public boolean retainAll(Collection<?> arg0) {
        // TODO Auto-generated method stub
        return false;
    }

    @Override
    public Integer set(int location, Integer object) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public int size() {
        // TODO Auto-generated method stub
        return 0;
    }

    @Override
    public List<Integer> subList(int start, int end) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public Object[] toArray() {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public <T> T[] toArray(T[] array) {
        // TODO Auto-generated method stub
        return null;
    }
    
    // *********************************
    // Parcelable Support
    // *********************************

    public static final Parcelable.Creator<Oid> CREATOR = 
    	new Parcelable.Creator<Oid>() {

        @Override
        public Oid createFromParcel(Parcel source) {
            return new Oid(source);
        }

        @Override
        public Oid[] newArray(int size) {
            return new Oid[size];
        }

    };
    
    private Oid(Parcel in) {
    	// TODO Auto-generated method stub
    }
    
	@Override
	public int describeContents() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public void writeToParcel(Parcel dest, int flags) {
		// TODO Auto-generated method stub
		
	}

}