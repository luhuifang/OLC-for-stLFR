package parameter;

import java.io.Serializable;

import org.apache.hadoop.conf.Configuration;

/**
 * 
 * @author luhuifang
 *
 */
public class Parameter implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	private String input;
	private String hdfsdir;
	private String outdir;
	private int reducenum;
	private String logdir;
	private int overlapLen;
	private double rateMismatch;
	private String seqType;
	private String umilist;
	private String mapjvm;
	private String reducejvm;
	
	private  void usage(){
		System.out.println("Name: Olc Assembly ");
		System.out.println("Version:1.0");
		System.out.println("Options:");
		System.out.println("    -input        <str>:    input path of bam/sam files, should be hdfs path [default: NULL]");
		System.out.println("    -hdfsdir      <str>:    hdfs dir for tmp result  [default: NULL]");
		System.out.println("    -outdir       <str>:    local results dir  [default: NULL]");
		System.out.println("    -reducenum    <int>:    reduce number [default: 1]");
		System.out.println("    -overlapLen   <str>:    Minimum of overlap [default: 10]");
		System.out.println("    -rateMismatch <str>:    Frequence of mismatchs [default: 0]");
		System.out.println("    -seqType      <str>:    Type of sequences(short/long) [default: short]");
		System.out.println("    -logdir    <String>:    local logger dir [default: null]");
		System.out.println("    -umilist   <String>:    file of umi and barcodes relationship[default: null]");
		System.out.println("    -mapjvm    <String>:    Jvm of mapper[default: null]");
		System.out.println("    -reducejvm <String>:    Jvm of reducer[default: null]");
		System.out.println("    -help      <switch>:    help information");
		
	}
	
	public Parameter()
	{
		setDefault();
	}
	
	public Parameter(Configuration conf) {
		getParametersFromConf(conf);
	}
	
	private void setDefault() {
		setInput(null);
		setHdfsdir(null);
		setOutdir(null);
		setReducenum(1);
		setLogdir(null);
		setOverlapLen(10);
		setRateMismatch(0);
		setSeqType("short");
		setUmilist(null);
		setMapjvm(null);
		setReducejvm(null);
	}

	public String getInput() {
		return input;
	}

	public void setInput(String input) {
		this.input = input;
	}

	public String getHdfsdir() {
		return hdfsdir;
	}

	public void setHdfsdir(String hdfsdir) {
		this.hdfsdir = hdfsdir;
	}

	public String getOutdir() {
		return outdir;
	}

	public void setOutdir(String outfile) {
		this.outdir = outfile;
	}

	public int getReducenum() {
		return reducenum;
	}

	public void setReducenum(int reducenum) {
		this.reducenum = reducenum;
	}

	public String getLogdir() {
		return logdir;
	}

	public void setLogdir(String logdir) {
		this.logdir = logdir;
	}

	public int getOverlapLen() {
		return overlapLen;
	}

	public void setOverlapLen(int overlapLen) {
		this.overlapLen = overlapLen;
	}

	public double getRateMismatch() {
		return rateMismatch;
	}

	public void setRateMismatch(double rateMismatch) {
		this.rateMismatch = rateMismatch;
	}

	public String getSeqType() {
		return seqType;
	}

	public void setSeqType(String seqType) {
		this.seqType = seqType;
	}
	
	public String getUmilist() {
		return umilist;
	}

	public void setUmilist(String umilist) {
		this.umilist = umilist;
	}

	
	public String getMapjvm() {
		return mapjvm;
	}

	public void setMapjvm(String mapjvm) {
		this.mapjvm = mapjvm;
	}

	public String getReducejvm() {
		return reducejvm;
	}

	public void setReducejvm(String reducejvm) {
		this.reducejvm = reducejvm;
	}

	public Parameter(String[] args){
		setDefault();
		
		int i = 0;
		while(i < args.length){
			if(args[i].equals("-input")){
				setInput(args[i+1]);
				i += 2;
				continue;
			}
			if(args[i].equals("-hdfsdir")){
				setHdfsdir(args[i+1]);
				i += 2;
				continue;
			}
			if(args[i].equals("-outdir")){
				setOutdir(args[i+1]);
				i += 2;
				continue;
			}
			if(args[i].equals("-reducenum")){
				setReducenum(Integer.parseInt(args[i+1]));
				i += 2;
				continue;
			}
			if(args[i].equals("-overlapLen")){
				setOverlapLen(Integer.parseInt(args[i+1]));
				i += 2;
				continue;
			}
			if(args[i].equals("-rateMismatch")){
				setRateMismatch(Double.parseDouble(args[i+1]));
				i += 2;
				continue;
			}
			if(args[i].equals("-seqType")){
				setSeqType(args[i+1]);
				i += 2;
				continue;
			}
			if(args[i].equals("-logdir")){
				setLogdir(args[i+1]);
				i += 2;
				continue;
			}
			if(args[i].equals("-umilist")) {
				setUmilist(args[i+1]);
				i += 2;
				continue;
			}
			if(args[i].equals("-mapjvm")) {
				setMapjvm(args[i+1]);
				i += 2;
				continue;
			}
			if(args[i].equals("-reducejvm")) {
				setReducejvm(args[i+1]);
				i += 2;
				continue;
			}
			if(args[i].equals("-help")||args[i].equals("-h")){
				usage();
				System.exit(0); 
			}
			System.err.println("Unknown parameters inputted from command line. Please check it.");
			break;
		}
		checkPara();
	}

	private void checkPara() {
		if(this.input == null){
			System.err.println("The input file not found! Please check it");
			usage();
			System.exit(-1);
		}
		
		if(this.hdfsdir == null){
			System.err.println("No HDFS dir, please check it");
			usage();
			System.exit(-1);
		}
		
		if(this.outdir == null){
			System.err.println("No output dir, please check it");
			usage();
			System.exit(-1);
		}
		
	}
	
	private void getParametersFromConf(Configuration conf) {
		setInput(conf.get("input", null));
		setHdfsdir(conf.get("hdfsdir", null));
		setOutdir(conf.get("outdir", null));
		setOverlapLen(conf.getInt("overlaplen", 10));
		setRateMismatch(conf.getDouble("ratemismatch", 0));
		setReducenum(conf.getInt("reducenum", 1));
		setSeqType(conf.get("seqtype", "short"));
		setLogdir(conf.get("logdir", null));
		setUmilist(conf.get("umilist", null));
		setMapjvm(conf.get("mapjvm", null));
		setReducejvm(conf.get("reducejvm", null));
	}
	
}
