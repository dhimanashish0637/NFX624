package com.searshc.nfx624.nfx624;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.bson.Document;
import org.bson.codecs.configuration.CodecRegistry;
import org.bson.codecs.pojo.PojoCodecProvider;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.mongodb.BasicDBObject;
import com.mongodb.MongoClient;
import com.mongodb.MongoClientSettings;
import com.mongodb.MongoClientURI;
import com.mongodb.client.FindIterable;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;

public class Imple {
	String MRSRTV_STORE = "";
	String MRSRTV_DIV = "";
	static ArrayList<String> store_data = new ArrayList<String>();
	static boolean storetableflag = false;
	static boolean storeontable = true;
	static FX624 fx624 = new FX624();
	static boolean flag = false;
	static MongoClient mongoClient = null;

	public MongoDatabase getConnection() {

		String template = "mongodb://%s:%s@%s/dailysales_trandetails?replicaSet=rs0&readpreference=%s";
		String username = "dlysalestest";
		String password = "shidlysales";
		String clusterEndpoint = "docdb-2019-11-29-07-48-46-testdlysales.cluster-cmws6fic1moc.us-east-2.docdb.amazonaws.com";
		String readPreference = "secondaryPreferred";
		String connectionString = String.format(template, username, password, clusterEndpoint, readPreference);
		MongoClientURI clientURI = new MongoClientURI(connectionString);
		mongoClient = new MongoClient(clientURI);
		CodecRegistry pojoCodecRegistry = org.bson.codecs.configuration.CodecRegistries.fromRegistries(
				MongoClientSettings.getDefaultCodecRegistry(), org.bson.codecs.configuration.CodecRegistries
						.fromProviders(PojoCodecProvider.builder().automatic(true).build()));
		MongoDatabase testDB = mongoClient.getDatabase("dailysales_transactions_db")
				.withCodecRegistry(pojoCodecRegistry);
		return testDB;
	}

