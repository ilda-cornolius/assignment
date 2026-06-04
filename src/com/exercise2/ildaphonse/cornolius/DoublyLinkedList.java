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

  //---------------- nested Node class ----------------
  /**
   * Node of a doubly linked list, which stores a reference to its
   * element and to both the previous and next node in the list.
   */
  private static class Node<E> {

    /** The element stored at this node */
    private E element;               // reference to the element stored at this node

    /** A reference to the preceding node in the list */
    private Node<E> prev;            // reference to the previous node in the list

    /** A reference to the subsequent node in the list */
    private Node<E> next;            // reference to the subsequent node in the list

    /**
     * Creates a node with the given element and next node.
     *
     * @param e  the element to be stored
     * @param p  reference to a node that should precede the new node
     * @param n  reference to a node that should follow the new node
     */
    public Node(E e, Node<E> p, Node<E> n) {
      element = e;
      prev = p;
      next = n;
    }

    // public accessor methods
    /**
     * Returns the element stored at the node.
     * @return the element stored at the node
     */
    public E getElement() { return element; }

    /**
     * Returns the node that precedes this one (or null if no such node).
     * @return the preceding node
     */
    public Node<E> getPrev() { return prev; }

    /**
     * Returns the node that follows this one (or null if no such node).
     * @return the following node
     */
    public Node<E> getNext() { return next; }

    // Update methods
    /**
     * Sets the node's previous reference to point to Node n.
     * @param p    the node that should precede this one
     */
    public void setPrev(Node<E> p) { prev = p; }

    /**
     * Sets the node's next reference to point to Node n.
     * @param n    the node that should follow this one
     */
    public void setNext(Node<E> n) { next = n; }

  } //----------- end of nested Node class -----------

  // instance variables of the DoublyLinkedList
  /** Sentinel node at the beginning of the list */
  private Node<E> header;                    // header sentinel

  /** Sentinel node at the end of the list */
  private Node<E> trailer;                   // trailer sentinel

  /** Number of elements in the list (not including sentinels) */
  private int size = 0;                      // number of elements in the list

  public interface Identifiable {
    int getId();
  }

  public static class WorkItem implements Identifiable {
    private final int id;
    private final String name;
    private final int priority;

    public WorkItem(int id, String name, int priority) {
      this.id = id;
      this.name = name;
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
      return "WorkItem{id=" + id + ", name='" + name + "', priority=" + priority + "}";
    }
  }

  /** Constructs a new empty list. */
  public DoublyLinkedList() {
    header = new Node<>(null, null, null);      // create header
    trailer = new Node<>(null, header, null);   // trailer is preceded by header
    header.setNext(trailer);                    // header is followed by trailer
  }

  // public accessor methods
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
   * Returns (but does not remove) the first element of the list.
   * @return element at the front of the list (or null if empty)
   */
  public E first() {
    if (isEmpty()) return null;
    return header.getNext().getElement();   // first element is beyond header
  }

  /**
   * Returns (but does not remove) the last element of the list.
   * @return element at the end of the list (or null if empty)
   */
  public E last() {
    if (isEmpty()) return null;
    return trailer.getPrev().getElement();    // last element is before trailer
  }

  // public update methods
  /**
   * Adds an element to the front of the list.
   * @param e   the new element to add
   */
  public void addFirst(E e) {
    addBetween(e, header, header.getNext());    // place just after the header
  }

  /**
   * Adds an element to the end of the list.
   * @param e   the new element to add
   */
  public void addLast(E e) {
    addBetween(e, trailer.getPrev(), trailer);  // place just before the trailer
  }

  /**
   * Removes and returns the first element of the list.
   * @return the removed element (or null if empty)
   */
  public E removeFirst() {
    if (isEmpty()) return null;                  // nothing to remove
    return remove(header.getNext());             // first element is beyond header
  }

  /**
   * Removes and returns the last element of the list.
   * @return the removed element (or null if empty)
   */
  public E removeLast() {
    if (isEmpty()) return null;                  // nothing to remove
    return remove(trailer.getPrev());            // last element is before trailer
  }

  // private update methods
  /**
   * Adds an element to the linked list in between the given nodes.
   * The given predecessor and successor should be neighboring each
   * other prior to the call.
   *
   * @param predecessor   node just before the location where the new element is inserted
   * @param successor     node just after the location where the new element is inserted
   */
  private void addBetween(E e, Node<E> predecessor, Node<E> successor) {
    // create and link a new node
    Node<E> newest = new Node<>(e, predecessor, successor);
    predecessor.setNext(newest);
    successor.setPrev(newest);
    size++;

    /* newest = Node(e,predecessor,successor)
     * predecessor.next=newest
     * successor.prev=newest
     * size=size+1
     *
     */
  }

  /**
   * Removes the given node from the list and returns its element.
   * @param node    the node to be removed (must not be a sentinel)
   */
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
//this method saves data into a textpad file 
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

  private static String gameNameForId(int id) {
    return switch (id) {
      case 101 -> "Defeat monster";
      case 102 -> "Quest reward";
      case 103 -> "Player turn";
      case 104 -> "Create boss";
      case 105 -> "Achievement";
      case 106 -> "Save game state";
      default -> "Game event " + id;
    };
  }

  private static WorkItem createGameEvent(int id, int priority) {
    return new WorkItem(id, gameNameForId(id), priority);
  }

  /**
   * Produces a string representation of the contents of the list.
   * This exists for debugging purposes only.
   */
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

  void main() throws IOException {
    
    List<Integer> checkpointIds = readCheckpoint("checkpoint1.txt");
    //creating two doublylinked lists called l1 and l2
    DoublyLinkedList<WorkItem> l1 = new DoublyLinkedList<>();
    DoublyLinkedList<WorkItem> l2 = new DoublyLinkedList<>();

    
    int priority = 1;
    //this for loop populates the two linked lists that will be concatenated later
    for (int id : checkpointIds) {
      WorkItem original = createGameEvent(id, priority);
      l1.addLast(original);
      l2.addLast(createGameEvent(id, original.getPriority() + 10));
      priority++;
    }

    //printing out the l1 list value
    IO.println("Exercise 2 - Doubly Linked List");
    IO.println("L1 built from checkpoint1.txt:");
    IO.println(l1);
    IO.println("L2 with transformed priorities:");
    IO.println(l2);

    //concatening link list l2 to the end of l1
    l1.concatenate(l2);
    IO.println("Final concatenated list:");
    IO.println(l1);
    IO.println("L2 after concatenation:");
    IO.println(l2);
//this method then saves the ids of the concatenedated list into l1 
    saveCheckpoint("checkpoint2.txt", l1.idSequence());
    IO.println("Saved checkpoint2.txt: " + l1.idSequence());
  }

} //----------- end of DoublyLinkedList class -----------
