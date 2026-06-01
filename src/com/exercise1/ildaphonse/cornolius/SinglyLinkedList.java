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

/**
 * A basic singly linked list implementation.
 *
 * @author Michael T. Goodrich
 * @author Roberto Tamassia
 * @author Michael H. Goldwasser
 */
public class SinglyLinkedList<E> implements Cloneable {
  //---------------- nested Node class ----------------
  /**
   * Node of a singly linked list, which stores a reference to its
   * element and to the subsequent node in the list (or null if this
   * is the last node).
   */
  public static class Node<E> {

    /** The element stored at this node */
    private E element;            // reference to the element stored at this node

    /** A reference to the subsequent node in the list */
    private Node<E> next;         // reference to the subsequent node in the list

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
  // instance variables of the SinglyLinkedList
  /** The head node of the list */
  private Node<E> head = null;               // head node of the list (or null if empty)

  /** The last node of the list */
  private Node<E> tail = null;               // last node of the list (or null if empty)

  /** Number of nodes in the list */
  private int size = 0;                      // number of nodes in the list

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

    @Override
    public String toString() {
      return "WorkItem{id=" + id + ", name='" + name + "', priority=" + priority + "}";
    }
  }

  /** Constructs an initially empty list. */
  public SinglyLinkedList() { }              // constructs an initially empty list

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
    return head.getElement();
  }

  /**
   * Returns (but does not remove) the last element of the list.
   * @return element at the end of the list (or null if empty)
   */
  public E last() {              // returns (but does not remove) the last element
    if (isEmpty()) return null;
    return tail.getElement();
  }

  // update methods
  /**
   * Adds an element to the front of the list.
   * @param e  the new element to add
   */
  public void addFirst(E e) {         // adds element e to the front of the list
    head = new Node<>(e, head);       // create and link a new node to existing first node
    if (size == 0)                    // list is empty
      tail = head;                    // special case: new node becomes tail also
    size++;
  }

  /**
   * Adds an element to the end of the list.
   * @param e  the new element to add
   */
  public void addLast(E e) {                 // adds element e to the end of the list
    Node<E> newest = new Node<>(e, null);    // new node will eventually be the tail
    if (isEmpty())
      head = newest;                         // special case: previously empty list
    else
      tail.setNext(newest);                  // new node after existing tail
    tail = newest;                           // new node becomes the tail
    size++;
  }

  /**
   * Removes and returns the first element of the list.
   * @return the removed element (or null if empty)
   */
  public E removeFirst() {                   // removes and returns the first element
    if (isEmpty()) return null;              // nothing to remove
    E answer = head.getElement();
    head = head.getNext();                   // will become null if list had only one node
    size--;
    if (size == 0)
      tail = null;                           // special case as list is now empty
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

    if (node1.getNext() == node2) {
      swapAdjacent(previous1, node1, node2);
    } else if (node2.getNext() == node1) {
      swapAdjacent(previous2, node2, node1);
    } else {
      swapSeparated(previous1, node1, previous2, node2);
    }

    if (node1.getNext() == null) {
      tail = node1;
    } else if (node2.getNext() == null) {
      tail = node2;
    }
  }

  private Node<E> findPrevious(Node<E> target) {
    if (target == head) {
      return null;
    }

    Node<E> current = head;
    while (current != null && current.getNext() != target) {
      current = current.getNext();
    }
    return current;
  }

  private void swapAdjacent(Node<E> beforeFirst, Node<E> firstNode, Node<E> secondNode) {
    if (beforeFirst == null) {
      head = secondNode;
    } else {
      beforeFirst.setNext(secondNode);
    }

    firstNode.setNext(secondNode.getNext());
    secondNode.setNext(firstNode);
  }

  private void swapSeparated(Node<E> previous1, Node<E> node1, Node<E> previous2, Node<E> node2) {
    if (previous1 == null) {
      head = node2;
    } else {
      previous1.setNext(node2);
    }

    if (previous2 == null) {
      head = node1;
    } else {
      previous2.setNext(node1);
    }

    Node<E> tempNext = node1.getNext();
    node1.setNext(node2.getNext());
    node2.setNext(tempNext);
  }

  public Node<E> locateNodeById(int id) {
    Node<E> walk = head;
    while (walk != null) {
      if (walk.getElement() instanceof Identifiable item && item.getId() == id) {
        return walk;
      }
      walk = walk.getNext();
    }
    return null;
  }

  public List<Integer> idSequence() {
    List<Integer> ids = new ArrayList<>();
    Node<E> walk = head;
    while (walk != null) {
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

 
  public boolean equals(Object o) {
    if (o == null) return false;
    if (getClass() != o.getClass()) return false;
    SinglyLinkedList other = (SinglyLinkedList) o;   // use nonparameterized type
    if (size != other.size) return false;
    Node walkA = head;                               // traverse the primary list
    Node walkB = other.head;                         // traverse the secondary list
    while (walkA != null) {
      if (!walkA.getElement().equals(walkB.getElement())) return false; //mismatch
      walkA = walkA.getNext();
      walkB = walkB.getNext();
    }
    return true;   // if we reach this, everything matched successfully
  }

  @SuppressWarnings({"unchecked"})
  public SinglyLinkedList<E> clone() throws CloneNotSupportedException {
    // always use inherited Object.clone() to create the initial copy
    SinglyLinkedList<E> other = (SinglyLinkedList<E>) super.clone(); // safe cast
    if (size > 0) {                    // we need independent chain of nodes
      other.head = new Node<>(head.getElement(), null);
      Node<E> walk = head.getNext();      // walk through remainder of original list
      Node<E> otherTail = other.head;     // remember most recently created node
      while (walk != null) {              // make a new node storing same element
        Node<E> newest = new Node<>(walk.getElement(), null);
        otherTail.setNext(newest);     // link previous node to this one
        otherTail = newest;
        walk = walk.getNext();
      }
    }
    return other;
  }

  public int hashCode() {
    int h = 0;
    for (Node walk=head; walk != null; walk = walk.getNext()) {
      h ^= walk.getElement().hashCode();      // bitwise exclusive-or with element's code
      h = (h << 5) | (h >>> 27);              // 5-bit cyclic shift of composite code
    }
    return h;
  }

  /**
   * Produces a string representation of the contents of the list.
   * This exists for debugging purposes only.
   */
  public String toString() {
    StringBuilder sb = new StringBuilder("(");
    Node<E> walk = head;
    while (walk != null) {
      sb.append(walk.getElement());
      if (walk != tail)
        sb.append(", ");
      walk = walk.getNext();
    }
    sb.append(")");
    return sb.toString();
  }

  void main() throws IOException {
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
