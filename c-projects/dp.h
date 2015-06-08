//
//	synch.h
//
//	Include file for synchronization stuff.  The synchronization
//	primitives include:
//	Semaphore
//	Lock
//	Condition
//
//	Semaphores are the only "native" synchronization primitive.
//	Condition variables and locks are implemented using semaphores.
//

#ifndef	_dp_h_
#define	_dp_h_

static int SEED = 2785646; // for random number gen

// ascii values for better printfs
const int THINKING = 'T';
const int HUNGRY = 'H';
const int EATING = 'E';

#define NUM_PHILS 5

// variables that will only be updated within the "monitor"
int state[NUM_PHILS];
struct Cond *dp_cond[NUM_PHILS];

struct Lock MonitorLock;
struct Lock *dp_lock;

extern void diningPhilosopher(int i);
extern void pickup(int);
extern void putdown(int);
extern void test(int);
extern void eat(int);
extern void think(int);
extern void doNonCritical(int, char *);

extern void dplog(int philNum, char *str);


//broadcast
void btread(int i);
void btwrite();
struct Cond *btcond;
struct Cond BTCond;
#endif	//_dp_h_
