package mil.darpa.transapp.ammo.launch.constants;

/**
 * Static constants used throughout the application.
 * @author Demetri Miller
 *
 */
public class Constants {
	/**
	 * Key to use when storing uris in intents.
	 */
	public static final String ROW_URI = "row_uri";

	/**
	 * Intent to load indiviual contact.
	 */
	public static final String INDIVIDUAL_CONTACT_ACTIVITY_LAUNCH = "mil.darpa.transapp.ammo.launch.individualcontactactivity.LAUNCH";
	
	public static final String AMMO_ACCOUNT_TYPE = "mil.darpa.transapp.ammo";
	public static final String AMMO_DEFAULT_ACCOUNT_NAME = "ammo";
    public static final String AMMO_AUTHTOKEN_TYPE = "mil.darpa.transapp.ammo";
    
    public static final String LDAP_MIME = "application/vnd.edu.vu.isis.ammo.launcher.contact_pull";
	
	public static final String MIME_INSIGNIA = "vnd.android.cursor.item/insignia";
	public static final String MIME_CALLSIGN = "vnd.android.cursor.item/callsign";
	public static final String MIME_USERID = "vnd.android.cursor.item/userid";
	public static final String MIME_RANK = "vnd.android.cursor.item/organization"; //vnd.android.cursor.item/rank";
	public static final String MIME_UNIT_NAME = "vnd.android.cursor.item/organization"; //vnd.android.cursor.item/unitname";
	
}