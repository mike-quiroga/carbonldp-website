# 0.25.2 (2016/01/26)
* unsupported content-type now throws the right exception
# 0.25.1 (2016/01/25)
* fix access point validation
# 0.25.0 (2016/01/21)
* Spring Active Profiles can now be specified as a program argument. Like: "-spring.profiles.active profile1,profile2"
# 0.24.0 (2016/01/21)
* carbon is now in check with w3c LDP specifications
# 0.23.0 (2016/01/07)
* carbon-platform has now an embedded Jetty instance; meaning it can now run as a jar application
# 0.22.0 (2016/01/04)
* App agents can now authenticate
# 0.21.0 (2015/12/17)
* Agents can now be removed from an AppRole
# 0.20.0 (2015/12/17)
* AppRoles can now be deleted
# 0.19.0 (2015/12/11)
* Existing agents can now be added as members of an appRole
# 0.18.0 (2015/12/10)
* AppRoles can now be created and related among themselves
# 0.17.0 (2015/12/10)
* ACLs can be retrieved
# 0.16.0 (2015/12/08)
* ACLs can now be modified
# 0.15.4 (2015/11/20)
* FIX CORS requests not returning header Access-Control-Expose-Headers
# 0.15.3 (2015/11/20)
* Fix application/rdf+json not returning a body
# 0.15.2 (2015/11/20)
* Throw proper error when an authentication token is requested without credentials
# 0.15.1 (2015/11/13)
* Create token-auth container in Platform and apps repositories
# 0.15.0 (2015/11/12)
* Authentication can now be made via JWT
# 0.14.1 (2015/11/12)
* Fix memberOfRelation for containers
# 0.14.0 (2015/11/06)
* Application agents can now be created
# 0.13.0 (2015/08/26)
* Members can now be selectively remove from a container
* Members can now be added to a container
# 0.12.0 (2015/08/14)
* Error responses are now returned with an error description
# 0.11.0 (2015/09/09)
* Apps can now be deleted
# 0.10.0 (2015/09/08)
* Added support for BNodes
* Added support for PUT requests with an interaction model of ldp:Container. Existing resources can now be added as
 members to a container.
# 0.9.0 (2015/08/18)
* Added RepositoryUpdater system.