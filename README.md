
# agent-client-relationships-frontend

[![Build Status](https://travis-ci.org/hmrc/agent-client-relationships-frontend.svg)](https://travis-ci.org/hmrc/agent-client-relationships-frontend) [ ![Download](https://api.bintray.com/packages/hmrc/releases/agent-client-relationships-frontend/images/download.svg) ](https://bintray.com/hmrc/releases/agent-client-relationships-frontend/_latestVersion)

## What the service does
This is a frontend microservice for Agent Client Authorisation.

Invitations service currently provides these functions:
- Agent can create an invitation to represent a client for specific tax regime
- Client can accept or decline an agent's invitation
- Agent can view their authorisation requests and inactive relationships from the last 30 days (track)
- Agent fast track service

Clients can be 'personal' or 'business' or 'trusts' (taxable and non-taxable).

Currently this service supports the following invitations:
- ITSA, including alternative ITSA (client does not need to be registered for ITSA)
- Personal Income Record
- MTD VAT
- Capital Gains Tax on UK property account
- Trusts (taxable and non-taxable)
- Plastic Packaging Tax
- CBC
- Pillar 2

Feature flags exist for each service and for requirement of known facts.

## Features

### Running the tests

    sbt test it/test

### Running the tests with coverage

    sbt clean coverageOn test it/test coverageReport

### Running the app locally

    sm2 --start AGENT_AUTHORISATION
    sm2 --stop AGENT_CLIENT_RELATIONSHIPS_FRONTEND
    sbt run

It should then be listening on port 9435

    browse http://localhost:9435/agent-client-relationships/authorisation-request

or

    browse http://localhost:9435/agent-client-relationships/agent-cancel-authorisation

## Endpoints
All Endpoints require Authentication.

### For Agents

Start page for Agents:

    GET   	/agent-client-relationships/agents/

Fast Track Invitation:

API to create a fast-track invitation.

```
POST   /agent-client-relationships/agents/fast-track
```

The following are the supported services and relevant fields required to create a fast track invitation:

|clientType| service                | clientIdentifierType   | clientIdentifier              |knownFact|
|--------|------------------------|------------------------|-------------------------------|-------|
|personal| HMRC-MTD-IT            | NINO                   | Valid Nino                    |Postcode|
|personal| PERSONAL-INCOME-RECORD | NINO                   | Valid Nino                    |Date of Birth|
|personal or business| HMRC-MTD-VAT           | VRN                    | Valid Vat Registration Number |Date of Client's VAT Registration|
|personal or business| HMRC-CGT-PD            | CGTPDRef               | Valid CGT-PD reference number |Postcode|
|business| HMRC-TERS-ORG          | UTR                    | Valid UTR                     |Date of trust registration|
|business| HMRC-TERSNT-ORG        | urn                    | Valid URN                     |Date of trust registration|
|personal or business| HMRC-PPT-ORG           | EtmpRegistrationNumber | Valid PPT ref                 |Date of registration|
|business| HMRC-CBC-ORG       | CbcId                  | Valid Cbc Id                  |Date of registration|
|business| HMRC-CBC-NONUK       | CbcId                  | Valid Cbc Id                  |Date of registration|
|business| HMRC-PILLAR2-ORG       | Pillar 2 ID            | Valid PlrId                   |Date of registration|


Note: Client Type and Known Fact are optional. If either of those are missing you will be redirected to the appropriate page. However, if any other information is missing / invalid / unsupported, you will be given an error url.

### For Clients

Start Page for Clients:

```
    GET     /agent-client-relationships/{clientType}/{uid}/{agentName}

```

### License

This code is open source software licensed under the [Apache 2.0 License]("http://www.apache.org/licenses/LICENSE-2.0.html")


