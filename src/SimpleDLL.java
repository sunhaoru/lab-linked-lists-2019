import java.util.ConcurrentModificationException;
import java.util.Iterator;
import java.util.ListIterator;
import java.util.NoSuchElementException;

/**
 * Simple doubly-linked lists.
 *
 * These do *not* (yet) support the Fail Fast policy.
 */
public class SimpleDLL<T> implements SimpleList<T> {
  // +--------+------------------------------------------------------------
  // | Fields |
  // +--------+

  /**
   * The front of the list
   */
  Node2<T> front;

  /**
   * The number of values in the list.
   */
  int size;

  // +--------------+------------------------------------------------------
  // | Constructors |
  // +--------------+

  /**
   * Create an empty list.
   */
  public SimpleDLL() {
    this.front = null;
    this.size = 0;
  } // SimpleDLL

  // +-----------+---------------------------------------------------------
  // | Iterators |
  // +-----------+

  public Iterator<T> iterator() {
    return listIterator();
  } // iterator()

  public ListIterator<T> listIterator() {
    return new ListIterator<T>() {
      // +--------+--------------------------------------------------------
      // | Fields |
      // +--------+

      /**
       * The position in the list of the next value to be returned.
       * Included because ListIterators must provide nextIndex and
       * prevIndex.
       */
      int pos = 0;

      /**
       * A cursor between neighboring values.
       */
      Node2<T> cursor = new Node2<T>(null, null, SimpleDLL.this.front);

      /**
       * The node to be updated by remove or set.  Has a value of
       * null when there is no such value.
       */
      Node2<T> update = null;

      // +---------+-------------------------------------------------------
      // | Methods |
      // +---------+

      public void add(T val) throws UnsupportedOperationException {
        // Special case: The list is empty)
        if (SimpleDLL.this.front == null) {
          SimpleDLL.this.front = new Node2<T>(val);
          this.cursor.prev = SimpleDLL.this.front;
        } // empty list
        // Special case: At the front of a list
        else if (cursor.prev == null) {
          this.cursor.prev = this.cursor.next.insertBefore(val);
          SimpleDLL.this.front = this.cursor.prev;
        } // front of list
        // Normal case
        else {
          this.cursor.prev = this.cursor.prev.insertAfter(val);
        } // normal case

        // Note that we cannot update
        this.update = null;

        // Increase the size
        ++SimpleDLL.this.size;

        // Update the position.  (See SimpleArrayList.java for more of
        // an explanation.)
        ++this.pos;
      } // add(T)

      public boolean hasNext() {
        return (this.pos < SimpleDLL.this.size);
      } // hasNext()

      public boolean hasPrevious() {
        return (this.pos > 0);
      } // hasPrevious()

      public T next() {
        if (!this.hasNext()) {
         throw new NoSuchElementException();
        } // if
        // Identify the node to update
        this.update = this.cursor.next;
        // Advance the cursor
        this.cursor.prev = this.cursor.next;
        this.cursor.next = this.cursor.next.next;
        // Note the movement
        ++this.pos;
        // And return the value
        return this.update.value;
      } // next()

      public int nextIndex() {
        return this.pos;
      } // nextIndex()

      public int previousIndex() {
        return this.pos - 1;
      } // prevIndex

      public T previous() throws NoSuchElementException {
        if (!this.hasPrevious()) {
          throw new NoSuchElementException();
        }//if
        this.update = this.cursor.prev;
        
        //Update cursor
        this.cursor.prev = this.update.prev;
        this.cursor.next = this.update;
        
        this.pos--;
        return this.update.value;
      } // previous()

      public void remove() {
        // Sanity check
        if (this.update == null) {
          throw new IllegalStateException();
        } // if

        // Update the cursor
        if (this.cursor.next == this.update) {
          this.cursor.next = this.update.next;
        } // if
        if (this.cursor.prev == this.update) {
          this.cursor.prev = this.update.prev;
        } // if

        // Update the front
        if (SimpleDLL.this.front == this.update) {
          SimpleDLL.this.front = this.update.next;
        } // if

        // Do the real work
        this.update.remove();
        --SimpleDLL.this.size;

        // Note that no more updates are possible
        this.update = null;
      } // remove()

      public void set(T val) {
        // Sanity check
        if (this.update == null) {
          throw new IllegalStateException();
        } // if
        // Do the real work
        this.update.value = val;
        // Note that no more updates are possible
        this.update = null;
      } // set(T)
    };
  } // listIterator()

} // class SimpleDLL<T>
