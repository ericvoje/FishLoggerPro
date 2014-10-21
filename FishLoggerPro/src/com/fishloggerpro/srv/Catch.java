package com.fishloggerpro.srv;

import java.util.Date;

public class Catch {

	private String note;
	private String species;
	private double weight;
	private double length;
	private String bait;
	private Date date;

	// image
	// time?

	/**
	 * Create a new Catch type. Image support to be added later
	 * 
	 * @param species
	 * @param note
	 * @param weight
	 * @param length
	 * @param bait
	 */
	public Catch(String species, String note, double weight, double length,
			String bait) {
		this.species = species;
		this.note = note;
		this.weight = weight;
		this.length = length;
		this.bait = bait;
		date = new Date();
	}

	/**
	 * Returns a string formatted to be sent to the server
	 * 
	 * @return
	 */
	public String printCatchString() {
		StringBuilder str = new StringBuilder();

		str.append(species + " ");
		str.append(weight + " ");
		str.append(length + " ");
		str.append(bait + " ");
		str.append(date.toString() + " ");
		str.append(note);

		return str.toString();
	}

	public String getNote() {
		return note;
	}

	public String getSpecies() {
		return species;
	}

	public double getWeight() {
		return weight;
	}

	public double getLength() {
		return length;
	}

	public String getBait() {
		return bait;
	}

	public Date getDate() {
		return date;
	}

}
