package training.supportbank;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.json.simple.parser.ParseException;
import org.xml.sax.SAXException;

import javax.xml.parsers.ParserConfigurationException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Scanner;


public class Main {
    private static Logger LOGGER = LogManager.getLogger();

    public static void main(String args[]) throws IOException, ParseException, ParserConfigurationException, SAXException {
        LOGGER.debug("Entering application.");
        userMenuLoop(setUp());
        LOGGER.debug("Exiting application.");
    }

    private static void userMenuLoop(Bank bank){
        Scanner sc = new Scanner(System.in);
        boolean choiceIsOK = false;
        do{
            System.out.println("Welcome To The Bank, What would you like to Check:\nAccount Balance (1)\nAccount Transactions (2)\nQuit (3)");
            String userChoice = sc.nextLine();
            switch (userChoice) {
                case "1":
                    accountBalance(bank.getAccountValueMap());
                    break;
                case "2":
                    accountTransactions(bank.getTransactions(), bank.getAccountValueMap());
                    break;
                case "3":
                    System.out.println("Thank you for using the bank");
                    choiceIsOK = true;
                    break;
                case "\n":
                default:
                    System.out.println("Invalid input, Try again");
            }
        }while(!choiceIsOK);
    }

    private static Bank setUp() throws IOException, ParseException, ParserConfigurationException, SAXException {
        Bank bank = Bank.getInstance();
        bank.populateBank();
        return bank;
    }

    private static void accountBalance(HashMap<String, Float> balanceHashMap){
        boolean choiceIsOk = false;
        String userName = "";
        Scanner sc = new Scanner(System.in);
        do{
            System.out.println("Please enter your name (Firstname Initial) or back");
            userName = sc.nextLine();
            if (balanceHashMap.containsKey(userName)){
                System.out.println(balanceHashMap.get(userName));
                choiceIsOk = true;
            }
            else if (userName.toLowerCase().equals("back")){
                choiceIsOk = true;
            }
            else {
                System.out.println("Invalid input or account doesnt exist, Try again");
            }
        }while (!choiceIsOk);
    }

    private static void accountTransactions(ArrayList<Transaction> transactionArray, HashMap<String, Float> accountHashMap){
        boolean choiceIsOk = false;
        String userName = "";
        Scanner sc = new Scanner(System.in);
        do{
            System.out.println("Please enter your name (Firstname Initial) or back");
            userName = sc.nextLine();
            if (accountHashMap.containsKey(userName)){
                for (Transaction t :transactionArray) {
                    if(t.getSender().equals(userName)){
                        System.out.println("\nID: " + t.getId() + "\nDate: " + t.getDate() + "\nSent To: " + t.getReceiver() + "\nFor: " + t.getDescription() + "\nValue: " + t.getValue() + "\n");
                    }
                    if(t.getReceiver().equals(userName)){
                        System.out.println("\nID: " + t.getId() + "\nDate: " + t.getDate() + "\nReceived from: " + t.getSender() + "\nFor: " + t.getDescription() + "\nValue: " + t.getValue() + "\n");
                    }
                }
                choiceIsOk = true;
            }
            else if (userName.toLowerCase().equals("back")){
                choiceIsOk = true;
            }
            else {
                System.out.println("Invalid input or account doesnt exist, Try again");
            }
        }while (!choiceIsOk);
    }

}