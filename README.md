# de.action42.idempiere.datevexport

Use branch idempiere-8.2

Apply migration and fill relevant BPartners DebtorId and/or CreditorId as needed

Import A42DatevExport 2pack and set export directory in SysConfig

Import xx_revenue_acct element / column / field 2packs and set the respective RevenueAccount for each TaxRate in the its Accounting tab. This will be used when exporting

On the import side ("ASCII-Import") you should define your own format, containing only the fields
- Umsatz
- Soll/Haben-Kennzeichen
- WKZ Umsatz
- Kurs
- Basis-Umsatz
- WKZ Basis-Umsatz
- Kontonummer
- Gegenkonto (ohne BU-Schlüssel)
- BU-Schluessel
- Belegdatum
- Belegfeld 1
- Buchungstext
- Kost 1
- UStID
- Festschreibung

Have a look at de.action42.idempiere.datev.io.CSV_Bewegungssatzdatei.appendBuchungssatz if you need more or other data to be exported
