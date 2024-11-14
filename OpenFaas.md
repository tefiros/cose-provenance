# Applying COSE Signatures for YANG Data Provenance. Serverless version

## COSE

The IETF Operations and Management Area Working Group (OP SAWG) is working on a standard method to check the origin and integrity of network monitoring data using YANG-compliant data models. This method proposes to use COSE (CBOR -Concise Binary Object Representation- Object Signing and Encryption) digital signatures. With the increase of evidence-based Operations, Administration and Maintenance (OAM) automation applications for IoT devices and the use of artificial intelligence or machine learning tools, origin validation becomes more important in all scenarios. 

This project suggests a method to incorporate digital signatures for YANG data, using COSE to compress and reduce the necessary resources for its calculation. The implementation of the code corresponds to the reference implementation of the IETF draft Applying COSE Signatures for YANG Data Provenance. The implementation applies to any serialization of YANG data that accepts a clear canonicalization method, although this project focuses on XML format.
At the moment the software provide a static run of the COSE signature algorithm. With the current version, you provide a file (in a precise local directory), you run the Signer/Verifier Java code and you receive the result from the performed action.

We see as a possible natural evolution of this software, a change on the project structure, that, aim to transform the static code run, to a more flexible RESTful API endpoint. To perform the sign/verify action, in this new version, you have to send the file as body (content-type: XML, the idea is to do the same with JSON and CBOR) and receive the processed one as response body.
In addition, the idea is to develop two version of the software with two approach to achieve the same result. One version will be a stateful version of the software, I.e. an always running containerized Java app, and another version that leverage the serverless paradigm, i.e. allocate and run only when the action is triggered by the user.
 
The plan to perform these updates are the following:
 
1.	Translate the current Maven compiled version to a Gradle compiled project, in order to be more flexible to future addition
2.	Transform the actual static code to a RESTful API endpoint by putting “in front” of the current backend a local HTTP server, with two possible route /sign and /verify to trigger.
3.	Dockerize the new version of code (RESTful app)
4.	Adapt the stateful app to the serverless approach (we are exploring OpenFaas framework)
5.	To analyze the two behaviors (stateful /stateless) plan some performance tests (to be defined ...)

## OpenFaas version

It changes the dockerfile used in order to work with the OpenFaas platform.
First, we package the java project using gradle into a jar file.  This jar will be executed in the monitored process and exposed in the function using the Openfaas watchdog.

The OpenFaaS watchdog is responsible for starting and monitoring functions in OpenFaaS. Any binary can become a function through the use of watchdog.
The watchdog becomes an "init process" with an embedded HTTP server written in Golang, it can support concurrent requests, timeouts and healthchecks. 

The main classes were adapted as well, deleting the local HTTP server. And the build.gradle.kts was changed in order to build the jar changing the main class creating one image for the signer function and another one for the verifier function.

## Types of enclosing methods

### 1. Including a Provenance Leaf in a YANG Element:
It requires a specific element in the YANG schema defining the element to be signed (the enclosing element). When using this enclosing method, a provenance-signature leaf MAY appear at any position in the enclosing element, but only one such leaf MUST be defined for the enclosing element.

```
<?xml version="1.0" encoding="UTF-8"?>
<interfaces-state xmlns="urn:ietf:params:xml:ns:yang:ietf-interfaces">
 <provenance-string>
0oRRowNjeG1sBGdlYzIua2V5ASag9lhAvzyFP5HP0nONaqTRxKmSqerrDS6CQXJSK+5NdprzQZ
Lf0QsHtAi2pxzbuDJDy9kZoy1JTvNaJmMxGTLdm4ktug==
 </provenance-string>
<interface> …..</interface></interfaces-state>
```


### 2. Including a Provenance Signature in NETCONF Event Notifications and YANG-Push Notifications
The signature is added to the header of the Notification along with the eventTime leaf. The YANG content to be processed MUST consist of the content of the notificationContent element.

