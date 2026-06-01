// Ildaphonse Cornolius - COMP254 Assignment 1- 300699371

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

/**
 * An implementation of a circularly linked list.
 *
 * @author Michael T. Goodrich
 * @author Roberto Tamassia
 * @author Michael H. Goldwasser
 */
public class CircularlyLinkedList<E> {
  //---------------- nested Node class ----------------
  /**
   * Singly linked node, which stores a reference to its element and
   * to the subsequent node in the list.
   */
  private static class Node<E> { //inner class

    /** The element stored at this node */
    private E element;     // an element stored at this node

    /** A reference to the subsequent node in the list */
    private Node<E> next;  // a reference to the subsequent node in the list

    /**
     * Creates a node with the given element and next node.
     *
     * @param e  the element to be stored
     * @param n  reference to a node that should follow the new node
     */
    public Node(E e, Node<E> n) {
      element = e;
      next = n;
    }

    // Accessor methods
    /**
     * Returns the element stored at the node.
     * @return the element stored at the node
     */
    public E getElement() { return element; }

    /**
     * Returns the node that follows this one (or null if no such node).
     * @return the following node
     */
    public Node<E> getNext() { return next; }

    // Modifier methods
    /**
     * Sets the node's next reference to point to Node n.
     * @param n    the node that should follow this one
     */
    public void setNext(Node<E> n) { next = n; }
  } //----------- end of nested Node class -----------
  //
  // instance variables of the CircularlyLinkedList
  /** The designated cursor of the list */
  private Node<E> tail = null;                  // we store tail (but not head)

  /** Number of nodes in the list */
  private int size = 0;                         // number of nodes in the list

  public static class WorkItem {
    private final int id;
    private final String name;
    private final int priority;

    public WorkItem(int id, String name, int priority) {
      this.id = id;
      this.name = name;
      this.priority = priority;
    }

    public int getId() {
      return id;
    }

    public WorkItem withPriority(int newPriority) {
      return new WorkItem(id, name, newPriority);
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
          && Objects.equals(name, workItem.name);
    }

    @Override
    public int hashCode() {
      return Objects.hash(id, name, priority);
    }

    @Override
    public String toString() {
      return "WorkItem{id=" + id + ", name='" + name + "', priority=" + priority + "}";
    }
  }

  /** Constructs an initially empty list. */
  public CircularlyLinkedList() { }             // constructs an initially empty list

  // access methods
  /**
   * Returns the number of elements in the linked list.
   * @return number of elements in the linked list
   */
  public int size() { return size; }

  /**
   * Tests whether the linked list is empty.
   * @return true if the linked list is empty, false otherwise
   */
  public boolean isEmpty() { return size == 0; }

  /**
   * Returns (but does not remove) the first element of the list
   * @return element at the front of the list (or null if empty)
   */
  public E first() {             // returns (but does not remove) the first element
    if (isEmpty()) return null;
    return tail.getNext().getElement();         // the head is *after* the tail
  }

  /**
   * Returns (but does not remove) the last element of the list
   * @return element at the back of the list (or null if empty)
   */
  public E last() {              // returns (but does not remove) the last element
    if (isEmpty()) return null;
    return tail.getElement();
  }

  // update methods
  /**
   * Rotate the first element to the back of the list.
   */
  public void rotate() {         // rotate the first element to the back of the list
    if (tail != null)                // if empty, do nothing
    	//assign head to tail
      tail = tail.getNext();         // the old head becomes the new tail
  }

  /**
   * Adds an element to the front of the list.
   * @param e  the new element to add
   */
  public void addFirst(E e) {                // adds element e to the front of the list
    if (size == 0) {
      tail = new Node<>(e, null);
      tail.setNext(tail);                     // link to itself circularly
    } else {
    	//the new node becomes first
      Node<E> newest = new Node<>(e, tail.getNext());
      tail.setNext(newest); //tail refers back to it
    }
    size++;
  }

  /**
   * Adds an element to the end of the list.
   * @param e  the new element to add
   */
  public void addLast(E e) { // adds element e to the end of the list
    addFirst(e);             // insert new element at front of list
    tail = tail.getNext();   // now new element becomes the tail
  }

  /**
   * Removes and returns the first element of the list.
   * @return the removed element (or null if empty)
   */
  public E removeFirst() {                   // removes and returns the first element
    if (isEmpty()) return null;              // nothing to remove
    Node<E> head = tail.getNext();
    if (head == tail) tail = null;           // must be the only node left
    //make tail to pint to where head pointed to
    else tail.setNext(head.getNext());       // removes "head" from the list
    size--;
    return head.getElement();
  }

  public boolean sameSequence(CircularlyLinkedList<WorkItem> other) {
    if (other == null || size != other.size) {
      return false;
    }
    if (size == 0) {
      return true;
    }

    List<WorkItem> mine = toWorkItemList();
    List<WorkItem> theirs = other.toWorkItemList();
    for (int start = 0; start < theirs.size(); start++) {
      if (matchesFrom(mine, theirs, start)) {
        return true;
      }
    }
    return false;
  }

  private boolean matchesFrom(List<WorkItem> mine, List<WorkItem> theirs, int start) {
    for (int index = 0; index < mine.size(); index++) {
      WorkItem left = mine.get(index);
      WorkItem right = theirs.get((start + index) % theirs.size());
      if (!Objects.equals(left, right)) {
        return false;
      }
    }
    return true;
  }

  private List<WorkItem> toWorkItemList() {
    List<WorkItem> values = new ArrayList<>();
    if (isEmpty()) {
      return values;
    }

    Node<E> walk = tail.getNext();
    for (int count = 0; count < size; count++) {
      values.add((WorkItem) walk.getElement());
      walk = walk.getNext();
    }
    return values;
  }

  @SuppressWarnings("unchecked")
  public void replaceFirst(WorkItem element) {
    if (isEmpty()) {
      return;
    }
    tail.getNext().element = (E) element;
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

  /**
   * Produces a string representation of the contents of the list.
   * This exists for debugging purposes only.
   */
  public String toString() {
    if (tail == null) return "()";
    StringBuilder sb = new StringBuilder("(");
    Node<E> walk = tail;
    do {
      walk = walk.getNext();
      sb.append(walk.getElement());
      if (walk != tail)
        sb.append(", ");
    } while (walk != tail);
    sb.append(")");
    return sb.toString();
  }

  void main() throws IOException {
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
    IO.println("c1.sameSequence(c2): " + c1.sameSequence(c2));

    WorkItem changed = c2.first().withPriority(99);
    c2.replaceFirst(changed);
    IO.println("C2 after modifying the first item priority:");
    IO.println(c2);
    IO.println("c1.sameSequence(c2) after modification: " + c1.sameSequence(c2));
  }
}
