## Lab 3 solution

To game the casino we will try to find out the seed of the random number generator and then send winning requests to the server.

### LCG

Get several consecutive numbers and use them to extract the seed of LCG.

Use (multiplicative inverse)[https://stackoverflow.com/questions/4798654/modular-multiplicative-inverse-function-in-python] to find `a` and `c` parameters of the generator.

![Run results](/lab3/lcg_run.png?raw=true)

### Mersenne Twister

Create new account which will be seeded with utc time. Receive the first number as a result of call. Get current time and try to guess the seed number by checking througth the recent times and running the generator.

Once found the seed value, send winning requests.

![Run results](/lab3/mt_run.png?raw=true)

### Mersenne Twister 2

Use untemper function to retrieve the state of generator. Retrieve the full state by making 624 calls to the server.

Set state with `np.random.set_state()` and use it to generate winning predictions.

![Run results](/lab3/mtstrong1.png?raw=true)

![Run results](/lab3/mtstrong2.png?raw=true)
