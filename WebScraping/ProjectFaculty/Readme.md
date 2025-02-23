# IIT Faculty & Project Positions Scraper

This part of project is a web scraping application built using Flask, Selenium, and MongoDB to collect faculty and project position data from various IIT websites. It provides an API to fetch live data or retrieve stored data from MongoDB.

## Features

- Scrapes faculty and project positions from IIT websites.
- Stores data in MongoDB for persistent access.
- Provides API endpoints to retrieve stored or live data.
- Uses Selenium for dynamic content scraping.

## Setup Instructions

### Prerequisites

Ensure you have the following installed:

- Python (>=3.7)
- MongoDB
- Google Chrome & Chromedriver
- See requirements.txt
- Required Python packages (install with `pip install -r requirements.txt`)

### Installation

Clone the repository:

```sh
git clone https://github.com/your-repo-url.git
cd your-project-folder
```

Install dependencies:

```sh
pip install -r requirements.txt
```
# Project File Structure & Control Flow

## 📂 Project Root

|── 📄 my_flask_app.py      &nbsp; &nbsp;           # Main API server (Flask)

│── 📄 scrapeDynamicFromIIT.py &nbsp; &nbsp;  # Main scraper controller\
│── 📄 chooseScrapper.py  &nbsp; &nbsp;      # Dispatcher to call correct scraper\
<!-- │── 📄 links.py                # Stores URLs of IIT departments\ -->
│── 📄 requirements.txt  &nbsp; &nbsp;       # Dependencies list\
│── 📄 README.md         &nbsp; &nbsp;       # Project documentation\
<!-- |── 📄 chooseScrapper.py       # chooses the corresponding scrapper\ -->
│── 📄 Dockerfile      &nbsp; &nbsp;          # Docker file for hosting\
│── 📂 ScrapeIITs   &nbsp; &nbsp;             # Directory for specific IIT scrapers\
│   ├── 📄 scrape_iit_bombay.py   &nbsp; &nbsp;    # Scraper for IIT Tirupati\
│   ├── 📄 scrape_iit_gandhinagar.py  &nbsp; &nbsp;      # Scraper for IIT Gandhinagar\
│   ├── 📄 scrape_iit_kanpur.py    &nbsp; &nbsp;    # Scraper for IIT Kanpur\
│   ├── 📄 scrape_iit_hyderabad.py &nbsp; &nbsp;       # Scraper for IIT Hyderabad\
│   ├── 📄 scrape_iit_kanpur.py   &nbsp; &nbsp;     # Scraper for IIT Kanpur\
│   ├── 📄 scrape_iit_tirupati.py &nbsp; &nbsp;       # Scraper for IIT Tirupati\
│   ├── 📄 chooseScrapper.py  &nbsp; &nbsp;      # Selects which script to run based on request\
│   ├── 📄 links.py   &nbsp; &nbsp;     # Stores the IIT Faculty links department wise\
<!-- │── 📂 data/                   # Directory to store scraped data (if needed)\
│── 📂 tests/                  # Directory for test scripts\ -->
Update MongoDB URI in `links.py` with your credentials.

Run the Flask application:

```sh
python my_flask_app.py
```

For running using Docker:

```sh
docker build -t my-flask-app .
docker run -p 5000:5000 my-flask-app
```


The API will be available at `http://127.0.0.1:5000/`

## File Structure & Control Flow

### 1. `app.py` (Main API Server)

- Initializes Flask application.
- Connects to MongoDB.
- Defines API endpoints:
  - `/getlive/<college>/<department>/` → Scrapes live data.
  - `/<college>/<department>/` → Fetches stored data from MongoDB.
  - `/daata` → Fetches all faculty data.
- Calls `scrape_website()` from `scrapeDynamicFromIIT.py`.

### 2. `scrapeDynamicFromIIT.py` (Scraper Controller)

- Contains `scrape_website()` & `scrape_website_allInfo()`.
- Determines correct IIT link from `links.py`.
- Calls `call_function_based_on_college()` from `chooseScrapper.py`.

### 3. `chooseScrapper.py` (Scraper Dispatcher)

- Contains registered scrapers for each IIT (`scrape_iittp`, `scrape_iitg`, `scrape_iitk`).
- Calls appropriate scraper based on college input.
- Uses Selenium to extract data.

### 4. `links.py` (URL Storage)

- Stores IIT department URLs in a dictionary.
- Used by `scrapeDynamicFromIIT.py` to fetch correct link.

## API Endpoints

| Method | Endpoint | Description |
|--------|----------------------------|----------------------------------|
| GET    | `/getlive/<college>/<department>/` | Scrape & return live data |
| GET    | `/<college>/<department>/` | Return stored data from MongoDB |
| GET    | `/daata` | Fetch all stored faculty data |

## Example Usage

### Fetching Live Data:
```sh
curl http://127.0.0.1:5000/getlive/iittp/faculty/
```

### Fetching Stored Data:
```sh
curl http://127.0.0.1:5000/iittp/faculty/
```

## Notes

- Ensure MongoDB is running before starting the application.
- ChromeDriver should be correctly installed for Selenium.
- Modify `links.py` to add more IITs if needed.

## License

IIT License
