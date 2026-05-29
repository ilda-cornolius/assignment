package com.exercise2.ildaphonse.cornolius;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

class IO {
    static void println(Object value) {
        System.out.println(value);
    }
}

public class DoublyLinkedList<E> {
    private static class Node<E> {
        private E element;
        private Node<E> previous;
        private Node<E> next;

        public Node(E element, Node<E> previous, Node<E> next) {
            this.element = element;
            this.previous = previous;
            this.next = next;
        }

        public E getElement() {
            return element;
        }
    }

    public interface Identifiable {
        int getId();
    }

    public static class WorkItem implements Identifiable {
        private final int id;
        private final String title;
        private final int priority;

        public WorkItem(int id, String title, int priority) {
            this.id = id;
            this.title = title;
            this.priority = priority;
        }

        @Override
        public int getId() {
            return id;
        }

        public int getPriority() {
            return priority;
        }

        @Override
        public String toString() {
            return "WorkItem{id=" + id + ", title='" + title + "', priority=" + priority + "}";
        }
    }

    private Node<E> header;
    private Node<E> trailer;
    private int size = 0;

    public DoublyLinkedList() {
        header = new Node<>(null, null, null);
        trailer = new Node<>(null, header, null);
        header.next = trailer;
    }

    public int size() {
        return size;
    }

    public boolean isEmpty() {
        return size == 0;
    }

    public E first() {
        return isEmpty() ? null : header.next.getElement();
    }

    public E last() {
        return isEmpty() ? null : trailer.previous.getElement();
    }

    public void addFirst(E element) {
        addBetween(element, header, header.next);
    }

    public void addLast(E element) {
        addBetween(element, trailer.previous, trailer);
    }

    public E removeFirst() {
        return isEmpty() ? null : remove(header.next);
    }

    public E removeLast() {
        return isEmpty() ? null : remove(trailer.previous);
    }

    private void addBetween(E element, Node<E> predecessor, Node<E> successor) {
        Node<E> newest = new Node<>(element, predecessor, successor);
        predecessor.next = newest;
        successor.previous = newest;
        size++;
    }

    private E remove(Node<E> node) {
        Node<E> predecessor = node.previous;
        Node<E> successor = node.next;
        predecessor.next = successor;
        successor.previous = predecessor;
        size--;
        return node.getElement();
    }

    public void concatenate(DoublyLinkedList<E> other) {
        if (other == null || other.isEmpty()) {
            return;
        }

        Node<E> thisLast = trailer.previous;
        Node<E> otherFirst = other.header.next;
        Node<E> otherLast = other.trailer.previous;

        thisLast.next = otherFirst;
        otherFirst.previous = thisLast;
        otherLast.next = trailer;
        trailer.previous = otherLast;
        size += other.size;

        other.header.next = other.trailer;
        other.trailer.previous = other.header;
        other.size = 0;
    }

    public List<Integer> idSequence() {
        List<Integer> ids = new ArrayList<>();
        Node<E> current = header.next;
        while (current != trailer) {
            if (current.getElement() instanceof Identifiable item) {
                ids.add(item.getId());
            }
            current = current.next;
        }
        return ids;
    }

    private static void saveCheckpoint(String fileName, List<Integer> ids) throws IOException {
        String line = ids.stream()
                .map(String::valueOf)
                .collect(Collectors.joining(","));
        Files.writeString(Path.of(fileName), line);
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

    private static WorkItem createAssignment(int id, int priority) {
        return new WorkItem(id, "Assignment task " + id, priority);
    }

    @Override
    public String toString() {
        List<String> values = new ArrayList<>();
        Node<E> current = header.next;
        while (current != trailer) {
            values.add(String.valueOf(current.getElement()));
            current = current.next;
        }
        return values.toString();
    }

    public static void main(String[] args) throws IOException {
        // Ildaphonse Cornolius - 300699371
        List<Integer> checkpointIds = readCheckpoint("checkpoint1.txt");
        DoublyLinkedList<WorkItem> l1 = new DoublyLinkedList<>();
        DoublyLinkedList<WorkItem> l2 = new DoublyLinkedList<>();

        int priority = 1;
        for (int id : checkpointIds) {
            WorkItem original = createAssignment(id, priority);
            l1.addLast(original);
            l2.addLast(createAssignment(id, original.getPriority() + 10));
            priority++;
        }

        IO.println("Exercise 2 - Doubly Linked List Concatenation");
        IO.println("L1 built from checkpoint1.txt:");
        IO.println(l1);
        IO.println("L2 with transformed priorities:");
        IO.println(l2);

        l1.concatenate(l2);
        IO.println("Final concatenated list:");
        IO.println(l1);
        IO.println("L2 after concatenation:");
        IO.println(l2);

        saveCheckpoint("checkpoint2.txt", l1.idSequence());
        IO.println("Saved checkpoint2.txt: " + l1.idSequence());
    }
}
