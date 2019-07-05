package main;

import java.io.IOException;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Job;
import org.apache.hadoop.mapreduce.lib.input.MultipleInputs;
import org.apache.hadoop.mapreduce.lib.input.TextInputFormat;
import org.apache.hadoop.mapreduce.lib.output.FileOutputFormat;
import input.FqText;
import mapreducer.BarcodeReadsMapper;
import mapreducer.BarcodeUmiMapper;
import mapreducer.LeftJoinGroupComparator;
import mapreducer.LeftJoinPartitioner;
import mapreducer.LeftJoinReducer;
import parameter.Parameter;

import edu.umd.cloud9.io.pair.PairOfStringInt;
import edu.umd.cloud9.io.pair.PairOfStrings;

public class LeftJoinBarcodeUmiMain {

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
		
		/*
		 * get and set Job
		 */
		Job job = Job.getInstance(conf, "LeftJoinBarcodeUmi");
		job.setJarByClass(BarcodeAssemblyMRMain.class);
		job.setReducerClass(LeftJoinReducer.class);
		job.setPartitionerClass(LeftJoinPartitioner.class);
		job.setGroupingComparatorClass(LeftJoinGroupComparator.class);
		
		job.setNumReduceTasks(para.getReducenum());//set the number of reducer tasks, default is 1;
		//job.setInputFormatClass(FqText.class);
		
		/*
		 * set Mapper output key and value
		 */
		job.setMapOutputKeyClass(PairOfStringInt.class);
		job.setMapOutputValueClass(PairOfStrings.class);
		
		/*
		 * set Reducer output key and value
		 */
		job.setOutputKeyClass(Text.class);
		job.setOutputValueClass(Text.class);
		
		
		/*
		 * set input file and output dir
		 */
		String[] inputs = para.getInput().split(",");
		for(String input : inputs) {
			MultipleInputs.addInputPath(job, new Path(input), FqText.class, BarcodeReadsMapper.class);
		}
			
		MultipleInputs.addInputPath(job, new Path(para.getUmilist()), TextInputFormat.class, BarcodeUmiMapper.class);
		FileOutputFormat.setOutputPath(job, new Path(para.getHdfsdir() + "/result/")); //set hdfs output dir
		job.waitForCompletion(true); //submit job
		System.exit(0);
		
	}

}
