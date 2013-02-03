import xbmcgui, xbmcaddon, subprocess, re

__addon__ 	= xbmcaddon.Addon()
__dht11_bin__	= __addon__.getSetting( 'dht11_bin' )

def read_data_dht11( dht11_bin ):
	dht11_output = subprocess.check_output( [ dht11_bin ] );
	matches = re.search( "Humidity = ([0-9.]+), Temperature = ([0-9.]+)", dht11_output )
	h = -1
	t = -1
	if( matches ):
		h = matches.group( 1 )
		t = matches.group( 2 )
	return( h, t )


( h, t ) = read_data_dht11( __dht11_bin__ )
dialog = xbmcgui.Dialog()
dialog.ok( "DHT11", "Temperature: " + t + ", Humidity: " + h )
del dialog
