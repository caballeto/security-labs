import random
import string
import bcrypt
import hashlib


def md5Hash(value):
    return hashlib.md5(value.encode()).hexdigest()


def sha1Hash(value):
    salt = ''.join(random.SystemRandom().choice(string.digits) for _ in range(16))
    return f"{hashlib.sha1((salt + value).encode()).hexdigest()}, {salt}"


def bcryptHash(value):
    return bcrypt.hashpw(value.encode(), bcrypt.gensalt()).decode()
