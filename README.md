# Reference Applying COSE Signatures for YANG Data Provenance
 
This Java project provides a set of classes and methods to handle serialized YANG files, to generate signatures and verify them making use of the COSE (CBOR Object Signing and Ecryption) library. Signatures are enclosed within the serialized YANG data as elements, and can be verified to guarantee the integrity and authenticity of the data.
The IETF draft can be seen at [Applying COSE Signatures for YANG Data Provenance](https://datatracker.ietf.org/doc/draft-lopez-opsawg-yang-provenance/06/).

### 1. Project Structure

1.1. com.telefonica.cose.provenance

- XMLEnclosingMethods: Methods to enclose the signature within the YANG data.
- JSONEnclosingMethods: Methods to enclose the signature within the YANG data.
- Parameters: Manages the load and access of the configuration file.
- XMLSignature: Implements the generation of COSE signatures.
- JSONSignature: Implements the generation of COSE signatures.
- XMLVerification: Implements the verification of COSE signatures.
- JSONVerification: Implements the verification of COSE signatures.
- XMLFileManagement: Auxiliary methods for XML file management.

1.2. test

- Signer: Use case to sign XML or JSON YANG files.
- Verifier: Use case to verify the previous signed documents.

### 2. Archivos

2.1. src/main/resources/cose_parameters.json: 

Configuration file that contains the configuration parameters (keys).

### 3. Use

3.1. Prerequisites

- Java 8 o superior.
- Bibliotecas adicionales:
   - JDOM 2
   - Gson
   - BouncyCastle
   - COSE-Java
   - XMLSEC
   - JacksonBind 
   - JCS canonicalization

3.2. Compilation

Gradle has been used as a construction system to handle dependencies

3.3. Execution

3.3.1. Signing 

To sign a document, execute Signer.java class with the route of the XML/JSON file to be signed as argument:
This will generate a file with a selected named	in the current directory.

3.3.2. Verification

To verify signature, execute Verifier.java class with the route of the previously signed JSON/XML document as an argument
This will try to verify the signature and will return the result of the success or failure.

### 4. Implementation

4.1. Enclosing Methods

The EnclosingMethods classes (JSONEnclosingMethods and XMLEnclosingMethods) provides methods to enclose signatures within the XML documents:

 - enclosingMethod: corresponds to the 'Including a Provenance Leaf in a YANG Element' method of the draft
 - enclosingMethod2: corresponds to the 'Including a Provenance Signature in NETCONF Event Notifications and YANG-Push Notifications' method of the draft
 - enclosingMethod3: corresponds to the 'Including Provenance as Metadata in YANG Instance Data' method of the draft
 - enclosingMethod4: corresponds to the 'Including Provenance in YANG Annotations' method of the draft
 
4.2. Signature 

(XML/JSON)Signature.java class handles the creation of COSE signatures using the private key stored in a KeyStore. The signature generated is serialized to a Base64 string.

4.3. Verification

(XMl/JSON)Verification.java class manages the verification of the COSE signatures. It extracts the signature from the YANG data it is enclosed in and verifies it using both the signature and the YANG module without the signature element, and the public key associated with the private key used in the generation of the signature.

4.4. Parameters

Parameters.java class loads the necessary parameters from an JSON document used as configuration file. These parameters include name of the signature element, key ID and other relevant values needed for the generation and verification of the signatures.

### 5. Other commands in CMD:
	
Removes the .class generated (if they exist), it will download the libraries as defined in the build.kts dependencies

`gradle clean build`


### Library:

To be used as library dependency in other projects download in the releases the .jar of the lastest version of the code as well as its javadoc for reference.
Then introduce it in /libs folder of your project and use implement it in the pom.xml/build.kts

### License

Copyright 2025 Telefonica (TID)

Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License. You may obtain a copy of the License at

http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License for the specific language governing permissions and limitations under the License.