```
<?xml version="1.0" encoding="UTF-8"?>
<notification xmlns="urn:ietf:params:xml:ns:netconf:notification:1.0">
 <eventTime>2024-02-03T11:37:25.94Z</eventTime>
 <notification-provenance>
0oRRowNjeG1sBGdlYzIua2V5ASag9lhADiPn3eMRclCAnMlauYyD5yKFvYeXipf4oAmQW5DURE
izL59Xs5erOerbryu8vs+A8YOl8AhlAUFzvThffcKPZg==
 </notification-provenance>
 <push-update xmlns="urn:ietf:params:xml:ns:yang:ietf-yang-push">
 <subscription-id>2147483648</subscription-id>
 <datastore-contents-xml>
 <interfaces-state xmlns="urn:ietf:params:xml:ns:yang:ietfinterfaces">
 <interface> …..</interface></interfaces-state>
```

### 3. Including Provenance as Metadata in YANG Instance Data

Provenance signature strings can be included as part of the metadata in YANG instance data files, as defined in [RFC9195] for data at rest.

*No hay ejemplo en el draft*



### 4.	Including Provenance in YANG Annotations

The use of annotations as defined in [RFC7952] seems a natural enclosing method, dealing with the provenance signature string as metadata and not requiring modification of existing YANG schemas.

```
<?xml version="1.0" encoding="UTF-8"?>
<interfaces-state xmlns="urn:ietf:params:xml:ns:yang:ietf-interfaces"
xmlns:ypmd="http://telefonica.com/temporary-ns-yangpmd"
ypmd:provenance-string=
"0oRRowNjeG1sBGdlYzIua2V5ASag9lhAzen3Bm9AZoyXuetpoTB70SzZqKVxeuOMW099sm+NX
SqCfnqBKfXeuqDNEkuEr+E0XiAso986fbAHQCHbAJMOhw==">
 <interface> …..</interface></interfaces-state>
```

## DEMO

To enter the virtual machine we have left with faasd installed

`ssh ubuntu@192.168.159.75`

### Use signer with enclosing method 1:

`curl -X POST http://192.168.159.75:8080/function/cose-signer --data-binary "@ietf-interfaces.xml" -o  "signed-interfaces.xml"`

With the -data-binary flag ‘@file.xml’ you can send the document (it must be in XML format but does not need to follow the YANG model) to sign that is in the folder from where curl is running.

With the -o flag ‘signed.xml’ the signed document is downloaded. If not used, the result will be returned on the screen. 

You can also curl by sending the xml text as message body of the POST.


### Use signer with enclosing method 2:

In this case a file containing NETCONF Event Notifications and YANG-Push Notifications shall be used as input XML document. The signature shall be inserted after the eventTime inside the notification. Here we have used the file ‘netconf-interfaces.xml’.


`curl -X POST http://192.168.159.75:8080/function/cose-signer-em2 --data-binary "@netconf-interfaces.xml" -o signed-netconf-interfaces.xml`


### Use signer with enclosing method 3:

In this case the input XML document shall be metadata. 

`curl -X POST http://192.168.159.75:8080/function/cose-signer-em3 --data-binary "@ietf-interfaces-metadata.xml"  -o metadata-signed.xml`

### Use signer with enclosing method 4:


The signer enclosing method 1 and 4 signs any type of XML but in this case the signature is encapsulated as an attribute within the annotation.

`curl -X POST http://192.168.159.75:8080/function/cose-signer-em4 --data-binary "@ietf-interfaces.xml" -o signed-EM4-interfaces.xml`

### Use verifier for all enclosing methods (universal verifier):

If we pass a signed document as a parameter, the function will extract the provenance string (the signature) previously added and verify that it is correct.

If it is not correct, an error will be returned.  It can be a COSE exception or the message ‘Invalid signature’. In the first case it is thrown when it detects that the signature added to the document cannot be decoded according to the COSE method, this is because the signature is different from the one previously used. In the second case, the signature has been extracted but the content of the XML file is not the same as the one that has been signed.


`curl -X POST -H "Content-Type: application/xml"   --data-binary @signed-interfaces.xml   http://192.168.159.75:8080/function/cose-verifier`

The verifier function is universal for all types of enclosing method.


## LAST UPDATES

 - **Fixed the bug of carriage return, new line and any kind of unimportant blank space:** Now when processing the input document, before canonicalisation, a preprocessing is done in which all the spaces created by \r or \n and/or combination of these are deleted. 
**IMPORTANT:** Documents previously signed with the previous version, as they have not been canonicalised in the same way and signed, cannot be verified with the current code version.