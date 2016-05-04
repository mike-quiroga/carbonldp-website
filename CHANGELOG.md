# 0.34.1 (2016/05/04)
- Fix [LDP-641](https://jira.base22.com/browse/LDP-641) - Missing properties in `prod-config.properties` file

# 0.34.0 (2016/04/28)
- Add prefer contained and membership resources to container preferences
- URL now accepts orderBy retrieval preferences for contained and membership resources retrieval

# 0.33.1 (2016/04/25)
- Change log4j2 configuration to a programmatic based one that uses `LOGConfigurationFactory`
- Add `requestID` and `shortRequestID` to log4j2's context map
- Configure log4j2 to log to the syslog service logger:601

# 0.33.0 (2016/04/22)
- Add backup export support
- Add backup import support
- Add job system (used by the backup export/import features)
- Fix [LDP-622](https://jira.base22.com/browse/LDP-622) - Literals where being compared as strings and therefore RDFSource replacements where failing
- Fix [LDP-618](https://jira.base22.com/browse/LDP-618) - platform/api/ was not being returned with an ETag header
- Fix [LDP-623](https://jira.base22.com/browse/LDP-623) - RDF lists were not being returned correctly in JSON-LD

# 0.32.0 (2016/04/12)
- Update Sesame to version 4.1.1
- Remove ALPHA from the version tag to follow semantic version naming convention

# 0.31.1 (2016/04/11)
- Add Jetty response/requestHeaderSize to Vars to make request/response size limit configurable

# 0.31.0 (2016/04/07)
- Switch from using weak ETags to strong ETags

# 0.30.2 (2016/04/07)
- SPARQL now sends a 403 HTTP response when you do not have permissions in the target service

# 0.30.1 (2016/04/05)
- Prefer minimal container is now working properly

# 0.30.0 (2016/04/05)
- BasicAuthentication now identifies if the request was sent from a Browser, if it wasn't it doesn't send the auth challenge (to avoid the login prompt)

# 0.29.4 (2016/03/22)
- Add "prod" Spring Profile with base URL set for production (carbonldp.com)

# 0.29.3 (2016/03/22)
- Fix validation when registering an Agent with an invalid document

# 0.29.2 (2016/03/22)
- Fix SPARQL Query support for predefined documents (apps/, agents/, etc.)

# 0.29.1 (2016/03/22)
- Add support for the following Accept media types when executing SPARQL CONSTRUCT/DESCRIBE queries
    - `application/trig`
    - `application/n-triples`
    - `text/n3`
    - `application/trix`
    - `application/x-binary-rdf`
    - `application/n-quads`

# 0.29.0 (2016/03/18)
- Anonymous agents can now be added as a subject to ACEs

# 0.28.5 (2016/03/18)
- App cors filter won't trigger when asking an app document in the platform repository

# 0.28.4 (2016/03/18)
- Enable app role authentication in platform documents related to that app

# 0.28.3 (2016/03/08)
- Fix a bug that prevented AppDevelopers from creating new applications

# 0.28.2 (2016/03/02)
- Access points now show their members when asked as a container

# 0.28.1 (2016/02/02)
- Fix compilation problem with missing dependency

# 0.28.0 (2016/02/29)
- Blank nodes have now an identifier

# 0.27.7 (2016/02/25)
- Documents have now a property pointing to it's ACL

# 0.27.6 (2016/02/16)
- Files are properly deleted when deleting an app and a RDFRepresentation as a RDFSource

# 0.27.5 (2016/02/15)
- NonRDFSources files are now being deleted properly

# 0.27.4 (2016/02/12)
- Proper validation when adding and removing container members

# 0.27.3 (2016/02/12)
- Require an ACL as container now throws the right exception

# 0.27.2 (2016/02/12)
- Add member request no longer require if-match

# 0.27.1 (2016/02/12)
- Malformed body in a request now throws the right exception

# 0.27.0 (2016/02/08)
- Membership triples of basic container can now be filtered by visibility

# 0.26.0 (2016/01/29)
- Improve security of platform and apps

# 0.25.4 (2016/01/26)
- When deleting an app their references will also be deleted

# 0.25.3 (2016/01/26)
- Fix bug in which the app-admin role wasn't getting all the permissions on the root container

# 0.25.2 (2016/01/26)
- Unsupported content-type now throws the right exception

# 0.25.1 (2016/01/25)
- Fix access point validation

# 0.25.0 (2016/01/21)
- Spring Active Profiles can now be specified as a program argument. Like: "-spring.profiles.active profile1,profile2"

# 0.24.0 (2016/01/21)
- Carbon is now in check with w3c LDP specifications

# 0.23.0 (2016/01/07)
- carbon-platform has now an embedded Jetty instance; meaning it can now run as a jar application

# 0.22.0 (2016/01/04)
- App agents can now authenticate

# 0.21.0 (2015/12/17)
- Agents can now be removed from an AppRole

# 0.20.0 (2015/12/17)
- AppRoles can now be deleted

# 0.19.0 (2015/12/11)
- Existing agents can now be added as members of an appRole

# 0.18.0 (2015/12/10)
- AppRoles can now be created and related among themselves

# 0.17.0 (2015/12/10)
- ACLs can be retrieved

# 0.16.0 (2015/12/08)
- ACLs can now be modified

# 0.15.4 (2015/11/20)
- FIX CORS requests not returning header Access-Control-Expose-Headers

# 0.15.3 (2015/11/20)
- Fix application/rdf+json not returning a body

# 0.15.2 (2015/11/20)
- Throw proper error when an authentication token is requested without credentials

# 0.15.1 (2015/11/13)
- Create token-auth container in Platform and apps repositories

# 0.15.0 (2015/11/12)
- Authentication can now be made via JWT

# 0.14.1 (2015/11/12)
- Fix memberOfRelation for containers

# 0.14.0 (2015/11/06)
- Application agents can now be created

# 0.13.0 (2015/08/26)
- Members can now be selectively remove from a container
- Members can now be added to a container

# 0.12.0 (2015/08/14)
- Error responses are now returned with an error description

# 0.11.0 (2015/09/09)
- Apps can now be deleted

# 0.10.0 (2015/09/08)
- Added support for BNodes
- Added support for PUT requests with an interaction model of ldp:Container. Existing resources can now be added as members to a container.

# 0.9.0 (2015/08/18)
- Added RepositoryUpdater system.
