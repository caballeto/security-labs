import click

from flask.cli import with_appcontext
from .models import db, User


@click.command('initdb')
@with_appcontext
def create_db():
    db.create_all()
    db.session.commit()
    print('Database created successfully')


def shell_text_processor():
    return {'db': db, 'User': User}
