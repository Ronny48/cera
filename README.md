# CERA (Campus Emergency Reporting App)

CERA is a modern JavaFX desktop application designed for university campuses in Ghana to securely report incidents, either anonymously or with identification. The app provides a safe, user-friendly platform for students and staff to report emergencies, view reports, and for admins to manage and resolve cases.

---

## Features

- **Anonymous & Identified Reporting:**
  - Submit incident reports without revealing your identity, or log in to report with your user account.
- **Rich Report Details:**
  - Attach images, documents, audio, or video files to provide detailed evidence.
  - Select incident category, location, and provide a description.
- **Admin Dashboard:**
  - Admins can view all reports in a table, mark them as resolved, and see attachments.
- **User Report View:**
  - Users can view all reports, filter by category, and see the status (e.g., Resolved, Under Investigation).
- **Modern, Clean UI:**
  - Intuitive navigation, visually distinct info boxes, and responsive layouts.

---

## Screenshots

### Login Screen

![Login Screen](src/main/resources/com/cera/assets/login.png)

### Home Screen

![Home Screen](src/main/resources/com/cera/assets/home1.png)

---

## Getting Started

### Prerequisites

- Java 17 or newer
- Maven 3.6+
- JavaFX SDK (if not using Maven dependencies)

### Setup & Run

1. **Clone the repository:**
   ```bash
   git clone <your-repo-url>
   cd cera
   ```
2. **Build the project:**
   ```bash
   mvn clean install
   ```
3. **Run the application:**
   ```bash
   mvn javafx:run
   ```

---

## Default Admin Login

- **Email:** admin@cera.com
- **Password:** admin123

---

## Project Structure

```
cera/
├── src/main/java/com/cera/           # Java source code (controllers, DAOs, App)
├── src/main/resources/com/cera/views/ # FXML UI layouts
├── src/main/resources/com/cera/assets/# Images, icons, screenshots
├── src/main/resources/com/cera/styles/# CSS styles
├── pom.xml                           # Maven build file
└── README.md                         # This file
```

---

## Key Files

- `App.java` — Main JavaFX application entry point
- `controller/` — All UI controllers (Home, Login, Report, Admin, etc.)
- `views/` — FXML files for each screen
- `assets/` — Icons, images, and screenshots
- `Database.java` — Handles SQLite DB setup and connection
- `UserDAO.java`, `ReportDAO.java` — Data access for users and reports

---

## Customization & Theming

- Modify FXML files in `views/` for layout changes
- Update styles in `styles/` for colors, fonts, and more
- Add or replace images in `assets/`

---

## License

This project is for educational and demonstration purposes. Please contact the author for other uses.
