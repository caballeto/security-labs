import requests
import numpy as np

MODE = "Lcg"
ACCOUNT_URL = "http://95.217.177.249/casino/createacc?id={}"
PLAY_URL = "http://95.217.177.249/casino/play{}?id={}&bet={}&number={}"

def play(mode, player_id, money_amount, bet_number):
    url = PLAY_URL.format(mode, player_id, money_amount, bet_number)
    r = requests.get(url).json()
    print("Bet response: {}".format(r))
    return r['realNumber']

def create_account(player_id):
    url = ACCOUNT_URL.format(player_id)
    r = requests.get(url).json()
    print("Account create response: {}".format(r))
    return r

# multiplicative inverse computation
def egcd(a, b):
    if a == 0:
        return (b, 0, 1)
    else:
        g, y, x = egcd(b % a, a)
        return (g, x - (b // a) * y, y)

def modinv(a, m):
    g, x, y = egcd(a, m)
    if g != 1:
        return None
    else:
        return x % m

def lcg_next(a, c, prev):
    value = (a * prev + c) % 2**32
    return np.int32(value)

def find_seed(player_id):
    x1 = play(MODE, player_id, 1, 1)
    x2 = play(MODE, player_id, 1, 1)
    x3 = play(MODE, player_id, 1, 1)
    
    inv_mod = modinv(x1 - x2, 2**32)
    while inv_mod is None:
        x1 = x2
        x2 = x3
        x3 = play(MODE, player_id, 1, 1)
        inv_mod = modinv(x1 - x2, 2**32)
        
    a = (inv_mod * (x2 - x3)) % 2**32
    c = (x2 - x1 * a) % 2**32
    
    return a, c, x3


if __name__ == "__main__":
    player_id = 6667
    money_amount = 100
    create_account(player_id)
    
    # make several requests to determine LCG seed values
    # create seeded generator, skip number of requests made
    # send winning requests
    a, c, bet_number = find_seed(player_id)
    
    print("Seed values: a = {}, c = {}".format(a, c))
    
    for i in range(10):
        bet_number = lcg_next(a, c, bet_number)
        play(MODE, player_id, money_amount, bet_number)
