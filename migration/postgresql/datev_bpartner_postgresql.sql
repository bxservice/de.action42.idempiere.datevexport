-- Jan 25, 2011 12:59:26 PM CET
-- DATEV Export
INSERT INTO AD_Element (AD_Client_ID,AD_Element_ID,AD_Org_ID,ColumnName,Created,CreatedBy,Description,EntityType,IsActive,Name,PrintName,Updated,UpdatedBy) VALUES (0,1000296,0,'DebtorId',TO_DATE('2011-01-25 12:59:25','YYYY-MM-DD HH24:MI:SS'),100,'DATEV Debitor Account No.','U','Y','DebtorId','DebtorId',TO_DATE('2011-01-25 12:59:25','YYYY-MM-DD HH24:MI:SS'),100)
;

-- Jan 25, 2011 12:59:26 PM CET
-- DATEV Export
INSERT INTO AD_Element_Trl (AD_Language,AD_Element_ID, Description,Help,Name,PO_Description,PO_Help,PO_Name,PO_PrintName,PrintName, IsTranslated,AD_Client_ID,AD_Org_ID,Created,Createdby,Updated,UpdatedBy) SELECT l.AD_Language,t.AD_Element_ID, t.Description,t.Help,t.Name,t.PO_Description,t.PO_Help,t.PO_Name,t.PO_PrintName,t.PrintName, 'N',t.AD_Client_ID,t.AD_Org_ID,t.Created,t.Createdby,t.Updated,t.UpdatedBy FROM AD_Language l, AD_Element t WHERE l.IsActive='Y' AND l.IsSystemLanguage='Y' AND l.IsBaseLanguage='N' AND t.AD_Element_ID=1000296 AND NOT EXISTS (SELECT * FROM AD_Element_Trl tt WHERE tt.AD_Language=l.AD_Language AND tt.AD_Element_ID=t.AD_Element_ID)
;

-- Jan 25, 2011 1:00:04 PM CET
-- DATEV Export
INSERT INTO AD_Element (AD_Client_ID,AD_Element_ID,AD_Org_ID,ColumnName,Created,CreatedBy,Description,EntityType,IsActive,Name,PrintName,Updated,UpdatedBy) VALUES (0,1000297,0,'CreditorId',TO_DATE('2011-01-25 13:00:04','YYYY-MM-DD HH24:MI:SS'),100,'DATEV Creditor Account No.','U','Y','CreditorId','CreditorId',TO_DATE('2011-01-25 13:00:04','YYYY-MM-DD HH24:MI:SS'),100)
;

-- Jan 25, 2011 1:00:04 PM CET
-- DATEV Export
INSERT INTO AD_Element_Trl (AD_Language,AD_Element_ID, Description,Help,Name,PO_Description,PO_Help,PO_Name,PO_PrintName,PrintName, IsTranslated,AD_Client_ID,AD_Org_ID,Created,Createdby,Updated,UpdatedBy) SELECT l.AD_Language,t.AD_Element_ID, t.Description,t.Help,t.Name,t.PO_Description,t.PO_Help,t.PO_Name,t.PO_PrintName,t.PrintName, 'N',t.AD_Client_ID,t.AD_Org_ID,t.Created,t.Createdby,t.Updated,t.UpdatedBy FROM AD_Language l, AD_Element t WHERE l.IsActive='Y' AND l.IsSystemLanguage='Y' AND l.IsBaseLanguage='N' AND t.AD_Element_ID=1000297 AND NOT EXISTS (SELECT * FROM AD_Element_Trl tt WHERE tt.AD_Language=l.AD_Language AND tt.AD_Element_ID=t.AD_Element_ID)
;

-- Jan 25, 2011 1:00:31 PM CET
-- DATEV Export
INSERT INTO AD_Column (AD_Client_ID,AD_Column_ID,AD_Element_ID,AD_Org_ID,AD_Reference_ID,AD_Table_ID,ColumnName,Created,CreatedBy,Description,EntityType,FieldLength,IsActive,IsAllowLogging,IsAlwaysUpdateable,IsAutocomplete,IsEncrypted,IsIdentifier,IsKey,IsMandatory,IsParent,IsSelectionColumn,IsSyncDatabase,IsTranslated,IsUpdateable,Name,Updated,UpdatedBy,Version) VALUES (0,1002368,1000296,0,10,291,'DebtorId',TO_DATE('2011-01-25 13:00:31','YYYY-MM-DD HH24:MI:SS'),100,'DATEV Debitor Account No.','U',40,'Y','Y','N','N','N','N','N','Y','N','N','Y','N','Y','DebtorId',TO_DATE('2011-01-25 13:00:31','YYYY-MM-DD HH24:MI:SS'),100,0)
;

