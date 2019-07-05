package mapreducer;

import java.io.IOException;

import org.apache.commons.lang.StringUtils;
import org.apache.hadoop.io.LongWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Mapper;

import edu.umd.cloud9.io.pair.Pair;
import edu.umd.cloud9.io.pair.PairOfStringInt;
import edu.umd.cloud9.io.pair.PairOfStrings;

public class BarcodeUmiMapper extends Mapper<LongWritable, Text, PairOfStringInt, PairOfStrings>{
	
	private PairOfStringInt outputKey;
	private PairOfStrings outputValue;

	@Override
	protected void map(LongWritable key, Text value, Context context)
			throws IOException, InterruptedException {
	
		String[] lines = StringUtils.strip(value.toString(), "()").split(",");
		
		String barcode = lines[0];
		String[] umis = lines[1].split("\t");
		
		for(String umi: umis) {
			outputKey = Pair.of(barcode, 1);
			outputValue = Pair.of("umi", umi);
			context.write(outputKey, outputValue);
			
		}
	}
	
}
