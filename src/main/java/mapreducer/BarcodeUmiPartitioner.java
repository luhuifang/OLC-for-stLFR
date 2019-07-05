package mapreducer;

import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Partitioner;

public class BarcodeUmiPartitioner extends Partitioner<Text, Text>{

	@Override
	public int getPartition(Text key, Text value, int num_partition) {
		if(key.toString().contains("_")) { //barcode
			String[] barcodes = key.toString().split("_");
			return (Integer.parseInt(barcodes[0]) + Integer.parseInt(barcodes[1]) + Integer.parseInt(barcodes[2]))%num_partition;
		}else { //umi
			return Math.abs(key.toString().hashCode())%num_partition;
		}
	}

	
}
