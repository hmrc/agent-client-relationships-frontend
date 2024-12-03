# -------------------------------------------------------------------------------------------
# Common
# -------------------------------------------------------------------------------------------
service.name.agents.auth=Gofyn i gleient eich awdurdodi
service.name.agents.de-auth=Canslo awdurdodiad cleient
service.name.clients=Penodi rhywun i ddelio â CThEM ar eich rhan

authorisation-request.service.name = Gofyn i gleient eich awdurdodi
agent-cancel-authorisation.service.name = Canslo awdurdodiad cleient

error.prefix=Gwall:' '
error.heading=Mae problem wedi codi

continue.button=Yn eich blaen
try-again.button=Rhoi cynnig arall arni
button.copy=<Translation needed>
button.copied=<Translation needed>
start-over.button=Dechrau eto
start-again.button=Dechrau eto

# -------------------------------------------------------------------------------------------
# Timeout/Auth/IV error pages
# -------------------------------------------------------------------------------------------

# Cannot confirm identity
cannot-confirm-identity.header=Nid oeddem yn gallu cadarnhau pwy ydych
cannot-confirm-identity.p1=Nid yw’r wybodaeth yr ydych wedi’i nodi yn cyd-fynd â’n cofnodion.
cannot-confirm-identity.p2=Os oes angen help arnoch i gadarnhau pwy ydych, defnyddiwch y cysylltiad ‘A yw’r dudalen hon yn gweithio’n iawn’.

# Locked-out
locked-out.header=Nid oeddem yn gallu cadarnhau pwy ydych
locked-out.p1=Rydych wedi nodi gwybodaeth nad yw’n cyd-fynd â’n cofnodion gormod o weithiau.
locked-out.p2=Am resymau diogelwch, mae’n rhaid i chi aros 24 awr ac yna mewngofnodi i roi cynnig arall arni.
locked-out.p3=Os oes angen help arnoch i gadarnhau pwy ydych, defnyddiwch y cysylltiad ‘A yw’r dudalen hon yn gweithio’n iawn’.

#Timed out
timed-out.header=Rydych wedi cael eich allgofnodi
timed-out.p1=Nid ydych wedi gwneud dim byd ers {0}, felly rydym wedi’ch allgofnodi er mwyn cadw’ch cyfrif yn ddiogel.
timed-out.p2.link=Mewngofnodwch eto
timed-out.p2.end=i ddefnyddio’r gwasanaeth hwn.
timed-out.button=Dechrau eto
timed-out.minutes=munud
timed-out.seconds=eiliad

# Technical issues
technical-issues.header=Mae’n ddrwg gennym, mae problem gyda’r gwasanaeth
technical-issues.p1=Rhowch gynnig arall arni yn nes ymlaen.
technical-issues.p2=Efallai nad ydym wedi cadw’ch atebion. Pan fydd y gwasanaeth ar gael, efallai y bydd yn rhaid i chi ddechrau eto.
technical-issues.sa-url=https://www.gov.uk/government/organisations/hm-revenue-customs/contact/welsh-language-helplines
technical-issues.vat-url= https://www.gov.uk/government/organisations/hm-revenue-customs/contact/vat-customs-and-excise-and-duties-enquiries-for-welsh-speaking-customers
technical-issues.vat.link=Ffoniwch linell Ymholiadau TAW, Tollau ac Ecséis CThEM
technical-issues.vat.end=os oes angen help arnoch gyda’r cynllun TAW.
technical-issues.it.link=Ffoniwch Wasanaeth Cwsmeriaid Cymraeg CThEM
technical-issues.it.end=os oes angen help arnoch gyda’r cynllun Troi Treth yn Ddigidol ar gyfer Treth Incwm.

#Not Authorised
not-authorised.header=Nid oes gennych fynediad at y dudalen hon
not-authorised.description.p1=Dim ond eich cleient all fynd at y dudalen hon.
not-authorised.description.p2=Gofynnwch i’ch cleient ddilyn y cysylltiad yr oeddech yn ceisio’i ddefnyddio er mwyn iddo allu derbyn eich cais am awdurdodiad.
not-authorised.button=Allgofnodi

#Error Cannot View Request
error.cannot-view-request.header=Ni allwch fwrw golwg dros y cais am awdurdodiad hwn
error.cannot-view-request.p1=Rydych wedi mewngofnodi gan ddefnyddio Dynodydd Defnyddiwr (ID) asiant.
error.cannot-view-request.p2=Os mai chi yw’r asiant, gofynnwch i’ch cleient ddilyn y cysylltiad er mwyn ymateb i’r cais am awdurdodiad.
error.cannot-view-request.p3=Os nad ydych yn asiant, mewngofnodwch gyda’r Dynodydd Defnyddiwr (ID) ar gyfer Porth y Llywodraeth rydych yn ei ddefnyddio ar gyfer eich {0}.
error.cannot-view-request.client-type.business=materion treth busnes
error.cannot-view-request.client-type.personal=materion treth personol
error.cannot-view-request.button=Mewngofnodi

# -------------------------------------------------------------------------------------------
# Select Client Type
# -------------------------------------------------------------------------------------------
clientType.authorisation-request.header=Gan ba fath o gleient y mae angen awdurdodiad arnoch?
clientType.agent-cancel-authorisation.header=Ar gyfer pa fath o gleient yr hoffech ganslo’ch awdurdodiad?
clientType.personal=Unigolyn neu unig fasnachwr
clientType.business=Cwmni neu bartneriaeth
clientType.trust=Ymddiriedolaeth neu ystâd
clientType.authorisation-request.error.required=Dewiswch y math o gleient rydych angen awdurdodiad ganddo
clientType.agent-cancel-authorisation.error.required=Dewiswch y math o gleient y mae angen i chi ganslo’ch awdurdodiad ar ei gyfer


