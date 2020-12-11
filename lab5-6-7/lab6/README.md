## Lab 5 Client-Server app: private info storage

#### How did you implement your storage?

MySQL database is used as storage. The abstraction is handled by the SQLAlchemy ORM.
In the lab I have implemented the encryption with Fernet encryption method. The privacy
sensitive fields, in our use case it is `first_name` and `last_name` are stored in encrypted
format. If the database gets compromised the attacker will only be able to access the encrypted
version of the data.

The key is used in encryption which is provided by environment variable. If the production
deployment will be using some cloud provider (e.g. Elastic Beanstalk), then the key injection
can be securely handled by the cloud provider. As the key is not stored directly in
the application code, even if the code is compromised, the attacker won't be able to get access
to the key.

#### Why did you choose particular storage options/algorithms/libs etc?

SQLAlchemy ORM provides easy interface for working with database, setting and updating
database records. Flask also has a special extension for working with sqlalchemy.

#### What are the possible attack vectors on your system (i.e. how the stored information may be stolen)?

There are several vulnerabilities in the system:

1. Try to get the full encryption key. If the full encryption key is known, then the encrypted data
can be decrypted using the found key. The security of the key will be dependent on the deployment. 
If the key is passed through cloud-provider key management solution, then it will be rather secure.
But it may be less secure if deployment is made without special key-management solutions, e.g. if key
is hardcoded in some config file or environment variables file.

2. Try to crack the passwords. The passwords are hashed with bcrypt using salts, but the attacker still may
try to crack some passwords with different attack methods. For example, using lists of most common passwords,
dictionary attacks, or using simple brute force with different initial password assumptions. If the passwords are 
cracked, then the attacked can access the user private information.
