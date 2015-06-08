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

#ifndef	_synch_h_
#define	_synch_h_

#include "queue.h"

typedef struct Sem {
    Queue	waiting;
    int		count;
} Sem;

extern void	SemInit (Sem *, int);
extern void	SemWait (Sem *);
extern void	SemSignal (Sem *);

typedef struct Lock {
  // Your stuff goes here...
	Sem l_mutex;
	Sem l_next;
	int nextCount;
} Lock;

typedef struct Cond {
  // Your stuff goes here...
	Lock *c_lock;
	Sem c_sem;
	int semCount;
} Cond;

extern void	LockInit (Lock *);
extern void	LockAcquire (Lock *);
extern void	LockRelease (Lock *);
extern void	CondInit (Cond *, Lock *);
extern void	CondWait (Cond *);
extern void	CondSignal (Cond *);
extern void	CondBroadcast (Cond *);

#endif	//_synch_h_


