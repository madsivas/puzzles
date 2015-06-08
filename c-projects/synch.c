//
//	synch.c
//
//	Routines for synchronization
//
//

#include "dlxos.h"
#include "process.h"
#include "synch.h"

//----------------------------------------------------------------------
//
//	SemInit
//
//	Initialize a semaphore to a particular value.  This just means
//	initting the process queue and setting the counter.
//
//----------------------------------------------------------------------
void
SemInit (Sem *sem, int count)
{
    QueueInit (&sem->waiting);
    sem->count = count;
}

//----------------------------------------------------------------------
//
//	SemWait
//
//	Wait on a semaphore.  As described in Section 6.4 of _OSC_,
//	we decrement the counter and suspend the process if the
//	semaphore's value is less than 0.  To ensure atomicity,
//	interrupts are disabled for the entire operation.  Note that,
//	if the process is put to sleep, interrupts will be OFF when
//	it returns from sleep.  Thus, we enable interrupts at the end of
//	the routine.
//
//----------------------------------------------------------------------
void
SemWait (Sem *sem)
{
    Link	*l;

    DisableIntrs ();
    dbprintf ('s', "Proc 0x%x waiting on sem 0x%x, count=%d.\n", currentPCB,
	      sem, sem->count);
    sem->count -= 1;
    if (sem->count < 0) {
	l = QueueAllocLink ();
	QueueLinkInit (l, (void *)currentPCB);
	dbprintf ('s', "Suspending current proc (0x%x).\n", currentPCB);
	QueueInsertLast (&sem->waiting, l);
	ProcessSleep ();
    }
    EnableIntrs ();
}

//----------------------------------------------------------------------
//
//	SemSignal
//
//	Signal on a semaphore.  Again, details are in Section 6.4 of
//	_OSC_.
//
//----------------------------------------------------------------------
void
SemSignal (Sem *sem)
{
    Link	*l;

    DisableIntrs ();
    dbprintf ('s', "Signalling on sem 0x%x, count=%d.\n", sem, sem->count);
    sem->count += 1;
    if (sem->count <= 0) {
			l = QueueFirst (&sem->waiting);
			QueueRemove (l);
			dbprintf ('s', "Waking up PCB 0x%x.\n", l->object);
			ProcessWakeup ((PCB *)(l->object));
			QueueFreeLink (l);
    }
    EnableIntrs ();
}

//----------------------------------------------------------------------
//
//	Your assignment is to implement locks and condition variables
//	using Mesa-style monitor synchronization.
//
//----------------------------------------------------------------------
void
LockInit (Lock *lock)
{
	SemInit(&lock->l_mutex, 1); // initialize the mutex semaphore in the lock to 1
	SemInit(&lock->l_next, 1); // init the next sem in lock to 1
	lock->nextCount = 0;
}

void
LockAcquire (Lock *lock)
{
	SemWait(&lock->l_mutex);
}

/* When a process ready to release the lock, it checks to see if there are
 * any processes waiting on next (i.e., if nextCount is > 0. If so, then
 * signal to the next process, else signal on mutex, to let any process
 * that is waiting outside the monitor on mutex, to come in. */
void	LockRelease (Lock *lock)
{
	if (lock->nextCount > 0) {
		SemSignal(&lock->l_next);
	} else {
		SemSignal(&lock->l_mutex);
	}
} // end LockRelease()

void	CondInit (Cond *cond, Lock *lock)
{
	LockInit(lock);
	cond->c_lock = lock;
	SemInit(&cond->c_sem, 0);
	cond->semCount = 0;
} // end CondInit()

void	CondWait (Cond *cond)
{
	cond->semCount += 1;
	LockRelease(cond->c_lock);
	SemWait(&cond->c_sem);
	cond->semCount -= 1;

	cond->c_lock->nextCount += 1;
	SemWait(&cond->c_lock->l_next);
	cond->c_lock->nextCount -= 1;
} // end CondWait()

void	CondSignal (Cond *cond)
{
	if (cond->semCount > 0) {
		SemSignal(&cond->c_sem);
	}
} // end CondSignal()

// Where CondSignal restarts one process waiting on the condition
// variable, CondBroadcast restarts ALL processes waiting on this
// condition variable.
void	CondBroadcast (Cond *cond)
{
	int i;
	for (i = 0; i < cond->semCount; i++) {
		SemSignal(&cond->c_sem);
	}
} // end CondBroadCast()

