package it.unisa.mobileprogramming;

public class Participant {
	private String name,surname,publicScopusLink,h_index,affiliation,subjectArea;

	public Participant(String name, String surname, String publicScopusLink, String h_index, String affiliation,
			String subjectArea) {
		this.name = name;
		this.surname = surname;
		this.publicScopusLink = publicScopusLink;
		this.h_index = h_index;
		this.affiliation = affiliation;
		this.subjectArea = subjectArea;
	}

	public String getSubjectArea() {
		return subjectArea;
	}

	public String getName() {
		return name;
	}

	public String getSurname() {
		return surname;
	}

	public String getPublicScopusLink() {
		return publicScopusLink;
	}

	public String getH_index() {
		return h_index;
	}

	public String getAffiliation() {
		return affiliation;
	}
}
