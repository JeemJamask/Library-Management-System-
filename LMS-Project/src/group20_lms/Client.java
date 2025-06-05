package group20_lms;

/*
GROUP 20:
Joud Jamal Alkishi > ID: 20xxxxx > SEC: EAR
Sara Abdulaziz Alzahrani > ID: 21xxxxx > SEC: VAR
Rayyanah Abdulkarim Alshehri > ID: 21xxxxx > SEC: VAR
Shahad Abdulaziz Salman > ID:21xxxxx > VAR
*/

import java.io.*;
import java.net.Socket;
import java.util.Scanner;

public class Client {

    public static void main(String[] args) throws IOException {

        // Create input and output streams
        BufferedReader bufferedReader;
        PrintWriter pw;

        // Create a socket connection to the server
        Socket serverSocket = new Socket("localhost", 1091);

        // Initialize input and output streams with socket streams
        bufferedReader = new BufferedReader(new InputStreamReader(serverSocket.getInputStream()));
        pw = new PrintWriter(new OutputStreamWriter(serverSocket.getOutputStream()), true);

        // Create a scanner to read user input
        Scanner user = new Scanner(System.in);

        // Display welcome message from the server
        System.out.println(bufferedReader.readLine());
        System.out.println("");
        // Prompt the user for username
        System.out.println("\t       ◈━◈━ Please enter your username ━◈━◈");
        String username = user.nextLine();
        pw.println(username);

        // Prompt the user for password
        System.out.println("\t       ◈━◈━ Please enter your password ━◈━◈");
        String password = user.nextLine();
        pw.println(password);

        System.out.println("\n✦•·······························•✦•·······························•✦");
        // Read the server response 'User role'
        String serverResponse = bufferedReader.readLine();
        System.out.println(serverResponse);
        System.out.println("✦•·······························•✦•·······························•✦");
        if (serverResponse.contains("member")) {

            user.nextLine();
            // Read the length string sent by the server
            String lengthString = bufferedReader.readLine();

            // Parse the length string into an integer
            int numOfBorrowed = Integer.parseInt(lengthString);

            // Notification for return date of client if he borrowed a book.
            if (numOfBorrowed >= 1) {
                // notifi
                serverResponse = bufferedReader.readLine();
                System.out.println(serverResponse);
                //return dates
                for (int i = 0; i < numOfBorrowed; i++) {
                    serverResponse = bufferedReader.readLine();
                    System.out.println(serverResponse);
                }
            }

            // Read the length string sent by the server
            String lengthStringAva = bufferedReader.readLine();

            // Parse the length string into an integer
            Integer numOfAva = Integer.parseInt(lengthStringAva);

            // Notification for availability if client requested a book
            if (numOfAva >= 1) { //2
                // Notification: The following requested books are now available: 
                serverResponse = bufferedReader.readLine();
                System.out.println(serverResponse);
                // Available books list
                for (int i = 0; i < numOfAva; i++) {

                    serverResponse = bufferedReader.readLine();
                    System.out.println(serverResponse);

                }
                // Browse
                System.out.println("");
                serverResponse = bufferedReader.readLine();
                System.out.println(serverResponse);

            } else {   // Browse
                System.out.println("");
                serverResponse = bufferedReader.readLine();
                System.out.println(serverResponse);

            }

            int selection = Integer.parseInt(user.nextLine());
            pw.println(selection);
            System.out.println("");

            switch (selection) {
                case 1:

                    serverResponse = bufferedReader.readLine();
                    System.out.println(serverResponse);
                    int numberOfBooks = Integer.parseInt(bufferedReader.readLine());

                    for (int i = 0; i < numberOfBooks; i++) {
                        serverResponse = bufferedReader.readLine();
                        System.out.println(serverResponse);
                    }

                    System.out.println("");
                    // Would you like to borrow a Book?  
                    serverResponse = bufferedReader.readLine();
                    System.out.println(serverResponse);

                    // yes
                    String answer = user.nextLine();
                    pw.println(answer);

                    String answer2 = "";
                    if (answer.equalsIgnoreCase("Yes")) {

                        System.out.println("");

                        // Write a book name to borrow:
                        serverResponse = bufferedReader.readLine();
                        System.out.println(serverResponse);

                        // send the book name ex: "The ABC Murders"
                        String bn = user.nextLine();
                        pw.println(bn);

                        System.out.println("");
                        // You borrowed : The ABC Murders | Book is not available
                        serverResponse = bufferedReader.readLine();
                        System.out.println(serverResponse);
                        System.out.println("");

                        // * THE BORROWED BOOK SHOULD BE RETURNED WITHIN 24 HOURS * 
                        serverResponse = bufferedReader.readLine();
                        System.out.println(serverResponse);

                        if (serverResponse.equalsIgnoreCase("➤ Would you like to be notified when the book is in stock?")) {
                            answer2 = user.nextLine();
                            pw.println(answer2);
                            System.out.println("");
                        }
                        if (answer2.equalsIgnoreCase("Yes")) {
                            //System.out.println("࿄ User chose to be notified ࿄");
                            // You will be notified when the book is in stock
                            serverResponse = bufferedReader.readLine();
                            System.out.println(serverResponse);
                            break;
                        } else if (answer2.equals("no")) {
                            //System.out.println("࿄ User chose NOT to be notified ࿄");
                            serverResponse = bufferedReader.readLine();
                            System.out.println(serverResponse);
                            break;
                        }

                    } else {
                        serverResponse = bufferedReader.readLine();
                        System.out.println(serverResponse);
                        break;
                    }
                    break;

                case 2:
                    // Write a book name to return
                    serverResponse = bufferedReader.readLine();
                    System.out.println(serverResponse);

                    // send the book name
                    String bn = user.nextLine();
                    pw.println(bn);

                    // You Returned :  
                    serverResponse = bufferedReader.readLine();
                    System.out.println(serverResponse);

                    break;

                default:
                    System.out.println("Invalid Option ✘");
                    break;
            }

        } else if (serverResponse.contains("librarian")) {
            user.nextLine();
            System.out.println("");
            serverResponse = bufferedReader.readLine();
            System.out.println(serverResponse);

            int selection = Integer.parseInt(user.nextLine());
            pw.println(selection);
            System.out.println("");

            switch (selection) {
                case 1: {

                    // Enter book title msg
                    serverResponse = bufferedReader.readLine();
                    System.out.println(serverResponse);
                    // Input of title
                    String newBookTitle = user.nextLine();
                    pw.println(newBookTitle);
                    System.out.println("");

                    // Enter no. of copies msg
                    serverResponse = bufferedReader.readLine();
                    System.out.println(serverResponse);
                    // Input no. of copies
                    int newBookCopies = Integer.parseInt(user.nextLine());
                    pw.println(newBookCopies);
                    System.out.println("");
                    pw.flush();
                    break;
                }
                case 2: {

                    // Enter book title to update msg
                    serverResponse = bufferedReader.readLine();
                    System.out.println(serverResponse);
                    // Input book title
                    String updateBookTitle = user.nextLine();
                    pw.println(updateBookTitle);
                    System.out.println("");

                    // Enter new no. of copies msg
                    serverResponse = bufferedReader.readLine();
                    System.out.println(serverResponse);
                    // Input no. of copies
                    int updateBookCopies = Integer.parseInt(user.nextLine());
                    pw.println(updateBookCopies);
                    System.out.println("");
                    pw.flush();
                    break;
                }
                case 3: {
                    // Enter book title to delete msg
                    serverResponse = bufferedReader.readLine();
                    System.out.println(serverResponse);
                    // Input of book title
                    String deleteBookTitle = user.nextLine();
                    pw.println(deleteBookTitle);
                    System.out.println("");
                    pw.flush();
                    break;
                }
                default: {
                    System.out.println("Invalid Option ✘");
                    break;
                }
            }

            // Receive and print the response from the server
            serverResponse = bufferedReader.readLine();
            System.out.println(serverResponse);
        }
    }
    
    
    
    
    public static void Member(int selection){
        
        
        
        
        
    }

}
