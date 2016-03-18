package de.metas.adempiere.bpartner.service.impl;

import java.util.Arrays;
import java.util.List;

import de.metas.adempiere.bpartner.service.IBPartnerPA;
import de.metas.adempiere.bpartner.service.OrgHasNoBPartnerLinkException;
import de.metas.adempiere.bpartner.service.ProductHasNoVendorException;
import de.metas.adempiere.db.IDatabaseBL;
import de.metas.adempiere.misc.service.IPOService;
import de.metas.adempiere.util.Services;
import org.compiere.model.I_AD_User;
import org.compiere.model.I_C_BPartner;
import org.compiere.model.I_C_BPartner_Location;
import org.compiere.model.I_C_Country;
import org.compiere.model.I_C_Greeting;
import org.compiere.model.I_C_Location;
import org.compiere.model.MBPartner;
import org.compiere.model.MBPartnerLocation;
import org.compiere.model.MLocation;
import org.compiere.model.MProduct;
import org.compiere.model.MUser;
import org.compiere.model.X_C_BPartner;
import org.compiere.model.X_C_BPartner_Location;
import org.compiere.model.X_C_Country;
import org.compiere.model.X_C_Greeting;
import org.compiere.model.X_C_Location;
import org.compiere.util.CLogger;
import org.compiere.util.Env;

public final class BPartnerPA implements IBPartnerPA {

	public static final String SQL_BPARTNER = "SELECT * FROM "
			+ I_C_BPartner.Table_Name + " WHERE "
			+ I_C_BPartner.COLUMNNAME_Value + "=?";

	public static final String SQL_SELECT_COUNTRY = "SELECT * FROM "
			+ I_C_Country.Table_Name + " WHERE "
			+ I_C_Country.COLUMNNAME_CountryCode + "=?";

	public static final String SQL_LOCATION = "SELECT * FROM "
			+ I_C_Location.Table_Name + " WHERE "
			+ I_C_Location.COLUMNNAME_Address1 + "=? AND "
			+ I_C_Location.COLUMNNAME_City + "=? AND "
			+ I_C_Location.COLUMNNAME_Postal + "=? AND "
			+ I_C_Location.COLUMNNAME_C_Country_ID + "=?";

	public static final String SQL_BPARTNERLOCATION = "SELECT * FROM "
			+ I_C_BPartner_Location.Table_Name + " WHERE "
			+ I_C_BPartner_Location.COLUMNNAME_Name + "=?";

	public static final String SQL_DEFAULT_VENDOR = //
	"SELECT bp.* " //
			+ " FROM C_BPartner bp "
			+ "   LEFT JOIN M_Product_PO p ON p.C_BPartner_ID = bp.C_BPartner_ID "
			+ " WHERE p.M_Product_ID=? " + " ORDER BY p.IsCurrentVendor DESC";

	public static final String SQL_GREETING = //
	"SELECT * FROM " + I_C_Greeting.Table_Name + " WHERE "
			+ I_C_Greeting.COLUMNNAME_Name + "=?";

	public static final String SQL_ORG_BPARNTER = //
	" SELECT p.* " //
			+ " FROM C_BPartner p " //
			+ " WHERE p.AD_OrgBP_ID=?";

	private static final CLogger logger = CLogger.getCLogger(BPartnerPA.class);

	public I_C_BPartner retrieveBPartner(final String value,
			final String trxName) {

		if (value == null) {
			throw new IllegalArgumentException("Param 'value' may not be null");
		}
		final IDatabaseBL db = Services.get(IDatabaseBL.class);
		final List<X_C_BPartner> result = db.retrieveList(SQL_BPARTNER,
				new Object[] { value }, X_C_BPartner.class, trxName);

		if (result.isEmpty()) {
			logger.fine("Didn't find bPartner with value '" + value
					+ "'. Returning null.");
			return null;
		}
		logger.fine("Returning bPartner with value '" + value + "'");
		return result.get(0);
	}

	public I_C_Location retrieveLocation(final String address1,
			final String city, final String postal, final String countryCode,
			final String trxName) {

		final I_C_Country country = retrieveCountry(countryCode, trxName);

		final IDatabaseBL db = Services.get(IDatabaseBL.class);

		final List<X_C_Location> result = db.retrieveList(SQL_LOCATION,
				new Object[] { address1, city, postal,
						country.getC_Country_ID() }, X_C_Location.class,
				trxName);
		if (result.isEmpty()) {
			return null;
		}
		return result.get(0);
	}

	/**
	 * @throws an
	 *             IllegalArgumentException if there is no country for the given
	 *             country code.
	 */
	public I_C_Country retrieveCountry(final String countryCode,
			final String trxName) {

		if (countryCode == null) {
			throw new IllegalArgumentException(
					"Param 'countryCode' may not be null");
		}

		final IDatabaseBL db = Services.get(IDatabaseBL.class);

		final List<X_C_Country> result = db.retrieveList(SQL_SELECT_COUNTRY,
				new Object[] { countryCode }, X_C_Country.class, trxName);

		if (result.isEmpty()) {
			throw new IllegalArgumentException(
					"Unable to retrieve country with country code '"
							+ countryCode + "'");
		}
		return result.get(0);
	}

