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
select-service.HMRC-MTD-VAT.personal=Rheoli ei TAW
clientService.HMRC-MTD-VAT.business=Rheoli ei TAW
clientService.HMRC-TERS-ORG.trust=Cynnal ei ymddiriedolaeth neu ystâd
clientService.HMRC-TERSNT-ORG.trust=<translation needed>
clientService.HMRC-CBC-ORG.trust=Rheoli ei adroddiadau gwlad-wrth-wlad
clientService.HMRC-CBC-ORG.business=Rheoli ei adroddiadau gwlad-wrth-wlad
clientService.HMRC-PILLAR2-ORG.business=Rheoli ei drethi atodol Colofn 2
clientService.HMRC-PILLAR2-ORG.trust=Rheoli ei drethi atodol Colofn 2

#-------------------------------------------------------------------------------------------
# Enter Client Fact
# -------------------------------------------------------------------------------------------


clientFact.HMRC-CGT-PD.countryCode.label=Beth yw gwlad cyfeiriad cyswllt eich cleient?
clientFact.HMRC-CGT-PD.countryCode.hint=Mae’n rhaid i hwn gyd-fynd â gwlad cyfeiriad cyswllt eich cleient yn ei gyfrif Treth Enillion Cyfalaf ar eiddo yn y DU. Dechreuwch deipio enw’r wlad.
clientFact.HMRC-CGT-PD.countryCode.error.required=Nodwch wlad cyfeiriad cyswllt eich cleient


clientFact.HMRC-CBC-ORG.email.label=<translation needed>
clientFact.HMRC-CBC-ORG.email.hint=<translation needed>
clientFact.HMRC-CBC-ORG.email.error.invalid=<translation needed>
clientFact.HMRC-CBC-ORG.email.error.required=<translation needed>
clientFact.HMRC-CBC-NONUK-ORG.email.label=<translation needed>
clientFact.HMRC-CBC-NONUK-ORG.email.hint=<translation needed>
clientFact.HMRC-CBC-NONUK-ORG.email.error.invalid=<translation needed>
clientFact.HMRC-CBC-NONUK-ORG.email.error.required=<translation needed>


# -------------------------------------------------------------------------------------------
# Enter Known Facts
# -------------------------------------------------------------------------------------------
# Nino
client-details.nino.label=<translation needed>
client-details.nino.hint=Er enghraifft, QQ 12 34 56 C
error.client-details.nino.invalid=Mae’n rhaid i rif Yswiriant Gwladol fod ar ffurf 2 lythyren, yna 6 rhif wedi’u dilyn gan A, B, C neu D, megis QQ 12 34 56 C
error.client-details.nino.required=Nodwch rif Yswiriant Gwladol eich cleient

# Postcode
client-details.postcode.label=Cod post
client-details.postcode.hint=Dyma god post cyfeiriad cofrestredig eich cleient
error.client-details.postcode.invalid=Nodwch god post go iawn
error.client-details.postcode.required=Nodwch god post eich cleient

# Date of Birth
client-details.dob.label=Dyddiad geni
client-details.dob.hint=Er enghraifft, 31 3 1980
error.client-details.dob.required=Nodwch ddyddiad geni’ch cleient
error.client-details.dob.invalid=Mae’n rhaid i’r dyddiad geni fod yn ddyddiad dilys
error.client-details.dob.day.required=Mae’n rhaid i’r dyddiad geni gynnwys diwrnod
error.client-details.dob.month.required=Mae’n rhaid i’r dyddiad geni gynnwys mis
error.client-details.dob.year.required=Mae’n rhaid i’r dyddiad geni gynnwys blwyddyn
error.client-details.dob.day-month.required=Mae’n rhaid i’r dyddiad geni gynnwys diwrnod a mis
error.client-details.dob.day-year.required=Mae’n rhaid i’r dyddiad geni gynnwys diwrnod a blwyddyn
error.client-details.dob.month-year.required=Mae’n rhaid i’r dyddiad geni gynnwys mis a blwyddyn

# Plastic Packaging Tax Reference
client-details.pptRef.label=Cyfeirnod Treth Deunydd Pacio Plastig
client-details.pptRef.hint=Mae hyn yn 15 o gymeriadau, er enghraifft XMPPT0000000001. Daeth hyn i law eich cleient pan gofrestrodd ar gyfer Treth Deunydd Pacio Plastig.
error.client-details.pptRef.required=Nodwch gyfeirnod Treth Deunydd Pacio Plastig eich cleient
error.client-details.pptRef.invalid=Nodwch gyfeirnod Treth Deunydd Pacio Plastig y cleient yn y fformat cywir

