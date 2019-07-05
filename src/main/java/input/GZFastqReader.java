package input;

import java.io.IOException;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.FSDataInputStream;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.io.compress.CompressionCodec;
import org.apache.hadoop.io.compress.CompressionCodecFactory;
import org.apache.hadoop.mapreduce.InputSplit;
import org.apache.hadoop.mapreduce.RecordReader;
import org.apache.hadoop.mapreduce.TaskAttemptContext;
import org.apache.hadoop.mapreduce.lib.input.FileSplit;
import org.apache.hadoop.util.LineReader;

public class GZFastqReader extends RecordReader<Text, Text>{
	protected static final Log LOG = LogFactory.getLog(GZFastqReader.class
			.getName());

	protected CompressionCodecFactory compressionCodecs = null;
	protected long start;
	protected long pos;
	protected long end;
	protected LineReader in;
	protected int maxLineLength;
	protected byte[] recordDelimiterBytes = null;
	protected String firstLine = "";
	protected String sampleID = null;
	protected Text key = null;
	protected Text value  = null;
	
	public GZFastqReader() {
		
	}
	
	@Override
	public void close() throws IOException {
		if (in != null) {
			in.close();
		}
	}

	@Override
	public Text getCurrentKey() throws IOException, InterruptedException {
		return key;
	}

	@Override
	public Text getCurrentValue() throws IOException, InterruptedException {
		return value;
	}

	@Override
	public float getProgress() throws IOException, InterruptedException {
		if (start == end) {
			return 0.0f;
		} else {
			return Math.min(1.0f, (pos - start) / (float) (end - start));
		}
	}

	public void getFirstFastqLine() throws IOException {
		Text tmpline = new Text();
		int size;
		while ((size = in.readLine(tmpline, maxLineLength, Math.max(
				(int) Math.min(Integer.MAX_VALUE, end - pos), maxLineLength))) != 0) {
			start += size;
			//System.out.println("start:" + start);
			//System.out.println("line: " + tmpline.toString());
			if (tmpline.toString().startsWith("@")) {
				firstLine = tmpline.toString();
				break;
			}
		}
	}
	
	@Override
	public void initialize(InputSplit inputSplit, TaskAttemptContext context) throws IOException, InterruptedException {
		
		FileSplit split =(FileSplit) inputSplit;
		Configuration job = context.getConfiguration();
		
		String delimiter = job.get("textinputformat.record.delimiter");
        if (null != delimiter)
            recordDelimiterBytes = delimiter.getBytes();

		this.maxLineLength = job.getInt("mapred.linerecordreader.maxlength",
				Integer.MAX_VALUE);
		start = split.getStart();
		end = start + split.getLength();
		final Path file = split.getPath();
		compressionCodecs = new CompressionCodecFactory(job);
		final CompressionCodec codec = compressionCodecs.getCodec(file);

		/*
		// System.err.println("split:" + split.getPath().toString());
		String multiSampleList = job.get("multiSampleList");
		if (multiSampleList != null && !multiSampleList.equals("")) {
			MultiSampleList samplelist;
			samplelist = new MultiSampleList(multiSampleList, false);
			SampleList slist = samplelist.getID(split.getPath().toString());
			if (slist != null) {
				sampleID = String.valueOf(slist.getId());
			} else {
				sampleID = "+";
			}
		}
		*/

		// open the file and seek to the start of the split
		FileSystem fs = file.getFileSystem(job);
		FSDataInputStream fileIn = fs.open(split.getPath());
		boolean skipFirstLine = false;
		if (codec != null) {
			if (null == this.recordDelimiterBytes) {
				in = new LineReader(codec.createInputStream(fileIn), job);
			} else {
				in = new LineReader(codec.createInputStream(fileIn), job,
						this.recordDelimiterBytes);
			}
			end = Long.MAX_VALUE;
		} else {
			if (start != 0) {
				skipFirstLine = true;
				--start;
				fileIn.seek(start);
			}
			if (null == this.recordDelimiterBytes) {
				in = new LineReader(fileIn, job);
			} else {
				in = new LineReader(fileIn, job, this.recordDelimiterBytes);
			}
		}
		if (skipFirstLine) { // skip first line and re-establish "start".
			start += in.readLine(new Text(), 0,
					(int) Math.min((long) Integer.MAX_VALUE, end - start));
		}
		getFirstFastqLine();
		this.pos = start;
	}

	@Override
	public boolean nextKeyValue() throws IOException, InterruptedException {
		if (key == null) {
			key = new Text();
		}
		if (value == null) {
			value = new Text();
		}
		int newSize = 0;
		boolean iswrongFq = false;
		while (pos < end) {
			Text tmp = new Text();
			String[] st = new String[4];
			int startIndex = 0;

			if (!firstLine.equals("")) {
				st[0] = firstLine;
				startIndex = 1;
				firstLine = "";
			}

			for (int i = startIndex; i < 4; i++) {
				newSize = in.readLine(tmp, maxLineLength, Math.max(
						(int) Math.min(Integer.MAX_VALUE, end - pos),
						maxLineLength));

				if (newSize == 0) {
					iswrongFq = true;
					break;
				}
				pos += newSize;
				st[i] = tmp.toString();
			}

			if(st[0].charAt(0)=='@' && st[1].charAt(0)=='@'){
				// st数组元素前移一位
				System.arraycopy(st, 1, st, 0, 3);
				newSize = in.readLine(tmp, maxLineLength, Math.max(
						(int) Math.min(Integer.MAX_VALUE, end - pos),
						maxLineLength));

				if (newSize == 0) {
					iswrongFq = true;
					break;
				}
				pos += newSize;
				st[3] = tmp.toString();
			}

			if (!iswrongFq) {
				int index = st[0].lastIndexOf("/");
				if (index < 0) {
					String[] splitTmp = st[0].split(" ");
					char ch;
					if(splitTmp.length == 1) {
						ch = '1';
						st[0] = splitTmp[0] + "/" + ch;
					}else {
						ch = splitTmp[1].charAt(0);
						if (ch != '1' && ch != '2')
							throw new RuntimeException("error fq format at reads:"
								+ st[0]);

						st[0] = splitTmp[0] + "_1" + splitTmp[1].substring(1) + "/"
							+ ch;
					}
					index = st[0].lastIndexOf("/");
				}
				String tempkey = st[0].substring(1, index).trim();
				char keyIndex = st[0].charAt(index + 1);

				if (sampleID == null || sampleID.equals("") || sampleID.equals("+")) {
					key.set(">" + st[0]);
					value.set(tempkey + "\t" + keyIndex + "\t" + st[1] + "\t"
							+ st[3]);
				} else {
					key.set(">" + sampleID);
					value.set(tempkey + "\t" + keyIndex + "\t" + st[1] + "\t"
							+ st[3]);
				}
			} else {
				LOG.warn("wrong fastq reads:blank line among fq file or end of file!");
			}
			break;
		}
		return !(newSize == 0 || iswrongFq);
	}

}
