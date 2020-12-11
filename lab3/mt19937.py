import requests
import numpy as np

from datetime import datetime

MODE = "Mt"
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

def find_seed(player_id):
    game_value = play(MODE, player_id, 1, 1)
    curr_time = int(datetime.utcnow().timestamp())
    for adjustment in range(-10000, 10000):
        possible_seed = curr_time + adjustment
        np.random.seed(possible_seed)
        if np.random.randint(0, 2**32) == game_value:
            return possible_seed
    
    raise ValueError("possible seed not found")


if __name__ == "__main__":
    player_id = 6671
    money_amount = 100
    create_account(player_id)

    seed = find_seed(player_id)
    
    print("Seed value: {}".format(seed))
    
    np.random.seed(seed)
    np.random.randint(0, 2**32) # skip 1 number
    for i in range(10):
        bet_number = np.random.randint(0, 2**32)
        play(MODE, player_id, money_amount, bet_number)
