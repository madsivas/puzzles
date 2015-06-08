//
//	barber.h
//
//	Include file for sleeping barber problem. Uses Locks and Cond
//	primitives to achieve synchronization.
//

#ifndef	_barber_h_
#define	_barber_h_

static int SEED = 7965241; // for random number gen

//#define MAX_CUSTS 100
#define MAX_CUSTS 10
#define NUM_CHAIRS 8

// ascii values for better printfs
const int EXITING = 'X';
const int WAITING = 'W';
const int BEING_SERVED = 'S';

const int BARBER_ID = 0;
const int BARBER_PREFIX = 'B';
const int CUST_PREFIX = 'C';

// variables that will only be updated within the "monitor"
int state[MAX_CUSTS];
struct Cond *newCust;
struct Cond *nextCust;

struct Lock MonitorLock;
struct Lock *sb_lock;

extern void sleepingBarber(int);
extern void barberTest();
extern void cutHair();
extern void customer(int);
extern void doNonCritical();

//extern void cutHair(int);
//extern void doNonCritical(int, char *);

extern void barberLog(char, int, char *str);

#endif	//_barber_h_
