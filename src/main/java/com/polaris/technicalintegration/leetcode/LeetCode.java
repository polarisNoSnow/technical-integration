package com.polaris.technicalintegration.leetcode;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

public class LeetCode {
	private static final LeetCode LeetCode = new LeetCode();
	int val;
	LeetCode next;

	LeetCode(int x) {
		val = x;
	}
	public LeetCode() {
	}
	

	public static void main(String[] args) {
		TreeNode node1 = LeetCode.new TreeNode(3);
		TreeNode node2 = LeetCode.new TreeNode(9);
		TreeNode node3 = LeetCode.new TreeNode(20);
		TreeNode node4 = LeetCode.new TreeNode(15);
		TreeNode node5 = LeetCode.new TreeNode(7);
		node1.left = node2;
		node1.right = node3;
		node3.left = node4;
		node3.right = node5;
		int[] in = new int[] {2,1,1,3,4,5,6,5,7};
		//System.out.println(rob213(in));
		System.out.println((double) 57.96*1.05);
		Integer a = Integer.valueOf(10000), b = 10000;
	    System.out.println(a.intValue() == b);//1
	    Integer c = 100, d = 100;
	    System.out.println(c == d);//2
	    System.out.println(Arrays.toString(topKFrequent(in, 2)));  
		 
	}

	private static LeetCode getNode(long l) {
		String totalStr = new Long(l).toString();
		char[] ch = totalStr.toCharArray();
		int len = ch.length;
		LeetCode node = new LeetCode(0);
		LeetCode nextNodes = new LeetCode(0);
		for (int j = 0; j < len; j++) {
			int e1 = ch[j] - '0';
			nextNodes = new LeetCode(e1);
			nextNodes.next = node.next;
			node.next = nextNodes;
		}
		return node.next;
	}

	public static LeetCode addTwoNumbers(LeetCode l1, LeetCode l2) {
		// 个位数
		int digit = 0;
		// 进位
		int tens = 0;
		LeetCode node = new LeetCode(0);
		LeetCode curr = node;
		while (l1 != null || l2 != null) {
			int sum = tens;
			if (l1 != null) {
				sum += l1.val;
				l1 = l1.next;
			}
			if (l2 != null) {
				sum += l2.val;
				l2 = l2.next;
			}
			digit = sum > 9 ? sum - 10 : sum;
			tens = sum > 9 ? 1 : 0;
			curr.next = new LeetCode(digit);
			curr = curr.next;
		}
		if (tens > 0) {
			curr.next = new LeetCode(tens);
			curr = curr.next;
		}
		return node.next;
	}

	//3. 无重复字符的最长子串
	public static int lengthOfLongestSubstring(String s) {
		// 记录字符上一次出现的位置
		int[] last = new int[128];
		int n = s.length();
		int len = 0;
		// 窗口开始位置
		int start = 0;
		for (int i = 0; i < n; i++) {
			int index = s.charAt(i);
			// 当前字符最近出现的位置
			start = Math.max(start, last[index]);
			len = Math.max(len, i - start + 1);
			// 覆盖原位置
			last[index] = i;
		}

		return len;
	}

	public static double findMedianSortedArrays(int[] nums1, int[] nums2) {
		if (nums1 == null || nums1.length <= 0) {
			int index = nums2.length / 2;
			return nums2.length % 2 == 0 ? ((double) (nums2[index - 1] + nums2[index]) / 2) : nums2[index];
		}
		if (nums2 == null || nums2.length <= 0) {
			int index = nums1.length / 2;
			return nums1.length % 2 == 0 ? ((double) (nums1[index - 1] + nums1[index]) / 2) : nums1[index];
		}
		int totalLength = nums1.length + nums2.length;
		List<Integer> list = new ArrayList<Integer>();
		int len1 = 0, len2 = 0;
		boolean fag1 = true, fag2 = true;
		for (int i = 0; i < totalLength; i++) {
			if (!fag2 || (fag1 && nums1[len1] < nums2[len2])) {
				list.add(nums1[len1]);
				len1++;
			} else if ((fag2 && fag1) && nums1[len1] == nums2[len2]) {
				list.add(nums1[len1]);
				len1++;
				list.add(nums2[len2]);
				len2++;
			} else {
				list.add(nums2[len2]);
				len2++;
			}
			int len = len1 + len2;
			if (len > totalLength / 2) {
				return totalLength % 2 == 0 ? ((double) (list.get(totalLength / 2 - 1) + list.get(totalLength / 2))) / 2
						: list.get(totalLength / 2);
			}
			if (len1 == nums1.length) {
				fag1 = false;
			}
			if (len2 == nums2.length) {
				fag2 = false;
			}
		}
		return 0;
	}

