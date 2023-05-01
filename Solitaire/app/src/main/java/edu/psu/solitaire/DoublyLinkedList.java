package edu.psu.solitaire;
import java.util.Random;
public class DoublyLinkedList {
    private int id;

    public String getName() {
        return name;
    }

    private final String name;
    private Node head = null;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }
    private int size;
    private Node tail = null;

    public int getSize() {
        return size;
    }

    public void setSize(int size) {
        this.size = size;
    }

    public DoublyLinkedList(int id,String name) {
        this.id = id;
        this.name=name;
        size=0;
    }

    public Node getHead() {
        return head;
    }

    public Node getTail() {
        return tail;
    }

    public void setTail(Node tail) {
        this.tail = tail;
    }

    public void setHead(Node head) {
        this.head = head;
    }




    public void insert(Node newNode) {
        size++;
        newNode.setOrder(size);
        newNode.setCol(this);
        if(head == null) {
            head = newNode;
            tail = newNode;
            head.setPrev(null);
            tail.setNext(null);
        }else {
            tail.setNext(newNode);
            newNode.setPrev(tail);
            tail = newNode;
            tail.setNext(null);
        }

        CardDatabase.getCard(newNode.getSuit(), newNode.getRank(), card -> {
            card.col = newNode.getCol().name;
            card.order = newNode.getOrder();
            card.faceUp= newNode.isFaceUp();

            CardDatabase.update(card);
        });

    }
    public Node remove(Node node){
        size--;
        if (head == node) {
            head=node.getNext();
            if(head!=null) {
                head.setPrev(null);
            }
            node.setPrev(null);
            node.setNext(null);
        }else if (tail==node){
            tail=node.getPrev();
            if(tail!=null) {
                tail.setNext(null);
            }
            node.setNext(null);
            node.setPrev(null);
        }else{
            node.getPrev().setNext(node.getNext());
            node.getNext().setPrev(node.getPrev());
            node.setPrev(null);
            node.setNext(null);
        }
        return node;
    }
    public void shuffle(){
        Random rand=new Random();
        int s=size;
        for(int i=52; i>0;i--){
            Node temp =head;
            int idx = rand.nextInt(i);
            int start=0;
            while(start!=idx){
                temp=temp.getNext();
                start++;
            }
            temp=remove(temp);
            tail.setNext(temp);
            temp.setPrev(tail);
            tail = temp;
        }
        size=s;
    }
    public void output(){
        Node temp = head;
        while(temp!= null){
            System.out.println(temp.getRank()+" "+temp.getSuit());
            temp=temp.getNext();
        }
    }
}