# -------------------------------------------------------------------------------------------
# Select Client Service
# -------------------------------------------------------------------------------------------
clientService.authorisation-request.header=Beth yr ydych am i’r cleient eich awdurdodi i’w wneud?
clientService.agent-cancel-authorisation.header=<translation needed>
clientService.hint=<translation needed>
clientService.alternative=Mae angen awdurdodiad arnaf ar gyfer rhywbeth arall
clientService.alt-suggestion=Dysgwch ragor am
clientService.alt-suggestion.link=aofyn i gleient eich awdurdodi i ddelio â gwasanaethau treth eraill (yn agor tab newydd)
clientService.authorisation-request.error.required=Dewiswch yr hyn rydych am i’r cleient eich awdurdodi i’w wneud
clientService.agent-cancel-authorisation.error.required=<translation needed>

clientService.PERSONAL-INCOME-RECORD.personal=Bwrw golwg dros ei gofnod incwm
clientService.HMRC-MTD-IT.personal=Rheoli ei gyfrif Troi Treth yn Ddigidol ar gyfer Treth Incwm
clientService.HMRC-PPT-ORG.personal=Rheoli ei Dreth Deunydd Pacio Plastig
clientService.HMRC-PPT-ORG.business=Rheoli ei Dreth Deunydd Pacio Plastig
clientService.HMRC-PPT-ORG.trust=Rheoli ei Treth Deunydd Pacio Plastig ymddiriedolaeth
clientService.HMRC-CGT-PD.personal=Rheoli ei gyfrif Treth Enillion Cyfalaf ar eiddo yn y DU
clientService.HMRC-CGT-PD.trust=Rheoli ei cyfrif Treth Enillion Cyfalaf ar eiddo yn y DU ar gyfer ymddiriedolaeth
clientService.HMRC-CBC-ORG.personal=
clientService.HMRC-CBC-ORG.business=Rheoli ei adroddiadau gwlad-wrth-wlad
clientService.HMRC-CBC-ORG.trust=Rheoli ei adroddiadau gwlad-wrth-wlad
clientService.HMRC-MTD-VAT.personal=<Translation needed>
clientService.HMRC-MTD-VAT.business=Rheoli ei TAW
clientService.HMRC-PILLAR2-ORG.business=Rheoli ei drethi atodol Colofn 2
clientService.HMRC-PILLAR2-ORG.trust=Rheoli ei drethi atodol Colofn 2
clientService.HMRC-TERS-ORG.trust=Cynnal ei ymddiriedolaeth neu ystâd
clientService.HMRC-TERSNT-ORG.trust=<translation needed>

# -------------------------------------------------------------------------------------------
# Client service refinement
# -------------------------------------------------------------------------------------------

clientServiceRefinement.HMRC-TERS-ORG.header=<Translation needed>
clientServiceRefinement.HMRC-TERSNT-ORG.header=<Translation needed>
clientServiceRefinement.HMRC-TERS-ORG.option=<Translation needed>
clientServiceRefinement.HMRC-TERSNT-ORG.option=<Translation needed>

# -------------------------------------------------------------------------------------------
# Enter Client ID
# -------------------------------------------------------------------------------------------

clientId.nino.label=<Translation needed>
clientId.nino.hint=<Translation needed>
clientId.nino.error.invalid=<Translation needed>
clientId.nino.error.required=<Translation needed>

clientId.vrn.label=<Translation needed>
clientId.vrn.hint=<Translation needed>
clientId.vrn.error.invalid=<Translation needed>
clientId.vrn.error.required=<Translation needed>

clientId.utr.label=<Translation needed>
clientId.utr.hint=<Translation needed>
clientId.utr.error.invalid=<Translation needed>
clientId.utr.error.required=<Translation needed>

clientId.urn.label=<Translation needed>
clientId.urn.hint=<Translation needed>
clientId.urn.error.invalid=<Translation needed>
clientId.urn.error.required=<Translation needed>

clientId.cgtRef.label=<Translation needed>
clientId.cgtRef.hint=T<Translation needed>
clientId.cgtRef.error.invalid=<Translation needed>
clientId.cgtRef.error.required=<Translation needed>

clientId.pptRef.label=<Translation needed>
clientId.pptRef.hint=<Translation needed>
clientId.pptRef.error.invalid=<Translation needed>
clientId.pptRef.error.required=<Translation needed>

clientId.cbcId.label=<Translation needed>
clientId.cbcId.hint=<Translation needed>
clientId.cbcId.error.invalid=<Translation needed>
clientId.cbcId.error.required=<Translation needed>

clientId.PlrId.label=<Translation needed>
clientId.PlrId.hint=<Translation needed>
clientId.PlrId.error.invalid=<Translation needed>
clientId.PlrId.error.required=<Translation needed>

#-------------------------------------------------------------------------------------------
# Enter Client Fact
# -------------------------------------------------------------------------------------------

clientFact.HMRC-MTD-IT.postcode.label=<translation needed>
clientFact.HMRC-MTD-IT.postcode.hint=<translation needed>
clientFact.HMRC-MTD-IT.postcode.error.required=<translation needed>
clientFact.HMRC-MTD-IT.postcode.error.invalid=<translation needed>

