package com.fishloggerpro.srv;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

import android.app.IntentService;
import android.content.Intent;
import android.os.IBinder;

import com.fishloggerpro.act.MenuActivity;

public class DBService extends IntentService {

	boolean isConnected;

	public DBService(String name) {
		super(name);
		isConnected = false;
	}

	public DBService() {
		super("FLP_IntentService");
		isConnected = false;
	}

	private Socket socket;
	private PrintWriter out;
	private BufferedReader in;
	private String connectionKey;

	/**
	 * Entry point for IntentService. Walks through command extra to communicate
	 * with server.
	 */
	@Override
	protected void onHandleIntent(Intent intent) {

		String command = intent.getExtras().getString("command");

		if (command == null) {
			return;
		} else if (command.compareTo("login") == 0) {
			testLogin("beerent", "password1");
		} else if (command.compareTo("add") == 0) {
			testAddFish();
		} else if (command.compareTo("search") == 0) {

		}

		return;
	}

	/**
	 * Connect to server
	 */
	private void connect() {
		try {
			socket = new Socket("192.168.1.109", 5678);
			out = new PrintWriter(socket.getOutputStream(), true);
			in = new BufferedReader(new InputStreamReader(
					socket.getInputStream()));
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	/**
	 * Reads from server
	 * 
	 * @return
	 */
	public String read() {
		try {
			return in.readLine();
		} catch (Exception e) {
			System.out.println(e.getStackTrace());
		}
		return null;
	}

	/**
	 * Writes to server
	 * 
	 * @param str
	 */
	private void write(String str) {
		out.println(str);
	}

	/**
	 * Attempts to add user to
	 * 
	 * @param username
	 * @param password
	 */
	public void testNewUser(String username, String password) {
		connect();
		read(); // read OK
		out.println("new;" + username + ";" + password);
		System.out.println(read());
	}

	/**
	 * Attempts to login to server with given credentials
	 * 
	 * @param username
	 * @param password
	 */
	public void testLogin(String username, String password) {
		connect();
		String result = read();
		if (result == null) {
			System.out.println("No response.");
			return;
		}
		out.println("login;" + username + ";" + password);
		result = read();
		this.connectionKey = result;
		System.out.println(result + " of length " + result.length());
		isConnected = true;
		Intent intent = new Intent(this, MenuActivity.class);
		startActivity(intent);
	}

	/**
	 * Attempts to add a fish
	 */
	public void testAddFish() {
		connect();
		read();
		out.println("add;" + this.connectionKey);
		if (read().equals("-1"))
			System.out.println("connection authentication failed");
		else {
			System.out.println("user recognized");
		}
	}

	/**
	 * Attempts to fetch a fish from the server
	 * 
	 * @param out
	 * @param in
	 */
	public void testGetFish(PrintWriter out, BufferedReader in) {

	}

}
