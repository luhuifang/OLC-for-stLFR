package io;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.lang3.StringUtils;
import org.apache.hadoop.io.Text;
import graph.Vertex;


/**
 * 
 * @author luhuifang
 *
 */
public class Input {
	
	/**
	 * Get list of sequence from fasta file
	 * @param fa A fasta file
	 * @return List of Vertex(sequences)
	 * @throws IOException
	 */
	public static List<Vertex> readFastaFile(File fa) throws IOException {
		BufferedReader br = null;
		List<Vertex> raw_seq = new ArrayList<Vertex>();
		
		if(check_file(fa)) {
			int count = -1;
			try {
				br = new BufferedReader( new FileReader(fa));
				String line ;
				while((line = br.readLine()) != null) {
					if(line.startsWith(">")) {
						count += 1;
						Matcher matcher = Pattern.compile(">(\\S+)").matcher(line);
						
						if(matcher.find()) {						
							String read_name = matcher.group(1);
							Vertex new_node = new Vertex(count, read_name, "");
							raw_seq.add(new_node);
						}
					}else {
						raw_seq.get(count).extendSeq(line);
					}
				}
			} catch (FileNotFoundException e) {
				e.printStackTrace();
			}finally {
				if(br != null) {
					br.close();
				}
			}
		}
		
		return raw_seq;
	}
	
	public static void readFastqFile() {
		
	}
	
	/**
	 * Get list of sequence from String array.
	 * @param seqs String array of sequences
	 * @return List of Vertex(sequences)
	 */
	public static List<Vertex> readFastaStream(String[] seqs) {
		
		List<Vertex> raw_seq = new ArrayList<Vertex>();
		int count = 0;
		for(String seq : seqs) {
			Vertex new_node = new Vertex(count, String.valueOf(count), seq);
			raw_seq.add(new_node);
			count += 1;
		}
		return raw_seq;
	}
	
	
	/**
	 * Get list of sequence from String Collection.
	 * @param seqs String array of sequences
	 * @return List of Vertex(sequences)
	 */
	public static <T extends Collection<String> > List<Vertex> readFastaStream( T seqs){
		List<Vertex> raw_seq = new ArrayList<Vertex>();
		int count = 0;
		for(String seq : seqs) {
			Vertex new_node = new Vertex(count, String.valueOf(count), seq);
			raw_seq.add(new_node);
			count += 1;
		}
		return null;
		
	}
	
	/**
	 * Get list of sequence from FqText.
	 * @param seqs Value of FqText object, format:
	 * 	read_name									1/2		sequence								quality
	 * 	CL100111543L2C010R054_4886#1272_854_253		1		CTAAAGGAGGAGTAACAGCTGAATGATTTTC...		@8EA9C@)@@CA>D;D?>>>D63?<1DEAEA=?;;DDB8B@F...
	 * @return List of Vertex(sequences)
	 */
	public static List<Vertex> readFastqStream(Iterable<Text> seqs) {
		
		List<Vertex> raw_seq = new ArrayList<Vertex>();
		int count = 0;
		for(Text s : seqs) {
			String[] s_list = s.toString().split("\\s+");
			String read_name = s_list[0] + "/" + s_list[1];
			String seq = s_list[2];
			Vertex new_node = new Vertex(count, read_name, seq);
			raw_seq.add(new_node);
			count ++;
		}
		return raw_seq;
	}

	/**
	 * Get a map of barcode and umi relation
	 * @param umilist A txt file of barcode and umi
	 * @return Map barcode_umiKey: barcode, Value: umi list
	 */
	public static Map<String, String[]> readUmilist(File umilist) {
		BufferedReader br = null;
		Map<String, String[]> barcode_umi = new HashMap<String, String[]>();
		if(check_file(umilist)) {
			try {
				br = new BufferedReader(new FileReader(umilist));
				String line;
				while((line = br.readLine()) != null) {
					//String[] lines = line.replace("(", "").replace(")", "").split(",");
					String[] lines = StringUtils.strip(line, "()").split(",");
					String barcode = lines[0];
					String[] umis = lines[1].split("\t");
					barcode_umi.put(barcode, umis);
				}
				
			} catch (FileNotFoundException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}finally {
				if (br != null) {
					try {
						br.close();
					} catch (IOException e) {
						e.printStackTrace();
					}
				}
			}
			
		}
		return barcode_umi;
		
	}
	
	private static boolean check_file(File f) {
		if (f.isDirectory()) {
			throw new RuntimeException("File is directory, " + f.toString());
		}else if (!f.exists()) {
			throw new RuntimeException("File not exists, " + f.toString());
		}
		return true;
		
	}
}