clientFact.PERSONAL-INCOME-RECORD.date.label=Beth yw dyddiad geni eich cleient?
clientFact.PERSONAL-INCOME-RECORD.date.hint=Er enghraifft, 22 7 1981.
clientFact.PERSONAL-INCOME-RECORD.date.error.required=Nodwch ddyddiad geni’ch cleient
clientFact.PERSONAL-INCOME-RECORD.date.error.invalid=Mae’n rhaid i’r dyddiad geni fod yn ddyddiad dilys
clientFact.PERSONAL-INCOME-RECORD.date.error.day.required=Mae’n rhaid i’r dyddiad geni gynnwys diwrnod
clientFact.PERSONAL-INCOME-RECORD.date.error.month.required=Mae’n rhaid i’r dyddiad geni gynnwys mis
clientFact.PERSONAL-INCOME-RECORD.date.error.year.required=Mae’n rhaid i’r dyddiad geni gynnwys blwyddyn
clientFact.PERSONAL-INCOME-RECORD.date.error.day-month.required=Mae’n rhaid i’r dyddiad geni gynnwys diwrnod a mis
clientFact.PERSONAL-INCOME-RECORD.date.error.day-year.required=Mae’n rhaid i’r dyddiad geni gynnwys diwrnod a blwyddyn
clientFact.PERSONAL-INCOME-RECORD.date.error.month-year.required=Mae’n rhaid i’r dyddiad geni gynnwys mis a blwyddyn

clientFact.HMRC-MTD-VAT.date.label=<translation needed>
clientFact.HMRC-MTD-VAT.date.hint=<translation needed>
clientFact.HMRC-MTD-VAT.date.error.required=Nodwch ddyddiad cofrestru TAW eich cleient
clientFact.HMRC-MTD-VAT.date.error.invalid=Nodwch ddyddiad cofrestru TAW dilys
clientFact.HMRC-MTD-VAT.date.error.day.required=Mae’n rhaid i’r dyddiad cofrestru TAW gynnwys diwrnod
clientFact.HMRC-MTD-VAT.date.error.month.required=Mae’n rhaid i’r dyddiad cofrestru TAW gynnwys mis
clientFact.HMRC-MTD-VAT.date.error.year.required=Mae’n rhaid i’r dyddiad cofrestru TAW gynnwys blwyddyn
clientFact.HMRC-MTD-VAT.date.error.day-month.required=Mae’n rhaid i’r dyddiad cofrestru TAW gynnwys diwrnod a mis
clientFact.HMRC-MTD-VAT.date.error.day-year.required=Mae’n rhaid i’r dyddiad cofrestru TAW gynnwys diwrnod a blwyddyn
clientFact.HMRC-MTD-VAT.date.error.month-year.required=Mae’n rhaid i’r dyddiad cofrestru TAW gynnwys mis a blwyddyn

clientFact.HMRC-CGT-PD.postcode.label=<translation needed>
clientFact.HMRC-CGT-PD.countryCode.label=Beth yw gwlad cyfeiriad cyswllt eich cleient?
clientFact.HMRC-CGT-PD.countryCode.hint=Mae’n rhaid i hwn gyd-fynd â gwlad cyfeiriad cyswllt eich cleient yn ei gyfrif Treth Enillion Cyfalaf ar eiddo yn y DU. Dechreuwch deipio enw’r wlad.
clientFact.HMRC-CGT-PD.countryCode.error.required=Nodwch wlad cyfeiriad cyswllt eich cleient

clientFact.HMRC-PPT-ORG.date.label=<translation needed>
clientFact.HMRC-PPT-ORG.date.hint=Er enghraifft, 31 8 2022.
clientFact.HMRC-PPT-ORG.date.error.required=Nodwch ddyddiad cofrestru eich cleient ar gyfer Treth Deunydd Pacio Plastig
clientFact.HMRC-PPT-ORG.date.error.invalid=Nodwch ddyddiad cofrestru ar gyfer Treth Deunydd Pacio Plastig sy’n ddilys
clientFact.HMRC-PPT-ORG.date.error.day.required=Mae’n rhaid i’r dyddiad cofrestru ar gyfer Treth Deunydd Pacio Plastig gynnwys diwrnod
clientFact.HMRC-PPT-ORG.date.error.month.required=Mae’n rhaid i’r dyddiad cofrestru ar gyfer Treth Deunydd Pacio Plastig gynnwys mis
clientFact.HMRC-PPT-ORG.date.error.year.required=Mae’n rhaid i’r dyddiad cofrestru ar gyfer Treth Deunydd Pacio Plastig gynnwys blwyddyn
clientFact.HMRC-PPT-ORG.date.error.day-month.required=Mae’n rhaid i’r dyddiad cofrestru ar gyfer Treth Deunydd Pacio Plastig gynnwys diwrnod a mis
clientFact.HMRC-PPT-ORG.date.error.day-year.required=Mae’n rhaid i’r dyddiad cofrestru ar gyfer Treth Deunydd Pacio Plastig gynnwys diwrnod a blwyddyn
clientFact.HMRC-PPT-ORG.date.error.month-year.required=Mae’n rhaid i’r dyddiad cofrestru ar gyfer Treth Deunydd Pacio Plastig gynnwys mis a blwyddyn

clientFact.HMRC-CBC-ORG.email.label=<translation needed>
clientFact.HMRC-CBC-ORG.email.hint=<translation needed>
clientFact.HMRC-CBC-ORG.email.error.invalid=<translation needed>
clientFact.HMRC-CBC-ORG.email.error.required=<translation needed>

clientFact.HMRC-CBC-NONUK-ORG.email.label=<translation needed>
clientFact.HMRC-CBC-NONUK-ORG.email.hint=<translation needed>
clientFact.HMRC-CBC-NONUK-ORG.email.error.invalid=<translation needed>
clientFact.HMRC-CBC-NONUK-ORG.email.error.required=<translation needed>