	// 392.
	public static boolean isSubsequence(String s, String t) {
		long sonLen = s.length();
		int index = 0;
		for (int i = 0; i < sonLen; i++) {
			String currStr = String.valueOf(s.charAt(i));
			index = t.indexOf(currStr, index);
			if (index < 0) {
				return false;
			}
			index++;
		}
		return true;
	}

	/**
	 * 5.查找最长的回文子串 解决方案 a.遍历字符串找出不重复的字符，然后每个字符前后查找，比较最大长度 b.从头遍历字符串，头部字符从尾部匹配，
	 * 当最大长度大于余下字符串长度 终止程序
	 * 
	 * @param s
	 * @return
	 */
	public static String longestPalindrome(String s) {
		if (s == null || s == "" || s.length() <= 0) {
			return "";
		}
		int maxLen = 0;
		String maxStr = String.valueOf(s.charAt(0));
		for (int i = 0; i < s.length(); i++) {
			char ch = s.charAt(i);
			int endIndex = s.lastIndexOf(ch);
			// 从尾部匹配
			while (endIndex > i) {
				int len = endIndex - i + 1;
				if (len <= maxLen) {
					break;
				}
				int start = i;
				int end = endIndex;
				// 判断是否回文
				boolean flag = false;
				while (start < end) {
					end--;
					start++;
					if (s.charAt(start) != s.charAt(end)) {
						break;
					}
					if (end - start <= 2) {
						flag = true;
					}
				}
				if (flag && (len > maxLen)) {
					maxLen = len;
					maxStr = s.substring(i, endIndex + 1);
					break;
				}
				endIndex = s.lastIndexOf(ch, endIndex - 1);
			}

			if (maxLen > s.length() - i) {
				return maxStr;
			}
		}
		return maxStr;
	}

	/**
	 * 公共树节点对象
	 * @author polaris
	 * @date 2020年8月6日
	 */
	public class TreeNode {
		int val;
		TreeNode left;
		TreeNode right;

		TreeNode(int x) {
			val = x;
		}
	}
	//104.给定一个二叉树，找出其最大深度。
	public static int maxDepth(TreeNode root) {
		int val = depth(root,0);
		return val;
	}

	private static int depth(TreeNode root, Integer i) {
		if(root == null) {
			return i;
		}
		i++;
		Integer dep = i;
		if(root.left!=null) {
			int left = depth(root.left, dep);
			if(left > i) i = left;
		}
		
		if(root.right!=null) {
			int right = depth(root.right, dep);
			if(right > i) i = right;
		}
		return i;
	}
	
	/**
	 * 6.Z字形变换
	 * 解决方案
	 * 1.按照排列顺序，存入二维数组，然后按照数组顺序读取
	 * 2.根据排列规律，单列间隔：（n-1 + n-2 + 1 ），斜线与第一行间隔：x + (n-x)*2
	 * @param s
	 * @param n 总行数
	 * @param numRows
	 * @return
	 */
    public static String convert(String s, int numRows) {
    	if(numRows <= 1 || s == "") {
    		return s;
    	}
    	//中间间隔
    	int mid = 2 * numRows - 2;
    	StringBuffer buf = new StringBuffer();
    	for (int i = 0; i < numRows; i++) {
    		int j = i;
    		while (j < s.length()) {
    			buf.append(s.charAt(j));
     			if(i > 0 && i < numRows-1) {
     				int m = j+(numRows-1-i)*2;
    				if(m < s.length()) {
    					buf.append(s.charAt(m));
    				}
        		}
    			j += mid;
			}
    	}
		return buf.toString();
    }
    
    //7. 给出一个 32 位的有符号整数，你需要将这个整数中每位上的数字进行反转
    public static int reverse(int x) {
    	int num = Math.abs(x);
    	String strRev = new StringBuffer(num+"").reverse().toString();
    	try{
    		num = Integer.valueOf(strRev);
    	}catch(NumberFormatException e) {
    		return 0;
    	}
    	if(x < 0) {
    		return -num;
    	}
		return num;
    }
    
