Tak Server
==========

#### NOTE: NOT AT FULL FUNCTIONALITY YET

The tak server is a RESTful api that allows people to play each other online.
It can keep track of friends and allow them to play games against each other.
It can also set up random games against random players.

The API is described in detail [here](api.md).
A summary of the methods are described below:

| Call                                      | Type      | Description                                                               |
|-------------------------------------------|-----------|---------------------------------------------------------------------------|
| /account/register                         | POST      | Register a new user                                                       |
| /account/changepass                       | POST      | Change your password                                                      |
| /account/changename                       | POST      | Change your username                                                      |
| /account/authenticate                     | GET       | Check if the given credentials are valid                                  |
| /account/user                             | GET       | Get the username and ID from the username or ID                           |
| /social/request/create/{id}               | POST      | Create a friend request                                                   |
| /social/request/cancel/{id}               | DELETE    | Cancel a friend request                                                   |
| /social/request/respond/{id}              | POST      | Accept or deny a friend request                                           |
| /social/request/incoming                  | GET       | Get all your incoming friend requests                                     |
| /social/request/outgoing                  | GET       | Get all your outgoing friend requests                                     |
| /social/user/unfriend/{id}                | POST      | Unfriend a user                                                           |
| /social/user/block/{id}                   | POST      | Block a user                                                              |
| /social/user/unblock/{id}                 | DELETE    | Unblock a user                                                            |
| /social/user/blocked/{id}                 | GET       | Check if you are blocked by a user                                        |
| /social/user/friends                      | GET       | Get a list of your friends                                                |
| /social/user/blocking                     | GET       | Get a list for users you are blocking                                     |
| /social/message/send/{id}                 | POST      | Send a message to a user                                                  |
| /social/message/search                    | GET       | Search through your messages                                              |
| /social/message/markread                  | POST      | Mark a message as read                                                    |
| /social/notifications                     | GET       | Get the number of incoming friend requests and unread messages            |
| /games/request/create/{id}                | POST      | Create a request for a game                                               |
| /games/request/cancel/{id}                | DELETE    | Delete a game request                                                     |
| /games/request/respond/{id}               | POST      | Accept or deny a game request                                             |
| /games/request/incoming                   | GET       | Get all your incoming game requests                                       |
| /games/request/outgoing                   | GET       | Get all your outgoing game requests                                       |
| /games/request/random/create/{size}       | POST      | Create a request for a random game                                        |
| /games/request/random/cancel              | DELETE    | Delete your request for a random game                                     |
| /games/request/random/outgoing            | GET       | Check the information on your random game request                         |
| /games/search                             | GET       | Search your games                                                         |
| /games/game/{id}                          | GET       | Get the information on a game                                             |
| /games/game/{id}/possible                 | GET       | Get all the next possible moves for a game                                |
| /games/game/{id}/play                     | POST      | Play a move on a game                                                     |
| /games/notifications                      | GET       | Get the number of incoming game requests and games where it is your turn  |

