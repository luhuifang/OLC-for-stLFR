package main;

import java.io.IOException;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.NullWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Job;
import org.apache.hadoop.mapreduce.lib.input.FileInputFormat;
import org.apache.hadoop.mapreduce.lib.output.FileOutputFormat;
import org.apache.hadoop.mapreduce.lib.output.LazyOutputFormat;
import org.apache.hadoop.mapreduce.lib.output.MultipleOutputs;
import org.apache.hadoop.mapreduce.lib.output.TextOutputFormat;
import input.FqText;
import io.Output;
import mapreducer.AssemblyReducer;
import mapreducer.BarcodeAssemblyMapper;
import parameter.Parameter;


/**
 * 
 * @author luhuifang
 *
 */
public class BarcodeAssemblyMRMain {
	
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
		
		/*
		 * get and set Job
		 */
		Job job = Job.getInstance(conf, "OLC_barcode_assembly");
		job.setJarByClass(BarcodeAssemblyMRMain.class);
		job.setMapperClass(BarcodeAssemblyMapper.class);
		job.setReducerClass(AssemblyReducer.class);
		
		job.setNumReduceTasks(para.getReducenum());//set the number of reducer tasks, default is 1;
		job.setInputFormatClass(FqText.class);
		
		/*
		 * set Mapper output key and value
		 */
		job.setMapOutputKeyClass(Text.class);
		job.setMapOutputValueClass(Text.class);
		
		/*
		 * set Reducer output key and value
		 */
		job.setOutputKeyClass(NullWritable.class);
		job.setOutputValueClass(Text.class);
		
		/*
		 * set MultipleOutputs
		 */
		MultipleOutputs.addNamedOutput(job, "contigs", TextOutputFormat.class, NullWritable.class, Text.class);
		MultipleOutputs.addNamedOutput(job, "paths", TextOutputFormat.class, NullWritable.class, Text.class);
		
		/*
		 * delete part-r-0000* output file
		 */
		LazyOutputFormat.setOutputFormatClass(job,TextOutputFormat.class);
		
		/*
		 * set input file and output dir
		 */
		FileInputFormat.addInputPath(job, new Path(para.getInput())); //set input file
		FileOutputFormat.setOutputPath(job, new Path(para.getHdfsdir() + "/result/")); //set hdfs output dir
		
		
		job.waitForCompletion(true); //submit job
		
		Output.DowloadResult(conf, para.getHdfsdir() + "/result/", para.getOutdir() , "contigs");
		Output.DowloadResult(conf, para.getHdfsdir() + "/result/", para.getOutdir() , "paths");
		System.exit(0);
	}
	

}
