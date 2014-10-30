package com.fishloggerpro.srv;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.ObjectOutputStream;
import java.io.PrintWriter;
import java.net.Socket;

import android.app.IntentService;
import android.content.Intent;
import android.os.Bundle;
import android.os.ResultReceiver;

public class DBService extends IntentService {

	private Socket socket;
	private PrintWriter out;
	private BufferedReader in;
	private String connectionKey;

	private ResultReceiver sReceiver;

	public DBService(String name) {
		super(name);
		connectionKey = "";
	}

	public DBService() {
		super("FLP_IntentService");
		connectionKey = "";
	}

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
			String result = testLogin("beerent", "password1");
			sReceiver = intent.getParcelableExtra("receiver");
			if (result.length() > 1) {
				Bundle b = new Bundle();
				b.putString("connectionKey", result);
				sReceiver.send(0, b);
			} else {
				sReceiver.send(-1, null);
			}
		} else if (command.compareTo("add") == 0) {
			sReceiver = intent.getParcelableExtra("receiver");
			Catch c = new Catch(intent.getExtras().getString("species"), intent
					.getExtras().getString("note"), intent.getExtras()
					.getDouble("weight"), intent.getExtras()
					.getDouble("length"), intent.getExtras().getString("bait"),
					intent.getExtras().getString("conditions"), intent
							.getExtras().getString("longitude"), intent
							.getExtras().getString("latitude"));
			connectionKey = intent.getStringExtra("connectionKey");
			int result = testAddFish(c);
			Bundle b = new Bundle();
			b.putInt("result", result);
			sReceiver.send(result, b);
		} else if (command.compareTo("search") == 0) {

		} else if (command.compareTo("newuser") == 0) {

		}

		return;
	}

	/**
	 * Connect to server
	 */
	private void connect() {
		try {
			socket = new Socket("192.168.1.26", 5678);
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
	public String testLogin(String username, String password) {
		connect();
		String result = read();
		if (result == null) {
			System.out.println("No response.");
			return "";
		}
		out.println("login");
		read();
		out.println(username);
		read();
		out.println(password);
		result = read();
		if (result.equals("-1")) {
			System.out.println("shit");
			return "";
		} else {
			this.connectionKey = result;
			System.out.println(result + " of length " + result.length());
			return result;
		}
	}

	/**
	 * Attempts to add a fish
	 */
	public int testAddFish(Catch c) {
		connect();
		read();
		out.println(connectionKey);
		if (read().equals("-1")) {
			System.out.println("connection authentication failed");
			return -1;
		}
		out.println("add");
		read();
		// Create the output stream to the server
		try {
			ObjectOutputStream outToServer = new ObjectOutputStream(
					socket.getOutputStream());
			outToServer.writeObject(c);
		} catch (IOException e) {
			e.printStackTrace();
			return -2;
		}
		return 0;

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
