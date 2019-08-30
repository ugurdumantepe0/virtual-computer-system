# Virtual Computer System

In this project a basic virtual computer system is created which willsimulate CPU, Memory and I/O devices.
Initially a program which simulates  a computer system is written: It reads one assembly file, load this file into a virtual Memory and
then a virtual CPU will execute the instructions of the process one-by-one.

Program initially reads assembly instructions from a file, transform them into binary code and write it to a file. At this step program is transforming the assembly instructions into binary code.

Furthermore the program is able to simulate the execution of multiple processes in a way that is similar to how computer systems work. with the execution of multiple processes, there are now additional concerns like Loading multiple processes, limited memory and assigning I/O operations to blocked processes. additional queues are implmented to deal with these.

Java is used in this project.
