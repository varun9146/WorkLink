package com.worklink.model;

public class Education {

	private int id;
	private String education;

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getEducation() {
		return education;
	}

	public void setEducation(String education) {
		this.education = education;
	}

	@Override
	public String toString() {
		return "Education [id=" + id + ", education=" + education + "]";
	}

}
