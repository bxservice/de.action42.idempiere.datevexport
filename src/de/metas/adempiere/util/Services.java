package de.metas.adempiere.util;

import java.util.HashMap;
import java.util.Map;

import org.compiere.util.CLogger;

public class Services {

	private final static CLogger logger = CLogger.getCLogger(Services.class);

	private static Map<Class<?>, Object> services = new HashMap<Class<?>, Object>();

	@SuppressWarnings("unchecked")
	public static <T> T get(final Class<T> clazz) {

		final T service = (T) services.get(clazz);
		if (service == null) {
			logger.saveError("No service is registered for " + clazz, "");
		}
		return service;
	}

	public static <T> void registerService(final Class<T> clazz, final T service) {

		logger.info("Registering service " + service + " (class "
				+ service.getClass().getName() + ") for " + clazz);

		if (!clazz.isInterface()) {
			throw new IllegalArgumentException(
					"Parameter 'clazz' must be an interface class. clazz is"
							+ clazz.getName());
		}
		if (!clazz.isAssignableFrom(service.getClass())) {
			throw new IllegalArgumentException("Service " + service
					+ " must implement interface " + clazz);
		}
		services.put(clazz, service);
	}

}
