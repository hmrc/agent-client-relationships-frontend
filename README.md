
# agent-client-relationships-frontend

## What the service does
This is a frontend microservice for agent-client-relationships. It handles the following agent client authorisation journeys and dashboards:

- Journey for agents to create an authorisation request (aka invitation) for a specific client and tax regime
- Journey for clients to accept or decline an agent's authorisation request
- Journey for agents or clients to de-authorise a relationship
- Agent can view their authorisation requests and inactive relationships from the last 30 days (track authorisation requests)
- Agent fast track service
- Client can view pending invitations, past and present agent authorisations

Clients can be 'personal' or 'business' or 'trusts' (taxable and non-taxable).

Currently this service supports the following invitations:
- ITSA for main or supporting agents, including alternative ITSA (client does not need to be registered for ITSA)
- Personal Income Record
- MTD VAT
- Capital Gains Tax on UK property account
- Trusts (taxable and non-taxable)
- Plastic Packaging Tax
- CBC
- Pillar 2


### Running the tests with coverage

    ./check.sh

### Running the app locally

    sm2 --start AGENT_AUTHORISATION
    sm2 --stop AGENT_CLIENT_RELATIONSHIPS_FRONTEND
    ./run.sh

It should then be listening on port 9435

    browse http://localhost:9435/agent-client-relationships/authorisation-request

or

    browse http://localhost:9435/agent-client-relationships/agent-cancel-authorisation

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
|personal| HMRC-MTD-IT or HMRC-MTD-IT-SUPP           | NINO                   | Valid Nino                    |Postcode|
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


