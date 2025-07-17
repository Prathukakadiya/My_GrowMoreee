package DS;

import Model.Transaction;

public class CustomLinkedList {
    private static class Node {
        Transaction data;
        Node next;
        Node(Transaction data) {
            this.data = data;
        }
    }

    private Node head;

    public void add(Transaction data) {
        Node node = new Node(data);
        if (head == null) head = node;
        else {
            Node temp = head;
            while (temp.next != null) temp = temp.next;
            temp.next = node;
        }
    }

    public void display() {
        Node temp = head;
        while (temp != null) {
            System.out.println(temp.data);
            temp = temp.next;
        }
    }
}