# Laboration 3: SSL/TLS-encrypted Sockets

## Task

Your task is to write a program that connects to your @kth.se email account, lists its contents, and then retrieves an arbitrary email. You are not allowed to use JavaMail; instead, you should perform these tasks "manually" following the IMAP and SMTP protocols. The email server configuration is as follows (from the KTH intranet):

Incoming Email Settings (IMAP):
- Server: webmail.kth.se
- Port: 993
- Protocol: SSL/TLS
- Authentication: Normal password

Outgoing Email Settings (SMTP):
- Server: smtp.kth.se
- Port: 587
- Protocol: STARTTLS
- Authentication: Normal password

Note:

- In the first case (IMAP with SSL/TLS), you start with an encrypted session, and in the second case (SMTP with STARTTLS), you transition to encryption during the session.
- The complete documentation of IMAP can be found in [rfc3501](https://tools.ietf.org/html/rfc3501), but for this task, you can compare it with an IMAP session like the one [here](https://en.wikipedia.org/wiki/Internet_Message_Access_Protocol).
- Useful examples of SMTP sessions can be found [here](https://www.samlogic.net/articles/smtp-commands-reference.htm). To send a username and password in SMTP, you need to use Base64 encoding, and [this link](https://docs.oracle.com/javase/8/docs/api/java/util/Base64.html) can be helpful.
- To avoid sharing the password accidentally, place it in a separate file and read it into the program.

You do not need a certificate for this subtask (authentication is done solely with a password).

Requirements: You should be able to explain how public and private keys in an asymmetric cipher, combined with a symmetric cipher, provide secure transmission of a symmetric key (Alice and Bob) and how cryptographic hashing ensures data integrity.

## Note

- If working in pairs, only one should upload the code to avoid plagiarism.
- Upload the code before or during the presentation.
