package com.poc.service;
import java.io.IOException;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.multipart.MultipartFile;

import com.poc.model.ExcelHelper;
import com.poc.model.Job;
import com.poc.repository.jobRepository;
@org.springframework.stereotype.Service
public class jobService implements jobServiceInf{
@Autowired
	private jobRepository rep;
	
	  
	/*@Override
	public void csv(String filePath) {
		List<Job> jobs=new ArrayList<Job>();
		String file="C://Users//mnaveen//Desktop//csv//sample.csv";
		String line="";
		BufferedReader breader = null;
		try {
			breader = new BufferedReader(new FileReader(file));
		} catch (FileNotFoundException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		
		try {
			while((line=breader.readLine())!=null){
				String[] fields=line.split(",");
				if(fields.length>0){
					Job job=new Job();
					job.setJobId(Integer.parseInt(fields[0]));
					job.setAvailability(fields[1]);
					job.setCountry(fields[2]);
					job.setExperience(Float.parseFloat(fields[3]));
					job.setJobDescription(fields[4]);
					job.setJobTitle(fields[5]);
					job.setJobType(fields[6]);
					job.setLanguage(fields[7]);
					job.setPayRate(Integer.parseInt(fields[8]));
					job.setReplyRate(Integer.parseInt(fields[9]));
					job.setSkills(fields[10]);
					job.setState(fields[11]);
				}
			}
		} catch (NumberFormatException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		for (Job job : jobs) {
			System.out.println(job);
		}
		
	}*/

}