clientFact.HMRC-PILLAR2-ORG.date.label=<translation needed>
clientFact.HMRC-PILLAR2-ORG.date.hint=<translation needed>
clientFact.HMRC-PILLAR2-ORG.date.error.required=Nodwch ddyddiad cofrestru eich cleient ar gyfer rhoi gwybod am drethi atodol Colofn 2
clientFact.HMRC-PILLAR2-ORG.date.error.invalid=Nodwch ddyddiad cofrestru ar gyfer trethi atodol Colofn 2 eich cleient
clientFact.HMRC-PILLAR2-ORG.date.error.day.required=Mae’n rhaid i’r dyddiad cofrestru ar gyfer trethi atodol Colofn 2 gynnwys diwrnod
clientFact.HMRC-PILLAR2-ORG.date.error.month.required=Mae’n rhaid i’r dyddiad cofrestru ar gyfer trethi atodol Colofn 2 gynnwys mis
clientFact.HMRC-PILLAR2-ORG.date.error.year.required=Mae’n rhaid i’r dyddiad cofrestru ar gyfer trethi atodol Colofn 2 gynnwys blwyddyn
clientFact.HMRC-PILLAR2-ORG.date.error.day-month.required=Mae’n rhaid i’r dyddiad cofrestru ar gyfer trethi atodol Colofn 2 gynnwys diwrnod a mis
clientFact.HMRC-PILLAR2-ORG.date.error.day-year.required=Mae’n rhaid i’r dyddiad cofrestru ar gyfer trethi atodol Colofn 2 gynnwys diwrnod a blwyddyn
clientFact.HMRC-PILLAR2-ORG.date.error.month-year.required=Mae’n rhaid i’r dyddiad cofrestru ar gyfer trethi atodol Colofn 2 gynnwys mis a blwyddyn

# ________________________________________________________________________________
# Confirm Client
# ________________________________________________________________________________

confirmClient.title=Ai hwn yw’r cleient yr hoffech gael awdurdodiad ganddo?
confirmClient.authorisation-request.header=Ai {0} yw’r cleient rydych am awdurdodiad ganddo?
confirmClient.agent-cancel-authorisation.header=<translation needed>
confirmClient.authorisation-request.error.required=Dewiswch ‘Iawn’ os hoffech ofyn i’r cleient hwn am awdurdodiad
confirmClient.agent-cancel-authorisation.error.required=<translation needed> 
confirmClient.true=Iawn
confirmClient.false=Na – mae angen i mi ddechrau eto

# ________________________________________________________________________________
# Select agent role
# ________________________________________________________________________________

selectAgentRole.HMRC-MTD-IT.newRelationship.header=<translation needed>
selectAgentRole.HMRC-MTD-IT.newRelationship.legend=<translation needed>
selectAgentRole.HMRC-MTD-IT.newRelationship.guidance=<translation needed>
selectAgentRole.HMRC-MTD-IT.newRelationship.option=<translation needed>
selectAgentRole.HMRC-MTD-IT-SUPP.newRelationship.option=<translation needed>
selectAgentRole.HMRC-MTD-IT.newRelationship.hint=<translation needed>
selectAgentRole.HMRC-MTD-IT-SUPP.newRelationship.hint=<translation needed>
agentRole.authorisation-request.error.required=<translation needed>

selectAgentRole.HMRC-MTD-IT.mainToSupporting.header=<translation needed>
selectAgentRole.HMRC-MTD-IT.mainToSupporting.intro=<translation needed>
selectAgentRole.HMRC-MTD-IT.mainToSupporting.guidance=<translation needed>
selectAgentRole.HMRC-MTD-IT.mainToSupporting.legend=<translation needed>
selectAgentRole.HMRC-MTD-IT.mainToSupporting.option=<translation needed>
selectAgentRole.HMRC-MTD-IT-SUPP.mainToSupporting.option=<translation needed>
selectAgentRole.HMRC-MTD-IT-SUPP.mainToSupporting.hint=<translation needed>

selectAgentRole.HMRC-MTD-IT.supportingToMain.header=<translation needed>
selectAgentRole.HMRC-MTD-IT.supportingToMain.intro=<translation needed>
selectAgentRole.HMRC-MTD-IT.supportingToMain.guidance=<translation needed>
selectAgentRole.HMRC-MTD-IT.supportingToMain.legend=<translation needed>
selectAgentRole.HMRC-MTD-IT.supportingToMain.option=<translation needed>
selectAgentRole.HMRC-MTD-IT-SUPP.supportingToMain.option=<translation needed>
selectAgentRole.HMRC-MTD-IT.supportingToMain.hint=<translation needed>

# ________________________________________________________________________________
# Check Your Answers
# ________________________________________________________________________________
checkYourAnswers.header=Check your answers
checkYourAnswers.h2=Authorisation details for {0}
checkYourAnswers.clientService.label=What do you want to do for the client
checkYourAnswers.agentType.label=How do you want to act for them

checkYourAnswers.confirmAndSend.button=Confirm and send
checkYourAnswers.change=Change

checkYourAnswers.PERSONAL-INCOME-RECORD=View their Income record
checkYourAnswers.HMRC-MTD-IT=Manage their Making Tax Digital for Income Tax
checkYourAnswers.HMRC-PPT-ORG=Manage their Plastic Packaging Tax
checkYourAnswers.HMRC-CGT-PD=Manage their Capital Gains Tax on UK property account
checkYourAnswers.HMRC-CBC-ORG=Manage their country-by-country reports
checkYourAnswers.HMRC-CBC-ORG=Manage their country-by-country reports
checkYourAnswers.HMRC-MTD-VAT=Manage their VAT
checkYourAnswers.HMRC-PILLAR2-ORG=Manage their Pillar 2 top-up taxes
checkYourAnswers.HMRC-TERS-ORG=Maintain their trust or an estate

