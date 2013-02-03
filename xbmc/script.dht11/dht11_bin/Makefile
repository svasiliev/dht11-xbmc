CC=gcc
OBJ = dht11.o
LDFLAGS=-lwiringPi

%.o: %.c
	$(CC) -c -o $@ $<

dht11: $(OBJ)
	gcc -o $@ $^ ${LDFLAGS}

