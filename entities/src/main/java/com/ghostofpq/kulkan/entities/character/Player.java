package com.ghostofpq.kulkan.entities.character;

import java.io.Serializable;
import java.util.Date;

public class Player implements Serializable {
    private static final long serialVersionUID = 3105715593359746378L;
    private String pseudo;
    private String firstName;
    private String lastName;
    private Date dateOfBirth;
    private int grade;
    private Team team;

    public Player(String pseudo) {
        this.pseudo = pseudo;

        StringBuilder teamNameBuilder = new StringBuilder();
        teamNameBuilder.append(pseudo);
        teamNameBuilder.append("'s Team");
        team = new Team(teamNameBuilder.toString());
    }

    /**
     * Getters and Setters
     */

    public String getPseudo() {
        return pseudo;
    }

    public void setPseudo(String pseudo) {
        this.pseudo = pseudo;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public Date getDateOfBirth() {
        return dateOfBirth;
    }

    public void setDateOfBirth(Date dateOfBirth) {
        this.dateOfBirth = dateOfBirth;
    }

    public int getGrade() {
        return grade;
    }

    public void setGrade(int grade) {
        this.grade = grade;
    }

    public Team getTeam() {
        return team;
    }

    public void setTeam(Team team) {
        this.team = team;
    }
}
