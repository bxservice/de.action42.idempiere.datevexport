package de.metas.adempiere.bpartner.service;

public final class OrgHasNoBPartnerLinkException extends Exception {

	private static final long serialVersionUID = -8629504530492929450L;

	public final int orgId;

	public OrgHasNoBPartnerLinkException(final int orgId) {
		super();
		this.orgId = orgId;
	}

	public OrgHasNoBPartnerLinkException(final int orgId, final String message) {
		super(message);
		this.orgId = orgId;
	}
}
