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

package edu.vu.isis.ammo;

import transapps.settings.Keys;

/**
 * Collection of all preference values used by Ammo. MAKE SURE THESE CONFORM
 * WITH res/values/preference_keys.xml
 */
public interface INetPrefKeys {

    /**
     * General External Keys CORE_DEVICE_ID : A unique identifier for the device
     * (see UniqueIdentifier.java) CORE_OPERATOR_KEY : A passkey for the
     * operator CORE_OPERATOR_ID : The panthr operator id
     */
    public static final String CORE_DEVICE_ID = "transapps_settings_device_uuid";
    public static final String CORE_OPERATOR_KEY = Keys.UserKeys.PASSWORD;
    public static final String CORE_OPERATOR_ID = Keys.UserKeys.USERNAME;

    // VALUES
    public static final String DEFAULT_CORE_DEVICE_ID = null;
    public static final String DEFAULT_CORE_OPERATOR_KEY =
            (Keys.UserKeys.DEFAULT_PASSWORD == null)
                    ? "" : Keys.UserKeys.DEFAULT_PASSWORD;
    public static final String DEFAULT_CORE_OPERATOR_ID =
            Keys.UserKeys.DEFAULT_USERNAME;

    /**
     * Journaling External Keys JOURNAL_DISABLED : Indicates that the user
     * wishes to make use if possible.
     */
    public static final String JOURNAL_DISABLED = "transapps_settings_network_jounal_disabled";
    
    // VALUES
    public static final boolean DEFAULT_JOURNAL_ENABLED = Keys.NetworkingKeys.DEFAULT_JOURNAL_ENABLED;

    /**
     * Gateway Channel and TCP Link Settings External Keys GATEWAY_DISABLED :
     * Indicates that the user wishes to make use if possible. GATEWAY_HOST :
     * The IP address of the gateway host GATEWAY_PORT : The listening port
     */
    public static final String GATEWAY_DISABLED = Keys.NetworkingKeys.GATEWAY_DISABLED;
    public static final String GATEWAY_HOST = Keys.NetworkingKeys.GATEWAY_HOST;
    public static final String GATEWAY_PORT = "CORE_IP_PORT";
    public static final String GATEWAY_FLAT_LINE_TIME = "CORE_FLAT_LINE_TIME";
    public static final String GATEWAY_TIMEOUT = "CORE_SOCKET_TIMEOUT";
    
    public static final String SERVER_ENABLED = "transaps.settings.server.disabled"; // Keys.NetworkingKeys.SERVER_DISABLED;
    public static final String SERVER_PORT = "SERVER_IP_PORT";

    // VALUES
    public static final boolean DEFAULT_GATEWAY_ENABLED = Keys.NetworkingKeys.DEFAULT_GATEWAY_ENABLED;
    public static final String DEFAULT_GATEWAY_HOST = Keys.NetworkingKeys.DEFAULT_GATEWAY_HOST;
    public static final int DEFAULT_GATEWAY_PORT = 33289;
    public static final int DEFAULT_GW_FLAT_LINE_TIME = 20; // 20 minutes
    public static final int DEFAULT_GW_TIMEOUT = 3; // 3 seconds
    
    public static final boolean DEFAULT_SERVER_ENABLED = true; // Keys.NetworkingKeys.DEFAULT_SERVER_ENABLED;
    public static final int DEFAULT_SERVER_PORT = 51423;

    /**
     * Multicast Channel and UDP multicast Settings External Keys
     * MULTICAST_DISABLED : Indicates that the user wishes to make use if
     * possible. MULTICAST_HOST : The IP address of the gateway host
     * MULTICAST_PORT : The listening port
     */
    public static final String MULTICAST_DISABLED = Keys.NetworkingKeys.MULTICAST_DISABLED;
    public static final String MULTICAST_HOST = "MULTICAST_IP_ADDRESS";
    public static final String MULTICAST_PORT = "MULTICAST_PORT";
    public static final String MULTICAST_NET_CONN_TIMEOUT = "MULTICAST_NET_CONN_TIMEOUT";
    public static final String MULTICAST_CONN_IDLE_TIMEOUT = "MULTICAST_CONN_IDLE_TIMEOUT";
    public static final String MULTICAST_TTL = "MULTICAST_TTL";

    // VALUES
    public static final boolean DEFAULT_MULTICAST_ENABLED = Keys.NetworkingKeys.DEFAULT_MULTICAST_ENABLED;
    public static final String DEFAULT_MULTICAST_HOST = "228.10.10.90";
    public static final int DEFAULT_MULTICAST_PORT = 9982;
    public static final int DEFAULT_MULTICAST_NET_CONN = 20;
    public static final int DEFAULT_MULTICAST_IDLE_TIME = 3;
    public static final int DEFAULT_MULTICAST_TTL = 10;

    /**
     * ReliableMulticast Channel Settings External Keys SERIAL_DISABLED :
     * Indicates that the user wishes to make use if possible.
     */
    public static final String RELIABLE_MULTICAST_DISABLED = Keys.NetworkingKeys.RELIABLE_MULTICAST_DISABLED;

    /**
     * Internal KEYS The remaining keys are still in flux.
     */
    public static final String RELIABLE_MULTICAST_HOST = "RELIABLE_MULTICAST_IP_ADDRESS";
    public static final String RELIABLE_MULTICAST_PORT = "RELIABLE_MULTICAST_PORT";
    public static final String RELIABLE_MULTICAST_MEDIA_PORT = "RELIABLE_MULTICAST_MEDIA_PORT";
    public static final String RELIABLE_MULTICAST_NET_CONN_TIMEOUT = "RELIABLE_MULTICAST_NET_CONN_TIMEOUT";
    public static final String RELIABLE_MULTICAST_CONN_IDLE_TIMEOUT = "RELIABLE_MULTICAST_CONN_IDLE_TIMEOUT";
    public static final String RELIABLE_MULTICAST_TTL = "RELIABLE_MULTICAST_TTL";
    public static final String RELIABLE_MULTICAST_FRAG_DELAY = Keys.NetworkingKeys.IP_RADIO_NETWORK_THRESHOLD;

