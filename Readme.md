# Goal of this plugin

The goal of this plugin is to simplify the life of language engineers.

It should make as easy as possible to setup new projects and perform typical operations.

This plugin is very opinionated: it is based on the standard practices used by the authors and seen to be adopted by 
other practicioners.

## How to setup the project

Example of _build.gradle_ file:

```
plugins {
	id 'com.strumenta.mpswizard' version '1.0.1'
}

// this is optional
mpsWizard {
	mpsVersion = "2020.1.6"
	useIets3 = true
}
```

The simplest configuration is this:

```
plugins {
	id 'com.strumenta.mpswizard'
}
```

That's it. 

We also expect you to have gradle (or better the gradle wrapper) setup.

## How to create a project using this plugin

You simply run this command:

```
./gradlew setupMpsProject
```

This will:
* update the _.gitignore_ file with the entries you need
* download dependencies in artifacts
* download MPS (useful for running tests from the command line) in the _artifacts/mps_ directory
* generate an empty MPS project
* setup the libraries in your MPS project

Basically, it would do all the setup for you.

## Tasks supported

* **generateGitignore** - Generate .gitignore file for MPS project
* **generateLibrariesConf** - Generate the libraries.xml file for the MPS Project
* **generateMpsProject** - Generate an empty MPS project
* **resolveMps** - Download a copy of MPS for running tests from the command line
* **resolveMpsArtifacts** - Download the dependencies used by the MPS Project
* **setupMpsProject** - Prepare the whole project
* **validateMpsWizardConfiguration** - Validate the MPS Wizard configuration

## License

This is licensed under the Apache License V2.0

## Acknowledgements

This plugin is heavily inspired by the great best practices developed at Itemis and supported by their 
[MPS gradle plugin](https://github.com/mbeddr/mps-gradle-plugin). The difference is that this plugin aims to enforce
those best practices and make the life of the user even simpler.
