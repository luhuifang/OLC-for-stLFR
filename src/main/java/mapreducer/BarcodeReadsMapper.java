package mapreducer;

import java.io.IOException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Mapper;

import edu.umd.cloud9.io.pair.Pair;
import edu.umd.cloud9.io.pair.PairOfStringInt;
import edu.umd.cloud9.io.pair.PairOfStrings;

public class BarcodeReadsMapper extends Mapper<Text, Text, PairOfStringInt, PairOfStrings> {

	private PairOfStringInt outputKey;
	private PairOfStrings outputValue;
	
	@Override
	protected void map(Text key, Text value, Context context)
			throws IOException, InterruptedException {
		if(key == null || value == null) return;
		Matcher matcher = Pattern.compile("(\\d+_\\d+_\\d+)").matcher(key.toString());
		if(matcher.find()) {
			String barcode = matcher.group(1);
			if(! "0_0_0".equals(barcode)) {
				outputKey = Pair.of(barcode, 2);
				outputValue = Pair.of("read", value.toString());
				context.write(outputKey, outputValue);
			}
		}
		
	}
	
	

}
