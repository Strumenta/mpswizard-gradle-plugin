package com.strumenta.mpswizard

import org.w3c.dom.Document
import org.w3c.dom.Element
import org.xml.sax.InputSource
import java.io.StringReader
import java.net.URL
import java.util.LinkedList
import javax.xml.parsers.DocumentBuilderFactory

private fun URL.readAsXml() : Document {
    val text = this.readText()
    val dbFactory = DocumentBuilderFactory.newInstance()
    val dBuilder = dbFactory.newDocumentBuilder()
    val xmlInput = InputSource(StringReader(text))
    val doc = dBuilder.parse(xmlInput)
    return doc
}

private fun deriveMajorMpsVersion(completeVersion: String) : String? {
    val parts = completeVersion.split(".")
    return if (parts.size >= 2) {
        "${parts[0]}.${parts[1]}"
    } else {
        null
    }
}

private fun listVersionsFromMetadata(url: URL) : List<String> {
    val mavenMetadata = url.readAsXml()
    val versioning = mavenMetadata.documentElement.getElementsByTagName("versioning").item(0) as Element
    val versions = versioning.getElementsByTagName("version")
    var exactMatchVersion : String? = null
    var approximateMatchVersion : String? = null
    val versionsRes = LinkedList<String>()
    for (i in 0 until versions.length) {
        val version = versions.item(i).textContent
        versionsRes.add(version)
    }
    return versionsRes
}

open class MpsWizardExtension {

    // MPS configuration

    var mpsVersion : String? = null
    val actualMpsVersion : String
        get() = mpsVersion ?: "2020.3.3"
    val actualMajorMpsVersion : String?
        get() =  deriveMajorMpsVersion(actualMpsVersion)

    // Mbeddr configuration

    var useMbeddr : Boolean? = null
    var mbeddrVersion : String? = null
    val actualUseMbeddr
        get() = useMbeddr == true || mbeddrVersion != null || actualUseIets3
    val actualMbeddrVersion : String
        get() = mbeddrVersion ?: findLatestMbeddrVersion(actualMpsVersion)

    // Iets3 configuration

    var useIets3 : Boolean? = null
    var iets3Version : String? = null
    val actualUseIets3
        get() = useIets3 == true || iets3Version != null
    val actualIets3Version : String
        get() =  iets3Version ?: findLatestIets3Version(actualMpsVersion)

    // MPSServer configuration

    var useMPSServer : Boolean? = null
        set(value) {
            println("SETTING useMPSServer $value (was $field)")
            field = value
        }
    var MPSServerVersion : String? = null
    val actualMPSServer
        get() = useMPSServer == true || MPSServerVersion != null
    val actualMPSServerVersion : String
        get() =  MPSServerVersion ?: findLatestMPSServerVersion(actualMpsVersion)
    var mpsserverPluginVersion : String? = null
    val actualMpsserverPluginVersion : String
        get() = mpsserverPluginVersion ?: "1.1.15"

    private fun findLatestVersion(mpsVersion: String, baseUrl: String, componentName: String) : String {
        val actualMajorMpsVersion = actualMajorMpsVersion

        val versions = listVersionsFromMetadata(URL("$baseUrl/${componentName}/maven-metadata.xml"))
        var exactMatchVersion : String? = null
        var approximateMatchVersion : String? = null
        for (version in versions) {
            val selected = actualMajorMpsVersion == null || version.startsWith("${actualMajorMpsVersion}.")
                    || version.startsWith("${actualMajorMpsVersion}-")
            if (selected) {
                val pom = URL("$baseUrl/${componentName}/$version/$componentName-$version.pom").readAsXml()
                val dependenciesNode = pom.documentElement.getElementsByTagName("dependencies").item(0) as Element
                val dependencies = dependenciesNode.getElementsByTagName("dependency")
                for (depI in 0 until dependencies.length) {
                    val dependency = dependencies.item(depI) as Element
                    val groupId = dependency.getElementsByTagName("groupId").item(0).textContent
                    val artifactId = dependency.getElementsByTagName("artifactId").item(0).textContent
                    if (groupId == "com.jetbrains" && artifactId == "mps") {
                        val mpsVersion = dependency.getElementsByTagName("version").item(0).textContent
                        if (mpsVersion == actualMpsVersion) {
                            exactMatchVersion = version
                        } else if (mpsVersion == actualMajorMpsVersion) {
                            approximateMatchVersion = version
                        }
                    }
                }
            }
        }
        return exactMatchVersion ?: approximateMatchVersion ?: throw RuntimeException("No match found for components ${componentName} at ${baseUrl}, using MPS Version ${mpsVersion}")
    }

    fun findLatestMbeddrVersion(mpsVersion: String) : String {
        return findLatestVersion(mpsVersion,"https://projects.itemis.de/nexus/content/repositories/mbeddr/com/mbeddr", "platform")
    }

    fun findLatestIets3Version(mpsVersion: String) : String {
        return findLatestVersion(mpsVersion,"https://projects.itemis.de/nexus/content/repositories/mbeddr/org/iets3", "opensource")
    }

    fun findLatestMPSServerVersion(mpsVersion: String) : String {
        return findLatestVersion(mpsVersion,"https://repo1.maven.org/maven2/com/strumenta/mpsserver", "mpsserver-core")
    }

    fun validate() : List<String> {
        val errors = LinkedList<String>()
        if (!actualUseMbeddr && actualUseIets3) {
            errors.add("Iets3 requires Mbeddr, you cannot enable Iets3 and disable Mbeddr")
        }
        return errors
    }

}