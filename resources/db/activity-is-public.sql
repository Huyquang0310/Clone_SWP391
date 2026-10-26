-- Run once on the SWP391 database. Existing activities remain public by default.
IF COL_LENGTH('dbo.extracurricular_activity', 'is_public') IS NULL
BEGIN
    ALTER TABLE dbo.extracurricular_activity
    ADD is_public BIT NOT NULL CONSTRAINT DF_extracurricular_activity_is_public DEFAULT (1);
END;
GO
-- Activities that require a registration form stay hidden until the form is saved.
UPDATE dbo.extracurricular_activity
SET is_public = CASE WHEN registration_form_required = 1 THEN 0 ELSE 1 END;
GO
