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
The (____) signify the object being represented by the string

1. AUTOCORRECT
Request String:
start_ac_req
Thaye (String)
end_ac_req

Response String:
start_ac_res
Thayer Street (String)
Thayer St (String)
Thayer Court (String)
Thayer Road (String)
end_ac_res


2. INTERSECTION
Request:
start_int_req
Thayer Street (String)
Cushing Street (String)
end_int_req

Response:
start_int_res
/n/4171.12009 (String representing MapNode id)
41.7123 -71.1341 (LatLongPoint)
end_int_res








##How to Run Tests ##


