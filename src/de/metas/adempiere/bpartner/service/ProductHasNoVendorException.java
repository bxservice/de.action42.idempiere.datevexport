package de.metas.adempiere.bpartner.service;

public final class ProductHasNoVendorException extends Exception {

	private static final long serialVersionUID = -7583112372829053131L;

	public ProductHasNoVendorException(String message, Throwable cause) {
		super(message, cause);
	}

	public ProductHasNoVendorException(String message) {
		super(message);
	}
}
