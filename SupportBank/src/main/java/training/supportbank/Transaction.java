package training.supportbank;


public class Transaction {
    private String id;
    private String date;
    private String sender;
    private String receiver;
    private String description;
    private String value;

    Transaction(String ID, String Date, String Sender, String Receiver, String Description, String Value){
        id = ID;
        date = Date;
        sender = Sender;
        receiver = Receiver;
        description = Description;
        value = Value;
    }

    public String getDate() {
        return date;
    }

    public String getDescription() {
        return description;
    }

    public String getValue() {
        return value;
    }

    public String getId() {
        return id;
    }

    public String getReceiver() {
        return receiver;
    }

    public String getSender() {
        return sender;
    }

}
