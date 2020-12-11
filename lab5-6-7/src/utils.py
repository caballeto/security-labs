import random
import string

from datetime import datetime, timedelta
from cryptography.fernet import Fernet
from src.config import Config

_fernet = Fernet(Config.ENCRYPTION_KEY)

def random_id(size=50):
    return ''.join(random.choices(string.ascii_letters + string.digits, k=size))


def next_day():
    return datetime.utcnow() + timedelta(days=1)


def encrypt(message: str) -> str:
    return _fernet.encrypt(message.encode()).decode()


def decrypt(token: str) -> str:
    return _fernet.decrypt(token.encode()).decode()
