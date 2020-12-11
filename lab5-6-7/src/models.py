from src.config import Config

from flask_sqlalchemy import SQLAlchemy
from flask_login import LoginManager, UserMixin
from flask_bcrypt import Bcrypt

from src.utils import encrypt, decrypt


db = SQLAlchemy()
bcrypt = Bcrypt()


class User(UserMixin, db.Model):
    # id
    id = db.Column(db.Integer, primary_key=True)

    # auth
    email = db.Column(db.String(50), unique=True)
    password_hash = db.Column(db.String(256))

    # other data
    first_name = db.Column(db.String(250))
    last_name = db.Column(db.String(250))

    @property
    def password(self):
        raise AttributeError("password is not a readable attribute")

    @property
    def first_name_value(self):
        return decrypt(self.first_name) if self.first_name else ''

    @first_name_value.setter
    def first_name_value(self, first_name):
        self.first_name = encrypt(first_name)

    @property
    def last_name_value(self):
        return decrypt(self.last_name) if self.last_name else ''

    @last_name_value.setter
    def last_name_value(self, last_name):
        self.last_name = encrypt(last_name)

    @password.setter
    def password(self, password):
        self.password_hash = bcrypt.generate_password_hash(password)

    @password.deleter
    def password(self):
        self.password_hash = None

    @property
    def has_password(self):
        return bool(self.password_hash)

    def verify_password(self, password):
        return bcrypt.check_password_hash(self.password_hash, password)


# setup login manager
login_manager = LoginManager()
login_manager.login_view = "auth.signin"


@login_manager.user_loader
def load_user(user_id):
    return User.query.get(int(user_id))

