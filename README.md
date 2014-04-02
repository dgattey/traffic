Traffic
====

A project for CS032 to draw maps to screen, calculate shortest paths and give directions.

##IDEAS##
Executables have string pass through that show client or server, main takes that and deals appropriately

##Authors##
aiguha
dgattey

##Backend Project Components ##
1. Autocorrect: dgattey
2. Stars: aiguha
3. Bacon: aiguha


## Known Bugs ##
None at this time.

##Design Details##


##Optimizations##
Multi Threading
1. Initial loading
2. Predictive paging for GUI
3. Route finding for GUI

##Protocol for Sending/Receiving##

Header/footer like "autocorrect" to wrap the data (so sending header, data objects, then footer)
Dropping client on server as easy as socket.close() and happens if footer doesn't get sent
On client, get IOException, closes gracefully, error message shows in dialog box
Convertible interface has encodeObject and decodeObject
Switch statement in Server and Client that correctly goes both directions

###Examples###

LatLongPoint Stringify:
<String rep Latitude> <String rep Longitude>

MapNode Stringify:
<String representing id>
<LatLongPoint stringify>

MapWay Stringify:
<String rep name>
<String rep id>
<start MapNode stringify>
<end MapNode stringify>

1. AUTOCORRECT
Request String:
<ac_req>
Thaye (String)
</ac_req>

Response String:
<ac_resp>
Thayer Street (String)
Thayer St (String)
Thayer Court (String)
Thayer Road (String)
</ac_resp>


2. INTERSECTION
Request:
<int_req>
Thayer Street (String)
Cushing Street (String)
</int_req>
RESPONSE:
<int_resp>
/n/4171.12009 (String representing MapNode id)
41.7123 -71.1341 (String rep MapNode's LatLongPoint)
end_int_res
</int_resp>

3. ROUTE
<route_req>
Thayer Street
Cushing Street
Thayer Street
Waterman Street
</route_req>

<route_resp>
Thayer Street (String rep name)
/w/1234.5678.12345.0.0 (String rep way id)
/n/1234.5678 (String rep start MapNode's id)
41.7123 -71.1341 (String rep start MapNode's LatLongPoint)
/n/1234.5678 (String rep end MapNode's id)
41.7123 -71.1341 (String rep end MapNode's LatLongPoint)
...
</route_resp>





##How to Run Tests ##


