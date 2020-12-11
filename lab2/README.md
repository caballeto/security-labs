## Lab 2 Solution

To recover the cipher text we will first use the crib
dragging technique. Using the following algorithm:

1. Take 2 arbitrary lines, l1 and l2.
2. By definition l1 = m1 ^ key, l2 = m2 ^ key. By xoring l1 ^ l2 we get m1 ^ m2. 
3. We then analyze the m1 ^ m2 and try to extract the plain text. We try to recover the text
using [the most common english words](https://en.wikipedia.org/wiki/Most_common_words_in_English).

We use a [crib dragging tool](https://lzutao.github.io/cribdrag/) to simplify the process.

### Result screenshots:

#### Crib dragging

![Crib drag](/lab2/crib_drag.png?raw=true "Optional Title")

#### Searching google

![Google search](/lab2/risk_it_on_one.png?raw=true "Optional Title")

#### Decoding

![Decoded text](/lab2/run_decode.png?raw=true)
