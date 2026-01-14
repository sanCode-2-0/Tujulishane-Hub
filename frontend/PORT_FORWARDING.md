# How to Port Forward the Frontend

To run the frontend and access it from a public URL, you need to use a port forwarding tool like `ngrok`.

### Prerequisites

*   You have Python installed.
*   You have `ngrok` installed and configured.

### Steps

1.  **Serve the frontend locally.**
    Open a terminal in the `frontend` directory and run the following command. This will start a simple web server on port 8000.
    ```bash
    python -m http.server 8000
    ```

2.  **Expose the local server to the internet.**
    Open a **new** terminal and run the following command. This will create a public URL for your local server.
    ```bash
    ngrok http 8000
    ```

After running the `ngrok` command, it will display a public URL (e.g., `https://random-string.ngrok.io`). You can use this URL to access the frontend from any device with an internet connection.
