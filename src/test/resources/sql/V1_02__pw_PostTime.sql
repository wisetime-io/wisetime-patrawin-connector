
CREATE OR ALTER PROCEDURE pw_PostTime
  -- (1) Case ID or (2) client ID to post time to, matches (1) column
  -- arendenr from table ARENDE_1 or (2) from client ref table.
  -- If not found, return error CASE_OR_CLIENT_ID_NOT_FOUND
  @case_or_client_id nvarchar(50),
  -- Username sent if we have one, email otherwise. If neither found,
  -- return error USER_NOT_FOUND
  @username_or_email nvarchar(50),
  -- If activity code is not found, return ACTIVITY_CODE_NOT_FOUND 
  @activity_code int,
  -- A formatted narrative for the time being recorded.
  -- Use Windows line breaks.
  --
  -- Example narrative:
  --
  --   Replying email regarding purchasing agreement and finalising same
  --   13:12 [5 mins] � Outlook � [P1033] Re: Latest numbers
  --   13:17 [11 mins] � Chrome � About ACME Corp
  --   13:22 [2 mins] � Finder � P1033
  --   13:25 [33 mins] � Word � ACME Corp Purchase Agreement [P1033]
  --   15:01 [7 mins] � Outlook � [P1033] Finalised ACME Corp Agreement
  @narrative nvarchar(max),
  -- Pass additional notes for the posting, may be discarded by procedure
  @narrative_internal_note nvarchar(max),
  -- Start time of activity. We prefer to use the time zone aware type. If
  -- you prefer the datetime type, we need to know how to query for the
  -- time zone that we should use to convert from UTC to the local
  -- datetime.
  @start_time datetimeoffset,
  -- Total duration of worked time the posted time group, in seconds
  -- (not used at present)
  @total_time_secs bigint,
  -- Duration of the time in the time group to be charged (converted to
  -- minutes in procedure)
  @chargeable_time_secs bigint
AS
	SET NOCOUNT ON

	DECLARE
		@userId int,
		@caseNo nvarchar(50),
		@clientNo nvarchar(7),
		@clientIsBlocked bit,
		@serviceNo int,
		@id int

	SELECT
		@caseNo = K25.Arendenr,
		@clientNo = K24.Kundnr,
		@clientIsBlocked = CASE C334.[Type] WHEN 2 THEN 1 ELSE 0 END
	FROM
		[dbo].[KUND_ARENDE_25] K25
		INNER JOIN [dbo].[KUND_24] K24 ON K25.Kundnr = K24.Kundnr
		INNER JOIN [dbo].[CREDIT_LEVEL_334] C334 ON K24.Kreditjn = C334.Creditcode
	WHERE
		K25.Part = 1 AND
		K25.Kundtyp = 2 AND
		K25.Arendenr = @case_or_client_id


	IF @clientNo IS NULL
		SELECT
			@clientNo = K24.Kundnr,
			@clientIsBlocked = CASE C334.[Type] WHEN 2 THEN 1 ELSE 0 END
		FROM
			[dbo].[KUND_24] K24
			INNER JOIN [dbo].[CREDIT_LEVEL_334] C334 ON K24.Kreditjn = C334.Creditcode
		WHERE
			K24.Kundnr = @case_or_client_id

	IF @clientNo IS NULL
		RETURN -1 -- CASE_OR_CLIENT_ID_NOT_FOUND

	IF @clientIsBlocked = 1
		RETURN -2 -- CLIENT_BLOCKED

	SELECT TOP 1 @userId = Id
	FROM
		[dbo].[BEHORIG_50]
	WHERE
		(Username = @username_or_email OR Email = @username_or_email) AND
		Isactive = 1
	ORDER BY
		CASE WHEN Username = @username_or_email THEN 0 ELSE 1 END

	IF @userId IS NULL
		RETURN -3  -- USER_NOT_FOUND_OR_INACTIVE

	SELECT @serviceNo = Fakturatextnr FROM [dbo].[FAKTURATEXTNR_15] WHERE Fakturatextnr = @activity_code AND Inaktiv = 0

	IF @serviceNo IS NULL
		RETURN -4 -- ACTIVITY_CODE_NOT_FOUND_OR_INACTIVE

	INSERT [dbo].[PENDING_TIME_335] ([User_Id], [Arendenr], [Kundnr], [StartTimeUtc], [Minutes], [Fakturatextnr], [Text])
	VALUES (@userId, @caseNo, @clientNo, CONVERT(datetime2, @start_time, 1), @chargeable_time_secs / 60, @serviceNo, @narrative)

	SET @id = SCOPE_IDENTITY()

	RETURN @id -- SUCCESS, returns Id of inserted record
GO