# ________________________________________________________________________________
# Agent confirm cancellation of authorisation
# ________________________________________________________________________________

confirmCancellation.header=Confirm cancellation
confirmCancellation.legend=Do you want to cancel your authorisation for this client?
confirmCancellation.true=Yes
confirmCancellation.false=No - I need to start again
confirmCancellation.agent-cancel-authorisation.error.required=Select ‘yes’ if you want to cancel authorisation for this client

confirmCancellation.HMRC-MTD-IT.current-status=You are currently authorised as the main agent to manage {0}’s Making Tax Digital for Income Tax.
confirmCancellation.HMRC-MTD-IT-SUPP.current-status=You are currently authorised as a supporting agent to manage {0}’s Making Tax Digital for Income Tax.
confirmCancellation.PERSONAL-INCOME-RECORD.current-status=You are currently authorised as the agent to view the Income record for {0}.
confirmCancellation.HMRC-MTD-VAT.current-status=You are currently authorised as the agent to manage {0}’s VAT.
confirmCancellation.HMRC-TERS-ORG.current-status=You are currently authorised as the agent to maintain {0}’s trust or an estate.
confirmCancellation.HMRC-TERSNT-ORG.current-status=You are currently authorised as the agent to maintain {0}’s trust or an estate.
confirmCancellation.HMRC-CGT-PD.current-status=You are currently authorised as the agent to manage {0}’s Capital Gains Tax on UK property account.
confirmCancellation.HMRC-PPT-ORG.current-status=You are currently authorised as the agent to manage Plastic Packaging Tax for {0}.
confirmCancellation.HMRC-CBC-ORG.current-status=You are currently authorised as the agent to manage country-by-country reports for {0}.
confirmCancellation.HMRC-CBC-NONUK-ORG.current-status=You are currently authorised as the agent to manage {0}’s country-by-country reports.
confirmCancellation.HMRC-PILLAR2-ORG.current-status=You are currently authorised as the agent to manage {0}’s Pillar 2 top-up taxes.

confirmCancellation.HMRC-MTD-IT.outcome=If you cancel your authorisation, you will not be able to manage Making Tax Digital for Income Tax for {0}.
confirmCancellation.PERSONAL-INCOME-RECORD.outcome=If you cancel this request, you will not be able to view the Income record for this client.
confirmCancellation.HMRC-MTD-VAT.outcome=If you cancel your authorisation, you will not be able to manage their VAT for {0}.
confirmCancellation.HMRC-TERS-ORG.outcome=If you cancel your authorisation, you will not be able to maintain a trust or an estate on behalf of {0}.
confirmCancellation.HMRC-TERSNT-ORG.outcome=If you cancel your authorisation, you will not be able to maintain a trust or an estate on behalf of {0}.
confirmCancellation.HMRC-CGT-PD.outcome=If you cancel your authorisation, you will not be able to manage Capital Gains Tax on UK property account for {0}.
confirmCancellation.HMRC-PPT-ORG.outcome=If you cancel your authorisation, you will not be able to manage Plastic Packaging Tax for {0}.
confirmCancellation.HMRC-CBC-ORG.outcome=If you cancel your authorisation, you will not be able to manage country-by-country reports for {0}.
confirmCancellation.HMRC-CBC-NONUK-ORG.outcome=If you cancel your authorisation, you will not be able to manage The Trust of {0} country-by-country reports.
confirmCancellation.HMRC-PILLAR2-ORG.outcome=If you cancel your authorisation, you will not be able to manage {0}’s Pillar 2 top-up taxes.

confirmCancellation.HMRC-MTD-IT.inset=Cancelling this authorisation will not automatically cancel any authorisation for this client for Self Assessment.

# ________________________________________________________________________________
# Invitation Created
# ________________________________________________________________________________

invitationCreated.header=You''ve created an authorisation request
invitationCreated.panel.body=Client: {0}
invitationCreated.h2=Next steps
invitationCreated.list.one=Copy this authorisation request link and send it to your client:
invitationCreated.list.two=Tell your client to select this link. They will then be asked to sign in.
invitationCreated.list.three=Ask your client to authorise you by {0} – they have {1} days to respond before your request expires.
invitationCreated.list.four=We will email you at {0} to update you on the status of this request.
invitationCreated.createAnother=Create another authorisation request
invitationCreated.agentHome=Go to your agent services account homepage

# ________________________________________________________________________________
#Track requests
# ________________________________________________________________________________

