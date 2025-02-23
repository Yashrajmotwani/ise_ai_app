from flask import Flask, jsonify, request  # type: ignore # Import necessary Flask modules
from scrapeDynamicFromIIT import scrape_website, scrape_website_allInfo  # Import scraping functions
from pymongo import MongoClient  # type: ignore # Import MongoDB client
from ScrapeIITs.links import links  # Import a dictionary containing URLs for different colleges and departments

# Initialize Flask application
app = Flask(__name__)

# Set up MongoDB connection
client = MongoClient(links["mongoDBLink"]["mongoDBAtlas"])  # MongoDB URI

db = client["IITDBLLM"]  # Connect to database IITDB
collectionFT = db["IITFaculty"]   # Collection for faculty data
collectionPRJ = db["IITProjects"]  # Collection for project data

@app.route("/")
def index():
    """Default route to check if the server is running."""
    return "Hello, World!"

@app.route("/daata", methods=["GET"])
def get_all_data():
    """Fetch all faculty data from the database. If no data exists, scrape and store it."""
    existing_data = collectionFT.find_one()
    
    if existing_data:  # If the collection has data, return it
        all_documents = list(collectionFT.find())
        for doc in all_documents:
            if "_id" in doc:
                doc["_id"] = str(doc["_id"])  # Convert ObjectId to string
        return jsonify(all_documents)
    
    # If no data, scrape and store it
    data = scrape_website_allInfo()
    if "error" in data:
        return jsonify(data), 400  # Return error if scraping fails
    
    collectionFT.insert_many(data)  # Store scraped data
    for doc in data:
        if "_id" in doc:
            doc["_id"] = str(doc["_id"])
    return jsonify(data)

@app.route("/getlive/<college>/<department>/", methods=["GET"])
@app.route("/<college>/<department>/", methods=["GET"])
def get_data(college, department):
    """Fetch data from database. If 'getlive' is in the URL, scrape fresh data."""
    print("Calling scraper...\n\n\n")
    
    # Determine which collection to use based on department
    collection = collectionPRJ if department == "project_positions" else collectionFT
    
    if "getlive" in request.path:  # If 'getlive' is in URL, scrape fresh data
        data = scrape_website(college, department)
        print("hello ", data)
        if "error" in data:
            return jsonify(data), 400  # Return error if scraping fails
        return jsonify(data)
    
    # Fetch existing data from the database
    # existing_data = collection.find_one()
    # if existing_data:
    #     all_documents = list(collection.find())
    #     for doc in all_documents:
    #         if "_id" in doc:
    #             doc["_id"] = str(doc["_id"])  # Convert ObjectId to string
    #     return jsonify(all_documents)
    
    # If no data, scrape and store it
    data = scrape_website(college, department)
    collection.insert_many(data)
    for doc in data:
        if "_id" in doc:
            doc["_id"] = str(doc["_id"])
    return jsonify(data)

if __name__ == "__main__":
    app.run(host="0.0.0.0", port=5000, debug=True)  # Start Flask server

# API Endpoints:
# http://127.0.0.1:5000/getlive/<college>/<department>/  -> Fetch live scraped data
# http://127.0.0.1:5000/<college>/<department>/         -> Fetch cached data from MongoDB
