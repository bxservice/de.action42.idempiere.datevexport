package de.metas.adempiere.bpartner.service;

import java.util.List;

import org.compiere.model.I_AD_User;
import org.compiere.model.I_C_BPartner;
import org.compiere.model.I_C_BPartner_Location;
import org.compiere.model.I_C_Country;
import org.compiere.model.I_C_Greeting;
import org.compiere.model.I_C_Location;

public interface IBPartnerPA {

	/**
	 * 
	 * @param value
	 * @return may return <code>null</code> if there is no matching bpartner
	 * 
	 */
	I_C_BPartner retrieveBPartner(String value, String trxName);

	/**
	 * 
	 * @param bPartnerId
	 * @return may return <code>null</code> if there is no matching bpartner
	 * 
	 */
	I_C_BPartner retrieveBPartner(int bPartnerId, String trxName);

	I_C_BPartner retrieveOrgBPartner(int orgId, String trxName)
			throws OrgHasNoBPartnerLinkException;

	I_C_Location retrieveLocation(String address1, String city, String postal,
			String countryCode, String trxName);

	I_C_Location retrieveLocation(int locationId, String trxName);
	
	I_C_Location createNewLocation(String trxName);

	/**
	 * 
	 * @param countryCode
	 * @param trxName
	 * @return
	 */
	I_C_Country retrieveCountry(String countryCode, String trxName);

	I_C_Country retrieveCountry(int countryId, String trxName);
	
	/**
	 * 
	 * @param name
	 *            the name of a business partner location.
	 * @param trxName
	 * @return may return <code>null</code> if there is no matching
	 *         bPartnerLocation
	 */
	I_C_BPartner_Location retrieveBPartnerLocation(String name, String trxName);

	I_C_BPartner_Location retrieveBPartnerLocation(int bPartnerLocationId,
			String trxName);

	I_C_BPartner_Location createNewBPartnerLocation(String trxName);

	List<I_C_BPartner_Location> retrieveBPartnerLocations(int bPartnerId,
			boolean reload, String trxName);

	List<I_AD_User> retrieveContacts(int bPartnerId, boolean reload,
			String trxName);

	I_AD_User createNewContact(I_C_BPartner bPartner, String trxName);

	I_C_BPartner retrieveDefaultVendor(int productId, String trxName)
			throws ProductHasNoVendorException;

	I_C_BPartner createNewBPartner(String trxName);

	I_C_Greeting retrieveGreeting(String name, String trxName);

	I_C_Greeting createNewGreeting(String trxName);

}