trackRequests.title=Rheoli eich ceisiadau diweddar am awdurdodiad
trackRequests.intro=Gwirio statws eich ceisiadau am awdurdodiad i gleientiaid yn ystod y {0} o ddiwrnodau diwethaf, neu ganslo ceisiadau ac awdurdodiadau nad oes eu hangen mwyach.
trackRequests.empty=Nid oes gennych unrhyw geisiadau diweddar am awdurdodiad.
trackRequests.view=
trackRequests.action.resend=Resend link
trackRequests.action.cancel=Cancel request
trackRequests.action.deauth=Deauthorise
trackRequests.empty.continue=Dechrau cais am awdurdodiad newydd
trackRequests.th.client=Manylion y cleient
trackRequests.table-row-header.clientNameUnknown=Methu adfer enw cleient
trackRequests.th.service=Cais am awdurdodiad wedi’i anfon
trackRequests.th.status=Statws
trackRequests.th.expiryDate=
trackRequests.th.actions=Camau gweithredu
trackRequests.resultsCaption=Showing {0} to {1} of {2} requests
trackRequests.service.HMRC-MTD-IT=Rheoli ei gyfrif Troi Treth yn Ddigidol ar gyfer Treth Incwm
trackRequests.service.PERSONAL-INCOME-RECORD=Bwrw golwg dros ei gofnod incwm
trackRequests.service.HMRC-MTD-VAT=Rheoli ei TAW
trackRequests.service.HMRC-TERS-ORG=Cynnal ymddiriedolaeth neu ystâd
trackRequests.service.HMRC-TERSNT-ORG=Cynnal ymddiriedolaeth neu ystâd
trackRequests.service.HMRC-CGT-PD=Rheoli ei gyfrif Treth Enillion Cyfalaf ar eiddo yn y DU
trackRequests.service.HMRC-PPT-ORG=Rheoli ei Dreth Deunydd Pacio Plastig
trackRequests.service.HMRC-CBC-ORG=Rheoli ei adroddiadau gwlad-wrth-wlad
trackRequests.service.HMRC-CBC-NONUK-ORG=Rheoli ei adroddiadau gwlad-wrth-wlad
trackRequests.service.HMRC-CBC=Rheoli adroddiadau gwlad-wrth-wlad
trackRequests.service.HMRC-PILLAR2-ORG=Rheoli ei drethi atodol Colofn 2

trackRequests.invitation.expires=Yn dod i ben:
trackRequests.status.Pending=Nid yw’r cleient wedi ymateb eto
trackRequests.status.Accepted=Derbyniwyd gan y cleient
trackRequests.status.Partialauth=Derbyniwyd gan y cleient
trackRequests.status.Rejected=Gwrthodwyd gan y cleient
trackRequests.status.Expired=Daeth y cais i ben gan i’r cleient fethu ag ymateb mewn pryd
trackRequests.status.Cancelled=Gwnaethoch ganslo’r cais hwn
trackRequests.sidebar.clientFilter=Filter by client
trackRequests.sidebar.statusFilter=Filter by status
trackRequests.sidebar.applyFilters=Apply filters
trackRequests.filter-status.selectAll=Select all
trackRequests.filter-status.ExpireInNext5Days=Expire in the next 5 days
trackRequests.filter-status.ActivityWithinLast5Days=Activity within the last 5 days
trackRequests.filter-status.ClientNotYetResponded=Client has not yet responded
trackRequests.filter-status.AgentCancelledAuthorisation=You cancelled your authorisation
trackRequests.filter-status.DeclinedByClient=Declined by client
trackRequests.filter-status.AcceptedByClient=Accepted by client
trackRequests.filter-status.Expired=Request expired as client did not respond in time
trackRequests.filter-status.ClientCancelledAuthorisation=Accepted by client. They later cancelled their authorisation
trackRequests.filter-status.HMRCCancelledAuthorisation=Accepted by client. HMRC later cancelled your authorisation
trackRequests.filter-status.Declined.HMRC-CBC={0} request to manage the clie


# ________________________________________________________________________________
# Journey exit headings
# ________________________________________________________________________________

# Agent Journey

journeyExit.client-not-found.header=Nid oeddem yn gallu dod o hyd i’ch cleient
journeyExit.not-registered.header=Mae angen i’ch cleient gofrestru ar gyfer Hunanasesiad
journeyExit.not-authorised.header=Nid ydych wedi eich awdurdodi
journeyExit.pending-authorisation-exists.header=Rydych eisoes wedi creu cais am awdurdodiad ar gyfer y gwasanaeth treth hwn
journeyExit.client-insolvent.header=Ni allwch greu cais am awdurdodiad gan fod y cleient hwn yn ansolfent
journeyExit.authorisation-already-exists.header=Rydych eisoes wedi’ch awdurdodi
journeyExit.no-change-of-agent-role.header=<translation needed>

# Client Journey

clientExit.agent-suspended.header=<translation needed>
clientExit.no-outstanding-requests.header=<translation needed>
clientExit.cannot-find-authorisation-request.header=<translation needed>
clientExit.authorisation-request-expired.header=<translation needed>
clientExit.authorisation-request-already-responded-to.header=<translation needed>
clientExit.authorisation-request-cancelled.header=<translation needed>

# ________________________________________________________________________________
# Agent Journey exit partials
# ________________________________________________________________________________

genericNotFound.description=Nid oeddem yn gallu dod o hyd i gleient yn ein cofnodion gan ddefnyddio’r manylion a roesoch i ni.
genericNotFound.advice=Gwiriwch y manylion a rhowch gynnig arall arni.

# ________________________________________________________________________________

clientNotRegistered.p1=Ni allwch greu cais am awdurdodiad ar gyfer y cleient hwn hyd nes ei fod wedi cofrestru ar gyfer Hunanasesiad a bod ganddo Gyfeirnod Unigryw y Trethdalwr ar gyfer Hunanasesiad.
clientNotRegistered.h2=Yr hyn i’w wneud nesaf
clientNotRegistered.p2=Gofynnwch i’ch cleient <a href="{0}">gofrestru ar gyfer Hunanasesiad</a>. Ar Ã´l iddo gofrestru, bydd yn cael Cyfeirnod Unigryw y Trethdalwr ar gyfer Hunanasesiad.
clientNotRegistered.p3=Wedyn, gallwch <a href="{0}">greu cais am awdurdodiad i’w anfon ato</a>.
clientNotRegistered.return=Dychwelyd i’ch ceisiadau am awdurdodiad

