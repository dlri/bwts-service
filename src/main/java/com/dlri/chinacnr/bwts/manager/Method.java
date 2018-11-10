package com.dlri.chinacnr.bwts.manager;

import java.lang.Math;

/**
 * 本类中包含增长序列的处理，以及最小二乘法等相关通用算法
 *
 * @version 1.0
 */
public strictfp class Method
{
		/**
	 * 时间序列二次指数平滑法预测计算器
	 * @param baseSer 被拟合时间序列
	 * @param alpha 二次指数平滑法中的alpha值，alpha的大小体现了修正的幅度，alpha越大，修正的幅度越大；alpha越小，修正的幅度也越小，通常取0.5
	 * @param futureLength 预测期的长度，预测几期，该值就为多少
	 * @return 返回一个序列，该序列是对输入的时间序列的历史回溯序列以及预测序列的按顺序组合
	 */
	public static double[] TimeSerCalculator(double[] baseSer, double alpha, int futureLength)
	{
		// 建立一次指数平滑序列（长度是原序列的长度+1）
		double[] s1 = new double[baseSer.length + 1];
		
		// 将初值赋在一次指数平滑序列的第一个值中
		s1[0] = baseSer[0];
		
		// 一次指数平滑算法
		for(int i = 1; i <= baseSer.length; i++)
		{
			s1[i] = alpha * baseSer[i-1] + (1 - alpha)* s1[i-1];
		}
		
		// 建立二次指数平滑序列
		double[] s2 = new double[baseSer.length + 1];
		
		// 将初值赋在二次指数平滑序列的第一个值中
		s2[0] = baseSer[0];
		
		// 二次指数平滑算法
		for(int i = 1; i < s1.length; i++)
		{
			s2[i] = alpha * s1[i]+ (1 - alpha) * s2[i-1];
		}
		
		// 预测值的斜率与截距
		double a_t = 0;
		
		double b_t = 0;
		
		a_t = 2.0 * s1[s1.length-1] - s2[s2.length-1];
		
		b_t = (alpha / (1.0 - alpha)) * (s1[s1.length-1] - s2[s2.length-1]);
		
		// 预测值的计算
		
		double[] future = new double[futureLength];
		
		for(int i = 0; i < futureLength; i++)
		{
			future[i] = a_t + b_t * (i + 1.0);
		}
		
		// 历史回溯值的计算
		double[] history = new double[baseSer.length];
		
		history[0] = baseSer[0];
		
		for(int i = 0; i < history.length - 1; i++)
		{
			history[i+1] = (1.0 + 1.0/(1.0 - alpha)) * s1[i + 1] - 1.0 /(1.0-alpha) * s2[i + 1];
		}
		
		// 将两列数组组合在一起输出
		double[] ans = new double[history.length + future.length];
		
		for(int i = 0; i< history.length; i++)
		{
			ans[i] = history[i];
		}
		
		for(int i = 0; i< future.length; i++)
		{
			ans[i + history.length] = future[i];
		}
		
		return ans;
	}
	
	public static void main(String args[]){
		double[] myList = new double[15];
		double[] myArray={10,12,11,14,10,11};
		myList=TimeSerCalculator(myArray, 0.05, 5);
		for (int i = 0; i < myList.length; i++) {
			System.out.println("myList ["+i+"] = "+String.format("%.2f", myList[i]));
	      }
		
	}
}
