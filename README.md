
# 📚 Library Management System (LMS) — Client-Server Java App

A Java-based Library Management System (LMS) developed using a client-server architecture as part of the CPCS371 Networks Project at King Abdulaziz University.

## 🛠️ Project Overview

This LMS enables members and librarians to manage library resources through a TCP-based client-server application. Librarians can add, update, and delete books, while members can browse, borrow, and return them. The system also features real-time updates and personalized notifications.

## 👩‍💻 Team Members

- **Joud Jamal Alkishi**
- **Sara Abdulaziz Alzahrani**
- **Rayyanah Abdulkarim Alshehri**
- **Shahad Abdulaziz Salman**

## 🧩 Features

### 👨‍🏫 Librarian
- Add new books
- Update existing book details
- Delete books from the library
- Receive confirmation of successful operations

### 📖 Member
- Browse available books
- Borrow books (limited to 24 hours)
- Return borrowed books
- Receive notifications for:
  - Due return dates
  - Availability of requested books

## ⚙️ Architecture

- **Client Side:** Java application with interactive CLI for user roles (Member/Librarian).
- **Server Side:** Java multi-threaded server handling business logic and communication.
- **Communication:** TCP protocol for reliable data transmission.
- **Storage:** File-based system storing user credentials and book data.

## 🧪 Test Cases

Several test cases were developed and executed including:
- Member: Browse, borrow, and return books
- Librarian: Add, update, and delete books
- Notification scenarios for borrowed and requested books

## 📂 File Structure

```plaintext
├── src/
│   ├── Server.java
│   ├── Client.java
│   └── *.txt (data files for books and users)
├── README.md
└── Group20_LMSReport.pdf
```

## 🚀 How to Run

### 1. Start the Server
```bash
javac Server.java
java Server
```

### 2. Start the Client
```bash
javac Client.java
java Client
```

> ⚠️ Make sure to run the server first before launching any client.

## 🔐 Login Credentials

Sample login credentials are stored in the users file (plaintext). The system differentiates between **librarian** and **member** roles based on usernames.

## 📈 Future Enhancements

- Mobile app support (Android/iOS)
- Advanced search and book recommendation system
- Modern graphical user interface (GUI)
- Role-based web interface

## 📄 Report

Full documentation is available in [`Group20_LMSReport.pdf`](./Group20_LMSReport.pdf), including:
- Architecture diagrams
- Implementation details
- User guide
- Test results
- Future improvement plans