-- Jan 25, 2011 1:00:31 PM CET
-- DATEV Export
INSERT INTO AD_Column_Trl (AD_Language,AD_Column_ID, Name, IsTranslated,AD_Client_ID,AD_Org_ID,Created,Createdby,Updated,UpdatedBy) SELECT l.AD_Language,t.AD_Column_ID, t.Name, 'N',t.AD_Client_ID,t.AD_Org_ID,t.Created,t.Createdby,t.Updated,t.UpdatedBy FROM AD_Language l, AD_Column t WHERE l.IsActive='Y' AND l.IsSystemLanguage='Y' AND l.IsBaseLanguage='N' AND t.AD_Column_ID=1002368 AND NOT EXISTS (SELECT * FROM AD_Column_Trl tt WHERE tt.AD_Language=l.AD_Language AND tt.AD_Column_ID=t.AD_Column_ID)
;

-- Jan 25, 2011 1:00:56 PM CET
-- DATEV Export
INSERT INTO AD_Column (AD_Client_ID,AD_Column_ID,AD_Element_ID,AD_Org_ID,AD_Reference_ID,AD_Table_ID,ColumnName,Created,CreatedBy,Description,EntityType,FieldLength,IsActive,IsAllowLogging,IsAlwaysUpdateable,IsAutocomplete,IsEncrypted,IsIdentifier,IsKey,IsMandatory,IsParent,IsSelectionColumn,IsSyncDatabase,IsTranslated,IsUpdateable,Name,Updated,UpdatedBy,Version) VALUES (0,1002369,1000297,0,10,291,'CreditorId',TO_DATE('2011-01-25 13:00:56','YYYY-MM-DD HH24:MI:SS'),100,'DATEV Creditor Account No.','U',40,'Y','Y','N','N','N','N','N','Y','N','N','Y','N','Y','CreditorId',TO_DATE('2011-01-25 13:00:56','YYYY-MM-DD HH24:MI:SS'),100,0)
;

-- Jan 25, 2011 1:00:56 PM CET
-- DATEV Export
INSERT INTO AD_Column_Trl (AD_Language,AD_Column_ID, Name, IsTranslated,AD_Client_ID,AD_Org_ID,Created,Createdby,Updated,UpdatedBy) SELECT l.AD_Language,t.AD_Column_ID, t.Name, 'N',t.AD_Client_ID,t.AD_Org_ID,t.Created,t.Createdby,t.Updated,t.UpdatedBy FROM AD_Language l, AD_Column t WHERE l.IsActive='Y' AND l.IsSystemLanguage='Y' AND l.IsBaseLanguage='N' AND t.AD_Column_ID=1002369 AND NOT EXISTS (SELECT * FROM AD_Column_Trl tt WHERE tt.AD_Language=l.AD_Language AND tt.AD_Column_ID=t.AD_Column_ID)
;

-- Jan 25, 2011 1:01:12 PM CET
-- DATEV Export
ALTER TABLE C_BPartner ADD DebtorId NVARCHAR2(40) NOT NULL
;

-- Jan 25, 2011 1:01:33 PM CET
-- DATEV Export
UPDATE AD_Column SET DefaultValue='@Value@', IsMandatory='N',Updated=TO_DATE('2011-01-25 13:01:33','YYYY-MM-DD HH24:MI:SS'),UpdatedBy=100 WHERE AD_Column_ID=1002368
;

-- Jan 25, 2011 1:01:41 PM CET
-- DATEV Export
ALTER TABLE C_BPartner ADD DebtorId NVARCHAR2(40) DEFAULT NULL 
;

-- Jan 25, 2011 1:01:57 PM CET
-- DATEV Export
UPDATE AD_Column SET DefaultValue='@Value@', IsMandatory='N',Updated=TO_DATE('2011-01-25 13:01:57','YYYY-MM-DD HH24:MI:SS'),UpdatedBy=100 WHERE AD_Column_ID=1002369
;

-- Jan 25, 2011 1:02:02 PM CET
-- DATEV Export
ALTER TABLE C_BPartner ADD CreditorId NVARCHAR2(40) DEFAULT NULL 
;

-- Jan 25, 2011 1:03:27 PM CET
-- DATEV Export
INSERT INTO AD_Field (AD_Client_ID,AD_Column_ID,AD_Field_ID,AD_Org_ID,AD_Tab_ID,Created,CreatedBy,Description,DisplayLength,EntityType,IsActive,IsCentrallyMaintained,IsDisplayed,IsEncrypted,IsFieldOnly,IsHeading,IsReadOnly,IsSameLine,Name,SeqNo,SortNo,Updated,UpdatedBy) VALUES (0,1002368,1003083,0,223,TO_DATE('2011-01-25 13:03:27','YYYY-MM-DD HH24:MI:SS'),100,'DATEV Debitor Account No.',0,'U','Y','Y','Y','N','N','N','N','N','DebtorId',270,0,TO_DATE('2011-01-25 13:03:27','YYYY-MM-DD HH24:MI:SS'),100)
;

