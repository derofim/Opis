![](https://i1.wp.com/www.worldautomation.net/wp-content/uploads/2017/08/logo.png)
# WorldAutomation.Net Opis Pack Repository #



This is a fork of ProfMobius' mod [Opis](http://minecraft.curseforge.com/projects/opis).
Opis is an important tool for profiling the client and server using a user-friendly GUI.
The main changes of this fork are:

* Removal of the integrated [MapWriter](minecraft.curseforge.com/projects/mapwriter-2) mod
* Removal of overlays that required MapWriter
* Integration of MobiusCore as opposed to building it as an external mod
* Fix for client profiling data "\<ERROR\>" strings
 
# Rationale

This fork was undertaken as an emergency fix for FTB Infinity, after Opis was removed
in 2.1.2 [for compatibility reasons](http://www.feed-the-beast.com/projects/ftb-infinity-evolved/files/2265972).
The incompatibility arises from a [MapWriter bug](https://bitbucket.org/ProfMobius/opis/issues/125/client-crash).

## Why remove MapWriter?

This is a personal choice. In a pack such as Infinity, mapping is already provided by
another mod such as JourneyMap. Subjectively, MapWriter is a less useful minimap. It
became an annoyance to attempt to disable it every pack (re)installation. I thought it OK
as I did not find any use for overlay functionality.

## Why integrate MobiusCore?

Subjectively, I found it too cumbersome in development to work with MobiusCore as a
separate mod and codebase whilst attempting to update Opis. I found that MobiusCore is
[not depended upon by any other mods](http://minecraft.curseforge.com/projects/mobiuscore/relations/dependents?filter-related-dependents=3),
so I thought it OK to integrate as a git submodule instead.

# Building

## Requirements

* [Gradle installation with gradle binary in PATH](http://www.gradle.org/installation).
Unlike the source package provided by Forge, this repository does not include a gradle
wrapper or distribution.

## Simple build

*Estimated time on a dedicated server: 3 minutes*

1. Clone repository with `--recursive` argument, e.g. 
`git clone --recursive https://github.com/Gamealition/Opis.git`
2. Execute `gradle setupCIWorkspace` inside the repository
3. Execute `gradle build`
    * If subsequent builds cause problems, execute `gradle clean`
4. Check the `output` directory for the built jar

## Certs (Windows)

Check:
java -version
where keytool

Change gradle.properties (!!!) :
systemProp.javax.net.ssl.trustStore=C:/Program Files (x86)/Java/jdk1.7.0_55/jre/lib/security/cacerts
systemProp.javax.net.ssl.trustStorePassword=changeit

Check version "%JAVA_HOME%/bin/"

Check PATH (C:\Program Files (x86)\Java\jdk1.7.0_55\bin)

( Read https://confluence.atlassian.com/kb/connecting-to-ssl-services-802171215.html )

1) Go to project folder 

2) Input
openssl s_client -connect modmaven.k-4u.nl:443 | sed -ne '/-BEGIN CERTIFICATE-/,/-END CERTIFICATE-/p' > modmaven.crt

3) Input
"%JAVA_HOME%/bin/keytool" -import -alias modmaven.k-4u.nl -keystore "%JAVA_HOME%/jre/lib/security/cacerts" -file modmaven.crt
"%JAVA_HOME%/bin/keytool" -import -alias modmaven.k-4u.nl -keystore "C:\Program Files\Java\jdk1.8.0_92\jre\lib\security\cacerts" -file modmaven.crt
Password: changeit
Trust: yes

Check ( C:\Program Files (x86)\Java\jdk1.7.0_55/jre/lib/security/cacerts )

4) Check
( Read https://confluence.atlassian.com/kb/unable-to-connect-to-ssl-services-due-to-pkix-path-building-failed-779355358.html )

Download https://confluence.atlassian.com/kb/files/779355358/779355357/1/1441897666313/SSLPoke.class

Check:
java SSLPoke modmaven.k-4u.nl 443
"%JAVA_HOME%/bin/java" SSLPoke modmaven.k-4u.nl 443

## IntelliJ

*Estimated time on a home connection: 5-15 minutes*

1. Clone repository with `--recursive` argument, e.g. 
`git clone --recursive https://github.com/Gamealition/Opis.git`
2. Open `build.gradle` as a project
3. Execute the `setupDecompWorkspace` task
4. Click the refresh button in the "Gradle" tab
5. Execute the `genIntellijRuns` task
6. For both the "Minecraft Client" and "Minecraft Server" run configurations, add the JVM
option `-Dfml.coreMods.load=mcp.mobius.mobiuscore.asm.CoreDescription`
