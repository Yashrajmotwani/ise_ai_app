from flask import Flask, jsonify, request  # type: ignore
from pymongo import MongoClient  # type: ignore
from scrapeDynamicFromIIT import scrape_website, scrape_website_allInfo
from ScrapeIITs.links import links  # Dictionary containing URLs for different IITs

# Initialize Flask app
app = Flask(__name__)

# MongoDB connection setup
client = MongoClient(links["mongoDBLink"]["mongoDBAtlas"])  # Connect to MongoDB Atlas
db = client["IITDBLLM"]  # Database instance

# Define collections
faculty_collection = db["IITFaculty"]   # Faculty data
projects_collection = db["IITProjects"]  # Project positions data


@app.route("/")
def home():
    """Root route to verify if the API is running."""
    return "Server is up and running!"


@app.route("/daata", methods=["GET"])
def fetch_all_faculty_data():
    """
    Fetch all faculty data from the database.
    If no data exists, scrape and store it.
    """
    existing_data = list(faculty_collection.find())

    if existing_data:  # Convert ObjectId to string before returning JSON
        for doc in existing_data:
            doc["_id"] = str(doc["_id"])
        return jsonify(existing_data)

    # If no data, trigger scraping
    scraped_data = scrape_website_allInfo()
    if "error" in scraped_data:
        return jsonify(scraped_data), 400

    faculty_collection.insert_many(scraped_data)  # Store scraped data
    for doc in scraped_data:
        doc["_id"] = str(doc["_id"])  # Convert ObjectId to string
    return jsonify(scraped_data)


@app.route("/getlive/<college>/<department>/", methods=["GET"])
@app.route("/<college>/<department>/", methods=["GET"])
def fetch_data(college, department):
    """
    Fetch data for a specific college and department.
    - If 'getlive' is in the URL, scrape fresh data.
    - Otherwise, fetch stored data from the database.
    """
    print(f"Fetching data for {college} - {department}...\n")

    # Determine the appropriate collection
    collection = projects_collection if department == "project_positions" else faculty_collection

    if "getlive" in request.path:  # Live scraping requested
        fresh_data = scrape_website(college, department)
        if "error" in fresh_data:
            return jsonify(fresh_data), 400
        return jsonify(fresh_data)

    # Fetch stored data
    stored_data = list(collection.find())
    if stored_data:
        for doc in stored_data:
            doc["_id"] = str(doc["_id"])  # Convert ObjectId to string
        return jsonify(stored_data)

    # If no stored data, scrape and store it
    scraped_data = scrape_website(college, department)
    collection.insert_many(scraped_data)
    for doc in scraped_data:
        doc["_id"] = str(doc["_id"])
    return jsonify(scraped_data)


if __name__ == "__main__":
    app.run(host="0.0.0.0", port=5000, debug=True)

