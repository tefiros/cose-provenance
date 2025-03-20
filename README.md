# Reference Implementation of COSE Signatures for YANG Data Provenance
 
This Java project provides a set of classes and methods to handle serilized YANG files, to generate signatures and verify them making use of the COSE (CBOR Object Signing and Encryption) library. Signatures are enclosed within the serialized YANG data as elements, and can be verified to guarantee the integrity and authenticity of the data.

### 1. Project Structure

1.1. com.telefonica.api

- EnclosingMethods: Methods to enclose the signature within the YANG data.
- Parameters: Manages the load and access of the configuration file.
- Signature: Implements the generation of COSE signatures.
- Verification: Implements the verification of COSE signatures.
- XMLFileManagement: Auxiliary methods for XML file management.

1.2. com.telefonica.api.example

- Signer: Use case to sign XML YANG files.
- Verifier: Use case to verify signatures.

### 2. Archivos

2.1. src/main/resources/cose_parameters.json: 

Configuration file that contains the configuration parameters.

### 3. Use

3.1. Prerequisites

- Java 8 o superior.
- Dependencies:
   - JDOM 2
   - Gson
   - BouncyCastle
   - COSE-Java
   - XMLSEC

3.2. Compilation

Gradle has been used as a construction system to handle dependencies

3.3. Execution

3.3.1. Signing 

To sign a document, execute Signer.java class with the route of the XML file to be signed as argument:

```
//mvn exec:java -Dexec.mainClass="com.telefonica.cose.provenance.example.Signer" -Dexec.args="<YANGDataFile>.xml"

gradle run 
```

This will generate a file named 'provenance-interfaces.xml'	in the current directory.

3.3.2. Verification

To verify signature, execute Verifier.java class with the route of the previously signed XML document as an argument

```
mvn exec:java -Dexec.mainClass="com.telefonica.cose.provenance.example.Verifier" -Dexec.args="provenance-interfaces.xml"
```

This will try ti verify the signature and will return the result of the success or failure.

### 4. Implementation

4.1. Enclosing Methods

The EnclosingMethods class provides methods to enclose signatures within the XML documents:

 - enclosingMethod: corresponds to the 'Including a Provenance Leaf in a YANG Element' method of the draft
 - enclosingMethod2: corresponds to the 'Including a Provenance Signature in NETCONF Event Notifications and YANG-Push Notifications' method of the draft
 - enclosingMethod3: corresponds to the 'Including Provenance as Metadata in YANG Instance Data' method of the draft
 - enclosingMethod4: corresponds to the 'Including Provenance in YANG Annotations' method of the draft
 
4.2. Signature 

Signature.java class handles the creation of COSE signatures using the private key stored in a KeyStore. The signature generated is serialized to a Base64 string.

4.3. Verification

Verification.java class manages the verification of the COSE signatures. It extracts the signature from the YANG data it is enclosed in and verifies it using both the signature and the YANG module without the signature element, and the public key associated with the private key used in the generation of the signature.

4.4. Parameters

Parameters.java class loads the necessary parameters from an JSON document used as configuration file. These parameters include name of the signature element, key ID and other relevant values needed for the generation and verification of the signatures.

### 5. Other commands in CMD:
	
Removes the .class generated (if they exist), it will download the libraries as defined in the pom.xml dependencies

`mvn clean`
	
Compiles the code. Generates .class out of the .java.

`mvn compile`

Generate jar or war files, depending on what has been defined in the pom.xml

`mvn install` 

### 5. Library

A packet .jar of the project as dependency for other projects has been released.
