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

public interface AmmoIntents {
	public static final String AMMO_ACTION_ETHER_LINK_CHANGE = "edu.vu.isis.ACTION_ETHER_LINK_CHANGE";
	public static final String AMMO_ACTION_WIFI_LINK_CHANGE = "edu.vu.isis.ACTION_WIFI_LINK_CHANGE";
	public static final String AMMO_ACTION_GATEWAY_STATUS_CHANGE = "edu.vu.isis.ACTION_GATEWAY_STATUS_CHANGE";
	public static final String AMMO_ACTION_CONNECTION_STATUS_CHANGE = "edu.vu.isis.ACTION_CONNECTION_STATUS_CHANGE";
	public static final String AMMO_ACTION_NETLINK_STATUS_CHANGE = "edu.vu.isis.ACTION_NETLINK_STATUS_CHANGE";
	public static final String ACTION_SERIAL_LINK_CHANGE = "edu.vu.isis.ACTION_SERIAL_LINK_CHANGE";
	public static final int LINK_UP = 1;
	public static final int LINK_DOWN = 2;
	
	/**
	 * Fields for naming extras.
	 */
	public static final String EXTRA_CHANNEL = "channel";
	public static final String EXTRA_CONNECT_STATUS = "connect-status";
	
}