# samvaad
 ![database structure](https://github.com/danish-angural/samvaad/blob/master/images/databasestructure1.jpeg.jpeg) 
  ![database structure](https://github.com/danish-angural/samvaad/blob/master/images/databasestructure2.jpeg)
 the app has the following features
1) an account can be linked to both a phone number.
while submitting a phone number, OTP verification is required. if the mobile number in on the same mobile from which the application is accessed, the number is Auto-verified, and user logs into the app.
2) on login, users are redirected to a settings page, which they can lave only after entering a username.
3) all the users of the app can be found on the find friends page, and there is a send friend request button only in front on those users which ave not been sent friend requests or those which are contacts. if the request is accepted, user is added to contact of each other.
4) there is also a group chat page which, for now, every one can access 
5) in the chat page, long press on a message deletes the message.(currently, this action messes up all further messages in that chat.)
6) on clicking the toolbar on a chat page, you are redirected to the profile of the user.
 
some edits i will add later-
1. correct the delete message option.
2. make the groups not public, by allowing only the group creator to add members, by assigning another field to every group having the creators name.
3. enable notifications.
4. restrict the users displayed in find friends column.
5. uploading profile image from camera.

**EDIT 1** at 6:30am/ 18.04.20
1) corrected the delete message functionality by setting the key to each message dependent on time and not the number of messages present.
2) added the last seen and online functionality.
3) changed the chat acivity so that the latest chat comes up first.

ps- online functionality causing app to slow down because of repeated change in state, so will have to be removed.
edit: the online functionality has been removed
