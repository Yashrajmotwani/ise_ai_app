```
 Right now, when the user clicks the sign-in button, there's no indication that the process is happening. Add a progress bar or loading spinner that appears when authentication starts and disappears when it’s complete
 ```
 ```
 Currently, if authentication fails, the app only shows a toast message. Improve error handling by detecting specific errors like network issues, incorrect credentials, or account restrictions, and provide detailed messages accordingly.
 ```
 ```
  If a user has signed in before, they shouldn't need to sign in again every time they open the app. Implement a feature using SharedPreferences to store the sign-in state and automatically log in the user without showing the sign-in screen.
  ```
  ```
   Right now, Google Sign-In is the only authentication method. Expand this by adding support for email/password authentication with Firebase so users have multiple options to sign in.
   ```
   ```
   Once the user logs in, they currently can’t log out. Modify MainActivity.kt to include a sign-out button that clears the authentication state and returns the user to SignInActivity.
   ```