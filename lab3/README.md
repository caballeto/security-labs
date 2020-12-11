## Lab 3 solution

To game the casino we will try to find out the seed of the random number generator and then send winning requests to the server.

### LCG

Get several consecutive numbers and use them to extract the seed of LCG.

Use (multiplicative inverse)[https://stackoverflow.com/questions/4798654/modular-multiplicative-inverse-function-in-python] to find `a` and `c` parameters of the generator.

![Run results](/lab3/lcg_run.png?raw=true)