-- Jan 25, 2011 1:03:27 PM CET
-- DATEV Export
INSERT INTO AD_Field_Trl (AD_Language,AD_Field_ID, Description,Help,Name, IsTranslated,AD_Client_ID,AD_Org_ID,Created,Createdby,Updated,UpdatedBy) SELECT l.AD_Language,t.AD_Field_ID, t.Description,t.Help,t.Name, 'N',t.AD_Client_ID,t.AD_Org_ID,t.Created,t.Createdby,t.Updated,t.UpdatedBy FROM AD_Language l, AD_Field t WHERE l.IsActive='Y' AND l.IsSystemLanguage='Y' AND l.IsBaseLanguage='N' AND t.AD_Field_ID=1003083 AND NOT EXISTS (SELECT * FROM AD_Field_Trl tt WHERE tt.AD_Language=l.AD_Language AND tt.AD_Field_ID=t.AD_Field_ID)
;

-- Jan 25, 2011 1:03:45 PM CET
-- DATEV Export
UPDATE AD_Field SET DisplayLogic='@IsCustomer@=Y',Updated=TO_DATE('2011-01-25 13:03:45','YYYY-MM-DD HH24:MI:SS'),UpdatedBy=100 WHERE AD_Field_ID=1003083
;

-- Jan 25, 2011 1:04:28 PM CET
-- DATEV Export
INSERT INTO AD_Field (AD_Client_ID,AD_Column_ID,AD_Field_ID,AD_Org_ID,AD_Tab_ID,Created,CreatedBy,Description,DisplayLength,DisplayLogic,EntityType,IsActive,IsCentrallyMaintained,IsDisplayed,IsEncrypted,IsFieldOnly,IsHeading,IsReadOnly,IsSameLine,Name,SeqNo,SortNo,Updated,UpdatedBy) VALUES (0,1002369,1003084,0,224,TO_DATE('2011-01-25 13:04:27','YYYY-MM-DD HH24:MI:SS'),100,'DATEV Creditor Account No.',0,'@IsVendor@=''Y''','U','Y','Y','Y','N','N','N','N','N','CreditorId',130,0,TO_DATE('2011-01-25 13:04:27','YYYY-MM-DD HH24:MI:SS'),100)
;

-- Jan 25, 2011 1:04:28 PM CET
-- DATEV Export
INSERT INTO AD_Field_Trl (AD_Language,AD_Field_ID, Description,Help,Name, IsTranslated,AD_Client_ID,AD_Org_ID,Created,Createdby,Updated,UpdatedBy) SELECT l.AD_Language,t.AD_Field_ID, t.Description,t.Help,t.Name, 'N',t.AD_Client_ID,t.AD_Org_ID,t.Created,t.Createdby,t.Updated,t.UpdatedBy FROM AD_Language l, AD_Field t WHERE l.IsActive='Y' AND l.IsSystemLanguage='Y' AND l.IsBaseLanguage='N' AND t.AD_Field_ID=1003084 AND NOT EXISTS (SELECT * FROM AD_Field_Trl tt WHERE tt.AD_Language=l.AD_Language AND tt.AD_Field_ID=t.AD_Field_ID)
;

-- Jan 25, 2011 1:34:03 PM CET
-- DATEV Export
UPDATE AD_Column SET AD_Reference_ID=11, FieldLength=10,Updated=TO_DATE('2011-01-25 13:34:03','YYYY-MM-DD HH24:MI:SS'),UpdatedBy=100 WHERE AD_Column_ID=1002369
;

-- Jan 25, 2011 1:34:08 PM CET
-- DATEV Export
ALTER TABLE C_BPartner MODIFY CreditorId NUMBER(10) DEFAULT NULL 
;

-- Jan 25, 2011 1:35:25 PM CET
-- DATEV Export
ALTER TABLE C_BPartner MODIFY CreditorId NUMBER(10) DEFAULT NULL 
;

-- Jan 25, 2011 1:35:59 PM CET
-- DATEV Export
UPDATE AD_Column SET DefaultValue=NULL,Updated=TO_DATE('2011-01-25 13:35:59','YYYY-MM-DD HH24:MI:SS'),UpdatedBy=100 WHERE AD_Column_ID=1002369
;

-- Jan 25, 2011 1:36:26 PM CET
-- DATEV Export
UPDATE AD_Column SET AD_Reference_ID=11, DefaultValue=NULL, FieldLength=10,Updated=TO_DATE('2011-01-25 13:36:26','YYYY-MM-DD HH24:MI:SS'),UpdatedBy=100 WHERE AD_Column_ID=1002368
;

-- Jan 25, 2011 1:36:30 PM CET
-- DATEV Export
ALTER TABLE C_BPartner MODIFY DebtorId NUMBER(10) DEFAULT NULL 
;

