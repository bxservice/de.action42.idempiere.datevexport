package de.action42.idempiere.datev.form;

import java.awt.BorderLayout;
import java.awt.Cursor;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.FilenameFilter;
import java.util.Date;
import java.util.GregorianCalendar;

import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JTextField;

import org.compiere.apps.ADialog;
import org.compiere.apps.StatusBar;
import org.compiere.apps.form.FormFrame;
import org.compiere.apps.form.FormPanel;
import org.compiere.grid.ed.VComboBox;
import org.compiere.grid.ed.VDate;
import org.compiere.model.MClient;
import org.compiere.swing.CPanel;
import org.compiere.util.Env;
import org.compiere.util.Ini;
import org.compiere.util.KeyNamePair;
import org.compiere.util.Login;
import org.compiere.util.Msg;

import de.action42.idempiere.datev.AddOnStarter;
import de.action42.idempiere.datev.DatevProperties;
import de.action42.idempiere.datev.IDatevSettings;
import de.action42.idempiere.datev.service.IMasterDataService;
import de.action42.idempiere.datev.service.Worker;
import de.action42.idempiere.datev.service.impl.BewegungsDatenCompressor;
import de.action42.idempiere.datev.service.impl.FactAcctLoader;
import de.action42.idempiere.datev.service.impl.MasterDataService;

public class DatevDialog extends CPanel implements FormPanel, ActionListener {

	public static final String EXPORT = "datev.export";
	public static final String UNTIL = "datev.until";
	public static final String FROM = "datev.from";
	public static final String ZEITRAUM = "datev.timerange";
	public static final String SELECT_TARGET_DIR = "datev.select_target_dir";
	public static final String ERROR_DATES_ORDERING = "datev.err_dates_ordering";
	public static final String QUESTION_DELETE_EXITING = "datev_q_delete_existing";
	public static final String WARNING_NOTHING_EXPORTED = "datev.warn_nothing_exported";
	public static final String WARNING_FILE_DELETION_FAILED = "datev.warn_file_deletion_failed";
	public static final String ONE_RECORD_EXPORTED = "datev.one_record_exported";
	public static final String MULTIPLE_RECORD_EXPORTED = "datev.multiple_records_exported";
	public static final String ORG = "AD_Org_ID";
    // XXX a42 - AK -Abrechnungsnummer
	public static final String ABRECHNUNGSNUMMER = "datev.abrechnungsnummer";

	private static final long serialVersionUID = 5984372803441104882L;

	private FormFrame frame;

	private int window_no;

    // XXX a42 - AK -Abrechnungsnummer
    private final JLabel lAbrechnungsnummer = new JLabel();

    private final JTextField fAbrechnungsnummer = new JTextField();
            
    //
	private final JLabel lDateFrom = new JLabel();

	private final VDate fDateFrom = new VDate();

	private final JLabel lDateTo = new JLabel();

	private final VDate fDateTo = new VDate();

	private final JButton bExport = new JButton();

	private final GridBagLayout mainLayout = new GridBagLayout();

	private final StatusBar statusBar = new StatusBar();

	private final JLabel lOrgFilter = new JLabel();

	private final VComboBox cOrgFilter = new VComboBox();

	public void dispose() {
		frame.dispose();
	}

