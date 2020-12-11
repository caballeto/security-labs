import os


class Config(object):
    SECRET_KEY = os.environ.get('SECRET_KEY') or 'dev'
    ENCRYPTION_KEY = (os.environ.get('ENCRYPTION_KEY') or 'dev-encrypt-key').encode()

    SQLALCHEMY_DATABASE_URI = os.environ.get("DATABASE_URL")
    SQLALCHEMY_TRACK_MODIFICATIONS = False

    BASE_URL = os.environ.get('BASE_URL') or 'http://localhost:5000'
