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
    private E element;

    /** A reference to the subsequent node in the list */
    private Node<E> next;

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

    public E getElement() { return element; }

    public Node<E> getNext() { return next; }

    public void setNext(Node<E> n) { next = n; }
  } //----------- end of nested Node class -----------

  private Node<E> head = null;
  private Node<E> tail = null;
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

    @Override
    public String toString() {
      return "WorkItem{id=" + id + ", title='" + title + "', priority=" + priority + "}";
    }
  }

  public SinglyLinkedList() { }

  public int size() { return size; }

  public boolean isEmpty() { return size == 0; }

  public E first() {
    if (isEmpty()) return null;
    return head.getElement();
  }

  public E last() {
    if (isEmpty()) return null;
    return tail.getElement();
  }

  public void addFirst(E e) {
    head = new Node<>(e, head);
    if (size == 0)
      tail = head;
    size++;
  }

  public void addLast(E e) {
    Node<E> newest = new Node<>(e, null);
    if (isEmpty())
      head = newest;
    else
      tail.setNext(newest);
    tail = newest;
    size++;
  }

  public E removeFirst() {
    if (isEmpty()) return null;
    E answer = head.getElement();
    head = head.getNext();
    size--;
    if (size == 0)
      tail = null;
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

  @SuppressWarnings({"unchecked"})
  public boolean equals(Object o) {
    if (o == null) return false;
    if (getClass() != o.getClass()) return false;
    SinglyLinkedList other = (SinglyLinkedList) o;
    if (size != other.size) return false;
    Node walkA = head;
    Node walkB = other.head;
    while (walkA != null) {
      if (!walkA.getElement().equals(walkB.getElement())) return false;
      walkA = walkA.getNext();
      walkB = walkB.getNext();
    }
    return true;
  }

  @SuppressWarnings({"unchecked"})
  public SinglyLinkedList<E> clone() throws CloneNotSupportedException {
    SinglyLinkedList<E> other = (SinglyLinkedList<E>) super.clone();
    if (size > 0) {
      other.head = new Node<>(head.getElement(), null);
      Node<E> walk = head.getNext();
      Node<E> otherTail = other.head;
      while (walk != null) {
        Node<E> newest = new Node<>(walk.getElement(), null);
        otherTail.setNext(newest);
        otherTail = newest;
        walk = walk.getNext();
      }
    }
    return other;
  }

  public int hashCode() {
    int h = 0;
    for (Node walk = head; walk != null; walk = walk.getNext()) {
      h ^= walk.getElement().hashCode();
      h = (h << 5) | (h >>> 27);
    }
    return h;
  }

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