	public void getAllJson() throws JsonParseException, JsonMappingException, IOException {

		String jsonobject = "";
		String path1 = "C:\\Users\\aashish\\Desktop\\FX110STR.txt";
		Imple implObj = new Imple();
		try {
			MongoDatabase testDB = getConnection();
			// dailysales_trandata
			ObjectMapper mapper = new ObjectMapper();
			MongoCollection<org.bson.Document> mongoCollection = testDB.getCollection("tranData");
			// BasicDBObject whereQuery = new BasicDBObject();
			// whereQuery.put("_id", "0157056702092019-01-1406:22:43");
			// FindIterable<org.bson.Document> docs = mongoCollection.find(whereQuery);

			BasicDBObject query = new BasicDBObject();
			List<BasicDBObject> obj = new ArrayList<BasicDBObject>();
			obj.add(new BasicDBObject("salesDate", "2020-01-21"));
			// obj.add(new BasicDBObject("B1.storeNumber", "01570"));
			query.put("$or", obj);
			query.put("B1.transactionTypeCode", new BasicDBObject("$ne", "F"));

			FindIterable<Document> docs = mongoCollection.find(query)
					.sort(new BasicDBObject("salesDate", 1).append("salesCheck", 1));
			ArrayList<String> storedata = implObj.intilizationpara1000(path1);
			for (org.bson.Document doc : docs) {
				System.out.println("document is ");
				System.out.println(doc);
				jsonobject = doc.toJson();
				Map<String, String> map = mapper.readValue(jsonobject, Map.class);
				// System.out.println(" map ==> " + map);
				implObj.get_segments(map, storedata);
			}
			mongoClient.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public ArrayList<String> intilizationpara1000(String a) throws IOException {
		ArrayList<String> data = readstorefile(a);
		storeontable = false;
		return data;
	}
	/*
	 * @name = readstorefile
	 * 
	 * @functionality =In which function the store file is read from the file &
	 * check the conditions. If the conditions are matched than put in a ArrayList.
	 * 
	 * @param ArrayList
	 * 
	 * @return
	 */

	public static ArrayList<String> readstorefile(String path) throws IOException {
		File file = new File(path);
		BufferedReader br = null;
		try {
			br = new BufferedReader(new FileReader(file));
			String line;

			while ((line = br.readLine()) != null) {
				String s = line.substring(1, 6) + line.substring(20, 26) + line.substring(78, 81);
				String check = s.substring(5, 11);
				if (check.equals("009231") || check.equals("009232") || check.equals("009233") || check.equals("009234")
						|| check.equals("009235") || check.equals("009236") || check.equals("009237")
						|| check.equals("009238") || check.equals("009239") || s.substring(11, 14).equals("TGI")) {
					store_data.add(s);
				}
			}

		} catch (Exception e) {
		} finally {
			br.close();
		}
		return store_data;
	}

	static int count = 0;
	static int k = 0;

	public void get_segments(Map<String, String> map, ArrayList<String> storedata)
			throws JsonParseException, JsonMappingException, IOException {
		ArrayList<String> equalmatch = new ArrayList<String>();
		B1TransactionHeaderRecordSegment b1transaction = null;
		ObjectMapper mapper = new ObjectMapper();
		for (Map.Entry<String, String> entry1 : map.entrySet()) {
			if (entry1.getKey().substring(0, 2).equals("C1")) {
				count++;
			}
		}
		for (Map.Entry<String, String> entry : map.entrySet()) {
			switch (entry.getKey().substring(0, 2)) {
			case "B1": {
				String jsonString = mapper.writeValueAsString(entry.getValue());
				b1transaction = mapper.readValue(jsonString, B1TransactionHeaderRecordSegment.class);
				break;
			}
			case "C1": {
				k++;
				String jsonString = mapper.writeValueAsString(entry.getValue());
				C1LineItemSegment c1LineItem = mapper.readValue(jsonString, C1LineItemSegment.class);
				Imple.Main_para_2000(equalmatch, storedata, b1transaction, c1LineItem);
				break;
			}
			}
		}
	}

	// 2100 main
	// 3000
	static void set3000(ArrayList<String> equalmatch, ArrayList<String> data,
			B1TransactionHeaderRecordSegment b1transaction1, C1LineItemSegment c1LineItem1)
			throws JsonParseException, JsonMappingException, IOException {

		B1TransactionHeaderRecordSegment b1transaction = b1transaction1;
		C1LineItemSegment C1LineItem = c1LineItem1;
		if (C1LineItem.getLineItemTypeCode1().equals("1") || C1LineItem.getLineItemTypeCode1().equals("4")) {
			if (C1LineItem.getLineItemTypeCode2().equals("2") || C1LineItem.getLineItemTypeCode2().equals("3")
					|| C1LineItem.getLineItemTypeCode2().equals("4")) {
				if (C1LineItem.getLineItemReasonCode().equals("04")
						|| C1LineItem.getLineItemReasonCode().equals("05")) {

					fx624.setSTORE(b1transaction.getStoreNumber());
					fx624.setDIV(C1LineItem.getDivisionNumber());
					fx624.setPRICE(c1LineItem1.getRegularPrice());
					fx624.setSalescheck_STR(b1transaction.getStoreNumber());
					fx624.setSalescheck_REG(b1transaction.getRegisterNumber());
					fx624.setSalescheck_TRANS(b1transaction.getTransactionNumber());
					fx624.setYYYY(b1transaction.getTransactionDate().substring(0, 4));
					fx624.setMM(b1transaction.getTransactionDate().substring(5, 7));
					fx624.setSLS1("/");
					fx624.setDD(b1transaction.getTransactionDate().substring(8, 10));
					fx624.setSLS2("/");

					equalmatch.add(fx624.toString());
					// writeFile(fx624.toString());
				}
			}
		}
		if (k == count && equalmatch.size() == 1) {
			// System.out.println("output is"+fx624.toString());

			writeFile(fx624.toString());
			k = 0;
			count = 0;
		} else if (equalmatch.size() >= 2) {
			checkdigit(equalmatch);
		} else {
			count--;
			k--;
		}

	}

	public static void checkdigit(ArrayList<String> equalmatch) throws IOException {
		boolean flag = false;
		String s1 = "";
		String s5 = "";
		String s = "";

//System.out.println("size is"+equalmatch.size());
		for (int i = 0; i < equalmatch.size(); i++) {
			for (int j = i + 1; j < equalmatch.size(); j++) {
				if (equalmatch.get(i).substring(15, 23).equals(equalmatch.get(j).substring(15, 23))) {
					flag = true;
					int a = Integer.parseInt(equalmatch.get(i).substring(8, 15))
							+ Integer.parseInt(equalmatch.get(j).substring(8, 15));
					s5 = Integer.toString(a);
					s = ("0000000" + s5).substring(s5.length());
					s1 = equalmatch.get(i).substring(0, 8) + s + equalmatch.get(i).substring(15);
					// System.out.println("Output is "+s1);
					writeFile(s1);
					k--;
					count--;
					break;
				}
			}

			if (!flag) {
				// System.out.println("output is "+equalmatch.get(i));
				writeFile(equalmatch.get(i));
			}
		}

	}

	// System.out.println("equalmatch" + equalmatch.size());
	// }

	public static void Main_para_2000(ArrayList<String> equalmatch, ArrayList<String> data,
			B1TransactionHeaderRecordSegment b1transaction1, C1LineItemSegment c1LineItem)
			throws JsonParseException, JsonMappingException, IOException {
		try {
			B1TransactionHeaderRecordSegment b1transaction = b1transaction1;
			String store_curr_unit = "     ";
			String prev_unit = "     ";
			store_curr_unit = b1transaction.getStoreNumber();
			if (store_curr_unit != prev_unit) {
				// perform 2100
				for (int i = 0; i < data.size(); i++) {
					String datastore = data.get(i);
					if (datastore.substring(0, 5).equals(store_curr_unit)) {
						storeontable = true;
					}
				}
			}
			if (storeontable) {
				if ((Integer.parseInt(b1transaction.getTransactionErrorCode()) > 00
						&& Integer.parseInt(b1transaction.getTransactionErrorCode()) < 85)
						|| b1transaction.getTransactionErrorCode().equals("99")
						|| b1transaction.getTransactionStatusCode().equals("3")
						|| b1transaction.getTransactionStatusCode().equals("4")) {
					return;
				}
				if (b1transaction.getTransactionTypeCode().equals("1")
						|| b1transaction.getTransactionTypeCode().equals("5")
						|| b1transaction.getTransactionTypeCode().equals("7")
						|| b1transaction.getTransactionTypeCode().equals("8")
						|| b1transaction.getTransactionTypeCode().equals("6")) {

					// in 2100
					// perform 3000
					set3000(equalmatch, data, b1transaction1, c1LineItem);
					// continue;
				} else {
					return;
				}

			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public static void writeFile(String outputRecord) throws IOException {
		try {
			FileWriter fileW = new FileWriter("C:/Users/aashish/Desktop/NFX624Output.txt", true);
			BufferedWriter b = new BufferedWriter(fileW);
			b.newLine();
			PrintWriter pw = new PrintWriter(b);
			if (outputRecord != "")
				pw.print(outputRecord);
			pw.close();
			fileW.close();
			b.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