	public void init(final int myWindowNo, final FormFrame myFrame) {

		new AddOnStarter().initAddon();
		
		frame = myFrame;
		window_no = myWindowNo;

		JLabel lDates = new JLabel();
		lDates.setText(Msg.getMsg(Env.getCtx(), ZEITRAUM));
		lDateFrom.setText(Msg.getMsg(Env.getCtx(), FROM));
		lDateTo.setText(Msg.getMsg(Env.getCtx(), UNTIL));

		bExport.setText(Msg.getMsg(Env.getCtx(), EXPORT));
		bExport.addActionListener(this);

		this.setLayout(mainLayout);

		this.add(lDates, new GridBagConstraints(0, 0, 1, 1, 0.0, 0.0,
				GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(5,
						5, 5, 5), 0, 0));

		this.add(lDateFrom, new GridBagConstraints(0, 1, 1, 1, 0.0, 0.0,
				GridBagConstraints.EAST, GridBagConstraints.NONE, new Insets(5,
						5, 5, 5), 0, 0));

		// using a calendar to preset the dateFrom and dateTo fields
		GregorianCalendar calendar = new GregorianCalendar();
		calendar.setTime(new Date());
		calendar.roll(GregorianCalendar.MONTH, -1);
		if (calendar.get(GregorianCalendar.MONTH) == 11) {
			calendar.roll(GregorianCalendar.YEAR, -1);
		}

		calendar.set(GregorianCalendar.DAY_OF_MONTH, 1);

		fDateFrom.setValue(new Date(calendar.getTimeInMillis()));

		this.add(fDateFrom, new GridBagConstraints(1, 1, 1, 1, 0.0, 0.0,
				GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(5,
						5, 5, 5), 0, 0));

		this.add(lDateTo, new GridBagConstraints(0, 2, 1, 1, 0.0, 0.0,
				GridBagConstraints.EAST, GridBagConstraints.NONE, new Insets(5,
						5, 5, 5), 0, 0));

		calendar.set(GregorianCalendar.DAY_OF_MONTH, calendar
				.getActualMaximum(GregorianCalendar.DAY_OF_MONTH));

		fDateTo.setValue(new Date(calendar.getTimeInMillis()));
		this.add(fDateTo, new GridBagConstraints(1, 2, 1, 1, 0.0, 0.0,
				GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(5,
						5, 5, 5), 0, 0));

		//
		// Org filter

		lOrgFilter.setText(Msg.translate(Env.getCtx(), ORG));
		lOrgFilter.setLabelFor(cOrgFilter);

		this.add(lOrgFilter, new GridBagConstraints(0, 3, 1, 1, 0.0, 0.0,
				GridBagConstraints.EAST, GridBagConstraints.NONE, new Insets(5,
						5, 5, 5), 0, 0));
		this.add(cOrgFilter, new GridBagConstraints(1, 3, 1, 1, 0.0, 0.0,
				GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(5,
						5, 5, 5), 0, 0));

		// find out the orgs that are available to the user for selection
		final MClient client = MClient.get(Env.getCtx());
		final KeyNamePair clientKeyNamePair = new KeyNamePair(client.get_ID(),
				client.getName());
		final Login login = new Login(Env.getCtx());
		final KeyNamePair[] orgs = login.getOrgs(clientKeyNamePair);

		for (final KeyNamePair org : orgs) {
			cOrgFilter.addItem(org);
		}

		// XXX a42 - AK -Abrechnungsnummer
        lAbrechnungsnummer.setText(Msg.translate(Env.getCtx(), ABRECHNUNGSNUMMER));
        lAbrechnungsnummer.setLabelFor(fAbrechnungsnummer);
        fAbrechnungsnummer.setColumns(6);
        this.add(lAbrechnungsnummer, new GridBagConstraints(0, 4, 1, 1, 0.0, 0.0,
                        GridBagConstraints.EAST, GridBagConstraints.NONE, new Insets(5,
                                        5, 5, 5), 0, 0));
        this.add(fAbrechnungsnummer, new GridBagConstraints(1, 4, 1, 1, 0.0, 0.0,
                        GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(5,
                                        5, 5, 5), 0, 0));


		//
		// "Export" button
		this.add(bExport, new GridBagConstraints(1, 4, 1, 1, 0.0, 0.0,
				GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(5,
						5, 5, 5), 0, 0));

		frame.getContentPane().add(this, BorderLayout.CENTER);
		frame.getContentPane().add(statusBar, BorderLayout.SOUTH);

		statusBar.setStatusLine(" ");
	}

	/**
	 * Lock User Interface. Called from the Worker before processing
	 * 
	 * @param pi
	 *            process info
	 */
	public void lockUI() {
		bExport.setEnabled(false);
		fDateFrom.setEnabled(false);
		fDateTo.setEnabled(false);
		cOrgFilter.setEnabled(false);
		this.setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
		this.setEnabled(false);
	}

	/**
	 * Unlock User Interface. Called from the Worker when processing is done
	 * 
	 * @param pi
	 *            result of execute ASync call
	 */
	public void unlockUI() {
		bExport.setEnabled(true);
		this.setEnabled(true);
		fDateFrom.setEnabled(true);
		fDateTo.setEnabled(true);
		cOrgFilter.setEnabled(true);
		this.setCursor(Cursor.getDefaultCursor());
	}

