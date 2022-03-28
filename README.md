= CoAP Shell

https://bintray.com/big-data/maven/coap-shell/_latestVersion[ image:https://api.bintray.com/packages/big-data/maven/coap-shell/images/download.svg[Download] ]

https://en.wikipedia.org/wiki/Constrained_Application_Protocol[CoAP] is a RESTful web transfer protocol specialized for use with constrained nodes and constrained networks in the Internet of Things (IoT).

The https://github.com/tzolov/coap-shell[CoAP Shell] provides an interactive, command line interface for interacting with CoAP enabled servers.
It supports the `coap:` and `coaps:` schemas (e.g. UDP and https://en.wikipedia.org/wiki/Datagram_Transport_Layer_Security[DTLS]).
It also can manage your `IKEA TRÅDFRI` smart lights set ;)

The CoAP Shell is build on top of the https://projects.spring.io/spring-shell/[Spring Shell], https://www.eclipse.org/californium/[Californium (Cf)] and https://www.eclipse.org/californium/[Scandium (Sc)]
projects. It is a https://spring.io/projects/spring-boot[SpringBoot] application, that builds into a single, self-executable jar and runs on any Java8+ environment.

image:https://raw.githubusercontent.com/tzolov/coap-shell/master/src/test/resources/coap-shell-demo2.gif[CoAP Shell Demo]

=== Features
- Plain https://tools.ietf.org/html/rfc7252#section-6.1[coap:] and secured https://tools.ietf.org/html/rfc7252#section-6.2[coaps:] endpoints (e.g. `UDP` and `DTLS` transports).
- CoAP `GET`, `PUT`, `POST` and `DELETE` methods.
- CoAP Resource https://tools.ietf.org/html/rfc7641[Observing].
- CoAP Resource https://tools.ietf.org/html/rfc7252#section-7.2[Discovery]. Filters by `href`, `ct`, `rt`, `obs` ...
- `Synchronous` and `Asynchronous` (`--async` argument) message exchanges.
- https://tools.ietf.org/html/draft-ietf-core-observe-08#section-3.5[Confirmable] and `Non-Confirmable` message exchange.
- `TAB` auto-completion for `commands` and `arguments`.
- Extensive `commands` help (type `help`).
- Plugable key/trust stores and credentials.
- https://spring.io/projects/spring-boot[SpringBoot], self-executable jar, running in any Java 8+ environment.
- Basic support for `IKEA Tradfri Gateway`.

The https://youtu.be/zhEGFfCJwTg[CoAP Shell Video] highlights some of the features:

https://youtu.be/zhEGFfCJwTg[image:https://raw.githubusercontent.com/tzolov/coap-shell/master/src/test/resources/coap-shell-video-log.png[CoAP Shell video,157,54]]


=== Quick Start

* Get a pre-build https://bintray.com/big-data/maven/download_file?file_path=io%2Fdatalake%2Fcoap%2Fcoap-shell%2F1.1.1%2Fcoap-shell-1.1.1.jar[coap-shell.jar] or build one yourself following the instructions further down.
* Then start the shell:
  [source,bash]
----
java -jar ./coap-shell-1.1.1.jar
----

[source,bash]
----
  _____     ___   ___     ______       ____
/ ___/__  / _ | / _ \   / __/ /  ___ / / /
/ /__/ _ \/ __ |/ ___/  _\ \/ _ \/ -_) / /
\___/\___/_/ |_/_/     /___/_//_/\__/_/_/
CoAP Shell (v1.1.1)
For assistance hit TAB or type "help".

server-unknown:>
----

CAUTION: If you see an exception like: `Caused by: java.lang.NumberFormatException: For input string:` then you have to remove the existing `spring-shell.log` file in the same directory.

* Connect to a CoAP server (such as `coap://californium.eclipse.org/` or `coap://coap.me`)
  [source,bash]
----
server-unknown:>connect coap://californium.eclipse.org
available
coap://californium.eclipse.org/:>
----

* Discover the available CoAP resources:
  [source,bash]
----
coap://californium.eclipse.org/:>discover --query href=/*

┌─────────────────┬──────────────────┬────────────────────────────────────┬──────────────┬─────────┬────────────────┐
│Path [href]      │Resource Type [rt]│Content Type [ct]                   │Interface [if]│Size [sz]│Observable [obs]│
├─────────────────┼──────────────────┼────────────────────────────────────┼──────────────┼─────────┼────────────────┤
│/.well-known/core│                  │                                    │              │         │                │
│/large           │block             │                                    │              │1280     │                │
│/link1           │Type1, Type2      │                                    │If1           │         │                │
│/multi-format    │                  │text/plain (0), application/xml (41)│              │         │                │
│/obs             │observe           │text/plain (0)                      │              │         │observable      │
.....
----