	public I_C_BPartner_Location retrieveBPartnerLocation(final String name,
			final String trxName) {

		if (name == null) {
			throw new IllegalArgumentException("Param 'name' may not be null");
		}

		final IDatabaseBL db = Services.get(IDatabaseBL.class);

		final List<X_C_BPartner_Location> result = db.retrieveList(
				SQL_BPARTNERLOCATION, new Object[] { name },
				X_C_BPartner_Location.class, trxName);

		if (result.isEmpty()) {
			logger.fine("Didn't find bPartnerLocation with name '" + name
					+ "'. Returning null.");
			return null;
		}
		logger.fine("Returning bPartnerLocationwith name '" + name + "'");
		return result.get(0);
	}

	public I_C_BPartner retrieveDefaultVendor(final int productId,
			final String trxName) throws ProductHasNoVendorException {

		final IDatabaseBL db = Services.get(IDatabaseBL.class);

		final List<X_C_BPartner> result = db.retrieveList(SQL_DEFAULT_VENDOR,
				new Object[] { productId }, X_C_BPartner.class, trxName);

		if (result.isEmpty()) {
			final MProduct prod = new MProduct(Env.getCtx(), productId, trxName);
			throw new ProductHasNoVendorException(
					"Unable to retrieve default vendor for product " + prod);
		}
		return result.get(0);

	}

	public I_C_BPartner retrieveBPartner(final int partnerId,
			final String trxName) {

		final I_C_BPartner result = new MBPartner(Env.getCtx(), partnerId,
				trxName);

		if (result.getC_BPartner_ID() == 0) {
			return null;
		}
		return result;
	}

	public List<I_C_BPartner_Location> retrieveBPartnerLocations(int partnerId,
			final boolean reload, final String trxName) {

		final MBPartner bPArtner = new MBPartner(Env.getCtx(), partnerId,
				trxName);
		final I_C_BPartner_Location[] locations = bPArtner.getLocations(reload);

		return Arrays.asList(locations);
	}

	public List<I_AD_User> retrieveContacts(int partnerId, boolean reload,
			String trxName) {

		final MBPartner bPArtner = new MBPartner(Env.getCtx(), partnerId,
				trxName);
		final I_AD_User[] users = bPArtner.getContacts(reload);

		return Arrays.asList(users);
	}

	/**
	 * Creates a new MUser and sets the following values from the given
	 * bPartner:
	 * <li>AD_Client_ID</li>
	 * <li>AD_Org_ID</li>
	 * <li>C_BPartner_ID</li>
	 * <li>Name</li>
	 */
	public MUser createNewContact(final I_C_BPartner partner,
			final String trxName) {

		final MUser contact = new MUser(Env.getCtx(), 0, trxName);

		final IPOService poService = Services.get(IPOService.class);
		poService.copyClientOrg(partner, contact);

		contact.setC_BPartner_ID(partner.getC_BPartner_ID());
		contact.setName(partner.getName());

		return contact;
	}

	public MBPartner createNewBPartner(final String trxName) {
		return new MBPartner(Env.getCtx(), 0, trxName);
	}

	public MBPartnerLocation createNewBPartnerLocation(final String trxName) {
		return new MBPartnerLocation(Env.getCtx(), 0, trxName);
	}

	public I_C_Location createNewLocation(final String trxName) {
		return new MLocation(Env.getCtx(), 0, trxName);
	}

	public I_C_Greeting createNewGreeting(String trxName) {
		return new X_C_Greeting(Env.getCtx(), 0, trxName);
	}

	public I_C_Greeting retrieveGreeting(String name, String trxName) {

		final IDatabaseBL db = Services.get(IDatabaseBL.class);

		final List<X_C_Greeting> result = db.retrieveList(SQL_GREETING,
				new Object[] { name }, X_C_Greeting.class, trxName);
		if (result.isEmpty()) {
			return null;
		}
		return result.get(0);
	}

	public I_C_BPartner_Location retrieveBPartnerLocation(
			final int partnerLocationId, final String trxName) {

		return new MBPartnerLocation(Env.getCtx(), partnerLocationId, trxName);
	}

	public I_C_BPartner retrieveOrgBPartner(int orgId, String trxName)
			throws OrgHasNoBPartnerLinkException {

		final IDatabaseBL databaseBL = Services.get(IDatabaseBL.class);

		final List<MBPartner> result = databaseBL.retrieveList(
				SQL_ORG_BPARNTER, new Object[] { orgId }, MBPartner.class,
				trxName);

		if (result.isEmpty()) {

			throw new OrgHasNoBPartnerLinkException(orgId);
		} else if (result.size() > 1) {

			logger.warning("Found more than one BPArtner for AD_Org_ID "
					+ orgId);
		}
		return result.get(0);
	}

	public I_C_Country retrieveCountry(final int countryId, final String trxName) {
		return new X_C_Country(Env.getCtx(), countryId, trxName);
	}

	public I_C_Location retrieveLocation(final int locationId,
			final String trxName) {
		return new X_C_Location(Env.getCtx(), locationId, trxName);
	}
}
