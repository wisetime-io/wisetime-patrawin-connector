-- A schema dump of selected tables from Patrawin version 13.2.10.0 (-2), taken on 27th February 2019

SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO
CREATE TABLE [ACCOUNTING_HISTORY_240](
	[Verifnr] [int] NULL,
	[Bokfdatum] [datetime] NULL,
	[Kontonr] [nvarchar](6) NULL,
	[Intaktstalle] [nvarchar](6) NULL,
	[Kostnadsbarare] [nvarchar](6) NULL,
	[Projekt] [nvarchar](100) NULL,
	[Faktnr] [int] NULL,
	[Kundnr] [nvarchar](7) NULL,
	[Arendenr] [nvarchar](50) NULL,
	[Belopp] [decimal](11, 2) NULL,
	[Belopputl] [decimal](11, 2) NULL,
	[Fritext] [nvarchar](max) NULL
) ON [PRIMARY] TEXTIMAGE_ON [PRIMARY]
GO
SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO
CREATE TABLE [ACTION_PROFILE_318](
	[Id] [int] IDENTITY(1,1) NOT NULL,
	[User_Id] [int] NOT NULL,
	[Name] [nvarchar](100) NOT NULL,
	[Description] [nvarchar](max) NULL,
	[ActionSelection] [int] NOT NULL,
	[AnnuitySelection] [int] NOT NULL,
	[NumberOfDays] [int] NOT NULL,
	[Rowid] [timestamp] NOT NULL,
	[IsFilterWarningSuppressed] [bit] NOT NULL,
	[TrademarkRenewalSelection] [int] NOT NULL,
	[DesignRenewalSelection] [int] NOT NULL,
 CONSTRAINT [PK_ACTION_PROFILE_318] PRIMARY KEY CLUSTERED
(
	[Id] ASC
)WITH (PAD_INDEX = OFF, STATISTICS_NORECOMPUTE = OFF, IGNORE_DUP_KEY = OFF, ALLOW_ROW_LOCKS = ON, ALLOW_PAGE_LOCKS = ON) ON [PRIMARY]
) ON [PRIMARY] TEXTIMAGE_ON [PRIMARY]
GO
SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO
CREATE TABLE [ACTION_PROFILE_ASSISTANTS_321](
	[Id] [int] IDENTITY(1,1) NOT NULL,
	[ActionProfile_Id] [int] NOT NULL,
	[Assistant_Id] [int] NULL,
 CONSTRAINT [PK_ACTION_PROFILE_ASSISTANTS_321] PRIMARY KEY CLUSTERED
(
	[Id] ASC
)WITH (PAD_INDEX = OFF, STATISTICS_NORECOMPUTE = OFF, IGNORE_DUP_KEY = OFF, ALLOW_ROW_LOCKS = ON, ALLOW_PAGE_LOCKS = ON) ON [PRIMARY]
) ON [PRIMARY]
GO
SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO
CREATE TABLE [ACTION_PROFILE_ATTORNEYS_325](
	[Id] [int] IDENTITY(1,1) NOT NULL,
	[ActionProfile_Id] [int] NOT NULL,
	[Attorney_Id] [int] NULL,
 CONSTRAINT [PK_ACTION_PROFILE_ATTORNEYS_325] PRIMARY KEY CLUSTERED
(
	[Id] ASC
)WITH (PAD_INDEX = OFF, STATISTICS_NORECOMPUTE = OFF, IGNORE_DUP_KEY = OFF, ALLOW_ROW_LOCKS = ON, ALLOW_PAGE_LOCKS = ON) ON [PRIMARY]
) ON [PRIMARY]
GO
SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO
CREATE TABLE [ACTION_PROFILE_EXCLUDED_ACTIONTYPES_323](
	[Id] [int] IDENTITY(1,1) NOT NULL,
	[ActionProfile_Id] [int] NOT NULL,
	[Fristkod] [nvarchar](15) NOT NULL,
 CONSTRAINT [PK_ACTION_PROFILE_EXCLUDED_ACTIONTYPES_323] PRIMARY KEY CLUSTERED
(
	[Id] ASC
)WITH (PAD_INDEX = OFF, STATISTICS_NORECOMPUTE = OFF, IGNORE_DUP_KEY = OFF, ALLOW_ROW_LOCKS = ON, ALLOW_PAGE_LOCKS = ON) ON [PRIMARY]
) ON [PRIMARY]
GO
SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO
CREATE TABLE [ACTION_PROFILE_INCOMEUNITS_322](
	[Id] [int] IDENTITY(1,1) NOT NULL,
	[ActionProfile_Id] [int] NOT NULL,
	[Intaktstalle] [nvarchar](6) NOT NULL,
 CONSTRAINT [PK_ACTION_PROFILE_INCOMEUNITS_322] PRIMARY KEY CLUSTERED
(
	[Id] ASC
)WITH (PAD_INDEX = OFF, STATISTICS_NORECOMPUTE = OFF, IGNORE_DUP_KEY = OFF, ALLOW_ROW_LOCKS = ON, ALLOW_PAGE_LOCKS = ON) ON [PRIMARY]
) ON [PRIMARY]
GO
SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO
CREATE TABLE [ACTION_PROFILE_RESPONSIBLE_ASSISTANTS_320](
	[Id] [int] IDENTITY(1,1) NOT NULL,
	[ActionProfileResponsible_Id] [int] NOT NULL,
	[Assistant_Id] [int] NULL,
 CONSTRAINT [PK_ACTION_PROFILE_RESPONSIBLE_ASSISTANTS_320] PRIMARY KEY CLUSTERED
(
	[Id] ASC
)WITH (PAD_INDEX = OFF, STATISTICS_NORECOMPUTE = OFF, IGNORE_DUP_KEY = OFF, ALLOW_ROW_LOCKS = ON, ALLOW_PAGE_LOCKS = ON) ON [PRIMARY]
) ON [PRIMARY]
GO
SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO
CREATE TABLE [ACTION_PROFILE_RESPONSIBLES_319](
	[Id] [int] IDENTITY(1,1) NOT NULL,
	[ActionProfile_Id] [int] NOT NULL,
	[Responsible_Id] [int] NULL,
 CONSTRAINT [PK_ACTION_PROFILE_RESPONSIBLES_319] PRIMARY KEY CLUSTERED
(
	[Id] ASC
)WITH (PAD_INDEX = OFF, STATISTICS_NORECOMPUTE = OFF, IGNORE_DUP_KEY = OFF, ALLOW_ROW_LOCKS = ON, ALLOW_PAGE_LOCKS = ON) ON [PRIMARY]
) ON [PRIMARY]
GO
SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO
CREATE TABLE [ADDRESS_174](
	[Contactid] [int] NOT NULL,
	[Addressno]  AS ([Contactid]),
	[Address1] [nvarchar](100) NULL,
	[Address2] [nvarchar](100) NULL,
	[Address3] [nvarchar](100) NULL,
	[Address4] [nvarchar](100) NULL,
	[Address5] [nvarchar](100) NULL,
	[Address6] [nvarchar](100) NULL,
	[Address7] [nvarchar](100) NULL,
	[Telephoneno] [nvarchar](50) NULL,
	[Telefaxno] [nvarchar](50) NULL,
	[Landkod] [nvarchar](2) NULL,
	[Rowid] [timestamp] NOT NULL,
	[EmailAddresses_To] [nvarchar](max) NULL,
	[EmailAddresses_Cc] [nvarchar](max) NULL,
	[EmailAddresses_Bcc] [nvarchar](max) NULL,
 CONSTRAINT [PK_ADDRESS_174] PRIMARY KEY CLUSTERED
(
	[Contactid] ASC
)WITH (PAD_INDEX = OFF, STATISTICS_NORECOMPUTE = OFF, IGNORE_DUP_KEY = OFF, ALLOW_ROW_LOCKS = ON, ALLOW_PAGE_LOCKS = ON) ON [PRIMARY]
) ON [PRIMARY] TEXTIMAGE_ON [PRIMARY]
GO
SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO
CREATE TABLE [ADVANCE_INVOICE_MAPPING_248](
	[Advanceid] [nvarchar](50) NOT NULL,
	[Fakturaid] [nvarchar](50) NULL,
	[Delfakturanr] [int] NULL,
	[Amount] [decimal](11, 2) NULL,
	[Vat] [decimal](11, 2) NULL,
	[Valutakod] [nvarchar](3) NULL,
	[Rowid] [timestamp] NOT NULL
) ON [PRIMARY]
GO
SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO
CREATE TABLE [ADVANCE_PAYMENT_247](
	[Id] [nvarchar](50) NOT NULL,
	[Number] [int] NULL,
	[Kundnr] [nvarchar](7) NULL,
	[Arendenr] [nvarchar](50) NULL,
	[Date] [datetime] NULL,
	[Paydate] [datetime] NULL,
	[Amount] [decimal](11, 2) NULL,
	[Vat] [decimal](11, 2) NULL,
	[Usedamount] [decimal](11, 2) NULL,
	[Usedvat] [decimal](11, 2) NULL,
	[Valutakod] [nvarchar](3) NULL,
	[Notes] [ntext] NULL,
	[Rowid] [timestamp] NOT NULL,
	[Anvid] [int] NULL,
	[Forfallodatum] [datetime] NULL,
	[Deleted] [int] NOT NULL,
 CONSTRAINT [PK_ADVANCE_PAYMENT_247] PRIMARY KEY CLUSTERED
(
	[Id] ASC
)WITH (PAD_INDEX = OFF, STATISTICS_NORECOMPUTE = OFF, IGNORE_DUP_KEY = OFF, ALLOW_ROW_LOCKS = ON, ALLOW_PAGE_LOCKS = ON) ON [PRIMARY]
) ON [PRIMARY] TEXTIMAGE_ON [PRIMARY]
GO
SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO
CREATE TABLE [AGENT_RULE_326](
	[Id] [int] IDENTITY(1,1) NOT NULL,
	[Landkod] [nvarchar](2) NOT NULL,
	[Kundnr] [nvarchar](7) NULL,
	[Klasskod] [nvarchar](1) NULL,
	[Attorney_Id] [int] NULL,
	[Ombudsnr] [nvarchar](7) NULL,
	[Rowid] [timestamp] NOT NULL,
 CONSTRAINT [PK_AGENT_RULE_326] PRIMARY KEY CLUSTERED
(
	[Id] ASC
)WITH (PAD_INDEX = OFF, STATISTICS_NORECOMPUTE = OFF, IGNORE_DUP_KEY = OFF, ALLOW_ROW_LOCKS = ON, ALLOW_PAGE_LOCKS = ON) ON [PRIMARY]
) ON [PRIMARY]
GO
SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO
CREATE TABLE [AKTFRIST_76](
	[Arendenr] [nvarchar](50) NOT NULL,
	[Lopnr] [smallint] NOT NULL,
	[Fristkod] [nvarchar](6) NOT NULL,
	[Fhandlagg] [int] NULL,
	[Belopp] [decimal](11, 2) NULL,
	[Sidor] [nvarchar](4) NULL,
	[Utfdag] [datetime] NOT NULL,
	[Frist] [datetime] NOT NULL,
	[Rapportsand] [datetime] NULL,
	[Paminnelse] [datetime] NULL,
	[Instrmottagen] [datetime] NULL,
	[Instrsand] [datetime] NULL,
	[Slutdag] [datetime] NULL,
	[Svaromal] [datetime] NULL,
	[Rowid] [timestamp] NOT NULL,
	[Aktfrist_76_id] [int] IDENTITY(1,1) NOT NULL,
	[Fritext] [ntext] NULL,
	[Moduleid] [uniqueidentifier] NULL,
	[Fritext2] [ntext] NULL,
	[Reviewed] [bit] NOT NULL,
	[Reviewuser] [int] NULL,
	[Reviewdate] [datetime] NULL,
 CONSTRAINT [PK_AKTFRIST_76] PRIMARY KEY CLUSTERED
(
	[Aktfrist_76_id] ASC
)WITH (PAD_INDEX = OFF, STATISTICS_NORECOMPUTE = OFF, IGNORE_DUP_KEY = OFF, ALLOW_ROW_LOCKS = ON, ALLOW_PAGE_LOCKS = ON) ON [PRIMARY]
) ON [PRIMARY] TEXTIMAGE_ON [PRIMARY]
GO
SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO
CREATE TABLE [AKTFRIST_BEHORIG_294](
	[Id] [int] IDENTITY(1,1) NOT NULL,
	[Frist_Id] [int] NOT NULL,
	[User_Id] [int] NOT NULL,
	[Flag] [int] NOT NULL,
	[Rowid] [timestamp] NOT NULL,
	[Remark] [nvarchar](500) NULL,
 CONSTRAINT [PK_AKTFRIST_BEHORIG_294] PRIMARY KEY CLUSTERED
(
	[Id] ASC
)WITH (PAD_INDEX = OFF, STATISTICS_NORECOMPUTE = OFF, IGNORE_DUP_KEY = OFF, ALLOW_ROW_LOCKS = ON, ALLOW_PAGE_LOCKS = ON) ON [PRIMARY]
) ON [PRIMARY]
GO
SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO
CREATE TABLE [AKTFRISTNY_134](
	[Arendenr] [nvarchar](50) NOT NULL,
	[Fristkod] [nvarchar](6) NOT NULL,
	[Fhandlagg] [int] NULL,
	[Belopp] [decimal](11, 2) NULL,
	[Sidor] [nvarchar](4) NULL,
	[Utfdag] [datetime] NOT NULL,
	[Frist] [datetime] NOT NULL,
	[Rapportsand] [datetime] NULL,
	[Paminnelse] [datetime] NULL,
	[Instrmottagen] [datetime] NULL,
	[Instrsand] [datetime] NULL,
	[Slutdag] [datetime] NULL,
	[Svaromal] [datetime] NULL,
	[Anvid] [int] NULL,
	[Rowid] [timestamp] NOT NULL,
	[Phase] [smallint] NOT NULL,
	[Lopnr] [smallint] NULL,
	[Fritext] [ntext] NULL
) ON [PRIMARY] TEXTIMAGE_ON [PRIMARY]
GO
SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO
CREATE TABLE [AKTLAGESHIST_127](
	[Regdatum] [datetime] NOT NULL,
	[Arendenr] [nvarchar](50) NULL,
	[Aktlage] [nvarchar](40) NULL,
	[Rowid] [timestamp] NOT NULL
) ON [PRIMARY]
GO
SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO
CREATE TABLE [ALTINN_BATCH_RULES_336](
	[Id] [int] IDENTITY(1,1) NOT NULL,
	[MessageTitleContaining] [nvarchar](500) NOT NULL,
	[Class] [int] NULL,
	[Category] [int] NULL,
	[Type] [int] NULL,
	[SecondaryClass] [int] NULL,
	[SecondaryCategory] [int] NULL,
	[SecondaryType] [int] NULL,
	[Filename] [nvarchar](500) NULL,
 CONSTRAINT [PK_ALTINN_BATCH_RULES_336] PRIMARY KEY CLUSTERED
(
	[Id] ASC
)WITH (PAD_INDEX = OFF, STATISTICS_NORECOMPUTE = OFF, IGNORE_DUP_KEY = OFF, ALLOW_ROW_LOCKS = ON, ALLOW_PAGE_LOCKS = ON) ON [PRIMARY]
) ON [PRIMARY]
GO
SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO
CREATE TABLE [ARBUFFERT_125](
	[Buffertid] [smallint] NOT NULL,
	[Arendenr] [nvarchar](50) NULL,
	[Kundnr] [nvarchar](7) NULL,
	[Sokord] [nvarchar](40) NULL,
	[Id] [int] NULL,
	[Sprakkod] [nvarchar](1) NULL,
	[Anvid] [int] NOT NULL,
	[Rowid] [timestamp] NOT NULL,
	[Timdeb] [int] NULL
) ON [PRIMARY]
GO
SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO
CREATE TABLE [ARENDE_1](
	[Arendenr] [nvarchar](50) NOT NULL,
	[Arsavgregel] [nvarchar](10) NULL,
	[Arendetyp] [nvarchar](1) NULL,
	[Servicetyp] [nvarchar](1) NULL,
	[Handlaggarid] [int] NULL,
	[Intaktstalle] [nvarchar](6) NULL,
	[Statuskod] [nvarchar](1) NULL,
	[Arendekod] [nvarchar](6) NULL,
	[Ansokningsnr] [nvarchar](50) NULL,
	[Patentnr] [nvarchar](50) NULL,
	[Lopdatum] [datetime] NULL,
	[Natinlamndag] [datetime] NULL,
	[Internatinlamndag] [datetime] NULL,
	[Istalleref] [nvarchar](15) NULL,
	[Anmarkning] [nvarchar](1) NULL,
	[Publiceringsnr] [nvarchar](50) NULL,
	[Antalbetalda] [smallint] NULL,
	[Antalaviserade] [smallint] NULL,
	[Slutdag] [datetime] NULL,
	[Offentligdag] [datetime] NULL,
	[Publiseringsdag] [datetime] NULL,
	[Beviljad] [datetime] NULL,
	[Titel] [nvarchar](500) NULL,
	[Slagord] [nvarchar](250) NULL,
	[Ankomsdat] [datetime] NULL,
	[Tillarstaxordat] [datetime] NULL,
	[Aktlage] [nvarchar](254) NULL,
	[Landkod] [nvarchar](2) NULL,
	[Prioritavi] [nvarchar](1) NULL,
	[Grundarende] [nvarchar](50) NULL,
	[Avdeladnr] [nvarchar](50) NULL,
	[Avdeladdat] [datetime] NULL,
	[Andratdatum] [datetime] NULL,
	[Skapatdat] [datetime] NULL,
	[Arendepicture] [nvarchar](254) NULL,
	[Anv] [int] NULL,
	[Titel2] [nvarchar](500) NULL,
	[Slagord2] [nvarchar](250) NULL,
	[Nedlagd] [datetime] NULL,
	[Part1] [nvarchar](3) NULL,
	[Refannatarende] [nvarchar](50) NULL,
	[Bevakomr] [nvarchar](1) NULL,
	[Slagordstrip] [nvarchar](250) NULL,
	[Orderutl] [datetime] NULL,
	[Internatansreg] [nvarchar](50) NULL,
	[Fulldes] [smallint] NULL,
	[Motsvarende] [nvarchar](50) NULL,
	[Fasid] [nvarchar](6) NULL,
	[Rowid] [timestamp] NOT NULL,
	[Utlagd] [datetime] NULL,
	[Exporttillarsavg] [smalldatetime] NULL,
	[Rowguid] [uniqueidentifier] NOT NULL,
	[Bevarsavg] [smalldatetime] NULL,
	[Exportfirstuser] [nvarchar](100) NULL,
	[Exportinitiatoruser] [nvarchar](100) NULL,
	[Exportlastuser] [nvarchar](100) NULL,
	[Assistant] [int] NULL,
	[Officeid] [int] NOT NULL,
	[Fritext] [nvarchar](max) NULL,
	[Topleveldomainid] [int] NULL,
	[Electronic_file] [int] NOT NULL,
	[Abstract] [nvarchar](max) NULL,
	[Excludedfromiprcontrol] [bit] NOT NULL,
	[Claims] [int] NULL,
	[Claimpages] [int] NULL,
	[Pages] [int] NULL,
	[Drawingsheets] [int] NULL,
	[Examinationdate] [datetime] NULL,
	[Outsourced] [bit] NOT NULL,
	[TrademarkType_Id] [int] NULL,
	[TrademarkInfo] [int] NULL,
	[FilingType_Id] [int] NULL,
 CONSTRAINT [PK_ARENDE_1] PRIMARY KEY CLUSTERED
(
	[Arendenr] ASC
)WITH (PAD_INDEX = OFF, STATISTICS_NORECOMPUTE = OFF, IGNORE_DUP_KEY = OFF, ALLOW_ROW_LOCKS = ON, ALLOW_PAGE_LOCKS = ON) ON [PRIMARY]
) ON [PRIMARY] TEXTIMAGE_ON [PRIMARY]
GO
SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO
CREATE TABLE [ARENDE_COST_263](
	[Id] [int] IDENTITY(1,1) NOT NULL,
	[Arendenr] [nvarchar](50) NOT NULL,
	[InvoiceNumber] [int] NULL,
	[AmountWithoutVat] [decimal](11, 2) NOT NULL,
	[CurrencyCode] [nvarchar](3) NOT NULL,
	[InvoiceDate] [datetime] NULL,
	[Origin] [int] NOT NULL,
	[Rowid] [timestamp] NOT NULL,
	[RowGuid] [uniqueidentifier] NOT NULL,
	[Advanceamount] [decimal](11, 2) NULL,
 CONSTRAINT [PK_ARENDE_COST_263] PRIMARY KEY CLUSTERED
(
	[Id] ASC
)WITH (PAD_INDEX = OFF, STATISTICS_NORECOMPUTE = OFF, IGNORE_DUP_KEY = OFF, ALLOW_ROW_LOCKS = ON, ALLOW_PAGE_LOCKS = ON) ON [PRIMARY]
) ON [PRIMARY]
GO
SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO
CREATE TABLE [ARENDE_DOMAIN_233](
	[Arendenr] [nvarchar](50) NOT NULL,
	[Pekare] [nvarchar](100) NULL,
	[Rowid] [timestamp] NOT NULL,
 CONSTRAINT [PK_ARENDE_DOMAIN_233] PRIMARY KEY CLUSTERED
(
	[Arendenr] ASC
)WITH (PAD_INDEX = OFF, STATISTICS_NORECOMPUTE = OFF, IGNORE_DUP_KEY = OFF, ALLOW_ROW_LOCKS = ON, ALLOW_PAGE_LOCKS = ON) ON [PRIMARY]
) ON [PRIMARY]
GO
SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO
CREATE TABLE [ARENDE_EXTRAINFO_284](
	[Id] [int] IDENTITY(1,1) NOT NULL,
	[Arendenr] [nvarchar](50) NOT NULL,
	[Faltid] [nvarchar](20) NOT NULL,
	[Value] [nvarchar](max) NULL,
	[Rowid] [timestamp] NOT NULL,
 CONSTRAINT [PK_ARENDE_EXTRAINFO_284] PRIMARY KEY CLUSTERED
(
	[Id] ASC
)WITH (PAD_INDEX = OFF, STATISTICS_NORECOMPUTE = OFF, IGNORE_DUP_KEY = OFF, ALLOW_ROW_LOCKS = ON, ALLOW_PAGE_LOCKS = ON) ON [PRIMARY]
) ON [PRIMARY] TEXTIMAGE_ON [PRIMARY]
GO
SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO
CREATE TABLE [ARENDE_FAS_158](
	[Fasid] [nvarchar](6) NOT NULL,
	[Rowid] [timestamp] NOT NULL,
	[Klasskod] [nvarchar](1) NULL,
	[Position] [int] NOT NULL,
 CONSTRAINT [PK_ARENDE_FAS_158] PRIMARY KEY CLUSTERED
(
	[Fasid] ASC
)WITH (PAD_INDEX = OFF, STATISTICS_NORECOMPUTE = OFF, IGNORE_DUP_KEY = OFF, ALLOW_ROW_LOCKS = ON, ALLOW_PAGE_LOCKS = ON) ON [PRIMARY]
) ON [PRIMARY]
GO
SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO
CREATE TABLE [ARENDE_IMAGE_264](
	[Arendenr] [nvarchar](50) NOT NULL,
	[ImageId] [int] NOT NULL,
	[Default] [bit] NULL,
	[Rowid] [timestamp] NOT NULL,
 CONSTRAINT [PK_ARENDE_IMAGE_264] PRIMARY KEY CLUSTERED
(
	[Arendenr] ASC,
	[ImageId] ASC
)WITH (PAD_INDEX = OFF, STATISTICS_NORECOMPUTE = OFF, IGNORE_DUP_KEY = OFF, ALLOW_ROW_LOCKS = ON, ALLOW_PAGE_LOCKS = ON) ON [PRIMARY]
) ON [PRIMARY]
GO
SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO
CREATE TABLE [ARENDE_PRODUCTCATEGORY_200](
	[Arendeproductcategoryid] [int] IDENTITY(1,1) NOT NULL,
	[Arendenr] [nvarchar](50) NOT NULL,
	[Productcategoryid] [int] NOT NULL,
 CONSTRAINT [PK_ARENDE_PRODUCTCATEGORY_200] PRIMARY KEY CLUSTERED
(
	[Arendeproductcategoryid] ASC
)WITH (PAD_INDEX = OFF, STATISTICS_NORECOMPUTE = OFF, IGNORE_DUP_KEY = OFF, ALLOW_ROW_LOCKS = ON, ALLOW_PAGE_LOCKS = ON) ON [PRIMARY]
) ON [PRIMARY]
GO
SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO
CREATE TABLE [ARENDEEXPORT_168](
	[Arendenr] [nvarchar](50) NOT NULL,
	[Anv] [int] NULL,
	[Mottaget] [datetime] NOT NULL,
	[Rowid] [timestamp] NOT NULL,
	[Type] [nvarchar](20) NULL,
	[Reason] [nvarchar](20) NULL,
	[Sent] [datetime] NULL,
	[Exportver] [nvarchar](2) NULL,
	[Comment] [ntext] NULL,
	[Data] [ntext] NULL
) ON [PRIMARY] TEXTIMAGE_ON [PRIMARY]
GO
SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO
CREATE TABLE [ARENDEFASNAMN_166](
	[Fasid] [nvarchar](6) NOT NULL,
	[Sprakkod] [nvarchar](1) NOT NULL,
	[Fasnamn] [nvarchar](50) NOT NULL,
	[Rowid] [timestamp] NOT NULL
) ON [PRIMARY]
GO
SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO
CREATE TABLE [ARENDEKOD_2](
	[Arendekod] [nvarchar](6) NOT NULL,
	[Klasskod] [nvarchar](1) NULL,
	[Rowid] [timestamp] NOT NULL,
	[Forklaring] [ntext] NULL
) ON [PRIMARY] TEXTIMAGE_ON [PRIMARY]
GO
SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO
CREATE TABLE [ARENDEKODNAMN_165](
	[Arendekod] [nvarchar](6) NOT NULL,
	[Sprakkod] [nvarchar](1) NOT NULL,
	[Arendekodnamn] [nvarchar](35) NULL,
	[Rowid] [timestamp] NOT NULL
) ON [PRIMARY]
GO
SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO
CREATE TABLE [ARENDETYP_3](
	[Arendetyp] [nvarchar](1) NOT NULL,
	[Klasskod] [nvarchar](1) NOT NULL,
	[Inharvkonto] [nvarchar](6) NULL,
	[Utlarvkonto] [nvarchar](6) NULL,
	[Utlombarvkonto] [nvarchar](6) NULL,
	[Inhoffavgkonto] [nvarchar](6) NULL,
	[Utloffavgkonto] [nvarchar](6) NULL,
	[Rowid] [timestamp] NOT NULL,
	[Kostnadsbarare] [nvarchar](6) NULL
) ON [PRIMARY]
GO
SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO
CREATE TABLE [ARENDETYPKLASS_42](
	[Klasskod] [nvarchar](1) NOT NULL,
	[Namn] [nvarchar](40) NULL,
	[Rowid] [timestamp] NOT NULL,
 CONSTRAINT [PK_ARENDETYPKLASS_42] PRIMARY KEY CLUSTERED
(
	[Klasskod] ASC
)WITH (PAD_INDEX = OFF, STATISTICS_NORECOMPUTE = OFF, IGNORE_DUP_KEY = OFF, ALLOW_ROW_LOCKS = ON, ALLOW_PAGE_LOCKS = ON) ON [PRIMARY]
) ON [PRIMARY]
GO
SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO
CREATE TABLE [ARENDETYPNAMN_164](
	[Arendetyp] [nvarchar](1) NOT NULL,
	[Sprakkod] [nvarchar](1) NOT NULL,
	[Arendenamn] [nvarchar](35) NULL,
	[Rowid] [timestamp] NOT NULL
) ON [PRIMARY]
GO
SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO
CREATE TABLE [ARSAVGREGEL_4](
	[Arsavgregel] [nvarchar](10) NOT NULL,
	[Arsavgregelnamn] [nvarchar](50) NULL,
	[Rowid] [timestamp] NOT NULL
) ON [PRIMARY]
GO
SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO
CREATE TABLE [AT_COLUMN](
	[Id] [int] IDENTITY(1,1) NOT NULL,
	[TableId] [int] NOT NULL,
	[ColumnName] [nvarchar](255) NOT NULL,
	[ColumnSpecifier] [nvarchar](255) NULL,
	[TranslationTable] [nvarchar](255) NULL,
	[TranslationKeyColumn] [nvarchar](255) NULL,
	[TranslationValueColumn] [nvarchar](255) NULL,
	[LanguageColumn] [nvarchar](255) NULL,
	[LanguageCode] [nvarchar](1) NULL,
 CONSTRAINT [PK_AT_COLUMN] PRIMARY KEY CLUSTERED
(
	[Id] ASC
)WITH (PAD_INDEX = OFF, STATISTICS_NORECOMPUTE = OFF, IGNORE_DUP_KEY = OFF, ALLOW_ROW_LOCKS = ON, ALLOW_PAGE_LOCKS = ON) ON [PRIMARY]
) ON [PRIMARY]
GO
SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO
CREATE TABLE [AT_FILTER](
	[Id] [int] IDENTITY(1,1) NOT NULL,
	[TableId] [int] NOT NULL,
	[FilterName] [nvarchar](255) NOT NULL,
	[FilterSpecifier] [nvarchar](255) NULL,
 CONSTRAINT [PK_AT_FILTER] PRIMARY KEY CLUSTERED
(
	[Id] ASC
)WITH (PAD_INDEX = OFF, STATISTICS_NORECOMPUTE = OFF, IGNORE_DUP_KEY = OFF, ALLOW_ROW_LOCKS = ON, ALLOW_PAGE_LOCKS = ON) ON [PRIMARY]
) ON [PRIMARY]
GO
SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO
CREATE TABLE [AT_FILTERCOLUMN](
	[Id] [int] IDENTITY(1,1) NOT NULL,
	[FilterId] [int] NOT NULL,
	[ColumnName] [nvarchar](255) NOT NULL,
	[ColumnCondition] [nvarchar](5) NOT NULL,
	[ColumnValue] [nvarchar](255) NOT NULL,
 CONSTRAINT [PK_AT_FILTERCOLUMN] PRIMARY KEY CLUSTERED
(
	[Id] ASC
)WITH (PAD_INDEX = OFF, STATISTICS_NORECOMPUTE = OFF, IGNORE_DUP_KEY = OFF, ALLOW_ROW_LOCKS = ON, ALLOW_PAGE_LOCKS = ON) ON [PRIMARY]
) ON [PRIMARY]
GO
SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO
CREATE TABLE [AT_LOG_ARENDE](
	[Id] [int] IDENTITY(1,1) NOT NULL,
	[AtTime] [datetime] NOT NULL,
	[AtUser] [nvarchar](135) NOT NULL,
	[ColumnId] [int] NOT NULL,
	[ObjectKey] [nvarchar](50) NOT NULL,
	[ObjectRelationKey] [nvarchar](255) NULL,
	[OldValue] [nvarchar](4000) NULL,
	[NewValue] [nvarchar](4000) NULL,
	[TriggerType] [nvarchar](1) NULL,
	[BatchId] [uniqueidentifier] NULL,
	[FilterId] [int] NULL,
	[ParentObjectKey] [nvarchar](50) NULL,
 CONSTRAINT [PK_AT_LOG_ARENDE] PRIMARY KEY CLUSTERED
(
	[Id] ASC
)WITH (PAD_INDEX = OFF, STATISTICS_NORECOMPUTE = OFF, IGNORE_DUP_KEY = OFF, ALLOW_ROW_LOCKS = ON, ALLOW_PAGE_LOCKS = ON) ON [PRIMARY]
) ON [PRIMARY]
GO
SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO
CREATE TABLE [AT_LOG_FAKTURATEXTNR](
	[Id] [int] IDENTITY(1,1) NOT NULL,
	[AtTime] [datetime] NOT NULL,
	[AtUser] [nvarchar](135) NOT NULL,
	[ColumnId] [int] NOT NULL,
	[ObjectKey] [nvarchar](16) NOT NULL,
	[ObjectRelationKey] [nvarchar](255) NULL,
	[OldValue] [nvarchar](4000) NULL,
	[NewValue] [nvarchar](4000) NULL,
	[TriggerType] [nvarchar](1) NULL,
	[BatchId] [uniqueidentifier] NULL,
	[FilterId] [int] NULL,
	[ParentObjectKey] [nvarchar](16) NULL,
 CONSTRAINT [PK_AT_LOG_FAKTURATEXTNR] PRIMARY KEY CLUSTERED
(
	[Id] ASC
)WITH (PAD_INDEX = OFF, STATISTICS_NORECOMPUTE = OFF, IGNORE_DUP_KEY = OFF, ALLOW_ROW_LOCKS = ON, ALLOW_PAGE_LOCKS = ON) ON [PRIMARY]
) ON [PRIMARY]
GO
SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO
CREATE TABLE [AT_LOG_KONTAKT](
	[Id] [int] IDENTITY(1,1) NOT NULL,
	[AtTime] [datetime] NOT NULL,
	[AtUser] [nvarchar](135) NOT NULL,
	[ColumnId] [int] NOT NULL,
	[ObjectKey] [nvarchar](16) NOT NULL,
	[ObjectRelationKey] [nvarchar](255) NULL,
	[OldValue] [nvarchar](4000) NULL,
	[NewValue] [nvarchar](4000) NULL,
	[TriggerType] [nvarchar](1) NULL,
	[BatchId] [uniqueidentifier] NULL,
	[FilterId] [int] NULL,
	[ParentObjectKey] [nvarchar](16) NULL,
 CONSTRAINT [PK_AT_LOG_KONTAKT] PRIMARY KEY CLUSTERED
(
	[Id] ASC
)WITH (PAD_INDEX = OFF, STATISTICS_NORECOMPUTE = OFF, IGNORE_DUP_KEY = OFF, ALLOW_ROW_LOCKS = ON, ALLOW_PAGE_LOCKS = ON) ON [PRIMARY]
) ON [PRIMARY]
GO
SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO
CREATE TABLE [AT_LOG_KUND](
	[Id] [int] IDENTITY(1,1) NOT NULL,
	[AtTime] [datetime] NOT NULL,
	[AtUser] [nvarchar](135) NOT NULL,
	[ColumnId] [int] NOT NULL,
	[ObjectKey] [nvarchar](16) NOT NULL,
	[ObjectRelationKey] [nvarchar](255) NULL,
	[OldValue] [nvarchar](4000) NULL,
	[NewValue] [nvarchar](4000) NULL,
	[TriggerType] [nvarchar](1) NULL,
	[BatchId] [uniqueidentifier] NULL,
	[FilterId] [int] NULL,
	[ParentObjectKey] [nvarchar](16) NULL,
 CONSTRAINT [PK_AT_LOG_KUND] PRIMARY KEY CLUSTERED
(
	[Id] ASC
)WITH (PAD_INDEX = OFF, STATISTICS_NORECOMPUTE = OFF, IGNORE_DUP_KEY = OFF, ALLOW_ROW_LOCKS = ON, ALLOW_PAGE_LOCKS = ON) ON [PRIMARY]
) ON [PRIMARY]
GO
SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO
CREATE TABLE [AT_LOG_PARAMETER](
	[Id] [int] IDENTITY(1,1) NOT NULL,
	[AtTime] [datetime] NOT NULL,
	[AtUser] [nvarchar](135) NOT NULL,
	[ColumnId] [int] NOT NULL,
	[ObjectKey] [nvarchar](100) NOT NULL,
	[ObjectRelationKey] [nvarchar](255) NULL,
	[OldValue] [nvarchar](4000) NULL,
	[NewValue] [nvarchar](4000) NULL,
	[TriggerType] [nvarchar](1) NULL,
	[BatchId] [uniqueidentifier] NULL,
	[FilterId] [int] NULL,
	[ParentObjectKey] [nvarchar](50) NULL,
 CONSTRAINT [PK_AT_LOG_PARAMETER] PRIMARY KEY CLUSTERED
(
	[Id] ASC
)WITH (PAD_INDEX = OFF, STATISTICS_NORECOMPUTE = OFF, IGNORE_DUP_KEY = OFF, ALLOW_ROW_LOCKS = ON, ALLOW_PAGE_LOCKS = ON) ON [PRIMARY]
) ON [PRIMARY]
GO
SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO
CREATE TABLE [AT_TABLE](
	[Id] [int] IDENTITY(1,1) NOT NULL,
	[TableName] [nvarchar](255) NOT NULL,
	[LogTable] [nvarchar](255) NOT NULL,
	[KeyColumn] [nvarchar](255) NOT NULL,
	[KeyRelation] [nvarchar](255) NULL,
	[ColumnSpecifierTable] [nvarchar](255) NULL,
	[ColumnSpecifierColumn] [nvarchar](255) NULL,
	[Triggers] [nvarchar](3) NULL,
	[ColumnSpecifierKeyColumn] [nvarchar](255) NULL,
	[LanguageColumn] [nvarchar](255) NULL,
	[LanguageCode] [nvarchar](1) NULL,
	[TableSpecifier] [nvarchar](255) NULL,
	[KeyColumnLength] [int] NULL,
	[UniqueColumn] [nvarchar](255) NULL,
	[LogKeyTable] [nvarchar](255) NULL,
	[LogKeyColumn] [nvarchar](255) NULL,
	[LogKeyMasterColumn] [nvarchar](255) NULL,
	[LogKeyDetailColumn] [nvarchar](255) NULL,
	[ParentKeyBaseTable] [nvarchar](255) NULL,
	[ParentKeyTable] [nvarchar](255) NULL,
	[ParentKeyMasterColumn] [nvarchar](255) NULL,
	[ParentKeyDetailColumn] [nvarchar](255) NULL,
	[ParentKeySpecifierColumn] [nvarchar](255) NULL,
 CONSTRAINT [PK_AT_TABLE] PRIMARY KEY CLUSTERED
(
	[Id] ASC
)WITH (PAD_INDEX = OFF, STATISTICS_NORECOMPUTE = OFF, IGNORE_DUP_KEY = OFF, ALLOW_ROW_LOCKS = ON, ALLOW_PAGE_LOCKS = ON) ON [PRIMARY]
) ON [PRIMARY]
GO
SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO
CREATE TABLE [ATGARDER_120](
	[Underlagid] [smallint] NULL,
	[Id] [int] NULL,
	[Kundnr] [nvarchar](7) NULL,
	[Arendenr] [nvarchar](50) NULL,
	[Datum] [datetime] NULL,
	[Fakturadatum] [datetime] NULL,
	[Ombudsnr] [nvarchar](7) NULL,
	[Belopp] [decimal](11, 2) NULL,
	[Debiterasjn] [nvarchar](1) NULL,
	[Regtid] [datetime] NULL,
	[Sokord] [nvarchar](40) NULL,
	[Atgardsnr] [smallint] NULL,
	[Ordsida] [int] NULL,
	[Tid] [float] NULL,
	[Fakturanr] [nvarchar](50) NULL,
	[Kundfaktdatum] [datetime] NULL,
	[Kundkreddatum] [datetime] NULL,
	[Direktord] [int] NULL,
	[Direkt] [datetime] NULL,
	[Valutakod] [nvarchar](3) NULL,
	[Transid] [int] NULL,
	[Momsjn] [nvarchar](1) NULL,
	[Intaktstalle] [nvarchar](6) NULL,
	[Kostnadsbarare] [nvarchar](6) NULL,
	[Kontonr] [nvarchar](6) NULL,
	[Levvaluta] [nvarchar](3) NULL,
	[Belopputl] [numeric](11, 2) NULL,
	[Rowid] [timestamp] NOT NULL,
	[Paslag] [decimal](11, 2) NULL,
	[Fastpris] [smallint] NULL,
	[Debetfaktnr] [int] NULL,
	[Kreditfaktnr] [int] NULL,
	[Debetdelfaktnr] [int] NULL,
	[Kreditdelfaktnr] [int] NULL,
	[Kalkylkostnad] [decimal](11, 2) NULL,
	[Betaldatum] [datetime] NULL,
	[Verifikationsnr] [nvarchar](15) NULL,
	[Slutbetald] [smallint] NULL,
	[Rabatt] [decimal](11, 2) NULL,
	[Timdeb] [int] NULL,
	[Atgardstext] [ntext] NULL,
	[Fakturaref] [ntext] NULL,
	[Forfallodatum] [datetime] NULL,
	[Kalkyltyp] [nvarchar](2) NULL,
	[Timarvode] [int] NULL,
	[Systemgenerated] [smallint] NULL,
	[Fakturaid] [nvarchar](50) NULL,
	[Kreditfakturaid] [nvarchar](50) NULL,
	[Advanceinvoice] [nvarchar](50) NULL,
	[Extref] [nvarchar](50) NULL,
	[Copied] [datetime] NULL,
	[Supplierspaydate] [datetime] NULL,
	[Invoicetype] [int] NULL,
	[Applyvat] [smallint] NULL
) ON [PRIMARY] TEXTIMAGE_ON [PRIMARY]
GO
SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO
CREATE TABLE [ATGBUFFERT_126](
	[Radnr] [smallint] NOT NULL,
	[Buffertid] [smallint] NOT NULL,
	[Ordsida] [numeric](11, 3) NULL,
	[Belopp] [decimal](11, 2) NULL,
	[Tid] [smallint] NULL,
	[Klockstatus] [smallint] NULL,
	[Atgardsnr] [smallint] NULL,
	[Fastpris] [nvarchar](1) NOT NULL,
	[Tidreell] [smallint] NULL,
	[Arbtid] [int] NULL,
	[Rabatt] [int] NULL,
	[Rabattjn] [nvarchar](1) NULL,
	[Momsjn] [nvarchar](1) NULL,
	[Rowid] [timestamp] NOT NULL,
	[Expfee] [nvarchar](50) NULL,
	[Task] [nvarchar](50) NULL,
	[Expense] [nvarchar](50) NULL,
	[Activity] [nvarchar](50) NULL,
	[Adjustment] [nvarchar](50) NULL,
	[Invdescr] [nvarchar](254) NULL,
	[Intaktstalle] [nvarchar](6) NULL,
	[Kostnadsbarare] [nvarchar](6) NULL,
	[Kontonr] [nvarchar](6) NULL,
	[Rabattpaatgard] [int] NULL,
	[Kalkylkostnad] [decimal](11, 2) NULL,
	[Timdeb] [int] NULL,
	[Debiterasjn] [nvarchar](1) NULL,
	[Atgardstext] [ntext] NULL,
	[Childrenstransid] [ntext] NULL,
	[Kalkyltyp] [nvarchar](2) NULL,
	[Timarvode] [int] NULL,
	[Valutakod] [nvarchar](3) NULL
) ON [PRIMARY] TEXTIMAGE_ON [PRIMARY]
GO
SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO
CREATE TABLE [AUDIT_CHANGE_261](
	[Id] [int] IDENTITY(1,1) NOT NULL,
	[ChangesetId] [int] NOT NULL,
	[Field_Id] [int] NOT NULL,
	[Field_Discriminator] [nvarchar](100) NULL,
	[Field_SubId] [int] NULL,
	[ChangeType] [int] NOT NULL,
	[DataType] [int] NOT NULL,
	[OldValueText] [nvarchar](max) NULL,
	[OldValueDate] [datetime] NULL,
	[OldValueNumeric] [decimal](18, 6) NULL,
	[OldValueBool] [bit] NULL,
	[NewValueText] [nvarchar](max) NULL,
	[NewValueDate] [datetime] NULL,
	[NewValueNumeric] [decimal](18, 6) NULL,
	[NewValueBool] [bit] NULL,
 CONSTRAINT [PK_AUDIT_CHANGE_261] PRIMARY KEY CLUSTERED
(
	[Id] ASC
)WITH (PAD_INDEX = OFF, STATISTICS_NORECOMPUTE = OFF, IGNORE_DUP_KEY = OFF, ALLOW_ROW_LOCKS = ON, ALLOW_PAGE_LOCKS = ON) ON [PRIMARY]
) ON [PRIMARY] TEXTIMAGE_ON [PRIMARY]
GO
SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO
CREATE TABLE [AUDIT_CHANGESET_260](
	[Id] [int] IDENTITY(1,1) NOT NULL,
	[EntityType] [int] NOT NULL,
	[EntityIdentifier] [nvarchar](50) NOT NULL,
	[ChangeType] [int] NOT NULL,
	[Anvid] [int] NOT NULL,
	[When] [datetime] NOT NULL,
 CONSTRAINT [PK_AUDIT_CHANGESET_260] PRIMARY KEY CLUSTERED
(
	[Id] ASC
)WITH (PAD_INDEX = OFF, STATISTICS_NORECOMPUTE = OFF, IGNORE_DUP_KEY = OFF, ALLOW_ROW_LOCKS = ON, ALLOW_PAGE_LOCKS = ON) ON [PRIMARY]
) ON [PRIMARY]
GO
SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO
CREATE TABLE [AVGIFTSKOD_6](
	[Avgiftskod] [smallint] NOT NULL,
	[Rowid] [timestamp] NOT NULL
) ON [PRIMARY]
GO
SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO
CREATE TABLE [AVIHISTORIK_8](
	[Sekvnravihist] [smallint] NOT NULL,
	[Avgiftskod] [smallint] NOT NULL,
	[Arendenr] [nvarchar](50) NOT NULL,
	[Avisekvensdatum] [datetime] NULL,
	[Anm] [nvarchar](22) NULL,
	[Anv] [int] NULL,
	[Rowid] [timestamp] NOT NULL,
	[Id] [int] IDENTITY(1,1) NOT NULL,
 CONSTRAINT [PK_AVIHISTORIK_8] PRIMARY KEY NONCLUSTERED
(
	[Id] ASC
)WITH (PAD_INDEX = OFF, STATISTICS_NORECOMPUTE = OFF, IGNORE_DUP_KEY = OFF, ALLOW_ROW_LOCKS = ON, ALLOW_PAGE_LOCKS = ON) ON [PRIMARY]
) ON [PRIMARY]
GO
SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO
CREATE TABLE [AVISEKVENS_7](
	[Sekvnravihist] [smallint] NOT NULL,
	[Sekvnravihistnamn] [nvarchar](100) NULL,
	[Rowid] [timestamp] NOT NULL
) ON [PRIMARY]
GO
SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO
CREATE TABLE [AVISERING_71](
	[Arendenr] [nvarchar](50) NOT NULL,
	[Avgiftskod] [smallint] NOT NULL,
	[Sekvensnr] [smallint] NOT NULL,
	[Tid] [datetime] NOT NULL,
	[Radnr] [smallint] NOT NULL,
	[Regel] [nvarchar](10) NULL,
	[Belopp] [decimal](11, 2) NULL,
	[Belopputl] [decimal](11, 2) NULL,
	[Moms] [decimal](10, 4) NULL,
	[Momsutl] [decimal](10, 4) NULL,
	[Paslag1] [decimal](10, 4) NULL,
	[Paslag2] [decimal](10, 4) NULL,
	[Valutakod] [nvarchar](3) NULL,
	[Rowid] [timestamp] NOT NULL,
	[Paslag1utl] [decimal](10, 4) NULL,
	[Paslag2utl] [decimal](10, 4) NULL
) ON [PRIMARY]
GO
SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO
CREATE TABLE [BACKUP_KUND_ARVODE_104](
	[Kundnr] [nvarchar](7) NOT NULL,
	[Klasskod] [nvarchar](1) NULL,
	[Landkod] [nvarchar](2) NULL,
	[Period] [smallint] NULL,
	[Valutakod] [nvarchar](3) NULL,
	[Arvode] [int] NULL,
	[Straffarvode] [int] NULL,
	[Arvodeklass] [int] NULL,
	[Straffarvodeklass] [int] NULL,
	[Arvodesamreg] [int] NULL,
	[Straffarvodesamreg] [int] NULL,
	[Rabattjn] [nvarchar](1) NULL,
	[Rowid] [timestamp] NOT NULL
) ON [PRIMARY]
GO
SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO
CREATE TABLE [BACKUP_RABATT_161](
	[Kundnr] [nvarchar](7) NOT NULL,
	[Fakturatextnr] [smallint] NOT NULL,
	[Rowid] [timestamp] NOT NULL,
	[Rabattpaatgard] [decimal](11, 2) NULL,
	[Fakturatext] [ntext] NULL,
	[Landkod] [nvarchar](2) NULL,
	[Fastpris] [smallint] NULL
) ON [PRIMARY] TEXTIMAGE_ON [PRIMARY]
GO
SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO
CREATE TABLE [BASIC_OUTSOURCING_SURCHARGE_242](
	[Landkod] [nvarchar](2) NULL,
	[Klasskod] [nvarchar](1) NULL,
	[Ombarvprocent] [decimal](5, 2) NOT NULL,
	[Offavgprocent] [decimal](5, 2) NOT NULL,
	[Rowid] [timestamp] NOT NULL
) ON [PRIMARY]
GO
SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO
CREATE TABLE [BEHORIG_50](
	[Id] [int] IDENTITY(1,1) NOT NULL,
	[Username] [nvarchar](6) NOT NULL,
	[Anvgrupp] [nvarchar](3) NULL,
	[Namn] [nvarchar](40) NULL,
	[Officeid] [int] NOT NULL,
	[Pidnew] [varbinary](50) NULL,
	[Usersettings] [nvarchar](max) NULL,
	[Email] [nvarchar](255) NULL,
	[Isactive] [bit] NOT NULL,
	[Isattorney] [bit] NOT NULL,
	[Assistantid] [int] NULL,
	[Mintid] [int] NULL,
	[Intaktstalle] [nvarchar](6) NULL,
	[Tc_datacert] [nvarchar](20) NULL,
	[Tc_tymetrix] [nvarchar](20) NULL,
	[Ekonomikod] [nvarchar](10) NULL,
	[Kalkylkostnad] [decimal](11, 2) NULL,
	[Rowid] [timestamp] NOT NULL,
	[FeeGroup_Id] [int] NULL,
 CONSTRAINT [PK_BEHORIG_50] PRIMARY KEY CLUSTERED
(
	[Id] ASC
)WITH (PAD_INDEX = OFF, STATISTICS_NORECOMPUTE = OFF, IGNORE_DUP_KEY = OFF, ALLOW_ROW_LOCKS = ON, ALLOW_PAGE_LOCKS = ON) ON [PRIMARY]
) ON [PRIMARY] TEXTIMAGE_ON [PRIMARY]
GO
SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO
CREATE TABLE [BEHORIG_BES_53](
	[Anvgrupp] [nvarchar](3) NOT NULL,
	[Anvgrnamn] [nvarchar](30) NULL,
	[Rowid] [timestamp] NOT NULL
) ON [PRIMARY]
GO
SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO
CREATE TABLE [BEHORIG_EN_63](
	[Funktion] [int] NOT NULL,
	[Namn] [nvarchar](40) NULL,
	[Start_tid] [datetime] NULL,
	[Status] [int] NULL,
	[Rowid] [timestamp] NOT NULL
) ON [PRIMARY]
GO
SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO
CREATE TABLE [BEHORIG_FUNK_54](
	[Anvfunk] [int] NOT NULL,
	[Anvfunknamn] [nvarchar](60) NULL,
	[Anvdefault] [nvarchar](1) NULL,
	[Rowid] [timestamp] NOT NULL
) ON [PRIMARY]
GO
SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO
CREATE TABLE [BEHORIG_GR_51](
	[Anvgrupp] [nvarchar](3) NOT NULL,
	[Anvfunk] [int] NOT NULL,
	[Niva] [nvarchar](1) NULL,
	[Rowid] [timestamp] NOT NULL
) ON [PRIMARY]
GO
SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO
CREATE TABLE [BERAKNATUTLAGG_9](
	[Avgiftskod] [smallint] NOT NULL,
	[Arendenr] [nvarchar](50) NOT NULL,
	[Utlaggisek] [int] NULL,
	[Kundnraviombud] [nvarchar](7) NULL,
	[Utlaggutl] [int] NULL,
	[Valutakod] [nvarchar](3) NULL,
	[Rowid] [timestamp] NOT NULL
) ON [PRIMARY]
GO
SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO
CREATE TABLE [BERAKNATVMUTL_160](
	[Avgiftskod] [smallint] NOT NULL,
	[Arendenr] [nvarchar](50) NOT NULL,
	[Utlaggisek] [int] NULL,
	[Kundnraviombud] [nvarchar](7) NULL,
	[Utlaggutl] [int] NULL,
	[Valutakod] [nvarchar](3) NULL,
	[Rowid] [timestamp] NULL
) ON [PRIMARY]
GO
SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO
CREATE TABLE [BESLUT_137](
	[Arendenr] [nvarchar](50) NOT NULL,
	[Instans1] [nvarchar](1) NULL,
	[Malnr1] [nvarchar](15) NULL,
	[Beslutdatum1] [datetime] NULL,
	[Instans2] [nvarchar](1) NULL,
	[Malnr2] [nvarchar](15) NULL,
	[Beslutdatum2] [datetime] NULL,
	[Instans3] [nvarchar](1) NULL,
	[Malnr3] [nvarchar](15) NULL,
	[Beslutdatum3] [datetime] NULL,
	[Avslutatdatum] [datetime] NULL,
	[Resultat] [nvarchar](1) NULL,
	[Rowid] [timestamp] NOT NULL,
	[Kommentar] [nvarchar](max) NULL,
 CONSTRAINT [PK_BESLUT_137] PRIMARY KEY CLUSTERED
(
	[Arendenr] ASC
)WITH (PAD_INDEX = OFF, STATISTICS_NORECOMPUTE = OFF, IGNORE_DUP_KEY = OFF, ALLOW_ROW_LOCKS = ON, ALLOW_PAGE_LOCKS = ON) ON [PRIMARY]
) ON [PRIMARY] TEXTIMAGE_ON [PRIMARY]
GO
SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO
CREATE TABLE [BETALVILLKOR_17](
	[Betalningskod] [int] NOT NULL,
	[Antaldagar] [int] NULL,
	[Rowid] [timestamp] NOT NULL,
 CONSTRAINT [PK_BETALVILLKOR_17] PRIMARY KEY CLUSTERED
(
	[Betalningskod] ASC
)WITH (PAD_INDEX = OFF, STATISTICS_NORECOMPUTE = OFF, IGNORE_DUP_KEY = OFF, ALLOW_ROW_LOCKS = ON, ALLOW_PAGE_LOCKS = ON) ON [PRIMARY]
) ON [PRIMARY]
GO
SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO
CREATE TABLE [BEV_OMR_141](
	[Kod] [nvarchar](1) NOT NULL,
	[Namn] [nvarchar](40) NULL,
	[Rowid] [timestamp] NOT NULL
) ON [PRIMARY]
GO
SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO
CREATE TABLE [BLANKETT_201](
	[Blankettid] [int] IDENTITY(1,1) NOT NULL,
	[Code] [nvarchar](20) NOT NULL,
	[Title] [nvarchar](80) NOT NULL,
	[Filepath] [nvarchar](254) NOT NULL,
	[Sprakkod] [nvarchar](1) NOT NULL,
	[Allowpublish] [bit] NOT NULL,
	[Rowid] [timestamp] NOT NULL,
 CONSTRAINT [PK_BLANKETT_201] PRIMARY KEY CLUSTERED
(
	[Blankettid] ASC
)WITH (PAD_INDEX = OFF, STATISTICS_NORECOMPUTE = OFF, IGNORE_DUP_KEY = OFF, ALLOW_ROW_LOCKS = ON, ALLOW_PAGE_LOCKS = ON) ON [PRIMARY]
) ON [PRIMARY]
GO
SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO
CREATE TABLE [BREV_10](
	[Brevnr] [nvarchar](20) NOT NULL,
	[Brevnrnamn] [nvarchar](80) NULL,
	[Brevtyp] [nvarchar](4) NULL,
	[Rowid] [timestamp] NOT NULL,
	[Allowmodify] [smallint] NULL,
	[Allowcopy] [smallint] NULL,
	[Dokdbclass] [int] NULL,
	[Dokdbcategory] [int] NULL,
	[Dokdbtype] [int] NULL,
	[Fristkod] [nvarchar](6) NULL,
	[Urapportsand] [smallint] NULL,
	[Upaminnelse] [smallint] NULL,
	[Uinstrsand] [smallint] NULL,
	[Allowpublish] [int] NOT NULL,
	[Pdfbackgroundfile] [nvarchar](254) NULL,
	[Emailattachmenttype] [int] NOT NULL,
	[Embeddedimagemaxwidth] [int] NOT NULL,
	[Embeddedimagemaxheight] [int] NOT NULL
) ON [PRIMARY]
GO
SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO
CREATE TABLE [BREVFRIST_151](
	[Landkod] [nvarchar](2) NULL,
	[Arendetyp] [nvarchar](1) NOT NULL,
	[Arendekod] [nvarchar](6) NULL,
	[Prioritavi] [nvarchar](1) NULL,
	[Datumkod] [nvarchar](30) NOT NULL,
	[Fristkod] [nvarchar](6) NULL,
	[Brevnr] [nvarchar](20) NULL,
	[Rowid] [timestamp] NOT NULL,
	[FilingType_Id] [int] NULL
) ON [PRIMARY]
GO
SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO
CREATE TABLE [BREVTEXT_11](
	[Brevnr] [nvarchar](20) NOT NULL,
	[Sprakkod] [nvarchar](1) NOT NULL,
	[Brevtext] [nvarchar](254) NULL,
	[Rowid] [timestamp] NOT NULL,
	[Externalname] [nvarchar](254) NULL,
	[Emailbodytemplate] [nvarchar](50) NULL,
	[Emailsubject] [nvarchar](255) NULL
) ON [PRIMARY]
GO
SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO
CREATE TABLE [BREVTYP_92](
	[Brevtyp] [nvarchar](4) NOT NULL,
	[Namn] [nvarchar](40) NULL,
	[Rowid] [timestamp] NOT NULL
) ON [PRIMARY]
GO
SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO
CREATE TABLE [CALCULATION_TYPE_196](
	[Kalkyltyp] [nvarchar](2) NULL,
	[Sprakkod] [nvarchar](1) NULL,
	[Namn] [nvarchar](254) NULL,
	[Rowid] [timestamp] NULL
) ON [PRIMARY]
GO
SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO
CREATE TABLE [CASE_BASES_190](
	[Arendenr] [nvarchar](50) NULL,
	[Basarendenr] [nvarchar](50) NULL,
	[Id] [int] IDENTITY(1,1) NOT NULL,
 CONSTRAINT [PK_CASE_BASES_190] PRIMARY KEY CLUSTERED
(
	[Id] ASC
)WITH (PAD_INDEX = OFF, STATISTICS_NORECOMPUTE = OFF, IGNORE_DUP_KEY = OFF, ALLOW_ROW_LOCKS = ON, ALLOW_PAGE_LOCKS = ON) ON [PRIMARY]
) ON [PRIMARY]
GO
SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO
CREATE TABLE [CASE_DATE_202](
	[Casedateid] [int] IDENTITY(1,1) NOT NULL,
	[Arendenr] [nvarchar](50) NOT NULL,
	[Datetype] [nvarchar](3) NOT NULL,
	[Datevalue] [datetime] NOT NULL,
 CONSTRAINT [PK_CASE_DATE_202] PRIMARY KEY CLUSTERED
(
	[Casedateid] ASC
)WITH (PAD_INDEX = OFF, STATISTICS_NORECOMPUTE = OFF, IGNORE_DUP_KEY = OFF, ALLOW_ROW_LOCKS = ON, ALLOW_PAGE_LOCKS = ON) ON [PRIMARY]
) ON [PRIMARY]
GO
SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO
CREATE TABLE [CASE_STATISTICS_171](
	[Arendenr] [nvarchar](50) NOT NULL,
	[Invoiced_current_year] [decimal](11, 2) NULL,
	[Invoiced_total] [decimal](11, 2) NULL
) ON [PRIMARY]
GO
SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO
CREATE TABLE [CASEUPDATES_251](
	[Id] [int] IDENTITY(1,1) NOT NULL,
	[Arendenr] [nvarchar](50) NOT NULL,
	[Source] [int] NOT NULL,
	[BatchId] [uniqueidentifier] NOT NULL,
	[Type] [int] NOT NULL,
	[NewValue] [nvarchar](max) NOT NULL,
	[ChangeFound] [datetime] NOT NULL,
	[Handled] [bit] NOT NULL,
 CONSTRAINT [PK_CASEUPDATES_251] PRIMARY KEY CLUSTERED
(
	[Id] ASC
)WITH (PAD_INDEX = OFF, STATISTICS_NORECOMPUTE = OFF, IGNORE_DUP_KEY = OFF, ALLOW_ROW_LOCKS = ON, ALLOW_PAGE_LOCKS = ON) ON [PRIMARY]
) ON [PRIMARY] TEXTIMAGE_ON [PRIMARY]
GO
SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO
CREATE TABLE [COMPENSATION_PHASES_204](
	[Id] [int] IDENTITY(1,1) NOT NULL,
	[Phase] [int] NOT NULL,
	[Sprakkod] [nvarchar](1) NOT NULL,
	[Description] [nvarchar](50) NOT NULL,
	[Rowid] [timestamp] NOT NULL,
 CONSTRAINT [PK_COMPENSATION_PHASES_204] PRIMARY KEY CLUSTERED
(
	[Id] ASC
)WITH (PAD_INDEX = OFF, STATISTICS_NORECOMPUTE = OFF, IGNORE_DUP_KEY = OFF, ALLOW_ROW_LOCKS = ON, ALLOW_PAGE_LOCKS = ON) ON [PRIMARY]
) ON [PRIMARY]
GO
SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO
CREATE TABLE [CONTACTGROUP_187](
	[Id] [int] IDENTITY(1,1) NOT NULL,
	[Contactid] [int] NOT NULL,
	[Grupp] [nvarchar](3) NOT NULL,
	[Rowid] [timestamp] NOT NULL,
 CONSTRAINT [PK_CONTACTGROUP_187] PRIMARY KEY CLUSTERED
(
	[Id] ASC
)WITH (PAD_INDEX = OFF, STATISTICS_NORECOMPUTE = OFF, IGNORE_DUP_KEY = OFF, ALLOW_ROW_LOCKS = ON, ALLOW_PAGE_LOCKS = ON) ON [PRIMARY]
) ON [PRIMARY]
GO
SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO
CREATE TABLE [CPA_ANNUITY_PRICE_282](
	[Id] [int] IDENTITY(1,1) NOT NULL,
	[Arendenr] [nvarchar](50) NOT NULL,
	[Frist] [datetime] NOT NULL,
	[Straff] [bit] NOT NULL,
	[Avgiftskod] [int] NOT NULL,
	[Arforfrist] [nvarchar](50) NOT NULL,
	[Belopp] [decimal](18, 2) NOT NULL,
	[Valutakod] [nvarchar](3) NOT NULL,
	[Arvode] [decimal](18, 2) NOT NULL,
	[Ombarv] [decimal](18, 2) NOT NULL,
	[Offavg] [decimal](18, 2) NOT NULL,
	[Fakturerad] [bit] NOT NULL,
	[Rowid] [timestamp] NOT NULL,
 CONSTRAINT [PK_CPA_ANNUITY_PRICE_282] PRIMARY KEY CLUSTERED
(
	[Id] ASC
)WITH (PAD_INDEX = OFF, STATISTICS_NORECOMPUTE = OFF, IGNORE_DUP_KEY = OFF, ALLOW_ROW_LOCKS = ON, ALLOW_PAGE_LOCKS = ON) ON [PRIMARY]
) ON [PRIMARY]
GO
SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO
CREATE TABLE [CPA_FRISTER_173](
	[Arendenr] [nvarchar](50) NOT NULL,
	[Regelnamns] [nvarchar](50) NULL,
	[Regelnamne] [nvarchar](50) NULL,
	[Arforfrist] [nvarchar](5) NULL,
	[Frist] [smalldatetime] NULL,
	[Friststraff] [smalldatetime] NULL,
	[Cparef] [nvarchar](15) NULL,
	[Rowid] [timestamp] NOT NULL
) ON [PRIMARY]
GO
SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO
CREATE TABLE [CPA_RENEWAL_PRICE_283](
	[Id] [int] IDENTITY(1,1) NOT NULL,
	[Arendenr] [nvarchar](50) NOT NULL,
	[Frist] [datetime] NOT NULL,
	[Straff] [bit] NOT NULL,
	[Period] [int] NOT NULL,
	[Lopno] [int] NOT NULL,
	[Belopp] [decimal](18, 2) NOT NULL,
	[Valutakod] [nvarchar](3) NOT NULL,
	[Arvode] [decimal](18, 2) NOT NULL,
	[ArvodeKlass] [decimal](18, 2) NOT NULL,
	[ArvodePerKlass] [decimal](18, 2) NOT NULL,
	[Ombarv] [decimal](18, 2) NOT NULL,
	[OmbarvKlass] [decimal](18, 2) NOT NULL,
	[OmbarvPerKlass] [decimal](18, 2) NOT NULL,
	[Offavg] [decimal](18, 2) NOT NULL,
	[OffavgKlass] [decimal](18, 2) NOT NULL,
	[OffavgPerKlass] [decimal](18, 2) NOT NULL,
	[Fakturerad] [bit] NOT NULL,
	[Rowid] [timestamp] NOT NULL,
	[AntalKlasser] [int] NOT NULL,
 CONSTRAINT [PK_CPA_RENEWAL_PRICE_283] PRIMARY KEY CLUSTERED
(
	[Id] ASC
)WITH (PAD_INDEX = OFF, STATISTICS_NORECOMPUTE = OFF, IGNORE_DUP_KEY = OFF, ALLOW_ROW_LOCKS = ON, ALLOW_PAGE_LOCKS = ON) ON [PRIMARY]
) ON [PRIMARY]
GO
SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO
CREATE TABLE [CREDIT_LEVEL_334](
	[Creditcode] [nvarchar](1) NOT NULL,
	[Creditdescription] [nvarchar](50) NULL,
	[Rowid] [timestamp] NOT NULL,
	[Type] [int] NOT NULL,
 CONSTRAINT [PK_CREDIT_LEVEL_334] PRIMARY KEY CLUSTERED
(
	[Creditcode] ASC
)WITH (PAD_INDEX = OFF, STATISTICS_NORECOMPUTE = OFF, IGNORE_DUP_KEY = OFF, ALLOW_ROW_LOCKS = ON, ALLOW_PAGE_LOCKS = ON) ON [PRIMARY]
) ON [PRIMARY]
GO
SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO
CREATE TABLE [CUSTOM_ACCOUNTS_197](
	[Saljkonto] [nvarchar](6) NULL,
	[Kostnadskonto] [nvarchar](6) NULL,
	[Rowid] [timestamp] NULL
) ON [PRIMARY]
GO
SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO
CREATE TABLE [DATA_INBOX_BATCH_251](
	[Id] [int] IDENTITY(1,1) NOT NULL,
	[Source] [int] NOT NULL,
	[BatchId] [uniqueidentifier] NOT NULL,
	[ChangeFound] [datetime] NOT NULL,
	[Arendenr] [nvarchar](50) NOT NULL,
 CONSTRAINT [PK_DATA_INBOX_BATCH_251] PRIMARY KEY CLUSTERED
(
	[Id] ASC
)WITH (PAD_INDEX = OFF, STATISTICS_NORECOMPUTE = OFF, IGNORE_DUP_KEY = OFF, ALLOW_ROW_LOCKS = ON, ALLOW_PAGE_LOCKS = ON) ON [PRIMARY]
) ON [PRIMARY]
GO
SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO
CREATE TABLE [DATA_INBOX_CASE_UPDATES_332](
	[Id] [int] IDENTITY(1,1) NOT NULL,
	[DataInboxBatch_Id] [int] NOT NULL,
	[Type] [int] NOT NULL,
	[NewValue] [nvarchar](max) NOT NULL,
	[Handled] [bit] NOT NULL,
 CONSTRAINT [PK_DATA_INBOX_CASE_UPDATES_332] PRIMARY KEY CLUSTERED
(
	[Id] ASC
)WITH (PAD_INDEX = OFF, STATISTICS_NORECOMPUTE = OFF, IGNORE_DUP_KEY = OFF, ALLOW_ROW_LOCKS = ON, ALLOW_PAGE_LOCKS = ON) ON [PRIMARY]
) ON [PRIMARY] TEXTIMAGE_ON [PRIMARY]
GO
SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO
CREATE TABLE [DATA_INBOX_DOCUMENTS_333](
	[Id] [int] IDENTITY(1,1) NOT NULL,
	[DataInboxBatch_Id] [int] NOT NULL,
	[DocumentIdentifier] [nvarchar](500) NOT NULL,
	[DocumentTypeIdentifier] [nvarchar](500) NOT NULL,
	[DocumentTypeName] [nvarchar](500) NULL,
	[Filename] [nvarchar](500) NULL,
	[Handled] [bit] NOT NULL,
 CONSTRAINT [PK_DATA_INBOX_DOCUMENTS_333] PRIMARY KEY CLUSTERED
(
	[Id] ASC
)WITH (PAD_INDEX = OFF, STATISTICS_NORECOMPUTE = OFF, IGNORE_DUP_KEY = OFF, ALLOW_ROW_LOCKS = ON, ALLOW_PAGE_LOCKS = ON) ON [PRIMARY]
) ON [PRIMARY]
GO
SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO
CREATE TABLE [DATUMTYPER_147](
	[Datumkod] [nvarchar](3) NOT NULL,
	[Datumnamn] [nvarchar](50) NULL,
	[Sprakkod] [nvarchar](1) NOT NULL,
	[Rowid] [timestamp] NOT NULL
) ON [PRIMARY]
GO
SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO
CREATE TABLE [DBINFO](
	[Majorversion] [int] NOT NULL,
	[Minorversion] [int] NOT NULL,
	[Notes] [nvarchar](254) NULL,
 CONSTRAINT [PK_DBINFO] PRIMARY KEY CLUSTERED
(
	[Majorversion] ASC,
	[Minorversion] ASC
)WITH (PAD_INDEX = OFF, STATISTICS_NORECOMPUTE = OFF, IGNORE_DUP_KEY = OFF, ALLOW_ROW_LOCKS = ON, ALLOW_PAGE_LOCKS = ON) ON [PRIMARY]
) ON [PRIMARY]
GO
SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO
CREATE TABLE [DBPROPERTY](
	[Dbpropertyid] [int] IDENTITY(1,1) NOT NULL,
	[Displayname] [nvarchar](50) NOT NULL,
	[Timeout] [int] NOT NULL,
	[Customfunctions] [nvarchar](50) NULL,
	[Isdiscoverable] [bit] NOT NULL,
 CONSTRAINT [PK_DBPROPERTY] PRIMARY KEY CLUSTERED
(
	[Dbpropertyid] ASC
)WITH (PAD_INDEX = OFF, STATISTICS_NORECOMPUTE = OFF, IGNORE_DUP_KEY = OFF, ALLOW_ROW_LOCKS = ON, ALLOW_PAGE_LOCKS = ON) ON [PRIMARY]
) ON [PRIMARY]
GO
SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO
CREATE TABLE [DESIG_LANDER_47](
	[Landkod] [nvarchar](2) NOT NULL,
	[Arendenr] [nvarchar](50) NOT NULL,
	[Fullfoljd] [nvarchar](1) NULL,
	[Natreg] [nvarchar](1) NULL,
	[Rowid] [timestamp] NOT NULL,
	[Avslag] [nvarchar](1) NULL,
	[Desarendenr] [nvarchar](50) NULL,
	[Haguelondon1934] [bit] NOT NULL,
	[Haguehague1960] [bit] NOT NULL,
	[Haguegeneva1999] [bit] NOT NULL,
	[Seniority] [datetime] NULL,
	[Id] [int] IDENTITY(1,1) NOT NULL,
 CONSTRAINT [PK_DESIG_LANDER_47] PRIMARY KEY CLUSTERED
(
	[Id] ASC
)WITH (PAD_INDEX = OFF, STATISTICS_NORECOMPUTE = OFF, IGNORE_DUP_KEY = OFF, ALLOW_ROW_LOCKS = ON, ALLOW_PAGE_LOCKS = ON) ON [PRIMARY]
) ON [PRIMARY]
GO
SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO
CREATE TABLE [DESIGN_84](
	[Arendenr] [nvarchar](50) NOT NULL,
	[Antalklass] [smallint] NULL,
	[Antalsamreg] [smallint] NULL,
	[Rowid] [timestamp] NOT NULL
) ON [PRIMARY]
GO
SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO
CREATE TABLE [DIARIE_155](
	[Arendenr] [nvarchar](50) NOT NULL,
	[Tidstampel] [datetime] NOT NULL,
	[Anvid] [int] NULL,
	[Datum] [datetime] NULL,
	[Rowid] [timestamp] NOT NULL,
	[Forklaring] [nvarchar](max) NULL,
	[Id] [int] IDENTITY(1,1) NOT NULL,
 CONSTRAINT [PK_DIARIE_155] PRIMARY KEY NONCLUSTERED
(
	[Id] ASC
)WITH (PAD_INDEX = OFF, STATISTICS_NORECOMPUTE = OFF, IGNORE_DUP_KEY = OFF, ALLOW_ROW_LOCKS = ON, ALLOW_PAGE_LOCKS = ON) ON [PRIMARY]
) ON [PRIMARY] TEXTIMAGE_ON [PRIMARY]
GO
SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO
CREATE TABLE [DISCOUNT_GROUP_269](
	[Id] [int] IDENTITY(1,1) NOT NULL,
	[Code] [nvarchar](20) NOT NULL,
	[Name] [nvarchar](100) NOT NULL,
	[Rowid] [timestamp] NOT NULL,
 CONSTRAINT [PK_DISCOUNT_GROUP_269] PRIMARY KEY CLUSTERED
(
	[Id] ASC
)WITH (PAD_INDEX = OFF, STATISTICS_NORECOMPUTE = OFF, IGNORE_DUP_KEY = OFF, ALLOW_ROW_LOCKS = ON, ALLOW_PAGE_LOCKS = ON) ON [PRIMARY]
) ON [PRIMARY]
GO
SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO
CREATE TABLE [DOMAIN_ACTION_243](
	[Kod] [nvarchar](2) NOT NULL,
	[Text] [nvarchar](25) NOT NULL,
	[Rowid] [timestamp] NOT NULL,
 CONSTRAINT [PK_DOMAIN_ACTION_243] PRIMARY KEY CLUSTERED
(
	[Kod] ASC
)WITH (PAD_INDEX = OFF, STATISTICS_NORECOMPUTE = OFF, IGNORE_DUP_KEY = OFF, ALLOW_ROW_LOCKS = ON, ALLOW_PAGE_LOCKS = ON) ON [PRIMARY]
) ON [PRIMARY]
GO
SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO
CREATE TABLE [DOMAIN_HISTORY_244](
	[Id] [int] IDENTITY(1,1) NOT NULL,
	[Arendenr] [nvarchar](50) NOT NULL,
	[Kod] [nvarchar](2) NOT NULL,
	[Datum] [datetime] NOT NULL,
	[Anvid] [int] NOT NULL,
	[Kommentar] [nvarchar](max) NULL,
	[Rowid] [timestamp] NOT NULL,
 CONSTRAINT [PK_DOMAIN_HISTORY_244] PRIMARY KEY CLUSTERED
(
	[Id] ASC
)WITH (PAD_INDEX = OFF, STATISTICS_NORECOMPUTE = OFF, IGNORE_DUP_KEY = OFF, ALLOW_ROW_LOCKS = ON, ALLOW_PAGE_LOCKS = ON) ON [PRIMARY]
) ON [PRIMARY] TEXTIMAGE_ON [PRIMARY]
GO
SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO
CREATE TABLE [E_INVOICE_ACTIVITY_VALUES_186](
	[Einvoicetype] [smallint] NOT NULL,
	[Transid] [int] NOT NULL,
	[Paramid] [smallint] NOT NULL,
	[Value] [nvarchar](254) NULL
) ON [PRIMARY]
GO
SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO
CREATE TABLE [E_INVOICE_PARAMS_176](
	[Einvoicetype] [smallint] NOT NULL,
	[Paramid] [smallint] NOT NULL,
	[Paramname] [nvarchar](50) NOT NULL,
	[Type] [nvarchar](1) NOT NULL,
	[Maxlength] [smallint] NULL,
	[Optional] [smallint] NOT NULL,
	[Rowid] [timestamp] NOT NULL,
	[Einvoiceaccent] [smallint] NULL
) ON [PRIMARY]
GO
SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO
CREATE TABLE [E_INVOICE_ROW_PARAMS_184](
	[Einvoicetype] [smallint] NOT NULL,
	[Paramid] [smallint] NOT NULL,
	[Paramname] [nvarchar](50) NOT NULL,
	[Type] [nvarchar](1) NOT NULL,
	[Maxlength] [smallint] NULL
) ON [PRIMARY]
GO
SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO
CREATE TABLE [E_INVOICE_ROW_VALUES_185](
	[Fakturanr] [int] NULL,
	[Delfakturanr] [smallint] NOT NULL,
	[Fakturaradnr] [smallint] NOT NULL,
	[Einvoicetype] [smallint] NOT NULL,
	[Paramid] [smallint] NOT NULL,
	[Value] [nvarchar](254) NULL,
	[Fakturaid] [nvarchar](50) NOT NULL
) ON [PRIMARY]
GO
SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO
CREATE TABLE [E_INVOICE_SETTINGS_177](
	[Kundnr] [nvarchar](7) NOT NULL,
	[Einvoicetype] [smallint] NOT NULL,
	[Paramid] [smallint] NOT NULL,
	[Rowid] [timestamp] NOT NULL,
	[Paramvalue] [ntext] NULL,
	[Einvoiceaccent] [smallint] NULL
) ON [PRIMARY] TEXTIMAGE_ON [PRIMARY]
GO
SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO
CREATE TABLE [E_INVOICE_TYPE_175](
	[Einvoicetype] [smallint] NOT NULL,
	[Description] [nvarchar](50) NOT NULL,
	[Rowid] [timestamp] NOT NULL,
	[Einvoiceaccent] [smallint] NOT NULL
) ON [PRIMARY]
GO
SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO
CREATE TABLE [EFFECT_DATE_RULES_231](
	[Landkod] [nvarchar](2) NOT NULL,
	[Epcnationalvalidation] [smallint] NOT NULL,
	[Triggerdatecode] [nvarchar](3) NOT NULL,
	[Rowid] [timestamp] NOT NULL,
	[Copydatecode] [nvarchar](3) NULL,
	[Months] [int] NULL
) ON [PRIMARY]
GO
SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO
CREATE TABLE [EKUPPF_147](
	[Arendenr] [nvarchar](50) NOT NULL,
	[Kundnr] [nvarchar](7) NOT NULL,
	[Fakturatextnr] [smallint] NULL,
	[Regdat] [datetime] NULL,
	[Valutakod] [nvarchar](3) NULL,
	[Levbelopp] [decimal](11, 2) NULL,
	[Belopp] [decimal](11, 2) NULL,
	[Rowid] [timestamp] NOT NULL,
	[Fasid] [nvarchar](6) NULL,
	[Fakturanr] [nvarchar](25) NULL,
	[Anmarkning] [nvarchar](max) NULL,
	[Id] [int] IDENTITY(1,1) NOT NULL,
 CONSTRAINT [PK_EKUPPF_147] PRIMARY KEY CLUSTERED
(
	[Id] ASC
)WITH (PAD_INDEX = OFF, STATISTICS_NORECOMPUTE = OFF, IGNORE_DUP_KEY = OFF, ALLOW_ROW_LOCKS = ON, ALLOW_PAGE_LOCKS = ON) ON [PRIMARY]
) ON [PRIMARY] TEXTIMAGE_ON [PRIMARY]
GO
SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO
CREATE TABLE [EMAIL_TEXT_241](
	[Id] [int] IDENTITY(1,1) NOT NULL,
	[Avinr] [int] NOT NULL,
	[Sprakkod] [nvarchar](1) NOT NULL,
	[Subject] [nvarchar](150) NOT NULL,
	[Body] [nvarchar](max) NOT NULL,
	[Rowid] [timestamp] NOT NULL,
 CONSTRAINT [PK_EMAIL_TEXT_241] PRIMARY KEY CLUSTERED
(
	[Id] ASC
)WITH (PAD_INDEX = OFF, STATISTICS_NORECOMPUTE = OFF, IGNORE_DUP_KEY = OFF, ALLOW_ROW_LOCKS = ON, ALLOW_PAGE_LOCKS = ON) ON [PRIMARY]
) ON [PRIMARY] TEXTIMAGE_ON [PRIMARY]
GO
SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO
CREATE TABLE [ENDFRIST_79](
	[Arendenr] [nvarchar](50) NOT NULL,
	[Lopnr] [smallint] NOT NULL,
	[Fristkod] [nvarchar](6) NOT NULL,
	[Fhandlagg] [int] NULL,
	[Belopp] [decimal](11, 2) NULL,
	[Sidor] [nvarchar](4) NULL,
	[Utfdag] [datetime] NULL,
	[Frist] [datetime] NULL,
	[Rapportsand] [datetime] NULL,
	[Paminnelse] [datetime] NULL,
	[Instrmottagen] [datetime] NULL,
	[Instrsand] [datetime] NULL,
	[Slutdag] [datetime] NULL,
	[Svaromal] [datetime] NULL,
	[Rowid] [timestamp] NOT NULL,
	[Aktfrist_76_id] [int] NULL,
	[Fritext] [ntext] NULL,
	[Fritext2] [ntext] NULL
) ON [PRIMARY] TEXTIMAGE_ON [PRIMARY]
GO
SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO
CREATE TABLE [EXCEL_COLUMN_HEADERS_163](
	[Id] [int] IDENTITY(1,1) NOT NULL,
	[Column_Id] [int] NOT NULL,
	[Sprakkod] [nvarchar](1) NOT NULL,
	[Header] [nvarchar](100) NULL,
	[Rowid] [timestamp] NOT NULL,
 CONSTRAINT [PK_EXCEL_COLUMN_HEADERS_163] PRIMARY KEY CLUSTERED
(
	[Id] ASC
)WITH (PAD_INDEX = OFF, STATISTICS_NORECOMPUTE = OFF, IGNORE_DUP_KEY = OFF, ALLOW_ROW_LOCKS = ON, ALLOW_PAGE_LOCKS = ON) ON [PRIMARY]
) ON [PRIMARY]
GO
SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO
CREATE TABLE [EXCEL_COLUMNS_301](
	[Id] [int] IDENTITY(1,1) NOT NULL,
	[ReportDefinition_Id] [int] NOT NULL,
	[ColumnIdentifier] [nvarchar](25) NOT NULL,
	[ColumnIndex] [int] NOT NULL,
	[Rowid] [timestamp] NOT NULL,
 CONSTRAINT [PK_EXCEL_COLUMNS_301] PRIMARY KEY CLUSTERED
(
	[Id] ASC
)WITH (PAD_INDEX = OFF, STATISTICS_NORECOMPUTE = OFF, IGNORE_DUP_KEY = OFF, ALLOW_ROW_LOCKS = ON, ALLOW_PAGE_LOCKS = ON) ON [PRIMARY]
) ON [PRIMARY]
GO
SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO
CREATE TABLE [EXCEL_DEFAULT_HEADERS_162](
	[Id] [int] IDENTITY(1,1) NOT NULL,
	[ColumnIdentifier] [nvarchar](25) NOT NULL,
	[Sprakkod] [nvarchar](1) NOT NULL,
	[Header] [nvarchar](100) NULL,
	[Rowid] [timestamp] NOT NULL,
 CONSTRAINT [PK_EXCEL_DEFAULT_HEADERS_162] PRIMARY KEY CLUSTERED
(
	[Id] ASC
)WITH (PAD_INDEX = OFF, STATISTICS_NORECOMPUTE = OFF, IGNORE_DUP_KEY = OFF, ALLOW_ROW_LOCKS = ON, ALLOW_PAGE_LOCKS = ON) ON [PRIMARY]
) ON [PRIMARY]
GO
SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO
CREATE TABLE [FAKTKVITTENS_124](
	[Id] [int] NULL,
	[Kundnr] [nvarchar](7) NOT NULL,
	[Kundnamn] [nvarchar](50) NULL,
	[Slagord] [nvarchar](250) NULL,
	[Kredit] [nvarchar](1) NULL,
	[Belopp] [decimal](11, 2) NULL,
	[Beloppkvaluta] [decimal](11, 2) NULL,
	[Hkonto] [nvarchar](6) NULL,
	[Istalle] [nvarchar](6) NULL,
	[Kbarare] [nvarchar](6) NULL,
	[Datum] [datetime] NULL,
	[Fakturadatum] [datetime] NULL,
	[Ombudsnr] [nvarchar](7) NULL,
	[Ombudsnamn] [nvarchar](50) NULL,
	[Anvandare] [int] NULL,
	[Fakturanr] [nvarchar](50) NULL,
	[Tidstampel] [datetime] NULL,
	[Typ] [smallint] NULL,
	[Valutakod] [nvarchar](3) NULL,
	[Transid] [int] NULL,
	[Arendenr] [nvarchar](50) NULL,
	[Rowid] [timestamp] NOT NULL,
	[Rabatt] [decimal](11, 2) NULL,
	[Tid] [float] NULL
) ON [PRIMARY]
GO
SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO
CREATE TABLE [FAKTURAHUVUD_13](
	[Fakturanr] [int] NULL,
	[Sprakkod] [nvarchar](1) NULL,
	[Kundnr] [nvarchar](7) NULL,
	[Debitkredit] [nvarchar](1) NULL,
	[Specfakt] [nvarchar](1) NULL,
	[Samlingsfakt] [nvarchar](1) NULL,
	[Moms] [nvarchar](1) NULL,
	[Anvid] [int] NULL,
	[Fakturatyp] [int] NULL,
	[Agarid] [int] NULL,
	[Bearbetad] [nvarchar](1) NULL,
	[Valutakod] [nvarchar](3) NULL,
	[Valutakurs] [decimal](11, 6) NULL,
	[Delfakturanr] [int] NOT NULL,
	[Rowid] [timestamp] NOT NULL,
	[Fritext] [ntext] NULL,
	[Avireferens] [bit] NULL,
	[Fakturaid] [nvarchar](50) NOT NULL,
	[Officeid] [int] NULL,
	[Printed] [bit] NOT NULL,
	[Createorder] [int] IDENTITY(1,1) NOT NULL,
	[DocumentGUID] [uniqueidentifier] NULL
) ON [PRIMARY] TEXTIMAGE_ON [PRIMARY]
GO
SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO
CREATE TABLE [FAKTURAKONT_23](
	[Fakturanr] [int] NULL,
	[Kontorad] [int] NOT NULL,
	[Lopnr] [smallint] NOT NULL,
	[Arendenr] [nvarchar](50) NULL,
	[Kontonr] [nvarchar](6) NULL,
	[Intaktstalle] [nvarchar](6) NULL,
	[Kostnadsbarare] [nvarchar](6) NULL,
	[Kbelopp] [decimal](11, 2) NULL,
	[Delfakturanr] [int] NOT NULL,
	[Rowid] [timestamp] NOT NULL,
	[Kbelopputl] [decimal](11, 2) NULL,
	[Fakturaid] [nvarchar](50) NOT NULL
) ON [PRIMARY]
GO
SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO
CREATE TABLE [FAKTURARAD_14](
	[Fakturanr] [int] NULL,
	[Fakturaradnr] [int] NOT NULL,
	[Lopnr] [int] NOT NULL,
	[Arendenr] [nvarchar](50) NULL,
	[Avgiftskod] [int] NULL,
	[Beloppvaluta] [decimal](11, 2) NULL,
	[Momsbeloppvaluta] [decimal](18, 8) NULL,
	[Belopp] [decimal](11, 2) NULL,
	[Momsbelopp] [decimal](18, 8) NULL,
	[Textnr] [int] NULL,
	[Delfakturanr] [int] NOT NULL,
	[Underlagid] [int] NULL,
	[Rowid] [timestamp] NOT NULL,
	[Datum] [datetime] NULL,
	[Forslagdatum] [datetime] NULL,
	[Skaparid] [int] NULL,
	[Ordsida] [int] NULL,
	[Tid] [float] NULL,
	[Fritext] [ntext] NULL,
	[Kalkyltyp] [nvarchar](2) NULL,
	[Timarvode] [int] NULL,
	[Maxdatum] [datetime] NULL,
	[Ref] [nvarchar](250) NULL,
	[Transid] [int] NULL,
	[Fakturaid] [nvarchar](50) NOT NULL,
	[Rabatt] [decimal](11, 2) NULL,
	[Rabattvaluta] [decimal](11, 2) NULL,
	[Regel] [nvarchar](10) NULL
) ON [PRIMARY] TEXTIMAGE_ON [PRIMARY]
GO
SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO
CREATE TABLE [FAKTURATEXT_16](
	[Fakturatextnr] [smallint] NOT NULL,
	[Sprakkod] [nvarchar](1) NOT NULL,
	[Skuggfakturatext] [nvarchar](50) NULL,
	[Rowid] [timestamp] NOT NULL,
	[Fakturatext] [nvarchar](max) NULL,
	[Id] [int] IDENTITY(1,1) NOT NULL,
	[Displayfakturatext]  AS ((rtrim(ltrim(str([Fakturatextnr])))+space((1)))+[Skuggfakturatext]) PERSISTED,
 CONSTRAINT [PK_FAKTURATEXT_16] PRIMARY KEY CLUSTERED
(
	[Id] ASC
)WITH (PAD_INDEX = OFF, STATISTICS_NORECOMPUTE = OFF, IGNORE_DUP_KEY = OFF, ALLOW_ROW_LOCKS = ON, ALLOW_PAGE_LOCKS = ON) ON [PRIMARY]
) ON [PRIMARY] TEXTIMAGE_ON [PRIMARY]
GO
SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO
CREATE TABLE [FAKTURATEXT_E_PARAMS_183](
	[Einvoicetype] [smallint] NOT NULL,
	[Paramid] [smallint] NOT NULL,
	[Paramname] [nvarchar](50) NOT NULL,
	[Type] [nvarchar](1) NOT NULL,
	[Maxlength] [smallint] NULL,
	[Rowid] [timestamp] NOT NULL
) ON [PRIMARY]
GO
SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO
CREATE TABLE [FAKTURATEXT_E_SETTINGS_182](
	[Fakturatextnr] [smallint] NOT NULL,
	[Einvoicetype] [smallint] NOT NULL,
	[Paramid] [smallint] NOT NULL,
	[Value] [nvarchar](50) NULL
) ON [PRIMARY]
GO
SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO
CREATE TABLE [FAKTURATEXT_KASKAD_287](
	[Id] [int] IDENTITY(1,1) NOT NULL,
	[Fakturatextnr] [smallint] NOT NULL,
	[Nastatextnr] [smallint] NOT NULL,
	[Sortorder] [smallint] NULL,
 CONSTRAINT [PK_FAKTURATEXT_KASKAD_287] PRIMARY KEY CLUSTERED
(
	[Id] ASC
)WITH (PAD_INDEX = OFF, STATISTICS_NORECOMPUTE = OFF, IGNORE_DUP_KEY = OFF, ALLOW_ROW_LOCKS = ON, ALLOW_PAGE_LOCKS = ON) ON [PRIMARY]
) ON [PRIMARY]
GO
SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO
CREATE TABLE [FAKTURATEXT_LAND_235](
	[Fakturatextnr] [smallint] NOT NULL,
	[Landkod] [nvarchar](2) NOT NULL,
	[Belopp1] [decimal](15, 2) NOT NULL,
	[Valutakod1] [nvarchar](3) NOT NULL,
	[Belopp2] [decimal](15, 2) NOT NULL,
	[Valutakod2] [nvarchar](3) NOT NULL,
	[Rowid] [timestamp] NOT NULL,
	[Id] [int] IDENTITY(1,1) NOT NULL,
 CONSTRAINT [PK_FAKTURATEXT_LAND_235] PRIMARY KEY CLUSTERED
(
	[Id] ASC
)WITH (PAD_INDEX = OFF, STATISTICS_NORECOMPUTE = OFF, IGNORE_DUP_KEY = OFF, ALLOW_ROW_LOCKS = ON, ALLOW_PAGE_LOCKS = ON) ON [PRIMARY]
) ON [PRIMARY]
GO
SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO
CREATE TABLE [FAKTURATEXTNR_15](
	[Fakturatextnr] [smallint] NOT NULL,
	[Momsbelagd] [nvarchar](1) NULL,
	[Kontonr] [nvarchar](6) NULL,
	[Rabatt] [nvarchar](1) NULL,
	[Intaktstalle] [nvarchar](6) NULL,
	[Kostnadsbarare] [nvarchar](6) NULL,
	[Rowid] [timestamp] NOT NULL,
	[Kalkyltyp] [nvarchar](2) NULL,
	[Debiterasjn] [nvarchar](1) NULL,
	[PaslagTimarvode] [int] NULL,
	[Inaktiv] [int] NULL,
	[Klasskod] [nvarchar](1) NULL,
	[DiscountGroup_Id] [int] NULL,
	[Beskrivning] [nvarchar](max) NULL,
	[AmountIncludedInStatistics] [bit] NOT NULL,
	[HoursIncludedInStatistics] [bit] NOT NULL
) ON [PRIMARY] TEXTIMAGE_ON [PRIMARY]
GO
SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO
CREATE TABLE [FAKTURATEXTVALUTA_242](
	[Fakturatextnr] [smallint] NOT NULL,
	[Valutakod] [nvarchar](3) NOT NULL,
	[Belopp] [decimal](15, 2) NULL,
	[Id] [int] IDENTITY(1,1) NOT NULL,
	[Timdeb] [int] NULL,
	[Paslag] [int] NULL,
 CONSTRAINT [PK_FAKTURATEXTVALUTA_242] PRIMARY KEY CLUSTERED
(
	[Id] ASC
)WITH (PAD_INDEX = OFF, STATISTICS_NORECOMPUTE = OFF, IGNORE_DUP_KEY = OFF, ALLOW_ROW_LOCKS = ON, ALLOW_PAGE_LOCKS = ON) ON [PRIMARY]
) ON [PRIMARY]
GO
SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO
CREATE TABLE [FAVOURITE_ARENDE_QUERY_262](
	[Id] [int] NOT NULL,
	[Rowid] [timestamp] NOT NULL,
 CONSTRAINT [PK_FAVOURITE_ARENDE_QUERY_262] PRIMARY KEY CLUSTERED
(
	[Id] ASC
)WITH (PAD_INDEX = OFF, STATISTICS_NORECOMPUTE = OFF, IGNORE_DUP_KEY = OFF, ALLOW_ROW_LOCKS = ON, ALLOW_PAGE_LOCKS = ON) ON [PRIMARY]
) ON [PRIMARY]
GO
SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO
CREATE TABLE [FAVOURITES_169](
	[Autoid] [int] IDENTITY(1,1) NOT NULL,
	[Sitename] [nvarchar](254) NOT NULL,
	[Url] [nvarchar](254) NOT NULL,
	[Position] [smallint] NOT NULL,
	[Rowid] [timestamp] NOT NULL,
	[Display000] [bit] NOT NULL,
	[Display527] [bit] NOT NULL
) ON [PRIMARY]
GO
SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO
CREATE TABLE [FEEGROUP_272](
	[Id] [int] IDENTITY(1,1) NOT NULL,
	[Code] [nvarchar](20) NOT NULL,
	[Name] [nvarchar](100) NOT NULL,
	[Rowid] [timestamp] NOT NULL,
 CONSTRAINT [PK_FEEGROUP_272] PRIMARY KEY CLUSTERED
(
	[Id] ASC
)WITH (PAD_INDEX = OFF, STATISTICS_NORECOMPUTE = OFF, IGNORE_DUP_KEY = OFF, ALLOW_ROW_LOCKS = ON, ALLOW_PAGE_LOCKS = ON) ON [PRIMARY]
) ON [PRIMARY]
GO
SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO
CREATE TABLE [FEEGROUPITEM_273](
	[Id] [int] IDENTITY(1,1) NOT NULL,
	[FeeGroup_Id] [int] NOT NULL,
	[Valutakod] [nvarchar](3) NOT NULL,
	[Landkod] [nvarchar](2) NULL,
	[Kundkategori] [nvarchar](1) NULL,
	[Kontoid] [int] NULL,
	[Amount] [decimal](11, 2) NOT NULL,
	[Rowid] [timestamp] NOT NULL,
 CONSTRAINT [PK_FEEGROUPITEM_273] PRIMARY KEY CLUSTERED
(
	[Id] ASC
)WITH (PAD_INDEX = OFF, STATISTICS_NORECOMPUTE = OFF, IGNORE_DUP_KEY = OFF, ALLOW_ROW_LOCKS = ON, ALLOW_PAGE_LOCKS = ON) ON [PRIMARY]
) ON [PRIMARY]
GO
SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO
CREATE TABLE [FILE_CASE_331](
	[Id] [int] IDENTITY(1,1) NOT NULL,
	[CaseReference] [nvarchar](500) NOT NULL,
	[Arendenr] [nvarchar](50) NULL,
	[Uid] [uniqueidentifier] NOT NULL,
	[IpType] [nvarchar](100) NULL,
	[Rowid] [timestamp] NOT NULL,
	[Identifier] [uniqueidentifier] NULL,
 CONSTRAINT [PK_FILE_CASE_331] PRIMARY KEY CLUSTERED
(
	[Id] ASC
)WITH (PAD_INDEX = OFF, STATISTICS_NORECOMPUTE = OFF, IGNORE_DUP_KEY = OFF, ALLOW_ROW_LOCKS = ON, ALLOW_PAGE_LOCKS = ON) ON [PRIMARY]
) ON [PRIMARY]
GO
SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO
CREATE TABLE [FILE_INSTRUCTION_327](
	[Id] [int] IDENTITY(1,1) NOT NULL,
	[Arendenr] [nvarchar](50) NOT NULL,
	[InstructionId] [uniqueidentifier] NOT NULL,
	[SentToFile] [datetime] NOT NULL,
	[CurrentStatus] [nvarchar](100) NULL,
	[SentToAgent] [datetime] NULL,
	[ReceivedByAgent] [datetime] NULL,
	[AcknowledgedByAgent] [datetime] NULL,
	[SentToPto] [datetime] NULL,
	[FilingReceiptReceived] [datetime] NULL,
	[InstructionComplete] [datetime] NULL,
	[Rowid] [timestamp] NOT NULL,
	[Description] [nvarchar](100) NOT NULL,
	[FileCase_Id] [int] NOT NULL,
	[GrantCertificateReceived] [datetime] NULL,
 CONSTRAINT [PK_FILE_INSTRUCTION_327] PRIMARY KEY CLUSTERED
(
	[Id] ASC
)WITH (PAD_INDEX = OFF, STATISTICS_NORECOMPUTE = OFF, IGNORE_DUP_KEY = OFF, ALLOW_ROW_LOCKS = ON, ALLOW_PAGE_LOCKS = ON) ON [PRIMARY]
) ON [PRIMARY]
GO
SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO
CREATE TABLE [FILINGTYPE_249](
	[Id] [int] IDENTITY(1,1) NOT NULL,
	[Code] [nvarchar](20) NOT NULL,
	[Description] [nvarchar](max) NULL,
	[Rowid] [timestamp] NOT NULL,
 CONSTRAINT [PK_FILINGTYPE_249] PRIMARY KEY CLUSTERED
(
	[Id] ASC
)WITH (PAD_INDEX = OFF, STATISTICS_NORECOMPUTE = OFF, IGNORE_DUP_KEY = OFF, ALLOW_ROW_LOCKS = ON, ALLOW_PAGE_LOCKS = ON) ON [PRIMARY]
) ON [PRIMARY] TEXTIMAGE_ON [PRIMARY]
GO
SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO
CREATE TABLE [FILINGTYPE_LANGUAGE_250](
	[Id] [int] IDENTITY(1,1) NOT NULL,
	[FilingType_Id] [int] NOT NULL,
	[Sprakkod] [nvarchar](1) NOT NULL,
	[Name] [nvarchar](200) NOT NULL,
	[Rowid] [timestamp] NOT NULL,
 CONSTRAINT [PK_FILINGTYPE_LANGUAGE_250] PRIMARY KEY CLUSTERED
(
	[Id] ASC
)WITH (PAD_INDEX = OFF, STATISTICS_NORECOMPUTE = OFF, IGNORE_DUP_KEY = OFF, ALLOW_ROW_LOCKS = ON, ALLOW_PAGE_LOCKS = ON) ON [PRIMARY]
) ON [PRIMARY]
GO
SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO
CREATE TABLE [FORETAGINFO_56](
	[Moms] [decimal](5, 2) NULL,
	[Land] [nvarchar](2) NULL,
	[Sokandenrtyp] [nvarchar](1) NULL,
	[Kundnrtyp] [nvarchar](1) NULL,
	[Fakturanrtyp] [nvarchar](1) NULL,
	[Skarmsprak] [nvarchar](1) NULL,
	[Vernrtyp] [nvarchar](1) NULL,
	[Arendenrtyp] [nvarchar](1) NULL,
	[Kundoverfor] [datetime] NULL,
	[Faktoverfor] [datetime] NULL,
	[Veroverfor] [datetime] NULL,
	[Uppfnrtyp] [nvarchar](1) NULL,
	[Faktaviserbelopp] [smallint] NULL,
	[Varunrtyp] [nvarchar](1) NULL,
	[Monsternrtyp] [nvarchar](1) NULL,
	[Pamforstaavispris] [nvarchar](1) NULL,
	[Projoverfor] [datetime] NULL,
	[Momsavrundning] [nvarchar](1) NULL,
	[Rabattpastraff] [nvarchar](1) NULL,
	[Refnrtyp] [nvarchar](1) NULL,
	[Divpnrtyp] [nvarchar](1) NULL,
	[Divvnrtyp] [nvarchar](1) NULL,
	[Divmnrtyp] [nvarchar](1) NULL,
	[Inhsprak] [nvarchar](1) NULL,
	[Rowid] [timestamp] NOT NULL,
	[Divonrtyp] [nvarchar](1) NULL,
	[Pdfpassword] [nvarchar](60) NULL,
	[Encrypt128] [smallint] NULL,
	[Companyid] [int] NOT NULL,
	[Autofakturanrtyp] [nvarchar](1) NULL,
	[Domannrtyp] [nvarchar](1) NULL,
	[Specialparams] [nvarchar](max) NULL,
	[Patent_electronic] [int] NOT NULL,
	[Trademark_electronic] [int] NOT NULL,
	[Design_electronic] [int] NOT NULL,
	[Domain_electronic] [int] NOT NULL,
	[Div_patent_electronic] [int] NOT NULL,
	[Div_trademark_electronic] [int] NOT NULL,
	[Div_design_electronic] [int] NOT NULL,
	[Div_various_electronic] [int] NOT NULL,
	[Forskottyp] [nvarchar](1) NULL
) ON [PRIMARY] TEXTIMAGE_ON [PRIMARY]
GO
SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO
CREATE TABLE [FRIST_ARENDE_18](
	[Avgiftskod] [smallint] NOT NULL,
	[Arendenr] [nvarchar](50) NOT NULL,
	[Frist] [datetime] NOT NULL,
	[Fristmedstraff] [datetime] NOT NULL,
	[Specialfristjn] [nvarchar](1) NULL,
	[Rowid] [timestamp] NOT NULL,
	[Id] [int] IDENTITY(1,1) NOT NULL,
 CONSTRAINT [PK_FRIST_ARENDE_18] PRIMARY KEY CLUSTERED
(
	[Id] ASC
)WITH (PAD_INDEX = OFF, STATISTICS_NORECOMPUTE = OFF, IGNORE_DUP_KEY = OFF, ALLOW_ROW_LOCKS = ON, ALLOW_PAGE_LOCKS = ON) ON [PRIMARY]
) ON [PRIMARY]
GO
SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO
CREATE TABLE [FRIST_ARENDE_BEHORIG_295](
	[Id] [int] IDENTITY(1,1) NOT NULL,
	[Frist_Id] [int] NOT NULL,
	[User_Id] [int] NOT NULL,
	[Flag] [int] NOT NULL,
	[Rowid] [timestamp] NOT NULL,
	[Remark] [nvarchar](500) NULL,
 CONSTRAINT [PK_FRIST_ARENDE_BEHORIG_295] PRIMARY KEY CLUSTERED
(
	[Id] ASC
)WITH (PAD_INDEX = OFF, STATISTICS_NORECOMPUTE = OFF, IGNORE_DUP_KEY = OFF, ALLOW_ROW_LOCKS = ON, ALLOW_PAGE_LOCKS = ON) ON [PRIMARY]
) ON [PRIMARY]
GO
SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO
CREATE TABLE [FRISTKOD_73](
	[Fristkod] [nvarchar](15) NOT NULL,
	[Extern] [nvarchar](1) NULL,
	[Datumkod] [nvarchar](3) NULL,
	[Klasskod] [nvarchar](1) NULL,
	[Rowid] [timestamp] NOT NULL,
	[Fhandlagg] [int] NULL,
	[Onnew] [nvarchar](6) NULL,
	[Onend] [nvarchar](6) NULL,
	[Doublecheckonnew] [smallint] NULL,
	[Useassistant] [smallint] NOT NULL,
	[Doublecheckonend] [smallint] NULL,
	[Showifattorney] [smallint] NOT NULL,
	[Description] [ntext] NULL,
	[Useattorney] [smallint] NOT NULL,
	[Parent] [nvarchar](6) NULL,
	[Reminderfrom] [int] NULL,
	[Reminderoffsetmonths] [int] NULL,
	[Reminderoffsetdays] [int] NULL,
	[Importance] [int] NOT NULL,
	[State] [int] NOT NULL
) ON [PRIMARY] TEXTIMAGE_ON [PRIMARY]
GO
SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO
CREATE TABLE [FRISTKODNAMN_167](
	[Fristkod] [nvarchar](6) NOT NULL,
	[Sprakkod] [nvarchar](1) NOT NULL,
	[Fristkodnamn] [nvarchar](50) NULL,
	[Rowid] [timestamp] NOT NULL
) ON [PRIMARY]
GO
SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO
CREATE TABLE [FRISTMODUL_74](
	[Fristkod] [nvarchar](6) NOT NULL,
	[Nastafristkod] [nvarchar](6) NOT NULL,
	[Manader] [smallint] NULL,
	[Dagar] [smallint] NULL,
	[Procent] [smallint] NULL,
	[Rowid] [timestamp] NOT NULL,
	[Onnew] [bit] NOT NULL,
	[Onexpiry] [bit] NOT NULL,
	[Onresponse] [bit] NOT NULL,
	[Automaticclosure] [bit] NOT NULL,
	[Issuedatetype] [smallint] NOT NULL,
	[Landkod] [nvarchar](2) NULL,
	[FilingType_Id] [int] NULL,
	[Modulecode]  AS (((([Fristkod]+'-')+coalesce([Landkod],'*'))+'-')+coalesce(CONVERT([nvarchar],[FilingType_Id],0),'*'))
) ON [PRIMARY]
GO
SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO
CREATE TABLE [FRITEXTMETA_153](
	[Objekttyp] [nvarchar](20) NOT NULL,
	[Faltid] [nvarchar](20) NOT NULL,
	[Falttyp] [nvarchar](1) NOT NULL,
	[Ledtext] [nvarchar](50) NULL,
	[Rowid] [timestamp] NOT NULL,
	[Sprakkod] [nvarchar](1) NOT NULL,
	[Hidden] [smallint] NOT NULL,
	[Fixed] [smallint] NOT NULL,
	[Mandatory] [bit] NOT NULL,
	[Autocomplete] [smallint] NOT NULL,
	[Wordlinkfieldcode] [nvarchar](20) NULL,
	[Showonalltabs] [bit] NOT NULL
) ON [PRIMARY]
GO
SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO
CREATE TABLE [GADGET_ANVGRUPP_246](
	[Id] [int] IDENTITY(1,1) NOT NULL,
	[Anvgrupp] [nvarchar](3) NOT NULL,
	[Fonsternummer] [int] NOT NULL,
 CONSTRAINT [PK_GADGET_ANVGRUPP] PRIMARY KEY CLUSTERED
(
	[Id] ASC
)WITH (PAD_INDEX = OFF, STATISTICS_NORECOMPUTE = OFF, IGNORE_DUP_KEY = OFF, ALLOW_ROW_LOCKS = ON, ALLOW_PAGE_LOCKS = ON) ON [PRIMARY]
) ON [PRIMARY]
GO
SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO
CREATE TABLE [GADGETPROFIL_245](
	[Id] [int] IDENTITY(1,1) NOT NULL,
	[Namn] [nvarchar](50) NOT NULL,
	[Anvid] [int] NOT NULL,
	[Xml] [ntext] NOT NULL,
	[Mainwindowimageheight] [int] NULL,
 CONSTRAINT [PK_GADGETPROFIL_245] PRIMARY KEY CLUSTERED
(
	[Id] ASC
)WITH (PAD_INDEX = OFF, STATISTICS_NORECOMPUTE = OFF, IGNORE_DUP_KEY = OFF, ALLOW_ROW_LOCKS = ON, ALLOW_PAGE_LOCKS = ON) ON [PRIMARY]
) ON [PRIMARY] TEXTIMAGE_ON [PRIMARY]
GO
SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO
CREATE TABLE [GEN_ARSAVGFRI_19](
	[Avgiftskod] [smallint] NOT NULL,
	[Arsavgregel] [nvarchar](10) NULL,
	[Landkod] [nvarchar](2) NOT NULL,
	[Arforfrist] [nvarchar](5) NULL,
	[Fristman] [smallint] NULL,
	[Straffman] [smallint] NULL,
	[Rowid] [timestamp] NOT NULL
) ON [PRIMARY]
GO
SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO
CREATE TABLE [GRAPH_TEMPLATE_228](
	[Id] [int] IDENTITY(1,1) NOT NULL,
	[Name] [nvarchar](50) NOT NULL,
	[Sql] [ntext] NOT NULL,
	[Graphtype] [int] NOT NULL,
	[Code] [nvarchar](50) NOT NULL,
 CONSTRAINT [PK_GRAPH_TEMPLATE_] PRIMARY KEY CLUSTERED
(
	[Id] ASC
)WITH (PAD_INDEX = OFF, STATISTICS_NORECOMPUTE = OFF, IGNORE_DUP_KEY = OFF, ALLOW_ROW_LOCKS = ON, ALLOW_PAGE_LOCKS = ON) ON [PRIMARY]
) ON [PRIMARY] TEXTIMAGE_ON [PRIMARY]
GO
SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO
CREATE TABLE [GRAPH_TEMPLATE_LANGUAGE_229](
	[Id] [int] IDENTITY(1,1) NOT NULL,
	[Languagecode] [nvarchar](1) NOT NULL,
	[Description] [nvarchar](100) NOT NULL,
	[Labelx] [nvarchar](50) NULL,
	[Labely] [nvarchar](50) NULL,
	[Graphtemplateid] [int] NOT NULL,
	[ExtendedDescription] [ntext] NULL,
 CONSTRAINT [PK_GRAPH_TEMPLATE_LANGUAGE] PRIMARY KEY CLUSTERED
(
	[Id] ASC
)WITH (PAD_INDEX = OFF, STATISTICS_NORECOMPUTE = OFF, IGNORE_DUP_KEY = OFF, ALLOW_ROW_LOCKS = ON, ALLOW_PAGE_LOCKS = ON) ON [PRIMARY]
) ON [PRIMARY] TEXTIMAGE_ON [PRIMARY]
GO
SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO
CREATE TABLE [GRUPPNAMN_72](
	[Grupp] [nvarchar](3) NOT NULL,
	[Gruppnamn] [nvarchar](20) NULL,
	[Gruppkategori] [nvarchar](1) NULL,
	[Rowid] [timestamp] NOT NULL
) ON [PRIMARY]
GO
SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO
CREATE TABLE [HISTORY_ANSFRIST_189](
	[Aktfrist_76_id] [int] NOT NULL,
	[Agare] [int] NULL,
	[Foretag] [nvarchar](100) NULL,
	[Anvid] [int] NULL,
	[Arendenr] [nvarchar](50) NULL,
	[Ansnr] [nvarchar](50) NULL,
	[Sokandeskortnamn] [nvarchar](50) NULL,
	[Intaktstallenamn] [nvarchar](40) NULL,
	[Landkod] [nvarchar](2) NULL,
	[Grundarende] [nvarchar](50) NULL,
	[Fristkodnamn] [nvarchar](50) NULL,
	[Fristkod] [nvarchar](6) NULL,
	[Tid] [datetime] NULL,
	[Handlaggarid] [int] NULL,
	[Statuskod] [nvarchar](1) NULL,
	[Utfdag] [datetime] NULL,
	[Frist] [datetime] NULL,
	[Rapportsand] [datetime] NULL,
	[Paminnelse] [datetime] NULL,
	[Instrmottagen] [datetime] NULL,
	[Instrsand] [datetime] NULL,
	[Slutdag] [datetime] NULL,
	[Svaromal] [datetime] NULL,
	[Fhandlagg] [int] NULL,
	[Lopnr] [smallint] NULL,
	[Belopp] [int] NULL,
	[Sidor] [nvarchar](4) NULL,
	[Nyjn] [nvarchar](1) NULL,
	[Avslutadjn] [nvarchar](1) NULL,
	[Extern] [nvarchar](1) NULL,
	[Slagord] [nvarchar](250) NULL,
	[Anm] [ntext] NULL,
	[Reviewed] [bit] NULL,
	[Reviewuser] [int] NULL,
	[Reviewdate] [datetime] NULL,
	[Importance] [int] NOT NULL,
	[Rowid] [timestamp] NOT NULL,
	[Fritext2] [ntext] NULL
) ON [PRIMARY] TEXTIMAGE_ON [PRIMARY]
GO
SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO
CREATE TABLE [IMAGE_265](
	[Id] [int] IDENTITY(1,1) NOT NULL,
	[Filename] [nvarchar](255) NOT NULL,
	[Originaldata] [varbinary](max) NOT NULL,
	[Originalchecksum] [binary](16) NOT NULL,
	[Pngthumbnail] [varbinary](max) NOT NULL,
	[Rowid] [timestamp] NOT NULL,
 CONSTRAINT [PK_IMAGE_265] PRIMARY KEY CLUSTERED
(
	[Id] ASC
)WITH (PAD_INDEX = OFF, STATISTICS_NORECOMPUTE = OFF, IGNORE_DUP_KEY = OFF, ALLOW_ROW_LOCKS = ON, ALLOW_PAGE_LOCKS = ON) ON [PRIMARY]
) ON [PRIMARY] TEXTIMAGE_ON [PRIMARY]
GO
SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO
CREATE TABLE [INSTANS_139](
	[Kod] [nvarchar](1) NOT NULL,
	[Namn] [nvarchar](40) NULL,
	[Rowid] [timestamp] NOT NULL
) ON [PRIMARY]
GO
SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO
CREATE TABLE [INSTRUCTION_QUEUE_268](
	[Id] [int] IDENTITY(1,1) NOT NULL,
	[InstructionIdentifier] [int] NULL,
	[Arendenr] [nvarchar](50) NOT NULL,
	[State] [int] NOT NULL,
	[Type] [int] NOT NULL,
	[Avgiftskod] [int] NOT NULL,
	[Rowid] [timestamp] NOT NULL,
	[Arforfrist] [nvarchar](5) NULL,
	[Frist] [datetime] NOT NULL,
	[CaseType] [int] NOT NULL,
 CONSTRAINT [PK_INSTRUCTION_QUEUE_268] PRIMARY KEY CLUSTERED
(
	[Id] ASC
)WITH (PAD_INDEX = OFF, STATISTICS_NORECOMPUTE = OFF, IGNORE_DUP_KEY = OFF, ALLOW_ROW_LOCKS = ON, ALLOW_PAGE_LOCKS = ON) ON [PRIMARY]
) ON [PRIMARY]
GO
SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO
CREATE TABLE [INSTRUCTION_QUEUE_EVENTS_271](
	[Id] [int] IDENTITY(1,1) NOT NULL,
	[Instruction_Id] [int] NOT NULL,
	[Type] [int] NOT NULL,
	[User_Id] [int] NOT NULL,
	[Date] [datetime] NOT NULL,
	[Remark] [nvarchar](max) NULL,
	[Rowid] [timestamp] NOT NULL,
 CONSTRAINT [PK_INSTRUCTION_QUEUE_EVENTS_269] PRIMARY KEY CLUSTERED
(
	[Id] ASC
)WITH (PAD_INDEX = OFF, STATISTICS_NORECOMPUTE = OFF, IGNORE_DUP_KEY = OFF, ALLOW_ROW_LOCKS = ON, ALLOW_PAGE_LOCKS = ON) ON [PRIMARY]
) ON [PRIMARY] TEXTIMAGE_ON [PRIMARY]
GO
SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO
CREATE TABLE [INTAKTSTALLE_21](
	[Intaktstalle] [nvarchar](6) NOT NULL,
	[Intaktstallenamn] [nvarchar](40) NULL,
	[Kundnr] [nvarchar](7) NULL,
	[Rowid] [timestamp] NOT NULL,
	[Officeid] [int] NOT NULL,
	[Inaktiv] [bit] NOT NULL
) ON [PRIMARY]
GO
SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO
CREATE TABLE [INTERNAL_INFO_172](
	[Optionname] [nvarchar](50) NOT NULL,
	[Svalue] [nvarchar](254) NULL,
	[Nvalue] [decimal](11, 2) NULL,
	[Dvalue] [datetime] NULL,
	[Tvalue] [ntext] NULL
) ON [PRIMARY] TEXTIMAGE_ON [PRIMARY]
GO
SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO
CREATE TABLE [INTERNALTIME_193](
	[Id] [int] NOT NULL,
	[Datum] [datetime] NOT NULL,
	[Regtid] [datetime] NOT NULL,
	[Atgardsnr] [smallint] NULL,
	[Tid] [decimal](11, 2) NULL,
	[Belopp] [decimal](11, 2) NULL,
	[Transid] [int] NULL,
	[Ordsida] [int] NULL,
	[Arendenr] [nvarchar](50) NULL,
	[Rowid] [timestamp] NOT NULL,
	[Kundnr] [nvarchar](7) NULL,
	[Tidkod] [nvarchar](3) NULL,
	[Timestamp] [datetime] NULL,
	[Timdeb] [int] NULL,
	[Export] [int] NULL,
	[Sparat] [datetime] NULL,
	[Atgardstext] [ntext] NULL,
	[Crmkundid] [nvarchar](7) NULL,
	[Crmarendeid] [nvarchar](50) NULL,
	[Debtid] [decimal](11, 2) NULL
) ON [PRIMARY] TEXTIMAGE_ON [PRIMARY]
GO
SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO
CREATE TABLE [INVENTOR_COMPENSATION_205](
	[Id] [int] IDENTITY(1,1) NOT NULL,
	[Arendenr] [nvarchar](50) NOT NULL,
	[Uppfnr] [nvarchar](7) NOT NULL,
 CONSTRAINT [PK_INVENTOR_COMPENSATION_205] PRIMARY KEY CLUSTERED
(
	[Id] ASC
)WITH (PAD_INDEX = OFF, STATISTICS_NORECOMPUTE = OFF, IGNORE_DUP_KEY = OFF, ALLOW_ROW_LOCKS = ON, ALLOW_PAGE_LOCKS = ON) ON [PRIMARY]
) ON [PRIMARY]
GO
SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO
CREATE TABLE [INVENTOR_COMPENSATION_PHASE_206](
	[Id] [int] IDENTITY(1,1) NOT NULL,
	[Compensationid] [int] NOT NULL,
	[Phase] [int] NOT NULL,
	[Paiddate] [datetime] NULL,
	[Amount] [int] NOT NULL,
	[Rowid] [timestamp] NOT NULL,
 CONSTRAINT [PK_INVENTOR_COMPENSATION_PHASE_206] PRIMARY KEY CLUSTERED
(
	[Id] ASC
)WITH (PAD_INDEX = OFF, STATISTICS_NORECOMPUTE = OFF, IGNORE_DUP_KEY = OFF, ALLOW_ROW_LOCKS = ON, ALLOW_PAGE_LOCKS = ON) ON [PRIMARY]
) ON [PRIMARY]
GO
SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO
CREATE TABLE [INVOICE_EXPORT_267](
	[Invoicenumber] [int] NULL,
	[Issuedate] [datetime] NULL,
	[Kundnr] [nvarchar](7) NULL,
	[Intaktstalle] [nvarchar](6) NULL,
	[Currency] [int] NULL,
	[Servicewithvat] [decimal](11, 2) NULL,
	[Servicenovat] [decimal](11, 2) NULL,
	[Expensewithvat] [decimal](11, 2) NULL,
	[Expensenovat] [decimal](11, 2) NULL,
	[Noterionwithvat] [decimal](11, 2) NULL,
	[Noterionnovat] [decimal](11, 2) NULL,
	[Shonotwithvat] [decimal](11, 2) NULL,
	[Shonotnovat] [decimal](11, 2) NULL,
	[Totalwithvat] [decimal](11, 2) NULL,
	[Totalnovat] [decimal](11, 2) NULL,
	[Vatpercent] [decimal](11, 2) NULL,
	[Vat] [decimal](11, 2) NULL,
	[Totalinclvat] [decimal](11, 2) NULL,
	[Discount] [decimal](11, 2) NULL,
	[Sumpayment] [decimal](11, 2) NULL,
	[Lastupdate] [datetime] NULL,
	[Exported] [datetime] NULL,
	[Imported] [datetime] NULL,
	[Externalid] [nvarchar](20) NULL
) ON [PRIMARY]
GO
SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO
CREATE TABLE [INVOICE_HISTORY_188](
	[Arendenr] [nvarchar](50) NULL,
	[Kundnr] [nvarchar](7) NULL,
	[Faktnr] [int] NULL,
	[Faktdatum] [datetime] NULL,
	[Bokfdatum] [datetime] NULL,
	[Faktbeloppvaluta] [decimal](11, 2) NULL,
	[Valutakod] [nvarchar](3) NULL,
	[Faktbelopp] [decimal](11, 2) NULL,
	[Momsbelopp] [decimal](11, 2) NULL,
	[Skapat] [datetime] NULL,
	[Rowguid] [uniqueidentifier] NOT NULL,
	[Verifnr] [int] NULL,
	[Forfallodatum] [datetime] NULL,
	[Kontonrfodran] [int] NULL,
	[Intaktstalle] [nvarchar](6) NULL,
	[Kostnadsbarare] [nvarchar](6) NULL,
	[Projekt] [nvarchar](100) NULL
) ON [PRIMARY]
GO
SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO
CREATE TABLE [KASKAD_160](
	[Kaskadkod] [nvarchar](3) NOT NULL,
	[Benamning] [nvarchar](40) NOT NULL,
	[Rowid] [timestamp] NOT NULL
) ON [PRIMARY]
GO
SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO
CREATE TABLE [KASKADBREV_161](
	[Kaskadkod] [nvarchar](3) NOT NULL,
	[Landkod] [nvarchar](2) NOT NULL,
	[Brevnr] [nvarchar](20) NOT NULL,
	[Rowid] [timestamp] NOT NULL
) ON [PRIMARY]
GO
SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO
CREATE TABLE [KLASSER_86](
	[Arendenr] [nvarchar](50) NOT NULL,
	[Klassnr] [nvarchar](30) NOT NULL,
	[Rowid] [timestamp] NOT NULL,
	[Designnr] [nvarchar](2) NULL,
	[Varor] [nvarchar](max) NULL,
	[Varor2] [nvarchar](max) NULL,
	[Id] [int] IDENTITY(1,1) NOT NULL,
 CONSTRAINT [PK_KLASSER_86] PRIMARY KEY CLUSTERED
(
	[Id] ASC
)WITH (PAD_INDEX = OFF, STATISTICS_NORECOMPUTE = OFF, IGNORE_DUP_KEY = OFF, ALLOW_ROW_LOCKS = ON, ALLOW_PAGE_LOCKS = ON) ON [PRIMARY]
) ON [PRIMARY] TEXTIMAGE_ON [PRIMARY]
GO
SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO
CREATE TABLE [KLASSNR_85](
	[Klassnr] [nvarchar](30) NOT NULL,
	[Klasskod] [nvarchar](1) NOT NULL,
	[Rowid] [timestamp] NOT NULL,
	[Varor] [nvarchar](max) NULL,
	[Varor2] [nvarchar](max) NULL
) ON [PRIMARY] TEXTIMAGE_ON [PRIMARY]
GO
SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO
CREATE TABLE [KONTAKT_130](
	[Contactid] [int] IDENTITY(1,1) NOT NULL,
	[Kundnr] [nvarchar](7) NOT NULL,
	[Kontakt] [nvarchar](50) NULL,
	[Addressno]  AS ([Contactid]),
	[Sprakkod] [nvarchar](1) NULL,
	[Comment] [nvarchar](max) NULL,
	[Meansofcommunication] [int] NULL,
	[Rowid] [timestamp] NOT NULL,
	[Extref] [nvarchar](50) NULL,
 CONSTRAINT [PK_KONTAKT_130] PRIMARY KEY CLUSTERED
(
	[Contactid] ASC
)WITH (PAD_INDEX = OFF, STATISTICS_NORECOMPUTE = OFF, IGNORE_DUP_KEY = OFF, ALLOW_ROW_LOCKS = ON, ALLOW_PAGE_LOCKS = ON) ON [PRIMARY]
) ON [PRIMARY] TEXTIMAGE_ON [PRIMARY]
GO
SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO
CREATE TABLE [KONTO_22](
	[Kontokod] [smallint] NOT NULL,
	[Kontonr] [nvarchar](6) NULL,
	[Kontokodnamn] [nvarchar](25) NULL,
	[Rowid] [timestamp] NOT NULL
) ON [PRIMARY]
GO
SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO
CREATE TABLE [KONTOKOD_132](
	[Kontokod] [nvarchar](6) NOT NULL,
	[Kontoid] [nvarchar](1) NOT NULL,
	[Kontonr] [nvarchar](6) NULL,
	[Rowid] [timestamp] NOT NULL
) ON [PRIMARY]
GO
SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO
CREATE TABLE [KONTOTABELL_58](
	[Kontonr] [nvarchar](6) NOT NULL,
	[Beskrivning] [nvarchar](25) NULL,
	[Momsjn] [nvarchar](1) NULL,
	[Rowid] [timestamp] NOT NULL
) ON [PRIMARY]
GO
SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO
CREATE TABLE [KOSTNADSBARARE_99](
	[Kostnadsbarare] [nvarchar](6) NOT NULL,
	[Namn] [nvarchar](40) NULL,
	[Rowid] [timestamp] NOT NULL,
	[Inaktiv] [bit] NOT NULL
) ON [PRIMARY]
GO
SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO
CREATE TABLE [KUND_24](
	[Kundnr] [nvarchar](7) NOT NULL,
	[Kundkategori] [nvarchar](1) NULL,
	[Kontokod] [smallint] NULL,
	[Valutakod] [nvarchar](3) NOT NULL,
	[Levvaluta] [nvarchar](3) NULL,
	[Landkod] [nvarchar](2) NOT NULL,
	[Sprakkod] [nvarchar](1) NOT NULL,
	[Servicetyp] [nvarchar](1) NULL,
	[Betalningskod] [int] NULL,
	[Kreditjn] [nvarchar](1) NULL,
	[Kundadress1] [nvarchar](100) NULL,
	[Kundadress2] [nvarchar](100) NULL,
	[Kundadress3] [nvarchar](100) NULL,
	[Kundadress4] [nvarchar](100) NULL,
	[Kundadress5] [nvarchar](100) NULL,
	[Kundadress6] [nvarchar](100) NULL,
	[Kundadress7] [nvarchar](100) NULL,
	[Telefonnrkund] [nvarchar](50) NULL,
	[Kortnamnkund] [nvarchar](50) NOT NULL,
	[Faxnr] [nvarchar](50) NULL,
	[Momsjn] [nvarchar](1) NULL,
	[Specfakturajn] [nvarchar](1) NULL,
	[Koncern] [nvarchar](7) NULL,
	[Kortort] [nvarchar](15) NULL,
	[Avikundjn] [nvarchar](1) NULL,
	[Andratdat] [datetime] NULL,
	[Orgnr] [nvarchar](20) NULL,
	[Skapatdat] [datetime] NULL,
	[Faktegenvalutajn] [nvarchar](1) NULL,
	[Extrabetdagar] [smallint] NULL,
	[Fkund] [nvarchar](7) NULL,
	[Pansvar] [int] NULL,
	[Vansvar] [int] NULL,
	[Xmoms] [nvarchar](2) NULL,
	[Anv] [int] NULL,
	[Prisnr] [smallint] NULL,
	[Varkund] [smallint] NULL,
	[Levadress] [nvarchar](500) NULL,
	[Servicev] [nvarchar](1) NULL,
	[Servicem] [nvarchar](1) NULL,
	[Mobil] [nvarchar](50) NULL,
	[Inaktivjn] [int] NULL,
	[Akund] [nvarchar](7) NULL,
	[Afakt] [nvarchar](7) NULL,
	[Samlingsfaktjn] [nvarchar](1) NULL,
	[Fornyviaannan] [nvarchar](1) NULL,
	[Fusion] [nvarchar](1) NULL,
	[Tidigarenamn] [nvarchar](100) NULL,
	[Internetadr] [nvarchar](254) NULL,
	[Rowid] [timestamp] NOT NULL,
	[Rowguid] [uniqueidentifier] NOT NULL,
	[Einvoicetype] [smallint] NOT NULL,
	[Timdeb] [int] NULL,
	[Fakturalayout] [nvarchar](3) NULL,
	[Fritextkund] [nvarchar](max) NULL,
	[Xmlinvoicetypeid] [smallint] NOT NULL,
	[Transferconfigurationsetid] [int] NULL,
	[Kurs] [decimal](11, 6) NULL,
	[Upprakningsprocent] [decimal](5, 2) NULL,
	[Einvoiceaccent] [smallint] NOT NULL,
	[Enableipforecaster] [bit] NOT NULL,
	[Meansofcommunication] [int] NULL,
	[Automatfakturajn] [nvarchar](1) NOT NULL,
	[Usebasicoutsourcingsurcharge] [bit] NOT NULL,
	[Invoicecontact] [int] NULL,
	[EmailAddresses_To] [nvarchar](max) NULL,
	[EmailAddresses_Cc] [nvarchar](max) NULL,
	[EmailAddresses_Bcc] [nvarchar](max) NULL,
	[NotificationEmailAddresses_To] [nvarchar](max) NULL,
	[NotificationEmailAddresses_Cc] [nvarchar](max) NULL,
	[NotificationEmailAddresses_Bcc] [nvarchar](max) NULL,
	[IsAgentInFile] [bit] NOT NULL,
 CONSTRAINT [PK_KUND_24] PRIMARY KEY CLUSTERED
(
	[Kundnr] ASC
)WITH (PAD_INDEX = OFF, STATISTICS_NORECOMPUTE = OFF, IGNORE_DUP_KEY = OFF, ALLOW_ROW_LOCKS = ON, ALLOW_PAGE_LOCKS = ON) ON [PRIMARY]
) ON [PRIMARY] TEXTIMAGE_ON [PRIMARY]
GO
SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO
CREATE TABLE [KUND_ARENDE_25](
	[Kundtyp] [smallint] NOT NULL,
	[Arendenr] [nvarchar](50) NOT NULL,
	[Kundnr] [nvarchar](7) NOT NULL,
	[Part] [smallint] NOT NULL,
	[Ref] [nvarchar](250) NULL,
	[Att] [nvarchar](50) NULL,
	[Rowid] [timestamp] NOT NULL,
	[Useaddress] [smallint] NOT NULL,
	[Id] [int] IDENTITY(1,1) NOT NULL,
	[Contactid] [int] NULL,
 CONSTRAINT [PK_KUND_ARENDE_25] PRIMARY KEY NONCLUSTERED
(
	[Id] ASC
)WITH (PAD_INDEX = OFF, STATISTICS_NORECOMPUTE = OFF, IGNORE_DUP_KEY = OFF, ALLOW_ROW_LOCKS = ON, ALLOW_PAGE_LOCKS = ON) ON [PRIMARY]
) ON [PRIMARY]
GO
SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO
CREATE TABLE [KUND_ARVODE_104](
	[Kundnr] [nvarchar](7) NOT NULL,
	[Klasskod] [nvarchar](1) NULL,
	[Landkod] [nvarchar](2) NULL,
	[Period] [smallint] NULL,
	[Valutakod] [nvarchar](3) NULL,
	[Arvode] [int] NULL,
	[Straffarvode] [int] NULL,
	[Arvodeklass] [int] NULL,
	[Straffarvodeklass] [int] NULL,
	[Arvodesamreg] [int] NULL,
	[Straffarvodesamreg] [int] NULL,
	[Rabattjn] [nvarchar](1) NULL,
	[Rowid] [timestamp] NOT NULL,
	[Id] [int] IDENTITY(1,1) NOT NULL,
 CONSTRAINT [PK_KUND_ARVODE_104] PRIMARY KEY CLUSTERED
(
	[Id] ASC
)WITH (PAD_INDEX = OFF, STATISTICS_NORECOMPUTE = OFF, IGNORE_DUP_KEY = OFF, ALLOW_ROW_LOCKS = ON, ALLOW_PAGE_LOCKS = ON) ON [PRIMARY]
) ON [PRIMARY]
GO
SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO
CREATE TABLE [KUND_BEHORIG_230](
	[Id] [int] IDENTITY(1,1) NOT NULL,
	[User_Id] [int] NOT NULL,
	[Kundnr] [nvarchar](7) NOT NULL,
	[Amount] [decimal](11, 2) NOT NULL,
	[Rowid] [timestamp] NOT NULL,
 CONSTRAINT [PK_KUND_BEHORIG_230] PRIMARY KEY CLUSTERED
(
	[Id] ASC
)WITH (PAD_INDEX = OFF, STATISTICS_NORECOMPUTE = OFF, IGNORE_DUP_KEY = OFF, ALLOW_ROW_LOCKS = ON, ALLOW_PAGE_LOCKS = ON) ON [PRIMARY]
) ON [PRIMARY]
GO
SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO
CREATE TABLE [KUND_DISCOUNT_270](
	[Id] [int] IDENTITY(1,1) NOT NULL,
	[Kundnr] [nvarchar](7) NOT NULL,
	[Fakturatextnr] [int] NULL,
	[DiscountGroup_Id] [int] NULL,
	[Landkod] [nvarchar](2) NULL,
	[Klasskod] [nvarchar](1) NULL,
	[DiscountPercentage] [decimal](5, 2) NULL,
	[FixedPrice] [decimal](11, 2) NULL,
	[Rowid] [timestamp] NOT NULL,
	[Type] [int] NOT NULL,
 CONSTRAINT [PK_KUND_DISCOUNT_270] PRIMARY KEY CLUSTERED
(
	[Id] ASC
)WITH (PAD_INDEX = OFF, STATISTICS_NORECOMPUTE = OFF, IGNORE_DUP_KEY = OFF, ALLOW_ROW_LOCKS = ON, ALLOW_PAGE_LOCKS = ON) ON [PRIMARY]
) ON [PRIMARY]
GO
SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO
CREATE TABLE [KUND_EXTRAINFO_285](
	[Id] [int] IDENTITY(1,1) NOT NULL,
	[Kundnr] [nvarchar](7) NOT NULL,
	[Faltid] [nvarchar](20) NOT NULL,
	[Value] [nvarchar](max) NULL,
	[Rowid] [timestamp] NOT NULL,
 CONSTRAINT [PK_KUND_EXTRAINFO_285] PRIMARY KEY CLUSTERED
(
	[Id] ASC
)WITH (PAD_INDEX = OFF, STATISTICS_NORECOMPUTE = OFF, IGNORE_DUP_KEY = OFF, ALLOW_ROW_LOCKS = ON, ALLOW_PAGE_LOCKS = ON) ON [PRIMARY]
) ON [PRIMARY] TEXTIMAGE_ON [PRIMARY]
GO
SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO
CREATE TABLE [KUND_FEEGROUP_274](
	[Id] [int] IDENTITY(1,1) NOT NULL,
	[FeeGroup_Id] [int] NOT NULL,
	[Kundnr] [nvarchar](7) NOT NULL,
	[Amount] [decimal](11, 2) NOT NULL,
	[Rowid] [timestamp] NOT NULL,
 CONSTRAINT [PK_KUND_FEEGROUP_274] PRIMARY KEY CLUSTERED
(
	[Id] ASC
)WITH (PAD_INDEX = OFF, STATISTICS_NORECOMPUTE = OFF, IGNORE_DUP_KEY = OFF, ALLOW_ROW_LOCKS = ON, ALLOW_PAGE_LOCKS = ON) ON [PRIMARY]
) ON [PRIMARY]
GO
SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO
CREATE TABLE [KUNDKAT_55](
	[Kundkategori] [nvarchar](1) NOT NULL,
	[Kundkatbesk] [nvarchar](30) NULL,
	[Rowid] [timestamp] NOT NULL
) ON [PRIMARY]
GO
SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO
CREATE TABLE [KUNDSORT_71](
	[Kundnr] [nvarchar](7) NOT NULL,
	[Grupp] [nvarchar](3) NOT NULL,
	[Datum] [datetime] NULL,
	[Rowid] [timestamp] NOT NULL,
	[Id] [int] IDENTITY(1,1) NOT NULL,
 CONSTRAINT [PK_KUNDSORT_71] PRIMARY KEY CLUSTERED
(
	[Id] ASC
)WITH (PAD_INDEX = OFF, STATISTICS_NORECOMPUTE = OFF, IGNORE_DUP_KEY = OFF, ALLOW_ROW_LOCKS = ON, ALLOW_PAGE_LOCKS = ON) ON [PRIMARY]
) ON [PRIMARY]
GO
SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO
CREATE TABLE [KUNDTYP_26](
	[Kundtyp] [int] NOT NULL,
	[Kundtypbesk] [nvarchar](50) NULL,
	[Rowid] [timestamp] NOT NULL,
	[Sprakkod] [nvarchar](1) NOT NULL
) ON [PRIMARY]
GO
SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO
CREATE TABLE [LAGRADEALTINNDOKUMENT_237](
	[Altinndocumentid] [int] NOT NULL,
	[Datestored] [datetime] NOT NULL,
 CONSTRAINT [PK_LAGRADEALTINNDOKUMENT_237] PRIMARY KEY CLUSTERED
(
	[Altinndocumentid] ASC
)WITH (PAD_INDEX = OFF, STATISTICS_NORECOMPUTE = OFF, IGNORE_DUP_KEY = OFF, ALLOW_ROW_LOCKS = ON, ALLOW_PAGE_LOCKS = ON) ON [PRIMARY]
) ON [PRIMARY]
GO
SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO
CREATE TABLE [LAND_28](
	[Landkod] [nvarchar](2) NOT NULL,
	[Valutakod] [nvarchar](3) NOT NULL,
	[Berakningsregel] [smallint] NULL,
	[Pvextrabetdagar] [smallint] NULL,
	[Pvsistaiman] [nvarchar](1) NULL,
	[Aripo] [nvarchar](1) NULL,
	[Oapi] [nvarchar](1) NULL,
	[Eurasien] [nvarchar](1) NULL,
	[Wipo] [nvarchar](1) NULL,
	[Epc] [nvarchar](1) NULL,
	[Eu] [nvarchar](1) NULL,
	[M1] [nvarchar](1) NULL,
	[M2] [nvarchar](1) NULL,
	[Pct] [nvarchar](1) NULL,
	[Rowid] [timestamp] NOT NULL,
	[Dialingcode] [nvarchar](5) NULL,
	[Regelp] [ntext] NULL,
	[Regelv] [ntext] NULL,
	[Regelm] [ntext] NULL,
	[Regela] [ntext] NULL,
	[Haguelondon1934] [bit] NOT NULL,
	[Haguehague1960] [bit] NOT NULL,
	[Haguegeneva1999] [bit] NOT NULL,
	[Trademarkrenewaloffset] [smallint] NOT NULL,
	[Epannuityoffset] [smallint] NOT NULL,
	[Gcc] [nvarchar](1) NULL,
	[Patenthyperlink] [nvarchar](500) NULL,
	[Trademarkhyperlink] [nvarchar](500) NULL,
	[Designhyperlink] [nvarchar](500) NULL,
	[Patentbtnname] [nvarchar](50) NULL,
	[Trademarkbtnname] [nvarchar](50) NULL,
	[Designbtnname] [nvarchar](50) NULL,
	[Exportpatents] [bit] NOT NULL,
	[Exporttrademarks] [bit] NOT NULL,
	[Exportdesigns] [bit] NOT NULL
) ON [PRIMARY] TEXTIMAGE_ON [PRIMARY]
GO
SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO
CREATE TABLE [LEDTEXT_70](
	[Textnr] [int] NOT NULL,
	[Arendetyp] [nvarchar](1) NOT NULL,
	[Sprakkod] [nvarchar](1) NOT NULL,
	[Ledtext] [nvarchar](50) NULL,
	[Rowid] [timestamp] NOT NULL
) ON [PRIMARY]
GO
SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO
CREATE TABLE [LEVFAKTEXP_129](
	[Agarid] [int] NULL,
	[Rowid] [timestamp] NOT NULL,
	[Rad] [ntext] NULL,
	[Id] [int] IDENTITY(1,1) NOT NULL,
 CONSTRAINT [PK_LEVFAKTEXP_129] PRIMARY KEY CLUSTERED
(
	[Id] ASC
)WITH (PAD_INDEX = OFF, STATISTICS_NORECOMPUTE = OFF, IGNORE_DUP_KEY = OFF, ALLOW_ROW_LOCKS = ON, ALLOW_PAGE_LOCKS = ON) ON [PRIMARY]
) ON [PRIMARY] TEXTIMAGE_ON [PRIMARY]
GO
SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO
CREATE TABLE [LOCALDNS_199](
	[Localdnsid] [int] IDENTITY(1,1) NOT NULL,
	[Arendenr] [nvarchar](50) NOT NULL,
	[Name] [nvarchar](255) NULL,
	[Ipaddress] [nvarchar](50) NULL,
	[Sortorder] [int] NOT NULL,
	[Isprimary] [bit] NOT NULL,
	[Rowid] [timestamp] NOT NULL,
 CONSTRAINT [PK_Localdns_199] PRIMARY KEY CLUSTERED
(
	[Localdnsid] ASC
)WITH (PAD_INDEX = OFF, STATISTICS_NORECOMPUTE = OFF, IGNORE_DUP_KEY = OFF, ALLOW_ROW_LOCKS = ON, ALLOW_PAGE_LOCKS = ON) ON [PRIMARY]
) ON [PRIMARY]
GO
SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO
CREATE TABLE [LOG_288](
	[Id] [int] IDENTITY(1,1) NOT NULL,
	[Source] [int] NOT NULL,
	[Timestamp] [datetime] NOT NULL,
	[Description] [nvarchar](max) NOT NULL,
	[Details] [nvarchar](max) NULL,
	[Level] [int] NOT NULL,
 CONSTRAINT [PK_LOG_288] PRIMARY KEY CLUSTERED
(
	[Id] ASC
)WITH (PAD_INDEX = OFF, STATISTICS_NORECOMPUTE = OFF, IGNORE_DUP_KEY = OFF, ALLOW_ROW_LOCKS = ON, ALLOW_PAGE_LOCKS = ON) ON [PRIMARY]
) ON [PRIMARY] TEXTIMAGE_ON [PRIMARY]
GO
SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO
CREATE TABLE [MASSFAKTURA_207](
	[Fakturaindex] [int] NOT NULL,
	[Transid] [int] NOT NULL,
	[Anvid] [int] NOT NULL,
	[Tid] [datetime] NOT NULL,
	[Arendenr] [nvarchar](50) NULL
) ON [PRIMARY]
GO
SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO
CREATE TABLE [MONTHLY_VARIATION_330](
	[Type] [int] NOT NULL,
	[Rowid] [timestamp] NOT NULL,
 CONSTRAINT [PK_MONTHLY_VARIANTION_330] PRIMARY KEY CLUSTERED
(
	[Type] ASC
)WITH (PAD_INDEX = OFF, STATISTICS_NORECOMPUTE = OFF, IGNORE_DUP_KEY = OFF, ALLOW_ROW_LOCKS = ON, ALLOW_PAGE_LOCKS = ON) ON [PRIMARY]
) ON [PRIMARY]
GO
SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO
CREATE TABLE [MONTHLY_VARIATION_PERCENTAGE_331](
	[Type] [int] NOT NULL,
	[Month] [int] NOT NULL,
	[Value] [decimal](18, 3) NOT NULL,
	[Rowid] [timestamp] NOT NULL,
 CONSTRAINT [PK_MONTHLY_VARIATION_PERCENTAGE_331] PRIMARY KEY CLUSTERED
(
	[Type] ASC,
	[Month] ASC
)WITH (PAD_INDEX = OFF, STATISTICS_NORECOMPUTE = OFF, IGNORE_DUP_KEY = OFF, ALLOW_ROW_LOCKS = ON, ALLOW_PAGE_LOCKS = ON) ON [PRIMARY]
) ON [PRIMARY]
GO
SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO
CREATE TABLE [NAMN_PA_LAND_29](
	[Landkod] [nvarchar](2) NOT NULL,
	[Sprakkod] [nvarchar](1) NOT NULL,
	[Landnamn] [nvarchar](50) NULL,
	[Rowid] [timestamp] NOT NULL
) ON [PRIMARY]
GO
SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO
CREATE TABLE [NUMMERSERIER_57](
	[Nrtyp] [nvarchar](1) NOT NULL,
	[Nrserie] [int] NULL,
	[Rowid] [timestamp] NOT NULL,
	[Format] [nvarchar](255) NULL
) ON [PRIMARY]
GO
SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO
CREATE TABLE [NUMMERSERIER_ARENDEKOD_MAP_232](
	[Arendekod] [nvarchar](6) NOT NULL,
	[Map] [nvarchar](10) NOT NULL,
	[Rowid] [timestamp] NOT NULL,
 CONSTRAINT [PK_NUMMERSERIER_ARENDEKOD_MAP_232] PRIMARY KEY CLUSTERED
(
	[Arendekod] ASC
)WITH (PAD_INDEX = OFF, STATISTICS_NORECOMPUTE = OFF, IGNORE_DUP_KEY = OFF, ALLOW_ROW_LOCKS = ON, ALLOW_PAGE_LOCKS = ON) ON [PRIMARY]
) ON [PRIMARY]
GO
SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO
CREATE TABLE [NUMMERSERIER_ARENDETYP_MAP_236](
	[Arendetyp] [nvarchar](1) NOT NULL,
	[Map] [nvarchar](10) NOT NULL,
	[Rowid] [timestamp] NOT NULL,
 CONSTRAINT [PK_NUMMERSERIER_ARENDETYP_MAP_236] PRIMARY KEY CLUSTERED
(
	[Arendetyp] ASC
)WITH (PAD_INDEX = OFF, STATISTICS_NORECOMPUTE = OFF, IGNORE_DUP_KEY = OFF, ALLOW_ROW_LOCKS = ON, ALLOW_PAGE_LOCKS = ON) ON [PRIMARY]
) ON [PRIMARY]
GO
SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO
CREATE TABLE [OFFICE_194](
	[Officeid] [int] IDENTITY(1,1) NOT NULL,
	[Companyid] [int] NOT NULL,
	[Code] [nvarchar](5) NOT NULL,
	[Foretagsnamn] [nvarchar](100) NOT NULL,
	[Adress] [nvarchar](50) NULL,
	[Postnr] [nvarchar](10) NULL,
	[Postadress] [nvarchar](50) NULL,
	[Telefonnr] [nvarchar](50) NULL,
	[Faxnr] [nvarchar](50) NULL,
	[Email] [nvarchar](100) NULL,
	[Orgnr] [nvarchar](20) NULL,
	[Rowid] [timestamp] NOT NULL,
	[Rowguid] [uniqueidentifier] NOT NULL,
	[Exportannuity] [bit] NOT NULL,
	[Exportrenewaltrademark] [bit] NOT NULL,
	[Exportrenewaldesign] [bit] NOT NULL,
	[Fakturanrtyp] [nvarchar](1) NULL
) ON [PRIMARY]
GO
SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO
CREATE TABLE [OMBUD_LAND_328](
	[Id] [int] IDENTITY(1,1) NOT NULL,
	[Kundnr] [nvarchar](7) NOT NULL,
	[Landkod] [nvarchar](2) NOT NULL,
 CONSTRAINT [PK_OMBUD_LAND_328] PRIMARY KEY CLUSTERED
(
	[Id] ASC
)WITH (PAD_INDEX = OFF, STATISTICS_NORECOMPUTE = OFF, IGNORE_DUP_KEY = OFF, ALLOW_ROW_LOCKS = ON, ALLOW_PAGE_LOCKS = ON) ON [PRIMARY]
) ON [PRIMARY]
GO
SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO
CREATE TABLE [PARAMETER_149](
	[Sektion] [nvarchar](50) NOT NULL,
	[Parameter] [nvarchar](100) NOT NULL,
	[Varde] [nvarchar](4000) NULL,
	[Beskrivning] [nvarchar](512) NULL,
	[Rowid] [timestamp] NOT NULL,
	[Notes] [nvarchar](max) NULL,
	[Id] [int] IDENTITY(1,1) NOT NULL,
	[Krypterad] [bit] NOT NULL,
 CONSTRAINT [PK_PARAMETER_149] PRIMARY KEY CLUSTERED
(
	[Id] ASC
)WITH (PAD_INDEX = OFF, STATISTICS_NORECOMPUTE = OFF, IGNORE_DUP_KEY = OFF, ALLOW_ROW_LOCKS = ON, ALLOW_PAGE_LOCKS = ON) ON [PRIMARY]
) ON [PRIMARY] TEXTIMAGE_ON [PRIMARY]
GO
SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO
CREATE TABLE [PART_OFFICE_195](
	[Part_office_id] [int] IDENTITY(1,1) NOT NULL,
	[Officeid] [int] NOT NULL,
	[Parttype] [nvarchar](1) NOT NULL,
	[Partno] [nvarchar](7) NOT NULL,
	[Rowid] [timestamp] NOT NULL
) ON [PRIMARY]
GO
SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO
CREATE TABLE [PART2_ARENDE_140](
	[Arendenr] [nvarchar](50) NOT NULL,
	[Part] [smallint] NOT NULL,
	[Titel] [nvarchar](500) NULL,
	[Slagord] [nvarchar](250) NULL,
	[Inlamndag] [datetime] NULL,
	[Ansokningsnr] [nvarchar](50) NULL,
	[Publiceringsdag] [datetime] NULL,
	[Landkod] [nvarchar](2) NULL,
	[Beviljad] [datetime] NULL,
	[Patentnr] [nvarchar](50) NULL,
	[Part2] [nvarchar](3) NULL,
	[Publiceringsnr] [nvarchar](50) NULL,
	[Figmark] [nvarchar](1) NULL,
	[Klasser] [nvarchar](254) NULL,
	[Antalsamreg] [smallint] NULL,
	[Rowid] [timestamp] NOT NULL,
	[Slutdag] [datetime] NULL,
	[Internatinlamndag] [datetime] NULL,
	[Offentligdag] [datetime] NULL,
	[Logotyp] [nvarchar](1) NULL,
	[Slagordstrip] [nvarchar](250) NULL,
	[TrademarkType_Id] [int] NULL
) ON [PRIMARY]
GO
SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO
CREATE TABLE [PENDING_TIME_335](
	[Id] [int] IDENTITY(1,1) NOT NULL,
	[User_Id] [int] NOT NULL,
	[Arendenr] [nvarchar](50) NULL,
	[Kundnr] [nvarchar](7) NOT NULL,
	[StartTimeUtc] [datetime2](7) NOT NULL,
	[Minutes] [int] NOT NULL,
	[Fakturatextnr] [smallint] NOT NULL,
	[Text] [nvarchar](max) NULL,
	[Imported] [datetime2](7) NULL,
	[ImportStatus] [int] NOT NULL,
	[Created] [datetime2](7) NOT NULL,
 CONSTRAINT [PK_PENDING_TIME_335] PRIMARY KEY CLUSTERED
(
	[Id] ASC
)WITH (PAD_INDEX = OFF, STATISTICS_NORECOMPUTE = OFF, IGNORE_DUP_KEY = OFF, ALLOW_ROW_LOCKS = ON, ALLOW_PAGE_LOCKS = ON) ON [PRIMARY]
) ON [PRIMARY] TEXTIMAGE_ON [PRIMARY]
GO
SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO
CREATE TABLE [PRINTOUTS_170](
	[Anvid] [int] NOT NULL,
	[Printid] [nvarchar](50) NOT NULL,
	[Paramindex] [int] NOT NULL,
	[Paramvalue] [nvarchar](50) NULL,
	[Datum] [datetime] NOT NULL
) ON [PRIMARY]
GO
SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO
CREATE TABLE [PRIOFRAN_LAND_48](
	[Arendenr] [nvarchar](50) NOT NULL,
	[Landkod] [nvarchar](2) NOT NULL,
	[Prionr] [nvarchar](50) NOT NULL,
	[Priofran_datum] [datetime] NULL,
	[Rowid] [timestamp] NOT NULL,
	[Designnr] [nvarchar](2) NULL,
	[Id] [int] IDENTITY(1,1) NOT NULL,
 CONSTRAINT [PK_PRIOFRAN_LAND_48] PRIMARY KEY NONCLUSTERED
(
	[Id] ASC
)WITH (PAD_INDEX = OFF, STATISTICS_NORECOMPUTE = OFF, IGNORE_DUP_KEY = OFF, ALLOW_ROW_LOCKS = ON, ALLOW_PAGE_LOCKS = ON) ON [PRIMARY]
) ON [PRIMARY]
GO
SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO
CREATE TABLE [PRISL_REGARV_101](
	[Nr] [int] NOT NULL,
	[Vm] [nvarchar](1) NOT NULL,
	[Giltdat] [datetime] NOT NULL,
	[Rowid] [timestamp] NOT NULL,
	[Id] [int] IDENTITY(1,1) NOT NULL,
 CONSTRAINT [PK_PRISL_REGARV_101] PRIMARY KEY NONCLUSTERED
(
	[Id] ASC
)WITH (PAD_INDEX = OFF, STATISTICS_NORECOMPUTE = OFF, IGNORE_DUP_KEY = OFF, ALLOW_ROW_LOCKS = ON, ALLOW_PAGE_LOCKS = ON) ON [PRIMARY]
) ON [PRIMARY]
GO
SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO
CREATE TABLE [PRISL_REGPRIS_102](
	[Nr] [int] NOT NULL,
	[Vm] [nvarchar](1) NOT NULL,
	[Giltdat] [datetime] NULL,
	[Rowid] [timestamp] NOT NULL,
	[Id] [int] IDENTITY(1,1) NOT NULL,
 CONSTRAINT [PK_PRISL_REGPRIS_102] PRIMARY KEY NONCLUSTERED
(
	[Id] ASC
)WITH (PAD_INDEX = OFF, STATISTICS_NORECOMPUTE = OFF, IGNORE_DUP_KEY = OFF, ALLOW_ROW_LOCKS = ON, ALLOW_PAGE_LOCKS = ON) ON [PRIMARY]
) ON [PRIMARY]
GO
SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO
CREATE TABLE [PRISL_VARTARV_32](
	[Prislistanrvarvod] [int] NOT NULL,
	[Plistavarvfrdat] [datetime] NULL,
	[Plistavarvregdat] [datetime] NULL,
	[Rowid] [timestamp] NOT NULL
) ON [PRIMARY]
GO
SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO
CREATE TABLE [PRISLISTA_30](
	[Prislistanr] [smallint] NOT NULL,
	[Prislistafrandat] [datetime] NULL,
	[Prislistaregdat] [datetime] NULL,
	[Rowid] [timestamp] NOT NULL
) ON [PRIMARY]
GO
SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO
CREATE TABLE [PRISLISTERAD_31](
	[Prislistanr] [smallint] NOT NULL,
	[Arsavgregel] [nvarchar](10) NULL,
	[Avgiftskod] [smallint] NOT NULL,
	[Landkod] [nvarchar](2) NOT NULL,
	[Prisoffavg] [int] NULL,
	[Prisomb] [int] NULL,
	[Prisoffavgstr] [int] NULL,
	[Prisombstr] [int] NULL,
	[Rowid] [timestamp] NOT NULL,
	[Offvalutakod] [nvarchar](3) NULL,
	[Ombvalutakod] [nvarchar](3) NULL
) ON [PRIMARY]
GO
SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO
CREATE TABLE [PRISLR_VARTARV_33](
	[Prislistanrvarvod] [int] NOT NULL,
	[Avgiftskod] [smallint] NOT NULL,
	[Prisvartarvode] [int] NULL,
	[Prisvartarvstraff] [int] NULL,
	[Prisvartarvutl] [int] NULL,
	[Prisvartarvstrutl] [int] NULL,
	[Rowid] [timestamp] NOT NULL
) ON [PRIMARY]
GO
SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO
CREATE TABLE [PRISLR_VARTARV_LAND_234](
	[Landkod] [nvarchar](2) NOT NULL,
	[Pris] [int] NOT NULL,
	[Straff] [int] NULL,
	[Valutakod] [nvarchar](3) NOT NULL,
	[Pris2] [int] NULL,
	[Straff2] [nchar](10) NULL,
	[Valutakod2] [nvarchar](3) NULL,
	[Rowid] [timestamp] NOT NULL,
 CONSTRAINT [PK_PRISLR_VARTARV_LAND_234] PRIMARY KEY CLUSTERED
(
	[Landkod] ASC
)WITH (PAD_INDEX = OFF, STATISTICS_NORECOMPUTE = OFF, IGNORE_DUP_KEY = OFF, ALLOW_ROW_LOCKS = ON, ALLOW_PAGE_LOCKS = ON) ON [PRIMARY]
) ON [PRIMARY]
GO
SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO
CREATE TABLE [PRODUCTCATEGORY_191](
	[Productcategoryid] [int] IDENTITY(1,1) NOT NULL,
	[Kundnr] [nvarchar](7) NOT NULL,
	[Productcode] [nvarchar](5) NOT NULL,
	[Klasskod] [nvarchar](1) NULL,
	[Parentid] [int] NULL,
	[Fritext] [ntext] NULL,
 CONSTRAINT [PK_PRODUCTCATEGORY_191] PRIMARY KEY CLUSTERED
(
	[Productcategoryid] ASC
)WITH (PAD_INDEX = OFF, STATISTICS_NORECOMPUTE = OFF, IGNORE_DUP_KEY = OFF, ALLOW_ROW_LOCKS = ON, ALLOW_PAGE_LOCKS = ON) ON [PRIMARY]
) ON [PRIMARY] TEXTIMAGE_ON [PRIMARY]
GO
SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO
CREATE TABLE [PRODUCTCATEGORYNAME_192](
	[Productcategorynameid] [int] IDENTITY(1,1) NOT NULL,
	[Productcategoryid] [int] NOT NULL,
	[Sprakkod] [nvarchar](1) NOT NULL,
	[Categoryname] [nvarchar](50) NOT NULL,
 CONSTRAINT [PK_PRODUCTCATEGORYNAME_192] PRIMARY KEY CLUSTERED
(
	[Productcategorynameid] ASC
)WITH (PAD_INDEX = OFF, STATISTICS_NORECOMPUTE = OFF, IGNORE_DUP_KEY = OFF, ALLOW_ROW_LOCKS = ON, ALLOW_PAGE_LOCKS = ON) ON [PRIMARY]
) ON [PRIMARY]
GO
SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO
CREATE TABLE [PROJEKTKODER_135](
	[Arendekod] [nvarchar](6) NULL,
	[Landkod] [nvarchar](2) NULL,
	[Inhemskjn] [nvarchar](1) NOT NULL,
	[Projektkod] [nvarchar](3) NOT NULL,
	[Rowid] [timestamp] NOT NULL
) ON [PRIMARY]
GO
SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO
CREATE TABLE [RABATT_UTLAGG_49](
	[Arendetyp] [nvarchar](1) NOT NULL,
	[Kundnr] [nvarchar](7) NOT NULL,
	[Rabattpatyputl] [smallint] NULL,
	[Rowid] [timestamp] NOT NULL
) ON [PRIMARY]
GO
SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO
CREATE TABLE [RECENT_ARENDE_QUERY_253](
	[Id] [int] NOT NULL,
	[Rowid] [timestamp] NOT NULL,
 CONSTRAINT [PK_RECENT_ARENDE_QUERY_253] PRIMARY KEY CLUSTERED
(
	[Id] ASC
)WITH (PAD_INDEX = OFF, STATISTICS_NORECOMPUTE = OFF, IGNORE_DUP_KEY = OFF, ALLOW_ROW_LOCKS = ON, ALLOW_PAGE_LOCKS = ON) ON [PRIMARY]
) ON [PRIMARY]
GO
SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO
CREATE TABLE [REGARVODE_98](
	[Straffjn] [nvarchar](1) NOT NULL,
	[Regarvdat] [datetime] NULL,
	[Arvodegrund] [int] NULL,
	[Arvodeklass] [int] NULL,
	[Arvodesam] [int] NULL,
	[Utlarvodegrund] [int] NULL,
	[Utlarvodeklass] [int] NULL,
	[Utlarvodesam] [int] NULL,
	[Period] [int] NOT NULL,
	[Klassoveri] [int] NULL,
	[Arvodeover] [int] NULL,
	[Klassoveru] [int] NULL,
	[Utlarvover] [int] NULL,
	[Rowid] [timestamp] NOT NULL,
	[Id] [int] IDENTITY(1,1) NOT NULL,
	[Prisl_Regarv_101_Id] [int] NOT NULL,
 CONSTRAINT [PK_REGARVODE_98] PRIMARY KEY NONCLUSTERED
(
	[Id] ASC
)WITH (PAD_INDEX = OFF, STATISTICS_NORECOMPUTE = OFF, IGNORE_DUP_KEY = OFF, ALLOW_ROW_LOCKS = ON, ALLOW_PAGE_LOCKS = ON) ON [PRIMARY]
) ON [PRIMARY]
GO
SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO
CREATE TABLE [REGFRIST_94](
	[Arendenr] [nvarchar](50) NOT NULL,
	[Regel] [nvarchar](10) NOT NULL,
	[Vm] [nvarchar](1) NOT NULL,
	[Frist] [datetime] NOT NULL,
	[Friststraff] [datetime] NOT NULL,
	[Period] [smallint] NULL,
	[Lopno] [smallint] NULL,
	[Rowid] [timestamp] NOT NULL,
	[Id] [int] IDENTITY(1,1) NOT NULL,
 CONSTRAINT [PK_REGFRIST_94] PRIMARY KEY CLUSTERED
(
	[Id] ASC
)WITH (PAD_INDEX = OFF, STATISTICS_NORECOMPUTE = OFF, IGNORE_DUP_KEY = OFF, ALLOW_ROW_LOCKS = ON, ALLOW_PAGE_LOCKS = ON) ON [PRIMARY]
) ON [PRIMARY]
GO
SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO
CREATE TABLE [REGFRIST_BEHORIG_296](
	[Id] [int] IDENTITY(1,1) NOT NULL,
	[Frist_Id] [int] NOT NULL,
	[User_Id] [int] NOT NULL,
	[Flag] [int] NOT NULL,
	[Rowid] [timestamp] NOT NULL,
	[Remark] [nvarchar](500) NULL,
 CONSTRAINT [PK_REGFRIST_BEHORIG_296] PRIMARY KEY CLUSTERED
(
	[Id] ASC
)WITH (PAD_INDEX = OFF, STATISTICS_NORECOMPUTE = OFF, IGNORE_DUP_KEY = OFF, ALLOW_ROW_LOCKS = ON, ALLOW_PAGE_LOCKS = ON) ON [PRIMARY]
) ON [PRIMARY]
GO
SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO
CREATE TABLE [REGMODUL_95](
	[Landkod] [nvarchar](2) NOT NULL,
	[Regel] [nvarchar](10) NOT NULL,
	[Vm] [nvarchar](1) NOT NULL,
	[Firstdue] [smallint] NULL,
	[Firststraff] [smallint] NULL,
	[Intervall] [smallint] NULL,
	[Intervallstraff] [smallint] NULL,
	[Friststart] [nvarchar](1) NULL,
	[Antalperioder] [smallint] NULL,
	[Arsavgift] [nvarchar](1) NULL,
	[Rowid] [timestamp] NOT NULL
) ON [PRIMARY]
GO
SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO
CREATE TABLE [REGPRIS_96](
	[Landkod] [nvarchar](2) NOT NULL,
	[Regel] [nvarchar](10) NOT NULL,
	[Straffjn] [nvarchar](1) NOT NULL,
	[Regdat] [datetime] NULL,
	[Valutakod] [nvarchar](3) NULL,
	[Grundoff] [int] NULL,
	[Ombarvode] [int] NULL,
	[Klassoff] [int] NULL,
	[Ombklass] [int] NULL,
	[Samreg] [int] NULL,
	[Period] [int] NOT NULL,
	[Ombsamreg] [int] NULL,
	[Antal] [int] NULL,
	[Landarvgrund] [int] NULL,
	[Landarvklass] [int] NULL,
	[Landarvsam] [int] NULL,
	[Rowid] [timestamp] NOT NULL,
	[Pristext] [ntext] NULL,
	[Offvalutakod] [nvarchar](3) NULL,
	[Ombvalutakod] [nvarchar](3) NULL,
	[Landarvvalutakod] [nvarchar](3) NULL,
	[Landarvgrund2] [int] NULL,
	[Landarvklass2] [int] NULL,
	[Landarvsam2] [int] NULL,
	[Landarvvalutakod2] [nvarchar](3) NULL,
	[Prisl_regpris_102_Id] [int] NOT NULL,
	[Id] [int] IDENTITY(1,1) NOT NULL,
	[Klassoff2] [int] NOT NULL,
	[Klassoff2From] [int] NULL,
	[Ombklass2] [int] NOT NULL,
	[Ombklass2From] [int] NULL,
 CONSTRAINT [PK_REGPRIS_96] PRIMARY KEY NONCLUSTERED
(
	[Id] ASC
)WITH (PAD_INDEX = OFF, STATISTICS_NORECOMPUTE = OFF, IGNORE_DUP_KEY = OFF, ALLOW_ROW_LOCKS = ON, ALLOW_PAGE_LOCKS = ON) ON [PRIMARY]
) ON [PRIMARY] TEXTIMAGE_ON [PRIMARY]
GO
SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO
CREATE TABLE [REGREGEL_93](
	[Regel] [nvarchar](10) NOT NULL,
	[Vm] [nvarchar](1) NOT NULL,
	[Regelnamn] [nvarchar](50) NULL,
	[Rowid] [timestamp] NOT NULL,
	[Sprakkod] [nvarchar](1) NOT NULL
) ON [PRIMARY]
GO
SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO
CREATE TABLE [REPORTDEFINITION_156](
	[Id] [int] IDENTITY(1,1) NOT NULL,
	[Rapportnamn] [nvarchar](100) NULL,
	[Rapportklass] [nvarchar](5) NOT NULL,
	[Sortkoder] [nvarchar](500) NULL,
	[Beskrivning] [nvarchar](max) NULL,
	[Rowid] [timestamp] NOT NULL,
 CONSTRAINT [PK_REPORTDEFINITION_156] PRIMARY KEY CLUSTERED
(
	[Id] ASC
)WITH (PAD_INDEX = OFF, STATISTICS_NORECOMPUTE = OFF, IGNORE_DUP_KEY = OFF, ALLOW_ROW_LOCKS = ON, ALLOW_PAGE_LOCKS = ON) ON [PRIMARY]
) ON [PRIMARY] TEXTIMAGE_ON [PRIMARY]
GO
SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO
CREATE TABLE [REPORTTEMPLATE_157](
	[Id] [int] IDENTITY(1,1) NOT NULL,
	[ReportDefinition_Id] [int] NOT NULL,
	[Sprakkod] [nvarchar](1) NOT NULL,
	[Rapportfil] [nvarchar](254) NOT NULL,
	[Rowid] [timestamp] NOT NULL,
 CONSTRAINT [PK_REPORTTEMPLATE_157] PRIMARY KEY CLUSTERED
(
	[Id] ASC
)WITH (PAD_INDEX = OFF, STATISTICS_NORECOMPUTE = OFF, IGNORE_DUP_KEY = OFF, ALLOW_ROW_LOCKS = ON, ALLOW_PAGE_LOCKS = ON) ON [PRIMARY]
) ON [PRIMARY]
GO
SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO
CREATE TABLE [RESULTAT_138](
	[Kod] [nvarchar](1) NOT NULL,
	[Namn] [nvarchar](40) NULL,
	[Rowid] [timestamp] NOT NULL
) ON [PRIMARY]
GO
SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO
CREATE TABLE [SAMAVISERING_36](
	[Arendetyp] [nvarchar](1) NOT NULL,
	[Kundnr] [nvarchar](7) NOT NULL,
	[Rowid] [timestamp] NOT NULL
) ON [PRIMARY]
GO
SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO
CREATE TABLE [SAMFAKTURERING_37](
	[Arendetyp] [nvarchar](1) NOT NULL,
	[Kundnr] [nvarchar](7) NOT NULL,
	[Rowid] [timestamp] NOT NULL
) ON [PRIMARY]
GO
SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO
CREATE TABLE [SAVED_ARENDE_QUERY_252](
	[Id] [int] IDENTITY(1,1) NOT NULL,
	[Anvid] [int] NOT NULL,
	[Namn] [nvarchar](100) NULL,
	[Arendetypklass_Patent] [bit] NULL,
	[Arendetypklass_Varumarke] [bit] NULL,
	[Arendetypklass_Design] [bit] NULL,
	[Arendetypklass_Domannamn] [bit] NULL,
	[Arendenr] [nvarchar](50) NULL,
	[Grundarende] [nvarchar](50) NULL,
	[Bevakomr] [nvarchar](1) NULL,
	[Titel] [nvarchar](100) NULL,
	[Kundkoncern] [nvarchar](7) NULL,
	[Kundkategori] [nvarchar](1) NULL,
	[Handlaggarid] [int] NULL,
	[Fhandlagg] [int] NULL,
	[Assistent] [int] NULL,
	[Dns] [nvarchar](100) NULL,
	[Diarietext] [nvarchar](100) NULL,
	[Varor] [nvarchar](100) NULL,
	[Fritext] [nvarchar](100) NULL,
	[Officeid] [int] NULL,
	[Klass_Exkludera] [bit] NULL,
	[Klass_Text] [nvarchar](100) NULL,
	[Kund1_ParameterType] [int] NULL,
	[Kund1_Wildcardtext] [nvarchar](50) NULL,
	[Kund1_Nummer] [nvarchar](7) NULL,
	[Kund1_Typ] [int] NULL,
	[Kund2_ParameterType] [int] NULL,
	[Kund2_Wildcardtext] [nvarchar](50) NULL,
	[Kund2_Typ] [int] NULL,
	[Kund2_Nummer] [nvarchar](7) NULL,
	[Sokande_ParameterType] [int] NULL,
	[Sokande_Wildcardtext] [nvarchar](50) NULL,
	[Sokande_Nummer] [nvarchar](7) NULL,
	[FgSokande_ParameterType] [int] NULL,
	[FgSokande_Wildcardtext] [nvarchar](50) NULL,
	[FgSokande_Nummer] [nvarchar](7) NULL,
	[Sokandekoncern_ParameterType] [int] NULL,
	[Sokandekoncern_Wildcardtext] [nvarchar](50) NULL,
	[Sokandekoncern_Nummer] [nvarchar](7) NULL,
	[Nummer_Ansokningsnr] [nvarchar](100) NULL,
	[Nummer_Patentnr] [nvarchar](100) NULL,
	[Nummer_Internatansregnr] [nvarchar](100) NULL,
	[Nummer_Prionr] [nvarchar](100) NULL,
	[Nummer_Publnr] [nvarchar](100) NULL,
	[Extrainfo_Typ] [nvarchar](20) NULL,
	[Extrainfo_Text] [nvarchar](100) NULL,
	[Beslut_Instans] [nvarchar](1) NULL,
	[Beslut_Malnr] [nvarchar](15) NULL,
	[Kundland_Typ] [int] NULL,
	[Kundland_Landkod] [nvarchar](2) NULL,
	[Attention_Typ] [int] NULL,
	[Attention_Text] [nvarchar](50) NULL,
	[Referens_Typ] [int] NULL,
	[Referens_Text] [nvarchar](250) NULL,
	[Produktkategori_Id] [int] NULL,
	[Produktkategori_Rekursiv] [bit] NULL,
	[Uppfinnare_ParameterType] [int] NULL,
	[Uppfinnare_Wildcardtext] [nvarchar](50) NULL,
	[Uppfinnare_Nummer] [nvarchar](7) NULL,
	[Slagord_Exkludera] [bit] NULL,
	[Slagord_Text] [nvarchar](100) NULL,
	[Alternativ_InklFalla] [bit] NULL,
	[Alternativ_InklArkiverade] [bit] NULL,
	[Alternativ_EndastMotparter] [bit] NULL,
	[Datum_Typ] [nvarchar](3) NULL,
	[Datum_Start] [datetime] NULL,
	[Datum_Slut] [datetime] NULL,
	[Frist_Typ] [nvarchar](6) NULL,
	[Frist_Start] [datetime] NULL,
	[Frist_Slut] [datetime] NULL,
	[Fornyelsefrist_Typ] [nvarchar](10) NULL,
	[Fornyelsefrist_Start] [datetime] NULL,
	[Fornyelsefrist_Slut] [datetime] NULL,
	[Cpafrist_Typ] [nvarchar](50) NULL,
	[Cpafrist_Start] [datetime] NULL,
	[Cpafrist_Slut] [datetime] NULL,
	[Skapad] [datetime] NOT NULL,
	[Rowid] [timestamp] NOT NULL,
	[RenewalsManagement] [int] NULL,
	[Arsavgiftsfrist_Typ] [nvarchar](50) NULL,
	[Arsavgiftsfrist_Start] [datetime] NULL,
	[Arsavgiftsfrist_Slut] [datetime] NULL,
	[Datum_ParameterType] [int] NULL,
	[Frist_ParameterType] [int] NULL,
	[Fornyelsefrist_ParameterType] [int] NULL,
	[Cpafrist_ParameterType] [int] NULL,
	[Arsavgiftsfrist_ParameterType] [int] NULL,
	[FileInstruct] [bit] NULL,
 CONSTRAINT [PK_SAVED_ARENDE_QUERY_252] PRIMARY KEY CLUSTERED
(
	[Id] ASC
)WITH (PAD_INDEX = OFF, STATISTICS_NORECOMPUTE = OFF, IGNORE_DUP_KEY = OFF, ALLOW_ROW_LOCKS = ON, ALLOW_PAGE_LOCKS = ON) ON [PRIMARY]
) ON [PRIMARY]
GO
SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO
CREATE TABLE [SAVED_ARENDE_QUERY_CASECODE_302](
	[Id] [int] IDENTITY(1,1) NOT NULL,
	[SavedArendeQueryId] [int] NOT NULL,
	[Arendekod] [nvarchar](6) NULL,
	[Rowid] [timestamp] NOT NULL,
 CONSTRAINT [PK_SAVED_ARENDE_QUERY_CASECODE_302] PRIMARY KEY CLUSTERED
(
	[Id] ASC
)WITH (PAD_INDEX = OFF, STATISTICS_NORECOMPUTE = OFF, IGNORE_DUP_KEY = OFF, ALLOW_ROW_LOCKS = ON, ALLOW_PAGE_LOCKS = ON) ON [PRIMARY]
) ON [PRIMARY]
GO
SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO
CREATE TABLE [SAVED_ARENDE_QUERY_CASEPHASE_303](
	[Id] [int] IDENTITY(1,1) NOT NULL,
	[SavedArendeQueryId] [int] NOT NULL,
	[Fasid] [nvarchar](6) NULL,
	[Rowid] [timestamp] NOT NULL,
 CONSTRAINT [PK_SAVED_ARENDE_QUERY_CASEPHASE_303] PRIMARY KEY CLUSTERED
(
	[Id] ASC
)WITH (PAD_INDEX = OFF, STATISTICS_NORECOMPUTE = OFF, IGNORE_DUP_KEY = OFF, ALLOW_ROW_LOCKS = ON, ALLOW_PAGE_LOCKS = ON) ON [PRIMARY]
) ON [PRIMARY]
GO
SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO
CREATE TABLE [SAVED_ARENDE_QUERY_CASETYPE_258](
	[Id] [int] IDENTITY(1,1) NOT NULL,
	[SavedArendeQueryId] [int] NOT NULL,
	[Arendetyp] [nvarchar](20) NOT NULL,
	[Rowid] [timestamp] NOT NULL,
 CONSTRAINT [PK_SAVED_ARENDE_QUERY_CASETYPE_258] PRIMARY KEY CLUSTERED
(
	[Id] ASC
)WITH (PAD_INDEX = OFF, STATISTICS_NORECOMPUTE = OFF, IGNORE_DUP_KEY = OFF, ALLOW_ROW_LOCKS = ON, ALLOW_PAGE_LOCKS = ON) ON [PRIMARY]
) ON [PRIMARY]
GO
SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO
CREATE TABLE [SAVED_ARENDE_QUERY_CASETYPE_VARIOUS_259](
	[Id] [int] IDENTITY(1,1) NOT NULL,
	[SavedArendeQueryId] [int] NOT NULL,
	[Arendetyp] [nvarchar](1) NOT NULL,
	[Rowid] [timestamp] NOT NULL,
 CONSTRAINT [PK_SAVED_ARENDE_QUERY_CASETYPE_VARIOUS_259] PRIMARY KEY CLUSTERED
(
	[Id] ASC
)WITH (PAD_INDEX = OFF, STATISTICS_NORECOMPUTE = OFF, IGNORE_DUP_KEY = OFF, ALLOW_ROW_LOCKS = ON, ALLOW_PAGE_LOCKS = ON) ON [PRIMARY]
) ON [PRIMARY]
GO
SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO
CREATE TABLE [SAVED_ARENDE_QUERY_FILINGTYPE_300](
	[Id] [int] IDENTITY(1,1) NOT NULL,
	[SavedArendeQueryId] [int] NOT NULL,
	[Rowid] [timestamp] NOT NULL,
	[FilingType_Id] [int] NULL,
 CONSTRAINT [PK_SAVED_ARENDE_QUERY_FILINGTYPE_300] PRIMARY KEY CLUSTERED
(
	[Id] ASC
)WITH (PAD_INDEX = OFF, STATISTICS_NORECOMPUTE = OFF, IGNORE_DUP_KEY = OFF, ALLOW_ROW_LOCKS = ON, ALLOW_PAGE_LOCKS = ON) ON [PRIMARY]
) ON [PRIMARY]
GO
SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO
CREATE TABLE [SAVED_ARENDE_QUERY_INCOMEUNIT_304](
	[Id] [int] IDENTITY(1,1) NOT NULL,
	[SavedArendeQueryId] [int] NOT NULL,
	[Kod] [nvarchar](6) NULL,
	[Rowid] [timestamp] NOT NULL,
 CONSTRAINT [PK_SAVED_ARENDE_QUERY_INCOMEUNIT_304] PRIMARY KEY CLUSTERED
(
	[Id] ASC
)WITH (PAD_INDEX = OFF, STATISTICS_NORECOMPUTE = OFF, IGNORE_DUP_KEY = OFF, ALLOW_ROW_LOCKS = ON, ALLOW_PAGE_LOCKS = ON) ON [PRIMARY]
) ON [PRIMARY]
GO
SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO
CREATE TABLE [SAVED_ARENDE_QUERY_LAND_255](
	[Id] [int] IDENTITY(1,1) NOT NULL,
	[SavedArendeQueryId] [int] NOT NULL,
	[Landkod] [nvarchar](2) NOT NULL,
	[Rowid] [timestamp] NOT NULL,
 CONSTRAINT [PK_SAVED_ARENDE_QUERY_LAND_255] PRIMARY KEY CLUSTERED
(
	[Id] ASC
)WITH (PAD_INDEX = OFF, STATISTICS_NORECOMPUTE = OFF, IGNORE_DUP_KEY = OFF, ALLOW_ROW_LOCKS = ON, ALLOW_PAGE_LOCKS = ON) ON [PRIMARY]
) ON [PRIMARY]
GO
SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO
CREATE TABLE [SAVED_ARENDE_QUERY_LAND_PARAMS_254](
	[SavedArendeQueryId] [int] NOT NULL,
	[Exkludera] [bit] NOT NULL,
	[InklDesigneringar] [bit] NOT NULL,
	[Rowid] [timestamp] NOT NULL,
 CONSTRAINT [PK_SAVED_ARENDE_QUERY_LAND_PARAMS_254] PRIMARY KEY CLUSTERED
(
	[SavedArendeQueryId] ASC
)WITH (PAD_INDEX = OFF, STATISTICS_NORECOMPUTE = OFF, IGNORE_DUP_KEY = OFF, ALLOW_ROW_LOCKS = ON, ALLOW_PAGE_LOCKS = ON) ON [PRIMARY]
) ON [PRIMARY]
GO
SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO
CREATE TABLE [SAVED_ARENDE_QUERY_SERVICELEVEL_306](
	[Id] [int] IDENTITY(1,1) NOT NULL,
	[SavedArendeQueryId] [int] NOT NULL,
	[Kod] [nvarchar](1) NULL,
	[Rowid] [timestamp] NOT NULL,
 CONSTRAINT [PK_SAVED_ARENDE_QUERY_SERVICELEVEL_306] PRIMARY KEY CLUSTERED
(
	[Id] ASC
)WITH (PAD_INDEX = OFF, STATISTICS_NORECOMPUTE = OFF, IGNORE_DUP_KEY = OFF, ALLOW_ROW_LOCKS = ON, ALLOW_PAGE_LOCKS = ON) ON [PRIMARY]
) ON [PRIMARY]
GO
SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO
CREATE TABLE [SAVED_ARENDE_QUERY_STATUSCODE_305](
	[Id] [int] IDENTITY(1,1) NOT NULL,
	[SavedArendeQueryId] [int] NOT NULL,
	[Kod] [nvarchar](1) NULL,
	[Rowid] [timestamp] NOT NULL,
 CONSTRAINT [PK_SAVED_ARENDE_QUERY_STATUSCODE_305] PRIMARY KEY CLUSTERED
(
	[Id] ASC
)WITH (PAD_INDEX = OFF, STATISTICS_NORECOMPUTE = OFF, IGNORE_DUP_KEY = OFF, ALLOW_ROW_LOCKS = ON, ALLOW_PAGE_LOCKS = ON) ON [PRIMARY]
) ON [PRIMARY]
GO
SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO
CREATE TABLE [SAVED_ARENDE_QUERY_TOPDOMAIN_257](
	[Id] [int] IDENTITY(1,1) NOT NULL,
	[SavedArendeQueryId] [int] NOT NULL,
	[Topleveldomainid] [int] NOT NULL,
	[Rowid] [timestamp] NOT NULL,
 CONSTRAINT [PK_SAVED_ARENDE_QUERY_TOPDOMAIN_257] PRIMARY KEY CLUSTERED
(
	[Id] ASC
)WITH (PAD_INDEX = OFF, STATISTICS_NORECOMPUTE = OFF, IGNORE_DUP_KEY = OFF, ALLOW_ROW_LOCKS = ON, ALLOW_PAGE_LOCKS = ON) ON [PRIMARY]
) ON [PRIMARY]
GO
SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO
CREATE TABLE [SAVED_ARENDE_QUERY_TOPDOMAIN_PARAMS_256](
	[SavedArendeQueryId] [int] NOT NULL,
	[Exkludera] [bit] NOT NULL,
	[Rowid] [timestamp] NOT NULL,
 CONSTRAINT [PK_SAVED_ARENDE_QUERY_TOPDOMAIN_PARAMS_256] PRIMARY KEY CLUSTERED
(
	[SavedArendeQueryId] ASC
)WITH (PAD_INDEX = OFF, STATISTICS_NORECOMPUTE = OFF, IGNORE_DUP_KEY = OFF, ALLOW_ROW_LOCKS = ON, ALLOW_PAGE_LOCKS = ON) ON [PRIMARY]
) ON [PRIMARY]
GO
SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO
CREATE TABLE [SAVED_ARENDE_QUERY_TRADEMARKTYPE_299](
	[Id] [int] IDENTITY(1,1) NOT NULL,
	[SavedArendeQueryId] [int] NOT NULL,
	[TrademarkType_Id] [int] NULL,
	[Rowid] [timestamp] NOT NULL,
 CONSTRAINT [PK_SAVED_ARENDE_QUERY_TRADEMARKTYPE_299] PRIMARY KEY CLUSTERED
(
	[Id] ASC
)WITH (PAD_INDEX = OFF, STATISTICS_NORECOMPUTE = OFF, IGNORE_DUP_KEY = OFF, ALLOW_ROW_LOCKS = ON, ALLOW_PAGE_LOCKS = ON) ON [PRIMARY]
) ON [PRIMARY]
GO
SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO
CREATE TABLE [SEKTION_148](
	[Sektion] [nvarchar](50) NOT NULL,
	[Beskrivning] [nvarchar](254) NULL,
	[Rowid] [timestamp] NOT NULL
) ON [PRIMARY]
GO
SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO
CREATE TABLE [SERVICETYP_38](
	[Servicetyp] [nvarchar](1) NOT NULL,
	[Servicetypnamn] [nvarchar](12) NULL,
	[Servicetypsekvtyp] [smallint] NULL,
	[Rowid] [timestamp] NOT NULL,
	[Sprakkod] [nvarchar](1) NOT NULL
) ON [PRIMARY]
GO
SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO
CREATE TABLE [SOKANDE_39](
	[Sokandenr] [nvarchar](7) NOT NULL,
	[Servicetyp] [nvarchar](1) NULL,
	[Landkod] [nvarchar](2) NOT NULL,
	[Sokandadress1] [nvarchar](100) NULL,
	[Sokandadress2] [nvarchar](100) NULL,
	[Sokandadress3] [nvarchar](100) NULL,
	[Sokandadress4] [nvarchar](100) NULL,
	[Sokandadress5] [nvarchar](100) NULL,
	[Sokandeskortnamn] [nvarchar](50) NOT NULL,
	[Sokandeskortort] [nvarchar](15) NULL,
	[Sokandadress6] [nvarchar](100) NULL,
	[Sokandadress7] [nvarchar](100) NULL,
	[Hemort] [nvarchar](50) NULL,
	[Nation] [nvarchar](50) NULL,
	[Koncern] [nvarchar](7) NULL,
	[Fusion] [nvarchar](1) NULL,
	[Andratdat] [datetime] NULL,
	[Anv] [int] NULL,
	[Varsokande] [smallint] NULL,
	[Sprakkod] [nvarchar](1) NOT NULL,
	[Smallentityjn] [nvarchar](1) NULL,
	[Inaktivjn] [int] NULL,
	[Telefonnr] [nvarchar](50) NULL,
	[Rowid] [timestamp] NOT NULL,
	[Rowguid] [uniqueidentifier] NOT NULL,
	[Legalperson] [smallint] NOT NULL,
	[Orgnr] [nvarchar](20) NULL,
	[Fritext] [ntext] NULL,
	[Transferconfigurationsetid] [int] NULL,
	[Meansofcommunication] [int] NULL,
	[EmailAddresses_To] [nvarchar](max) NULL,
	[EmailAddresses_Cc] [nvarchar](max) NULL,
	[EmailAddresses_Bcc] [nvarchar](max) NULL,
 CONSTRAINT [PK_SOKANDE_39] PRIMARY KEY CLUSTERED
(
	[Sokandenr] ASC
)WITH (PAD_INDEX = OFF, STATISTICS_NORECOMPUTE = OFF, IGNORE_DUP_KEY = OFF, ALLOW_ROW_LOCKS = ON, ALLOW_PAGE_LOCKS = ON) ON [PRIMARY]
) ON [PRIMARY] TEXTIMAGE_ON [PRIMARY]
GO
SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO
CREATE TABLE [SOKANDE_ARENDE_78](
	[Sokandenr] [nvarchar](7) NOT NULL,
	[Arendenr] [nvarchar](50) NOT NULL,
	[Typ] [nvarchar](2) NOT NULL,
	[Part] [int] NOT NULL,
	[Rowid] [timestamp] NOT NULL,
	[Sortorder] [int] NULL,
	[Id] [int] IDENTITY(1,1) NOT NULL,
 CONSTRAINT [PK_SOKANDE_ARENDE_78] PRIMARY KEY CLUSTERED
(
	[Id] ASC
)WITH (PAD_INDEX = OFF, STATISTICS_NORECOMPUTE = OFF, IGNORE_DUP_KEY = OFF, ALLOW_ROW_LOCKS = ON, ALLOW_PAGE_LOCKS = ON) ON [PRIMARY]
) ON [PRIMARY]
GO
SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO
CREATE TABLE [SOKANDE_EXTRAINFO_286](
	[Id] [int] IDENTITY(1,1) NOT NULL,
	[Sokandenr] [nvarchar](7) NOT NULL,
	[Faltid] [nvarchar](20) NOT NULL,
	[Value] [nvarchar](max) NULL,
	[Rowid] [timestamp] NOT NULL,
 CONSTRAINT [PK_SOKANDE_EXTRAINFO_286] PRIMARY KEY CLUSTERED
(
	[Id] ASC
)WITH (PAD_INDEX = OFF, STATISTICS_NORECOMPUTE = OFF, IGNORE_DUP_KEY = OFF, ALLOW_ROW_LOCKS = ON, ALLOW_PAGE_LOCKS = ON) ON [PRIMARY]
) ON [PRIMARY] TEXTIMAGE_ON [PRIMARY]
GO
SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO
CREATE TABLE [SOKANDESORT_136](
	[Sokandenr] [nvarchar](7) NOT NULL,
	[Grupp] [nvarchar](3) NOT NULL,
	[Datum] [datetime] NULL,
	[Rowid] [timestamp] NOT NULL
) ON [PRIMARY]
GO
SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO
CREATE TABLE [SPRAK_40](
	[Sprakkod] [nvarchar](1) NOT NULL,
	[Spraknamn] [nvarchar](20) NULL,
	[Ekonomisprak] [nvarchar](3) NULL,
	[Datumformat] [nvarchar](50) NULL,
	[Manadsnamn] [nvarchar](254) NULL,
	[Rowid] [timestamp] NOT NULL,
	[Sendtoiprcontrol] [bit] NOT NULL
) ON [PRIMARY]
GO
SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO
CREATE TABLE [STATUSHIST_152](
	[Arendenr] [nvarchar](50) NOT NULL,
	[Tidstampel] [datetime] NOT NULL,
	[Anvid] [int] NULL,
	[Statuskod] [nvarchar](1) NULL,
	[Rowid] [timestamp] NOT NULL
) ON [PRIMARY]
GO
SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO
CREATE TABLE [STATUSKOD_41](
	[Statuskod] [nvarchar](1) NOT NULL,
	[Statuskodnamn] [nvarchar](12) NULL,
	[Rowid] [timestamp] NOT NULL,
	[Sprakkod] [nvarchar](1) NOT NULL
) ON [PRIMARY]
GO
SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO
CREATE TABLE [SYNC_COMMONCONFIGURATION_208](
	[Useimageuncpathreplacement] [bit] NOT NULL,
	[Imageuncpath] [nvarchar](255) NOT NULL,
	[Imageuncpathdriveletter] [nvarchar](2) NULL,
	[Includeclosedterms] [bit] NOT NULL,
	[Closedtermsnumberdaysold] [int] NOT NULL,
	[Deleteexternaldocuments] [bit] NULL,
	[Docusharedocumentpath] [nvarchar](200) NULL,
	[IsChanged] [bit] NOT NULL
) ON [PRIMARY]
GO
SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO
CREATE TABLE [SYNC_CONFIGURATIONSET_212](
	[Id] [int] IDENTITY(1,1) NOT NULL,
	[Name] [nvarchar](255) NOT NULL,
	[Includeinvoicedthisyear] [bit] NOT NULL,
	[Includeinvoicedtotal] [bit] NOT NULL,
	[Includedocuments] [bit] NOT NULL,
	[IsChanged] [bit] NOT NULL,
 CONSTRAINT [PK_SYNC_CONFIGURATIONSET_212] PRIMARY KEY CLUSTERED
(
	[Id] ASC
)WITH (PAD_INDEX = OFF, STATISTICS_NORECOMPUTE = OFF, IGNORE_DUP_KEY = OFF, ALLOW_ROW_LOCKS = ON, ALLOW_PAGE_LOCKS = ON) ON [PRIMARY]
) ON [PRIMARY]
GO
SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO
CREATE TABLE [SYNC_EXCHANGED_ARENDE_COST_266](
	[Id] [int] IDENTITY(1,1) NOT NULL,
	[ArendeCostIdentifier] [uniqueidentifier] NOT NULL,
	[TransferDate] [datetime] NOT NULL,
 CONSTRAINT [PK_SYNC_EXCHANGED_ARENDE_COST_266] PRIMARY KEY CLUSTERED
(
	[Id] ASC
)WITH (PAD_INDEX = OFF, STATISTICS_NORECOMPUTE = OFF, IGNORE_DUP_KEY = OFF, ALLOW_ROW_LOCKS = ON, ALLOW_PAGE_LOCKS = ON) ON [PRIMARY]
) ON [PRIMARY]
GO
SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO
CREATE TABLE [SYNC_EXCHANGED_CASE_222](
	[Id] [int] IDENTITY(1,1) NOT NULL,
	[Rowguid] [nvarchar](255) NOT NULL,
	[Transferred] [datetime] NOT NULL,
 CONSTRAINT [PK_SYNC_EXCHANGED_CASE_222] PRIMARY KEY CLUSTERED
(
	[Id] ASC
)WITH (PAD_INDEX = OFF, STATISTICS_NORECOMPUTE = OFF, IGNORE_DUP_KEY = OFF, ALLOW_ROW_LOCKS = ON, ALLOW_PAGE_LOCKS = ON) ON [PRIMARY]
) ON [PRIMARY]
GO
SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO
CREATE TABLE [SYNC_EXCHANGED_DOCUMENT_223](
	[Id] [int] IDENTITY(1,1) NOT NULL,
	[Documentid] [int] NOT NULL,
	[Changedate] [datetime] NOT NULL,
	[Rowguid] [nvarchar](50) NOT NULL,
	[Documentrowid] [varbinary](8) NULL,
 CONSTRAINT [PK_SYNC_EXCHANGED_DOCUMENT_223] PRIMARY KEY CLUSTERED
(
	[Id] ASC
)WITH (PAD_INDEX = OFF, STATISTICS_NORECOMPUTE = OFF, IGNORE_DUP_KEY = OFF, ALLOW_ROW_LOCKS = ON, ALLOW_PAGE_LOCKS = ON) ON [PRIMARY]
) ON [PRIMARY]
GO
SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO
CREATE TABLE [SYNC_EXCHANGED_IMAGE_224](
	[Id] [int] IDENTITY(1,1) NOT NULL,
	[Identifier] [nvarchar](50) NOT NULL,
	[Checksum] [binary](16) NOT NULL,
 CONSTRAINT [PK_SYNC_EXCHANGED_IMAGE_224] PRIMARY KEY CLUSTERED
(
	[Id] ASC
)WITH (PAD_INDEX = OFF, STATISTICS_NORECOMPUTE = OFF, IGNORE_DUP_KEY = OFF, ALLOW_ROW_LOCKS = ON, ALLOW_PAGE_LOCKS = ON) ON [PRIMARY]
) ON [PRIMARY]
GO
SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO
CREATE TABLE [SYNC_EXCHANGED_PART_225](
	[Id] [int] IDENTITY(1,1) NOT NULL,
	[Rowguid] [uniqueidentifier] NOT NULL,
	[Transferred] [datetime] NOT NULL,
 CONSTRAINT [PK_SYNC_EXCHANGED_PART] PRIMARY KEY CLUSTERED
(
	[Id] ASC
)WITH (PAD_INDEX = OFF, STATISTICS_NORECOMPUTE = OFF, IGNORE_DUP_KEY = OFF, ALLOW_ROW_LOCKS = ON, ALLOW_PAGE_LOCKS = ON) ON [PRIMARY]
) ON [PRIMARY]
GO
SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO
CREATE TABLE [SYNC_EXCHANGED_PRODUCT_CATEGORY_227](
	[Id] [int] IDENTITY(1,1) NOT NULL,
	[ProductCategoryId] [int] NULL,
	[Identifier] [nvarchar](255) NULL,
 CONSTRAINT [PK_SYNC_EXCHANGED_PRODUCT_CATEGORY_] PRIMARY KEY CLUSTERED
(
	[Id] ASC
)WITH (PAD_INDEX = OFF, STATISTICS_NORECOMPUTE = OFF, IGNORE_DUP_KEY = OFF, ALLOW_ROW_LOCKS = ON, ALLOW_PAGE_LOCKS = ON) ON [PRIMARY]
) ON [PRIMARY]
GO
SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO
CREATE TABLE [SYNC_EXCLUDEDAPPLICATIONTERM_213](
	[Id] [int] IDENTITY(1,1) NOT NULL,
	[Termcode] [nvarchar](50) NOT NULL,
	[Configurationsetid] [int] NOT NULL,
 CONSTRAINT [PK_SYNC_EXCLUDEDAPPLICATIONTERM_213] PRIMARY KEY CLUSTERED
(
	[Id] ASC
)WITH (PAD_INDEX = OFF, STATISTICS_NORECOMPUTE = OFF, IGNORE_DUP_KEY = OFF, ALLOW_ROW_LOCKS = ON, ALLOW_PAGE_LOCKS = ON) ON [PRIMARY]
) ON [PRIMARY]
GO
SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO
CREATE TABLE [SYNC_EXCLUDEDCASETYPE_239](
	[Id] [int] IDENTITY(1,1) NOT NULL,
	[Casetypecode] [nvarchar](1) NOT NULL,
	[Configurationsetid] [int] NOT NULL,
 CONSTRAINT [PK_SYNC_EXCLUDEDCASETYPE_239] PRIMARY KEY CLUSTERED
(
	[Id] ASC
)WITH (PAD_INDEX = OFF, STATISTICS_NORECOMPUTE = OFF, IGNORE_DUP_KEY = OFF, ALLOW_ROW_LOCKS = ON, ALLOW_PAGE_LOCKS = ON) ON [PRIMARY]
) ON [PRIMARY]
GO
SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO
CREATE TABLE [SYNC_EXCLUDEDPARTTYPE_215](
	[Id] [int] IDENTITY(1,1) NOT NULL,
	[Partcode] [nvarchar](50) NOT NULL,
	[Configurationsetid] [int] NOT NULL,
 CONSTRAINT [PK_SYNC_EXCLUDEDPARTTYPE_215] PRIMARY KEY CLUSTERED
(
	[Id] ASC
)WITH (PAD_INDEX = OFF, STATISTICS_NORECOMPUTE = OFF, IGNORE_DUP_KEY = OFF, ALLOW_ROW_LOCKS = ON, ALLOW_PAGE_LOCKS = ON) ON [PRIMARY]
) ON [PRIMARY]
GO
SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO
CREATE TABLE [SYNC_EXCLUDEDSTATUSCODE_216](
	[Id] [int] IDENTITY(1,1) NOT NULL,
	[Statuscode] [nvarchar](25) NOT NULL,
	[Configurationsetid] [int] NOT NULL,
 CONSTRAINT [PK_SYNC_EXCLUDESTATUSCODE_216] PRIMARY KEY CLUSTERED
(
	[Id] ASC
)WITH (PAD_INDEX = OFF, STATISTICS_NORECOMPUTE = OFF, IGNORE_DUP_KEY = OFF, ALLOW_ROW_LOCKS = ON, ALLOW_PAGE_LOCKS = ON) ON [PRIMARY]
) ON [PRIMARY]
GO
SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO
CREATE TABLE [SYNC_INCLUDEDEXTRAINFO_219](
	[Id] [int] IDENTITY(1,1) NOT NULL,
	[Objecttype] [nvarchar](50) NOT NULL,
	[Fieldid] [nvarchar](50) NOT NULL,
	[Configurationsetid] [int] NOT NULL,
 CONSTRAINT [PK_SYNC_INCLUDEDEXTRAINFO_219] PRIMARY KEY CLUSTERED
(
	[Id] ASC
)WITH (PAD_INDEX = OFF, STATISTICS_NORECOMPUTE = OFF, IGNORE_DUP_KEY = OFF, ALLOW_ROW_LOCKS = ON, ALLOW_PAGE_LOCKS = ON) ON [PRIMARY]
) ON [PRIMARY]
GO
SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO
CREATE TABLE [SYNC_LOG_220](
	[Id] [int] IDENTITY(1,1) NOT NULL,
	[Date] [datetime] NULL,
 CONSTRAINT [PK_SYNC_LOG_220] PRIMARY KEY CLUSTERED
(
	[Id] ASC
)WITH (PAD_INDEX = OFF, STATISTICS_NORECOMPUTE = OFF, IGNORE_DUP_KEY = OFF, ALLOW_ROW_LOCKS = ON, ALLOW_PAGE_LOCKS = ON) ON [PRIMARY]
) ON [PRIMARY]
GO
SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO
CREATE TABLE [SYNC_LOG_DESCRIPTION_221](
	[Id] [int] IDENTITY(1,1) NOT NULL,
	[Timestamp] [datetime] NOT NULL,
	[Level] [int] NOT NULL,
	[Logid] [int] NOT NULL,
	[Description] [ntext] NOT NULL,
	[Details] [ntext] NULL,
 CONSTRAINT [PK_SYNC_LOG_DESCRIPTION_221] PRIMARY KEY CLUSTERED
(
	[Id] ASC
)WITH (PAD_INDEX = OFF, STATISTICS_NORECOMPUTE = OFF, IGNORE_DUP_KEY = OFF, ALLOW_ROW_LOCKS = ON, ALLOW_PAGE_LOCKS = ON) ON [PRIMARY]
) ON [PRIMARY] TEXTIMAGE_ON [PRIMARY]
GO
SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO
CREATE TABLE [SYNC_MERGETERM_209](
	[Id] [int] IDENTITY(1,1) NOT NULL,
 CONSTRAINT [PK_SYNC_MERGETERM_209] PRIMARY KEY CLUSTERED
(
	[Id] ASC
)WITH (PAD_INDEX = OFF, STATISTICS_NORECOMPUTE = OFF, IGNORE_DUP_KEY = OFF, ALLOW_ROW_LOCKS = ON, ALLOW_PAGE_LOCKS = ON) ON [PRIMARY]
) ON [PRIMARY]
GO
SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO
CREATE TABLE [SYNC_MERGETERM_DESCRIPTION_211](
	[Id] [int] IDENTITY(1,1) NOT NULL,
	[Languageid] [nvarchar](1) NOT NULL,
	[Description] [nvarchar](255) NOT NULL,
	[Mergetermid] [int] NOT NULL,
 CONSTRAINT [PK_SYNC_MERGETERM_DESCRIPTION_211] PRIMARY KEY CLUSTERED
(
	[Id] ASC
)WITH (PAD_INDEX = OFF, STATISTICS_NORECOMPUTE = OFF, IGNORE_DUP_KEY = OFF, ALLOW_ROW_LOCKS = ON, ALLOW_PAGE_LOCKS = ON) ON [PRIMARY]
) ON [PRIMARY]
GO
SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO
CREATE TABLE [SYNC_MERGETERM_TERMCODE_210](
	[Id] [int] IDENTITY(1,1) NOT NULL,
	[Termcode] [nvarchar](50) NOT NULL,
	[Mergetermid] [int] NOT NULL,
 CONSTRAINT [PK_SYNC_MERGETERM_TERMCODE_210] PRIMARY KEY CLUSTERED
(
	[Id] ASC
)WITH (PAD_INDEX = OFF, STATISTICS_NORECOMPUTE = OFF, IGNORE_DUP_KEY = OFF, ALLOW_ROW_LOCKS = ON, ALLOW_PAGE_LOCKS = ON) ON [PRIMARY]
) ON [PRIMARY]
GO
SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO
CREATE TABLE [SYNC_QUEUED_CASE_226](
	[Id] [int] IDENTITY(1,1) NOT NULL,
	[Rowguid] [nvarchar](50) NOT NULL
) ON [PRIMARY]
GO
SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO
CREATE TABLE [TID_122](
	[Id] [int] NULL,
	[Datum] [datetime] NULL,
	[Atgardsnr] [smallint] NULL,
	[Tid] [float] NULL,
	[Belopp] [decimal](11, 2) NULL,
	[Debtid] [int] NULL,
	[Valutakod] [nvarchar](3) NULL,
	[Transid] [int] NULL,
	[Ordsida] [numeric](11, 3) NULL,
	[Arendenr] [nvarchar](50) NULL,
	[Rowid] [timestamp] NOT NULL,
	[Kundnr] [nvarchar](7) NULL,
	[Atgardstext] [ntext] NULL
) ON [PRIMARY] TEXTIMAGE_ON [PRIMARY]
GO
SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO
CREATE TABLE [TOPLEVELDOMAIN_198](
	[Topleveldomainid] [int] IDENTITY(1,1) NOT NULL,
	[Tld] [nvarchar](50) NOT NULL,
	[Landkod] [nvarchar](2) NULL,
	[Rules] [ntext] NULL,
	[Rowid] [timestamp] NOT NULL,
	[Fristintervall] [int] NULL,
	[Offavg] [int] NULL,
	[Arvode] [int] NULL,
	[Valutakod] [nvarchar](3) NULL,
	[Localpresence] [int] NULL,
	[Pekare] [int] NULL,
 CONSTRAINT [PK_Topleveldomain_198] PRIMARY KEY CLUSTERED
(
	[Topleveldomainid] ASC
)WITH (PAD_INDEX = OFF, STATISTICS_NORECOMPUTE = OFF, IGNORE_DUP_KEY = OFF, ALLOW_ROW_LOCKS = ON, ALLOW_PAGE_LOCKS = ON) ON [PRIMARY]
) ON [PRIMARY] TEXTIMAGE_ON [PRIMARY]
GO
SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO
CREATE TABLE [TRADEMARKTYPE_297](
	[Id] [int] IDENTITY(1,1) NOT NULL,
	[Code] [nvarchar](20) NOT NULL,
	[Rowid] [timestamp] NOT NULL,
 CONSTRAINT [PK_TRADEMARKTYPE_297] PRIMARY KEY CLUSTERED
(
	[Id] ASC
)WITH (PAD_INDEX = OFF, STATISTICS_NORECOMPUTE = OFF, IGNORE_DUP_KEY = OFF, ALLOW_ROW_LOCKS = ON, ALLOW_PAGE_LOCKS = ON) ON [PRIMARY]
) ON [PRIMARY]
GO
SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO
CREATE TABLE [TRADEMARKTYPE_LANGUAGE_298](
	[Id] [int] IDENTITY(1,1) NOT NULL,
	[TrademarkType_Id] [int] NOT NULL,
	[Sprakkod] [nvarchar](1) NOT NULL,
	[Name] [nvarchar](100) NOT NULL,
	[Rowid] [timestamp] NOT NULL,
 CONSTRAINT [PK_TRADEMARKTYPE_LANGUAGE_298] PRIMARY KEY CLUSTERED
(
	[Id] ASC
)WITH (PAD_INDEX = OFF, STATISTICS_NORECOMPUTE = OFF, IGNORE_DUP_KEY = OFF, ALLOW_ROW_LOCKS = ON, ALLOW_PAGE_LOCKS = ON) ON [PRIMARY]
) ON [PRIMARY]
GO
SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO
CREATE TABLE [UNDERLAG_127](
	[Underlagid] [smallint] NOT NULL,
	[Kundnr] [nvarchar](7) NULL,
	[Anvid] [int] NULL,
	[Sprakkod] [nvarchar](1) NULL,
	[Debetkredit] [nvarchar](1) NULL,
	[Arendenr] [nvarchar](50) NULL,
	[Specadjn] [nvarchar](1) NULL,
	[Manuellrefjn] [nvarchar](1) NULL,
	[Rowid] [timestamp] NOT NULL,
	[Referens] [ntext] NULL,
	[Merge] [bit] NULL,
	[Avireferens] [bit] NULL,
	[Skapad] [datetime] NULL
) ON [PRIMARY] TEXTIMAGE_ON [PRIMARY]
GO
SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO
CREATE TABLE [UNDERRAD_128](
	[Radnr] [smallint] NOT NULL,
	[Underlagid] [smallint] NOT NULL,
	[Belopp] [decimal](11, 2) NULL,
	[Atgardsnr] [smallint] NULL,
	[Valutakod] [nvarchar](3) NULL,
	[Intaktstalle] [nvarchar](6) NULL,
	[Momsjn] [nvarchar](1) NULL,
	[Rowid] [timestamp] NOT NULL,
	[Datum] [datetime] NULL,
	[Kostnadsbarare] [nvarchar](6) NULL,
	[Anvid] [int] NULL,
	[Transid] [int] NULL,
	[Ordsida] [int] NULL,
	[Tid] [float] NULL,
	[Arendenr] [nvarchar](50) NULL,
	[Atgardstext] [ntext] NULL,
	[Sammanslagnatransid] [ntext] NULL,
	[Kalkyltyp] [nvarchar](2) NULL,
	[Timarvode] [int] NULL,
	[Maxdatum] [datetime] NULL,
	[Rabatt] [decimal](11, 2) NULL,
	[Applyvat] [smallint] NULL
) ON [PRIMARY] TEXTIMAGE_ON [PRIMARY]
GO
SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO
CREATE TABLE [UPPF_ARENDE_68](
	[Arendenr] [nvarchar](50) NOT NULL,
	[Uppfnr] [nvarchar](7) NOT NULL,
	[Rowid] [timestamp] NOT NULL,
	[Sortorder] [smallint] NULL,
	[Kategori] [nvarchar](50) NULL,
	[Id] [int] IDENTITY(1,1) NOT NULL,
	[Share] [nvarchar](100) NULL,
 CONSTRAINT [PK_UPPF_ARENDE_68] PRIMARY KEY CLUSTERED
(
	[Id] ASC
)WITH (PAD_INDEX = OFF, STATISTICS_NORECOMPUTE = OFF, IGNORE_DUP_KEY = OFF, ALLOW_ROW_LOCKS = ON, ALLOW_PAGE_LOCKS = ON) ON [PRIMARY]
) ON [PRIMARY]
GO
SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO
CREATE TABLE [UPPFINNARE_69](
	[Uppfnr] [nvarchar](7) NOT NULL,
	[Landkod] [nvarchar](2) NOT NULL,
	[Uppfadress1] [nvarchar](100) NULL,
	[Uppfadress2] [nvarchar](100) NULL,
	[Uppfadress3] [nvarchar](100) NULL,
	[Uppfadress4] [nvarchar](100) NULL,
	[Uppfadress5] [nvarchar](100) NULL,
	[Uppfkortnamn] [nvarchar](50) NOT NULL,
	[Uppfkortort] [nvarchar](15) NULL,
	[Uppfadress6] [nvarchar](100) NULL,
	[Uppfadress7] [nvarchar](100) NULL,
	[Hemort] [nvarchar](50) NULL,
	[Nation] [nvarchar](50) NULL,
	[Andratdat] [datetime] NULL,
	[Anv] [int] NULL,
	[Telefonnr] [nvarchar](50) NULL,
	[Sprakkod] [nvarchar](1) NOT NULL,
	[Rowid] [timestamp] NOT NULL,
	[Rowguid] [uniqueidentifier] NOT NULL,
	[Fritext] [ntext] NULL,
	[Personnr] [nvarchar](15) NULL,
	[Kategori] [nvarchar](50) NULL,
	[Anstalld] [bit] NOT NULL,
	[Transferconfigurationsetid] [int] NULL,
	[Meansofcommunication] [int] NULL,
	[EmailAddresses_To] [nvarchar](max) NULL,
	[EmailAddresses_Cc] [nvarchar](max) NULL,
	[EmailAddresses_Bcc] [nvarchar](max) NULL,
 CONSTRAINT [PK_UPPFINNARE_69] PRIMARY KEY CLUSTERED
(
	[Uppfnr] ASC
)WITH (PAD_INDEX = OFF, STATISTICS_NORECOMPUTE = OFF, IGNORE_DUP_KEY = OFF, ALLOW_ROW_LOCKS = ON, ALLOW_PAGE_LOCKS = ON) ON [PRIMARY]
) ON [PRIMARY] TEXTIMAGE_ON [PRIMARY]
GO
SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO
CREATE TABLE [UPPLYSGRUPP_87](
	[Gruppkod] [nvarchar](2) NOT NULL,
	[Upplysnamn] [nvarchar](25) NULL,
	[Rowid] [timestamp] NOT NULL
) ON [PRIMARY]
GO
SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO
CREATE TABLE [UPPLYSNING_88](
	[Arendenr] [nvarchar](50) NOT NULL,
	[Gruppkod] [nvarchar](2) NOT NULL,
	[Rowid] [timestamp] NOT NULL,
	[Id] [int] IDENTITY(1,1) NOT NULL,
 CONSTRAINT [PK_UPPLYSNING_88] PRIMARY KEY CLUSTERED
(
	[Id] ASC
)WITH (PAD_INDEX = OFF, STATISTICS_NORECOMPUTE = OFF, IGNORE_DUP_KEY = OFF, ALLOW_ROW_LOCKS = ON, ALLOW_PAGE_LOCKS = ON) ON [PRIMARY]
) ON [PRIMARY]
GO
SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO
CREATE TABLE [USER_BUDGET_MONTH_329](
	[Id] [int] IDENTITY(1,1) NOT NULL,
	[User_Id] [int] NOT NULL,
	[Year] [int] NOT NULL,
	[Month] [int] NOT NULL,
	[BudgetAmount] [int] NOT NULL,
	[BudgetHours] [int] NOT NULL,
	[Rowid] [timestamp] NOT NULL,
 CONSTRAINT [PK_USER_BUDGET_MONTH_329] PRIMARY KEY CLUSTERED
(
	[Id] ASC
)WITH (PAD_INDEX = OFF, STATISTICS_NORECOMPUTE = OFF, IGNORE_DUP_KEY = OFF, ALLOW_ROW_LOCKS = ON, ALLOW_PAGE_LOCKS = ON) ON [PRIMARY]
) ON [PRIMARY]
GO
SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO
CREATE TABLE [USERSETTINGS_250](
	[Id] [int] IDENTITY(1,1) NOT NULL,
	[Userid] [int] NOT NULL,
	[Sektion] [nvarchar](100) NOT NULL,
	[Parameter] [nvarchar](100) NOT NULL,
	[Varde] [nvarchar](4000) NOT NULL,
	[Rowid] [timestamp] NOT NULL,
	[Krypterad] [bit] NOT NULL,
 CONSTRAINT [PK_ROUTENAME_250] PRIMARY KEY CLUSTERED
(
	[Id] ASC
)WITH (PAD_INDEX = OFF, STATISTICS_NORECOMPUTE = OFF, IGNORE_DUP_KEY = OFF, ALLOW_ROW_LOCKS = ON, ALLOW_PAGE_LOCKS = ON) ON [PRIMARY]
) ON [PRIMARY]
GO
SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO
CREATE TABLE [UT_ANSFRIST_75](
	[Agare] [int] NULL,
	[Foretag] [nvarchar](100) NULL,
	[Anvid] [int] NULL,
	[Arendenr] [nvarchar](50) NULL,
	[Ansnr] [nvarchar](50) NULL,
	[Sokandeskortnamn] [nvarchar](50) NULL,
	[Intaktstallenamn] [nvarchar](40) NULL,
	[Landkod] [nvarchar](2) NULL,
	[Grundarende] [nvarchar](50) NULL,
	[Fristkodnamn] [nvarchar](50) NULL,
	[Fristkod] [nvarchar](6) NULL,
	[Tid] [datetime] NULL,
	[Handlaggarid] [int] NULL,
	[Statuskod] [nvarchar](1) NULL,
	[Utfdag] [datetime] NULL,
	[Frist] [datetime] NULL,
	[Rapportsand] [datetime] NULL,
	[Paminnelse] [datetime] NULL,
	[Instrmottagen] [datetime] NULL,
	[Instrsand] [datetime] NULL,
	[Slutdag] [datetime] NULL,
	[Svaromal] [datetime] NULL,
	[Fhandlagg] [int] NULL,
	[Lopnr] [smallint] NULL,
	[Belopp] [decimal](11, 2) NULL,
	[Sidor] [nvarchar](4) NULL,
	[Nyjn] [nvarchar](1) NULL,
	[Avslutadjn] [nvarchar](1) NULL,
	[Extern] [nvarchar](1) NULL,
	[Slagord] [nvarchar](250) NULL,
	[Rowid] [timestamp] NOT NULL,
	[Anm] [ntext] NULL,
	[Reviewed] [bit] NULL,
	[Reviewuser] [int] NULL,
	[Reviewdate] [datetime] NULL,
	[Importance] [int] NOT NULL
) ON [PRIMARY] TEXTIMAGE_ON [PRIMARY]
GO
SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO
CREATE TABLE [UT_AVRAKNING_62](
	[Arendenr] [nvarchar](50) NULL,
	[Landkod] [nvarchar](2) NULL,
	[Regnr] [nvarchar](15) NULL,
	[Kundnr] [nvarchar](7) NULL,
	[Arforfrist] [nvarchar](5) NULL,
	[Levbelopp] [decimal](11, 2) NULL,
	[Faktbelopp] [decimal](11, 2) NULL,
	[Diff] [int] NULL,
	[Rowid] [timestamp] NOT NULL
) ON [PRIMARY]
GO
SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO
CREATE TABLE [UT_AVVIKELSE_60](
	[Anvid] [nvarchar](35) NULL,
	[Land] [nvarchar](2) NULL,
	[Patentnr] [nvarchar](50) NULL,
	[Arendenr] [nvarchar](50) NULL,
	[Anm] [nvarchar](30) NULL,
	[Agarid] [int] NULL,
	[Avikundnr] [nvarchar](7) NULL,
	[Rowid] [timestamp] NOT NULL
) ON [PRIMARY]
GO
SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO
CREATE TABLE [UT_BETORDER_65](
	[Anvid] [nvarchar](35) NULL,
	[Arendenr] [nvarchar](50) NULL,
	[Patentnr] [nvarchar](50) NULL,
	[Publnr] [nvarchar](50) NULL,
	[Arforfrist] [nvarchar](5) NULL,
	[Anm] [nvarchar](20) NULL,
	[Innehavare] [nvarchar](100) NULL,
	[Land] [nvarchar](50) NULL,
	[Fristdat] [datetime] NULL,
	[Friststraffdat] [datetime] NULL,
	[Specialfrist] [nvarchar](1) NULL,
	[Manad] [int] NULL,
	[Avgiftskod] [smallint] NULL,
	[Arendetyp] [nvarchar](1) NULL,
	[Kontokod] [int] NULL,
	[Kundnr] [nvarchar](7) NULL,
	[Sprakkod] [nvarchar](1) NULL,
	[Specfakt] [nvarchar](1) NULL,
	[Momsjn] [nvarchar](1) NULL,
	[Istalle] [nvarchar](6) NULL,
	[Arsavgregel] [nvarchar](10) NULL,
	[Aviombudknr] [nvarchar](7) NULL,
	[Avikundnr] [nvarchar](7) NULL,
	[Cckundnr] [nvarchar](7) NULL,
	[Ccorg] [nvarchar](15) NULL,
	[Cctext] [nvarchar](15) NULL,
	[Cckund1] [nvarchar](100) NULL,
	[Cckund2] [nvarchar](100) NULL,
	[Sprak] [nvarchar](20) NULL,
	[Ansokningsnr] [nvarchar](50) NULL,
	[Servicetyp] [nvarchar](1) NULL,
	[Anmarkning] [nvarchar](1) NULL,
	[Kreditjn] [nvarchar](1) NULL,
	[Aviombudsref] [nvarchar](250) NULL,
	[Faxnr] [nvarchar](50) NULL,
	[Tid] [datetime] NULL,
	[Agarid] [int] NULL,
	[Utskriftstyp] [smallint] NULL,
	[Valutakod] [nvarchar](3) NULL,
	[Belopputl] [decimal](11, 2) NULL,
	[Belopparv] [decimal](11, 2) NULL,
	[Beloppmoms] [decimal](11, 2) NULL,
	[Refnr] [int] NULL,
	[Betdag] [datetime] NULL,
	[Rowid] [timestamp] NOT NULL
) ON [PRIMARY]
GO
SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO
CREATE TABLE [UT_BEVAKNING_59](
	[Anvandare] [int] NULL,
	[Rubrik] [nvarchar](10) NULL,
	[Fromdatum] [datetime] NULL,
	[Tomdatum] [datetime] NULL,
	[Forfallodag] [datetime] NULL,
	[Fristmarkering] [nvarchar](2) NULL,
	[Patentnr] [nvarchar](50) NULL,
	[Publnr] [nvarchar](50) NULL,
	[Instr] [nvarchar](1) NULL,
	[Servtyp] [nvarchar](1) NULL,
	[Avgift] [nvarchar](5) NULL,
	[Kortnamn] [nvarchar](50) NULL,
	[Istalle] [nvarchar](6) NULL,
	[Anmarkn] [nvarchar](1) NULL,
	[Landkod] [nvarchar](2) NULL,
	[Arendenr] [nvarchar](50) NULL,
	[Handlaggarid] [int] NULL,
	[Kundnr] [nvarchar](7) NULL,
	[Istalleref] [nvarchar](15) NULL,
	[Avikundref] [nvarchar](250) NULL,
	[Slagord] [nvarchar](250) NULL,
	[Rowid] [timestamp] NOT NULL,
	[Sokandeskortnamn] [nvarchar](50) NULL,
	[Ordersand] [nvarchar](1) NULL,
	[Basicoutsourcingjn] [nvarchar](1) NULL,
	[Arsavgregel] [nvarchar](10) NULL,
	[Offavg] [decimal](18, 2) NULL,
	[Offavgvalutakod] [nvarchar](3) NULL,
	[Kreditjn] [nvarchar](1) NULL,
	[Direktbetalningjn] [nvarchar](1) NULL
) ON [PRIMARY]
GO
SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO
CREATE TABLE [UT_BREV_100](
	[Agarid] [int] NULL,
	[Ankom] [datetime] NULL,
	[Ansnr] [nvarchar](50) NULL,
	[Antalklasser] [smallint] NULL,
	[Antalsamreg] [smallint] NULL,
	[Arendeklass] [nvarchar](1) NULL,
	[Arendekod] [nvarchar](6) NULL,
	[Arendenr] [nvarchar](50) NULL,
	[Arendetyp] [nvarchar](1) NULL,
	[Arsavgiftsregel] [nvarchar](10) NULL,
	[Attention] [nvarchar](50) NULL,
	[Avgiftskod] [smallint] NULL,
	[Belopp] [decimal](11, 2) NULL,
	[Beloppmoms] [decimal](11, 2) NULL,
	[Beviljad] [datetime] NULL,
	[Brevkod] [nvarchar](20) NULL,
	[Figurmarke] [nvarchar](1) NULL,
	[Flersokande] [nvarchar](1) NULL,
	[Frist] [datetime] NULL,
	[Fristhandlaggarid] [int] NULL,
	[Fristkod] [nvarchar](6) NULL,
	[Fristmedstraff] [datetime] NULL,
	[Handlaggarid] [int] NULL,
	[Intaktstallekod] [nvarchar](6) NULL,
	[Intaktstalleref] [nvarchar](35) NULL,
	[Internationell] [datetime] NULL,
	[Kopiebrev] [nvarchar](1) NULL,
	[Kundnr1] [nvarchar](7) NULL,
	[Kundnr2] [nvarchar](7) NULL,
	[Landkod] [nvarchar](2) NULL,
	[Lopdag] [datetime] NULL,
	[Nationell] [datetime] NULL,
	[Offentlig] [datetime] NULL,
	[Ombudetsref] [nvarchar](250) NULL,
	[Patentnr] [nvarchar](50) NULL,
	[Publicering] [datetime] NULL,
	[Publiceringsnr] [nvarchar](50) NULL,
	[Rapportsand] [datetime] NULL,
	[Referens] [nvarchar](250) NULL,
	[Servicetyp] [nvarchar](1) NULL,
	[Skapad] [datetime] NULL,
	[Skaparid] [int] NULL,
	[Skaparnamn] [nvarchar](35) NULL,
	[Slagord] [nvarchar](250) NULL,
	[Slut] [datetime] NULL,
	[Sokandenr] [nvarchar](7) NULL,
	[Sprakkod] [nvarchar](1) NULL,
	[Titel] [nvarchar](500) NULL,
	[Utfardad] [datetime] NULL,
	[Valutakod] [nvarchar](3) NULL,
	[Sidor] [nvarchar](4) NULL,
	[Kund2att] [nvarchar](50) NULL,
	[Rowid] [timestamp] NOT NULL
) ON [PRIMARY]
GO
SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO
CREATE TABLE [UT_BREV_45](
	[Anvid] [nvarchar](35) NULL,
	[Samavi] [nvarchar](1) NULL,
	[Sprak] [nvarchar](1) NULL,
	[Avikundnr] [nvarchar](7) NULL,
	[Avikund1] [nvarchar](100) NULL,
	[Avikund2] [nvarchar](100) NULL,
	[Avikund3] [nvarchar](100) NULL,
	[Avikund4] [nvarchar](100) NULL,
	[Avikund5] [nvarchar](100) NULL,
	[Avikund6] [nvarchar](100) NULL,
	[Avikund7] [nvarchar](100) NULL,
	[Land] [nvarchar](50) NULL,
	[Patentnr] [nvarchar](50) NULL,
	[Forfallodag] [datetime] NULL,
	[Arforfrist] [nvarchar](5) NULL,
	[Innehavare] [nvarchar](100) NULL,
	[Avikundref] [nvarchar](250) NULL,
	[Arendenr] [nvarchar](50) NULL,
	[Cc_org] [nvarchar](15) NULL,
	[Cctext] [nvarchar](15) NULL,
	[Cckund1] [nvarchar](100) NULL,
	[Cckund2] [nvarchar](100) NULL,
	[Moms] [nvarchar](1) NULL,
	[Ansoknr] [nvarchar](50) NULL,
	[Publnr] [nvarchar](50) NULL,
	[Avikundatt] [nvarchar](50) NULL,
	[Arsavgregel] [nvarchar](50) NULL,
	[Landkod] [nvarchar](2) NULL,
	[Avgiftskod] [smallint] NULL,
	[Intaktstalle] [nvarchar](6) NULL,
	[Valutakod] [nvarchar](3) NULL,
	[Beloppexkl] [decimal](11, 2) NULL,
	[Beloppinkl] [decimal](11, 2) NULL,
	[Beloppmoms] [decimal](11, 2) NULL,
	[Agarid] [int] NULL,
	[Typ] [smallint] NULL,
	[Servicetyp] [nvarchar](1) NULL,
	[Kreditjn] [nvarchar](1) NULL,
	[Brevdatum] [datetime] NULL,
	[Brevdatumprev] [datetime] NULL,
	[Forfallodagprev] [datetime] NULL,
	[Ccatt] [nvarchar](50) NULL,
	[Rowid] [timestamp] NOT NULL,
	[Avikundfax] [nvarchar](50) NULL,
	[Avikundemail] [nvarchar](max) NULL,
	[Avikundland] [nvarchar](50) NULL,
	[Innehavare2] [nvarchar](100) NULL,
	[Belopparvode] [decimal](11, 2) NULL,
	[Belopparvodemoms] [decimal](11, 2) NULL,
	[Beloppombarv] [decimal](11, 2) NULL,
	[Beloppombarvmoms] [decimal](11, 2) NULL,
	[Beloppoffavg] [decimal](11, 2) NULL,
	[Beloppoffavgmoms] [decimal](11, 2) NULL
) ON [PRIMARY] TEXTIMAGE_ON [PRIMARY]
GO
SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO
CREATE TABLE [UT_BREV_52](
	[Anvid] [int] NULL,
	[Anvandare] [nvarchar](35) NULL,
	[Agarid] [int] NULL,
	[Sprakkod] [nvarchar](1) NULL,
	[Brevkod] [nvarchar](20) NULL,
	[Arendenr] [nvarchar](50) NULL,
	[Ansnr] [nvarchar](50) NULL,
	[Patnr] [nvarchar](50) NULL,
	[Avikundref] [nvarchar](250) NULL,
	[Avikundatt] [nvarchar](50) NULL,
	[Slagord] [nvarchar](250) NULL,
	[Landnamn] [nvarchar](50) NULL,
	[Sokandeadr1] [nvarchar](100) NULL,
	[Avikundnr] [nvarchar](7) NULL,
	[Avikund1] [nvarchar](100) NULL,
	[Avikund2] [nvarchar](100) NULL,
	[Avikund3] [nvarchar](100) NULL,
	[Avikund4] [nvarchar](100) NULL,
	[Avikund5] [nvarchar](100) NULL,
	[Avikund6] [nvarchar](100) NULL,
	[Avikund7] [nvarchar](100) NULL,
	[Frist] [datetime] NULL,
	[Friststraff] [datetime] NULL,
	[Lopnr] [smallint] NULL,
	[Ledtext1] [nvarchar](50) NULL,
	[Ledtext2] [nvarchar](50) NULL,
	[Ledtext4] [nvarchar](50) NULL,
	[Ledtext5] [nvarchar](50) NULL,
	[Antalklasser] [smallint] NULL,
	[Antalsamreg] [smallint] NULL,
	[Titel] [nvarchar](500) NULL,
	[Klasser] [nvarchar](254) NULL,
	[Instrdatum] [datetime] NULL,
	[Ccorg] [nvarchar](15) NULL,
	[Cctext] [nvarchar](15) NULL,
	[Cckund1] [nvarchar](100) NULL,
	[Cckund2] [nvarchar](100) NULL,
	[Andrattext] [nvarchar](60) NULL,
	[Landkod] [nvarchar](2) NULL,
	[Rowid] [timestamp] NOT NULL,
	[Period] [smallint] NULL
) ON [PRIMARY]
GO
SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO
CREATE TABLE [UT_BREV_66](
	[Anvid] [int] NULL,
	[Agarid] [int] NULL,
	[Typ] [nvarchar](3) NULL,
	[Samavi] [nvarchar](1) NULL,
	[Sprak] [nvarchar](1) NULL,
	[Avikundnr] [nvarchar](7) NULL,
	[Avikund1] [nvarchar](100) NULL,
	[Avikund2] [nvarchar](100) NULL,
	[Avikund3] [nvarchar](100) NULL,
	[Avikund4] [nvarchar](100) NULL,
	[Avikund5] [nvarchar](100) NULL,
	[Avikund6] [nvarchar](100) NULL,
	[Avikund7] [nvarchar](100) NULL,
	[Land] [nvarchar](50) NULL,
	[Patentnr] [nvarchar](50) NULL,
	[Forfallodag] [datetime] NULL,
	[Arforfrist] [nvarchar](5) NULL,
	[Innehavare] [nvarchar](100) NULL,
	[Avikundref] [nvarchar](250) NULL,
	[Arendenr] [nvarchar](50) NULL,
	[Cc_org] [nvarchar](15) NULL,
	[Cctext] [nvarchar](15) NULL,
	[Cckund1] [nvarchar](100) NULL,
	[Cckund2] [nvarchar](100) NULL,
	[Ansokningsnr] [nvarchar](50) NULL,
	[Publiceringsnr] [nvarchar](50) NULL,
	[Avikundattention] [nvarchar](50) NULL,
	[Instrdag] [datetime] NULL,
	[Arsavgregel] [nvarchar](50) NULL,
	[Flersokande] [nvarchar](1) NULL,
	[Intaktstalle] [nvarchar](6) NULL,
	[Anvnamn] [nvarchar](35) NULL,
	[Ledtext] [nvarchar](50) NULL,
	[Fristmedstraff] [datetime] NULL,
	[Tid] [datetime] NULL,
	[Valutakod] [nvarchar](3) NULL,
	[Beloppexkl] [decimal](11, 2) NULL,
	[Beloppinkl] [decimal](11, 2) NULL,
	[Beloppmoms] [decimal](11, 2) NULL,
	[Rowid] [timestamp] NOT NULL
) ON [PRIMARY]
GO
SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO
CREATE TABLE [UT_DAVISERING_239](
	[Agarid] [int] NULL,
	[Sprakkod] [nvarchar](1) NOT NULL,
	[Kundnr] [nvarchar](7) NOT NULL,
	[Arendenr] [nvarchar](50) NOT NULL,
	[Slagord] [nvarchar](250) NULL,
	[Dns] [bit] NOT NULL,
	[Frist] [datetime] NOT NULL,
	[Belopp] [decimal](18, 2) NOT NULL,
	[Valutakod] [nvarchar](3) NOT NULL,
	[Rowid] [timestamp] NOT NULL,
	[Aktfrist_76_id] [int] NOT NULL
) ON [PRIMARY]
GO
SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO
CREATE TABLE [UT_ETIKETT_82](
	[Arendenr] [nvarchar](50) NULL,
	[Anvid] [int] NULL,
	[Antal] [smallint] NULL,
	[Flagga] [nvarchar](1) NULL,
	[Tidsstampel] [datetime] NULL,
	[Agarid] [int] NULL,
	[Rowid] [timestamp] NOT NULL
) ON [PRIMARY]
GO
SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO
CREATE TABLE [UT_FAVISERING_44](
	[Brevdatum] [datetime] NULL,
	[Typ] [smallint] NULL,
	[Agarid] [int] NULL,
	[Sprak] [nvarchar](1) NULL,
	[Avikundnr] [nvarchar](7) NULL,
	[Avikund1] [nvarchar](100) NULL,
	[Avikund2] [nvarchar](100) NULL,
	[Avikund3] [nvarchar](100) NULL,
	[Avikund4] [nvarchar](100) NULL,
	[Avikund5] [nvarchar](100) NULL,
	[Avikund6] [nvarchar](100) NULL,
	[Avikund7] [nvarchar](100) NULL,
	[Land] [nvarchar](50) NULL,
	[Arendenr] [nvarchar](50) NULL,
	[Patentnr] [nvarchar](50) NULL,
	[Ansokningsnr] [nvarchar](50) NULL,
	[Servicetyp] [nvarchar](1) NULL,
	[Forfallodag] [datetime] NULL,
	[Lopno] [smallint] NULL,
	[Regel] [nvarchar](10) NULL,
	[Vm] [nvarchar](1) NULL,
	[Kreditjn] [nvarchar](1) NULL,
	[Momsjn] [nvarchar](1) NULL,
	[Sokandeadress1] [nvarchar](100) NULL,
	[Avikundref] [nvarchar](250) NULL,
	[Avikundatt] [nvarchar](50) NULL,
	[Slagord] [nvarchar](250) NULL,
	[Ccorg] [nvarchar](15) NULL,
	[Cctext] [nvarchar](15) NULL,
	[Cckund1] [nvarchar](100) NULL,
	[Cckund2] [nvarchar](100) NULL,
	[Ledtext1] [nvarchar](50) NULL,
	[Ledtext2] [nvarchar](50) NULL,
	[Ledtext4] [nvarchar](50) NULL,
	[Ledtext5] [nvarchar](50) NULL,
	[Antalklasser] [smallint] NULL,
	[Titel] [nvarchar](500) NULL,
	[Antalsamreg] [smallint] NULL,
	[Klasser] [nvarchar](254) NULL,
	[Intaktstalle] [nvarchar](6) NULL,
	[Skaparid] [int] NULL,
	[Valutakod] [nvarchar](3) NULL,
	[Grundavgiftexkl] [decimal](11, 2) NULL,
	[Grundavgiftinkl] [decimal](11, 2) NULL,
	[Klassavgiftexkl] [decimal](11, 2) NULL,
	[Klassavgiftinkl] [decimal](11, 2) NULL,
	[Brevdatumprev] [datetime] NULL,
	[Forfallodagprev] [datetime] NULL,
	[Ccatt] [nvarchar](50) NULL,
	[Sokandeadress2] [nvarchar](100) NULL,
	[Landkod] [nvarchar](2) NULL,
	[Grundklasser] [smallint] NULL,
	[Rowid] [timestamp] NOT NULL,
	[Period] [smallint] NULL,
	[Avikundfax] [nvarchar](50) NULL,
	[Avikundemail] [nvarchar](max) NULL,
	[Avikundland] [nvarchar](50) NULL,
	[Belopparvode] [decimal](11, 2) NULL,
	[Belopparvodemoms] [decimal](11, 2) NULL,
	[Beloppombarv] [decimal](11, 2) NULL,
	[Beloppombarvmoms] [decimal](11, 2) NULL,
	[Beloppoffavg] [decimal](11, 2) NULL,
	[Beloppoffavgmoms] [decimal](11, 2) NULL
) ON [PRIMARY] TEXTIMAGE_ON [PRIMARY]
GO
SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO
CREATE TABLE [UT_KVITTENS_27](
	[Id] [int] NULL,
	[Landkod] [nvarchar](2) NULL,
	[Nr] [nvarchar](25) NULL,
	[Arforfrist] [nvarchar](5) NULL,
	[Text] [nvarchar](40) NULL,
	[Utlagg] [int] NULL,
	[Noteringsdag] [datetime] NULL,
	[Anmarkning] [nvarchar](1) NULL,
	[Totalbelopp] [int] NULL,
	[Arendenr] [nvarchar](50) NULL,
	[Kontokodnamn] [nvarchar](25) NULL,
	[Avikund] [nvarchar](50) NULL,
	[Tid] [datetime] NULL,
	[Publiceringsnr] [nvarchar](50) NULL,
	[Utskriftstyp] [smallint] NULL,
	[Rowid] [timestamp] NOT NULL
) ON [PRIMARY]
GO
SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO
CREATE TABLE [UT_KVITTENS_43](
	[Anvid] [int] NULL,
	[Agarid] [int] NULL,
	[Arendenr] [nvarchar](50) NULL,
	[Anspatnr] [nvarchar](50) NULL,
	[Slagord] [nvarchar](250) NULL,
	[Landkod] [nvarchar](2) NULL,
	[Text1] [nvarchar](40) NULL,
	[Text2] [nvarchar](40) NULL,
	[Regel] [nvarchar](10) NULL,
	[Frist] [datetime] NULL,
	[Friststraff] [datetime] NULL,
	[Period] [smallint] NULL,
	[Belopp] [decimal](11, 2) NULL,
	[Kortnamnkund] [nvarchar](50) NULL,
	[Tid] [datetime] NULL,
	[Rowid] [timestamp] NOT NULL
) ON [PRIMARY]
GO
SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO
CREATE TABLE [UT_RAPPORT_67](
	[Sokande] [nvarchar](100) NULL,
	[Land] [nvarchar](2) NULL,
	[Manad] [nvarchar](20) NULL,
	[Anspatnr] [nvarchar](50) NULL,
	[Ar] [nvarchar](5) NULL,
	[Forfallodag] [datetime] NULL,
	[Arendenr] [nvarchar](50) NULL,
	[Kund] [nvarchar](50) NULL,
	[Arsavgregel] [nvarchar](10) NULL,
	[Belopp] [decimal](11, 2) NULL,
	[Straffanm] [nvarchar](1) NULL,
	[Slagord] [nvarchar](250) NULL,
	[Grundarende] [nvarchar](50) NULL,
	[Ledtext] [nvarchar](50) NULL,
	[Anmarkning] [nvarchar](40) NULL,
	[Sortdatum] [datetime] NULL,
	[Rowid] [timestamp] NOT NULL,
	[Regel] [nvarchar](10) NULL,
	[Regelnamn] [nvarchar](50) NULL,
	[Arvode] [decimal](11, 2) NULL,
	[Arvodeklass] [decimal](11, 2) NULL,
	[Ombarv] [decimal](11, 2) NULL,
	[Ombarvklass] [decimal](11, 2) NULL,
	[Offavg] [decimal](11, 2) NULL,
	[Offavgklass] [decimal](11, 2) NULL,
	[P1ombarv] [decimal](11, 2) NULL,
	[P1ombarvklass] [decimal](11, 2) NULL,
	[P1offavg] [decimal](11, 2) NULL,
	[P1offavgklass] [decimal](11, 2) NULL,
	[P2arv] [decimal](11, 2) NULL,
	[P2arvklass] [decimal](11, 2) NULL,
	[P2ombarv] [decimal](11, 2) NULL,
	[P2ombarvklass] [decimal](11, 2) NULL,
	[P2offavg] [decimal](11, 2) NULL,
	[P2offavgklass] [decimal](11, 2) NULL,
	[Arvp1] [decimal](11, 2) NULL,
	[Arv2p1] [decimal](11, 2) NULL
) ON [PRIMARY]
GO
SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO
CREATE TABLE [UT_REGBLAD_35](
	[Arendenr] [nvarchar](50) NULL,
	[Anvid] [int] NULL,
	[Antal] [smallint] NULL,
	[Flagga] [nvarchar](1) NULL,
	[Tidsstampel] [datetime] NULL,
	[Agarid] [int] NULL,
	[Rowid] [timestamp] NOT NULL
) ON [PRIMARY]
GO
SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO
CREATE TABLE [UTBMSKOD_181](
	[Einvoicetype] [smallint] NOT NULL,
	[Category] [nvarchar](50) NULL,
	[Code] [nvarchar](20) NULL,
	[Description] [nvarchar](255) NULL,
	[Rowid] [timestamp] NOT NULL,
	[Categorygroup] [nvarchar](2) NULL
) ON [PRIMARY]
GO
SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO
CREATE TABLE [UTBRYGGA_12](
	[Verifnr] [int] NULL,
	[Bokfdatum] [datetime] NULL,
	[Kontonr] [nvarchar](6) NULL,
	[Intaktstalle] [nvarchar](6) NULL,
	[Kostnadsbarare] [nvarchar](6) NULL,
	[Projekt] [nvarchar](100) NULL,
	[Faktnr] [int] NULL,
	[Kundnr] [nvarchar](7) NULL,
	[Arendenr] [nvarchar](50) NULL,
	[Belopp] [decimal](11, 2) NULL,
	[Belopputl] [decimal](11, 2) NULL,
	[Rowid] [timestamp] NOT NULL,
	[Fritext] [nvarchar](max) NULL
) ON [PRIMARY] TEXTIMAGE_ON [PRIMARY]
GO
SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO
CREATE TABLE [UTBRYGGA_REDO_64](
	[Kundnr] [nvarchar](7) NULL,
	[Faktnr] [int] NULL,
	[Verifnr] [int] NULL,
	[Faktdatum] [datetime] NULL,
	[Bokfdatum] [datetime] NULL,
	[Forfallodatum] [datetime] NULL,
	[Faktbeloppvaluta] [decimal](11, 2) NULL,
	[Valutakod] [nvarchar](3) NULL,
	[Kontonrfodran] [int] NULL,
	[Intaktstalle] [nvarchar](6) NULL,
	[Kostnadsbarare] [nvarchar](6) NULL,
	[Projekt] [nvarchar](100) NULL,
	[Arendenr] [nvarchar](50) NULL,
	[Faktbelopp] [decimal](11, 2) NULL,
	[Momsbelopp] [decimal](11, 2) NULL,
	[Rowid] [timestamp] NOT NULL
) ON [PRIMARY]
GO
SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO
CREATE TABLE [VALUTA_46](
	[Valutakod] [nvarchar](3) NOT NULL,
	[Valutatyp] [smallint] NULL,
	[Saljkurs] [decimal](11, 6) NULL,
	[Regdat] [datetime] NULL,
	[Kopkurs] [decimal](11, 6) NULL,
	[Upprakningsproc] [decimal](5, 2) NULL,
	[Ordning] [smallint] NULL,
	[Ekonomikod] [nvarchar](3) NULL,
	[Rowid] [timestamp] NOT NULL
) ON [PRIMARY]
GO
SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO
CREATE TABLE [VMAVIHIST_108](
	[Sekvnrvmhist] [smallint] NOT NULL,
	[Regel] [nvarchar](10) NOT NULL,
	[Arendenr] [nvarchar](50) NOT NULL,
	[Avgiftskod] [smallint] NOT NULL,
	[Avisekvensdatum] [datetime] NOT NULL,
	[Anm] [nvarchar](22) NULL,
	[Anv] [int] NULL,
	[Rowid] [timestamp] NOT NULL,
	[Id] [int] IDENTITY(1,1) NOT NULL,
 CONSTRAINT [PK_VMAVIHIST_108] PRIMARY KEY CLUSTERED
(
	[Id] ASC
)WITH (PAD_INDEX = OFF, STATISTICS_NORECOMPUTE = OFF, IGNORE_DUP_KEY = OFF, ALLOW_ROW_LOCKS = ON, ALLOW_PAGE_LOCKS = ON) ON [PRIMARY]
) ON [PRIMARY]
GO
SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO
CREATE TABLE [XML_INSTRUCTION_QUEUE_276](
	[Id] [int] IDENTITY(1,1) NOT NULL,
	[Arendenr] [nvarchar](50) NOT NULL,
	[Avgiftskod] [int] NOT NULL,
	[Arforfrist] [nvarchar](5) NOT NULL,
	[Frist] [datetime] NOT NULL,
	[State] [int] NOT NULL,
	[Landkod] [nvarchar](2) NOT NULL,
	[Klasskod] [nvarchar](1) NOT NULL,
	[Batch] [datetime] NULL,
	[Rowid] [timestamp] NOT NULL,
	[Amount] [decimal](11, 2) NULL,
	[Straff] [bit] NULL,
	[Normalfrist] [datetime] NULL,
	[Straffrist] [datetime] NULL,
 CONSTRAINT [PK_XML_INSTRUCTION_QUEUE] PRIMARY KEY CLUSTERED
(
	[Id] ASC
)WITH (PAD_INDEX = OFF, STATISTICS_NORECOMPUTE = OFF, IGNORE_DUP_KEY = OFF, ALLOW_ROW_LOCKS = ON, ALLOW_PAGE_LOCKS = ON) ON [PRIMARY]
) ON [PRIMARY]
GO
SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO
CREATE TABLE [XML_INSTRUCTION_QUEUE_EVENTS_277](
	[Id] [int] IDENTITY(1,1) NOT NULL,
	[Instruction_Id] [int] NOT NULL,
	[Type] [int] NOT NULL,
	[User_id] [int] NOT NULL,
	[Date] [datetime] NOT NULL,
	[Rowid] [timestamp] NOT NULL,
 CONSTRAINT [PK_XML_INSTRUCTION_QUEUE_EVENT_277] PRIMARY KEY CLUSTERED
(
	[Id] ASC
)WITH (PAD_INDEX = OFF, STATISTICS_NORECOMPUTE = OFF, IGNORE_DUP_KEY = OFF, ALLOW_ROW_LOCKS = ON, ALLOW_PAGE_LOCKS = ON) ON [PRIMARY]
) ON [PRIMARY]
GO
SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO
CREATE TABLE [XML_INVOICE_TYPE_203](
	[Xmlinvoicetypeid] [int] NOT NULL,
	[Description] [nvarchar](50) NOT NULL,
	[Stylesheetdirectory] [nvarchar](255) NOT NULL,
	[Stylesheets] [nvarchar](255) NOT NULL,
	[Outputfiles] [nvarchar](255) NOT NULL,
	[Copyinvoicetooutputdirectory] [bit] NOT NULL,
 CONSTRAINT [PK_XML_INVOICE_TYPE_203] PRIMARY KEY CLUSTERED
(
	[Xmlinvoicetypeid] ASC
)WITH (PAD_INDEX = OFF, STATISTICS_NORECOMPUTE = OFF, IGNORE_DUP_KEY = OFF, ALLOW_ROW_LOCKS = ON, ALLOW_PAGE_LOCKS = ON) ON [PRIMARY]
) ON [PRIMARY]
GO
SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO
CREATE TABLE [XML_PAYMENT_TYPE_275](
	[Id] [int] IDENTITY(1,1) NOT NULL,
	[Name] [nvarchar](100) NOT NULL,
	[Landkod] [nvarchar](2) NOT NULL,
	[Patents] [bit] NOT NULL,
	[Trademarks] [bit] NOT NULL,
	[Designs] [bit] NOT NULL,
	[TemplateName] [nvarchar](100) NOT NULL,
	[OutputExtension] [nvarchar](50) NOT NULL,
	[Rowid] [timestamp] NOT NULL,
 CONSTRAINT [PK_XML_PAYMENT_TYPE_275] PRIMARY KEY CLUSTERED
(
	[Id] ASC
)WITH (PAD_INDEX = OFF, STATISTICS_NORECOMPUTE = OFF, IGNORE_DUP_KEY = OFF, ALLOW_ROW_LOCKS = ON, ALLOW_PAGE_LOCKS = ON) ON [PRIMARY]
) ON [PRIMARY]
GO
SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO
CREATE TABLE [ADDITIONAL_CASES_311](
	[Docid] [int] NOT NULL,
	[Caseno] [nvarchar](50) NOT NULL
) ON [PRIMARY]
GO
SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO
CREATE TABLE [DOC_CATEGORY_309](
	[Category] [int] NOT NULL,
	[Name] [nvarchar](50) NOT NULL,
	[Class] [int] NOT NULL,
	[Rowid] [timestamp] NOT NULL,
	[Tabname] [nvarchar](50) NULL,
 CONSTRAINT [PK_DOC_CATEGORY_309] PRIMARY KEY CLUSTERED
(
	[Category] ASC
)WITH (PAD_INDEX = OFF, STATISTICS_NORECOMPUTE = OFF, IGNORE_DUP_KEY = OFF, ALLOW_ROW_LOCKS = ON, ALLOW_PAGE_LOCKS = ON) ON [PRIMARY]
) ON [PRIMARY]
GO
SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO
CREATE TABLE [DOC_CLASS_308](
	[Class] [int] NOT NULL,
	[Name] [nvarchar](50) NOT NULL,
	[Rowid] [timestamp] NOT NULL,
	[Tabname] [nvarchar](50) NULL,
 CONSTRAINT [PK_DOC_CLASS_308] PRIMARY KEY CLUSTERED
(
	[Class] ASC
)WITH (PAD_INDEX = OFF, STATISTICS_NORECOMPUTE = OFF, IGNORE_DUP_KEY = OFF, ALLOW_ROW_LOCKS = ON, ALLOW_PAGE_LOCKS = ON) ON [PRIMARY]
) ON [PRIMARY]
GO
SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO
CREATE TABLE [DOC_DELEGATED_315](
	[Docdelegatedid] [int] IDENTITY(1,1) NOT NULL,
	[Userid] [int] NULL,
	[Comment] [ntext] NULL,
	[Docid] [int] NOT NULL,
	[Delegateddate] [datetime] NOT NULL,
	[History] [int] NOT NULL,
	[Delegatedbyuserid] [int] NOT NULL,
	[Aktfrist_76_id] [int] NULL,
	[Fristkod] [nvarchar](6) NULL,
	[Frist] [datetime] NULL,
	[Expires] [datetime] NULL,
	[UserFlagTooTip] [nvarchar](255) NULL,
	[Reset] [datetime] NULL,
	[Docgroupid] [int] NULL,
	[Docgroupname] [nvarchar](50) NULL,
	[UserFlag] [int] NULL,
	[Tmp_Userid] [nvarchar](6) NULL,
 CONSTRAINT [PK_DOC_DELEGATED_315] PRIMARY KEY CLUSTERED
(
	[Docdelegatedid] ASC
)WITH (PAD_INDEX = OFF, STATISTICS_NORECOMPUTE = OFF, IGNORE_DUP_KEY = OFF, ALLOW_ROW_LOCKS = ON, ALLOW_PAGE_LOCKS = ON) ON [PRIMARY]
) ON [PRIMARY] TEXTIMAGE_ON [PRIMARY]
GO
SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO
CREATE TABLE [DOC_GROUP_313](
	[Docgroupid] [int] IDENTITY(1,1) NOT NULL,
	[Code] [nvarchar](3) NOT NULL,
	[Name] [nvarchar](50) NOT NULL,
	[Description] [nvarchar](254) NULL,
	[Rowid] [timestamp] NOT NULL,
 CONSTRAINT [PK_DOC_GROUP_313] PRIMARY KEY CLUSTERED
(
	[Docgroupid] ASC
)WITH (PAD_INDEX = OFF, STATISTICS_NORECOMPUTE = OFF, IGNORE_DUP_KEY = OFF, ALLOW_ROW_LOCKS = ON, ALLOW_PAGE_LOCKS = ON) ON [PRIMARY]
) ON [PRIMARY]
GO
SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO
CREATE TABLE [DOC_GROUP_USER_314](
	[Docgroupuserid] [int] IDENTITY(1,1) NOT NULL,
	[Docgroupid] [int] NOT NULL,
	[Userid] [int] NOT NULL,
	[Rowid] [timestamp] NOT NULL,
 CONSTRAINT [PK_DOC_GROUP_USER_314] PRIMARY KEY CLUSTERED
(
	[Docgroupuserid] ASC
)WITH (PAD_INDEX = OFF, STATISTICS_NORECOMPUTE = OFF, IGNORE_DUP_KEY = OFF, ALLOW_ROW_LOCKS = ON, ALLOW_PAGE_LOCKS = ON) ON [PRIMARY]
) ON [PRIMARY]
GO
SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO
CREATE TABLE [DOC_PINNED_317](
	[Docpinnedid] [int] IDENTITY(1,1) NOT NULL,
	[Userid] [int] NOT NULL,
	[Docid] [int] NOT NULL,
	[Pinneddate] [datetime] NOT NULL,
 CONSTRAINT [PK_DOC_PINNED_317] PRIMARY KEY CLUSTERED
(
	[Docpinnedid] ASC
)WITH (PAD_INDEX = OFF, STATISTICS_NORECOMPUTE = OFF, IGNORE_DUP_KEY = OFF, ALLOW_ROW_LOCKS = ON, ALLOW_PAGE_LOCKS = ON) ON [PRIMARY]
) ON [PRIMARY]
GO
SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO
CREATE TABLE [DOC_TYPE_310](
	[Type] [int] NOT NULL,
	[Name] [nvarchar](50) NOT NULL,
	[Category] [int] NOT NULL,
	[Rowid] [timestamp] NOT NULL,
	[Tabname] [nvarchar](50) NULL,
 CONSTRAINT [PK_DOC_TYPE_310] PRIMARY KEY CLUSTERED
(
	[Type] ASC
)WITH (PAD_INDEX = OFF, STATISTICS_NORECOMPUTE = OFF, IGNORE_DUP_KEY = OFF, ALLOW_ROW_LOCKS = ON, ALLOW_PAGE_LOCKS = ON) ON [PRIMARY]
) ON [PRIMARY]
GO
SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO
CREATE TABLE [DOCUMENT_VERSIONS_324](
	[Id] [int] IDENTITY(1,1) NOT NULL,
	[Documents_307_id] [int] NOT NULL,
	[DocumentGUID] [uniqueidentifier] NOT NULL,
	[Latestversion] [bit] NOT NULL,
	[Contentdatabase] [int] NOT NULL,
	[Version] [datetime2](7) NOT NULL,
	[Changedate] [datetime] NOT NULL,
	[Documentdata_id] [int] NOT NULL,
	[Filesize] [int] NULL,
 CONSTRAINT [PK_DOCUMENTVERSIONS_324] PRIMARY KEY CLUSTERED
(
	[Id] ASC
)WITH (PAD_INDEX = OFF, STATISTICS_NORECOMPUTE = OFF, IGNORE_DUP_KEY = OFF, ALLOW_ROW_LOCKS = ON, ALLOW_PAGE_LOCKS = ON) ON [PRIMARY]
) ON [PRIMARY]
GO
SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO
CREATE TABLE [DOCUMENTS_307](
	[Docid] [int] IDENTITY(1,1) NOT NULL,
	[DocumentGUID] [uniqueidentifier] NOT NULL,
	[Docname] [nvarchar](254) NOT NULL,
	[Management] [int] NOT NULL,
	[Class] [int] NULL,
	[Category] [int] NULL,
	[Type] [int] NULL,
	[Userid] [int] NOT NULL,
	[Createdate] [datetime] NOT NULL,
	[Keywords] [nvarchar](254) NULL,
	[Comments] [nvarchar](254) NULL,
	[Wprotect] [int] NOT NULL,
	[Rprotect] [int] NOT NULL,
	[Cfolder] [nvarchar](254) NULL,
	[CUserid] [int] NULL,
	[Fextension] [nvarchar](100) NULL,
	[Rowid] [timestamp] NOT NULL,
	[Caseno] [nvarchar](50) NULL,
	[Customerno] [nvarchar](7) NULL,
	[Applicantno] [nvarchar](7) NULL,
	[Inventorno] [nvarchar](7) NULL,
	[Filesize] [int] NOT NULL,
	[Batchguid] [uniqueidentifier] NULL,
	[Changedate] [datetime] NULL,
	[Changeuserid] [int] NULL,
	[Extref] [nvarchar](100) NULL,
	[AllowPublish] [int] NOT NULL,
	[Rowguid] [uniqueidentifier] NOT NULL,
	[Doctitle] [nvarchar](254) NULL,
	[Version] [int] NULL,
	[Deleted] [int] NOT NULL,
	[Docgroupid] [int] NULL,
	[Visualizeas] [nvarchar](8) NULL,
	[Isattachedtodocid] [int] NULL,
	[Showmailandattachmenticon] [int] NULL,
	[Documentisurl] [int] NULL,
	[DocumentUrl] [nvarchar](254) NULL,
	[DocumentDated] [datetime] NULL,
	[Secondaryclass] [int] NULL,
	[Secondarycategory] [int] NULL,
	[Secondarytype] [int] NULL,
	[Ids] [int] NULL,
	[Versioncontrol] [int] NOT NULL,
 CONSTRAINT [PK_DOCUMENTS_307] PRIMARY KEY CLUSTERED
(
	[Docid] ASC
)WITH (PAD_INDEX = OFF, STATISTICS_NORECOMPUTE = OFF, IGNORE_DUP_KEY = OFF, ALLOW_ROW_LOCKS = ON, ALLOW_PAGE_LOCKS = ON) ON [PRIMARY]
) ON [PRIMARY]
GO
SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO
CREATE TABLE [DOCUMENTS_TMP_312](
	[Docid] [int] IDENTITY(1,1) NOT NULL,
	[Docname] [nvarchar](254) NOT NULL,
	[Management] [int] NOT NULL,
	[Class] [int] NULL,
	[Category] [int] NULL,
	[Type] [int] NULL,
	[Userid] [int] NOT NULL,
	[Createdate] [datetime] NOT NULL,
	[Keywords] [nvarchar](254) NULL,
	[Comments] [nvarchar](254) NULL,
	[Wprotect] [int] NOT NULL,
	[Rprotect] [int] NOT NULL,
	[Cfolder] [nvarchar](254) NULL,
	[CUserid] [int] NULL,
	[Fdata] [varbinary](max) NULL,
	[Fextension] [nvarchar](100) NULL,
	[Rowid] [timestamp] NOT NULL,
	[Caseno] [nvarchar](50) NULL,
	[Customerno] [nvarchar](7) NULL,
	[Applicantno] [nvarchar](7) NULL,
	[Inventorno] [nvarchar](7) NULL,
	[Filesize] [int] NOT NULL,
	[Batchguid] [uniqueidentifier] NULL,
	[Changedate] [datetime] NULL,
	[Changeuserid] [int] NULL,
	[Extref] [nvarchar](100) NULL,
	[AllowPublish] [int] NOT NULL,
	[Rowguid] [uniqueidentifier] NOT NULL,
	[Agarid] [int] NOT NULL,
	[Objtype] [nvarchar](15) NULL,
	[Doctitle] [nvarchar](254) NULL,
	[Printid] [nvarchar](50) NULL,
	[AdditionalCases] [nvarchar](max) NULL,
	[Fakturanr] [int] NULL,
	[DocumentDated] [datetime] NULL,
 CONSTRAINT [PK_DOCUMENTS_TMP_312] PRIMARY KEY CLUSTERED
(
	[Docid] ASC
)WITH (PAD_INDEX = OFF, STATISTICS_NORECOMPUTE = OFF, IGNORE_DUP_KEY = OFF, ALLOW_ROW_LOCKS = ON, ALLOW_PAGE_LOCKS = ON) ON [PRIMARY]
) ON [PRIMARY] TEXTIMAGE_ON [PRIMARY]
GO
SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO
CREATE TABLE [FILENAMES_316](
	[Filenameid] [int] IDENTITY(1,1) NOT NULL,
	[Filename] [nvarchar](254) NOT NULL,
	[Rowid] [timestamp] NOT NULL,
 CONSTRAINT [PK_FILENAMES_316] PRIMARY KEY CLUSTERED
(
	[Filenameid] ASC
)WITH (PAD_INDEX = OFF, STATISTICS_NORECOMPUTE = OFF, IGNORE_DUP_KEY = OFF, ALLOW_ROW_LOCKS = ON, ALLOW_PAGE_LOCKS = ON) ON [PRIMARY]
) ON [PRIMARY]
GO
SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO
CREATE TABLE [ARTICLE_278](
	[Id] [int] IDENTITY(1,1) NOT NULL,
	[PublishDate] [datetime] NOT NULL,
	[Changed] [datetime] NOT NULL,
	[Title] [nvarchar](100) NOT NULL,
	[Text] [nvarchar](max) NOT NULL,
	[MinVersionString] [nvarchar](50) NULL,
	[MaxVersionString] [nvarchar](50) NULL,
	[ExternalIdentifier] [uniqueidentifier] NOT NULL,
	[Rowid] [timestamp] NOT NULL,
 CONSTRAINT [PK_ARTICLE_278_1d] PRIMARY KEY CLUSTERED
(
	[Id] ASC
)WITH (PAD_INDEX = OFF, STATISTICS_NORECOMPUTE = OFF, IGNORE_DUP_KEY = OFF, ALLOW_ROW_LOCKS = ON, ALLOW_PAGE_LOCKS = ON) ON [PRIMARY]
) ON [PRIMARY] TEXTIMAGE_ON [PRIMARY]
GO
SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO
CREATE TABLE [ARTICLE_LINK_280](
	[Id] [int] IDENTITY(1,1) NOT NULL,
	[Article_Id] [int] NOT NULL,
	[Name] [nvarchar](100) NOT NULL,
	[Url] [nvarchar](max) NOT NULL,
	[ExternalIdentifier] [uniqueidentifier] NOT NULL,
 CONSTRAINT [PK_ARTICLE_LINK_280] PRIMARY KEY CLUSTERED
(
	[Id] ASC
)WITH (PAD_INDEX = OFF, STATISTICS_NORECOMPUTE = OFF, IGNORE_DUP_KEY = OFF, ALLOW_ROW_LOCKS = ON, ALLOW_PAGE_LOCKS = ON) ON [PRIMARY]
) ON [PRIMARY] TEXTIMAGE_ON [PRIMARY]
GO
SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO
CREATE TABLE [ARTICLE_READ_281](
	[Id] [int] IDENTITY(1,1) NOT NULL,
	[User_Id] [int] NOT NULL,
	[Time] [datetime] NOT NULL,
	[ExternalIdentifier] [uniqueidentifier] NOT NULL,
 CONSTRAINT [PK_ARTICLE_READ_281] PRIMARY KEY CLUSTERED
(
	[Id] ASC
)WITH (PAD_INDEX = OFF, STATISTICS_NORECOMPUTE = OFF, IGNORE_DUP_KEY = OFF, ALLOW_ROW_LOCKS = ON, ALLOW_PAGE_LOCKS = ON) ON [PRIMARY]
) ON [PRIMARY]
GO
SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO
CREATE TABLE [ARTICLE_TAG_279](
	[Id] [int] IDENTITY(1,1) NOT NULL,
	[Article_Id] [int] NOT NULL,
	[Value] [nvarchar](100) NOT NULL,
 CONSTRAINT [PK_ARTICLE_TAG_279] PRIMARY KEY CLUSTERED
(
	[Id] ASC
)WITH (PAD_INDEX = OFF, STATISTICS_NORECOMPUTE = OFF, IGNORE_DUP_KEY = OFF, ALLOW_ROW_LOCKS = ON, ALLOW_PAGE_LOCKS = ON) ON [PRIMARY]
) ON [PRIMARY]
GO
SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO
CREATE TABLE [CITATION_290](
	[Id] [int] IDENTITY(1,1) NOT NULL,
	[ReferenceNumber] [nvarchar](5) NOT NULL,
	[Category] [nvarchar](50) NOT NULL,
	[Reference] [nvarchar](max) NULL,
	[Remark] [nvarchar](max) NULL,
	[OfficeAction_Id] [int] NOT NULL,
	[PriorArtDocument_id] [int] NOT NULL,
 CONSTRAINT [PK_CITATION_290] PRIMARY KEY CLUSTERED
(
	[Id] ASC
)WITH (PAD_INDEX = OFF, STATISTICS_NORECOMPUTE = OFF, IGNORE_DUP_KEY = OFF, ALLOW_ROW_LOCKS = ON, ALLOW_PAGE_LOCKS = ON) ON [PRIMARY]
) ON [PRIMARY] TEXTIMAGE_ON [PRIMARY]
GO
SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO
CREATE TABLE [CITATIONSTATUS_293](
	[Id] [int] IDENTITY(1,1) NOT NULL,
	[Date] [datetime] NULL,
	[UserId] [int] NOT NULL,
	[Remark] [nvarchar](max) NOT NULL,
	[Type] [int] NOT NULL,
	[Arendenr] [nvarchar](50) NOT NULL,
	[Citation_Id] [int] NOT NULL,
 CONSTRAINT [PK_CITATIONSTATUS_293] PRIMARY KEY CLUSTERED
(
	[Id] ASC
)WITH (PAD_INDEX = OFF, STATISTICS_NORECOMPUTE = OFF, IGNORE_DUP_KEY = OFF, ALLOW_ROW_LOCKS = ON, ALLOW_PAGE_LOCKS = ON) ON [PRIMARY]
) ON [PRIMARY] TEXTIMAGE_ON [PRIMARY]
GO
SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO
CREATE TABLE [OFFICEACTION_289](
	[Id] [int] IDENTITY(1,1) NOT NULL,
	[Arendenr] [nvarchar](50) NOT NULL,
	[IssueDate] [datetime] NOT NULL,
	[Docid] [int] NULL,
	[Path] [nvarchar](260) NULL,
 CONSTRAINT [PK_OFFICEACTION_289] PRIMARY KEY CLUSTERED
(
	[Id] ASC
)WITH (PAD_INDEX = OFF, STATISTICS_NORECOMPUTE = OFF, IGNORE_DUP_KEY = OFF, ALLOW_ROW_LOCKS = ON, ALLOW_PAGE_LOCKS = ON) ON [PRIMARY]
) ON [PRIMARY]
GO
SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO
CREATE TABLE [PRIORARTDOCUMENT_291](
	[Id] [int] IDENTITY(1,1) NOT NULL,
	[Name] [nvarchar](250) NULL,
	[Patentee] [nvarchar](500) NULL,
	[DocumentCode] [nvarchar](10) NULL,
	[Type] [int] NULL,
	[Landkod] [nvarchar](2) NULL,
	[Inventor] [nvarchar](500) NULL,
	[NameStripped] [nvarchar](500) NULL,
 CONSTRAINT [PK_PRIORARTDOCUMENT_291] PRIMARY KEY CLUSTERED
(
	[Id] ASC
)WITH (PAD_INDEX = OFF, STATISTICS_NORECOMPUTE = OFF, IGNORE_DUP_KEY = OFF, ALLOW_ROW_LOCKS = ON, ALLOW_PAGE_LOCKS = ON) ON [PRIMARY]
) ON [PRIMARY]
GO
SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO
CREATE TABLE [PRIORARTDOCUMENT_DOCUMENTS_292](
	[Id] [int] IDENTITY(1,1) NOT NULL,
	[Docid] [int] NULL,
	[Path] [nvarchar](260) NULL,
	[PriorArtDocument_Id] [int] NOT NULL,
 CONSTRAINT [PK_PRIORARTDOCUMENT_DOCUMENTS_292] PRIMARY KEY CLUSTERED
(
	[Id] ASC
)WITH (PAD_INDEX = OFF, STATISTICS_NORECOMPUTE = OFF, IGNORE_DUP_KEY = OFF, ALLOW_ROW_LOCKS = ON, ALLOW_PAGE_LOCKS = ON) ON [PRIMARY],
 CONSTRAINT [IX_PRIORARTDOCUMENT_DOCUMENTS_292_Docid] UNIQUE NONCLUSTERED
(
	[Docid] ASC,
	[Path] ASC
)WITH (PAD_INDEX = OFF, STATISTICS_NORECOMPUTE = OFF, IGNORE_DUP_KEY = OFF, ALLOW_ROW_LOCKS = ON, ALLOW_PAGE_LOCKS = ON) ON [PRIMARY]
) ON [PRIMARY]
GO
ALTER TABLE [PENDING_TIME_335] ADD  CONSTRAINT [DF_PENDING_TIME_335_ImportStatus]  DEFAULT ((0)) FOR [ImportStatus]
GO
ALTER TABLE [PENDING_TIME_335] ADD  CONSTRAINT [DF_PENDING_TIME_335_Created]  DEFAULT (sysdatetime()) FOR [Created]
GO
ALTER TABLE [KUND_ARENDE_25] ADD  CONSTRAINT [DF_KUND_ARENDE_25_Useaddress]  DEFAULT ((0)) FOR [Useaddress]
GO