    //8. 字符串转换整数 (atoi)
    public static int myAtoi(String str) {
    	if(str == null) return 0;
    	str = str.trim();
    	if( str == "" || str.length() <= 0) return 0;
    	char ch0 = str.charAt(0);
    	if(!(ch0 >= '0' && ch0 <= '9') && ch0 != '+' && ch0 != '-') return 0;
    	StringBuffer buf = new StringBuffer();
    	for (int i = 0; i < str.length(); i++) {
			if(i==0) {
				ch0 = str.charAt(0);
				if((ch0 >= '0' && ch0 <= '9') || ch0 == '+' || ch0 == '-') buf.append(ch0);
				else break;
			}
			else if(str.charAt(i)>='0' && str.charAt(i)<='9')  buf.append(str.charAt(i));
			else break;
		}
    	try {
    		new BigDecimal(buf.toString());	
    	}catch (Exception e) {
			return 0;
		}
    	try {
    		return Integer.valueOf(buf.toString());
    	}catch (NumberFormatException e) {
    		if(buf.charAt(0) == '-') 
    			return Integer.MIN_VALUE;
    		else 
    			return Integer.MAX_VALUE;
		}
    }
    //509. || 剑指10-I
    public static int fib(int N) {
    	int a = 0,b = 1;
    	if(N == 0) {
    		return a;
    	}
    	if(N == 1 || N == 2 ) {
    		return b;
    	}
        for (int i = 1; i < N; i++) {
        	int mid = b;
			b = a + b;
			a = mid;
			b %= 1000000007;
		}
		return b;
    }
    //9.回文数
    public static boolean isPalindrome(int x) {
    	if(x == 0)return true;
    	if(x < 0 || x % 10 ==0) {
    		return false;
    	}
    	int y = 0;
    	while(x > y) {
			y = y*10 + x%10;
			x /= 10;
		}
    	 return x == y || x == y / 10;
    }
    
    //TODO 10. 正则表达式
    public boolean isMatch(String s, String p) {
    	// 匹配任意单个字符
    	char ch0 = '.';
    	// 匹配零个或多个前面的那一个元素
    	char ch1 = '*';
    	return false;
    }
    
    //343. 整数拆分--规律法
    public static int integerBreak(int n) {
    	if(n == 2) {
    		return 1;
    	}
    	if(n == 3) {
    		return 2;
    	}
    	int num = n/3;
    	int remainder = n%3;
    	if(remainder==1) {
    		return Math.multiplyExact((int) Math.pow(3, num-1), 3+remainder) ;
    	}else {
    		return Math.multiplyExact((int) Math.pow(3, num), remainder==0?1:remainder) ;
    	}
    }
  
    //343. 整数拆分--动态规划
    public static int integerBreak_dyn(int n) {
    	int[] dp = new int[n+1];
    	dp[2] = 1;
    	for (int i = 2; i <= n; i++) {
    		int j = 1;
    		while( j < i ) {
    			dp[i] = Math.max(dp[i],Math.max(j*(i-j), j*dp[i-j]));
    			j++;
    		}
		}
		return dp[n];
    }
    /**
     * LCP 13. 寻宝
     * 1.启动位置S->石头位置O(多个位置)
     * 2.石头位置O->机关位置M(多个位置)
     * 3.机关位置M->终点位置T
     * attention：第2步中间次序无所谓，最后一次的机关位置最好靠近终点
     * @param maze
     * @return
     */
    //TODO 动态规划、状态DP
    public static int minimalSteps(String[] maze) {
		return 1;
    }
    
    //415. 字符串相加
    public static String addStrings(String num1, String num2) {
        if(num1 == "" && num2 == "") return "";
        else if(num1 == "") return num2;
        else if(num2 == "") return num1;

        int len = num1.length()>=num2.length()?num1.length():num2.length();
        StringBuffer buf = new StringBuffer();
        int next = 0;
        for(int i = 0; i < len; i++){
            int a = num1.length()<i+1?0:Integer.valueOf(num1.charAt(num1.length()-i-1 ) - '0');
            int b = num2.length()<i+1?0:Integer.valueOf(num2.charAt(num2.length()-i-1 ) - '0');
            int total = a + b + next;
            if(total >= 10) {
                buf.append(total-10);
                next = 1;
            }
            else {
                buf.append(total);
                next = 0;
            }
        }
        if(next == 1) {
        	buf.append(1);
        }
        return buf.reverse().toString();
    }
    
    /**
     * 11. 盛最多水的容器
     * @param height
     * @return
     */
    public int maxArea(int[] height) {
        int area = 0; 
        for(int i = 0; i < height.length-1; i++){
            int start = i;
            int end = height.length-1;
            while(start < end){
                int width = Math.min(height[start],height[end]);
                int length = end - start;
                area = Math.max(area,width*length);    
                end--;
            }
        }
        return area;
    }
    
