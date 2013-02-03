
/*

 - left pin ( marked 's' ) signal
 - medium Vcc
 - right ( marked '1' ) GND
 - pull up resistor is already installed ( 10Kom )

*/

#include <stdio.h>
#include <stdlib.h>
#include <stdint.h>
#include <wiringPi.h>

#define DHT11_DATA_PIN 7
#define TIMEOUT 100000
#define MAXTIMINGS 100
#define TESTS 5

void read_dht11_data();

int main()
{
	unsigned int h, t, i;

	if( wiringPiSetup() == -1 )
	{
		exit( 1 );
	}

	for( i = 0; i < TESTS; i++ )
	{
		h = 0;
		t = 0;
		read_dht11_data( &h, &t );
		if( h > 0 && t > 0 )
		{
			printf( "Humidity = %d, Temperature = %d\n", h, t );
			break;
		}

		delay( 1000 );
	}

	return( 0 );
}

void read_dht11_data( unsigned int *h, unsigned int *t )
{
	unsigned int data[ 5 ], i, lastState, counter, bit = 0, byte = 0;

	for( i = 0; i < 5; i++ )
		data[ i ] = 0;

	pinMode( DHT11_DATA_PIN, OUTPUT );
	digitalWrite( DHT11_DATA_PIN, LOW );
	delay( 18 );
	digitalWrite( DHT11_DATA_PIN, HIGH );
	delayMicroseconds( 40 );

	pinMode( DHT11_DATA_PIN, INPUT );

	lastState = HIGH;
	for( i = 0; i < MAXTIMINGS; i++ )
	{
		counter = 0;
		while( digitalRead( DHT11_DATA_PIN ) == lastState )
		{
			counter++;
			if( counter == TIMEOUT )
				break;
		}

		lastState = digitalRead( DHT11_DATA_PIN );

		if( i <= 3 )
			continue;

		if( lastState == LOW )
		{
			byte = bit / 8;
			data[ byte ] <<= 1;
			if( counter > 200 )
				data[ byte ] |= 1;
			bit++;
		} 
	}

	if( data[ 4 ] == (  (data[ 0 ] + data[ 1 ] + data[ 2 ] + data[ 3 ] ) & 0xFF ) )
	{
		*h = data[ 0 ];
		*t = data[ 2 ];
	}
}
