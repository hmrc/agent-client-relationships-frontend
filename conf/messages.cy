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
