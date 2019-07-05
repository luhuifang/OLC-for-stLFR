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
import io.Output;
import mapreducer.AssemblyReducer;
import mapreducer.BarcodeUmiPartitioner;
import mapreducer.UmiAssemblyMapper;
import parameter.Parameter;

public class UmiAssemblyMRMain {

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
		Job job = Job.getInstance(conf, "OLC_umi_assembly");
		job.setJarByClass(BarcodeAssemblyMRMain.class);
		job.setMapperClass(UmiAssemblyMapper.class);
		job.setReducerClass(AssemblyReducer.class);
		job.setPartitionerClass(BarcodeUmiPartitioner.class);
		
		job.setNumReduceTasks(para.getReducenum());//set the number of reducer tasks, default is 1;
		
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
		String[] inputs = para.getInput().split(",");
		for(String i : inputs) {
			FileInputFormat.addInputPath(job, new Path(i)); //set input file
		}
		FileOutputFormat.setOutputPath(job, new Path(para.getHdfsdir() + "/result/")); //set hdfs output dir
		
		
		job.waitForCompletion(true); //submit job
		
		Output.DowloadResult(conf, para.getHdfsdir() + "/result/contigs", para.getOutdir() , "contigs");
		Output.DowloadResult(conf, para.getHdfsdir() + "/result/paths", para.getOutdir() , "paths");
		System.exit(0);
	}
}