# ________________________________________________________________________________
# Not authorised to de-auth
# ________________________________________________________________________________
notAuthorised.HMRC-MTD-IT.p=Nid yw’r cleient hwn wedi’ch awdurdodi i reoli ei gyfrif Troi Treth yn Ddigidol ar gyfer Treth Incwm.
notAuthorised.PERSONAL-INCOME-RECORD.p=Nid yw’r cleient hwn wedi’ch awdurdodi i fwrw golwg dros ei gofnod incwm.
notAuthorised.HMRC-MTD-VAT.p=Nid yw’r cleient hwn wedi’ch awdurdodi i reoli ei TAW.
notAuthorised.HMRC-TERS-ORG.p=Nid yw’r cleient hwn wedi’ch awdurdodi i gynnal ymddiriedolaeth nac ystâd.
notAuthorised.HMRC-TERSNT-ORG.p=Nid yw’r cleient hwn wedi’ch awdurdodi i gynnal ymddiriedolaeth nac ystâd.
notAuthorised.HMRC-CGT-PD.p=Nid yw’r cleient hwn wedi’ch awdurdodi i reoli ei Dreth Enillion Cyfalaf ar warediadau eiddo yn y DU.
notAuthorised.HMRC-PPT-ORG.p=Nid yw’r cleient hwn wedi’ch awdurdodi i reoli ei Dreth Deunydd Pacio Plastig.
notAuthorised.HMRC-CBC-ORG.p=Nid yw’r cleient hwn wedi’ch awdurdodi i reoli ei adroddiadau gwlad-wrth-wlad.
notAuthorised.HMRC-CBC-NONUK-ORG.p=Nid yw’r cleient hwn wedi’ch awdurdodi i reoli ei adroddiadau gwlad-wrth-wlad.

# ________________________________________________________________________________
# Pending authorisation already exists
# ________________________________________________________________________________

clientAlreadyInvited.p=Ni allwch fynd yn eich blaen hyd nes bod {0} wedi derbyn y cysylltiad at y cais am awdurdodiad.
clientAlreadyInvited.p2=Ail-anfonwch y cysylltiad at y cais am awdurdodiad a grÃ«wyd pan wnaethoch ofyn i {0} eich awdurdodi yn y lle cyntaf:
clientAlreadyInvited.h2=Yr hyn y gallwch ei wneud nesaf

# ________________________________________________________________________________
# Client insolvent
# ________________________________________________________________________________

clientInsolvent.p=Ni all cleientiaid awdurdodi asiant ar eu rhan pan fyddan nhw’n ansolfent.

# ________________________________________________________________________________
# Active authorisation already exists
# ________________________________________________________________________________

authorisationAlreadyExists.HMRC-MTD-IT.p1=Mae’r cleient hwn eisoes wedi’ch awdurdodi i reoli ei gyfrif Troi Treth yn Ddigidol ar gyfer Treth Incwm.
authorisationAlreadyExists.PERSONAL-INCOME-RECORD.p1=Mae’r cleient hwn eisoes wedi’ch awdurdodi i fwrw golwg dros ei gofnod incwm.
authorisationAlreadyExists.HMRC-MTD-VAT.p1=Mae’r cleient hwn eisoes wedi’ch awdurdodi i reoli ei TAW.
authorisationAlreadyExists.HMRC-TERS-ORG.p1=Mae’r cleient hwn eisoes wedi’ch awdurdodi i gynnal ymddiriedolaeth neu ystâd.
authorisationAlreadyExists.HMRC-TERSNT-ORG.p1=Mae’r cleient hwn eisoes wedi’ch awdurdodi i gynnal ymddiriedolaeth neu ystâd.
authorisationAlreadyExists.HMRC-CGT-PD.p1=Mae’r cleient hwn eisoes wedi’ch awdurdodi i reoli ei Dreth Enillion Cyfalaf ar warediadau eiddo yn y DU.
authorisationAlreadyExists.HMRC-PPT-ORG.p1=Mae’r cleient hwn eisoes wedi’ch awdurdodi i reoli ei Dreth Deunydd Pacio Plastig.
authorisationAlreadyExists.HMRC-CBC-ORG.p1=<translation needed>
authorisationAlreadyExists.HMRC-CBC-NONUK-ORG.p1=<translation needed>
authorisationAlreadyExists.HMRC-PILLAR2-ORG.p1=<translation needed>
authorisationAlreadyExists.p2=Nid oes yn rhaid i chi ofyn iddo eich awdurdodi ar gyfer y gwasanaeth hwn eto.
authorisationAlreadyExists.return=<translation needed>

# ________________________________________________________________________________
# Authorise agent start page
# ________________________________________________________________________________

authoriseAgentStartPage.h1.income-tax=Authorise {0} to manage your Income Tax
authoriseAgentStartPage.h1.income-record-viewer=Authorise {0} to view your Income Record
authoriseAgentStartPage.h1.vat=Authorise {0} to manage your VAT
authoriseAgentStartPage.h1.capital-gains-tax-uk-property=Authorise {0} to manage your Capital Gains Tax on UK property account
authoriseAgentStartPage.h1.plastic-packaging-tax=Authorise {0} to manage your Plastic Packaging Tax
authoriseAgentStartPage.h1.country-by-country-reporting=Authorise {0} to manage your Country-by-country Reports
authoriseAgentStartPage.h1.pillar-2=Authorise {0} to manage your Pillar 2 top-up taxes
authoriseAgentStartPage.h1.trusts-and-estates=Authorise {0} to maintain your Trust or an Estate

