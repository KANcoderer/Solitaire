package edu.psu.solitaire;

public class Node{
    private int order;
    private int rank;
    private String suit;
    private int id;
    private int image;
    private Node prev;
    private Node next;
    private boolean faceUp;

    public DoublyLinkedList getCol() {
        return col;
    }

    public void setCol(DoublyLinkedList col) {
        this.col = col;
    }

    DoublyLinkedList col;

    public int getImage() {
        return image;
    }

    public void setImage(int image) {
        this.image = image;
    }

    public int getId() {
        return id;
    }

    public int getRank() {
        return rank;
    }

    public void setRank(int rank) {
        this.rank = rank;
    }

    public Node getPrev() {
        return prev;
    }

    public Node getNext() {
        return next;
    }

    public void setNext(Node next) {
        this.next = next;
    }

    public void setPrev(Node prev) {
        this.prev = prev;
    }

    public String getSuit() {
        return suit;
    }

    public void setSuit(String suit) {
        this.suit = suit;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getOrder() {
        return order;
    }

    public void setOrder(int order) {
        this.order = order;
    }

    public Node(int id, String suit, int rank, int image){
        this.id=id;
        this.suit=suit;
        this.rank=rank;
        this.image=image;
        faceUp=false;
    }
    public Node(int id, String suit, int rank, int image,boolean faceUp){
        this.id=id;
        this.suit=suit;
        this.rank=rank;
        this.image=image;
        this.faceUp=faceUp;
    }

    public boolean isFaceUp() {
        return faceUp;
    }

    public void setFaceUp(boolean faceUp) {
        this.faceUp = faceUp;
    }

}
