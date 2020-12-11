## Lab 5 Client-Server app: authentication

### Part 1

I have take following measures to ensure application security:

1. Password hashing:
    - hash passwords with Bcrypt
    - use salt for each password
2. Input validation:
    - make sure that no empty fields are submitted
    - validate length ranges to make sure no too long inputs provided
    - enforce password length to be at least 8 chars, 64 chars max
    - allow any characters in passwords to be submitted
    - validate email with regex (using framework provided validation)
    - both client-side and server-side validation
    - use csrf tokens
3. Authentication:
    - use framework provided functions to generate and compare passwords
    - use constant time comparing function
4. Error reporting:
    - show same error message for incorrect username / password to prevent 
      attacker from distinguishing from incorrect username or password

### Part 2

I have used `Flask` framefork and several extensions for it, namely:

- `flask-wtforms` - creating forms with input validation / easy processing
- `flask-bcrypt` - bcrypt implementation integrated with `Flask` user management

Flask is a mini-framework with little support for built-in security measures. I have
integrated several extensions to implement secure password storage, correct input validation,
authentication and error reporting.