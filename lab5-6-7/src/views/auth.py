from flask import Blueprint, render_template, redirect, url_for, flash, session
from flask_login import logout_user, login_required, current_user, login_user

from src.forms import LoginForm, RegisterForm, SettingsForm


blueprint = Blueprint("auth", __name__)


def flash_errors(form):
    for field, errors in form.errors.items():
        for error in errors:
            flash(error, category="error")


@blueprint.route('/signin', methods=('GET', 'POST'))
def signin():
    if current_user.is_authenticated:
        return redirect(url_for('main.main'))

    form = LoginForm()
    if form.validate_on_submit():
        user = form.get_user()
        if user:
            login_user(user)
            return redirect(url_for('main.main'))
    else:
        flash_errors(form)
    return render_template("signin.html", form=form)


@blueprint.route('/signup', methods=('GET', 'POST'))
def signup():
    if current_user.is_authenticated:
        return redirect(url_for('main.main'))

    form = RegisterForm()
    if form.validate_on_submit():
        user = form.create_user()
        if user:
            login_user(user)
            return redirect(url_for('main.main'))
    else:
        flash_errors(form)
    return render_template("signup.html", form=form)


@login_required
@blueprint.route('/settings', methods=('GET', 'POST'))
def settings():
    print(current_user)
    form = SettingsForm(first_name=current_user.first_name_value, last_name=current_user.last_name_value)
    form.user = current_user
    if form.validate_on_submit():
        form.save_user()
        return redirect(url_for('main.main'))
    else:
        flash_errors(form)
    return render_template("settings.html", form=form, user=current_user)


@blueprint.route('/signout')
def signout():
    if current_user.is_authenticated:
        logout_user()
    return redirect(url_for('main.main'))
