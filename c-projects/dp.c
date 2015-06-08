//
//	diningPhilosopher
//
//	A function to simulate what each dining philosopher does-
//	essentially, EAT, THINK and be HUNGRY.
//

#include "dlxos.h"
#include "synch.h"
#include "dp.h"

void 
diningPhilosopher(int philNum) 
{

	struct Cond PhilCond;
	dp_cond[philNum] = &PhilCond;
	CondInit(dp_cond[philNum], dp_lock);

	while (1) {
		dplog(philNum,"Starting To Think");
		think(philNum);
		dplog(philNum,"Before pickup");
		pickup(philNum);
		dplog(philNum,"Pickup returned - Starting To Eat");
		eat(philNum);
		dplog(philNum,"Before putdown - Finished Eating");
		putdown(philNum);
		dplog(philNum,"Putdown returned");
	} // end while
} // end diningPhilosopher

void 
pickup(int philNum) 
{
	LockAcquire(dp_lock); // lock to enter the "monitor"
	state[philNum] = HUNGRY;
	test(philNum);
	if (state[philNum] != EATING) {
		CondWait(dp_cond[philNum]);
	}
	LockRelease(dp_lock); // release the lock before eating
} // end pickup

void 
putdown(int philNum) 
{
	LockAcquire(dp_lock);
	state[philNum] = THINKING;
	// check if either side neighbours want to eat
	test((philNum+NUM_PHILS-1)%NUM_PHILS);
	test((philNum+1)%NUM_PHILS);
	LockRelease(dp_lock);
} // end putdown

void 
test(int philNum) 
{
	if (state[(philNum+NUM_PHILS-1)%NUM_PHILS] != EATING &&
		state[philNum] == HUNGRY &&
		state[(philNum+1)%NUM_PHILS] != EATING)
	{
		state[philNum] = EATING;
		CondSignal(dp_cond[philNum]);
	}
} // end test()

void 
eat(int philNum) 
{
	doNonCritical(philNum, "Eating");
}

void 
think(int philNum) 
{
	doNonCritical(philNum, "Thinking");
}

//----------------------------------------------------------------------
//
//	doNonCritical
//
//	This is an empty loop.  Its params indicate what non-critical stuff
//	is being done, and by which philosopher - for debugging, it will be
//  useful to print the stuff and philosopher#.
//
//----------------------------------------------------------------------
void 
doNonCritical (int philNum, char *str) 
{
  long		i,rn;
  extern int	random(),srandom();

  srandom (SEED);

  rn = random();
  
  // reduce random number, so loop does not take forever to execute
  rn = rn % 100000; 

  while (i < rn) {
    i += 1;
  }
}

void
dplog(int n, char *str)
{
  int i;
//goto x;
  printf("%d:: ",n);
  for(i=0;i<NUM_PHILS;i++) {printf("%c ",state[i]);}
  printf(": %s    \n", str);
  return;
x:
  dbprintf('0',"%d:: ",n);
  for(i=0;i<NUM_PHILS;i++) {dbprintf('0',"%c ",state[i]);}
  dbprintf('0',": %s    \n", str);
}


//testing broadcasts
void
btread(int i)
{
LockAcquire(dp_lock);
CondWait(btcond);
printf("btread woke up: %d\n",i);
LockRelease(dp_lock);
}

void 
btwrite()
{
int i;
for (i=0;i<40;i++) doNonCritical(0,"btwrite");
LockAcquire(dp_lock);
printf("Semcount is %d\n",btcond->semCount);
CondBroadcast(btcond);
printf("btwrite finished\n");
LockRelease(dp_lock);
}