* Get resource data
  [source,bash]
----
coap://californium.eclipse.org/:>get /multi-format --accept application/xml

----------------------------------- Response -----------------------------------
GET coap://californium.eclipse.org/multi-format
MID: 31291, Type: ACK, Token: [0a10eaafaf3d024f], RTT: 123ms
Options: {"Content-Format":"application/xml"}
Status : 205-Reset Content, Payload: 63B
----------------------------------- Payload ------------------------------------
<msg type="CON" code="GET" mid=31291 accept="application/xml"/>
--------------------------------------------------------------------------------

----

* Use `help` to the available commands and how are they used.
* Use `TAB` for command and argument auto-completion.

CAUTION: If you see `org.eclipse.californium.elements.EndpointMismatchException` error message that means that your DTLS session has expired
and you have to re-connect again.

=== IKEA TRÅDFRI Gateway Support

* Generate Gateway pre-share key
  Use the `ikea gateway key` to register a new account (e.g. identity + secret) to your IKEA Gateway:

[source,bash]
----
server-unknown:>ikea gateway key --ip 192.168.178.151 --identity myIkeaGatewayIdentity --security-code <Gateway Code Label>

-------------------------------- CoAP Response ---------------------------------
MID    : 58318
Token  : [60d1dcf80d8eb84f]
Type   : ACK
Status : 201-Created
Options: {}
RTT    : 371 ms
Payload: 45 Bytes
............................... Body Payload ...................................
{"9091":"X5xyYM41qFS7vNa9","9029":"1.3.0014"}
--------------------------------------------------------------------------------
IDENTITY: myIkeaGatewayIdentity , PRE_SHARED_KEY: X5xyYM41qFS7vN10
----

The `192.168.178.151` is the IP address of the gateway in your network. You should find the IP of your IKEA gateway box and use it instead!
The `myIkeaGatewayIdentity` is the new identity to be registered with the gateway. the `<Gateway Code Label>` is printed on the back side of the Gateway box.

The response `IDENTITY: myIkeaGatewayIdentity , PRE_SHARED_KEY: X5xyYM41qFS7vNa9` contains the new credential created for you.
Store the generated identity and secret so you can use them to interact with your IKEA gateway.

* Use the generated credentials to connect to the gateway

[source,bash]
----
server-unknown:>connect coaps://192.168.178.151:5684 --identity myIkeaGatewayIdentity --secret X5xyYM41qFS7vN10
available
coaps://192.168.178.151:5684:>
----
(Again substitute with the IP of your gateway. Use the identity and secret created in the previous step)

* List all devices registered with the gateway

[source,bash]
----
coaps://192.168.178.151:5684:>ikea device list

┌────────┬─────────┬──────┬───────────────────────────────┬────────┬───────────┬──────┐
│Instance│Name     │Type  │Model                          │Firmware│Battery [%]│ON/OFF│
├────────┼─────────┼──────┼───────────────────────────────┼────────┼───────────┼──────┤
│65537   │E27 LR4  │LIGHT │TRADFRI bulb E27 CWS opal 600lm│1.3.002 │-          │OFF   │
│65539   │GU10 WC  │LIGHT │TRADFRI bulb GU10 W 400lm      │1.2.214 │-          │OFF   │
│65536   │Remote LR│SWITCH│TRADFRI remote control         │1.2.214 │87         │-     │
│65542   │GU10 LR3 │LIGHT │TRADFRI bulb GU10 WS 400lm     │1.2.217 │-          │ON    │
│65540   │GU10 LR1 │LIGHT │TRADFRI bulb GU10 WS 400lm     │1.2.217 │-          │ON    │
│65541   │GU10 LR2 │LIGHT │TRADFRI bulb GU10 WS 400lm     │1.2.217 │-          │ON    │
│65538   │Sensor WC│SENSOR│TRADFRI motion sensor          │1.2.214 │100        │-     │
└────────┴─────────┴──────┴───────────────────────────────┴────────┴───────────┴──────┘
----

* Turn a lamp on/off

[source,bash]
----
coaps://192.168.178.151:5684:>ikea turn on --instance 65539
OK