	public void actionPerformed(final ActionEvent actionEvent) {

		final Date dateTo = (Date) fDateTo.getValue();
		final Date dateFrom = (Date) fDateFrom.getValue();
		if (dateFrom.after(dateTo)) {
			ADialog.error(window_no, this, "", ERROR_DATES_ORDERING);
			return;
		}
        // XXX a42 - AK
        String abrechnungsnummer = fAbrechnungsnummer.getText();
        if (abrechnungsnummer.length() > 6) {
                abrechnungsnummer = abrechnungsnummer.substring(0, 6);
        }
        if (abrechnungsnummer.length() < 6) {
                for (int i=abrechnungsnummer.length(); i < 6 ; i++) {
                        abrechnungsnummer = '0' + abrechnungsnummer;
                }
        }
        // end a42
		statusBar.setStatusToolTip(Msg.getMsg(Env.getCtx(), SELECT_TARGET_DIR));

		final String startDir = Ini.getAdempiereHome() + File.separator
				+ "data";
		final JFileChooser chooser = new JFileChooser(startDir);
		chooser.setMultiSelectionEnabled(false);
		chooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);

		int returnVal = chooser.showSaveDialog(this);
		if (returnVal != JFileChooser.APPROVE_OPTION) {
			return;
		}

		final File exportDir = chooser.getSelectedFile();
		final String strExportDir = exportDir.getAbsolutePath();

		final FilenameFilter datevFileFilter = new FilenameFilter() {

			public boolean accept(File dir, String name) {
				return "DV01".equals(name)
						|| (name != null && name.matches("DE[0-9][0-9][0-9]"));
			}

		};
//		final String[] list = exportDir.list(datevFileFilter);
//		if (list.length > 0) {
//			boolean delete = ADialog.ask(window_no, this,
//					QUESTION_DELETE_EXITING);
//			if (!delete) {
//				return;
//			} else {
//
//				for (int i = 0; i < list.length; i++) {
//					final File datevFile = new File(exportDir, list[i]);
//
//					if (!datevFile.delete()) {
//						boolean goOn = ADialog.ask(window_no, this, Msg.getMsg(
//								Env.getCtx(), WARNING_FILE_DELETION_FAILED,
//								new Object[] { datevFile.getAbsolutePath() }));
//						if (!goOn) {
//							return;
//						}
//					}
//				}
//			}
//		}

		statusBar.setStatusToolTip("Exportiere nach " + strExportDir);
		statusBar.setStatusToolTip("Exportiere");
		this.setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));

		final IDatevSettings settings = new DatevProperties();

        // XXX a42 - AK
        settings.setAbrechnungsnummer(abrechnungsnummer);
        //

		final KeyNamePair selectedOrg = (KeyNamePair) cOrgFilter
				.getSelectedItem();

		final int clientId = Env.getAD_Client_ID(Env.getCtx());
		final int orgId = selectedOrg.getKey();

		final Worker worker = new Worker( //
				exportDir, //
				orgId, //
				(Date) fDateFrom.getValue(), //
				(Date) fDateTo.getValue(), settings);

		final IMasterDataService masterDataService = new MasterDataService(
				exportDir, settings);
		worker.setMasterDataService(masterDataService);
		worker.setProcessor(new BewegungsDatenCompressor());
		worker.setLoader(new FactAcctLoader(clientId, orgId,
						masterDataService));

		final Runnable runnable = new Runnable() {

			public void run() {

				try {
					//worker.exportData();
					worker.exportDataCSV();
				} catch (Exception e) {
					ADialog.error(window_no, DatevDialog.this, "Error while Exporting!", e.getMessage());
					e.printStackTrace();
				} finally {
					unlockUI();
				}
				int exportedRecords = worker.getExportedRecords();
				if (exportedRecords == 0) {
					ADialog.info(window_no, DatevDialog.this,
							WARNING_NOTHING_EXPORTED);
				}
				DatevDialog.this.setCursor(Cursor.getDefaultCursor());
				String msg = null;
				if (exportedRecords == 1) {
					msg = Msg.getMsg(Env.getCtx(), ONE_RECORD_EXPORTED);
				} else {
					msg = Msg.getMsg(Env.getCtx(), MULTIPLE_RECORD_EXPORTED,
							new Object[] { exportedRecords });
				}
				statusBar.setStatusLine(msg);
			}
		};

		Thread t = new Thread(runnable);
		t.setName("DatevExport");
		lockUI();
		t.start();
		statusBar.setStatusLine("Exporting...");
	}
}
