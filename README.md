Traffic
====

A project for CS032 to draw maps to screen, calculate shortest paths and give directions.

##TODO##
- Take out error print lines
- Error checking!!!
	- Make sure empty index files work (junit tests)
- JUnit tests
- System tests
- README
- Commenting
- Server status print - if connected to traffic, num clients, host name/port num, num threads?
- Heartbeat ID in Server to have actual clients print out?
- Other REPL stuff for Server??

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