coaps://192.168.178.151:5684:>ikea device list
┌────────┬─────────┬──────┬───────────────────────────────┬────────┬───────────┬──────┐
│Instance│Name     │Type  │Model                          │Firmware│Battery [%]│ON/OFF│
├────────┼─────────┼──────┼───────────────────────────────┼────────┼───────────┼──────┤
│65539   │GU10 WC  │LIGHT │TRADFRI bulb GU10 W 400lm      │1.2.214 │-          │ON    │


coaps://192.168.178.151:5684:>ikea turn off --instance 65539
OK

coaps://192.168.178.151:5684:>ikea device list
┌────────┬─────────┬──────┬───────────────────────────────┬────────┬───────────┬──────┐
│Instance│Name     │Type  │Model                          │Firmware│Battery [%]│ON/OFF│
├────────┼─────────┼──────┼───────────────────────────────┼────────┼───────────┼──────┤
│65539   │GU10 WC  │LIGHT │TRADFRI bulb GU10 W 400lm      │1.2.214 │-          │OFF   │

----

* Use the CoAP's `GET` check the raw message response

[source,bash]
----
coaps://192.168.178.151:5684:>get //15001/65539

----------------------------------- Response -----------------------------------
GET coaps://192.168.178.151:5684//15001/65539
MID: 30881, Type: ACK, Token: [260128b68be34371], RTT: 5ms
Options: {"Content-Format":"application/json", "Max-Age":604800}
Status : 205-Reset Content, Payload: 220B
----------------------------------- Payload ------------------------------------
{
"3311" : [ {
"5850" : 0,
"5851" : 203,
"9003" : 0
} ],
"9001" : "GU10 WC",
"9002" : 1528124737,
"9020" : 1528447038,
"9003" : 65539,
"9054" : 0,
"5750" : 2,
"9019" : 0,
"3" : {
"0" : "IKEA of Sweden",
"1" : "TRADFRI bulb GU10 W 400lm",
"2" : "",
"3" : "1.2.214",
"6" : 1
}
}
--------------------------------------------------------------------------------
----

NOTE: The CoAP Gateway follows (*partially!*) some of the https://github.com/OpenMobileAlliance/lwm2m-registry[OpenMobileAlliance] (formerly IPSO) Smart Object specs.
For example https://github.com/OpenMobileAlliance/lwm2m-registry/blob/test/3311.xml["3311"] block corresponds to the `Light Control` object and the `5850` is an mandatory `On/Off` attribute within this object.
Similarly the https://github.com/OpenMobileAlliance/lwm2m-registry/blob/test/LWM2M_Device-v1_1.xml[LWM2M_Device] section provides a `Device` manufacturer object spec.
The gateway is not `OpenMobileAlliance/IPSO` compliant though as some compulsory attributes are missing. The range of `9xxx` codes are proprietary. Some description of those codes can be found
https://github.com/eclipse/smarthome/blob/master/extensions/binding/org.eclipse.smarthome.binding.tradfri/src/main/java/org/eclipse/smarthome/binding/tradfri/TradfriBindingConstants.java[here].

* Next you can use CoAP's `PUT` method with JSON payload to turn a light On or Off.

Set `5850` to `1` for turning the lamp ON:

[source,bash]
----
coaps://192.168.178.151:5684:>put //15001/65539 --payload '{"3311":[{"5850":1}]}'
----

or `0` for turning it OFF:

[source,bash]
----
coaps://192.168.178.151:5684:>put //15001/65539 --payload '{"3311":[{"5850":0}]}'
----

Use the `//15001/<DEVICE ID>` template to address the device you want.

CAUTION: If you see `org.eclipse.californium.elements.EndpointMismatchException` message that means that your DTLS session has expired
due to IDLE timeout. You have to re-connect again.

=== How to Build

Clone the project from GitHub and build with Maven.

[source,bash]
----
git clone https://github.com/tzolov/coap-shell.git
cd ./coap-shell
./mvnw clean install
----

Then run the self-executable jar in the `target` folder.

=== Debugging

Start the shell with `--logging.level=DEBUG` to enable debug log level for the entire applicationor `--logging.level.org.eclipse.californium=DEBUG`
to debug only californium and scandium. Later is useful to debug the CoAP request message and DTLS interactions.

For example:
[source,bash]
----
java -jar ./target/coap-shell-1.1.1-SNAPSHOT.jar --logging.level.org.eclipse.californium=DEBUG
----
