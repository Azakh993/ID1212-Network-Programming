# Laboration 2: A Simple HTTP Server with Sockets

## Task

Your task is to create a guessing game using sockets with the following dialogue (when connecting to your web browser):

[Guessing Game](https://www.csc.kth.se/~stene/guess.php)

Program Requirements:

- Implement at least three classes:
  1. A class for handling incoming HTTP requests from the web browser (Controller).
  2. A class for the game logic (session ID for the client, number of guesses, secret number; this represents the Model).
  3. A class for generating HTTP responses with HTML (View).
- Use the `java.net.Socket` and `java.net.ServerSocket` classes.

Note:

- Each new client connection should lead to a new instance of the game (a new game logic object) and a "Set-Cookie" field added to the HTTP response.
- There should be one thread per web browser client (you can reuse code from the previous laboratory).
- The browser may make an additional request for the bookmark icon "favicon.ico" (browser-dependent, test and handle this). You need to filter this request somehow.
- A new web browser window (but not tab) usually creates a new session (browser-dependent, test and handle this).
- You should only use Java SE in the solution; Java EE is not allowed (it will be covered in later labs).

### Extra Task: Use `java.net.HttpURLConnection`

Use `java.net.HttpURLConnection` to simulate a web browser and play the game 100 times, then present the average number of guesses.

Note:

- If you are using JDK 11 or higher, you can use the `java.net.http.HttpClient` class instead of `HttpURLConnection`.

## Note

- If working in pairs, only one should upload the code to avoid plagiarism.
- Upload the code before or during the presentation.
