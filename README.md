Traffic
====

A project for CS032 to draw maps to screen, calculate shortest paths and give directions.

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
### Frontend ###
1. app <br>
-The app is loaded from the main based on the existence of the --gui flag

-You can enter --gui and --debug plus three filenames to start it

-The app loads the hub (for the GUI on a thread, for the REPL on the same thread without the autocorrect)

-Then it either opens a REPL loop or the window

-The REPL will accept input, check it for issues (and tokenize), and get information from the hub to show to the user

-The GUI will do the same via event handlers and the canvas
2. eventhandlers <br>
-There's a handler for autocorrect creation and for route creation

-Both callback the viewport and set the relevant information for the user

-Messages are displayed on the viewport

-These are created in the viewport

3. view <br>
-The viewport is the main class

-It opens a JFrame and JPanels for view

-Canvas is dynamically added via callback

-Canvas is a customized JComponent and has translation and scale that transforms LatLong to Screen and draws everything

-Repaint is called wherever needed

### Hub ###
The hub creates the XControllers of the backend and uses HubController, which implements the Controllable interface, to allow the frontend to communicate to the backend.

### Backend ###
Each project component has its own package, with a XController class in each which the Hub can use to construct and interact with these classes. 

1. KDTree <br>
2. Graph <br>
3. IO <br>
4. Autocorrect <br>


##Optimizations ##
Multi Threading
1. Initial loading
2. Predictive paging for GUI
3. Route finding for GUI

##How to Run Tests ##


