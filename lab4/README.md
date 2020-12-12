## Lab 4: password generation and password cracking

### Part 1

To generate passwords we use several methods:

- generate password from top 1M passwords
- generate password from top most common passwords
- generate totally random password by randomizing characters/digits and password length
- generate password by combining adjectives
- generate password by randomly adding year dates
- generate password from digits only

The generated passwords are saved into csv files. Salts are also saved. Following hashes are used:

- md5
- sha1 with salt
- bcrypt

`md5` and `sha1` each hashed 100k passwords, while bcrypt hashed 1000 passwords in total. `bcrypt` is much slower by comparison.
