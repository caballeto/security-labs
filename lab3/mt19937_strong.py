import requests
import numpy as np

from datetime import datetime

MODE = "BetterMt"
ACCOUNT_URL = "http://95.217.177.249/casino/createacc?id={}"
PLAY_URL = "http://95.217.177.249/casino/play{}?id={}&bet={}&number={}"

def play(mode, player_id, money_amount, bet_number):
    url = PLAY_URL.format(mode, player_id, money_amount, bet_number)
    r = requests.get(url).json()
    print("Bet response: {}".format(r))
    return np.uint32(r['realNumber'])

def create_account(player_id):
    url = ACCOUNT_URL.format(player_id)
    r = requests.get(url).json()
    print("Account create response: {}".format(r))
    return r

def untemper(value):
    assert value < 2**32
    assert value >= 0

    y = value

    # Inverse of y = y ^ (y >> 18)
    y = y ^ (y >> 18)

    # Inverse of y = y ^ ((y << 15) & 0xefc60000)
    y = y ^ ((y & 0x1df8c) << 15)

    # Inverse of y = y ^ ((y << 7) & 0x9d2c5680)
    t = y
    t = ((t & 0x0000002d) << 7) ^ y
    t = ((t & 0x000018ad) << 7) ^ y
    t = ((t & 0x001a58ad) << 7) ^ y
    y = ((t & 0x013a58ad) << 7) ^ y

    # Inverse of y = y ^ (y >> 11)
    top = y & 0xffe00000
    mid = y & 0x001ffc00
    low = y & 0x000003ff

    y = top | ((top >> 11) ^ mid) | ((((top >> 11) ^ mid) >> 11) ^ low)

    return np.uint32(y)

def find_seed(player_id):
    return [untemper(play(MODE, player_id, 1, 1)) for i in range(624)]

if __name__ == "__main__":
    player_id = 6672
    money_amount = 100
    create_account(player_id)

    seed = find_seed(player_id)
    
    print("Seed value: {}".format(seed))
    
    np.random.set_state((('MT19937', seed, 624)))
    for i in range(25):
        bet_number = np.random.randint(0, 2**32)
        play(MODE, player_id, money_amount, bet_number)
