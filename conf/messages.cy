# -------------------------------------------------------------------------------------------
# Common
# -------------------------------------------------------------------------------------------
service.name.agents.auth=Gofyn i gleient eich awdurdodi

error.prefix=Gwall:' '
error.heading=Mae problem wedi codi

continue.button=Yn eich blaen


# -------------------------------------------------------------------------------------------
# Select Client Type
# -------------------------------------------------------------------------------------------
clientType.header=<translation needed>
clientType.personal=Unigolyn neu unig fasnachwr
clientType.business=Cwmni neu bartneriaeth
clientType.trust=Ymddiriedolaeth neu ystâd
error.clientType.required=Dewiswch y math o gleient rydych angen awdurdodiad ganddo


# -------------------------------------------------------------------------------------------
# Select Client Service
# -------------------------------------------------------------------------------------------
clientService.header=Beth yr ydych am i’r cleient eich awdurdodi i’w wneud?
clientService.p1=<translation needed>
clientService.alternative=Mae angen awdurdodiad arnaf ar gyfer rhywbeth arall
clientService.alt-suggestion=Dysgwch ragor am <a id="guidanceLink" href="{0}" target="_blank" rel="noopener noreferrer">ofyn i gleient eich awdurdodi i ddelio â gwasanaethau treth eraill (yn agor tab newydd)</a>.
error.clientService.required=Dewiswch yr hyn rydych am i’r cleient eich awdurdodi i’w wneud

clientService.PERSONAL-INCOME-RECORD.personal=Bwrw golwg dros ei gofnod incwm
clientService.HMRC-MTD-IT.personal=Rheoli ei gyfrif Troi Treth yn Ddigidol ar gyfer Treth Incwm
clientService.HMRC-PPT-ORG.personal=Rheoli ei Dreth Deunydd Pacio Plastig
clientService.HMRC-PPT-ORG.business=Rheoli ei Dreth Deunydd Pacio Plastig
clientService.HMRC-PPT-ORG.trust=Rheoli ei Treth Deunydd Pacio Plastig ymddiriedolaeth
clientService.HMRC-CGT-PD.personal=Rheoli ei gyfrif Treth Enillion Cyfalaf ar eiddo yn y DU
clientService.HMRC-CGT-PD.trust=Rheoli ei cyfrif Treth Enillion Cyfalaf ar eiddo yn y DU ar gyfer ymddiriedolaeth


# -------------------------------------------------------------------------------------------
# Enter Known Facts
# -------------------------------------------------------------------------------------------
# Nino
client-details.nino.label=<Translation needed>
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


# ________________________________________________________________________________
# Confirm Client
# ________________________________________________________________________________
confirm-client.title=Ai hwn yw’r cleient yr hoffech gael awdurdodiad ganddo?
confirm-client.header=Ai {0} yw’r cleient rydych am awdurdodiad ganddo?
error.confirm-client.required=Dewiswch ‘Iawn’ os hoffech ofyn i’r cleient hwn am awdurdodiad
accepted.true=Iawn
accepted.false=Na – mae angen i mi ddechrau eto
error.accepted.required=Dewiswch ‘Iawn’ os hoffech ofyn i’r cleient hwn am awdurdodiad


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