    // VALUES
    public static final boolean DEFAULT_RELIABLE_MULTICAST_ENABLED = Keys.NetworkingKeys.DEFAULT_RELIABLE_MULTICAST_ENABLED;
    public static final String DEFAULT_RELIABLE_MULTICAST_HOST = "228.8.8.8";
    public static final int DEFAULT_RELIABLE_MULTICAST_PORT = 45588;
    public static final int DEFAULT_RELIABLE_MULTICAST_MEDIA_PORT = 45590;
    public static final int DEFAULT_RELIABLE_MULTICAST_NET_CONN = 20;
    public static final int DEFAULT_RELIABLE_MULTICAST_IDLE_TIME = 3;
    public static final int DEFAULT_RELIABLE_MULTICAST_TTL = 1;
    public static final String DEFAULT_RELIABLE_MULTICAST_FRAG_DELAY = Keys.NetworkingKeys.DEFAULT_IP_RADIO_NETWORK_THRESHOLD;

    /**
     * Serial Channel and USB Link Settings Serial Channel/Link : External Keys
     * SERIAL_DISABLED : Indicates that the user wishes to make use if possible.
     * SERIAL_RADIOS_IN_GROUP : the number of slots SERIAL_SLOT_NUMBER : The
     * slot assigned to the link. SERIAL_SLOT_DURATION : time allocated to each
     * slot.
     */
    public static final String SERIAL_DISABLED = Keys.NetworkingKeys.SERIAL_DISABLED;
    public static final String SERIAL_SLOT_NUMBER = Keys.NetworkingKeys.SLOT_ID;
    public static final String SERIAL_RADIOS_IN_GROUP = Keys.NetworkingKeys.SLOT_COUNT;
    public static final String SERIAL_SLOT_DURATION = "SERIAL_SLOT_DURATION";

    /**
     * Internal KEYS SERIAL_SEND_ENABLED & SERIAL_RECEIVE_ENABLED : For
     * debugging, indicates partial activation. SERIAL_BAUD_RATE : Valid values
     * are {4800, 9600} SERIAL_DEVICE : Probably auto-detectable.
     * SERIAL_TRANSMIT_DURATION : Probably derived from the SERIAL_SLOT_DURATION
     */
    public static final String SERIAL_DEVICE = "SERIAL_DEVICE";
    public static final String SERIAL_SEND_ENABLED = "SERIAL_SEND_ENABLED";
    public static final String SERIAL_BAUD_RATE = "SERIAL_BAUD_RATE";
    public static final String SERIAL_RECEIVE_ENABLED = "SERIAL_RECEIVE_ENABLED";
    public static final String SERIAL_TRANSMIT_DURATION = "SERIAL_TRANSMIT_DURATION";

    // VALUES
    // IMPORTANT: These values must match the values in the AmmoCore xml prefs
    // files.
    public static final boolean DEFAULT_SERIAL_ENABLED = Keys.NetworkingKeys.DEFAULT_SERIAL_ENABLED;
    public static final String DEFAULT_SERIAL_DEVICE = "/dev/ttyUSB0";
    public static final int DEFAULT_SERIAL_BAUD_RATE = 9600;
    public static final int DEFAULT_SERIAL_SLOT_NUMBER = 8;
    public static final int DEFAULT_SERIAL_RADIOS_IN_GROUP = 16;
    public static final int DEFAULT_SERIAL_SLOT_DURATION = 750;
    public static final int DEFAULT_SERIAL_TRANSMIT_DURATION = 500;
    public static final boolean DEFAULT_SERIAL_SEND_ENABLED = true;

    
    // Message Size limits and defaults for each channel ....
    /**
     * maximum message length that OMMA allows over Gateway:  an integer number of MB.
     */
    public static final String GATEWAY_MAX_MESSAGE_SIZE = Keys.NetworkingKeys.GATEWAY_MAX_MESSAGE_SIZE;
    public static final String DEFAULT_GATEWAY_MAX_MESSAGE_SIZE = Keys.NetworkingKeys.DEFAULT_GATEWAY_MAX_MESSAGE_SIZE;

    /**
     * maximum message length that OMMA allows over Reliable Multicast:  an integer number of MB.
     */
    public static final String RELIABLE_MULTICAST_MAX_MESSAGE_SIZE = Keys.NetworkingKeys.RELIABLE_MULTICAST_MAX_MESSAGE_SIZE;
    public static final String DEFAULT_RELIABLE_MULTICAST_MAX_MESSAGE_SIZE = Keys.NetworkingKeys.DEFAULT_RELIABLE_MULTICAST_MAX_MESSAGE_SIZE;

    /**
     * maximum message length that OMMA allows over Serial:  an integer number of MB.
     *
     * This is for SatCom, not 152.
     */
    public static final String SERIAL_MAX_MESSAGE_SIZE = Keys.NetworkingKeys.SERIAL_MAX_MESSAGE_SIZE;;
    public static final String DEFAULT_SERIAL_MAX_MESSAGE_SIZE = Keys.NetworkingKeys.DEFAULT_SERIAL_MAX_MESSAGE_SIZE;
}
