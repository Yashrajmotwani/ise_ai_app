# ise_ai_app
Creating the ISE app using AI tools.

## Features
1. Splash Screen with Logo
2. Login screen to Sign In from google email
3. Search for Open Positions using colleges, name of projects, etc.
4. Search for Faculty using Area of Research, colleges, department, name, etc.
5. On clicking the cardview, it opens a dialog view to display additional details of the positions or the faculty.
6. For faculty, if the Phone number or email is provided, on clicking that, it opens the Phone app with phone number set and the email app with the receiver email set.
7. Save, Remove Open Positions and Faculty Profiles from the Dialog view
8. Can see Saved data in separate Favorites sections using the Floating Action Button (FAB)
9. Implemented BottomSheet Dialog linked to the FAB to show other features
10. View different colleges using the Map API
11. View your current location and receive notification if within a radius of an IIT
12. View all the IITs, their details, and on clicking the cardview, can go to their respective websites
13. Switching between fragments with highlights and colors using Bottom Navigation View (kept at the top)
14. Sign Out feature

### Have updated the search to show a pop up in case no data is returned for open projects and teacher details.

## Working
To run the app properly, we need to start the render so that the link - BASE_URL in retrofit client will work and allow the connection from the database. The login details are shared in a file in the assignment submission in Classroom.  
If we do not want to use render and run the server.js locally, replace the BASE_URL (RetrofitClient.kt) to the IP in your system.
It will look something like this:
```
val BASE_URL = "http://a.b.c.d:5000/" // ipv4_address - a.b.c.d
```
We also need to change the uri in server.js to the connection string on the DataBase:
> const uri = mongodb+srv://<username>:<password>@cluster0.mongodb.net/<dbname>?retryWrites=true&w=majority  

To get your IP Address:
- Run in the Command prompt terminal:
```
ipconfing
```
- Pick the IPv4 address
  
Additionally, for all the app features to work make sure the following are enabled:
1. Internet - for connection to database
2. Location - for the Maps/Geofence features and sending notifications based on that  

Also, it should be ensured that app is allowed access to the location when the View Maps feature is used, which might require you to go back and restart that feature.
