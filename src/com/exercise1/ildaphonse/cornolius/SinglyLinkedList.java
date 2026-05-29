package com.exercise1.ildaphonse.cornolius;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

class IO {
    static void println(Object value) {
        System.out.println(value);
    }
}

public class SinglyLinkedList<E> {
    public static class Node<E> {
        private E element;
        private Node<E> next;

        public Node(E element, Node<E> next) {
            this.element = element;
            this.next = next;
        }

        public E getElement() {
            return element;
        }

        public Node<E> getNext() {
            return next;
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

        @Override
        public String toString() {
            return "WorkItem{id=" + id + ", title='" + title + "', priority=" + priority + "}";
        }
    }

    private Node<E> head = null;
    private Node<E> tail = null;
    private int size = 0;

    public SinglyLinkedList() {
    }

    public int size() {
        return size;
    }

    public boolean isEmpty() {
        return size == 0;
    }

    public E first() {
        return isEmpty() ? null : head.getElement();
    }

    public E last() {
        return isEmpty() ? null : tail.getElement();
    }

    public void addFirst(E element) {
        head = new Node<>(element, head);
        if (size == 0) {
            tail = head;
        }
        size++;
    }

    public void addLast(E element) {
        Node<E> newest = new Node<>(element, null);
        if (isEmpty()) {
            head = newest;
        } else {
            tail.next = newest;
        }
        tail = newest;
        size++;
    }

    public E removeFirst() {
        if (isEmpty()) {
            return null;
        }
        E answer = head.getElement();
        head = head.getNext();
        size--;
        if (size == 0) {
            tail = null;
        }
        return answer;
    }

    public void swapTwoNodes(Node<E> node1, Node<E> node2) {
        if (node1 == null || node2 == null || node1 == node2) {
            return;
        }

        Node<E> previous1 = findPrevious(node1);
        Node<E> previous2 = findPrevious(node2);
        if ((node1 != head && previous1 == null) || (node2 != head && previous2 == null)) {
            return;
        }

        if (node1.next == node2) {
            swapAdjacent(previous1, node1, node2);
        } else if (node2.next == node1) {
            swapAdjacent(previous2, node2, node1);
        } else {
            swapSeparated(previous1, node1, previous2, node2);
        }

        if (node1.next == null) {
            tail = node1;
        } else if (node2.next == null) {
            tail = node2;
        }
    }

    private Node<E> findPrevious(Node<E> target) {
        if (target == head) {
            return null;
        }

        Node<E> current = head;
        while (current != null && current.next != target) {
            current = current.next;
        }
        return current;
    }

    private void swapAdjacent(Node<E> beforeFirst, Node<E> firstNode, Node<E> secondNode) {
        if (beforeFirst == null) {
            head = secondNode;
        } else {
            beforeFirst.next = secondNode;
        }

        firstNode.next = secondNode.next;
        secondNode.next = firstNode;
    }

    private void swapSeparated(Node<E> previous1, Node<E> node1, Node<E> previous2, Node<E> node2) {
        if (previous1 == null) {
            head = node2;
        } else {
            previous1.next = node2;
        }

        if (previous2 == null) {
            head = node1;
        } else {
            previous2.next = node1;
        }

        Node<E> tempNext = node1.next;
        node1.next = node2.next;
        node2.next = tempNext;
    }

    public Node<E> locateNodeById(int id) {
        Node<E> current = head;
        while (current != null) {
            if (current.getElement() instanceof Identifiable item && item.getId() == id) {
                return current;
            }
            current = current.getNext();
        }
        return null;
    }

    public List<Integer> idSequence() {
        List<Integer> ids = new ArrayList<>();
        Node<E> current = head;
        while (current != null) {
            if (current.getElement() instanceof Identifiable item) {
                ids.add(item.getId());
            }
            current = current.getNext();
        }
        return ids;
    }

    private static void saveCheckpoint(String fileName, List<Integer> ids) throws IOException {
        String line = ids.stream()
                .map(String::valueOf)
                .collect(Collectors.joining(","));
        Files.writeString(Path.of(fileName), line);
    }

    @Override
    public String toString() {
        List<String> values = new ArrayList<>();
        Node<E> current = head;
        while (current != null) {
            values.add(String.valueOf(current.getElement()));
            current = current.getNext();
        }
        return values.toString();
    }

    public static void main(String[] args) throws IOException {
        // Ildaphonse Cornolius - 300699371
        SinglyLinkedList<WorkItem> assignments = new SinglyLinkedList<>();
        assignments.addLast(new WorkItem(101, "Read chapter notes", 1));
        assignments.addLast(new WorkItem(102, "Complete quiz", 2));
        assignments.addLast(new WorkItem(103, "Draft lab answer", 3));
        assignments.addLast(new WorkItem(104, "Review lecture code", 4));
        assignments.addLast(new WorkItem(105, "Prepare submission", 5));
        assignments.addLast(new WorkItem(106, "Upload files", 6));

        IO.println("Exercise 1 - Singly Linked List Node Swap");
        IO.println("Before swaps:");
        IO.println(assignments);

        assignments.swapTwoNodes(assignments.locateNodeById(102), assignments.locateNodeById(105));
        IO.println("After normal swap of IDs 102 and 105:");
        IO.println(assignments);

        assignments.swapTwoNodes(assignments.locateNodeById(103), assignments.locateNodeById(104));
        IO.println("After adjacent-node swap of IDs 103 and 104:");
        IO.println(assignments);

        assignments.swapTwoNodes(assignments.locateNodeById(101), assignments.locateNodeById(101));
        IO.println("After same-node swap of ID 101:");
        IO.println(assignments);

        saveCheckpoint("checkpoint1.txt", assignments.idSequence());
        IO.println("Saved checkpoint1.txt: " + assignments.idSequence());
    }
}
