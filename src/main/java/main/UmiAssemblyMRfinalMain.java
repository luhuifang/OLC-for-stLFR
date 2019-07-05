package main;

import java.io.IOException;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.NullWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Job;
import org.apache.hadoop.mapreduce.lib.input.FileInputFormat;
import org.apache.hadoop.mapreduce.lib.input.MultipleInputs;
import org.apache.hadoop.mapreduce.lib.input.TextInputFormat;
import org.apache.hadoop.mapreduce.lib.jobcontrol.ControlledJob;
import org.apache.hadoop.mapreduce.lib.jobcontrol.JobControl;
import org.apache.hadoop.mapreduce.lib.output.FileOutputFormat;
import org.apache.hadoop.mapreduce.lib.output.LazyOutputFormat;
import org.apache.hadoop.mapreduce.lib.output.MultipleOutputs;
import org.apache.hadoop.mapreduce.lib.output.TextOutputFormat;
import input.FqText;
import mapreducer.AssemblyReducer;
import mapreducer.BarcodeReadsMapper;
import mapreducer.BarcodeUmiMapper;
import mapreducer.BarcodeUmiPartitioner;
import mapreducer.LeftJoinGroupComparator;
import mapreducer.LeftJoinPartitioner;
import mapreducer.LeftJoinReducer;
import mapreducer.UmiAssemblyMapper;
import parameter.Parameter;

import edu.umd.cloud9.io.pair.PairOfStringInt;
import edu.umd.cloud9.io.pair.PairOfStrings;

public class UmiAssemblyMRfinalMain {
	public static void main(String[] args) throws IOException, ClassNotFoundException, InterruptedException {
		
		/*
		 * get Parameters
		 */
		Parameter para = new Parameter(args);
		/*
		 *get and set Configuration  
		 */
		Configuration conf = new Configuration();
		conf.set("input", para.getInput());
		conf.set("hdfsdir", para.getHdfsdir());
		conf.set("outdir", para.getOutdir());
		conf.set("logdir", para.getLogdir());
		conf.set("seqtype", para.getSeqType());
		conf.setInt("overlaplen", para.getOverlapLen());
		conf.setDouble("ratemismatch", para.getRateMismatch());
		conf.setInt("reducenum", para.getReducenum());
		conf.set("umilist", para.getUmilist());
		
		if(para.getMapjvm() != null)
			conf.set("mapreduce.map.java.opts", para.getMapjvm());
		
		if(para.getReducejvm() != null)
			conf.set("mapreduce.reduce.java.opts", para.getReducejvm());
		
		Job job1 = Job.getInstance(conf, "LeftJoinBarcodeUmi"); //leftJoinBarcodeUmiJob
		Job job2 = Job.getInstance(conf, "OLC_umi_assembly");
		
		//job1
		job1.setJarByClass(UmiAssemblyMRfinalMain.class);
		job1.setReducerClass(LeftJoinReducer.class);
		job1.setPartitionerClass(LeftJoinPartitioner.class);
		job1.setGroupingComparatorClass(LeftJoinGroupComparator.class);
		job1.setNumReduceTasks(para.getReducenum());//set the number of reducer tasks, default is 1;

		job1.setMapOutputKeyClass(PairOfStringInt.class);
		job1.setMapOutputValueClass(PairOfStrings.class);
	
		job1.setOutputKeyClass(Text.class);
		job1.setOutputValueClass(Text.class);
		
		String[] inputs = para.getInput().split(",");
		for(String input : inputs) {
			MultipleInputs.addInputPath(job1, new Path(input), FqText.class, BarcodeReadsMapper.class);
		}
		MultipleInputs.addInputPath(job1, new Path(para.getUmilist()), TextInputFormat.class, BarcodeUmiMapper.class); //umilist file
		FileOutputFormat.setOutputPath(job1, new Path(para.getHdfsdir() + "/barcodeUmiReads/")); //set hdfs output dir
		
		
		// job2
		job2.setJarByClass(UmiAssemblyMRfinalMain.class);
		job2.setMapperClass(UmiAssemblyMapper.class);
		job2.setReducerClass(AssemblyReducer.class);
		job2.setPartitionerClass(BarcodeUmiPartitioner.class);
		
		job2.setNumReduceTasks(para.getReducenum());//set the number of reducer tasks, default is 1;
		job2.setMapOutputKeyClass(Text.class);
		job2.setMapOutputValueClass(Text.class);
		
		job2.setOutputKeyClass(NullWritable.class);
		job2.setOutputValueClass(Text.class);
		
		MultipleOutputs.addNamedOutput(job2, "contigs", TextOutputFormat.class, NullWritable.class, Text.class);
		MultipleOutputs.addNamedOutput(job2, "paths", TextOutputFormat.class, NullWritable.class, Text.class);
		LazyOutputFormat.setOutputFormatClass(job2,TextOutputFormat.class);
		
		FileInputFormat.addInputPath(job2, new Path(para.getHdfsdir() + "/barcodeUmiReads/part*")); //set input file
		FileOutputFormat.setOutputPath(job2, new Path(para.getHdfsdir() + "/assemblyResult/")); //set hdfs output dir
		
		JobControl control = new JobControl("UmiAssembly");
        
        ControlledJob aJob = new ControlledJob(job1.getConfiguration());
        ControlledJob bJob = new ControlledJob(job2.getConfiguration());
        // 设置作业依赖关系
        bJob.addDependingJob(aJob);
        
        control.addJob(aJob);
        control.addJob(bJob);
        
        Thread thread = new Thread(control);
        thread.start();
        
        while(!control.allFinished()) {
            //thread.sleep(1000);
        	Thread.sleep(1000);
        }
        
        control.stop();
        
        System.exit(0);
	}

}
