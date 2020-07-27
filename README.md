# Bacon-Game

### To Use
Just compile and run in your favorite Java IDE. A menu will show up with all of the possible ways to manipulate the graph. 


### Overview
An actor's "Bacon Number" is the number of degrees of separation between them and Kevin Bacon. For example, Jennifer Lopez has a Bacon Number of 2 because she appeared in U Turn (1997) with Sean Penn, and Sean Penn appeared in Mystic River (2003) with Kevin Bacon. This game allows you to calculate the Bacon Number of thousands of actors using graph analysis. It creates a large graph and runs a Breadth First Search to determine the shortest path from each actor to the "center of the universe." Although it starts as Kevin Bacon, because he is a very well connected actor, the user can update the "center" to be any actor. 

Other functinality includes sorting the actors by the number of connections they have, showing the actors which are completely unconnected from the current center, and showing the best centers of the universe by average separation. This last method takes roughly 40sec the first time, as the program is looping over every possible center, running BFS and calculating the average separation. 

Note: to run you may have to change the file names to their absolute paths. 
