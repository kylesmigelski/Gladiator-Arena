/*********************************************************
 Generic singly linked queue class.

 @author Kyle Smigelski
 @version Winter 2021
 *********************************************************/

public class Queue<T> {

    /** ***********************************************************************
     * Inner Node class initializes node next with t data attached.
     */
    private class Node{
        T item;
        Node next;

        Node(T t){
            item = t;
            next = null;
        }
    }

    /** ***********************************************************************
     * Instance Variables
     */
    private Node head;                // Node head points to first object
    private Node tail;                // Node tail points to last object
    private int n;                    // Size of the queue

    /** ***********************************************************************
     * Builds the queue.
     */
    public Queue(){
        head = null;
        tail = null;
        n = 0;
    }

    /** ***********************************************************************
     * Adds object to the queue.
     * @param item object to be added
     */
    void enqueue(T item){
        Node oldTail = tail;

        // Create new node with entered data
        tail = new Node(item);

        // If queue is empty, set tail to the head
        if(isEmpty())
            head = tail;

        // Set the previous tails next node to the new node
        else
            oldTail.next = tail;
            n++;
    }

    /** ***********************************************************************
     * Removes item from the front of the queue.
     */
    public T dequeue(){
        if(isEmpty())
            return null;

        // Cast generic object T to the item at head
        T t = head.item;
        head = head.next;
        n--;
        return t;
    }

    /** ***********************************************************************
     * @return the front item without removing it.
     */
    public T peek(){
        return head.item;
    }

    /** ***********************************************************************
     * @return the size of the queue
     */
   public int size(){
        return n;
    }

    /** ***********************************************************************
     * @return true is the queue is empty
     */
    boolean isEmpty(){
        return head == null;
    }
}
