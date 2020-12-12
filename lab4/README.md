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

### Part 2

1. MD5 Dictionary attack.

We will first dictionary attack with `john` command line utility.
We will using the following command to attack md5:

```shell script
john --format=Raw-MD5 --wordlist=part1/resources/top-1m-passwords.txt part2/resources/md5.txt
```

The output could be retrieved from `$JOHN/john.pot` file.

It out case, we have total 99999 passwords:

```shell scriptsalt
$ cat part2/resources/md5.txt | wc -l
99999
```

```shell script
$ cat ~/snap/john-the-ripper/297/.john/john.pot | wc -l
56885
```

Dictionary attack allowed us to retrieve 56.8% of the md5 hashes.

2. Attempt to decrypt Bcrypt

We try to decrypt bcrypt with the following command:

```shell script
$ john part2/resources/bcrypt.txt
```

After running 15 minutes it was able to decrypt around 3% of passwords.
Majority are very simple passwords like `summer`, `qwerty`, etc.
The duration of decryption process is much longer than with md5 or sha1.

![bcrypt run](/lab4/bcrypt_run.png?raw=true)

## Conclusion

We tried to run simple dictionary attacks on a set of passwords. We used `john`
program to run the dictionary attacks on `md5` and `bcrypt` hashes. From the showed
performance we may make a conclusion that `md5` is a very weak hash function, which
is susceptible to dictionary attacks.

The majority of decrypted passwords were the passwords from the list of most commonly used
passwords. Cracking `md5` was easy, as the passwords were not salted, therefore many similar
passwords were found without hash recomputation.

The `bcrypt` hash function is much harder to crack. The computation time is too long
to run the brute-force attacks or to try to find the salts.

We can make several takeaways from analysis above:
- most common passwords are a big source of password vulnerability 
- short passwords are much easier to crack, they maybe cracked with brute-force attack
- passwords consisting of bigger charsets are harder to decrypt

Therefore, recommendations for improving password security:

1. Use `bcrypt` for password hashing.
2. Enforce the minimum length of the password to be at least 8 or 9.
3. Enforce the password to include small/uppercase, special characters and numbers.
4. Create or use existing implementation of library for measuring password complexity,
   do not allow passwords that are too weak (too common, short, etc.)
5. Incentivize users to create randomly generated passwords to ensure maximum protection.
6. Consider use MFA to enhance application security.
