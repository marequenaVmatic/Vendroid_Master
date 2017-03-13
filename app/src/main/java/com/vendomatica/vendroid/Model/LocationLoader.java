package com.vendomatica.vendroid.Model;

import android.content.Context;
import android.content.Intent;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.provider.Settings;
import android.util.Log;

import java.io.IOException;
import java.util.List;
import java.util.Locale;
import java.util.Timer;
import java.util.TimerTask;

public class LocationLoader implements LocationListener {
	public static final int ERROR_INACTIVE		= 1;
	public static final int ERROR_TIMEOUT			= 2;
	public static final int ERROR_WEATHER		= 3;
	public static final int ERROR_ADDRESS			= 4;
	public static final int ERROR_UNKNOWN 		= 999;
	
	private static final int TWO_MINUTES = 1000 * 60 * 2;
	private static final long MIN_TIMES = 10000;
	private static final float MIN_DISTANCE = 1.0f;
	
	private static final int LOCATION_TIMEOUT = 10 * 1000;
	
	private OnLoadEventListener m_loadEventListener = null;
	public void SetOnLoadEventListener(OnLoadEventListener loadEventListener) {
		m_loadEventListener = loadEventListener;
	}
	
	public LocationLoader(Context context, boolean bGetAddress) {
		mContext = context;
		
		m_locationManager = (LocationManager)mContext.getSystemService(Context.LOCATION_SERVICE);
		
		m_currentProvider = LocationManager.GPS_PROVIDER;
		if(!m_locationManager.isProviderEnabled(m_currentProvider))
			m_currentProvider = LocationManager.NETWORK_PROVIDER;
		
		m_bGetAddress = bGetAddress;
	}
		
	public interface OnLoadEventListener {
		void onLocationChanged(Location location);
		void onAddressChanged(String strAddress);
		void onError(int iErrorCode);
	}
	
	private Context mContext = null;
	private Location m_location = null;
	private LocationManager m_locationManager = null;
	private String m_currentProvider = null;
	private boolean m_isLoading = false;
	private Handler m_restartHandler = new Handler() {

		@Override
		public void handleMessage(Message msg) {
			// TODO Auto-generated method stub
			Start();
			super.handleMessage(msg);
		}				
	};
	
	private boolean m_bGetAddress = false;
	
	private String m_strAddress = "";
	private Thread m_addressThread = null;
	private Runnable m_addressRunnable = new Runnable() {
		
		@Override
		public void run() {
			// TODO Auto-generated method stub
			if(m_location == null)
				return;
			
			Log.i("MCall", "location=" + m_location.getLatitude() + ", " + m_location.getLongitude() + ", acc=" + m_location.getAccuracy());
			Geocoder geoCoder = new Geocoder(mContext, Locale.KOREA);
			String strAddress = "";
			try {
				double fLat = (int)(m_location.getLatitude() * 100) / 100.0;
				double fLon = (int)(m_location.getLongitude() * 100) / 100.0;
				List<Address> addresses = geoCoder.getFromLocation(fLat, fLon, 1);
				if(addresses.size() > 0) {
					Address addr = addresses.get(0);
					strAddress = addr.getLocality() + " " + addr.getThoroughfare();
					if(!m_strAddress.equals(strAddress)) {
						m_strAddress = strAddress;
						if(m_loadEventListener != null)
							m_loadEventListener.onAddressChanged(m_strAddress);
					}
				}
			} catch (IOException e) {
				// TODO Auto-generated catch block
				if(m_loadEventListener != null)
					m_loadEventListener.onError(ERROR_ADDRESS);
			}
		}
	};

	private void toggleGPS(boolean enable) {
		String provider = Settings.Secure.getString(mContext.getContentResolver(),
				Settings.Secure.LOCATION_PROVIDERS_ALLOWED);

		if(provider.contains("gps") == enable) {
			return; // the GPS is already in the requested state
		}

		final Intent poke = new Intent();
		poke.setClassName("com.android.settings",
				"com.android.settings.widget.SettingsAppWidgetProvider");
		poke.addCategory(Intent.CATEGORY_ALTERNATIVE);
		poke.setData(Uri.parse("3"));
		mContext.sendBroadcast(poke);
	}

