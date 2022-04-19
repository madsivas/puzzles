class Solution(object):
        def twoSum(self, nums, target):
            """
            :type nums: List[int]
            :type target: int
            :rtype: List[int]
            """
            subnums = nums.copy()
            print(nums)
    
            subnums.pop(0) # remove first elem
            print(subnums)
                    
            for idx1, e1 in enumerate(nums):
                for idx2, e2 in enumerate(subnums):
                    print(e1, e2)
                    if e1 + e2 == target:
                        idx2 = idx2+1 # correct for subnums index being off by 1
                        print("Elements {} and {} at index {} and {} add up to target {}".format(e1, e2, idx1, idx2, target))
                        break
                    else:
                        continue
                break
                                        
sol = Solution()
arg1 = [2, 3, 9, 10]
targ = 11
sol.twoSum(arg1, targ)

