package com.example.model;

public class TupleThree<A, B, C>  {
	public final A Item1;

    public final B Item2;
    
    public final C Item3;

    public TupleThree(A a, B b, C c){
    	Item1 = a;
    	Item2 = b;
    	Item3 = c;
    }

    public String toString(){
        return "(" + Item1 + ", " + Item2 + ", " + Item3 + ")";
    }
}
