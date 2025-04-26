package sidstack;

class Stack {
    private /*@ spec_public @*/ int[] elements;
    private /*@ spec_public @*/ int size;
  
    //@ public invariant elements != null;
    //@ public invariant size >= 0;
    //@ public invariant size <= elements.length;
  
    /*@ requires capacity > 0;
      @ ensures elements.length == capacity;
      @ ensures size == 0;
      @*/
    public Stack(int capacity) {
        elements = new int[capacity];
        size = 0;
    }
  
    /*@ requires size < elements.length;
    @ ensures size == \old(size) + 1;
    @ ensures elements[size-1] == element;
    @ assignable elements[size], size;
    @*/
    public void push(int element) {
      elements[size] = element; // Add element to the array
       // Increment size
    }
  
    /*@ requires size > 0;
    
      @ ensures size == \old(size) - 1;
      @ ensures \result == \old(elements[size-1]);
      @ assignable size;
      @*/
    public int pop() {
        return elements[--size];
    }
  
    public static void main(String[] args) {
        // System.out.println("Starting Stack Contract Demonstration\n");
  
        // Test 1: Invalid construction
        // System.out.println("Test 1: Attempting to create stack with zero capacity...");
        new Stack(0); // Will fail with an assertion error
        
  
        // Test 2: Valid operations
        // System.out.println("Test 2: Performing valid operations...");
        Stack stack = new Stack(2);
        // System.out.println("Created stack with capacity 2");
  
        stack.push(10);
        // System.out.println("Pushed 10");
  
        stack.push(20);
        // System.out.println("Pushed 20");
  
        // System.out.println("Popped: " + stack.pop());
        // System.out.println("Popped: " + stack.pop());
        // System.out.println("Valid operations completed successfully\n");
  
        // Test 3: Stack overflow
        // System.out.println("Test 3: Attempting to overflow stack...");
        stack = new Stack(1);
        stack.push(10);
        stack.push(20); // Will fail with an assertion error
  
        // Test 4: Stack underflow
        // System.out.println("Test 4: Attempting to pop from empty stack...");
        stack = new Stack(1);
        stack.pop(); // Will fail with an assertion error
    }
  }
  