# Plastic Packaging Tax Registration Date
client-details.registrationDate.label=Dyddiad cofrestru ar gyfer Treth Deunydd Pacio Plastig
client-details.registrationDate.hint=Er enghraifft, 21 8 2021
error.client-details.registrationDate.invalid=Nodwch ddyddiad cofrestru ar gyfer Treth Deunydd Pacio Plastig sy’n ddilys
error.client-details.registrationDate.required=Nodwch ddyddiad cofrestru eich cleient ar gyfer Treth Deunydd Pacio Plastig
error.client-details.registrationDate.day.required=Mae’n rhaid i’r dyddiad cofrestru ar gyfer Treth Deunydd Pacio Plastig gynnwys diwrnod
error.client-details.registrationDate.month.required=Mae’n rhaid i’r dyddiad cofrestru ar gyfer Treth Deunydd Pacio Plastig gynnwys mis
error.client-details.registrationDate.year.required=Mae’n rhaid i’r dyddiad cofrestru ar gyfer Treth Deunydd Pacio Plastig gynnwys blwyddyn
error.client-details.registrationDate.day-month.required=Mae’n rhaid i’r dyddiad cofrestru ar gyfer Treth Deunydd Pacio Plastig gynnwys diwrnod a mis
error.client-details.registrationDate.day-year.required=Mae’n rhaid i’r dyddiad cofrestru ar gyfer Treth Deunydd Pacio Plastig gynnwys diwrnod a blwyddyn
error.client-details.registrationDate.month-year.required=Mae’n rhaid i’r dyddiad cofrestru ar gyfer Treth Deunydd Pacio Plastig gynnwys mis a blwyddyn

# Capital Gains Tax account reference
client-details.cgtRef.label=Pa rif cyfrif Treth Enillion Cyfalaf ar eiddo yn y DU sydd gan eich cleient?
client-details.cgtRef.hint=Bydd hyn yn ein helpu i baru ei fanylion â’r wybodaeth sydd gennym. Mae hyn yn 15 o gymeriadau, er enghraifft XYCGTP123456789. Daeth hyn i law eich cleient pan greodd ei gyfrif.
error.client-details.cgtRef.required=Nodwch gyfeirnod cyfrif Treth Enillion Cyfalaf y cleient
error.client-details.cgtRef.invalid=Nodwch gyfeirnod cyfrif Treth Enillion Cyfalaf y cleient yn y fformat cywir


#-------------------------------------------------------------------------------------------
# Enter Client Fact
# -------------------------------------------------------------------------------------------

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

