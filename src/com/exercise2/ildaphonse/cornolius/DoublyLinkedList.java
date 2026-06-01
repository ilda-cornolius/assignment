/*
 * Copyright 2014, Michael T. Goodrich, Roberto Tamassia, Michael H. Goldwasser
 *
 * Developed for use with the book:
 *
 *    Data Structures and Algorithms in Java, Sixth Edition
 *    Michael T. Goodrich, Roberto Tamassia, and Michael H. Goldwasser
 *    John Wiley & Sons, 2014
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
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

/**
 * A basic doubly linked list implementation.
 *
 * @author Michael T. Goodrich
 * @author Roberto Tamassia
 * @author Michael H. Goldwasser
 */
public class DoublyLinkedList<E> {

  private static class Node<E> {

    private E element;
    private Node<E> prev;
    private Node<E> next;

    public Node(E e, Node<E> p, Node<E> n) {
      element = e;
      prev = p;
      next = n;
    }

    public E getElement() { return element; }

    public Node<E> getPrev() { return prev; }

    public Node<E> getNext() { return next; }

    public void setPrev(Node<E> p) { prev = p; }

    public void setNext(Node<E> n) { next = n; }
  } //----------- end of nested Node class -----------

  private Node<E> header;
  private Node<E> trailer;
  private int size = 0;

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

  public DoublyLinkedList() {
    header = new Node<>(null, null, null);
    trailer = new Node<>(null, header, null);
    header.setNext(trailer);
  }

  public int size() { return size; }

  public boolean isEmpty() { return size == 0; }

  public E first() {
    if (isEmpty()) return null;
    return header.getNext().getElement();
  }

  public E last() {
    if (isEmpty()) return null;
    return trailer.getPrev().getElement();
  }

  public void addFirst(E e) {
    addBetween(e, header, header.getNext());
  }

  public void addLast(E e) {
    addBetween(e, trailer.getPrev(), trailer);
  }

  public E removeFirst() {
    if (isEmpty()) return null;
    return remove(header.getNext());
  }

  public E removeLast() {
    if (isEmpty()) return null;
    return remove(trailer.getPrev());
  }

  private void addBetween(E e, Node<E> predecessor, Node<E> successor) {
    Node<E> newest = new Node<>(e, predecessor, successor);
    predecessor.setNext(newest);
    successor.setPrev(newest);
    size++;
  }

  private E remove(Node<E> node) {
    Node<E> predecessor = node.getPrev();
    Node<E> successor = node.getNext();
    predecessor.setNext(successor);
    successor.setPrev(predecessor);
    size--;
    return node.getElement();
  }

  public void concatenate(DoublyLinkedList<E> other) {
    if (other == null || other.isEmpty()) {
      return;
    }

    Node<E> thisLast = trailer.getPrev();
    Node<E> otherFirst = other.header.getNext();
    Node<E> otherLast = other.trailer.getPrev();

    thisLast.setNext(otherFirst);
    otherFirst.setPrev(thisLast);
    otherLast.setNext(trailer);
    trailer.setPrev(otherLast);
    size += other.size;

    other.header.setNext(other.trailer);
    other.trailer.setPrev(other.header);
    other.size = 0;
  }

  public List<Integer> idSequence() {
    List<Integer> ids = new ArrayList<>();
    Node<E> walk = header.getNext();
    while (walk != trailer) {
      if (walk.getElement() instanceof Identifiable item) {
        ids.add(item.getId());
      }
      walk = walk.getNext();
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

  public String toString() {
    StringBuilder sb = new StringBuilder("(");
    Node<E> walk = header.getNext();
    while (walk != trailer) {
      sb.append(walk.getElement());
      walk = walk.getNext();
      if (walk != trailer)
        sb.append(", ");
    }
    sb.append(")");
    return sb.toString();
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