	public void Start() {
		//Common.debug("start get location...");

		try {
			m_locationManager.requestLocationUpdates(m_currentProvider, MIN_TIMES, MIN_DISTANCE, this);
		} catch(SecurityException e) {
			e.printStackTrace();
		}
		m_isLoading = true;
		
		new Timer().schedule(new TimerTask() {
			@Override
			public void run() {
				// TODO Auto-generated method stub
				if(m_isLoading) {
					if(m_currentProvider.equals(LocationManager.GPS_PROVIDER)) {
						Stop();
						m_currentProvider = LocationManager.NETWORK_PROVIDER;
						m_restartHandler.sendEmptyMessage(0);
					} else {
						try {
							Location location = m_locationManager.getLastKnownLocation(m_currentProvider);
							if(location != null)
								onLocationChanged(location);
							else {
								if(m_loadEventListener != null) {
									m_loadEventListener.onError(ERROR_TIMEOUT);
								/*Location loc = new Location("testprovider");
								loc.setLatitude(37.0);
								loc.setLongitude(121.0);
								onLocationChanged(loc);*/
								}
							}
						}catch(SecurityException e){

						}
					}
				}
			}
		}, LOCATION_TIMEOUT);
	}
	public void Stop() {
		try {
			m_locationManager.removeUpdates(this);
		}catch (SecurityException e) {
		}
		m_isLoading = false;
	}

	@Override
	public void onLocationChanged(Location location) {
		// TODO Auto-generated method stub
		if(isBetterLocation(location, m_location)) {
			m_isLoading = false;
			
			m_location = location;
			if(m_loadEventListener != null)
				m_loadEventListener.onLocationChanged(m_location);
			
			Stop();
			
			if(m_bGetAddress) {
				if(m_addressThread != null) {
					m_addressThread.interrupt();
					m_addressThread = null;
				}
				m_addressThread = new Thread(m_addressRunnable);
				m_addressThread.start();
			}
		}
	}
	
	@Override
	public void onProviderDisabled(String provider) {
		// TODO Auto-generated method stub
		if(provider.equals(LocationManager.GPS_PROVIDER)) {
			Stop();
			m_currentProvider = LocationManager.NETWORK_PROVIDER;
			Start();
		}
		else if(provider.equals(LocationManager.NETWORK_PROVIDER)) {
			if(m_loadEventListener != null)
				m_loadEventListener.onError(ERROR_INACTIVE);
		}
	}

	@Override
	public void onProviderEnabled(String provider) {
		// TODO Auto-generated method stub		
		if(provider.equals(LocationManager.GPS_PROVIDER)) {
			Stop();
			m_currentProvider = LocationManager.GPS_PROVIDER;
			Start();
		}
	}

	@Override
	public void onStatusChanged(String provider, int status, Bundle extras) {
		// TODO Auto-generated method stub
		Log.i("LocationLoader", provider + " status=" + status );
	}
	
	
	public Location GetLocation()
	{
		return m_location;
	}
	public double getDistanceFromLocation(Location location1, Location location2)	{
		if(location1 == null || location2 == null)
			return -1;
		
		double R = 6371;
		double dLat = Math.abs(location1.getLatitude() - location2.getLatitude());
		double dLon = Math.abs(location1.getLongitude() - location2.getLongitude());
		double a = Math.sin(dLat/2) * Math.sin(dLat/2) +
		        Math.cos(location1.getLatitude()) * Math.cos(location2.getLatitude()) *
		        Math.sin(dLon/2) * Math.sin(dLon/2);
		double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1-a));
		double d = R * c;
		
		return d;
	}
	
	protected boolean isBetterLocation(Location location, Location currentBestLocation) {
	    if (currentBestLocation == null) {
	        // A new location is always better than no location
	        return true;
	    }

	    // Check whether the new location fix is newer or older
	    long timeDelta = location.getTime() - currentBestLocation.getTime();
	    boolean isSignificantlyNewer = timeDelta > TWO_MINUTES;
	    boolean isSignificantlyOlder = timeDelta < -TWO_MINUTES;
	    boolean isNewer = timeDelta > 0;

	    // If it's been more than two minutes since the current location, use the new location
	    // because the user has likely moved
	    if (isSignificantlyNewer) {
	        return true;
	    // If the new location is more than two minutes older, it must be worse
	    } else if (isSignificantlyOlder) {
	        return false;
	    }

	    // Check whether the new location fix is more or less accurate
	    int accuracyDelta = (int) (location.getAccuracy() - currentBestLocation.getAccuracy());
	    boolean isLessAccurate = accuracyDelta > 0;
	    boolean isMoreAccurate = accuracyDelta < 0;
	    boolean isSignificantlyLessAccurate = accuracyDelta > 200;

	    // Check if the old and new location are from the same provider
	    boolean isFromSameProvider = isSameProvider(location.getProvider(),
	            currentBestLocation.getProvider());

	    // Determine location quality using a combination of timeliness and accuracy
	    if (isMoreAccurate) {
	        return true;
	    } else if (isNewer && !isLessAccurate) {
	        return true;
	    } else if (isNewer && !isSignificantlyLessAccurate && isFromSameProvider) {
	        return true;
	    }
	    return false;
	}

	/** Checks whether two providers are the same */
	private boolean isSameProvider(String provider1, String provider2) {
	    if (provider1 == null) {
	      return provider2 == null;
	    }
	    return provider1.equals(provider2);
	}

}
