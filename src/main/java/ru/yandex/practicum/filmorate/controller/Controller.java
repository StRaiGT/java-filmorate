package ru.yandex.practicum.filmorate.controller;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

public abstract class Controller<T> {
    protected final Map<Integer, T> items = new HashMap<>();
    protected int id = 1;
    abstract public Collection<T> getAll();
    abstract public T add(T t);
    abstract public T update(T t);
    abstract protected void validate(T t);
}
