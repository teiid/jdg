Teiid JDG Translator / Connector 
=====

This is the repo for the JDG translator / connector to support accessing a JDG Cache as a data source or for materialization.

The is to support JDG 7.1 running in Teiid 8.12.x

## Useful Links
- Website - http://teiid.org
- Documentation - https://teiid.gitbooks.io/documents/content/
- Documentation Project - https://teiid.gitbooks.io
- JIRA Issues -  https://issues.jboss.org/browse/TEIID
- User Forum - https://community.jboss.org/en/teiid?view=discussions
- Wiki - https://community.jboss.org/wiki/TheTeiidProject

## To build Teiid

If running the unit test, must use JDK 1.8+.
Can use JDK 1.7 if the unit test are not run.  Use -Dmaven.test.skip=true

The included settings_jdg.xml is a settings file that can be used to build against a local JDG maven repository.


## Created Artifacts

The build of this project will produce the following jboss-as deployable artifacts

* connector-jdg-hotrod-$version.zip
* translator-jdg-hotrod-$version.zip
	
You can find the deployment artifacts in the corresponding "target" directory of each sub project, once the build is completed.

Unzip at the JBOSS_HOME root directory.

