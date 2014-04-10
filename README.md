Traffic
========
*By aiguha and dgattey*

A project for CS032 to draw maps to screen, calculate shortest paths around map,
 and give directions. Includes traffic data sent by random server.

###Backend Project Components
1. Autocorrect: dgattey
2. Stars: aiguha
3. Bacon: aiguha

### Known Bugs
None at this time!

###How to Run
- **Creating the executables:** ant create_exec && ant jar
- **Running server:** bin/trafficServer info/ways.tsv info/nodes.tsv
 info/index.tsv localhost 9999 10000
- **Running client:** bin/trafficClient localhost 10000 (with
   --debug flag if you want errors to print)

##Design Details
We took our **frontend** package from Maps and made it the **client** package.
 The **backend** package became the **server** package. Because of our earlier
separation, we were able to simply change the **hub** controller and add a
**core** server package and everything else could stay the same. The **main**
package has a Main class that given the appropriate number of flags and the
correct arguments, starts up either a ClientApp or a ServerApp. Given our
interfaces, everything is pretty seamless.

Our client, server, and traffic bot all can be connected
together in any order. If no connection exists, the client will attempt to reconnect
to the server, and the server will attempt to reconnect to the traffic server
every few seconds. Basically, because of this, no weird connection errors will
happen. It'll either work and connect, or it'll try again in a bit.

###Client
The client simply spawns a GUI and attempts a connection. It starts a timer that
attempts a connection every few seconds. If it can't connect, the bar at bottom
will be red, all traffic data disappears, and the route disappears. If there's a
connection, the bottom bar is green and everything works perfectly. Routing works
by clicking two points or entering four streets and pressing the Route button.
If nothing is found, the appropriate status will be shown. Otherwise, it'll move
you to the start location of the route (and highlight it). Traffic is shown on
the map from a range of yellow to red, red being worst traffic. White is no traffic.
As you zoom out, small roads (under 3 pixels) will disappear for quicker drawing.
Anything outside the bounds of the current view won't even be drawn. Chunks load
themselves when the view bounds get close enough to another chunk. Everything
goes through the HubController which makes a CommController to communicate with
the server. We used Callables to return a value from that thread, and an
ExecutorService to run it.

###Server

##Optimizations
###Multithreading
1. Initial loading of all nodes (for KDTree)
2. Predictive paging for GUI
3. Route finding for GUI
4. 20 chunks loading at a time, no more

###Other
1. Only having one request for a route at a time
2. Cancelling of threads on server and client
3. Reusing objects as much as possible (HashMaps and HashSets)
4. Each request runs on its own thread

###Protocol for Sending/Receiving
We had a ProtocolManager that...

##Testing
Test our code by running the following:
- **Server system tests:** ant server_test
- **Client system tests:** ant client_test
- **JUnit tests:** ant unit_test

###By Hand
We extensively tested the interactions between the client and server by running
netcat and tossing bad data at both sides. Additionally, we started a server and
connected five clients to it and did everything we could to make it mad. Lots of
long routes, concurrent requests, same location requests, etc. Additionally, we
tested closing the connection from the server to the client and vice versa in
the middle of a request. No matter what order we did things, nothing failed!
Additionally, we modified the traffic bot to send us information every x amount
of time, and tested it with requests every 0.0000001 seconds. The whole map
filled up, but even with crazy data happening, routes completed and reflected the
traffic at the moment at which the route was created.
