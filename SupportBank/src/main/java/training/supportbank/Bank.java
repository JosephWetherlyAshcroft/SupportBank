package training.supportbank;

import com.opencsv.CSVReader;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.time.Month;
import java.util.Iterator;

import java.io.FileNotFoundException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.*;

public class Bank {
    private static Bank instance;
    private static int transactionNum = 0;

    private static HashMap<String, Float> accountValueMap = new HashMap<String, Float>();
    private static ArrayList<Transaction> transactions = new ArrayList<>();

    public static ArrayList<Transaction> getTransactions() {
        return transactions;
    }

    public static Bank getInstance() {
        if (instance == null) {
            instance = new Bank();
        }
        return instance;
    }

    public static HashMap<String, Float> getAccountValueMap() {
        return accountValueMap;
    }

    public static void populateBank() throws IOException, ParseException, ParserConfigurationException, SAXException {
        processXMLFileData("Transactions2012.xml");
        processJSONFileData("Transactions2013.json");
        processCSVFileData("Transactions2014.csv");
        processCSVFileData("DodgyTransactions2015.csv");
    }

    private static void processCSVFileData(String fileName) throws FileNotFoundException {
        boolean IgnoreFirstLine = true;
        int id = 0;
        CSVReader reader = new CSVReader(new FileReader(fileName));

        DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");

        for (Iterator<String[]> it = reader.iterator(); it.hasNext(); ) {
            String[] nextLine = it.next();
            if (IgnoreFirstLine) {
                IgnoreFirstLine = false;
            } else {
                if (nextLine[4].matches("^-?\\d+(\\.\\d+)?$")) {
                    if (nextLine[0].matches("^\\d{2}/\\d{2}/\\d{4}$")) {
                        LocalDate parsedLocalDate = LocalDate.parse(nextLine[0], dateFormatter);
                        if (parsedLocalDate.isBefore(LocalDate.now())) {
                            transaction(nextLine[1], nextLine[2], Float.parseFloat(nextLine[4]));
                            transactions.add(transactionNum, new Transaction("CSV" + id, nextLine[0], nextLine[1], nextLine[2], nextLine[3], nextLine[4]));
                            transactionNum++;
                            id++;
                        }
                    } else {
                        System.out.println(nextLine[0]);
                    }
                } else {
                    System.out.println(nextLine[4]);
                }
            }
        }
    }

    private static void processJSONFileData(String fileName) throws IOException, ParseException {
        boolean IgnoreFirstLine = true;
        DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");

        JSONParser parser = new JSONParser();
        Object obj = parser.parse(new FileReader(fileName));
        JSONArray jsonArray = (JSONArray) obj;


        for (int i = 0; i < jsonArray.size(); i++){
            JSONObject currentTransaction = (JSONObject) jsonArray.get(i);

            String date = currentTransaction.get("date").toString();
            String from = currentTransaction.get("fromAccount").toString();
            String to = currentTransaction.get("toAccount").toString();
            String description = currentTransaction.get("narrative").toString();
            String amount = currentTransaction.get("amount").toString();

            if (IgnoreFirstLine) {
                IgnoreFirstLine = false;
            } else {
                if (amount.matches("^-?\\d+(\\.\\d+)?$")) {
                    if (date.toString().matches("^\\d{4}-\\d{2}-\\d{2}$")) {
                        LocalDate parsedLocalDate = LocalDate.parse(date, dateFormatter);
                        if (parsedLocalDate.isBefore(LocalDate.now())) {
                            transaction(from, to, Float.parseFloat(amount));
                            transactions.add(transactionNum, new Transaction("JSON" + i, date, from, to, description, amount));
                            transactionNum++;
                        }
                    } else {
                        System.out.println(date);
                    }
                } else {
                    System.out.println(amount);
                }
            }
        }
    }

    private static void processXMLFileData(String fileName) throws ParserConfigurationException, IOException, SAXException {

        File fXmlFile = new File(fileName);
        DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
        DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
        Document doc = dBuilder.parse(fXmlFile);
        doc.getDocumentElement().normalize();

        NodeList nList = doc.getElementsByTagName("SupportTransaction");

        for (int i = 0; i < nList.getLength(); i++) {

            Node nNode = nList.item(i);

            if (nNode.getNodeType() == Node.ELEMENT_NODE) {

                Element eElement = (Element) nNode;

                LocalDate date = DateSystemConverter1900(Integer.parseInt(eElement.getAttribute("Date")));
                String description = eElement.getElementsByTagName("Description").item(0).getTextContent();
                String value = eElement.getElementsByTagName("Value").item(0).getTextContent();
                String from = eElement.getElementsByTagName("From").item(0).getTextContent();
                String to = eElement.getElementsByTagName("To").item(0).getTextContent();

                if (value.matches("^-?\\d+(\\.\\d+)?$")) {
                    if (date.toString().matches("^\\d{4}-\\d{2}-\\d{2}$")) {
                        if (date.isBefore(LocalDate.now())) {
                            transaction(from, to, Float.parseFloat(value));
                            transactions.add(transactionNum, new Transaction("XML" + i, date.toString(), from, to, description, value));
                            transactionNum++;
                        }
                    }
                    else {
                        System.out.println(date);
                    }
                }
                else {
                    System.out.println(value);
                }
            }
        }
    }

    private static void transaction(String accountHolderFrom, String accountHolderTo, Float value) {
        if (accountValueMap.containsKey(accountHolderFrom)) {
            accountValueMap.put(accountHolderFrom, accountValueMap.get(accountHolderFrom) - value);
        } else {
            accountValueMap.put(accountHolderFrom, -value);
        }
        if (accountValueMap.containsKey(accountHolderTo)) {
            accountValueMap.put(accountHolderTo, accountValueMap.get(accountHolderTo) + value);
        } else {
            accountValueMap.put(accountHolderTo, value);
        }
    }

    private static LocalDate DateSystemConverter1900(Integer dateToBeConverted){
        LocalDate startDate = LocalDate.of(1900, Month.JANUARY, 1);
        LocalDate newDate = startDate.plusDays(dateToBeConverted);
        return newDate;
    }

    private static void detectTransactionFileType(String fileName) {

    }


}
