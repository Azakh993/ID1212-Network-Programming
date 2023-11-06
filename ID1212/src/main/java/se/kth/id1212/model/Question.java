package se.kth.id1212.model;

public record Question(Integer id, String questionText, String answer, String[] options) {
}