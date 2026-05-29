# Theme: Course Assignments

# COMP-254 Lab 1

Name: Ildaphonse Cornolius  
Student Number: 300699371

## Exercise 1 - Singly Linked List Node Swap

Swapping linked-list nodes is more complex than swapping element values because the links around each node must be changed without losing access to the rest of the list. The method must update the previous nodes, the `next` references, and sometimes the `head` or `tail`. One edge case that required extra care was swapping adjacent nodes, because the first node points directly to the second node before the swap.

Checkpoint produced: `checkpoint1.txt`

## Exercise 2 - Doubly Linked List Concatenation

Exercise 2 reuses the output from Exercise 1 by reading `checkpoint1.txt` and rebuilding list `L1` from the saved ID sequence. A second list `L2` is created with the same IDs and transformed priorities, then `L2` is appended to `L1`.

Checkpoint produced: `checkpoint2.txt`

## Exercise 3 - Circular Linked List Rotation Check

Circular comparison must consider different starting points because a circular list does not have one permanent logical beginning. Two circular lists can contain the same sequence even when one list's current first node is a rotation of the other list's current first node.

Exercise 3 reads `checkpoint2.txt`, builds circular list `C1`, creates a rotated `C2`, and checks that the two lists represent the same sequence. It then changes one item in `C2` and verifies that the comparison returns `false`.

## Checkpoint Continuation Rule

No manual checkpoint files were used. Both checkpoint files were produced by running the program.

## AI Disclosure

Tool used: Cursor AI assistant  
Purpose: Help create the Java project files, linked-list methods, test code, checkpoint writing/reading, and README responses.  
What I kept or changed: I kept the generated structure and reviewed the linked-list pointer logic, package names, checkpoint flow, and assignment explanations.  
What I personally tested or verified: I compiled and ran the Java programs for all three exercises, checked that `checkpoint1.txt` and `checkpoint2.txt` were produced by the programs, and verified that the circular-list comparison returns `true` before modification and `false` after modification.

## Video Walkthrough

Video walkthrough file: add the recorded 3-4 minute walkthrough file to the project root before creating the ZIP submission.
