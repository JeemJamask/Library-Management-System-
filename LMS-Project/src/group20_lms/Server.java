package group20_lms;

/*
GROUP 20:
Joud Jamal Alkishi > ID: 20xxxxx > SEC: EAR
Sara Abdulaziz Alzahrani > ID: 21xxxxx > SEC: VAR
Rayyanah Abdulkarim Alshehri > ID: 21xxxxx > SEC: VAR
Shahad Abdulaziz Salman > ID:21xxxxx > VAR
*/

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class Server {

    // Users and Books data
    private static Map<String, String> users = new HashMap<>();
    private static Map<String, String> userRoles = new HashMap<>();
    private static Map<String, Integer> books = new HashMap<>();
    private static ArrayList<String> requestedBooks1 = new ArrayList<String>();
    private static ArrayList<String> requestedBooks2 = new ArrayList<String>();
    private static ArrayList<String> BooksBorrowed1 = new ArrayList<String>();
    private static ArrayList<String> BooksBorrowed2 = new ArrayList<String>();
    private static ArrayList<String> returnedBooks = new ArrayList<String>();

    public static void main(String[] args) throws IOException {
        ServerSocket serverSocket = new ServerSocket(1091);
        System.out.println("◉ ✓ Server on...\n");

        readUsers("users.txt");
        readBooks("books.txt");

        while (true) {
            System.out.println("◉ Server is waiting for a connection.");

            // accept incoming client connections
            Socket clientSocket = serverSocket.accept();

            // indicate successful client connection
            System.out.println("◉ ✓ Client Successfully Connected!");

            // handle client connection in a separate thread
            new ClientHandler(clientSocket).start();
        }
    }

    // Inner class to handle each client connection separately
    private static class ClientHandler extends Thread {

        private Socket clientSocket;

        public ClientHandler(Socket socket) {
            this.clientSocket = socket;
        }

        public void run() {

            try (BufferedReader in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
                    PrintWriter out = new PrintWriter(clientSocket.getOutputStream(), true)) {

                // Print msg message
                out.println(" ╔═══════════════════════ ꕥ WELCOME TO LMS ꕥ ═══════════════════════╗");
                // Authenticate user
                String username = in.readLine();
                String password = in.readLine();

                // Check user info from database
                if (users.containsKey(username) && users.get(username).equals(password)) {
                    String role = userRoles.get(username);
                    out.println("You are authenticated as a " + role + "! Please press enter to go to the menu.");

                    // If user is Librarian > display librarian menu 
                    if ("librarian".equalsIgnoreCase(role)) {
                        out.println("【 1. Add a new book ┊ 2. Update book information ┊ 3. Delete a book 】");
                        int option = Integer.parseInt(in.readLine());
                        switch (option) {
                            case 1:
                                out.println("➤ Enter book title:");
                                String title = in.readLine();
                                out.println("➤ Enter number of copies:");
                                int copies = Integer.parseInt(in.readLine());
                                AddBook(out, in, "books.txt", title, copies);
                                break;
                            case 2:
                                UpdateBook(out, in);
                                break;
                            case 3:
                                DeleteBook(out, in);
                                break;
                            default:
                                out.println("Invalid option.");
                                break;
                        }

                        // If user is Member > display member menu 
                    } else if ("member".equalsIgnoreCase(role)) {

                        // Send return date notification for user if he borrowed a book
                        sendNotifications(out,username);
                        // Send notification for availability of requested book if user chose to be notified
                        requestedBooks(out, in, returnedBooks, username);

                        out.println("\t【 1. Browse and Borrow Books ┊ 2. Return Books 】");
                        // Prompt Member to enter option
                        int option = Integer.parseInt(in.readLine());

                        // Cases of member
                        switch (option) {
                            // Browse & Borrow book
                            case 1:
                                out.println("► List of Books available ↴");
                                BrowseBook(out, in);
                                out.println("➤ Would you like to borrow a Book? ");
                                String optionBorrow = in.readLine();
                                if (optionBorrow.equals("yes")) {
                                    out.println("➤ Write a book name to borrow: ");
                                    String bookToBorrow = in.readLine();
                                    BorrowBook(out, in, bookToBorrow, username);
                                } else {
                                    out.println("Understood, Have a nice day! ");
                                    break;

                                }

                            // Return book
                            case 2:
                                out.println("➤ Write a book name to return: ");
                                String bookToReturn = in.readLine();
                                ReturnBook(out, in, bookToReturn, username);
                                // Return method
                                break;
                            default:
                                out.println("Invalid option.");
                                break;
                        }
                    }
                } // If user info is not on the database
                else {
                    out.println("Authentication failed.");
                }

            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        // AddBook: a librarian method, to add new book to the database.
        private void AddBook(PrintWriter out, BufferedReader in, String filePath, String title, int copies) throws IOException {
            BufferedWriter bw = new BufferedWriter(new FileWriter(filePath, true));
            if (!books.containsKey(title)) {
                bw.write(title + " " + copies);
                bw.newLine();
                bw.flush();
                books.put(title, copies);
                out.println("✔ Book added successfully ✔");
            } else {
                out.println("Book already exists.");
            }
        }

        // UpdateBook: a librarian method, to update book info > num of copies.
        private void UpdateBook(PrintWriter out, BufferedReader in) throws IOException {
            out.println("➤ Enter book title to update:");
            String title = in.readLine();

            if (books.containsKey(title)) {
                out.println("➤ Enter new number of copies:");
                int newCopies = Integer.parseInt(in.readLine());

                books.put(title, newCopies);

                rewriteBooksToFile("books.txt");

                out.println("✔ Book updated successfully ✔");
            } else {
                out.println("Book not found.");
            }
        }

        // DeleteBook: a librarian method, to delete a specific book.
        private void DeleteBook(PrintWriter out, BufferedReader in) throws IOException {
            out.println("➤ Enter book title to delete:");
            String title = in.readLine();

            if (books.containsKey(title)) {
                // Remove the book from the in-memory map
                books.remove(title);

                // Update the books.txt file to reflect the deletion
                rewriteBooksToFile("books.txt");

                out.println("✔ Book deleted successfully ✔");
            } else {
                out.println("Book not found.");
            }
        }

        // BrowseBook: a member method, to display a list of available books and browse them.
        private void BrowseBook(PrintWriter out, BufferedReader in) throws IOException {
            int count = 0;
            for (Map.Entry<String, Integer> entry : books.entrySet()) {
                if (entry.getValue() != 0) {
                    count++;
                }
            }

            out.println(count);  // Send the number of books to the client
            for (Map.Entry<String, Integer> entry : books.entrySet()) {
                if (entry.getValue() != 0) {
                    out.println("• Title: " + entry.getKey() + " - Copies: " + entry.getValue());
                }
            }
        }

        // requestedBooks: a member method, to send notification for availability of requested books.
        private void requestedBooks(PrintWriter out, BufferedReader in, ArrayList<String> returnedBooks, String username) throws IOException {
            ArrayList<String> availableBooks1 = new ArrayList<>();
            ArrayList<String> availableBooks2 = new ArrayList<>();
            int countAvailable1 = 0; // Correctly count the number of borrowed books
            int countAvailable2 = 0;

            if (username.equals("Sherlock")) {
                for (String book : returnedBooks) {
                    if (requestedBooks1.contains(book)) {
                        availableBooks1.add(book);
                        countAvailable1++;
                    }
                }
                // Send the count of available books to the client
                out.println(countAvailable1);

                // Notify the user about the availability of requested books
                if (countAvailable1 > 0) {
                    out.println("࿄ Notification ➤ The following requested books are now available:");
                    for (String availableBook : availableBooks1) {
                        out.println(availableBook);
                    }
                    // Clear the list of requested books since they have been notified
                    requestedBooks1.removeAll(availableBooks1);
                }
            } else {
                for (String book : returnedBooks) {
                    if (requestedBooks2.contains(book)) {
                        availableBooks2.add(book);
                        countAvailable2++;
                    }
                }
                // Send the count of available books to the client
                out.println(countAvailable2);

                // Notify the user about the availability of requested books
                if (countAvailable2 > 0) {
                    out.println("࿄ Notification ➤ The following requested books are now available:");
                    for (String availableBook : availableBooks2) {
                        out.println(availableBook);
                    }
                    // Clear the list of requested books since they have been notified
                    requestedBooks2.removeAll(availableBooks2);
                }

            }
        }

        // BorrowBook: a member method, to borrow a book and to be notified if the book is not instock.
        private void BorrowBook(PrintWriter out, BufferedReader in, String bookToBorrow, String username) throws IOException {
            boolean bookFound = false;
            for (Map.Entry<String, Integer> entry : books.entrySet()) {
                if (entry.getKey().equalsIgnoreCase(bookToBorrow)) {
                    bookFound = true;
                    int copies = entry.getValue();
                    if (username.equals("Sherlock")) {
                        if (copies == 0) {
                            out.println("Book is not available ✘");
                            out.println("➤ Would you like to be notified when the book is in stock?");
                            String notifyUser = in.readLine();
                            if (notifyUser.equalsIgnoreCase("yes")) {
                                out.println("✔ You will be notified when the book is in stock ✔");
                                requestedBooks1.add(bookToBorrow);

                            }
                        } else {
                            books.put(entry.getKey(), copies - 1); // Update the number of copies
                            out.println("➤ You borrowed : " + entry.getKey());
                            out.println("ᯤ THE BORROWED BOOK SHOULD BE RETURNED WITHIN 24 HOURS ᯤ");
                            BooksBorrowed1.add(bookToBorrow);
                            break;
                        }
                    } else {
                        if (copies == 0) {
                            out.println("Book is not available ✘");
                            out.println("➤ Would you like to be notified when the book is in stock?");
                            String notifyUser = in.readLine();
                            if (notifyUser.equalsIgnoreCase("yes")) {
                                out.println("✔ You will be notified when the book is in stock ✔");
                                requestedBooks2.add(bookToBorrow);

                            }
                        } else {
                            books.put(entry.getKey(), copies - 1); // Update the number of copies
                            out.println("➤ You borrowed : " + entry.getKey());
                            out.println("ᯤ THE BORROWED BOOK SHOULD BE RETURNED WITHIN 24 HOURS ᯤ");
                            BooksBorrowed2.add(bookToBorrow);
                            break;
                        }

                    }
                    break; // Exit the loop once the book is found and processed
                }
            }
            rewriteBooksToFile("books.txt");
            if (!bookFound) {
                out.println("Book not found in the library ✘");
            }
        }

        // ReturnBook: a member method, to return books.
        private void ReturnBook(PrintWriter out, BufferedReader in, String bookToReturn, String username) throws IOException {
            boolean bookFound = false;
            for (Map.Entry<String, Integer> entry : books.entrySet()) {
                if (entry.getKey().equalsIgnoreCase(bookToReturn)) {
                    bookFound = true;
                    int copies = entry.getValue();
                    books.put(entry.getKey(), copies + 1); // Correctly update the number of copies
                    out.println("➤ You Returned : " + entry.getKey());
                    if (username.equals("Sherlock")) {
                        BooksBorrowed1.remove(bookToReturn);
                        returnedBooks.add(bookToReturn);
                    } else {
                        BooksBorrowed2.remove(bookToReturn);
                        returnedBooks.add(bookToReturn);
                    }
                    break;
                }
            }
            rewriteBooksToFile("books.txt"); // Move file writing outside the loop to avoid redundancy
            if (!bookFound) {
                out.println("Book not found in the library ✘");
            }
        }

        // sendNotifications: a member method, to send return date notification for user borrowed books.
        private void sendNotifications(PrintWriter out, String username) {

            if (username.equals("Sherlock")) {
                int counter1 = BooksBorrowed1.size(); // Count the number of borrowed books
                out.println(counter1); // Send the number of borrowed books to the client

                // Notification for borrowed books return dates
                if (!BooksBorrowed1.isEmpty()) {
                    out.println("࿄ Notification ➤ Your borrowed books return dates:");
                    for (String borrowedBook : BooksBorrowed1) {
                        String borrowedReturnDate = getReturnDateForBook(borrowedBook); // Use Date type
                        out.println(borrowedBook + " - Return Date: " + borrowedReturnDate);
                    }
                }
            } else {
                int counter2 = BooksBorrowed2.size(); // Count the number of borrowed books
                out.println(counter2); // Send the number of borrowed books to the client
                if (!BooksBorrowed2.isEmpty()) {
                    out.println("࿄ Notification ➤ Your borrowed books return dates:");
                    for (String borrowedBook : BooksBorrowed2) {
                        String borrowedReturnDate = getReturnDateForBook(borrowedBook); // Use Date type
                        out.println(borrowedBook + " - Return Date: " + borrowedReturnDate);
                    }
                }

            }

        }

        // Method to get return date for a borrowed book
        private String getReturnDateForBook(String bookName) {
            Calendar calendar = Calendar.getInstance();
            calendar.add(Calendar.HOUR_OF_DAY, 24); // 24 hours from now
            Date returnDate = calendar.getTime();
            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            String returnDateString = dateFormat.format(returnDate);

            return returnDateString;
        }

    }

    // Update database info
    private static void rewriteBooksToFile(String filePath) {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(filePath))) {
            writer.write("# Books Database");
            writer.newLine();
            writer.write("# Format: title, copies_available");
            writer.newLine();
            writer.newLine();

            for (Map.Entry<String, Integer> entry : books.entrySet()) {
                String title = entry.getKey();
                int copies = entry.getValue();
                writer.write(title + " " + copies);
                writer.newLine();
            }

            System.out.println("Books database updated successfully.");
        } catch (IOException e) {
            System.out.println("Error occurred while rewriting books database: " + e.getMessage());
        }
    }

    private static void readUsers(String filePath) throws FileNotFoundException, IOException {
        BufferedReader br = new BufferedReader(new FileReader(filePath));
        String line;
        while ((line = br.readLine()) != null) {
            line = line.trim();
            if (!line.startsWith("#") && !line.isEmpty()) {
                String[] parts = line.split("\\s+");
                String username = parts[0];
                String password = parts[1];
                String role = parts[2];

                users.put(username, password);
                userRoles.put(username, role);
            }
        }
    }

    private static void readBooks(String filePath) throws FileNotFoundException, IOException {
        BufferedReader br = new BufferedReader(new FileReader(filePath));
        String line;
        while ((line = br.readLine()) != null) {
            line = line.trim();
            if (!line.startsWith("#") && !line.isEmpty()) {
                String[] parts = line.split("\\s+");
                String title = String.join(" ", parts).replaceAll("\\s\\d+$", "");
                int copies = Integer.parseInt(parts[parts.length - 1]);
                books.put(title, copies);
            }
        }
    }

}
