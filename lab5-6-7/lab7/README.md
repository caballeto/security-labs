## Lab 7: TLS configuration

The TLS for web app was configured on a reverse proxy. Nginx is working as a reverse proxy by proxying
all the traffic to the application server via unix socket.

### Part 1

The TLS configuration supports interaction via TLSv1.1 and TLSv1.2. TLSv1.3 is not yet broadly supported,
therefore older version have been selected. As for the ciphersuites, of the recommended ciphersuites have
been selected. The server also forbids connection via bad ciphers like MD5.

The selected ciphers are mainly different versions / combinations of Diffie-Hellman scheme (e.g. ECDH, DHE, ECDHE).
As for the bulk encryption algorithm, AES. RSA may be used also for key-exchange or digital signature algorithm.

### Part 2
TLS configuration uses self-signed certificates created using the following command:

```shell script
openssl req -new -newkey rsa:4096 -x509 -sha256 -days 365 -nodes -out cert.crt -keyout key.key
``` 

Additionally Diffie-Hellman (DH) key exchange parameters were created:

```shell script
openssl dhparam -out /root/certs/security_app/dhparam.pem 4096
```

All the certificates, keys, params are stored in `/root/certs/security_app/` folder.

