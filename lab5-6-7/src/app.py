from flask import Flask, render_template
from .config import Config
from .models import db, login_manager
from .views import main_blueprint, auth_blueprint
from .cli import create_db, shell_text_processor
from werkzeug.exceptions import HTTPException


# sign in
# sign up
# main / settings page
def handle_exception(e):
    return render_template("error.html")


def create_app():
    app = Flask(__name__)
    app.config.from_object(Config)
    app.register_blueprint(main_blueprint)
    app.register_blueprint(auth_blueprint)
    app.register_error_handler(HTTPException, handle_exception)
    app.cli.add_command(create_db)
    app.shell_context_processors.append(shell_text_processor)
    db.init_app(app)
    login_manager.init_app(app)
    return app


if __name__ == '__main__':
    app = create_app()
    app.run(host='0.0.0.0', port=8080)