#Track invitation requests
trackRequests.title=Rheoli eich ceisiadau diweddar am awdurdodiad
trackRequests.intro=Gwirio statws eich ceisiadau am awdurdodiad i gleientiaid yn ystod y {0} o ddiwrnodau diwethaf, neu ganslo ceisiadau ac awdurdodiadau nad oes eu hangen mwyach.
trackRequests.empty=Nid oes gennych unrhyw geisiadau diweddar am awdurdodiad.
trackRequests.empty.continue=Dechrau cais am awdurdodiad newydd
trackRequests.th.client=Manylion y cleient
trackRequests.table-row-header.clientNameUnknown=Methu adfer enw cleient
trackRequests.th.service=Cais am awdurdodiad wedi’i anfon
trackRequests.th.status=Statws
trackRequests.th.actions=Camau gweithredu
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
trackRequests.service.details.HMRC-CBC=Manylion am adroddiadau gwlad-wrth-wlad
trackRequests.service.clients.HMRC-CBC=Rheoli adroddiadau gwlad-wrth-wlad y cleient
trackRequests.invitation.identifier.nino=Rhif Yswiriant Gwladol:
trackRequests.invitation.identifier.vrn=Rhif cofrestru TAW:
trackRequests.invitation.identifier.MTDITID=Dynodydd Troi Treth yn Ddigidol ar gyfer Treth Incwm
trackRequests.invitation.expires=Yn dod i ben:
trackRequests.status.Pending=Nid yw’r cleient wedi ymateb eto
trackRequests.status.Accepted=Derbyniwyd gan y cleient
trackRequests.status.Partialauth=Derbyniwyd gan y cleient
trackRequests.status.Rejected=Gwrthodwyd gan y cleient
trackRequests.status.Expired=Daeth y cais i ben gan i’r cleient fethu ag ymateb mewn pryd
trackRequests.status.Cancelled=Gwnaethoch ganslo’r cais hwn
trackRequests.invitation.status.acceptedthencancelledbyagent=Gwnaethoch ganslo’ch awdurdodiad
trackRequests.invitation.status.acceptedthencancelledbyclient=Derbyniwyd gan y cleient. Gwnaeth y cleient ganslo’i awdurdodiad yn nes ymlaen
trackRequests.invitation.status.acceptedthencancelledbyhmrc=Derbyniwyd gan y cleient. Gwnaeth CThEM ganslo’ch awdurdodiad yn nes ymlaen
trackRequests.invitation.status.invalidrelationship=Gwnaethoch ganslo’ch awdurdodiad
trackRequests.invitations.actions.acceptedthencancelledbyagent=Dechrau cais newydd
trackRequests.invitations.actions.aria-text=Dechrau cais newydd ar gyfer {0} i {1}
trackRequests.invitations.actions.acceptedthencancelledbyclient=Dechrau cais newydd
trackRequests.invitations.actions.acceptedthencancelledbyhmrc=Dechrau cais newydd
trackRequests.invitations.actions.partialauth.signup=Cofrestru eich cleient (yn agor tab newydd)
trackRequests.invitations.actions.partialauth.cancel=Canslo’ch awdurdodiad
trackRequests.invitations.actions.partialauth.cancel.aria-text=Canslo’ch awdurdodiad ar gyfer {0} to {1}
trackRequests.invitations.actions.pending=Ailanfon cais at y cleient
trackRequests.invitations.actions.pending.aria-text=Ailanfon cais at gleient {0} i {1}
trackRequests.invitations.actions.pending.cancel=Canslo’r cais hwn
trackRequests.invitations.actions.pending.cancel.aria-text=Canslo’r cais hwn ar gyfer {0} i {1}
trackRequests.invitations.actions.accepted=Canslo’ch awdurdodiad
trackRequests.invitations.actions.accepted.aria-text=Canslo’ch awdurdodiad ar gyfer {0} to {1}
trackRequests.invitations.actions.rejected=Dechrau cais newydd
trackRequests.invitations.actions.expired=Dechrau cais newydd
trackRequests.invitations.actions.cancelled=Dechrau cais newydd
trackRequests.invitations.actions.invalidrelationship=Dechrau cais newydd
trackRequests.pagination.showing=Dangos
trackRequests.pagination.to=i
trackRequests.pagination.of=o
trackRequests.filter-client.label=Chwilio yn Ã´l cleient
trackRequests.filter-status.label=Hidlo yn Ã´l statws
trackRequests.filter.filter.button=Hidlo
trackRequests.filter.clear.button=Clirio pob hidlydd
trackRequests.filter-status.filter.button=Hidlo
trackRequests.error.status-empty=Rhaid i chi ddewis statws o’r rhestr
trackRequests.error.status-invalid=Rhaid i chi ddewis statws o’r rhestr
trackRequests.error.client-empty=Rhaid i chi ddewis enw o’r rhestr
trackRequests.error.client-invalid=Rhaid i chi ddewis enw o’r rhestr
trackRequests.filter-status.AllStatuses=Pob statws
trackRequests.filter-status.ExpireInNext5Days=Yn dod i ben yn y 5 diwrnod nesaf
trackRequests.filter-status.ActivityWithinLast5Days=Gweithgarwch yn ystod y 5 diwrnod diwethaf
trackRequests.filter-status.ClientNotYetResponded=Nid yw’r cleient wedi ymateb eto
trackRequests.filter-status.AgentCancelledAuthorisation=Gwnaethoch ganslo’ch awdurdodiad
trackRequests.filter-status.DeclinedByClient=Gwrthodwyd gan y cleient
trackRequests.filter-status.AcceptedByClient=Derbyniwyd gan y cleient
trackRequests.filter-status.Expired=Daeth y cais i ben gan i’r cleient fethu ag ymateb mewn pryd
trackRequests.filter-status.ClientCancelledAuthorisation=Derbyniwyd gan y cleient. Gwnaeth y cleient ganslo’i awdurdodiad yn nes ymlaen
trackRequests.filter-status.HMRCCancelledAuthorisation=Derbyniwyd gan y cleient. Gwnaeth CThEM ganslo’ch awdurdodiad yn nes ymlaen
trackRequests.filter-status.Declined.HMRC-CBC=Cafodd cais {0} i reoli adroddiadau gwlad-wrth-wlad y cleient ei wrthod.

# ________________________________________________________________________________
# Journey exit headings
# ________________________________________________________________________________

journeyExit.client-not-found.header=Nid oeddem yn gallu dod o hyd i’ch cleient
journeyExit.not-registered.header=Mae angen i’ch cleient gofrestru ar gyfer Hunanasesiad
journeyExit.not-authorised.header=Nid ydych wedi eich awdurdodi
journeyExit.pending-authorisation-exists.header=Rydych eisoes wedi creu cais am awdurdodiad ar gyfer y gwasanaeth treth hwn
journeyExit.client-insolvent.header=Ni allwch greu cais am awdurdodiad gan fod y cleient hwn yn ansolfent
journeyExit.authorisation-already-exists.header=Rydych eisoes wedi’ch awdurdodi

# ________________________________________________________________________________
# Journey exit partials
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
