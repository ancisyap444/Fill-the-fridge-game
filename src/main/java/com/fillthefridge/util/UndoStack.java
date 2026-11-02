package com.fillthefridge.util;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Deque;
import java.util.List;
import java.util.Optional;

public class UndoStack<T> {
    private final Deque<T> stack;

    public UndoStack() {
        this.stack = new ArrayDeque<>();
    }

    public void push(T action) {
        if (action != null) {
            stack.push(action);
        }
    }

    public Optional<T> pop() {
        if (stack.isEmpty()) {
            return Optional.empty();
        }
        return Optional.of(stack.pop());
    }

    public Optional<T> peek() {
        if (stack.isEmpty()) {
            return Optional.empty();
        }
        return Optional.of(stack.peek());
    }

    public boolean remove(T action) {
        return stack.remove(action);
    }

    public boolean isEmpty() {
        return stack.isEmpty();
    }

    public int size() {
        return stack.size();
    }

    public void clear() {
        stack.clear();
    }

    public List<T> asList() {
        return new ArrayList<>(stack);
    }
}
