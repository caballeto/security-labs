from flask import Blueprint, render_template
from flask_login import current_user

blueprint = Blueprint("main", __name__)


@blueprint.route('/')
def main():
    return render_template("index.html", user=current_user)
