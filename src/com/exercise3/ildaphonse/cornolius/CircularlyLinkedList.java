package com.exercise3.ildaphonse.cornolius;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

class IO {
    static void println(Object value) {
        System.out.println(value);
    }
}

public class CircularlyLinkedList<E> {
    private static class Node<E> {
        private E element;
        private Node<E> next;

        public Node(E element, Node<E> next) {
            this.element = element;
            this.next = next;
        }

        public E getElement() {
            return element;
        }
    }

    public static class WorkItem {
        private final int id;
        private final String title;
        private final int priority;

        public WorkItem(int id, String title, int priority) {
            this.id = id;
            this.title = title;
            this.priority = priority;
        }

        public int getId() {
            return id;
        }

        public WorkItem withPriority(int newPriority) {
            return new WorkItem(id, title, newPriority);
        }

        @Override
        public boolean equals(Object other) {
            if (this == other) {
                return true;
            }
            if (!(other instanceof WorkItem workItem)) {
                return false;
            }
            return id == workItem.id
                    && priority == workItem.priority
                    && Objects.equals(title, workItem.title);
        }

        @Override
        public int hashCode() {
            return Objects.hash(id, title, priority);
        }

        @Override
        public String toString() {
            return "WorkItem{id=" + id + ", title='" + title + "', priority=" + priority + "}";
        }
    }

    private Node<E> tail = null;
    private int size = 0;

    public CircularlyLinkedList() {
    }

    public int size() {
        return size;
    }

    public boolean isEmpty() {
        return size == 0;
    }

    public E first() {
        return isEmpty() ? null : tail.next.getElement();
    }

    public E last() {
        return isEmpty() ? null : tail.getElement();
    }

    public void rotate() {
        if (tail != null) {
            tail = tail.next;
        }
    }

    public void addFirst(E element) {
        if (size == 0) {
            tail = new Node<>(element, null);
            tail.next = tail;
        } else {
            Node<E> newest = new Node<>(element, tail.next);
            tail.next = newest;
        }
        size++;
    }

    public void addLast(E element) {
        addFirst(element);
        tail = tail.next;
    }

    public E removeFirst() {
        if (isEmpty()) {
            return null;
        }
        Node<E> head = tail.next;
        if (head == tail) {
            tail = null;
        } else {
            tail.next = head.next;
        }
        size--;
        return head.getElement();
    }

    public void replaceFirst(E element) {
        if (isEmpty()) {
            return;
        }
        tail.next.element = element;
    }

    public boolean sameSequence(CircularlyLinkedList<E> other) {
        if (other == null || size != other.size) {
            return false;
        }
        if (size == 0) {
            return true;
        }

        List<E> mine = toList();
        List<E> theirs = other.toList();
        for (int start = 0; start < theirs.size(); start++) {
            if (matchesFrom(mine, theirs, start)) {
                return true;
            }
        }
        return false;
    }

    private boolean matchesFrom(List<E> mine, List<E> theirs, int start) {
        for (int index = 0; index < mine.size(); index++) {
            E left = mine.get(index);
            E right = theirs.get((start + index) % theirs.size());
            if (!Objects.equals(left, right)) {
                return false;
            }
        }
        return true;
    }

    public List<E> toList() {
        List<E> values = new ArrayList<>();
        if (isEmpty()) {
            return values;
        }

        Node<E> current = tail.next;
        for (int count = 0; count < size; count++) {
            values.add(current.getElement());
            current = current.next;
        }
        return values;
    }

    private static List<Integer> readCheckpoint(String fileName) throws IOException {
        String line = Files.readString(Path.of(fileName)).trim();
        if (line.isEmpty()) {
            return List.of();
        }
        return Arrays.stream(line.split(","))
                .map(String::trim)
                .map(Integer::parseInt)
                .toList();
    }

    private static CircularlyLinkedList<WorkItem> buildAssignments(List<Integer> ids) {
        CircularlyLinkedList<WorkItem> list = new CircularlyLinkedList<>();
        int priority = 1;
        for (int id : ids) {
            list.addLast(new WorkItem(id, "Assignment task " + id, priority));
            priority++;
        }
        return list;
    }

    @Override
    public String toString() {
        return toList().toString();
    }

    public static void main(String[] args) throws IOException {
        // Ildaphonse Cornolius - 300699371
        List<Integer> checkpointIds = readCheckpoint("checkpoint2.txt");
        CircularlyLinkedList<WorkItem> c1 = buildAssignments(checkpointIds);
        CircularlyLinkedList<WorkItem> c2 = buildAssignments(checkpointIds);
        c2.rotate();
        c2.rotate();
        c2.rotate();

        IO.println("Exercise 3 - Circular Linked List Rotation Check");
        IO.println("C1 from checkpoint2.txt:");
        IO.println(c1);
        IO.println("C2 rotated from C1:");
        IO.println(c2);
        IO.println("C1.sameSequence(C2): " + c1.sameSequence(c2));

        WorkItem changed = c2.first().withPriority(99);
        c2.replaceFirst(changed);
        IO.println("C2 after modifying the first item priority:");
        IO.println(c2);
        IO.println("C1.sameSequence(C2) after modification: " + c1.sameSequence(c2));
    }
}
