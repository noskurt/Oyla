package com.ygznsl.noskurt.oyla.entity;

public enum Gender {
    MALE('E'),
    FEMALE('K'),
    BOTH('B');

    private char character;

    Gender(char character){
        this.character = character;
    }

    public char getCharacter() {
        return character;
    }

    public static Gender of(char character){
        switch (character){
            case 'E':
                return MALE;
            case 'K':
                return FEMALE;
            case 'B':
                return BOTH;
            default:
                return null;
        }
    }

}
