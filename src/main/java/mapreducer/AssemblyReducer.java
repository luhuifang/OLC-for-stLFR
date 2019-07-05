package mapreducer;

import java.io.IOException;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.commons.lang3.tuple.MutablePair;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.io.NullWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Reducer;
import org.apache.hadoop.mapreduce.lib.output.MultipleOutputs;
import assembly.UmiAssembly;
import parameter.Parameter;

public class AssemblyReducer extends Reducer<Text, Text, NullWritable, Text>{

	private Parameter para;
	private MultipleOutputs<NullWritable, Text> mos; 
	
	@Override
	protected void setup(Context context) throws IOException, InterruptedException {
		Configuration conf = context.getConfiguration();
		this.para = new Parameter(conf);
		mos = new MultipleOutputs<NullWritable, Text>(context);
	}
	
	@Override
	protected void reduce(Text k2, Iterable<Text> v2, Context context)
			throws IOException, InterruptedException {
		
		UmiAssembly assembly = new UmiAssembly(v2);
		MutablePair<Map<Integer, String>, Map<Integer, String>> res = 
				assembly.runAssemblyAfterFilter(para.getOverlapLen(), para.getRateMismatch(), para.getSeqType());
		
		Map<Integer, String> contigs = res.left;
		Map<Integer, String> path = res.right;
		
		int reads_num = assembly.getRaw_seq().size();

		for(Entry<Integer, String> entry : contigs.entrySet()) {
			String writer = ">" + k2.toString() + "_" + entry.getKey() 
							+ "#" + reads_num 
							+ "#" +entry.getValue().length() 
							+ "\n" + entry.getValue();
			mos.write(NullWritable.get(), new Text(writer), "contigs/contigs");
		}
		
		for(Entry<Integer, String> entry : path.entrySet()) {
			String writer = ">" + k2.toString() + "_" + entry.getKey() 
							+ "#" + reads_num 
							+ "#" + entry.getValue().split("\\s+").length
							+ "\n" + entry.getValue();
			mos.write(NullWritable.get(), new Text(writer), "paths/paths");
		}
	}

	@Override
	protected void cleanup(Reducer<Text, Text, NullWritable, Text>.Context context)
			throws IOException, InterruptedException {
		if(mos != null) {
			mos.close();
			mos=null;
		}
	}


}
