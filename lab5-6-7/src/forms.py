from flask import flash
from flask_wtf import Form
from wtforms.fields.html5 import EmailField
from wtforms import StringField, PasswordField, TextField
from wtforms.validators import DataRequired, EqualTo, Email, Length
from sqlalchemy.exc import IntegrityError
from src.models import db, User
from flask_login import current_user


class LoginForm(Form):
    email = EmailField("Email", validators=[DataRequired(), Email(), Length(max=64)])
    password = PasswordField("Password", validators=[
        DataRequired(),
        Length(min=8),
        Length(max=64)
    ])

    def get_user(self):
        user = User.query.filter_by(email=self.email.data).first()
        if not user:
            flash("Invalid user name or password", category="error")
            return None
        if not user.verify_password(self.password.data):
            flash("Invalid user name or password", category="error")
            return None
        return user


class SettingsForm(Form):
    first_name = TextField(validators=[DataRequired(), Length(min=2), Length(max=50)])
    last_name = TextField(validators=[DataRequired(), Length(min=2), Length(max=50)])

    def save_user(self):
        self.user.first_name_value = self.first_name.data
        self.user.last_name_value = self.last_name.data
        db.session.add(self.user)
        db.session.commit()


class RegisterForm(Form):
    email = StringField("Email", validators=[
        DataRequired(),
        Email()
    ])
    password = PasswordField(
        "Password",
        validators=[
            DataRequired(),
            Length(min=8),
            Length(max=64)
        ],
    )

    def create_user(self):
        user = User(email=self.email.data)
        user.password = self.password.data
        db.session.add(user)
        try:
            db.session.commit()
            return user
        except IntegrityError:
            flash("Invalid user name or password", category="error")
            return None
