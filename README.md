Theme: Game Programming

COMP-254 Assignment 1

Name: Ildaphonse Cornolius  
Student Number: 300699371

Exercise 1:  Singly Linked List Node Swap

Explain why swapping nodes is more complex than swapping element values, and identify one
edge case that required extra care.


With elements, you are changing the value, with nodes you are changing which nodes point to which between two different linked lists. If you update these node links in the wrong order you can lose parts of the list.

An example of an edge case would be, that if the head node of a list is involved in a switch then i have to update the head "sentinel" since it's still pointing to the old node that was in it's place.


Exercise 2: Doubly Linked List Concatenation

Explain how Exercise 1 output is reused here.

After Excercise 1 is completed it saves the data to a txt file. That data is read in Excercise 2 and it builds a list from it. That is how Excercise 2 uses what Excercise 1 produces.



Exercise 3: Circular Linked List Rotation Check

Explain why circular comparison must consider different starting points.

Circular linked lists need to consider different starting points because there is no designated starting position. Singly Linked Lists and Doubly linked lists can do so.
Using the rotate method only changes where c2 starts but the node positions are the same.   



Checkpoint Continuation Rule:

No manual checkpoint files were used.


AI Disclosure:

No AI was used for the completion of this project. 

Video Walkthrough:

Attached in the project is a video walkthrough. 
