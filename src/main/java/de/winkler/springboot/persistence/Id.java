package de.winkler.springboot.persistence;

public interface Id<TYPE> {

    Long id();

    TYPE get();

    Id<TYPE> identifier();

}
