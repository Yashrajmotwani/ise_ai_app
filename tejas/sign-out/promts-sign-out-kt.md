```
Generate a SignOut activity in Kotlin for an Android app. It should 
extend AppCompatActivity and override onCreate() to set the layout using setContentView().
```
```
Inside SignOut.kt, initialize Firebase Authentication (mAuth) and Google Sign-In 
(mGoogleSignInClient). Use GoogleSignInOptions.Builder to request the ID token and email, then get the GoogleSignInClient instance.
```
```
Find the TextView with ID name from the layout and set up Firebase authentication 
to get the current user. Store the authenticated user in a variable but don’t modify the UI for now
```
```
Find the logout button (logout_button) using findViewById() and set a click listener 
on it. When clicked, it should call a function to sign out the user and navigate to the SignInActivity.
```
```
Create a function signOutAndStartSignInActivity() that first signs out the user from 
Firebase using mAuth.signOut(). Then, it should sign out from Google using mGoogleSignInClient.signOut().
 Log success or failure messages and navigate back to SignInActivity, finishing the current activity
```
```
Add Log.d() messages to confirm when the user signs out successfully. Also, add Log.e() to 
catch errors if sign-out fails.
```