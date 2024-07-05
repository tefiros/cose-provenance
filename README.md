# Reference Implementation of COSE Signatures for YANG Data Provenance
 
This Java project provides a set of classes and methods to handle serilized YANG files, to generate signatures and verify them making use of the COSE (CBRO Object Signing and Ecryption) library. Signatures are enclosed within the serialized YANG data as elements, and can be verified to guarantee the integrity and authenticity of the data.

## Content List

- [Project Structure](#project-structure)
  - [com.telefonica.api](#com.telefonica.api)
  - [com.telefonica.api.example](#com.telefonica.api.example)
- [Files](#files)
- [Use](#use)
  - [Prerequisites](#prerequisites)
  - [Compilation](#compilation)
  - [Execution](#execution)
    - [Signing](#signing)
    - [Verification](#verifictaion)
- Implementation](#implementation)
  - [Enclosing Methods](#enclosing-methods)
  - [Signature](#signature)
  - [Verification](#verification)
  - [Parameters](#parameters)

## Project Structure

### com.telefonica.api
EnclosingMethods: Methods to enclose the signature within the YANG data.
Parameters: Manages the load and access of the configuration file.
Signature: Implements the generation of COSE signatures.
Verification: Implements the verification of COSE signatures.
XMLFileManagement: Auxiliary methods for XML file management.

### com.telefonica.api.example
Signer: Use case to sign XML YANG files.
Verifier: Use case to verify signatures.

## Files

src/main/resources/cose_parameters.json: Configuration file that contains the configuration parameters.

## Use

### Prerequisites
- Java 8 o superior.
- Bibliotecas adicionales:
   -JDOM 2
   -Gson
   -BouncyCastle
   -COSE-Java
   -XMLSEC

### Compilation
Maven has been used as a construction system to handle dependencies

### Execution

#### Signing 
To sign a document, execute Signer.java class with the route of the XML file to be signed as argument:

``mvn exec:java -Dexec.mainClass="com.telefonica.api.example.Signer" -Dexec.args="<YANGDataFile>.xml"``

This will generate a file named 'provenance-interfaces.xml'	in the current directory.

#### Verification
To verify signature, execute Verifier.java class with the route of the previously signed XML document as an argument

``mvn exec:java -Dexec.mainClass="com.telefonica.api.example.Verifier" -Dexec.args="provenance-interfaces.xml"``

This will try ti verify the signature and will return the result of the success or failure.

## Implementation

### Enclosing Methods
The EnclosingMethods class provides methods to enclose signatures within the XML documents:

 -enclosingMethod: corresponds to the 'Including a Provenance Leaf in a YANG Element' method of the draft
 -enclosingMethod2: corresponds to the 'Including a Provenance Signature in NETCONF Event Notifications and YANG-Push Notifications' method of the draft
 -enclosingMethod3: corresponds to the 'Including Provenance as Metadata in YANG Instance Data' method of the draft
 -enclosingMethod4: corresponds to the 'Including Provenance in YANG Annotations' method of the draft
 
### Signature 
Signature.java class handles the creation of COSE signatures using the private key stored in a KeyStore. The signature generated is serialized to a Base64 string.

### Verification
Verification.java class manages the verification of the COSE signatures. It extracts the signature from the YANG data it is enclosed in and verifies it using both the signature and the YANG module without the signature element, and the public key associated with the private key used in the generation of the signature.

### Parameters

Parameters.java class loads the necessary parameters from an JSON document used as configuration file. These parameters include name of the signature element, key ID and other relevant values needed for the generation and verification of the signatures.
