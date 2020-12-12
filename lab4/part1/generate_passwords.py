import random
import string


def get_resource(name):
    with open('part1/resources/' + name, 'r') as f:
        return f.read().split('\n')


TOP_ADJECTIVES = get_resource('top-1k-adjectives.txt')
TOP_NOUNS = get_resource('top-5k-nouns.txt')
TOP_WORDS = get_resource('top-10k-words.txt')
TOP_PASSWORDS_100 = get_resource('top-100-passwords.txt')
TOP_PASSWORDS_1M = get_resource('top-1m-passwords.txt')


def generate_password_from_top100():
    return random.choice(TOP_PASSWORDS_100)


def generate_password_from_top1m():
    return random.choice(TOP_PASSWORDS_1M)


def generate_password_random():
    pass_len = random.randint(8, 25)
    return ''.join(random.SystemRandom().choice(string.ascii_letters + string.digits) for _ in range(pass_len))


def generate_password_from_adjectives():
    return random.choice(TOP_ADJECTIVES)


def generate_password_from_nouns():
    return random.choice(TOP_NOUNS) + random.choice(TOP_NOUNS)


def generate_password_with_year():
    password = generate_password_from_top1m()
    year = random.randint(1900, 2050)
    return random.choice([str(year) + password, password + str(year)])


def generate_number_password():
    pass_len = random.randint(4, 10)
    return ''.join(random.SystemRandom().choice(string.digits) for _ in range(pass_len))


def generate_human_passwords():
    generators = [generate_password_from_top100, generate_password_from_top1m, generate_password_random,
                  generate_password_from_adjectives, generate_password_from_nouns, generate_password_with_year,
                  generate_number_password]

    selected_generators = random.choices(population=generators,
                                         weights=[0.075, 0.8, 0.03, 0.02, 0.018, 0.018, 0.04],
                                         k=100000)

    passwords = [generate() for generate in selected_generators]

    return passwords


