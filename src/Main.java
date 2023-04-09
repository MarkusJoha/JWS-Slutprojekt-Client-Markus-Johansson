import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.io.*;
import java.net.*;
import java.util.Scanner;
import java.util.Set;

public class Main {
    public static void main(String[] args) {
        System.out.println("Klienten är redo!");

        //Initialiserar saker och sätter som "null"
        Socket socket = null;
        InputStreamReader inputStreamReader = null;
        OutputStreamWriter outputStreamWriter = null;
        BufferedReader bufferedReader = null;
        BufferedWriter bufferedWriter = null;

        //Startar klienten
        try {
            //Initierar socket med specifik port
            socket = new Socket("localhost", 7648);

            //Initierar Reader och Writer och kopplar dem till socket
            inputStreamReader = new InputStreamReader(socket.getInputStream());
            outputStreamWriter = new OutputStreamWriter(socket.getOutputStream());
            bufferedReader = new BufferedReader(inputStreamReader);
            bufferedWriter = new BufferedWriter(outputStreamWriter);

            while (true) {
                //Anropar meny för användare, låter dem göra ett val
                //Valet returneras som en färdig JSON string
                String message = userInput();

                if (message.equals("quit")) break;

                //Skicka meddelande till server
                bufferedWriter.write(message);
                bufferedWriter.newLine();
                bufferedWriter.flush();

                //Hämtar responsen från server
                String resp = bufferedReader.readLine();

                openResponse(resp, message);


            }
        } catch (UnknownHostException e) {
            System.out.println(e);
        } catch (IOException e) {
            System.out.println(e);
        } catch (ParseException e) {
            System.out.println(e);
        } finally {
            try {
                //Stänger kopplingar
                if (socket != null) inputStreamReader.close();
                if (inputStreamReader != null) outputStreamWriter.close();
                if (bufferedReader != null) bufferedReader.close();
                if (bufferedWriter != null) bufferedWriter.close();
            } catch (Exception e) {
                System.out.println(e);
            }
            System.out.println("Klienten Avslutas!");
        }
    }
    static String userInput() {
        //Skriver ut en meny för användaren
        System.out.println("1. Hämta all data i JSON String");
        System.out.println("2. Hämta namn.");
        System.out.println("3. Hämta level.");
        System.out.println("4. Hämta klass.");
        System.out.println("5. Hämta ras.");

        //Låter användaren göra ett val
        Scanner scan = new Scanner(System.in);
        System.out.print("Skriv in ditt menyval: ");

        String userChoice = scan.nextLine();
        String choice = userChoice.toLowerCase();

        //Bearbetar användarens val
        JSONObject jsonReturn = new JSONObject();
        jsonReturn.put("httpURL", choice);
        jsonReturn.put("httpMethod", "get");

        return jsonReturn.toJSONString();
    }

    static String openResponse(String resp, String message) throws ParseException {
        //Skriver ut responsen från Servern
        //System.out.println(message);

        //Initierar Parser för att parsa till JSON objekt
        JSONParser parser = new JSONParser();

        //Skapar ett JSON objekt från server respons
        JSONObject jsonOb = (JSONObject) parser.parse(message);
        String messageString = jsonOb.get("httpURL").toString();
        String[] cutMsgString = messageString.split("/");
        String who = cutMsgString[0];

        String stringResponse = "";

        JSONObject serverResponse = (JSONObject) parser.parse(resp);

        //Kollar om respons lyckades
        if ("200".equals(serverResponse.get("httpStatusCode").toString())) {
            //Bygger upp ett JSON objekt av den returnerade datan
            JSONObject data = (JSONObject) parser.parse((String) serverResponse.get("data"));

            //Hämtar en lista av alla nycklar attribut i data och loopar sedan igenom dem
            Set<String> keys = data.keySet();
            for (String x : keys) {
                //Hämtar varje person object som finns i data
                JSONObject person = (JSONObject) data.get(x);

                if ("1".equals(who)) {
                    System.out.println(data);
                } else if ("2".equals(who)) {
                    System.out.println("Name:" + person.get("name"));
                } else if ("3".equals(who)) {
                    System.out.println("Level: " + person.get("level"));
                } else if ("4".equals(who)) {
                    System.out.println("Class: " + person.get("class"));
                } else if ("5".equals(who)) {
                    System.out.println("Race: " + person.get("race"));
                }
            }
        }

        return stringResponse;
    }
}