package de.winkler.springboot.persistence;

public interface Id<TYPE extends Id<TYPE>> {

    Long id();

    TYPE type();

}
