package com.example.demo.services;

import java.io.IOException;
import java.io.StringReader;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.ArrayList;
import java.util.List;

import javax.annotation.PostConstruct;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVRecord;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import com.example.demo.model.LocationStates;

@Service
public class CoronaVirusDataServices {
	// All of data set into list
	private List<LocationStates> allstates = new ArrayList<LocationStates>();

	public List<LocationStates> getAllstates() {
		return allstates;
	}

	public void setAllstates(List<LocationStates> allstates) {
		this.allstates = allstates;
	}

	private static String VIRUS_DATA_URL = "https://raw.githubusercontent.com/CSSEGISandData/COVID-19/master/csse_covid_19_data/csse_covid_19_time_series/time_series_covid19_deaths_global.csv";

	@PostConstruct // calls the method only once, just after the initialization of bean properties
	@Scheduled(cron = "* * * 1 * *") // scheduling is a process of executing the tasks for the specific time period
	public void fetchVirusData() throws IOException, InterruptedException {
		List<LocationStates> newstates = new ArrayList<LocationStates>();
		HttpClient client = HttpClient.newHttpClient();
		// This request is used to get the data from the gitHub link
		HttpRequest request = HttpRequest.newBuilder().uri(URI.create(VIRUS_DATA_URL)).build();
		// get the response from gethub link
		HttpResponse<String> httpResponse = client.send(request, HttpResponse.BodyHandlers.ofString());
		// System.out.println(httpResponse.body());
		StringReader csvBodyreader = new StringReader(httpResponse.body());
		Iterable<CSVRecord> records = CSVFormat.DEFAULT.withFirstRecordAsHeader().parse(csvBodyreader);
		// data set into LocationStates(Model class)
		for (CSVRecord record : records) {
			LocationStates losta = new LocationStates();
			losta.setState(record.get("Province/State"));
			losta.setCountry(record.get("Country/Region"));
			int latestCase = Integer.parseInt(record.get(record.size() - 1));
			int PrevCase = Integer.parseInt(record.get(record.size() - 2));
			losta.setLatestTotalDeaths(latestCase);
			losta.setDifferFromPrevay(latestCase - PrevCase);
			System.out.println(losta);

			newstates.add(losta);

		}
		this.allstates = newstates;

	}
}
