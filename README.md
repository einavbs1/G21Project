# G21 Library Management System

A Java client-server library management application built for two user roles — **librarians** and **subscribers** — using a custom TCP messaging protocol, JavaFX desktop UIs, and a MySQL backend.

## Project Structure

This repository contains three Eclipse projects:

| Module | Description |
|---|---|
| [`G21_Client_Prototype`](G21_Client_Prototype) | JavaFX desktop client used by both librarians and subscribers |
| [`G21_Server_Prototype_HW2`](G21_Server_Prototype_HW2) | Server application: request routing, business logic, MySQL access, and an admin GUI for monitoring connections |
| [`OCSF`](OCSF) | Object Client-Server Framework — shared TCP socket networking layer used by both client and server |

## Architecture

- **Networking:** Client and server communicate over TCP sockets using OCSF (`AbstractClient` / `AbstractServer`). The client sends requests as a `HashMap<String, String>` whose key encodes the requested operation (e.g. `Subscriber+...`, `Book+...`); it then blocks until the server responds.
- **Server dispatch:** `EchoServer` parses the request key and routes it to the matching handler in `serverForHandleEntitiesRequests` (books, book copies, subscribers, librarians, borrowed records, orders, reminders, notifications, activity logs). Each handler executes SQL via classes in `queries/` against a MySQL database (`Server/mysqlConnection.java`).
- **Responses:** The server broadcasts results back to clients as either a `String` or a `List<String>`.
- **Server admin GUI:** Tracks connected clients (IP, hostname, connect/disconnect events) in a live JavaFX table (`ClientConnectionStatusController`).
- **Client UI:** JavaFX FXML screens per feature/role, backed by controllers in `mainControllers`, `librarianControllers`, and `subscriberControllers`.

## Features

**Librarian**
- Borrow / return books
- Register and update subscribers
- View all subscribers
- View notifications
- Generate reports (borrow/return statistics, subscriber status charts)

**Subscriber**
- Search for books
- Order books
- Request borrow extensions
- View order history and reminders
- Update personal details

## Tech Stack

- Java
- JavaFX (FXML + CSS)
- MySQL / JDBC (`mysql-connector-java-8.0.13`)
- Custom TCP client-server framework (OCSF)
- Eclipse project structure (no Maven/Gradle build)

## Setup

1. Import all three projects (`OCSF`, `G21_Server_Prototype_HW2`, `G21_Client_Prototype`) into Eclipse.
2. Add the JavaFX SDK (version 17) and the MySQL Connector/J jar as user libraries, matching the `.classpath` entries in each project.
3. Create a MySQL schema named `g21_project_schema` and update the connection string/credentials in [`Server/mysqlConnection.java`](G21_Server_Prototype_HW2/src/Server/mysqlConnection.java) if needed.
4. Run `Server/ServerUI.java` to start the server, enter a port, and start listening.
5. Run `client/ClientUI.java` (or the connection setup screen) to start a client and connect to the server's host/port.

## Notes

- There is no automated test suite; this was built as an academic/course project.
- Database credentials in `mysqlConnection.java` are hardcoded for local development — replace with environment-based configuration before any real deployment.
