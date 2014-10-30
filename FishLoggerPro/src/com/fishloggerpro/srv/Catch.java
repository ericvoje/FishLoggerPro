package com.fishloggerpro.srv;

import java.io.Serializable;
import java.util.Date;

public class Catch implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private String note;
	private String species;
	private double weight;
	private double length;
	private String bait;
	private Date date;
	private String conditions;
	private String longitude;
	private String latitude;

	// private String image;

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
			String bait, String conditions, String longitude, String latitude) {
		this.species = species;
		this.note = note;
		this.weight = weight;
		this.length = length;
		this.bait = bait;
		date = new Date();
		this.conditions = conditions;
		this.longitude = longitude;
		this.latitude = latitude;
	}

	/**
	 * Returns a string formatted to be sent to the server
	 * 
	 * @return
	 */
	@Override
	public String toString() {
		StringBuilder str = new StringBuilder();

		str.append(species + " ");
		str.append(weight + " ");
		str.append(length + " ");
		str.append(bait + " ");
		str.append(date.toString() + " ");
		str.append(note + " ");
		str.append(conditions + " ");
		str.append(longitude + " ");
		str.append(latitude + " ");

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

	public String getConditions() {
		return conditions;
	}

	public String getLongitude() {
		return longitude;
	}

	public String getLatitude() {
		return latitude;
	}

}
