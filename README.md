# Digital Form

An alternative to proprietary form services.

Host your own form server and take control of your data!

## Getting Started

Deploying your own Digital Form instance is easy with Docker Compose.

1.  **Clone the repository:** (Assuming you have the project code in a Git repository)
    ```bash
    git clone https://github.com/andmhn/digitalform
    cd digital-form
    ```
2.  **Start the services:**
    ```bash
    docker compose up
    ```

    This command will build and start the following services:
    * **Spring Boot:** The backend API server.
    * **Angular:** The frontend user interface.
    * **PostgreSQL:** The database to store form definitions and submissions.

Once the services are up and running, you can access the application in your web browser. The frontend is typically served on port `8080` (or another port you configure in your `docker-compose.yml` file).

## Features

Digital Form offers the following features:

* **Form Creation:** An intuitive interface allows users to create customized forms with various question types.
* **Form Sharing:** Easily share your forms with others by distributing the unique form link.
* **Submission Management:** Form owners can view all submissions made to their forms through a dedicated dashboard.
* **Data Export:** Export form submission data to a CSV file for further analysis.
* **Backend Authentication:** The backend API is secured with Basic Authentication to protect your data.
* **Data Validation:** Ensure data integrity with built-in validation rules for different question types.
* **Query Reordering:** Easily rearrange the order of questions in your form using a drag-and-drop interface or similar mechanism.
* **Multiple Query Types:** Choose from a variety of question types, such as text input, multiple choice, checkboxes, dropdowns, and more.
* **Form Editing:** Modify existing forms even after they have been shared, allowing for flexibility and updates.

## Technologies Used

* **Backend:** Spring Boot (Java)
* **Frontend:** Angular (TypeScript)
* **Database:** PostgreSQL
* **Containerization:** Docker, Docker Compose

## Future Milestones
* **Allow uploading Media**
* **Graph for tracking submissions**
