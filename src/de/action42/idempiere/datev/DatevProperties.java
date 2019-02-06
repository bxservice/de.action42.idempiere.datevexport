package de.action42.idempiere.datev;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

import de.action42.idempiere.datev.service.Worker;

public class DatevProperties implements IDatevSettings {

	public final String PROPERTIES_FILE = "datev.properties";

	private Properties properties = new Properties();

	public DatevProperties() {
		try {
			readProperties();
		} catch (IOException e) {
			throw new DatevException("Can't load settings from file "
					+ PROPERTIES_FILE, e);
		}
	}

	private void readProperties() throws IOException {
		InputStream propertiesInput = Worker.class.getClassLoader()
				.getResourceAsStream(PROPERTIES_FILE);
		properties.load(propertiesInput);
	}

	public int getAbrechnungsnummer() {
		return Integer.parseInt(properties
				.getProperty("datev.abrechnungsnummer"));
	}

	public String getBeratername() {
		return properties.getProperty("datev.beratername");
	}

	public int getBeraternummer() {
		return Integer.parseInt(properties.getProperty("datev.beraternummer"));
	}

	public String getDatentraegernummer() {
		return properties.getProperty("datev.datentraegernummer");
	}

	public int getMandantennummer() {
		return Integer
				.parseInt(properties.getProperty("datev.mandantennummer"));
	}

	public String getNamenskuerzel() {
		return properties.getProperty("datev.namenskuerzel");
	}

	public String getPasswort() {
		return properties.getProperty("datev.passwort");
	}

	public short getPrimanotaseite() {
		return Short.parseShort(properties.getProperty("datev.primanotaseite"));
	}

	// XXX a42 - AK - make abrechnungsnummer changeable
    public void setAbrechnungsnummer(String abrechnungsnummer) {
    	properties.setProperty("datev.abrechnungsnummer",abrechnungsnummer);
    	return;
    }

}
