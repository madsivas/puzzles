//
//	SleepingBarber
//
//	A function to simulate the sleeping barber and his customers-
//	how customers Enter his shop, wait, get hair cut and leave.
//
//  The barbershop consists of a waiting room with 'n' chairs, and the
//  barber room with a barber chair. If there are no customers, the 
//  barber goes to sleep. If a customer enters the shop and all the n
//  chairs in the waiting room are occupied, he leaves the shop. If the
//  barber is busy but the waiting room has chairs available, he sits 
//  and waits. If the barber is asleep, the customer wakes up the barber.
//
//  This program uses Locks, Condition variables (and Semaphores) to
//  co-ordinate the activities of the barber and the customers.
//

#include "dlxos.h"
#include "synch.h"
#include "barber.h"

void 
sleepingBarber(int numChairs) 
{
	struct Cond BarbCond;
	struct Cond CustCond;

	newCust = &BarbCond;
	CondInit(newCust, sb_lock);
	nextCust = &CustCond;
	CondInit(nextCust, sb_lock);

	while (1) {
		barberLog(BARBER_PREFIX, BARBER_ID, "Waiting for customers...");
		barberTest(BARBER_ID);
		barberLog(BARBER_PREFIX, BARBER_ID, "Before cutting hair");
		cutHair();
		barberLog(BARBER_PREFIX, BARBER_ID, "After cutting hair");
	} // end while
} // end sleepingBarber

void 
barberTest(int custNum)
{
	LockAcquire(sb_lock); // lock to enter the "monitor"
	if (custNum == BARBER_ID) {
		if (nextCust->semCount == 0) { // means waiting room is empty
			CondWait(newCust);
		}
	} else { // custNum is not BARBER_ID
		if (state[custNum] == WAITING) { // if a customer is waiting
			// go cut hair...
			state[custNum] = BEING_SERVED;
			CondSignal(nextCust);
		}
	}

	LockRelease(sb_lock); // release the lock before cutting hair
} // end barber

void 
customer(int custNum)
{
	// to avoid 2 custs entering waiting room at same time,
	// acquire sb_lock first
	LockAcquire(sb_lock);
	if (nextCust->semCount == NUM_CHAIRS) { // means waiting room is full
		state[custNum] = EXITING;
		LockRelease(sb_lock);
		barberLog(CUST_PREFIX, custNum, "Waiting room full, customer leaves");
		return; // goes away if waiting room is full
	} else if (nextCust->semCount == 0) {
		barberLog(CUST_PREFIX, custNum, "Waiting room empty, signalling barber on newCust");
		CondSignal(newCust); // barber may be sleeping, send signal to wake him
	} else { // wait in waiting room
		state[custNum] = WAITING;
		barberLog(CUST_PREFIX, custNum, "Waiting for barber on nextCust");
		CondWait(nextCust);
	}
	LockRelease(sb_lock);
	return;
} // end customer()

void 
cutHair() 
{
	//doNonCritical(custNum, "Getting hair cut");
	doNonCritical();
}

//----------------------------------------------------------------------
//
//	doNonCritical
//
//	This is an empty loop symbolising whatever non-critical stuff is
//	done by the barber to each customer.
//
//----------------------------------------------------------------------
void 
doNonCritical ()
{
  long		i,rn;
  extern int	random(),srandom();

  srandom (SEED);

	//barberLog(CUST_PREFIX, custNum, str);

  rn = random();
  
  // reduce random number, so loop does not take forever to execute
  rn = rn % 100000; 

  while (i < rn) {
    i += 1;
  }
}

void
barberLog(char c, int n, char *str)
{
  int i;
//goto x;
  printf("%c%d:: ", c, n);
  printf("%c ",state[n]);
  printf(": %s    \n", str);
  return;
x:
  dbprintf('0',"%d:: ",n);
  printf("%c ",state[n]);
  dbprintf('0',": %s    \n", str);
}