    /**
     * 11. 盛最多水的容器--双指针法
     * @param height
     * @return
     */
    public int maxArea2(int[] height) {
    	 int area = 0; 
    	 int start = 0;
         int end = height.length-1;
         while(start < end){
             int width = Math.min(height[start],height[end]);
             area = Math.max(area,width*(end - start)); 
             if(height[start] > height[end]) {
            	 end--;
             }else {
            	 start++;
             }
         }
         return area;
    }
    
    /**
     * TODO
     * 210. 课程表 II 与 207. 课程表 类似
     * 基于DFS的拓扑排序
     * @param numCourses
     * @param prerequisites
     * @return
     */
    public int[] findOrder(int numCourses, int[][] prerequisites) {
    	int[] data = new int[] {};
    	return null;
    }
    
    /**
     * 
     * 198. 打家劫舍
     * 动态规划，dp[i]=max(dp[i−2]+nums[i],dp[i−1])
     */
    public static int rob198(int[] nums) {
		/*if(nums == null || nums.length <=0 )return 0;
		if(nums.length == 1)return nums[0];
		if(nums.length == 2)return Math.max(nums[0], nums[1]);*/
     	int[] maxs = new int[nums.length];
    	maxs[0] = nums[0];
    	maxs[1] = Math.max(nums[0], nums[1]);
    	for (int i = 2; i < nums.length; i++) {
    		maxs[i] = Math.max(maxs[i],Math.max(maxs[i-2]+nums[i], maxs[i-1]));
		}
		return maxs[nums.length-1];
    }
    
    /**
     * 213. 打家劫舍 II
     * @param nums
     * @return
     */
    public static int rob213(int[] nums) {
    	if(nums == null || nums.length <=0 )return 0;
    	if(nums.length == 1)return nums[0];
    	if(nums.length == 2)return Math.max(nums[0], nums[1]);
    	int[] nums1 = Arrays.copyOf(nums, nums.length);	
    	int[] nums2 = Arrays.copyOf(nums, nums.length);
    	nums1[0] = 0;
    	nums2[nums.length-1] = 0;
    	int r1 = rob198(nums1);
    	int r2 = rob198(nums2);
		return Math.max(r1,r2);
    }
    
    //选中的节点
	static Map<TreeNode, Integer> f = new HashMap<TreeNode, Integer>();
	//不选中的节点
	static Map<TreeNode, Integer> g = new HashMap<TreeNode, Integer>();
	
    /**
     * 337. 打家劫舍 III
     * 
     * 选中节点o的最大值 f(o)=g(l)+g(r)
     * 不选中节点o的最大值 g(o)=max{f(l),g(l)}+max{f(r),g(r)}
     * @param nums
     * @return
     */
    public static int rob337(TreeNode root) {
    	dfs(root);
    	return Math.max(f.getOrDefault(root, 0),g.getOrDefault(root, 0));
    }
    
    //深度优先搜索
    public static void dfs(TreeNode node) {
        if (node == null) {
            return;
        }
        dfs(node.left);
        dfs(node.right);
        f.put(node, node.val + g.getOrDefault(node.left, 0) + + g.getOrDefault(node.right, 0));
        g.put(node, Math.max(f.getOrDefault(node.left, 0), g.getOrDefault(node.left, 0)) + 
        		Math.max(f.getOrDefault(node.right, 0), g.getOrDefault(node.right, 0)));
    }
    
    /**
     * 43. 字符串相乘
     * @param num1
     * @param num2
     * @return
     */
    public static String multiply(String num1, String num2) {
    	if(num1 == "" || num1.equals("0")) {
    		return num2;
    	}
    	if(num2 == "" || num2.equals("0")) {
    		return num1;
    	}
    	//TODO dosomething
    	return addStrings(num1, num2);
    }
    
    /**
     * 347. 前 K 个高频元素。给定一个非空的整数数组，返回其中出现频率前 k 高的元素。
     * 解题思路：用类hash的方式可以快速统计次数，但是无法快速排序
     * 用map存储元素及元素个数 ，此时排序元素个数是这题的关键点
     * @param nums
     * @param k
     * @return
     */
    public static int[] topKFrequent(int[] nums, int k) {
    	int[] res = new int[k];
    	Map<Integer, Integer> map =new HashMap<Integer, Integer>();
    	for(int i = 0; i < nums.length; i++) {
    		map.put(nums[i], map.get(nums[i])==null?1:(map.get(nums[i])+1));
    	}
    	
    	List<Map.Entry<Integer, Integer>> list = new ArrayList<Map.Entry<Integer, Integer>>(map.entrySet());
    	list.sort((o1,o2) -> o2.getValue() - o1.getValue());
        for (int i = 0; i < res.length; i++) {
			res[i] = list.get(i).getKey();
		}
    	return res;
    }

}
