package com.fishloggerpro.srv;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
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
			testLogin("beerent", "password1");
			sReceiver = intent.getParcelableExtra("receiver");
			if (connectionKey.length() > 1) {
				Bundle b = new Bundle();
				b.putString("connectionKey", connectionKey);
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
			File image = (File) intent.getExtras().get("image");
			connectionKey = intent.getStringExtra("connectionKey");
			int result = testAddFish(c, image);
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
	private int connect() {
		try {
			socket = new Socket("192.168.1.149", 5678);
			out = new PrintWriter(socket.getOutputStream(), true);
			in = new BufferedReader(new InputStreamReader(
					socket.getInputStream()));
		} catch (IOException e) {
			e.printStackTrace();
			return -1;
		}
		return 0;
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
		// Connect
		if (connect() == -1) {
			return;
		}
		String result = read();
		if (result == null) {
			System.out.println("No response.");
			return;
		}
		out.println("login");
		read();
		out.println(username);
		read();
		out.println(password);
		result = read();
		if (result.equals("-1")) {
			return;
		} else {
			this.connectionKey = result;
			return;
		}
	}

	/**
	 * Attempts to add a fish
	 */
	public int testAddFish(Catch c, File image) {
		System.out.println("try to send catch");
		// Connect
		if (connect() == -1) {
			System.out.println("connection failed");
			return -1;
		}
		System.out.println("connected");
		// Wait for response
		read();
		// Send connection key
		out.println(connectionKey);
		if (read().equals("-1")) {
			System.out.println("connection authentication failed");
			return -2;
		}
		// Send 'add' command
		out.println("add");
		System.out.println("add");
		// Wait for response
		read();
		System.out.println("add response");
		// Write Catch object to server
		try {
			ObjectOutputStream outToServer = new ObjectOutputStream(
					socket.getOutputStream());
			outToServer.writeObject(c);
			System.out.println("catch sent");
		} catch (IOException e) {
			e.printStackTrace();
			System.out.println("Catch object failed");
			return -3;
		}
		// Wait for server response
		read();
		System.out.println("catch response gotten");
		// Send image
		try {
			FileInputStream fIn = new FileInputStream(image);
			OutputStream os = socket.getOutputStream();

			byte[] buf = new byte[8192];
			int len = 0;
			while ((len = fIn.read(buf)) != -1) {
				os.write(buf, 0, len);
				System.out.println(len);
			}
			os.flush();
			System.out.println("image written");
		} catch (FileNotFoundException e) {
			e.printStackTrace();
			System.out.println("image write failed");
			return -4;
		} catch (IOException e) {
			e.printStackTrace();
			System.out.println("image write failed 2");
			return -4;
		}
		// read ok
		read();
		System.out.println("done sending catch");
		// Done
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
