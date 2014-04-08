Traffic
====

A project for CS032 to draw maps to screen, calculate shortest paths and give directions.

##TODO##
- BufferedWriter not PrintWriter so sending traffic will cancel closed clients
- First time traffic data
- Protocol for sending list of traffic data
- Coloring
- Route being null in front end?
- Hashmap for traffic data (for the routes too)
- ID thing for routing (ID already exists in the header)
- Error checking!!!
- Testing

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

Dropping client on server as easy as socket.close() and happens if footer doesn't get sent
On client, get IOException, closes gracefully, error message shows in dialog box
Switch statement in Server and Client that correctly goes both directions

<way:
______ (id)
______ (name)
______ (startid)
#llp: __ __ 
______ (endid)
#llp: __ __
>

<chunk:
#llp: __ __
	<list:way:20
		<way:
		>
		<way:
		>
		<way:
		>
	>
>

AC:

RS:

RC:

MC:



##How to Run Tests ##


