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

import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import android.content.Context;
import android.database.Cursor;
import android.database.CursorIndexOutOfBoundsException;
import android.net.Uri;
import edu.vu.isis.ammo.core.provider.DistributorSchema;
import edu.vu.isis.ammo.core.provider.PresenceSchema;
import edu.vu.isis.ammo.core.provider.Relations;
import edu.vu.isis.ammo.core.provider.TemporalState;

/**
 * Class encapsulates user network presence information from Ammo
 */
public class AmmoPresence {
    private static final Logger logger = LoggerFactory.getLogger("api.ammo.presence");

    /**
     * constants for presence state
     */
    public static final int PRESENT = TemporalState.PRESENT.code;
    public static final int RARE = TemporalState.RARE.code;
    public static final int MISSED = TemporalState.MISSED.code;
    public static final int LOST = TemporalState.LOST.code;
    public static final int ABSENT = TemporalState.ABSENT.code;

    private Context mContext;

    public AmmoPresence() {
        mContext = null;
    }

    private AmmoPresence(Context context) {
        mContext = context;
    }

    public static AmmoPresence newInstance(Context context) {
        return new AmmoPresence(context);
    }

    /**
     * Container for the network presence status for a single user.
     */
    static public class UserStatus {
        public UserStatus() {
        }

        // Username
        private String userId;

        public String getUserId() {
            return this.userId;
        }

        public UserStatus setUserId(String val) {
            this.userId = val;
            return this;
        }

        // Presence status
        private int status;

        public int getStatus() {
            return this.status;
        }

        public UserStatus setStatus(int val) {
            this.status = val;
            return this;
        }
    }

    /**
     * Get list of all currently available users.
     * 
     * @return arraylist of UserStatus objects, each containing the status of a
     *         user available on the network.
     */
    public List<UserStatus> getAllAvailableUsers() {
        List<UserStatus> userList = queryForAllUsers();
        if (userList == null) {
            logger.error("null users list");
        }
        return userList;
    }

    /**
     * Determine whether a given user is currently available on the network.
     * e.g. <code>
        import edu.vu.isis.ammo.api.AmmoPresence;
        import edu.vu.isis.ammo.api.AmmoPresence.UserStatus;
        
        AmmoPresence p = AmmoPresence.newInstance(mContext);
        
        // Status of single, named user
        final int status = p.getUserPresenceStatus("bubba");
        if (status == AmmoPresence.ABSENT) {
            Log.d(TAG, "   --> user is absent");
        }
        </code>
     * 
     * @param The userid (string), e.g. 'john.doe', to query for availability.
     * @return Integer value corresponding to PRESENT, RARE, LOST, ABSENT
     */
    public int getUserPresenceStatus(String userId) {
        int status = queryUserPresence(userId);
        return status;
    }

    /**
     * Report on all observed users available on the network.
     * e.g. <code>
        import edu.vu.isis.ammo.api.AmmoPresence;
        import edu.vu.isis.ammo.api.AmmoPresence.UserStatus;
        
        AmmoPresence p = AmmoPresence.newInstance(mContext);
        
        // List of all users whose status is known
        ArrayList<UserStatus> userStatusList = p.getAllAvailableUsers();
        for (UserStatus ustat : userStatusList) {
            logger.info("user={} status={}", ustat.userId, ustat.status);
        </code>
     * 
     * @return Integer value corresponding to PRESENT, RARE, LOST, ABSENT
     */
    private List<UserStatus> queryForAllUsers() {
        Cursor presenceCursor = null;
        ArrayList<UserStatus> list = new ArrayList<UserStatus>();
        try {
            Uri presenceUri = DistributorSchema.CONTENT_URI.get(Relations.PRESENCE);
            String[] projection = {
                    PresenceSchema.OPERATOR.field, PresenceSchema.STATE.field
            };
            String selection = null;
            String[] selectionArgs = null;
            presenceCursor = mContext.getContentResolver().query(presenceUri, projection,
                    selection, selectionArgs, null);

            if (presenceCursor == null) {
                logger.error("queryForAllUsers: null cursor");
                return null;
            }

            int count = presenceCursor.getCount();
            if (count <= 0) {
                logger.error("queryForAllUsers: no rows in cursor");
                return null;
            }

            for (int i = 0; i < count; i++) {
                presenceCursor.moveToNext();

                String userId = presenceCursor.getString(presenceCursor
                        .getColumnIndex(PresenceSchema.OPERATOR.field));
                final int status = presenceCursor.getInt(presenceCursor
                        .getColumnIndex(PresenceSchema.STATE.field));

                int decodeStatus = TemporalState.decodeState(status).code;

                logger.debug("queryForAllUsers: user={} status={}",
                        userId, decodeStatus);

                UserStatus u = new UserStatus();
                u.setUserId(userId);
                u.setStatus(decodeStatus);

                list.add(u);
            }
            return list;
        } catch (IllegalArgumentException e) {
            logger.error("Error while querying for presence", e);
            return null;
        } catch (CursorIndexOutOfBoundsException e) {
            logger.error("Cursor out of bounds", e);
            e.printStackTrace();
            return null;
        } catch (ArrayIndexOutOfBoundsException e) {
            logger.error("Array index out of bounds", e);
            return null;
        } finally {
            if (presenceCursor != null) {
                presenceCursor.close();
            }
        }
    }

    private int queryUserPresence(String userId) {
        final int FALLBACK_RETVAL = -1;
        Cursor presenceCursor = null;
        try {
            logger.debug("queryUserPresence: {}", userId);
            Uri presenceUri = DistributorSchema.CONTENT_URI.get(Relations.PRESENCE);
            String[] projection = {
                    PresenceSchema.STATE.field
            };
            String selection = new StringBuilder()
                    .append(PresenceSchema.OPERATOR).append("=?")
                    .toString();
            String[] selectionArgs = {
                    userId
            };
            presenceCursor = mContext.getContentResolver().query(presenceUri, projection,
                    selection, selectionArgs, null);

            if (presenceCursor == null) {
                logger.error("queryUserPresence: null cursor");
                return FALLBACK_RETVAL;
            }
            if (!presenceCursor.moveToFirst()) {
                logger.error("queryUserPresence: cursor error");
                return FALLBACK_RETVAL;
            }

            final int status = presenceCursor.getInt(presenceCursor
                    .getColumnIndex(PresenceSchema.STATE.field));
            int decodeStatus = TemporalState.decodeState(status).code;

            logger.debug("queryUserPresence: status={} coded={}", status, decodeStatus);
            return decodeStatus;

        } catch (IllegalArgumentException e) {
            logger.error("Error while querying for presence", e);
            return FALLBACK_RETVAL;
        } finally {
            if (presenceCursor != null) {
                presenceCursor.close();
            }
        }
        // Shouldn't make it to here
    }

    /**
     * This method has not yet been fully specified.
     * The general intent is that each time the state of the user
     * changes the runnable will be invoked.
     * 
     * @param userId
     * @param runnable
     */
    /*
    public void setOnChangeCallback(String userId, Runnable runnable) {

        if (userId == null) {
            // all users
        }

    }
    */

}
