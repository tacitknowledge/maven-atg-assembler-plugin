Maven ATG 2007.1.p3 Assembler Plugin
        Generate ATG-specific manifests and descriptors for ATG module creation,
        and assemble/package an ATG BigEar for JBoss. This plugin wraps ATG's assembler.jar


*NOTE*:  you must have a licensed version of the 2007.1.p3 assembler jar from ATG to build this code.  
Its used in the ant scripts provided by ATG (now Oracle).  We recommend adding this to a mirror 
or local filesystem repository. However, you may want to modify the pom and change the 
dependency to a system dependency.

# Dependency is currently set to:
	<dependency>
  	  <groupId>>atg</groupId>
  	  <artifactId>assembler</artifactId>
  	  <version>2007.1.p3</version>
	</dependency>
 you also will need to build and install our [maven-plugin-support library](https://github.com/tacitknowledge/maven-plugin-support)


# Goals
* update-appxml: updates the app xml for jboss
* war-manifest: creates the MANIFEST.MF for the war according to Nucleus needs
* ear-manifest: creates the MANIFEST.MF for the ear according to Nucleus needs
* assemble-unpacked-ear:  creates the directory structure for an unpacked ear file


