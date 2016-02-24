### Design outlines:
1. A RoverSquad controls a set of underlying rovers
2. The number of Rovers is set to 10 in the application configuration
3. The RoverSquad receives three type of messages: DimensionText, PositionText, MotionText, for the plateau dimension, the initial position of a rover and the motion sequence respectively. 
4. The Rover operates in three different modes: Initially it starts with the "intitalization" mode accepts only dimension messages. once receieved, it swiches to the 'Positining' mode where it expects Initial position messages only. once received the behavior changes to the 'operating'class receives a command string, parses it and then attempt to execute it.
3. A Rover holds a state enfolding its current position and the dimension of the plateau.
4. A rover validates that the requested movement sequence, doesn't take it beyound the given plateau, or else it stops working

###TODO List
1. Exception handling Mechanism needs to be implemented. RoversSquad has to supervise its children and watch their errors and react in a convenient way, according to a predefined supervisionStratgey
2. console messages to be removed
3. Assertion based test cases are yet to be implemented 
4. Property based test cases, are yet to be implemented 
5. Akka test kit, to be used for testing the messaging framework.
6. Mysterious numbers have to be removed and replaced with application configuration parameters, with descriptive names.
7. Sequential Mechanism is not implemented correctly. The RoversSquad has to be mandate that no more positioning / movement commands are being sent to any other rover, until the current one is done with its mission. The way how it is implemented now, is not correct, and causes the second set of commands (position / motion sequence) to fail.

###How To Run
java -jar rovers-simulator.jar -f=../../sample-commands
OR
java -jar rovers-simulator.jar (will pick up the file from the same location of the jar)
