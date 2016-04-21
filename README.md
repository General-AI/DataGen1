# DataGen1

## How To Use:

This is a data generator. It is used to generate a hierarchically patterened string of characters.

To use, intialize the generator. The data will be stored in a queue at Generator.q.
Calling the method Generator.Generate(int numChars) populates the queue with a string of queues that has at least numChars characters. Once numChars is surpassed, it will add more characters until the pattern it is currently inputting is completed.

## Hierarchy of Patterns:

Note: "n" means choose a random pattern from level n.

Note: y^$ (3<= $ <= 5) means concatenate y $ times where $ is a random value between 3 and 5. ex: yyyy

### Level 0:

alphabetical characters a-z

### Level 1:

0: kw

1: aaab

2: cd

3: eqe

4: rr

5: uv"0"z

6: l"0"p

7: m^$y^$ (2<=$<=8)

8: day

9: banana


### Level 2:


A: 123

B: 4^$ (4<=$<=6)

C: 5"1"6

D: 98

E: 078

F: 24243

G: good8


### Level 3:


H: FACEpalm

I: soBAD

J: 552ACE255

K: FB"0"^$GF (0<=$<=9)

L: FAD

M: "1"DAD"2"

O: B^$ (4<=$<=6)


### Level 4:


H^$ (3<=$<=5)

IIG

LMI

JM

KOK

O"3"

