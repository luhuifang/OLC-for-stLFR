package io;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.FSDataInputStream;
import org.apache.hadoop.fs.FileStatus;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.IOUtils;

public class Output {
	private static OutputStream out;

	
	public static boolean DowloadResult(Configuration conf, String hdfsresult, String outdir, String prefix) {
		try {
			out = new FileOutputStream(outdir + "/" + prefix + ".txt", true);
			FileSystem fs = FileSystem.get(conf);
			FileStatus[] fstatus = fs .listStatus(new Path(hdfsresult));
			
			for(FileStatus file : fstatus){
				if(file.getPath().toString().contains(prefix)) {
					FSDataInputStream fsopen = fs.open(file.getPath());
					IOUtils.copyBytes(fsopen, out, 4096, false);
					fsopen.close();
				}
			}
			return true;
		} catch (FileNotFoundException e) {
			return false;
		} catch (IOException e) {
			return false;
		}finally {
			if(out != null){
				try {
					out.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}

	}
}
