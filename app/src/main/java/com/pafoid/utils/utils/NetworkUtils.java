package com.pafoid.utils.utils;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.wifi.WifiManager;

import java.math.BigInteger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * A utility class that exposes network related helper methods
 * <p>
 * {@code NetworkUtils} does not have a constructor and only static methods and constants
 */
public class NetworkUtils {

	private static final Pattern ipv4Pattern = Pattern.compile("(([0-9]+)\\.){3}([0-9]*)");

	public static class TcpStates {
		public static final int ESTBLSH = 1;
		public static final int SYNSENT = 2;
		public static final int SYNRECV = 3;
		public static final int FWAIT1 = 4;
		public static final int FWAIT2 = 5;
		public static final int TMEWAIT = 6;
		public static final int CLOSED = 7;
		public static final int CLSWAIT = 8;
		public static final int LASTACK = 9;
		public static final int LISTEN = 10;
		public static final int CLOSING = 11;
		public static final int UNKNOWN = 12;
	}

	//Ip Addresses
	/**
	 * Hepler method used to convert an hexadecimal value to decimal
	 * @param hexValue the hexadecimal value to convert
	 * @return a String value representing the converted value
	 */
	public static String hexToDecString(String hexValue) {
		return String.valueOf(Long.parseLong(hexValue, 16));
	}

	/**
	 * Helper method used to convert an hexadecimal String value to decimal
	 * @param hexValue the hexadecimal value to convert
	 * @return a long value representing the converted value
	 */
	public static long hexToDec(String hexValue) {
		return Long.parseLong(hexValue, 16);
	}

	/**
	 * Helper method used to convert an IP Address to a long value
	 * @param ipAddress the IP Address to convert
	 * @return a long value representing the converted value
	 */
	public static long ipToLong(String ipAddress) {
		return new BigInteger(ipAddress, ipAddress.length()).longValue();
	}

	/**
	 * Helper method used to convert an hexadecimal IP Address to a String value
	 * @param hexIp the IP Address to convert
	 * @return a String value representing the converted value
	 */
	public static String hexIpToString(String hexIp) {
		String ip = "";

		if (hexIp == null) return ip;

		for (int j = 0; j < hexIp.length(); j += 2) {
			String sub = hexIp.substring(j, j + 2);
			int num = Integer.parseInt(sub, 16);
			ip = "." + num + ip;
		}

		ip = ip.substring(1, ip.length());

		return ip;
	}

	/**
	 * Helper method used to convert an IPV6 Address to a String value
	 * @param hexIp the IP Address to convert
	 * @return a String value representing the converted IPV6 Address
	 */
	public static String hexIPV6ToString(String hexIp) {
		String ip = "";

		if (hexIp == null) return ip;

		String sub;
		for (int j = hexIp.length(); j > 0; j -= 4) {
			sub = hexIp.substring(j - 2, j) + hexIp.substring(j - 4, j - 2);
			ip = ip + ((Integer.parseInt(sub, 16) == 0) ? "" : sub.replaceFirst("^0+(?!$)", "")) + ":";
		}

		ip = ip.substring(0, ip.length() - 1).replaceAll("((?::0\\b){2,}):?(?!\\S*\\b\\1:0\\b)(\\S*)", "::$2").toLowerCase();
		return ip;
	}

	/**
	 * Helper method used to convert an IPV6 address to it's IPV4 equivalent
	 * @param hexIp the IP Address to convert
	 * @return the converted IPV4 value
	 */
	public static String hexIPV6toIPV4String(String hexIp) {
		String ip = "";

		if (hexIp == null) return ip;

		for (int j = 0; j < hexIp.length(); j += 2) {
			String sub = hexIp.substring(j, j + 2);
			int num = Integer.parseInt(sub, 16);
			ip = "." + num + ip;
		}

		ip = ip.substring(1, ip.length());

		Matcher match = ipv4Pattern.matcher(ip);
		match.lookingAt();
		ip = match.group(0);

		return ip;
	}

	//Connection
	/**
	 * Helper method to verify if the device is connected to Internet
	 * @param context the Context to use
	 * @return true or false
	 */
	public static boolean isConnected(Context context){
		ConnectivityManager manager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo netInfo = manager.getActiveNetworkInfo();

		return netInfo != null && netInfo.isConnectedOrConnecting();
	}

	/**
	 * Helper method used to verify if the network info object passed is connected
	 * @param networkInfo the NetworkInfo to inspect
	 * @return true or false
	 */
	public static boolean isConnected(NetworkInfo networkInfo) {
		return networkInfo != null && networkInfo.isConnected();
	}

	/**
	 * Helper method used to retrieve the type of active connection
	 * @param context
	 * @return -1 if not connected or one of {@link ConnectivityManager#TYPE_MOBILE}, {@link
	 * ConnectivityManager#TYPE_WIFI}, {@link ConnectivityManager#TYPE_WIMAX}, {@link
	 * ConnectivityManager#TYPE_ETHERNET},  {@link ConnectivityManager#TYPE_BLUETOOTH}, or other
	 */
	public static int getConnectionType(Context context) {
		ConnectivityManager manager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo netInfo = manager.getActiveNetworkInfo();

		if(netInfo == null) return -1;
		return netInfo.getType();
	}

	//Connection States

	/**
	 * Helper method used to verify if the provided status is active or not
	 * @param status the status to inspect
	 * @return true or false
	 */
	public static boolean isTcpActiveState(int status) {
		switch (status) {
			case NetworkUtils.TcpStates.ESTBLSH:
				return true;
			case NetworkUtils.TcpStates.FWAIT1:
				return true;
			case NetworkUtils.TcpStates.FWAIT2:
				return true;
			case NetworkUtils.TcpStates.TMEWAIT:
				return true;
			case NetworkUtils.TcpStates.CLSWAIT:
				return true;
			case NetworkUtils.TcpStates.LASTACK:
				return true;
			case NetworkUtils.TcpStates.CLOSING:
				return true;
		}

		return false;
	}

	//Wifi
	/**
	 * Helper method used to verify is the Wifi is turned on or not
	 * @param context the Context to use
	 * @return true or false
	 */
	public static boolean isWifiOn(Context context) {
		WifiManager wifi = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);

		return wifi.isWifiEnabled();
	}
}