authoriseAgentStartPage.p1.income-tax=You need to sign in with the user ID you use for Income Tax.
authoriseAgentStartPage.p1.income-record-viewer=You need to sign in with the user ID you use for your personal tax account.
authoriseAgentStartPage.p1.vat=You need to sign in with the user ID you use for VAT.
authoriseAgentStartPage.p1.capital-gains-tax-uk-property=You need to sign in with the user ID you use for Capital Gains Tax on UK property account.
authoriseAgentStartPage.p1.plastic-packaging-tax=You need to sign in with the user ID you use for Plastic Packaging Tax.
authoriseAgentStartPage.p1.country-by-country-reporting=You need to sign in with the user ID you use for Country-by-country Reports.
authoriseAgentStartPage.p1.pillar-2=You need to sign in with the user ID you use for Pillar 2 top-up taxes.
authoriseAgentStartPage.p1.trusts-and-estates=You need to sign in with the user ID you use for maintaining your trust or estate.
authoriseAgentStartPage.p2=If you do not have sign in details, you‘ll be able to create some.
authoriseAgentStartPage.link.text1=Start now
authoriseAgentStartPage.link.text2=I do not want {0} to act for me.

# ________________________________________________________________________________
# Client journey exit partials
# ________________________________________________________________________________

# ________________________________________________________________________________
# You cannot appoint this tax agent (Agent suspended)
# ________________________________________________________________________________
agentSuspended.p1=This tax agent cannot manage your Making Tax Digital for Income Tax at this time.
agentSuspended.p2=If you have any questions, contact the tax agent who sent you this request.
agentSuspended.signout-link=Finish and sign out

# ________________________________________________________________________________
# There are no outstanding authorisation requests for you to respond to
# ________________________________________________________________________________
noOutstandingRequests.p1= If you think this is wrong, contact the agent who sent you the request or <a href={0} class="govuk-link" target="_blank" rel="noreferrer noopener">view your request history</a>.

# ________________________________________________________________________________
# This authorisation request has already expired
# ________________________________________________________________________________
authorisationRequestExpired.p1=This request expired on {0}. For details, <a href={1} class="govuk-link" target="_blank" rel="noreferrer noopener">view your history</a> to check for any expired, cancelled or outstanding requests.
authorisationRequestExpired.p2=If your agent has sent you a recent request, <a href={0} class="govuk-link" target="_blank" rel="noreferrer noopener">make sure you have signed up to the tax service you need.
authorisationRequestExpired.p3=You could also check you have signed in with the correct Government Gateway user ID. It must be the same one you used to sign up to the tax service the authorisation request is for.
authorisationRequestExpired.p4=<a href={0} class="govuk-link" target="_blank" rel="noreferrer noopener">Sign in with the Government Gateway user ID</a> you use for managing your personal tax affairs.

# ________________________________________________________________________________
# We cannot find this authorisation request
# ________________________________________________________________________________
cannotFindAuthRequest.p1=We cannot find a request from {0}.
cannotFindAuthRequest.p2=<a href={0} class="govuk-link" target="_blank" rel="noreferrer noopener">Make sure you have signed up for the tax service you need.</a> Ask your agent if you are not sure.
cannotFindAuthRequest.p3=You need to sign in with the correct Government Gateway user ID. It is possible to have more than one, so make sure it is the same one you used to sign up to the tax service the authorisation request is for.<a href={0} class="govuk-link" target="_blank" rel="noreferrer noopener">Try signing in with a different Government Gateway user ID</a> (the one that you use for managing your personal tax affairs).

# ________________________________________________________________________________
# This authorisation request has been cancelled
# ________________________________________________________________________________
authorisationRequestCancelled.p1=This request has been cancelled by your agent on {0}. For details, <a href={1} class="govuk-link" target="_blank" rel="noreferrer noopener">view your history</a> to check for any expired, cancelled or outstanding requests.
authorisationRequestCancelled.p2=If your agent has sent you a recent request, <a href={0} class="govuk-link" target="_blank" rel="noreferrer noopener">make sure you have signed up to the tax service you need.
authorisationRequestCancelled.p3=You could also check you have signed in with the correct Government Gateway user ID. It must be the same one you used to sign up to the tax service the authorisation request is for.
authorisationRequestCancelled.p4=<a href={0} class="govuk-link" target="_blank" rel="noreferrer noopener">Sign in with the Government Gateway user ID</a> you use for managing your personal tax affairs.

# ________________________________________________________________________________
# This authorisation request has already been responded to
# ________________________________________________________________________________
authorisationRequestRespondedTo.p1=This request has already been responded to on {0}. For details, <a href={1} class="govuk-link" target="_blank" rel="noreferrer noopener">view your history</a> to check for any expired, cancelled or outstanding requests.
authorisationRequestRespondedTo.p2=If your agent has sent you a recent request, <a href={0} class="govuk-link" target="_blank" rel="noreferrer noopener">make sure you have signed up to the tax service you need.
authorisationRequestRespondedTo.p3=You could also check you have signed in with the correct Government Gateway user ID. It must be the same one you used to sign up to the tax service the authorisation request is for.
authorisationRequestRespondedTo.p4=<a href={0} class="govuk-link" target="_blank" rel="noreferrer noopener">Sign in with the Government Gateway user ID</a> you use for managing your personal tax affairs.

# ________________________________________________________________________________
# Page not found
# ________________________________________________________________________________
pageNotFound.p1=If you have typed the web address, check it is correct.
pageNotFound.p2=If you pasted the web address, check you have copied the entire address.
pageNotFound.p3=If the