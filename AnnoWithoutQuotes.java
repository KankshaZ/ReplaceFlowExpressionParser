import org.checkerframework.checker.lock.qual.*;

import java.util.concurrent.locks.ReentrantLock;

class AnnoWithoutQuotes {
  final Object lockA = new Object();
  final Object lockB = new Object();
  @GuardedBy(lockA) Object x = new Object();
  @GuardedBy({lockA, lockB}) Object y = new Object();
  @Holding(lockB)
  void myMethod() {
      synchronized(lockA) {
        x.toString();  // dereferences y's value without holding lock lockB
      }
  }

  @EnsuresLockHeld(this)
  public static void lock(){
    
  }
}